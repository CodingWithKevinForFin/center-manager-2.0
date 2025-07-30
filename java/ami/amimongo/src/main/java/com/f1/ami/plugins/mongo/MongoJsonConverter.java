package com.f1.ami.plugins.mongo;

import org.bson.types.ObjectId;

import com.f1.utils.CharReader;
import com.f1.utils.converter.json2.FromJsonConverterSession;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.impl.StringCharReader;

public class MongoJsonConverter extends ObjectToJsonConverter {

	@Override
	protected Object onUnknownType(FromJsonConverterSession session) {
		CharReader stream = session.getStream();
		if (stream.peakSequence("ObjectId")) {
			stream.expectSequence("ObjectId");
			stream.skip(StringCharReader.WHITE_SPACE);
			stream.expectSequence("(");
			stream.skip(StringCharReader.WHITE_SPACE);
			StringBuilder buf = new StringBuilder();
			stream.expectSequence("\"");
			stream.readUntil('\"', buf);
			stream.expectSequence("\"");
			stream.skip(StringCharReader.WHITE_SPACE);
			stream.expectSequence(")");
			return new ObjectId(buf.toString());
		}
		return super.onUnknownType(session);
	}

}
