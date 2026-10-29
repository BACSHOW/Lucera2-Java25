package l2.commons.lang;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class ExecutableQueue<E extends Runnable> implements Runnable, Queue<E> {
   private final Consumer<Runnable> qn;
   private final Queue<E> qo;
   private final AtomicReference state = new AtomicReference<>(ExecutableQueue.State.NONE);

   public ExecutableQueue(Consumer<Runnable> var1) {
      this.qn = var1;
      this.qo = new ConcurrentLinkedQueue<>();
   }

   @Override
   public void run() {
      while (this.state.compareAndSet(ExecutableQueue.State.QUEUED, ExecutableQueue.State.RUNNING)) {
         try {
            while (true) {
               Runnable var1 = this.poll();
               if (var1 == null) {
                  break;
               }

               var1.run();
            }
         } finally {
            this.state.compareAndSet(ExecutableQueue.State.RUNNING, ExecutableQueue.State.NONE);
         }
      }
   }

   @Override
   public int size() {
      synchronized (this.qo) {
         return this.qo.size();
      }
   }

   @Override
   public boolean isEmpty() {
      synchronized (this.qo) {
         return this.qo.isEmpty();
      }
   }

   @Override
   public boolean contains(Object var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Iterator<E> iterator() {
      throw new UnsupportedOperationException();
   }

   @Override
   public Object[] toArray() {
      throw new UnsupportedOperationException();
   }

   @Override
   public <T> T[] toArray(T[] var1) {
      throw new UnsupportedOperationException();
   }

   public boolean add(E var1) {
      synchronized (this.qo) {
         if (!this.qo.add((E)var1)) {
            return false;
         }
      }

      if (this.state.getAndSet(ExecutableQueue.State.QUEUED) == ExecutableQueue.State.NONE) {
         this.qn.accept(this);
      }

      return true;
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
   public boolean addAll(Collection<? extends E> var1) {
      var1.forEach(this::add);
      return true;
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
      synchronized (this.qo) {
         this.qo.clear();
      }
   }

   public boolean offer(E var1) {
      return this.add((E)var1);
   }

   public E remove() {
      synchronized (this.qo) {
         return this.qo.remove();
      }
   }

   public E poll() {
      synchronized (this.qo) {
         return this.qo.poll();
      }
   }

   public E element() {
      synchronized (this.qo) {
         return this.qo.element();
      }
   }

   public E peek() {
      synchronized (this.qo) {
         return this.qo.peek();
      }
   }

   private enum State {
      NONE,
      QUEUED,
      RUNNING;
   }
}
