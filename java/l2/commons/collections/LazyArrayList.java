package l2.commons.collections;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;

public class LazyArrayList<E> implements Serializable, Cloneable, List<E>, RandomAccess {
   private static final long serialVersionUID = 8683452581122892189L;
   private static final int POOL_SIZE = Integer.parseInt(System.getProperty("lazyarraylist.poolsize", "-1"));
   private static final ObjectPool pP = new GenericObjectPool(new LazyArrayList.PoolableLazyArrayListFactory(), POOL_SIZE, (byte)2, 0L, -1);
   private static final int L = 8;
   private static final int H = 1024;
   protected transient Object[] elementData;
   protected transient int size = 0;
   protected transient int capacity = 8;

   public static <E> LazyArrayList<E> newInstance() {
      try {
         return (LazyArrayList<E>)pP.borrowObject();
      } catch (Exception var1) {
         var1.printStackTrace();
         return new LazyArrayList<>();
      }
   }

   public static <E> void recycle(LazyArrayList<E> var0) {
      try {
         pP.returnObject(var0);
      } catch (Exception var2) {
         var2.printStackTrace();
      }
   }

   public LazyArrayList(int var1) {
      if (var1 < 1024) {
         while (this.capacity < var1) {
            this.capacity <<= 1;
         }
      } else {
         this.capacity = var1;
      }
   }

   public LazyArrayList() {
      this(8);
   }

   @Override
   public boolean add(E var1) {
      this.ensureCapacity(this.size + 1);
      this.elementData[this.size++] = var1;
      return true;
   }

   @Override
   public E set(int var1, E var2) {
      Object var3 = null;
      if (var1 >= 0 && var1 < this.size) {
         var3 = this.elementData[var1];
         this.elementData[var1] = var2;
      }

      return (E)var3;
   }

   @Override
   public void add(int var1, E var2) {
      if (var1 >= 0 && var1 < this.size) {
         this.ensureCapacity(this.size + 1);
         System.arraycopy(this.elementData, var1, this.elementData, var1 + 1, this.size - var1);
         this.elementData[var1] = var2;
         this.size++;
      }
   }

   @Override
   public boolean addAll(int var1, Collection<? extends E> var2) {
      if (var2 == null || var2.isEmpty()) {
         return false;
      }

      if (var1 >= 0 && var1 < this.size) {
         Object[] var3 = var2.toArray();
         int var4 = var3.length;
         this.ensureCapacity(this.size + var4);
         int var5 = this.size - var1;
         if (var5 > 0) {
            System.arraycopy(this.elementData, var1, this.elementData, var1 + var4, var5);
         }

         System.arraycopy(var3, 0, this.elementData, var1, var4);
         this.size += var4;
         return true;
      } else {
         return false;
      }
   }

   protected void ensureCapacity(int var1) {
      if (var1 > this.capacity) {
         if (var1 < 1024) {
            while (this.capacity < var1) {
               this.capacity <<= 1;
            }
         } else {
            while (this.capacity < var1) {
               this.capacity = this.capacity * 3 / 2;
            }
         }

         Object[] var2 = new Object[this.capacity];
         if (this.elementData != null) {
            System.arraycopy(this.elementData, 0, var2, 0, this.size);
         }

         this.elementData = var2;
      } else if (this.elementData == null) {
         this.elementData = new Object[this.capacity];
      }
   }

   @Override
   public E remove(int var1) {
      Object var2 = null;
      if (var1 >= 0 && var1 < this.size) {
         this.size--;
         var2 = this.elementData[var1];
         this.elementData[var1] = this.elementData[this.size];
         this.elementData[this.size] = null;
         this.trim();
      }

      return (E)var2;
   }

   @Override
   public boolean remove(Object var1) {
      if (this.size == 0) {
         return false;
      }

      int var2 = -1;

      for (int var3 = 0; var3 < this.size; var3++) {
         if (this.elementData[var3] == var1) {
            var2 = var3;
            break;
         }
      }

      if (var2 == -1) {
         return false;
      }

      this.size--;
      this.elementData[var2] = this.elementData[this.size];
      this.elementData[this.size] = null;
      this.trim();
      return true;
   }

   @Override
   public boolean contains(Object var1) {
      if (this.size == 0) {
         return false;
      }

      for (int var2 = 0; var2 < this.size; var2++) {
         if (this.elementData[var2] == var1) {
            return true;
         }
      }

      return false;
   }

   @Override
   public int indexOf(Object var1) {
      if (this.size == 0) {
         return -1;
      }

      int var2 = -1;

      for (int var3 = 0; var3 < this.size; var3++) {
         if (this.elementData[var3] == var1) {
            var2 = var3;
            break;
         }
      }

      return var2;
   }

   @Override
   public int lastIndexOf(Object var1) {
      if (this.size == 0) {
         return -1;
      }

      int var2 = -1;

      for (int var3 = 0; var3 < this.size; var3++) {
         if (this.elementData[var3] == var1) {
            var2 = var3;
         }
      }

      return var2;
   }

   protected void trim() {
   }

   @Override
   public E get(int var1) {
      return (E)(this.size > 0 && var1 >= 0 && var1 < this.size ? this.elementData[var1] : null);
   }

   @Override
   public Object clone() {
      LazyArrayList var1 = new LazyArrayList();
      if (this.size > 0) {
         var1.capacity = this.capacity;
         var1.elementData = new Object[this.elementData.length];
         System.arraycopy(this.elementData, 0, var1.elementData, 0, this.size);
      }

      return var1;
   }

