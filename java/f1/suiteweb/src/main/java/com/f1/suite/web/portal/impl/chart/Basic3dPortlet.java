package com.f1.suite.web.portal.impl.chart;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.suite.web.JsFunction;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletSchema;
import com.f1.suite.web.portal.impl.AbstractPortlet;
import com.f1.suite.web.portal.impl.BasicPortletSchema;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Double;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.json.JsonBuilder;

public class Basic3dPortlet extends AbstractPortlet {

	private static final String OPTION_X_ROTATE = "rotX";//0-360
	private static final String OPTION_X_CENTER = "centerX";
	private static final String OPTION_X_FOCAL = "focalX";
	//	private static final String OPTION_X_MIN_ROTATE = "xMinRot";//0-360
	//	private static final String OPTION_X_MAX_ROTATE = "xMaxRot";//0-360

	private static final String OPTION_Y_ROTATE = "rotY";//0-360
	private static final String OPTION_Y_CENTER = "centerY";
	private static final String OPTION_Y_FOCAL = "focalY";
	//	private static final String OPTION_Y_MIN_ROTATE = "yMinRot";//0-360
	//	private static final String OPTION_Y_MAX_ROTATE = "yMaxRot";//0-360

	private static final String OPTION_Z_ROTATE = "rotZ";//0-360
	//	private static final String OPTION_Z_MIN_ROTATE = "zMinRot";//0-360
	//	private static final String OPTION_Z_MAX_ROTATE = "zMaxRot";//0-360

	private static final String OPTION_ZOOM = "zoom";
	private static final String OPTION_BACKGROUND = "background";
	private static final String OPTION_BACKGROUND_CLASS = "backgroundClass";
	private static final String OPTION_FOV = "fov";

	private double rotX = 0;
	private double rotY = 0;
	private double rotZ = 0;
	private double centerX = 0;
	private double centerY = 0;
	private double focalX = 0;
	private double focalY = 0;
	private double zoom = 1;
	private double fov = 70;

	private List<Basic3dPortletListener> listeners = new ArrayList<Basic3dPortletListener>();

	public static final PortletSchema<Basic3dPortlet> SCHEMA = new BasicPortletSchema<Basic3dPortlet>("ThreeDeePortlet", "ThreeDeePortlet", Basic3dPortlet.class, false, true);

	public Basic3dPortlet(PortletConfig portletConfig) {
		super(portletConfig);
	}

	private String backgroundColor;
	private String backgroundClass;

	@Override
	public PortletSchema<? extends Basic3dPortlet> getPortletSchema() {
		return SCHEMA;
	}

	private List<Triangle> triangles = new ArrayList<Triangle>();
	private List<Line> lines = new ArrayList<Line>();
	private List<Text> texts = new ArrayList<Text>();
	private boolean dataChanged = false;
	private boolean optionsChanged = false;

	private boolean hasTranslation;
	private int positionX;
	private int positionY;
	private int positionZ;
	private double scaleX = 1d;
	private double scaleY = 1d;
	private double scaleZ = 1d;
	private double shiftX = 0d;
	private double shiftY = 0d;
	private double shiftZ = 0d;
	private Set<Integer> selected = new HashSet<Integer>();
	private int selectedColor = 16777023;

	public void addTriangle(Triangle triangle) {
		onDataChanged();
		if (hasTranslation) {
			triangle.x1 += positionX;
			triangle.x2 += positionX;
			triangle.x3 += positionX;
			triangle.y1 += positionY;
			triangle.y2 += positionY;
			triangle.y3 += positionY;
			triangle.z1 += positionZ;
			triangle.z2 += positionZ;
			triangle.z3 += positionZ;
		}
		this.triangles.add(triangle);
	}
	public void clearTriangles() {
		this.triangles.clear();
		this.selected.clear();
		onDataChanged();
	}
	public void clearSelected() {
		if (this.selected.isEmpty())
			return;
		this.selected.clear();
		onDataChanged();
	}

