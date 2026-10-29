package l2.gameserver.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameObjectArray<E extends GameObject> implements Iterable<E> {
   private static final Logger _log = LoggerFactory.getLogger(GameObjectArray.class);
   public final String name;
   public final int resizeStep;
   public final int initCapacity;
   private final List<Integer> Fv;
   private E[] Fw;
   private int size = 0;
   private int Fx = 0;

   public GameObjectArray(String var1, int var2, int var3) {
      this.name = var1;
      this.resizeStep = var3;
      this.initCapacity = var2;
      if (var2 < 0) {
         throw new IllegalArgumentException("Illegal Capacity (" + this.name + "): " + var2);
      }

      if (this.resizeStep < 1) {
         throw new IllegalArgumentException("Illegal resize step (" + this.name + "): " + this.resizeStep);
      }

      this.Fv = new ArrayList<>(this.resizeStep);
      this.Fw = (E[])(new GameObject[var2]);
   }

   public int size() {
      return this.size;
   }

   public int getRealSize() {
      return this.Fx;
   }

   public int capacity() {
      return this.Fw.length;
   }

   public synchronized int add(E var1) {
      Integer var2 = null;
      if (this.Fv.size() > 0) {
         var2 = this.Fv.remove(this.Fv.size() - 1);
      }

      if (var2 != null) {
         this.Fx++;
         this.Fw[var2] = (E)var1;
         return var2;
      }

      if (this.Fw.length <= this.size) {
         int var3 = this.Fw.length + this.resizeStep;
         _log.warn("Object array [" + this.name + "] resized: " + this.Fw.length + " -> " + var3);
         this.Fw = Arrays.copyOf(this.Fw, var3);
      }

      this.Fw[this.size++] = (E)var1;
      this.Fx++;
      return this.size - 1;
   }

   public synchronized E remove(int var1, int var2) {
      if (var1 >= this.size) {
         return null;
      }

      GameObject var3 = this.Fw[var1];
      if (var3 != null && var3.getObjectId() == var2) {
         this.Fw[var1] = null;
         this.Fx--;
         if (var1 == this.size - 1) {
            this.size--;
         } else {
            this.Fv.add(var1);
         }

         return (E)var3;
      } else {
         return null;
      }
   }

   public E get(int var1) {
      return var1 >= this.size ? null : this.Fw[var1];
   }

   public E findByObjectId(int var1) {
      if (var1 <= 0) {
         return null;
      }

      for (int var3 = 0; var3 < this.size; var3++) {
         GameObject var2 = this.Fw[var3];
         if (var2 != null && var2.getObjectId() == var1) {
            return (E)var2;
         }
      }

      return null;
   }

   public E findByName(String var1) {
      if (var1 == null) {
         return null;
      }

      for (int var3 = 0; var3 < this.size; var3++) {
         GameObject var2 = this.Fw[var3];
         if (var2 != null && var1.equalsIgnoreCase(var2.getName())) {
            return (E)var2;
         }
      }

      return null;
   }

   public List<E> findAllByName(String var1) {
      if (var1 == null) {
         return null;
      }

      ArrayList var2 = new ArrayList();

      for (int var4 = 0; var4 < this.size; var4++) {
         GameObject var3 = this.Fw[var4];
         if (var3 != null && var1.equalsIgnoreCase(var3.getName())) {
            var2.add(var3);
         }
      }

      return var2;
   }

   public List<E> getAll() {
      return this.getAll(new ArrayList<>(this.size));
   }

   public List<E> getAll(List<E> var1) {
      for (int var3 = 0; var3 < this.size; var3++) {
         GameObject var2 = this.Fw[var3];
         if (var2 != null) {
            var1.add((E)var2);
         }
      }

      return var1;
   }

   private int a(E var1) {
      if (var1 == null) {
         return -1;
      }

      for (int var2 = 0; var2 < this.size; var2++) {
         if (var1.equals(this.Fw[var2])) {
            return var2;
         }
      }

      return -1;
   }

   public boolean contains(E var1) {
      return this.a((E)var1) > -1;
   }

   public synchronized void clear() {
      this.Fw = (E[])(new GameObject[0]);
      this.size = 0;
      this.Fx = 0;
   }

   @Override
   public Iterator<E> iterator() {
      return new GameObjectArray.Itr();
   }

   class Itr implements Iterator<E> {
      private int cursor = 0;
      private GameObject Fy;

      @Override
      public boolean hasNext() {
         while (this.cursor < GameObjectArray.this.size) {
            if ((this.Fy = GameObjectArray.this.Fw[this.cursor++]) != null) {
               return true;
            }
         }

         return false;
      }

      public E next() {
         GameObject var1 = this.Fy;
         this.Fy = null;
         if (var1 == null) {
            throw new NoSuchElementException();
         } else {
            return (E)var1;
         }
      }

      @Override
      public void remove() {
         throw new UnsupportedOperationException();
      }
   }
}
