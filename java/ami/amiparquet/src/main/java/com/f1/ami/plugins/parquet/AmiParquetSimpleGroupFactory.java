package com.f1.ami.plugins.parquet;

import org.apache.parquet.example.data.Group;
import org.apache.parquet.example.data.GroupFactory;
import org.apache.parquet.schema.MessageType;

public class AmiParquetSimpleGroupFactory extends GroupFactory {

  private final MessageType schema;

  public AmiParquetSimpleGroupFactory(MessageType schema) {
    this.schema = schema;
  }

  @Override
  public Group newGroup() {
    return new AmiParquetSimpleGroup(schema);
  }

}
