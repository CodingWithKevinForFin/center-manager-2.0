package com.vortex.web.portlet.trees;

import com.f1.povo.f1app.inspect.F1AppInspectionArray;
import com.f1.povo.f1app.inspect.F1AppInspectionEntity;
import com.f1.povo.f1app.inspect.F1AppInspectionObject;
import com.f1.povo.f1app.inspect.F1AppInspectionString;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.FastTreePortlet;
import com.f1.suite.web.tree.WebTreeNode;
import com.f1.utils.ByteHelper;
import com.f1.utils.structs.IntKeyMap;

public class VortexWebInspectionTreePortlet extends FastTreePortlet {

	private IntKeyMap<F1AppInspectionEntity> entities;

	public VortexWebInspectionTreePortlet(PortletConfig portletConfig, IntKeyMap<F1AppInspectionEntity> inspectionEntities) {
		super(portletConfig);
		this.entities = inspectionEntities;
		//TODO: this is a stupid pack
		F1AppInspectionEntity top = inspectionEntities.get(1);
		WebTreeNode root = getTreeManager().getRoot();
		root.setName("Partition");
		addNode(root, top);
	}

	private WebTreeNode addNode(WebTreeNode root, F1AppInspectionEntity top) {
		if (top instanceof F1AppInspectionObject) {
			final F1AppInspectionObject obj = (F1AppInspectionObject) top;
			WebTreeNode r = createNode(getString(obj.getClassName()), root, false);
			final int[] names = obj.getFieldNames();
			final byte[] types = obj.getFieldTypes();
			final long[] values = obj.getFieldValues();
			final int size = names.length;
			for (int i = 0; i < size; i++) {
				switch (types[i]) {
					case F1AppInspectionEntity.TYPE_OBJECT: {
						WebTreeNode node = getTreeManager().createNode(getString(names[i]), r, false);
						F1AppInspectionEntity entity = entities.get((int) values[i]);
						if (entity != null)
							addNode(node, entity);
						break;
					}
					case F1AppInspectionEntity.TYPE_BOOL:
						getTreeManager().createNode(getString(names[i]) + (values[i] == 0 ? ": true" : ": false"), r, false);
						break;
					case F1AppInspectionEntity.TYPE_BYTE:
					case F1AppInspectionEntity.TYPE_SHORT:
					case F1AppInspectionEntity.TYPE_INT:
					case F1AppInspectionEntity.TYPE_LONG:
						getTreeManager().createNode(getString(names[i]) + ": " + values[i], r, false);
						break;
					case F1AppInspectionEntity.TYPE_FLOAT:
						getTreeManager().createNode(getString(names[i]) + ": " + Float.intBitsToFloat((int) values[i]), r, false);
						break;
					case F1AppInspectionEntity.TYPE_DOUBLE:
						getTreeManager().createNode(getString(names[i]) + ": " + Double.longBitsToDouble(values[i]), r, false);
						break;
				}
			}
			return r;
		} else if (top instanceof F1AppInspectionString) {
			final F1AppInspectionString obj = (F1AppInspectionString) top;
			return createNode(obj.getString(), root, false);
		} else if (top instanceof F1AppInspectionArray) {
			F1AppInspectionArray array = (F1AppInspectionArray) top;
			WebTreeNode r = createNode(getString(array.getComponentType()) + " [" + array.getLength() + "]", root, false);
			final byte[] buf = array.getValues();
			switch (array.getArrayType()) {
				case F1AppInspectionEntity.TYPE_OBJECT: {
					for (int i = 0; i < buf.length; i += 4) {
						addNode(r, entities.get(ByteHelper.readInt(buf, i)));
					}
					break;
				}
				case F1AppInspectionEntity.TYPE_BYTE: {
					StringBuilder sb = new StringBuilder();
					sb.append('[');
					for (int i = 0; i < buf.length; i += 1) {
						if (i > 0)
							sb.append(',');
						sb.append(buf[i]);
					}
					sb.append(']');
					createNode(sb.toString(), r, false);
					break;
				}
				case F1AppInspectionEntity.TYPE_SHORT: {
					StringBuilder sb = new StringBuilder();
					sb.append('[');
					for (int i = 0; i < buf.length; i += 2) {
						if (i > 0)
							sb.append(',');
						sb.append(ByteHelper.readShort(buf, i));
					}
					sb.append(']');
					createNode(sb.toString(), r, false);
					break;
				}
				case F1AppInspectionEntity.TYPE_CHAR: {
					StringBuilder sb = new StringBuilder();
					sb.append('[');
					for (int i = 0; i < buf.length; i += 2) {
						if (i > 0)
							sb.append(',');
						sb.append(ByteHelper.readChar(buf, i));
					}
					sb.append(']');
					createNode(sb.toString(), r, false);
					break;
				}
				case F1AppInspectionEntity.TYPE_INT: {
					StringBuilder sb = new StringBuilder();
					sb.append('[');
					for (int i = 0; i < buf.length; i += 4) {
						if (i > 0)
							sb.append(',');
						sb.append(ByteHelper.readInt(buf, i));
					}
					sb.append(']');
					createNode(sb.toString(), r, false);
					break;
				}
				case F1AppInspectionEntity.TYPE_FLOAT: {
					StringBuilder sb = new StringBuilder();
					sb.append('[');
					for (int i = 0; i < buf.length; i += 4) {
						if (i > 0)
							sb.append(',');
						sb.append(ByteHelper.readFloat(buf, i));
					}
					sb.append(']');
					createNode(sb.toString(), r, false);
					break;
				}
				case F1AppInspectionEntity.TYPE_LONG: {
					StringBuilder sb = new StringBuilder();
					sb.append('[');
					for (int i = 0; i < buf.length; i += 8) {
						if (i > 0)
							sb.append(',');
						sb.append(ByteHelper.readLong(buf, i));
					}
					sb.append(']');
					createNode(sb.toString(), r, false);
					break;
				}
				case F1AppInspectionEntity.TYPE_DOUBLE: {
					StringBuilder sb = new StringBuilder();
					sb.append('[');
					for (int i = 0; i < buf.length; i += 8) {
						if (i > 0)
							sb.append(',');
						sb.append(ByteHelper.readDouble(buf, i));
					}
					sb.append(']');
					createNode(sb.toString(), r, false);
					break;
				}
			}
			return r;
		} else
			return null;
	}
	private String getString(int i) {
		F1AppInspectionString str = (F1AppInspectionString) entities.get(i);
		if (str == null)
			return null;
		return str.getString();
	}
}
