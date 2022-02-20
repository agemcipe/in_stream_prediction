package com.dmml;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import com.dmml.tuples.VariableStreamElement;

import org.junit.Test;

public class VariableStreamElementTest {

   @Test
   public void testHasNoAnomaly() {

      String[] data = { "0", "0.1", "0.2", "0.3" };
      VariableStreamElement t = new VariableStreamElement(data);
      assertEquals(t.eventTime, Long.valueOf(0));

      float[] expected = { (float) 0.1, (float) 0.2, (float) 0.3 };
      assertArrayEquals(t.values, expected, (float) 0.0001);
   }

   @Test
   public void testHasAnomaly() {

      String[] data = { "0", "0.1", "0.2", "0.3", "0" };
      VariableStreamElement t = new VariableStreamElement(data, true);

      assertEquals(t.eventTime, Long.valueOf(0));

      float[] expected = { (float) 0.1, (float) 0.2, (float) 0.3 };
      assertArrayEquals(t.values, expected, (float) 0.0001);
   }
}
