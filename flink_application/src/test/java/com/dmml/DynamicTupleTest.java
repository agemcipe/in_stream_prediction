package com.dmml;

import java.util.ArrayList;
import java.util.List;

import org.apache.flink.api.java.tuple.Tuple;
import org.junit.Test;

// TODO: DELETE, purpose was only to see if we can create tuple of variable Length

public class DynamicTupleTest {

   public class varTuple extends Tuple {

      private final int arity;
      private List<Object> tupleValues = new ArrayList<Object>();

      public varTuple(Object... values) {
         super();
         int i = 0;
         for (Object val : values) {
            this.tupleValues.add(val);
            i++;
         }
         this.arity = i;

      }

      @Override
      public varTuple copy() {
         return new varTuple(this.tupleValues.toArray());
      }

      @Override
      public int getArity() {
         return this.arity;
      }

      @Override
      public Object getField(int arg0) {
         return this.tupleValues.get(arg0);
      }

      @Override
      public <T> void setField(T arg0, int arg1) {
         this.tupleValues.set(arg1, arg0);

      }

   }

   @Test
   public void testGetClass() {

      Class<? extends Tuple> c = Tuple.getTupleClass(4);
      Object[] values = { 1, 2, 1.0, "test" };
      varTuple t = new varTuple(values);

      int a = 4;
   }
}