	public void addLine(Line line) {
		onDataChanged();
		if (hasTranslation) {
			line.x1 += this.positionX;
			line.x2 += this.positionX;
			line.y1 += this.positionY;
			line.y2 += this.positionY;
			line.z1 += this.positionZ;
			line.z2 += this.positionZ;
		}
		this.lines.add(line);
	}

	public void clearLines() {
		this.lines.clear();
		onDataChanged();
	}

	public void addText(Text text) {
		onDataChanged();
		this.texts.add(text);
	}

	public void clearTexts() {
		this.texts.clear();
		onDataChanged();
	}

	@Override
	public void drainJavascript() {
		super.drainJavascript();
		if (getVisible()) {
			if (this.dataChanged) {
				JsFunction func = callJsFunction("setData");
				JsonBuilder json = func.startJson();
				json.startMap();
				json.addKey("polys");
				json.startList();
				for (Triangle tri : getTriangles()) {
					boolean selected = this.selected.contains(tri.getId());
					// putting the coordinates in the 3d chart
					json.startList();
					json.addEntry(scaleX * (shiftX + tri.x1));
					json.addEntry(scaleY * (shiftY + tri.y1));
					json.addEntry(scaleZ * (shiftZ + tri.z1));
					json.addEntry(selected ? this.selectedColor : tri.getColor1());
					json.addEntry(scaleX * (shiftX + tri.x2));
					json.addEntry(scaleY * (shiftY + tri.y2));
					json.addEntry(scaleZ * (shiftZ + tri.z2));
					json.addEntry(selected ? this.selectedColor : tri.getColor2());
					json.addEntry(scaleX * (shiftX + tri.x3));
					json.addEntry(scaleY * (shiftY + tri.y3));
					json.addEntry(scaleZ * (shiftZ + tri.z3));
					json.addEntry(selected ? this.selectedColor : tri.getColor3());
					json.addEntry(tri.getId());
					json.endList();
				}
				json.endList();

				json.addKey("texts");
				json.startList();
				{
					for (Text text : getTexts()) {
						json.startList();
						json.addEntry(scaleX * (shiftX + text.x1));
						json.addEntry(scaleY * (shiftY + text.y1));
						json.addEntry(scaleZ * (shiftZ + text.z1));
						json.addEntryQuoted(text.text);
						json.addEntry(text.alignment);
						json.addEntry(text.color);
						json.endList();
					}
				}
				json.endList();
				json.addKey("lines");
				json.startList();
				{
					for (Line line : getLines()) {
						json.startList();
						json.addEntry(scaleX * (shiftX + line.x1));
						json.addEntry(scaleY * (shiftY + line.y1));
						json.addEntry(scaleZ * (shiftZ + line.z1));
						json.addEntry(scaleX * (shiftX + line.x2));
						json.addEntry(scaleY * (shiftY + line.y2));
						json.addEntry(scaleZ * (shiftZ + line.z2));
						json.addEntry(line.color);
						json.addEntry(line.width);
						json.endList();
					}
				}
				json.endList();

				json.endMap();
				json.close();
				func.end();
				this.dataChanged = false;
			}
			if (this.optionsChanged) {
				JsFunction func = callJsFunction("setOptions");
				JsonBuilder json = func.startJson();
				json.startMap();
				json.addKeyValueQuoted(OPTION_BACKGROUND, this.backgroundColor);
				json.addKeyValueQuoted(OPTION_BACKGROUND_CLASS, this.backgroundClass);
				json.addKeyValue(OPTION_X_ROTATE, this.rotX);
				json.addKeyValue(OPTION_Y_ROTATE, this.rotY);
				json.addKeyValue(OPTION_Z_ROTATE, this.rotZ);
				json.addKeyValue(OPTION_X_CENTER, this.centerX);
				json.addKeyValue(OPTION_Y_CENTER, this.centerY);
				json.addKeyValue(OPTION_X_FOCAL, this.focalX);
				json.addKeyValue(OPTION_Y_FOCAL, this.focalY);
				json.addKeyValue(OPTION_ZOOM, this.zoom);
				json.addKeyValue(OPTION_FOV, this.fov);
				json.endMap();
				json.close();
				func.end();
				this.optionsChanged = false;
			}
			callJsFunction("repaintIfNeeded").end();
		}
	}
	public List<Triangle> getTriangles() {
		return this.triangles;
	}
	public List<Line> getLines() {
		return this.lines;
	}
	public List<Text> getTexts() {
		return this.texts;
	}