   @Override
   public void clear() {
      if (this.size != 0) {
         for (int var1 = 0; var1 < this.size; var1++) {
            this.elementData[var1] = null;
         }

         this.size = 0;
         this.trim();
      }
   }

   @Override
   public int size() {
      return this.size;
   }

   @Override
   public boolean isEmpty() {
      return this.size == 0;
   }

   public int capacity() {
      return this.capacity;
   }

   @Override
   public boolean addAll(Collection<? extends E> var1) {
      if (var1 != null && !var1.isEmpty()) {
         Object[] var2 = var1.toArray();
         int var3 = var2.length;
         this.ensureCapacity(this.size + var3);
         System.arraycopy(var2, 0, this.elementData, this.size, var3);
         this.size += var3;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean containsAll(Collection<?> var1) {
      if (var1 == null) {
         return false;
      }

      if (var1.isEmpty()) {
         return true;
      }

      Iterator var2 = var1.iterator();

      while (var2.hasNext()) {
         if (!this.contains(var2.next())) {
            return false;
         }
      }

      return true;
   }

   @Override
   public boolean retainAll(Collection<?> var1) {
      if (var1 == null) {
         return false;
      }

      boolean var2 = false;
      Iterator var3 = this.iterator();

      while (var3.hasNext()) {
         if (!var1.contains(var3.next())) {
            var3.remove();
            var2 = true;
         }
      }

      return var2;
   }

   @Override
   public boolean removeAll(Collection<?> var1) {
      if (var1 != null && !var1.isEmpty()) {
         boolean var2 = false;
         Iterator var3 = this.iterator();

         while (var3.hasNext()) {
            if (var1.contains(var3.next())) {
               var3.remove();
               var2 = true;
            }
         }

         return var2;
      } else {
         return false;
      }
   }

   @Override
   public Object[] toArray() {
      Object[] var1 = new Object[this.size];
      if (this.size > 0) {
         System.arraycopy(this.elementData, 0, var1, 0, this.size);
      }

      return var1;
   }

   @Override
   public <T> T[] toArray(T[] var1) {
      Object[] var2 = var1.length >= this.size ? var1 : (Object[])Array.newInstance(var1.getClass().getComponentType(), this.size);
      if (this.size > 0) {
         System.arraycopy(this.elementData, 0, var2, 0, this.size);
      }

      if (var2.length > this.size) {
         var2[this.size] = null;
      }

      return (T[])var2;
   }

   @Override
   public Iterator<E> iterator() {
      return new LazyArrayList.LazyItr();
   }

   @Override
   public ListIterator<E> listIterator() {
      return new LazyArrayList.LazyListItr(0);
   }

   @Override
   public ListIterator<E> listIterator(int var1) {
      return new LazyArrayList.LazyListItr(var1);
   }

   @Override
   public String toString() {
      if (this.size == 0) {
         return "[]";
      }

      StringBuilder var1 = new StringBuilder();
      var1.append('[');

      for (int var2 = 0; var2 < this.size; var2++) {
         Object var3 = this.elementData[var2];
         var1.append(var3 == this ? "this" : var3);
         if (var2 == this.size - 1) {
            var1.append(']').toString();
         } else {
            var1.append(", ");
         }
      }

      return var1.toString();
   }

   @Override
   public List<E> subList(int var1, int var2) {
      throw new UnsupportedOperationException();
   }

   private class LazyItr implements Iterator<E> {
      int cursor = 0;
      int lastRet = -1;

      private LazyItr() {
      }

      @Override
      public boolean hasNext() {
         return this.cursor < LazyArrayList.this.size();
      }

      @Override
      public E next() {
         Object var1 = LazyArrayList.this.get(this.cursor);
         this.lastRet = this.cursor++;
         return (E)var1;
      }

      @Override
      public void remove() {
         if (this.lastRet == -1) {
            throw new IllegalStateException();
         }

         LazyArrayList.this.remove(this.lastRet);
         if (this.lastRet < this.cursor) {
            this.cursor--;
         }

         this.lastRet = -1;
      }
   }

   private class LazyListItr extends LazyArrayList<E>.LazyItr implements ListIterator<E> {
      LazyListItr(int var2) {
         this.cursor = var2;
      }

      @Override
      public boolean hasPrevious() {
         return this.cursor > 0;
      }

      @Override
      public E previous() {
         int var1 = this.cursor - 1;
         Object var2 = LazyArrayList.this.get(var1);
         this.lastRet = this.cursor = var1;
         return (E)var2;
      }

      @Override
      public int nextIndex() {
         return this.cursor;
      }

      @Override
      public int previousIndex() {
         return this.cursor - 1;
      }

      @Override
      public void set(E var1) {
         if (this.lastRet == -1) {
            throw new IllegalStateException();
         }

         LazyArrayList.this.set(this.lastRet, (E)var1);
      }

      @Override
      public void add(E var1) {
         LazyArrayList.this.add(this.cursor++, (E)var1);
         this.lastRet = -1;
      }
   }

   private static class PoolableLazyArrayListFactory implements PoolableObjectFactory {
      private PoolableLazyArrayListFactory() {
      }

      public Object makeObject() throws Exception {
         return new LazyArrayList();
      }

      public void destroyObject(Object var1) throws Exception {
         ((LazyArrayList)var1).clear();
      }

      public boolean validateObject(Object var1) {
         return true;
      }

      public void activateObject(Object var1) throws Exception {
      }

      public void passivateObject(Object var1) throws Exception {
         ((LazyArrayList)var1).clear();
      }
   }
}
