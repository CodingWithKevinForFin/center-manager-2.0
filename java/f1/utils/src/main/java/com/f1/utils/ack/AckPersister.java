/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.ack;

import java.io.File;
import java.io.IOException;

import com.f1.utils.ByteHelper;
import com.f1.utils.FastByteArrayDataInputStream;
import com.f1.utils.IOH;
import com.f1.utils.concurrent.FastFileOutputStream;

public class AckPersister {

	final private File locations;
	final private FastFileOutputStream dataStream;
	final private FastFileOutputStream locsStream;
	final private boolean safetyCheck;
	final private int size;

	public AckPersister(File data, int size, boolean safetyCheck) throws IOException {
		try {
			IOH.ensureDir(data.getParentFile());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		this.size = size;
		this.locations = new File(data.getParent(), data.getName() + ".loc");
		if (locations.exists() && data.exists()) {
			dataStream = new FastFileOutputStream(data, this.size, false);
			locsStream = new FastFileOutputStream(locations, this.size, false);
			byte[] buf = new byte[(int) locations.length()];
			locsStream.readAt(0, buf, 0, locsStream.length());
			FastByteArrayDataInputStream dis = new FastByteArrayDataInputStream(buf);
			long tail = -1, pos;
			while ((pos = dis.readLong()) != 0) {
				tail = pos - 1;
				locsStream.skip(8);
			}
			if (tail != -1) {
				dataStream.readAt(tail, buf, 0, 4);
				int len = ByteHelper.readInt(buf, 0);
				dataStream.skip(len + tail + 4);
			}
			dis.close();
		} else {
			dataStream = new FastFileOutputStream(data, this.size, true);
			locsStream = new FastFileOutputStream(locations, this.size, false);
		}
		this.safetyCheck = safetyCheck;
	}

	public int writeMessage(byte data[]) throws IOException {
		byte[] data2 = new byte[data.length + 8];
		ByteHelper.writeInt(data.length, data2, 4);
		System.arraycopy(data, 0, data2, 8, data.length);
		return writePreparedMessageAndStoreAckId(data2);
	}

	// returns ack id
	public int writePreparedMessageAndStoreAckId(byte data[]) throws IOException {
		if (safetyCheck) {
			if (ByteHelper.readInt(data, 0) != 0)
				throw new IOException("expecting leading 4 bytes to be zero:");
			if (ByteHelper.readInt(data, 4) != data.length - 8)
				throw new IOException("expecting bytes [4,5,6,7] to indicate length: " + ByteHelper.readInt(data, 4) + "!=" + (data.length - 4));
		}
		long pos = dataStream.writeAndGetPosition(data, 4, data.length - 4);
		byte[] buf = new byte[8];
		ByteHelper.writeLong(pos + 1, buf, 0);
		long id = locsStream.writeAndGetPosition(buf);
		int ackId = 1 + (int) (id >> 3);
		ByteHelper.writeInt(ackId, data, 0);
		return ackId;
	}

	public byte[] readMessage(int ackId) {
		byte[] buf = new byte[8];
		int loc = (ackId - 1) << 3;
		if (loc + 8 > locsStream.length())
			return null;
		locsStream.readAt(loc, buf, 0, 8);
		long pos = ByteHelper.readLong(buf, 0) - 1;
		if (pos == -1)
			return null;
		dataStream.readAt(pos, buf, 0, 4);
		int length = ByteHelper.readInt(buf, 0);
		buf = new byte[length + 8];
		dataStream.readAt(pos, buf, 4, length + 4);
		ByteHelper.writeInt(ackId, buf, 0);
		return buf;
	}

	protected void close() throws IOException {
		if (dataStream != null)
			dataStream.close();
		if (locsStream != null) {
			locsStream.close();
			if (locations != null && locations.exists())
				if (!locations.delete()) {
					throw new IOException("Unable to delete file: " + locations.getName());
				}
		}
	}
}
