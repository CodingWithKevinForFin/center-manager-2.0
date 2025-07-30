package com.f1.ami.sim;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.f1.ami.client.AmiClient;

public class AmiSimSession {

	Random rand;
	long now;
	private AmiSim sim;
	private List<AmiSimObject> added = new ArrayList<AmiSimObject>();
	private List<AmiSimObject> updated = new ArrayList<AmiSimObject>();
	private List<AmiSimObject> removed = new ArrayList<AmiSimObject>();
	private AmiClient client;

	public AmiSimSession(AmiClient client, Random rand, long now, AmiSim sim) {
		this.rand = rand;
		this.now = now;
		this.sim = sim;
		this.client = client;
	}
	public Random getRand() {
		return rand;
	}
	public void setRand(Random rand) {
		this.rand = rand;
	}
	public long getNow() {
		return now;
	}
	public void setNow(long now) {
		this.now = now;
	}

	public void onMessage(String message) {

	}
	public AmiSim getSim() {
		return sim;
	}

	public void sendAdd(AmiSimObject obj, boolean isAlert) {
		this.added.add(obj);
		client.startObjectMessage(obj.getType(), obj.getId(), obj.getExpires()).addMessageParams(obj.getParams()).sendMessage();
	}
	public void sendUpdate(AmiSimObject obj, Map<String, Object> params, boolean isAlert) {
		this.updated.add(obj);
		client.startObjectMessage(obj.getType(), obj.getId(), obj.getExpires()).addMessageParams(obj.getParams()).sendMessage();
	}
	public void sendRemove(AmiSimObject obj, boolean isAlert) {
		client.startDeleteMessage(obj.getType(), obj.getId()).sendMessage();
	}

	public void resetChanges() {
		this.added.clear();
		this.updated.clear();
		this.removed.clear();
	}

	public List<AmiSimObject> getAdded() {
		return added;
	}
	public List<AmiSimObject> getUpdated() {
		return updated;
	}
	public List<AmiSimObject> getRemoved() {
		return removed;
	}
}
