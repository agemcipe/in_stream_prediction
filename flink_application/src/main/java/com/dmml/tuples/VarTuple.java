
package com.dmml.tuples;

import java.util.ArrayList;
import java.util.List;

import org.apache.flink.api.java.tuple.Tuple;

// TODO: DELETE

public class VarTuple extends Tuple {

   private final int arity;
   private List<Object> tupleValues = new ArrayList<Object>();

   public VarTuple(Object... values) {
      super();
      int i = 0;
      for (Object val : values) {
         this.tupleValues.add(val);
         i++;
      }
      this.arity = i;

   }

   @Override
   public VarTuple copy() {
      return new VarTuple(this.tupleValues.toArray());
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