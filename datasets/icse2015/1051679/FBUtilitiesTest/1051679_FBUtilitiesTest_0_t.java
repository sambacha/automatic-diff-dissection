 /**
  * Licensed to the Apache Software Foundation (ASF) under one
  * or more contributor license agreements.  See the NOTICE file
  * distributed with this work for additional information
  * regarding copyright ownership.  The ASF licenses this file
  * to you under the Apache License, Version 2.0 (the
  * "License"); you may not use this file except in compliance
  * with the License.  You may obtain a copy of the License at
  *
  *     http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */
 
 package org.apache.cassandra.utils;
 
 import static org.junit.Assert.assertArrayEquals;
 import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
 
 import java.io.IOException;
 import java.nio.ByteBuffer;
 import java.nio.charset.CharacterCodingException;
 import java.util.Arrays;
 
 import org.junit.Test;
 
 public class FBUtilitiesTest 
 {
 	@Test
     public void testHexBytesConversion()
     {
         for (int i = Byte.MIN_VALUE; i <= Byte.MAX_VALUE; i++)
         {
             byte[] b = new byte[]{ (byte)i };
             String s = FBUtilities.bytesToHex(b);
             byte[] c = FBUtilities.hexToBytes(s);
             assertArrayEquals(b, c);
         }
     }
     
     @Test
     public void testHexToBytesStringConversion()
     {
         String[] values = new String[]
         {
             "0",
             "10",
             "100",
             "101",
             "f",
             "ff"
         };
         byte[][] expected = new byte[][]
         {
             new byte[] { 0x00 },
             new byte[] { 0x10 },
             new byte[] { 0x01, 0x00 },
             new byte[] { 0x01, 0x01 },
             new byte[] { 0x0f },
             new byte[] { (byte)0x000000ff }
         };
         
         for (int i = 0; i < values.length; i++)
             assert Arrays.equals(FBUtilities.hexToBytes(values[i]), expected[i]);
     }
 
     @Test
     public void testIntBytesConversions()
     {
         // positive, negative, 1 and 2 byte cases, including a few edges that would foul things up unless you're careful
         // about masking away sign extension.
         int[] ints = new int[]
         {
             -20, -127, -128, 0, 1, 127, 128, 65534, 65535, -65534, -65535
         };
 
         for (int i : ints) {
             ByteBuffer ba = FBUtilities.toByteBuffer(i);
             int actual = FBUtilities.byteBufferToInt(ba);
             assertEquals(i, actual);
         }
     }
 
    @Test
    public void testCopyIntoBytes()
    {
        int i = 300;
        long l = 1000;
        byte[] b = new byte[20];
        FBUtilities.copyIntoBytes(b, 0, i);
        FBUtilities.copyIntoBytes(b, 4, l);
        assertEquals(i, FBUtilities.byteArrayToInt(b, 0));
        assertEquals(l, FBUtilities.byteArrayToLong(b, 4));
    }
    
    @Test
    public void testLongBytesConversions()
    {
        // positive, negative, 1 and 2 byte cases, including
        // a few edges that would foul things up unless you're careful
        // about masking away sign extension.
        long[] longs = new long[]
        {
            -20L, -127L, -128L, 0L, 1L, 127L, 128L, 65534L, 65535L, -65534L, -65535L,
            4294967294L, 4294967295L, -4294967294L, -4294967295L
        };

        for (long l : longs) {
            byte[] ba = FBUtilities.toByteArray(l);
            long actual = FBUtilities.byteArrayToLong(ba);
            assertEquals(l, actual);
        }
    }
  
    @Test
    public void testCompareByteSubArrays()
    {
        byte[] bytes = new byte[16];

        // handle null
        assert FBUtilities.compareByteSubArrays(
                null, 0, null, 0, 0) == 0;
        assert FBUtilities.compareByteSubArrays(
                null, 0, FBUtilities.toByteArray(524255231), 0, 4) == -1;
        assert FBUtilities.compareByteSubArrays(
                FBUtilities.toByteArray(524255231), 0, null, 0, 4) == 1;

        // handle comparisons
        FBUtilities.copyIntoBytes(bytes, 3, 524255231);
        assert FBUtilities.compareByteSubArrays(
                bytes, 3, FBUtilities.toByteArray(524255231), 0, 4) == 0;
        assert FBUtilities.compareByteSubArrays(
                bytes, 3, FBUtilities.toByteArray(524255232), 0, 4) == -1;
        assert FBUtilities.compareByteSubArrays(
                bytes, 3, FBUtilities.toByteArray(524255230), 0, 4) == 1;

        // check that incorrect length throws exception
        try
        {
            assert FBUtilities.compareByteSubArrays(
                    bytes, 3, FBUtilities.toByteArray(524255231), 0, 24) == 0;
            fail("Should raise an AssertionError.");
        } catch (AssertionError ae)
        {
        }
        try
        {
            assert FBUtilities.compareByteSubArrays(
                    bytes, 3, FBUtilities.toByteArray(524255231), 0, 12) == 0;
            fail("Should raise an AssertionError.");
        } catch (AssertionError ae)
        {
        }
    }

     @Test(expected=CharacterCodingException.class)
     public void testDecode() throws IOException
     {
         ByteBuffer bytes = ByteBuffer.wrap(new byte[]{(byte)0xff, (byte)0xfe});
         FBUtilities.decodeToUTF8(bytes);
     } 
 }
