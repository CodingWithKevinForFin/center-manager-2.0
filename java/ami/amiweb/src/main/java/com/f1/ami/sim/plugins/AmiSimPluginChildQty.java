package com.f1.ami.sim.plugins;

import com.f1.ami.client.AmiClient;
import com.f1.ami.sim.AmiSimObject;
import com.f1.ami.sim.AmiSimPlugin;
import com.f1.ami.sim.AmiSimSession;
import com.f1.ami.sim.AmiSimType;
import com.f1.utils.CH;
import com.f1.utils.RandomStringPattern;
import com.f1.utils.structs.SkipList;
import com.f1.utils.structs.SkipListDataEntry;
import com.f1.utils.structs.Tuple2;

public class AmiSimPluginChildQty implements AmiSimPlugin {

	private String parentType;
	private String childType;
	private String parentQuantityParam;
	private String childQuantityParam;
	private String parentIdParam;
	private String childIdParam;
	private RandomStringPattern delta;
	private double addPerSecond;

	final private SkipList<SkipListDataEntry<Tuple2<Long, AmiSimObject>>> parents = new SkipList<SkipListDataEntry<Tuple2<Long, AmiSimObject>>>(1000);
	@Override
	public void visit(AmiClient client, AmiSimSession session, long timePassed) {
		AmiSimType children = session.getSim().getType(childType);
		for (AmiSimObject a : session.getAdded())
			if (parentType.equals(a.getType())) {
				Long value = ((Number) a.getParams().get(parentQuantityParam)).longValue();
				parents.add(new SkipListDataEntry<Tuple2<Long, AmiSimObject>>(new Tuple2<Long, AmiSimObject>(value, a)));
			}
		int addCount = AmiSimType.getCount(addPerSecond * parents.size(), timePassed, session.getRand());
		for (int i = 0; i < addCount; i++) {
			if (parents.size() == 0)
				break;
			int parentPos = session.getRand().nextInt(parents.size());
			Tuple2<Long, AmiSimObject> tuple = parents.get(parentPos).getData();
			AmiSimObject child = children.createObject(session, CH.l(tuple.getB()));

			long delta = this.delta.generateLong(session.getRand());
			if (tuple.getA() <= delta) {
				delta = tuple.getA();
				parents.remove(parentPos);
			} else
				tuple.setA(tuple.getA() - delta);
			Object parentId = tuple.getB().getParams().get(parentIdParam);
			child.getParams().put(childIdParam, parentId);
			child.getParams().put(childQuantityParam, delta);
			session.sendAdd(child, children.getIsAlert());
		}
	}

	public String getParentType() {
		return parentType;
	}
	public void setParentType(String parentType) {
		this.parentType = parentType;
	}
	public String getChildType() {
		return childType;
	}
	public void setChildType(String childType) {
		this.childType = childType;
	}
	public String getParentQuantityParam() {
		return parentQuantityParam;
	}
	public void setParentQuantityParam(String parentQuantityParam) {
		this.parentQuantityParam = parentQuantityParam;
	}
	public String getChildQuantityParam() {
		return childQuantityParam;
	}
	public void setChildQuantityParam(String childQuantityParam) {
		this.childQuantityParam = childQuantityParam;
	}
	public String getParentIdParam() {
		return parentIdParam;
	}
	public void setParentIdParam(String parentIdParam) {
		this.parentIdParam = parentIdParam;
	}
	public String getChildIdParam() {
		return childIdParam;
	}
	public void setChildIdParam(String childIdParam) {
		this.childIdParam = childIdParam;
	}
	public String getDelta() {
		return delta.getPattern();
	}
	public void setDelta(String delta) {
		this.delta = new RandomStringPattern(delta);
	}
	public double getAddPerSecond() {
		return addPerSecond;
	}
	public void setAddPerSecond(double addPerSecond) {
		this.addPerSecond = addPerSecond;
	}
	public SkipList<SkipListDataEntry<Tuple2<Long, AmiSimObject>>> getParents() {
		return parents;
	}

}
