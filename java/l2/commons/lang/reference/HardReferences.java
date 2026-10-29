package l2.commons.lang.reference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class HardReferences {
   private static HardReference<?> qt = new HardReferences.EmptyReferencedHolder(null);

   private HardReferences() {
   }

   public static <T> HardReference<T> emptyRef() {
      return (HardReference<T>)qt;
   }

   public static <T> Collection<T> unwrap(Collection<HardReference<T>> var0) {
      ArrayList var1 = new ArrayList(var0.size());

      for (HardReference var3 : var0) {
         Object var4 = var3.get();
         if (var4 != null) {
            var1.add(var4);
         }
      }

      return var1;
   }

   public static <T> Iterable<T> iterate(Iterable<HardReference<T>> var0) {
      return new HardReferences.WrappedIterable<>(var0);
   }

   private static class EmptyReferencedHolder extends AbstractHardReference<Object> {
      public EmptyReferencedHolder(Object var1) {
         super(var1);
      }
   }

   private static class WrappedIterable<T> implements Iterable<T> {
      final Iterable<HardReference<T>> refs;

      WrappedIterable(Iterable<HardReference<T>> var1) {
         this.refs = var1;
      }

      @Override
      public Iterator<T> iterator() {
         return new HardReferences.WrappedIterable.WrappedIterator<>(this.refs.iterator());
      }

      private static class WrappedIterator<T> implements Iterator<T> {
         final Iterator<HardReference<T>> iterator;

         WrappedIterator(Iterator<HardReference<T>> var1) {
            this.iterator = var1;
         }

         @Override
         public boolean hasNext() {
            return this.iterator.hasNext();
         }

         @Override
         public T next() {
            return this.iterator.next().get();
         }

         @Override
         public void remove() {
            this.iterator.remove();
         }
      }
   }
}