	public static class Line {
		private double x1, y1, z1, x2, y2, z2;
		private int color;
		private int width;

		public Line(double x1, double y1, double z1, double x2, double y2, double z2, int color, int width) {
			this.x1 = x1;
			this.y1 = y1;
			this.z1 = z1;
			this.x2 = x2;
			this.y2 = y2;
			this.z2 = z2;
			this.setColor(color);
			this.setWidth(width);
		}

		public double getX1() {
			return x1;
		}

		public void setX1(double x1) {
			this.x1 = x1;
		}

		public double getY1() {
			return y1;
		}

		public void setY1(double y1) {
			this.y1 = y1;
		}

		public double getZ1() {
			return z1;
		}

		public void setZ1(double z1) {
			this.z1 = z1;
		}

		public double getX2() {
			return x2;
		}

		public void setX2(double x2) {
			this.x2 = x2;
		}

		public double getY2() {
			return y2;
		}

		public void setY2(double y2) {
			this.y2 = y2;
		}

		public double getZ2() {
			return z2;
		}

		public void setZ2(double z2) {
			this.z2 = z2;
		}

		public int getColor() {
			return color;
		}

		public void setColor(int color) {
			this.color = color;
		}

		public int getWidth() {
			return width;
		}

		public void setWidth(int width) {
			this.width = width;
		}

	}

	public void setPosition(int x, int y, int z) {
		this.positionX = x;
		this.positionY = y;
		this.positionZ = z;
		this.hasTranslation = this.positionX != 0 || this.positionY != 0 || this.positionZ != 0;
	}

	public static class Triangle {
		private int color1;
		private int color2;
		private int color3;
		private double x1, y1, z1, x2, y2, z2, x3, y3, z3;
		private int id;

		public Triangle(double x1, double y1, double z1, int color1, double x2, double y2, double z2, int color2, double x3, double y3, double z3, int color3) {
			this.x1 = x1;
			this.y1 = y1;
			this.z1 = z1;

			this.x2 = x2;
			this.y2 = y2;
			this.z2 = z2;

			this.x3 = x3;
			this.y3 = y3;
			this.z3 = z3;

			this.color1 = color1;
			this.color2 = color2;
			this.color3 = color3;
		}

		public int getColor1() {
			return color1;
		}
		public int getColor2() {
			return color2;
		}
		public int getColor3() {
			return color3;
		}

		public int getId() {
			return id;
		}

		public Triangle setId(int id) {
			this.id = id;
			return this;
		}

	}

	public static class Text {
		public static final byte ALIGNED_LEFT = -1;
		public static final byte ALIGNED_CENTER = 0;
		public static final byte ALIGNED_RIGHT = 1;
		private byte alignment = ALIGNED_LEFT;
		private double x1, y1, z1;
		private String text;
		private int color;

		public Text(double x1, double y1, double z1, String text) {
			this(x1, y1, z1, 0, text, ALIGNED_CENTER);
		}
		public Text(double x1, double y1, double z1, int color, String text, byte alignment) {
			this.x1 = x1;
			this.y1 = y1;
			this.z1 = z1;
			this.color = color;
			this.text = text;
			this.alignment = alignment;
		}

		public double getX1() {
			return x1;
		}

		public void setX1(double x1) {
			this.x1 = x1;
		}

		public double getY1() {
			return y1;
		}

		public void setY1(double y1) {
			this.y1 = y1;
		}

