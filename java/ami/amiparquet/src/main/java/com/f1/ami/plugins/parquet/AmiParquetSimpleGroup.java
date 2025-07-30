package com.f1.ami.plugins.parquet;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.parquet.example.data.Group;
import org.apache.parquet.example.data.simple.BinaryValue;
import org.apache.parquet.example.data.simple.BooleanValue;
import org.apache.parquet.example.data.simple.DoubleValue;
import org.apache.parquet.example.data.simple.FloatValue;
import org.apache.parquet.example.data.simple.Int96Value;
import org.apache.parquet.example.data.simple.IntegerValue;
import org.apache.parquet.example.data.simple.LongValue;
import org.apache.parquet.example.data.simple.NanoTime;
import org.apache.parquet.example.data.simple.Primitive;
import org.apache.parquet.io.api.Binary;
import org.apache.parquet.io.api.RecordConsumer;
import org.apache.parquet.schema.GroupType;
import org.apache.parquet.schema.Type;

import com.f1.base.Table;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.structs.table.columnar.ColumnarTable;


public class AmiParquetSimpleGroup extends Group {

	private static final Logger log = LH.get();
	
  private final GroupType schema;
  private final List<Object>[] data;

  @SuppressWarnings("unchecked")
  public AmiParquetSimpleGroup(GroupType schema) {
    this.schema = schema;
    this.data = new List[schema.getFields().size()];
    for (int i = 0; i < schema.getFieldCount(); i++) {
       this.data[i] = new ArrayList<Object>();
    }
  }
  
  public List<Object>[] getData() { return data; }
  
  public List<Object> getDataFlat() {
	  List<Object> result = new ArrayList<Object>();
	  for (int i = 0; i < schema.getFieldCount(); ++i) {
		  final Type t = schema.getType(i);
		  String typename = t.asPrimitiveType().getPrimitiveTypeName().toString();
		  if (typename.equals("BINARY"))
			  typename =  t.getLogicalTypeAnnotation().toString();
		  typename = SH.toUpperCase(typename);
		  
		  try {
			  if (typename.equals("INT32"))
				  result.add(this.getInteger(i, 0));
			  else if (typename.equals("INT64"))
				  result.add(this.getLong(i, 0));
			  else if (typename.equals("INT96")) {
				  BigInteger val = BigInteger.ZERO;
				  byte[] b = this.getInt96(i, 0).getBytes();
				  for (int j = 0; j < b.length; ++j) {
					  val = val.add(BigInteger.valueOf((long)b[j]).shiftLeft(j * 8));
				  }
				  result.add(val.longValue());
			  }
			  else if (typename.equals("STRING"))
				  result.add(this.getString(i, 0));
			  else if (typename.equals("BOOLEAN"))
				  result.add(this.getBoolean(i, 0));
			  else if (typename.equals("DOUBLE"))
				  result.add(this.getDouble(i, 0));
			  else if (typename.equals("FLOAT"))
				  result.add(this.getFloat(i, 0));
			  else {
				  log.warning("Unknown typename: " + typename);
				  result.add(null);
			  }
		  } catch (Exception e) {
			  result.add(null);
		  }
	  }
	  return result;
	  }

  @Override
  public String toString() {
    return toString("");
  }

  private Class<?> getBaseType(final Type t) {
//	  PrimitiveType p = t.asPrimitiveType();
//	  System.out.println(p.getPrimitiveTypeName().toString());
	  
	  String name = t.asPrimitiveType().getPrimitiveTypeName().toString();
	  if (name.equals("BINARY"))
		  name =  t.getLogicalTypeAnnotation().toString();
	  name = SH.toUpperCase(name);
	  
//	  System.out.println("name: " + name);
	  
	  if (name.equals("INT32"))
		  return Integer.class;
	  else if (name.equals("INT64"))
		  return Long.class;
	  else if (name.equals("INT96"))
		  return Long.class;
	  else if (name.equals("STRING"))
		  return String.class;
	  else if (name.equals("DOUBLE"))
		  return Double.class;
	  else if (name.equals("FLOAT"))
		  return Float.class;
	  else if (name.equals("BOOLEAN"))
		  return Boolean.class;
	  else
		 log.warning("Unknown typename: " + name);

	  return null;
  }
  
  public Table toAmiSchema() {
	  int schema_size = schema.getFieldCount();
	  final Class<?>[] types = new Class<?>[schema_size];
	  final String[] colNames = new String[schema_size];
	  for (int i = 0; i < schema_size; ++i) {
		  types[i] = getBaseType(schema.getType(i));
		  colNames[i] = schema.getFieldName(i);
	  }
	  return new ColumnarTable(types, colNames);
  }
  
  private StringBuilder appendToString(StringBuilder builder, String indent) {
    int i = 0;
    for (Type field : schema.getFields()) {
      String name = field.getName();
      List<Object> values = data[i];
      ++i;
      if (values != null && !values.isEmpty()) {
        for (Object value : values) {
          builder.append(indent).append(name);
          if (value == null) {
            builder.append(": NULL\n");
          } else if (value instanceof Group) {
            builder.append('\n');
            ((AmiParquetSimpleGroup) value).appendToString(builder, indent + "  ");
          } else {
            builder.append(": ").append(value.toString()).append('\n');
          }
        }
      }
    }
    return builder;
  }

