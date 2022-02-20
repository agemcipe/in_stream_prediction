package com.dmml.tuples;

public class VariableStreamElement {

   public Long eventTime;
   public float[] values;
   public byte isAnomaly;

   public VariableStreamElement() {

   }

   public VariableStreamElement(String... values) {

      this(values, false);
   }

   public VariableStreamElement(String[] values, boolean hasAnomalyVariable) {

      this.eventTime = Long.valueOf(values[0]);

      int numValues;
      if (hasAnomalyVariable) {
         numValues = values.length - 2;

         this.isAnomaly = Byte.valueOf(values[values.length - 1]);
      } else {
         numValues = values.length - 1;
      }

      this.values = new float[numValues];

      // turn [eventTime, val1, val2, ..., isAnomaly] into [val1, val2, ...]
      for (int j = 0; j < numValues; j++) {
         this.values[j] = (float) Float.parseFloat(values[j + 1]);
      }
   }

}
