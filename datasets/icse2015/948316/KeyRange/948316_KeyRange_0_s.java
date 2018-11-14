 /**
  * Autogenerated by Thrift
  *
  * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
  */
 package org.apache.cassandra.thrift;
 
 import java.util.List;
 import java.util.ArrayList;
 import java.util.Map;
 import java.util.HashMap;
 import java.util.EnumMap;
 import java.util.Set;
 import java.util.HashSet;
 import java.util.EnumSet;
 import java.util.Collections;
 import java.util.BitSet;
 import java.util.Arrays;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 import org.apache.thrift.*;
 import org.apache.thrift.meta_data.*;
 import org.apache.thrift.protocol.*;
 
 /**
  * The semantics of start keys and tokens are slightly different.
  * Keys are start-inclusive; tokens are start-exclusive.  Token
  * ranges may also wrap -- that is, the end token may be less
  * than the start one.  Thus, a range from keyX to keyX is a
  * one-element range, but a range from tokenY to tokenY is the
  * full ring.
  */
 public class KeyRange implements TBase<KeyRange._Fields>, java.io.Serializable, Cloneable, Comparable<KeyRange> {
   private static final TStruct STRUCT_DESC = new TStruct("KeyRange");
 
   private static final TField START_KEY_FIELD_DESC = new TField("start_key", TType.STRING, (short)1);
   private static final TField END_KEY_FIELD_DESC = new TField("end_key", TType.STRING, (short)2);
   private static final TField START_TOKEN_FIELD_DESC = new TField("start_token", TType.STRING, (short)3);
   private static final TField END_TOKEN_FIELD_DESC = new TField("end_token", TType.STRING, (short)4);
   private static final TField COUNT_FIELD_DESC = new TField("count", TType.I32, (short)5);
 
   public byte[] start_key;
   public byte[] end_key;
   public String start_token;
   public String end_token;
   public int count;
 
   /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
   public enum _Fields implements TFieldIdEnum {
     START_KEY((short)1, "start_key"),
     END_KEY((short)2, "end_key"),
     START_TOKEN((short)3, "start_token"),
     END_TOKEN((short)4, "end_token"),
     COUNT((short)5, "count");
 
     private static final Map<Integer, _Fields> byId = new HashMap<Integer, _Fields>();
     private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();
 
     static {
       for (_Fields field : EnumSet.allOf(_Fields.class)) {
         byId.put((int)field._thriftId, field);
         byName.put(field.getFieldName(), field);
       }
     }
 
     /**
      * Find the _Fields constant that matches fieldId, or null if its not found.
      */
     public static _Fields findByThriftId(int fieldId) {
       return byId.get(fieldId);
     }
 
     /**
      * Find the _Fields constant that matches fieldId, throwing an exception
      * if it is not found.
      */
     public static _Fields findByThriftIdOrThrow(int fieldId) {
       _Fields fields = findByThriftId(fieldId);
       if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
       return fields;
     }
 
     /**
      * Find the _Fields constant that matches name, or null if its not found.
      */
     public static _Fields findByName(String name) {
       return byName.get(name);
     }
 
     private final short _thriftId;
     private final String _fieldName;
 
     _Fields(short thriftId, String fieldName) {
       _thriftId = thriftId;
       _fieldName = fieldName;
     }
 
     public short getThriftFieldId() {
       return _thriftId;
     }
 
     public String getFieldName() {
       return _fieldName;
     }
   }
 
   // isset id assignments
   private static final int __COUNT_ISSET_ID = 0;
   private BitSet __isset_bit_vector = new BitSet(1);
 
   public static final Map<_Fields, FieldMetaData> metaDataMap = Collections.unmodifiableMap(new EnumMap<_Fields, FieldMetaData>(_Fields.class) {{
     put(_Fields.START_KEY, new FieldMetaData("start_key", TFieldRequirementType.OPTIONAL, 
         new FieldValueMetaData(TType.STRING)));
     put(_Fields.END_KEY, new FieldMetaData("end_key", TFieldRequirementType.OPTIONAL, 
         new FieldValueMetaData(TType.STRING)));
     put(_Fields.START_TOKEN, new FieldMetaData("start_token", TFieldRequirementType.OPTIONAL, 
         new FieldValueMetaData(TType.STRING)));
     put(_Fields.END_TOKEN, new FieldMetaData("end_token", TFieldRequirementType.OPTIONAL, 
         new FieldValueMetaData(TType.STRING)));
     put(_Fields.COUNT, new FieldMetaData("count", TFieldRequirementType.REQUIRED, 
         new FieldValueMetaData(TType.I32)));
   }});
 
   static {
     FieldMetaData.addStructMetaDataMap(KeyRange.class, metaDataMap);
   }
 
   public KeyRange() {
     this.count = 100;
 
   }
 
   public KeyRange(
     int count)
   {
     this();
     this.count = count;
     setCountIsSet(true);
   }
 
   /**
    * Performs a deep copy on <i>other</i>.
    */
   public KeyRange(KeyRange other) {
     __isset_bit_vector.clear();
     __isset_bit_vector.or(other.__isset_bit_vector);
     if (other.isSetStart_key()) {
       this.start_key = new byte[other.start_key.length];
       System.arraycopy(other.start_key, 0, start_key, 0, other.start_key.length);
     }
     if (other.isSetEnd_key()) {
       this.end_key = new byte[other.end_key.length];
       System.arraycopy(other.end_key, 0, end_key, 0, other.end_key.length);
     }
     if (other.isSetStart_token()) {
       this.start_token = other.start_token;
     }
     if (other.isSetEnd_token()) {
       this.end_token = other.end_token;
     }
     this.count = other.count;
   }
 
   public KeyRange deepCopy() {
     return new KeyRange(this);
   }
 
   @Deprecated
   public KeyRange clone() {
     return new KeyRange(this);
   }
 
   public byte[] getStart_key() {
     return this.start_key;
   }
 
   public KeyRange setStart_key(byte[] start_key) {
     this.start_key = start_key;
     return this;
   }
 
   public void unsetStart_key() {
     this.start_key = null;
   }
 
   /** Returns true if field start_key is set (has been asigned a value) and false otherwise */
   public boolean isSetStart_key() {
     return this.start_key != null;
   }
 
   public void setStart_keyIsSet(boolean value) {
     if (!value) {
       this.start_key = null;
     }
   }
 
   public byte[] getEnd_key() {
     return this.end_key;
   }
 
   public KeyRange setEnd_key(byte[] end_key) {
     this.end_key = end_key;
     return this;
   }
 
   public void unsetEnd_key() {
     this.end_key = null;
   }
 
   /** Returns true if field end_key is set (has been asigned a value) and false otherwise */
   public boolean isSetEnd_key() {
     return this.end_key != null;
   }
 
   public void setEnd_keyIsSet(boolean value) {
     if (!value) {
       this.end_key = null;
     }
   }
 
   public String getStart_token() {
     return this.start_token;
   }
 
   public KeyRange setStart_token(String start_token) {
     this.start_token = start_token;
     return this;
   }
 
   public void unsetStart_token() {
     this.start_token = null;
   }
 
   /** Returns true if field start_token is set (has been asigned a value) and false otherwise */
   public boolean isSetStart_token() {
     return this.start_token != null;
   }
 
   public void setStart_tokenIsSet(boolean value) {
     if (!value) {
       this.start_token = null;
     }
   }
 
   public String getEnd_token() {
     return this.end_token;
   }
 
   public KeyRange setEnd_token(String end_token) {
     this.end_token = end_token;
     return this;
   }
 
   public void unsetEnd_token() {
     this.end_token = null;
   }
 
   /** Returns true if field end_token is set (has been asigned a value) and false otherwise */
   public boolean isSetEnd_token() {
     return this.end_token != null;
   }
 
   public void setEnd_tokenIsSet(boolean value) {
     if (!value) {
       this.end_token = null;
     }
   }
 
   public int getCount() {
     return this.count;
   }
 
   public KeyRange setCount(int count) {
     this.count = count;
     setCountIsSet(true);
     return this;
   }
 
   public void unsetCount() {
     __isset_bit_vector.clear(__COUNT_ISSET_ID);
   }
 
   /** Returns true if field count is set (has been asigned a value) and false otherwise */
   public boolean isSetCount() {
     return __isset_bit_vector.get(__COUNT_ISSET_ID);
   }
 
   public void setCountIsSet(boolean value) {
     __isset_bit_vector.set(__COUNT_ISSET_ID, value);
   }
 
   public void setFieldValue(_Fields field, Object value) {
     switch (field) {
     case START_KEY:
       if (value == null) {
         unsetStart_key();
       } else {
         setStart_key((byte[])value);
       }
       break;
 
     case END_KEY:
       if (value == null) {
         unsetEnd_key();
       } else {
         setEnd_key((byte[])value);
       }
       break;
 
     case START_TOKEN:
       if (value == null) {
         unsetStart_token();
       } else {
         setStart_token((String)value);
       }
       break;
 
     case END_TOKEN:
       if (value == null) {
         unsetEnd_token();
       } else {
         setEnd_token((String)value);
       }
       break;
 
     case COUNT:
       if (value == null) {
         unsetCount();
       } else {
         setCount((Integer)value);
       }
       break;
 
     }
   }
 
   public void setFieldValue(int fieldID, Object value) {
     setFieldValue(_Fields.findByThriftIdOrThrow(fieldID), value);
   }
 
   public Object getFieldValue(_Fields field) {
     switch (field) {
     case START_KEY:
       return getStart_key();
 
     case END_KEY:
       return getEnd_key();
 
     case START_TOKEN:
       return getStart_token();
 
     case END_TOKEN:
       return getEnd_token();
 
     case COUNT:
       return new Integer(getCount());
 
     }
     throw new IllegalStateException();
   }
 
   public Object getFieldValue(int fieldId) {
     return getFieldValue(_Fields.findByThriftIdOrThrow(fieldId));
   }
 
   /** Returns true if field corresponding to fieldID is set (has been asigned a value) and false otherwise */
   public boolean isSet(_Fields field) {
     switch (field) {
     case START_KEY:
       return isSetStart_key();
     case END_KEY:
       return isSetEnd_key();
     case START_TOKEN:
       return isSetStart_token();
     case END_TOKEN:
       return isSetEnd_token();
     case COUNT:
       return isSetCount();
     }
     throw new IllegalStateException();
   }
 
   public boolean isSet(int fieldID) {
     return isSet(_Fields.findByThriftIdOrThrow(fieldID));
   }
 
   @Override
   public boolean equals(Object that) {
     if (that == null)
       return false;
     if (that instanceof KeyRange)
       return this.equals((KeyRange)that);
     return false;
   }
 
   public boolean equals(KeyRange that) {
     if (that == null)
       return false;
 
     boolean this_present_start_key = true && this.isSetStart_key();
     boolean that_present_start_key = true && that.isSetStart_key();
     if (this_present_start_key || that_present_start_key) {
       if (!(this_present_start_key && that_present_start_key))
         return false;
       if (!java.util.Arrays.equals(this.start_key, that.start_key))
         return false;
     }
 
     boolean this_present_end_key = true && this.isSetEnd_key();
     boolean that_present_end_key = true && that.isSetEnd_key();
     if (this_present_end_key || that_present_end_key) {
       if (!(this_present_end_key && that_present_end_key))
         return false;
       if (!java.util.Arrays.equals(this.end_key, that.end_key))
         return false;
     }
 
     boolean this_present_start_token = true && this.isSetStart_token();
     boolean that_present_start_token = true && that.isSetStart_token();
     if (this_present_start_token || that_present_start_token) {
       if (!(this_present_start_token && that_present_start_token))
         return false;
       if (!this.start_token.equals(that.start_token))
         return false;
     }
 
     boolean this_present_end_token = true && this.isSetEnd_token();
     boolean that_present_end_token = true && that.isSetEnd_token();
     if (this_present_end_token || that_present_end_token) {
       if (!(this_present_end_token && that_present_end_token))
         return false;
       if (!this.end_token.equals(that.end_token))
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
 
   public int compareTo(KeyRange other) {
     if (!getClass().equals(other.getClass())) {
       return getClass().getName().compareTo(other.getClass().getName());
     }
 
     int lastComparison = 0;
     KeyRange typedOther = (KeyRange)other;
 
     lastComparison = Boolean.valueOf(isSetStart_key()).compareTo(typedOther.isSetStart_key());
     if (lastComparison != 0) {
       return lastComparison;
     }
     if (isSetStart_key()) {      lastComparison = TBaseHelper.compareTo(start_key, typedOther.start_key);
       if (lastComparison != 0) {
         return lastComparison;
       }
     }
     lastComparison = Boolean.valueOf(isSetEnd_key()).compareTo(typedOther.isSetEnd_key());
     if (lastComparison != 0) {
       return lastComparison;
     }
     if (isSetEnd_key()) {      lastComparison = TBaseHelper.compareTo(end_key, typedOther.end_key);
       if (lastComparison != 0) {
         return lastComparison;
       }
     }
     lastComparison = Boolean.valueOf(isSetStart_token()).compareTo(typedOther.isSetStart_token());
     if (lastComparison != 0) {
       return lastComparison;
     }
     if (isSetStart_token()) {      lastComparison = TBaseHelper.compareTo(start_token, typedOther.start_token);
       if (lastComparison != 0) {
         return lastComparison;
       }
     }
     lastComparison = Boolean.valueOf(isSetEnd_token()).compareTo(typedOther.isSetEnd_token());
     if (lastComparison != 0) {
       return lastComparison;
     }
     if (isSetEnd_token()) {      lastComparison = TBaseHelper.compareTo(end_token, typedOther.end_token);
       if (lastComparison != 0) {
         return lastComparison;
       }
     }
     lastComparison = Boolean.valueOf(isSetCount()).compareTo(typedOther.isSetCount());
     if (lastComparison != 0) {
       return lastComparison;
     }
     if (isSetCount()) {      lastComparison = TBaseHelper.compareTo(count, typedOther.count);
       if (lastComparison != 0) {
         return lastComparison;
       }
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
       switch (field.id) {
         case 1: // START_KEY
           if (field.type == TType.STRING) {
             this.start_key = iprot.readBinary();
           } else { 
             TProtocolUtil.skip(iprot, field.type);
           }
           break;
         case 2: // END_KEY
           if (field.type == TType.STRING) {
             this.end_key = iprot.readBinary();
           } else { 
             TProtocolUtil.skip(iprot, field.type);
           }
           break;
         case 3: // START_TOKEN
           if (field.type == TType.STRING) {
             this.start_token = iprot.readString();
           } else { 
             TProtocolUtil.skip(iprot, field.type);
           }
           break;
         case 4: // END_TOKEN
           if (field.type == TType.STRING) {
             this.end_token = iprot.readString();
           } else { 
             TProtocolUtil.skip(iprot, field.type);
           }
           break;
         case 5: // COUNT
           if (field.type == TType.I32) {
             this.count = iprot.readI32();
             setCountIsSet(true);
           } else { 
             TProtocolUtil.skip(iprot, field.type);
           }
           break;
         default:
           TProtocolUtil.skip(iprot, field.type);
       }
       iprot.readFieldEnd();
     }
     iprot.readStructEnd();
 
     // check for required fields of primitive type, which can't be checked in the validate method
     if (!isSetCount()) {
       throw new TProtocolException("Required field 'count' was not found in serialized data! Struct: " + toString());
     }
     validate();
   }
 
   public void write(TProtocol oprot) throws TException {
     validate();
 
     oprot.writeStructBegin(STRUCT_DESC);
     if (this.start_key != null) {
       if (isSetStart_key()) {
         oprot.writeFieldBegin(START_KEY_FIELD_DESC);
         oprot.writeBinary(this.start_key);
         oprot.writeFieldEnd();
       }
     }
     if (this.end_key != null) {
       if (isSetEnd_key()) {
         oprot.writeFieldBegin(END_KEY_FIELD_DESC);
         oprot.writeBinary(this.end_key);
         oprot.writeFieldEnd();
       }
     }
     if (this.start_token != null) {
       if (isSetStart_token()) {
         oprot.writeFieldBegin(START_TOKEN_FIELD_DESC);
         oprot.writeString(this.start_token);
         oprot.writeFieldEnd();
       }
     }
     if (this.end_token != null) {
       if (isSetEnd_token()) {
         oprot.writeFieldBegin(END_TOKEN_FIELD_DESC);
         oprot.writeString(this.end_token);
         oprot.writeFieldEnd();
       }
     }
     oprot.writeFieldBegin(COUNT_FIELD_DESC);
     oprot.writeI32(this.count);
     oprot.writeFieldEnd();
     oprot.writeFieldStop();
     oprot.writeStructEnd();
   }
 
   @Override
   public String toString() {
     StringBuilder sb = new StringBuilder("KeyRange(");
     boolean first = true;
 
     if (isSetStart_key()) {
       sb.append("start_key:");
       if (this.start_key == null) {
         sb.append("null");
       } else {
           int __start_key_size = Math.min(this.start_key.length, 128);
           for (int i = 0; i < __start_key_size; i++) {
             if (i != 0) sb.append(" ");
             sb.append(Integer.toHexString(this.start_key[i]).length() > 1 ? Integer.toHexString(this.start_key[i]).substring(Integer.toHexString(this.start_key[i]).length() - 2).toUpperCase() : "0" + Integer.toHexString(this.start_key[i]).toUpperCase());
           }
           if (this.start_key.length > 128) sb.append(" ...");
       }
       first = false;
     }
     if (isSetEnd_key()) {
       if (!first) sb.append(", ");
       sb.append("end_key:");
       if (this.end_key == null) {
         sb.append("null");
       } else {
           int __end_key_size = Math.min(this.end_key.length, 128);
           for (int i = 0; i < __end_key_size; i++) {
             if (i != 0) sb.append(" ");
             sb.append(Integer.toHexString(this.end_key[i]).length() > 1 ? Integer.toHexString(this.end_key[i]).substring(Integer.toHexString(this.end_key[i]).length() - 2).toUpperCase() : "0" + Integer.toHexString(this.end_key[i]).toUpperCase());
           }
           if (this.end_key.length > 128) sb.append(" ...");
       }
       first = false;
     }
     if (isSetStart_token()) {
       if (!first) sb.append(", ");
       sb.append("start_token:");
       if (this.start_token == null) {
         sb.append("null");
       } else {
         sb.append(this.start_token);
       }
       first = false;
     }
     if (isSetEnd_token()) {
       if (!first) sb.append(", ");
       sb.append("end_token:");
       if (this.end_token == null) {
         sb.append("null");
       } else {
         sb.append(this.end_token);
       }
       first = false;
     }
     if (!first) sb.append(", ");
     sb.append("count:");
     sb.append(this.count);
     first = false;
     sb.append(")");
     return sb.toString();
   }
 
   public void validate() throws TException {
     // check for required fields
     // alas, we cannot check 'count' because it's a primitive and you chose the non-beans generator.
   }
 
 }
 
