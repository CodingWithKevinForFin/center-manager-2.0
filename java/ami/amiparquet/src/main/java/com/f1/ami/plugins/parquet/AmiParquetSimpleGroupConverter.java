package com.f1.ami.plugins.parquet;

import org.apache.parquet.example.data.Group;
import org.apache.parquet.io.api.Converter;
import org.apache.parquet.io.api.GroupConverter;
import org.apache.parquet.schema.GroupType;
import org.apache.parquet.schema.Type;

class AmiParquetSimpleGroupConverter extends GroupConverter {
  private final AmiParquetSimpleGroupConverter parent;
  private final int index;
  protected Group current;
  private Converter[] converters;

  AmiParquetSimpleGroupConverter(AmiParquetSimpleGroupConverter parent, int index, GroupType schema) {
    this.parent = parent;
    this.index = index;

    converters = new Converter[schema.getFieldCount()];

    for (int i = 0; i < converters.length; i++) {
      final Type type = schema.getType(i);
      if (type.isPrimitive()) {
        converters[i] = new AmiParquetSimplePrimitiveConverter(this, i);
      } else {
        converters[i] = new AmiParquetSimpleGroupConverter(this, i, type.asGroupType());
      }

    }
  }

  @Override
  public void start() {
    current = parent.getCurrentRecord().addGroup(index);
  }

  @Override
  public Converter getConverter(int fieldIndex) {
    return converters[fieldIndex];
  }

  @Override
  public void end() {
  }

  public Group getCurrentRecord() {
    return current;
  }
}