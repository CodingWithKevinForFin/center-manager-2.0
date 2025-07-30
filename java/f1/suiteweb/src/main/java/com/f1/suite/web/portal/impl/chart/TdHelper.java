package com.f1.suite.web.portal.impl.chart;

import java.util.List;

import com.f1.suite.web.portal.impl.chart.Basic3dPortlet.Triangle;

public class TdHelper {

	static public void newCube(double x1, double y1, double z1, double x2, double y2, double z2, int color, int id, List<Triangle> sink) {
		//front
		sink.add(new Triangle(x1, y1, z1, color, x1, y2, z1, color, x2, y1, z1, color).setId(id));
		sink.add(new Triangle(x1, y2, z1, color, x2, y2, z1, color, x2, y1, z1, color).setId(id));
		//back
		sink.add(new Triangle(x1, y1, z2, color, x1, y2, z2, color, x2, y1, z2, color).setId(id));
		sink.add(new Triangle(x1, y2, z2, color, x2, y2, z2, color, x2, y1, z2, color).setId(id));
		//left
		sink.add(new Triangle(x1, y1, z2, color, x1, y2, z2, color, x1, y1, z1, color).setId(id));
		sink.add(new Triangle(x1, y2, z2, color, x1, y2, z1, color, x1, y1, z1, color).setId(id));
		//right
		sink.add(new Triangle(x2, y1, z2, color, x2, y2, z2, color, x2, y1, z1, color).setId(id));
		sink.add(new Triangle(x2, y2, z2, color, x2, y2, z1, color, x2, y1, z1, color).setId(id));
		//top
		sink.add(new Triangle(x1, y1, z1, color, x1, y1, z2, color, x2, y1, z2, color).setId(id));
		sink.add(new Triangle(x1, y1, z1, color, x2, y1, z2, color, x2, y1, z1, color).setId(id));
		//bottom
		sink.add(new Triangle(x1, y2, z1, color, x1, y2, z2, color, x2, y2, z2, color).setId(id));
		sink.add(new Triangle(x1, y2, z1, color, x2, y2, z2, color, x2, y2, z1, color).setId(id));
	}

	public static void newPyramid(double x1, double y1, double z1, double x2, double y2, double z2, int color, int id, List<Triangle> sink) {
		//find midpoints
		double xMiddle = (x1 + x2) / 2;
		double yMiddle = (y1 + y2) / 2;
		// bottom
		sink.add(new Triangle(x1, y1, z1, color, x2, y1, z1, color, x2, y2, z1, color).setId(id));
		sink.add(new Triangle(x1, y1, z1, color, x1, y2, z1, color, x2, y2, z1, color).setId(id));
		//front
		sink.add(new Triangle(x1, y1, z1, color, x2, y1, z1, color, xMiddle, yMiddle, z2, color).setId(id));
		//left
		sink.add(new Triangle(x1, y1, z1, color, x1, y2, z1, color, xMiddle, yMiddle, z2, color).setId(id));
		//right
		sink.add(new Triangle(x2, y1, z1, color, x2, y2, z1, color, xMiddle, yMiddle, z2, color).setId(id));
		//back
		sink.add(new Triangle(x1, y2, z1, color, x2, y2, z1, color, xMiddle, yMiddle, z2, color).setId(id));
	}

	public static void newDiamond(double x1, double y1, double z1, double x2, double y2, double z2, int color, int id, List<Triangle> sink) {
		// find midpoints
		double zMiddle = (z1 + z2) / 2;
		double xMiddle = (x1 + x2) / 2;
		double yMiddle = (y1 + y2) / 2;
		//front top
		sink.add(new Triangle(x1, y1, zMiddle, color, x2, y1, zMiddle, color, xMiddle, yMiddle, z2, color).setId(id));
		//front bottom
		sink.add(new Triangle(x1, y1, zMiddle, color, x2, y1, zMiddle, color, xMiddle, yMiddle, z1, color).setId(id));
		//left top
		sink.add(new Triangle(x1, y1, zMiddle, color, x1, y2, zMiddle, color, xMiddle, yMiddle, z2, color).setId(id));
		//left bottom
		sink.add(new Triangle(x1, y1, zMiddle, color, x1, y2, zMiddle, color, xMiddle, yMiddle, z1, color).setId(id));
		//right top
		sink.add(new Triangle(x2, y1, zMiddle, color, x2, y2, zMiddle, color, xMiddle, yMiddle, z2, color).setId(id));
		//right bottom
		sink.add(new Triangle(x2, y1, zMiddle, color, x2, y2, zMiddle, color, xMiddle, yMiddle, z1, color).setId(id));
		//back top
		sink.add(new Triangle(x1, y2, zMiddle, color, x2, y2, zMiddle, color, xMiddle, yMiddle, z2, color).setId(id));
		//back bottom
		sink.add(new Triangle(x1, y2, zMiddle, color, x2, y2, zMiddle, color, xMiddle, yMiddle, z1, color).setId(id));
	}

	public static void newTriangle(double x1, double y1, double z1, double x2, double y2, double z2, int color, int id, List<Triangle> sink) {
		// find midpoints
		double xMiddle = (x1 + x2) / 2;
		double yMiddle = (y1 + y2) / 2;
		//front
		sink.add(new Triangle(x1, y1, z1, color, x2, y1, z1, color, xMiddle, yMiddle, z2, color).setId(id));
		//left
		sink.add(new Triangle(x1, y1, z1, color, xMiddle, y2, z1, color, xMiddle, yMiddle, z2, color).setId(id));
		//right
		sink.add(new Triangle(x2, y1, z1, color, xMiddle, y2, z1, color, xMiddle, yMiddle, z2, color).setId(id));
		//bottom
		sink.add(new Triangle(x1, y1, z1, color, xMiddle, y2, z1, color, x2, y1, z1, color).setId(id));
	}

}
