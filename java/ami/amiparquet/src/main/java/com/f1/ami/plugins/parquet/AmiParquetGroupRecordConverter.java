package com.f1.ami.plugins.parquet;

import org.apache.parquet.example.data.Group;
import org.apache.parquet.io.api.GroupConverter;
import org.apache.parquet.io.api.RecordMaterializer;
import org.apache.parquet.schema.MessageType;

public class AmiParquetGroupRecordConverter extends RecordMaterializer<Group> {

  private final AmiParquetSimpleGroupFactory simpleGroupFactory;

  private AmiParquetSimpleGroupConverter root;

  public AmiParquetGroupRecordConverter(MessageType schema) {
    this.simpleGroupFactory = new AmiParquetSimpleGroupFactory(schema);
    this.root = new AmiParquetSimpleGroupConverter(null, 0, schema) {
      @Override
      public void start() {
        this.current = simpleGroupFactory.newGroup();
      }

      @Override
      public void end() {
      }
    };
  }

  @Override
  public Group getCurrentRecord() {
    return root.getCurrentRecord();
  }

  @Override
  public GroupConverter getRootConverter() {
    return root;
  }

}