package com.vortex.agent.filetail;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.f1.container.ContainerScope;
import com.f1.utils.FastByteArrayOutputStream;
import com.f1.vortexcommon.msg.agent.VortexAgentTailFileEvent;
import com.f1.vortexcommon.msg.agent.VortexAgentTailFileEvents;

public class FileTailManagerLister implements FileTailManagerListener {

	private ContainerScope cs;

	final private Map<String, FileId> fileIds = new HashMap<String, FileId>();
	final private List<VortexAgentTailFileEvent> pending = new ArrayList<VortexAgentTailFileEvent>();

	public FileTailManagerLister(ContainerScope cs) {
		this.cs = cs;
	}

	@Override
	public void onOpened(FileTail file, File existing) {
		FileId fileId = fileIds.get(file.getFilePath());
		if (fileId == null) {
			fileIds.put(file.getFilePath(), fileId = new FileId(fileIds.size()));
		} else {
			final VortexAgentTailFileEvent remaining = fileId.reset();
			if (remaining != null)
				pending.add(remaining);
		}

		final VortexAgentTailFileEvent e = cs.nw(VortexAgentTailFileEvent.class);
		e.setType(VortexAgentTailFileEvent.TYPE_OPENED);
		e.setFileId(fileId.id);
		e.setFileName(file.getFilePath());
		if (existing != null)
			e.setFromFileName(existing.getPath());
		pending.add(e);
	}

	@Override
	public void onRemoved(FileTail file) {
		final FileId fileId = fileIds.get(file.getFilePath());

		final VortexAgentTailFileEvent remaining = fileId.reset();
		if (remaining != null)
			pending.add(remaining);

		final VortexAgentTailFileEvent e = cs.nw(VortexAgentTailFileEvent.class);
		e.setType(VortexAgentTailFileEvent.TYPE_REMOVED);
		e.setFileId(fileId.id);
		e.setFileName(file.getFilePath());
		pending.add(e);
	}

	@Override
	public void onReset(FileTail file, File existing) {
		final FileId fileId = fileIds.get(file.getFilePath());

		final VortexAgentTailFileEvent remaining = fileId.reset();
		if (remaining != null)
			pending.add(remaining);

		final VortexAgentTailFileEvent e = cs.nw(VortexAgentTailFileEvent.class);
		e.setType(VortexAgentTailFileEvent.TYPE_RESET);
		e.setFileId(fileId.id);
		if (existing != null)
			e.setFromFileName(existing.getPath());
		pending.add(e);
	}

	@Override
	public void onData(FileTail file, long filePosition, byte[] data, int start, int length, boolean posDup) {

		final FileId fileId = fileIds.get(file.getFilePath());
		fileId.onData(data, filePosition, posDup, start, start + length, pending);

	}

	public VortexAgentTailFileEvents popEvents() {
		if (pending.isEmpty())
			return null;
		VortexAgentTailFileEvents r = cs.nw(VortexAgentTailFileEvents.class);
		r.setEvents(new ArrayList<VortexAgentTailFileEvent>(pending));
		pending.clear();
		return r;
	}

	private class FileId {
		final public long id;
		final public FastByteArrayOutputStream buf;
		public boolean posDup = true;
		private long filePosition;
		private boolean first;
		private boolean isPartial;
		private boolean partial;

		public FileId(long id) {
			this.id = id;
			this.buf = new FastByteArrayOutputStream();
			this.first = true;
		}

		public VortexAgentTailFileEvent reset() {
			this.first = true;
			if (buf.getCount() == 0)
				return null;
			final VortexAgentTailFileEvent e = cs.nw(VortexAgentTailFileEvent.class);
			e.setType(VortexAgentTailFileEvent.TYPE_DATA);
			e.setFileId(id);
			e.setPosDup(posDup);
			e.setData(buf.toByteArray());
			buf.reset();
			e.setFilePosition(filePosition - e.getData().length);
			filePosition = 0;
			return e;
		}

		public void onData(byte[] data, long position, boolean posDup, int start, int end, List<VortexAgentTailFileEvent> sink) {
			if (first) {
				isPartial = position > 0;
				first = false;
			}
			if (posDup)
				this.posDup = posDup;
			for (int i = start; i < end; i++) {
				byte d = data[i];
				if (d == '\n') {
					final VortexAgentTailFileEvent e = cs.nw(VortexAgentTailFileEvent.class);
					e.setType(VortexAgentTailFileEvent.TYPE_DATA);
					e.setFileId(id);
					if (posDup)
						e.setPosDup(true);
					if (partial) {
						e.setPartial(true);
						partial = false;
					}
					e.setData(buf.toByteArray());
					buf.reset();
					e.setFilePosition(filePosition - e.getData().length);
					sink.add(e);
					if (!posDup)
						this.posDup = false;
				} else {
					buf.write(d);
				}
				filePosition++;
			}
		}

	}

}
