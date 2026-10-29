package l2.commons.net.nio.impl;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

public class MMOExecutableQueue<T extends MMOClient> implements Runnable, Queue<ReceivablePacket<T>> {
   private static final int NONE = 0;
   private static final int qT = 1;
   private static final int qU = 2;
   private final IMMOExecutor<T> qV;
   private final Queue<ReceivablePacket<T>> qW;
   private AtomicInteger qX = new AtomicInteger(0);

   public MMOExecutableQueue(IMMOExecutor<T> var1) {
      this.qV = var1;
      this.qW = new ArrayDeque<>();
   }

   @Override
   public void run() {
      while (this.qX.compareAndSet(1, 2)) {
         try {
            while (true) {
               ReceivablePacket var1 = this.poll();
               if (var1 == null) {
                  break;
               }

               var1.run();
            }
         } finally {
            this.qX.compareAndSet(2, 0);
         }
      }
   }

   @Override
   public int size() {
      return this.qW.size();
   }

   @Override
   public boolean isEmpty() {
      return this.qW.isEmpty();
   }

   @Override
   public boolean contains(Object var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Iterator<ReceivablePacket<T>> iterator() {
      throw new UnsupportedOperationException();
   }

   @Override
   public Object[] toArray() {
      throw new UnsupportedOperationException();
   }

   @Override
   public <E> E[] toArray(E[] var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean remove(Object var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean containsAll(Collection<?> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean addAll(Collection<? extends ReceivablePacket<T>> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean removeAll(Collection<?> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainAll(Collection<?> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void clear() {
      synchronized (this.qW) {
         this.qW.clear();
      }
   }

   public boolean add(ReceivablePacket<T> var1) {
      synchronized (this.qW) {
         if (!this.qW.add(var1)) {
            return false;
         }
      }

      if (this.qX.getAndSet(1) == 0) {
         this.qV.execute(this);
      }

      return true;
   }

   public boolean offer(ReceivablePacket<T> var1) {
      synchronized (this.qW) {
         return this.qW.offer(var1);
      }
   }

   public ReceivablePacket<T> remove() {
      synchronized (this.qW) {
         return this.qW.remove();
      }
   }

   public ReceivablePacket<T> poll() {
      synchronized (this.qW) {
         return this.qW.poll();
      }
   }

   public ReceivablePacket<T> element() {
      synchronized (this.qW) {
         return this.qW.element();
      }
   }

   public ReceivablePacket<T> peek() {
      synchronized (this.qW) {
         return this.qW.peek();
      }
   }
}