		public double getZ1() {
			return z1;
		}

		public void setZ1(double z1) {
			this.z1 = z1;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

		public byte getAligment() {
			return alignment;
		}

		public int getColor() {
			return this.color;
		}

	}

	private void onDataChanged() {
		this.dataChanged = true;
		flagPendingAjax();
	}

	@Override
	public void initJs() {
		super.initJs();
		flagPendingAjax();
		onDataChanged();
		flagOptionsChanged();
	}

	@Override
	public void handleCallback(String callback, Map<String, String> attributes) {
		if ("onPerspective".equals(callback)) {
			this.rotX = CH.getOrThrow(Caster_Double.PRIMITIVE, attributes, "rx");
			this.rotY = CH.getOrThrow(Caster_Double.PRIMITIVE, attributes, "ry");
			this.rotZ = CH.getOrThrow(Caster_Double.PRIMITIVE, attributes, "rz");
			this.zoom = CH.getOrThrow(Caster_Double.PRIMITIVE, attributes, "zm");
			this.fov = CH.getOrThrow(Caster_Double.PRIMITIVE, attributes, "fov");
			firePerspectiveChanged();
		} else if ("onSelection".equals(callback)) {
			String action = CH.getOrThrow(Caster_String.INSTANCE, attributes, "action");
			if ("clear".equals(action)) {
				if (this.selected.size() == 0)
					return;
				this.selected.clear();
			} else {
				String[] ids = SH.split(',', CH.getOr(Caster_String.INSTANCE, attributes, "ids", ""));
				if ("toggle".equals(action))
					for (String s : ids)
						CH.toggle(this.selected, SH.parseInt(s));
				else {
					if ("replace".equals(action))
						this.selected.clear();
					for (String s : ids)
						this.selected.add(SH.parseInt(s));
				}
			}
			onDataChanged();
			fireSelectionChanged();
		} else if ("showContextMenu".equals(callback)) {
			fireContextMenu();
		} else if ("onHover".equals(callback)) {
			int polyId = CH.getOr(Caster_Integer.INSTANCE, attributes, "polyId", -1);
			int x = CH.getOr(Caster_Integer.INSTANCE, attributes, "mouseX", -1);
			int y = CH.getOr(Caster_Integer.INSTANCE, attributes, "mouseY", -1);
			Triangle triangle = polyId == -1 || polyId >= this.triangles.size() ? null : this.triangles.get(polyId);
			fireOnHover(x, y, polyId, triangle);
		} else
			super.handleCallback(callback, attributes);
	}
	private void fireSelectionChanged() {
		for (Basic3dPortletListener i : this.listeners)
			i.onSelectionChanged(this);
	}
	private void fireContextMenu() {
		for (Basic3dPortletListener i : this.listeners)
			i.onContextMenu(this);
	}
	private void firePerspectiveChanged() {
		for (Basic3dPortletListener i : this.listeners)
			i.onPerspective(this);
	}
	private void fireOnHover(int x, int y, int selectId, Triangle triangle) {
		for (Basic3dPortletListener i : this.listeners)
			i.onHover(this, x, y, selectId, triangle);
	}
	public double getRotX() {
		return rotX;
	}
	public void setRotX(double rotX) {
		if (this.rotX == rotX)
			return;
		this.rotX = rotX;
		flagOptionsChanged();
	}
	public double getRotY() {
		return rotY;
	}
	public void setRotY(double rotY) {
		if (this.rotY == rotY)
			return;
		this.rotY = rotY;
		flagOptionsChanged();
	}
	public double getRotZ() {
		return rotZ;
	}
	public void setRotZ(double rotZ) {
		if (this.rotZ == rotZ)
			return;
		this.rotZ = rotZ;
		flagOptionsChanged();
	}
	public double getZoom() {
		return zoom;
	}
	public void setZoom(double zoom) {
		if (this.zoom == zoom)
			return;
		this.zoom = zoom;
		flagOptionsChanged();
	}
	public double getFov() {
		return fov;
	}
	public void setFov(double fov) {
		if (this.fov == fov)
			return;
		this.fov = fov;
		flagOptionsChanged();
	}

