 /**
  * Autogenerated by Thrift
  *
  * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
  */
 package org.apache.cassandra.service;
 /*
  * 
  * Licensed to the Apache Software Foundation (ASF) under one
  * or more contributor license agreements.  See the NOTICE file
  * distributed with this work for additional information
  * regarding copyright ownership.  The ASF licenses this file
  * to you under the Apache License, Version 2.0 (the
  * "License"); you may not use this file except in compliance
  * with the License.  You may obtain a copy of the License at
  * 
  *   http://www.apache.org/licenses/LICENSE-2.0
  * 
  * Unless required by applicable law or agreed to in writing,
  * software distributed under the License is distributed on an
  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  * KIND, either express or implied.  See the License for the
  * specific language governing permissions and limitations
  * under the License.
  * 
  */
 
 
 import java.util.List;
 import java.util.ArrayList;
 import java.util.Map;
 import java.util.HashMap;
 import java.util.Set;
 import java.util.HashSet;
 import java.util.Collections;
 import java.util.BitSet;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 import org.apache.thrift.*;
 import org.apache.thrift.meta_data.*;
 import org.apache.thrift.protocol.*;
 
 public class SliceRange implements TBase, java.io.Serializable, Cloneable, Comparable<SliceRange> {
   private static final TStruct STRUCT_DESC = new TStruct("SliceRange");
   private static final TField START_FIELD_DESC = new TField("start", TType.STRING, (short)1);
   private static final TField FINISH_FIELD_DESC = new TField("finish", TType.STRING, (short)2);
   private static final TField REVERSED_FIELD_DESC = new TField("reversed", TType.BOOL, (short)3);
   private static final TField COUNT_FIELD_DESC = new TField("count", TType.I32, (short)4);
 
   public byte[] start;
  public static final int START = 1;
   public byte[] finish;
  public static final int FINISH = 2;
   public boolean reversed;
  public static final int REVERSED = 3;
   public int count;
   public static final int COUNT = 4;
 
   // isset id assignments
   private static final int __REVERSED_ISSET_ID = 0;
   private static final int __COUNT_ISSET_ID = 1;
   private BitSet __isset_bit_vector = new BitSet(2);
 
   public static final Map<Integer, FieldMetaData> metaDataMap = Collections.unmodifiableMap(new HashMap<Integer, FieldMetaData>() {{
     put(START, new FieldMetaData("start", TFieldRequirementType.REQUIRED, 
         new FieldValueMetaData(TType.STRING)));
     put(FINISH, new FieldMetaData("finish", TFieldRequirementType.REQUIRED, 
         new FieldValueMetaData(TType.STRING)));
     put(REVERSED, new FieldMetaData("reversed", TFieldRequirementType.REQUIRED, 
         new FieldValueMetaData(TType.BOOL)));
     put(COUNT, new FieldMetaData("count", TFieldRequirementType.REQUIRED, 
         new FieldValueMetaData(TType.I32)));
   }});
 
   static {
     FieldMetaData.addStructMetaDataMap(SliceRange.class, metaDataMap);
   }
 
   public SliceRange() {
     this.reversed = false;
 
     this.count = 100;
 
   }
 
   public SliceRange(
     byte[] start,
     byte[] finish,
     boolean reversed,
     int count)
   {
     this();
     this.start = start;
     this.finish = finish;
     this.reversed = reversed;
     setReversedIsSet(true);
     this.count = count;
     setCountIsSet(true);
   }
 
   /**
    * Performs a deep copy on <i>other</i>.
    */
   public SliceRange(SliceRange other) {
     __isset_bit_vector.clear();
     __isset_bit_vector.or(other.__isset_bit_vector);
     if (other.isSetStart()) {
       this.start = new byte[other.start.length];
       System.arraycopy(other.start, 0, start, 0, other.start.length);
     }
     if (other.isSetFinish()) {
       this.finish = new byte[other.finish.length];
       System.arraycopy(other.finish, 0, finish, 0, other.finish.length);
     }
     this.reversed = other.reversed;
     this.count = other.count;
   }
 
  @Override
   public SliceRange clone() {
     return new SliceRange(this);
   }
 
   public byte[] getStart() {
     return this.start;
   }
 
   public SliceRange setStart(byte[] start) {
     this.start = start;
     return this;
   }
 
   public void unsetStart() {
     this.start = null;
   }
 
   // Returns true if field start is set (has been asigned a value) and false otherwise
   public boolean isSetStart() {
     return this.start != null;
   }
 
   public void setStartIsSet(boolean value) {
     if (!value) {
       this.start = null;
     }
   }
 
   public byte[] getFinish() {
     return this.finish;
   }
 
   public SliceRange setFinish(byte[] finish) {
     this.finish = finish;
     return this;
   }
 
   public void unsetFinish() {
     this.finish = null;
   }
 
   // Returns true if field finish is set (has been asigned a value) and false otherwise
   public boolean isSetFinish() {
     return this.finish != null;
   }
 
   public void setFinishIsSet(boolean value) {
     if (!value) {
       this.finish = null;
     }
   }
 
   public boolean isReversed() {
     return this.reversed;
   }
 
   public SliceRange setReversed(boolean reversed) {
     this.reversed = reversed;
     setReversedIsSet(true);
     return this;
   }
 
   public void unsetReversed() {
     __isset_bit_vector.clear(__REVERSED_ISSET_ID);
   }
 
   // Returns true if field reversed is set (has been asigned a value) and false otherwise
   public boolean isSetReversed() {
     return __isset_bit_vector.get(__REVERSED_ISSET_ID);
   }
 
   public void setReversedIsSet(boolean value) {
     __isset_bit_vector.set(__REVERSED_ISSET_ID, value);
   }
 
   public int getCount() {
     return this.count;
   }
 
   public SliceRange setCount(int count) {
     this.count = count;
     setCountIsSet(true);
     return this;
   }
 
   public void unsetCount() {
     __isset_bit_vector.clear(__COUNT_ISSET_ID);
   }
 
   // Returns true if field count is set (has been asigned a value) and false otherwise
   public boolean isSetCount() {
     return __isset_bit_vector.get(__COUNT_ISSET_ID);
   }
 
   public void setCountIsSet(boolean value) {
     __isset_bit_vector.set(__COUNT_ISSET_ID, value);
   }
 
   public void setFieldValue(int fieldID, Object value) {
     switch (fieldID) {
     case START:
       if (value == null) {
         unsetStart();
       } else {
         setStart((byte[])value);
       }
       break;
 
     case FINISH:
       if (value == null) {
         unsetFinish();
       } else {
         setFinish((byte[])value);
       }
       break;
 
     case REVERSED:
       if (value == null) {
         unsetReversed();
       } else {
         setReversed((Boolean)value);
       }
       break;
 
     case COUNT:
       if (value == null) {
         unsetCount();
       } else {
         setCount((Integer)value);
       }
       break;
 
     default:
       throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
     }
   }
 
   public Object getFieldValue(int fieldID) {
     switch (fieldID) {
     case START:
       return getStart();
 
     case FINISH:
       return getFinish();
 
     case REVERSED:
       return new Boolean(isReversed());
 
     case COUNT:
       return new Integer(getCount());
 
     default:
       throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
     }
   }
 
   // Returns true if field corresponding to fieldID is set (has been asigned a value) and false otherwise
   public boolean isSet(int fieldID) {
     switch (fieldID) {
     case START:
       return isSetStart();
     case FINISH:
       return isSetFinish();
     case REVERSED:
       return isSetReversed();
     case COUNT:
       return isSetCount();
     default:
       throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
     }
   }
 
   @Override
   public boolean equals(Object that) {
     if (that == null)
       return false;
     if (that instanceof SliceRange)
       return this.equals((SliceRange)that);
     return false;
   }
 
   public boolean equals(SliceRange that) {
     if (that == null)
       return false;
 
     boolean this_present_start = true && this.isSetStart();
     boolean that_present_start = true && that.isSetStart();
     if (this_present_start || that_present_start) {
       if (!(this_present_start && that_present_start))
         return false;
       if (!java.util.Arrays.equals(this.start, that.start))
         return false;
     }
 
     boolean this_present_finish = true && this.isSetFinish();
     boolean that_present_finish = true && that.isSetFinish();
     if (this_present_finish || that_present_finish) {
       if (!(this_present_finish && that_present_finish))
         return false;
       if (!java.util.Arrays.equals(this.finish, that.finish))
         return false;
     }
 
     boolean this_present_reversed = true;
     boolean that_present_reversed = true;
     if (this_present_reversed || that_present_reversed) {
       if (!(this_present_reversed && that_present_reversed))
         return false;
       if (this.reversed != that.reversed)
         return false;
     }
 
     boolean this_present_count = true;
     boolean that_present_count = true;
     if (this_present_count || that_present_count) {
       if (!(this_present_count && that_present_count))
         return false;
       if (this.count != that.count)
         return false;
     }
 
     return true;
   }
 
   @Override
   public int hashCode() {
     return 0;
   }
 
   public int compareTo(SliceRange other) {
     if (!getClass().equals(other.getClass())) {
       return getClass().getName().compareTo(other.getClass().getName());
     }
 
     int lastComparison = 0;
     SliceRange typedOther = (SliceRange)other;
 
     lastComparison = Boolean.valueOf(isSetStart()).compareTo(isSetStart());
     if (lastComparison != 0) {
       return lastComparison;
     }
     lastComparison = TBaseHelper.compareTo(start, typedOther.start);
     if (lastComparison != 0) {
       return lastComparison;
     }
     lastComparison = Boolean.valueOf(isSetFinish()).compareTo(isSetFinish());
     if (lastComparison != 0) {
       return lastComparison;
     }
     lastComparison = TBaseHelper.compareTo(finish, typedOther.finish);
     if (lastComparison != 0) {
       return lastComparison;
     }
     lastComparison = Boolean.valueOf(isSetReversed()).compareTo(isSetReversed());
     if (lastComparison != 0) {
       return lastComparison;
     }
     lastComparison = TBaseHelper.compareTo(reversed, typedOther.reversed);
     if (lastComparison != 0) {
       return lastComparison;
     }
     lastComparison = Boolean.valueOf(isSetCount()).compareTo(isSetCount());
     if (lastComparison != 0) {
       return lastComparison;
     }
     lastComparison = TBaseHelper.compareTo(count, typedOther.count);
     if (lastComparison != 0) {
       return lastComparison;
     }
     return 0;
   }
 
   public void read(TProtocol iprot) throws TException {
     TField field;
     iprot.readStructBegin();
     while (true)
     {
       field = iprot.readFieldBegin();
       if (field.type == TType.STOP) { 
         break;
       }
       switch (field.id)
       {
         case START:
           if (field.type == TType.STRING) {
             this.start = iprot.readBinary();
           } else { 
             TProtocolUtil.skip(iprot, field.type);
           }
           break;
         case FINISH:
           if (field.type == TType.STRING) {
             this.finish = iprot.readBinary();
           } else { 
             TProtocolUtil.skip(iprot, field.type);
           }
           break;
         case REVERSED:
           if (field.type == TType.BOOL) {
             this.reversed = iprot.readBool();
             setReversedIsSet(true);
           } else { 
             TProtocolUtil.skip(iprot, field.type);
           }
           break;
         case COUNT:
           if (field.type == TType.I32) {
             this.count = iprot.readI32();
             setCountIsSet(true);
           } else { 
             TProtocolUtil.skip(iprot, field.type);
           }
           break;
         default:
           TProtocolUtil.skip(iprot, field.type);
           break;
       }
       iprot.readFieldEnd();
     }
     iprot.readStructEnd();
 
 
     // check for required fields of primitive type, which can't be checked in the validate method
     if (!isSetReversed()) {
       throw new TProtocolException("Required field 'reversed' was not found in serialized data! Struct: " + toString());
     }
     if (!isSetCount()) {
       throw new TProtocolException("Required field 'count' was not found in serialized data! Struct: " + toString());
     }
     validate();
   }
 
   public void write(TProtocol oprot) throws TException {
     validate();
 
     oprot.writeStructBegin(STRUCT_DESC);
     if (this.start != null) {
       oprot.writeFieldBegin(START_FIELD_DESC);
       oprot.writeBinary(this.start);
       oprot.writeFieldEnd();
     }
     if (this.finish != null) {
       oprot.writeFieldBegin(FINISH_FIELD_DESC);
       oprot.writeBinary(this.finish);
       oprot.writeFieldEnd();
     }
     oprot.writeFieldBegin(REVERSED_FIELD_DESC);
     oprot.writeBool(this.reversed);
     oprot.writeFieldEnd();
     oprot.writeFieldBegin(COUNT_FIELD_DESC);
     oprot.writeI32(this.count);
     oprot.writeFieldEnd();
     oprot.writeFieldStop();
     oprot.writeStructEnd();
   }
 
   @Override
   public String toString() {
     StringBuilder sb = new StringBuilder("SliceRange(");
     boolean first = true;
 
     sb.append("start:");
     if (this.start == null) {
       sb.append("null");
     } else {
         int __start_size = Math.min(this.start.length, 128);
         for (int i = 0; i < __start_size; i++) {
           if (i != 0) sb.append(" ");
           sb.append(Integer.toHexString(this.start[i]).length() > 1 ? Integer.toHexString(this.start[i]).substring(Integer.toHexString(this.start[i]).length() - 2).toUpperCase() : "0" + Integer.toHexString(this.start[i]).toUpperCase());
         }
         if (this.start.length > 128) sb.append(" ...");
     }
     first = false;
     if (!first) sb.append(", ");
     sb.append("finish:");
     if (this.finish == null) {
       sb.append("null");
     } else {
         int __finish_size = Math.min(this.finish.length, 128);
         for (int i = 0; i < __finish_size; i++) {
           if (i != 0) sb.append(" ");
           sb.append(Integer.toHexString(this.finish[i]).length() > 1 ? Integer.toHexString(this.finish[i]).substring(Integer.toHexString(this.finish[i]).length() - 2).toUpperCase() : "0" + Integer.toHexString(this.finish[i]).toUpperCase());
         }
         if (this.finish.length > 128) sb.append(" ...");
     }
     first = false;
     if (!first) sb.append(", ");
     sb.append("reversed:");
     sb.append(this.reversed);
     first = false;
     if (!first) sb.append(", ");
     sb.append("count:");
     sb.append(this.count);
     first = false;
     sb.append(")");
     return sb.toString();
   }
 
   public void validate() throws TException {
     // check for required fields
     if (start == null) {
       throw new TProtocolException("Required field 'start' was not present! Struct: " + toString());
     }
     if (finish == null) {
       throw new TProtocolException("Required field 'finish' was not present! Struct: " + toString());
     }
     // alas, we cannot check 'reversed' because it's a primitive and you chose the non-beans generator.
     // alas, we cannot check 'count' because it's a primitive and you chose the non-beans generator.
     // check that fields of type enum have valid values
   }
 
 }
 