  public String toString(String indent) {
    StringBuilder builder = new StringBuilder();
    appendToString(builder, indent);
    return builder.toString();
  }

  @Override
  public Group addGroup(int fieldIndex) {
	  AmiParquetSimpleGroup g = new AmiParquetSimpleGroup(schema.getType(fieldIndex).asGroupType());
    add(fieldIndex, g);
    return g;
  }

  @Override
  public Group getGroup(int fieldIndex, int index) {
    return (Group)getValue(fieldIndex, index);
  }

  private Object getValue(int fieldIndex, int index) {
    List<Object> list;
    try {
      list = data[fieldIndex];
    } catch (IndexOutOfBoundsException e) {
      throw new RuntimeException("not found " + fieldIndex + "(" + schema.getFieldName(fieldIndex) + ") in group:\n" + this);
    }
    try {
      return list.get(index);
    } catch (IndexOutOfBoundsException e) {
      throw new RuntimeException("not found " + fieldIndex + "(" + schema.getFieldName(fieldIndex) + ") element number " + index + " in group:\n" + this);
    }
  }

  private void add(int fieldIndex, Primitive value) {
    Type type = schema.getType(fieldIndex);
    List<Object> list = data[fieldIndex];
    if (!type.isRepetition(Type.Repetition.REPEATED)
        && !list.isEmpty()) {
      throw new IllegalStateException("field "+fieldIndex+" (" + type.getName() + ") can not have more than one value: " + list);
    }
    list.add(value);
  }

  @Override
  public int getFieldRepetitionCount(int fieldIndex) {
    List<Object> list = data[fieldIndex];
    return list == null ? 0 : list.size();
  }

  @Override
  public String getValueToString(int fieldIndex, int index) {
    return String.valueOf(getValue(fieldIndex, index));
  }

  @Override
  public String getString(int fieldIndex, int index) {
    return ((BinaryValue)getValue(fieldIndex, index)).getString();
  }

  @Override
  public int getInteger(int fieldIndex, int index) {
    return ((IntegerValue)getValue(fieldIndex, index)).getInteger();
  }

  @Override
  public long getLong(int fieldIndex, int index) {
    return ((LongValue)getValue(fieldIndex, index)).getLong();
  }

  @Override
  public double getDouble(int fieldIndex, int index) {
    return ((DoubleValue)getValue(fieldIndex, index)).getDouble();
  }

  @Override
  public float getFloat(int fieldIndex, int index) {
    return ((FloatValue)getValue(fieldIndex, index)).getFloat();
  }

  @Override
  public boolean getBoolean(int fieldIndex, int index) {
    return ((BooleanValue)getValue(fieldIndex, index)).getBoolean();
  }

  @Override
  public Binary getBinary(int fieldIndex, int index) {
    return ((BinaryValue)getValue(fieldIndex, index)).getBinary();
  }

  public NanoTime getTimeNanos(int fieldIndex, int index) {
    return NanoTime.fromInt96((Int96Value)getValue(fieldIndex, index));
  }

  @Override
  public Binary getInt96(int fieldIndex, int index) {
    return ((Int96Value)getValue(fieldIndex, index)).getInt96();
  }

  @Override
  public void add(int fieldIndex, int value) {
    add(fieldIndex, new IntegerValue(value));
  }

  @Override
  public void add(int fieldIndex, long value) {
    add(fieldIndex, new LongValue(value));
  }

  @Override
  public void add(int fieldIndex, String value) {
    add(fieldIndex, new BinaryValue(Binary.fromString(value)));
  }

  @Override
  public void add(int fieldIndex, boolean value) {
    add(fieldIndex, new BooleanValue(value));
  }

  @Override
  public void add(int fieldIndex, Binary value) {
    switch (getType().getType(fieldIndex).asPrimitiveType().getPrimitiveTypeName()) {
      case BINARY:
      case FIXED_LEN_BYTE_ARRAY:
        add(fieldIndex, new BinaryValue(value));
        break;
      case INT96:
        add(fieldIndex, new Int96Value(value));
        break;
      default:
        throw new UnsupportedOperationException(
            getType().asPrimitiveType().getName() + " not supported for Binary");
    }
  }

  @Override
  public void add(int fieldIndex, float value) {
    add(fieldIndex, new FloatValue(value));
  }

  @Override
  public void add(int fieldIndex, double value) {
    add(fieldIndex, new DoubleValue(value));
  }

  @Override
  public void add(int fieldIndex, Group value) {
    data[fieldIndex].add(value);
  }

  @Override
  public GroupType getType() {
    return schema;
  }

  @Override
  public void writeValue(int field, int index, RecordConsumer recordConsumer) {
    ((Primitive)getValue(field, index)).writeValue(recordConsumer);
  }

	@Override
	public void add(int fieldIndex, NanoTime value) {
		// TODO Auto-generated method stub
		
	}

}