	public void addListener(Basic3dPortletListener listener) {
		this.listeners.add(listener);
	}
	public void removeListener(Basic3dPortletListener listener) {
		this.listeners.remove(listener);
	}
	public String getBackgroundColor() {
		return backgroundColor;
	}
	public void setBackgroundColor(String backgroundColor) {
		if (OH.eq(this.backgroundColor, backgroundColor))
			return;
		this.backgroundColor = backgroundColor;
		flagOptionsChanged();
	}
	public String getBackgroundClass() {
		return backgroundClass;
	}
	public void setBackgroundClass(String backgroundClass) {
		if (OH.eq(this.backgroundClass, backgroundClass))
			return;
		this.backgroundClass = backgroundClass;
		flagOptionsChanged();
	}
	private void flagOptionsChanged() {
		if (this.optionsChanged)
			return;
		this.optionsChanged = true;
		flagPendingAjax();
	}
	public double getCenterX() {
		return centerX;
	}
	public void setCenterX(double centerX) {
		if (this.centerX == centerX)
			return;
		this.centerX = centerX;
		flagOptionsChanged();
	}
	public double getCenterY() {
		return centerY;
	}
	public void setCenterY(double centerY) {
		if (this.centerY == centerY)
			return;
		this.centerY = centerY;
		flagOptionsChanged();
	}

	public double getFocalX() {
		return focalX;
	}
	public void setFocalX(double focalX) {
		if (this.focalX == focalX)
			return;
		this.focalX = focalX;
		flagOptionsChanged();
	}
	public double getFocalY() {
		return focalY;
	}
	public void setFocalY(double focalY) {
		if (this.focalY == focalY)
			return;
		this.focalY = focalY;
		flagOptionsChanged();
	}
	public void setScaleX(double scaleX) {
		if (this.scaleX == scaleX)
			return;
		this.scaleX = scaleX;
		onDataChanged();
	}
	public int getScaleX() {
		return (int) this.scaleX;
	}

	public void setScaleY(double scaleY) {
		if (this.scaleY == scaleY)
			return;
		this.scaleY = scaleY;
		onDataChanged();
	}
	public int getScaleY() {
		return (int) this.scaleY;
	}

	public void setScaleZ(double scaleZ) {
		if (this.scaleZ == scaleZ)
			return;
		this.scaleZ = scaleZ;
		onDataChanged();
	}
	public int getScaleZ() {
		return (int) this.scaleZ;
	}

	public void setShiftX(double shiftX) {
		if (this.shiftX == shiftX)
			return;
		this.shiftX = shiftX;
		onDataChanged();
	}
	public int getShiftX() {
		return (int) this.shiftX;
	}

	public void setShiftY(double shiftY) {
		if (this.shiftY == shiftY)
			return;
		this.shiftY = shiftY;
		onDataChanged();
	}
	public int getShiftY() {
		return (int) this.shiftY;
	}

	public void setShiftZ(double shiftZ) {
		if (this.shiftZ == shiftZ)
			return;
		this.shiftZ = shiftZ;
		onDataChanged();
	}
	public int getShiftZ() {
		return (int) this.shiftZ;
	}
	public Set<Integer> getSelected() {
		return selected;
	}
	public void clear() {
		this.clearLines();
		this.clearSelected();
		this.clearTexts();
		this.clearTriangles();
	}

	public int getSelectedColor() {
		return selectedColor;
	}
	public void setSelectedColor(int selectedColor) {
		this.selectedColor = selectedColor;
	}
	public void setHoverOver(int x, int y, int polyId, String string) {
		if (SH.is(string) && getVisible())
			callJsFunction("setHover").addParam(x).addParam(y).addParam(polyId).addParamQuoted(string).addParam(-1).addParam(1).end();
	}
}
