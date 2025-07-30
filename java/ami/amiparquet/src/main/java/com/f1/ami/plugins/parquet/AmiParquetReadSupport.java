package com.f1.ami.plugins.parquet;

import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.hadoop.api.ReadSupport;
import org.apache.parquet.io.api.RecordMaterializer;
import org.apache.parquet.schema.MessageType;
import org.apache.parquet.schema.Types;
import org.apache.parquet.schema.Types.MessageTypeBuilder;

public class AmiParquetReadSupport extends ReadSupport<Group> {
	String requestedColumns = "";

	public AmiParquetReadSupport() {

	}
	public AmiParquetReadSupport(String requestedColumns) {
		this.requestedColumns = requestedColumns;
	}

	@Override
	public org.apache.parquet.hadoop.api.ReadSupport.ReadContext init(Configuration configuration, Map<String, String> keyValueMetaData, MessageType fileSchema) {
		String partialSchemaString = configuration.get(ReadSupport.PARQUET_READ_SCHEMA);
		if (!requestedColumns.isEmpty() && !requestedColumns.equals("*")) {
			String[] requestedString = requestedColumns.split(",");
			MessageTypeBuilder builder = Types.buildMessage();
			for (String s : requestedString) {
				builder.addField(fileSchema.getType(s));
			}
			MessageType schema = builder.named("partialSchemaString");
			partialSchemaString = schema.toString();
		}
		MessageType requestedProjection = getSchemaForRead(fileSchema, partialSchemaString);
		return new ReadContext(requestedProjection);
	}

	@Override
	public RecordMaterializer<Group> prepareForRead(Configuration configuration, Map<String, String> keyValueMetaData, MessageType fileSchema,
			org.apache.parquet.hadoop.api.ReadSupport.ReadContext readContext) {
		return new AmiParquetGroupRecordConverter(readContext.getRequestedSchema());
	}

}
