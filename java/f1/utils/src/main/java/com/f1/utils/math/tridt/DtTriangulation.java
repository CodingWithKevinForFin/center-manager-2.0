package com.f1.utils.math.tridt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class DtTriangulation {

	// the first and last points (used only for first step construction)
	private DtPoint firstP;
	private DtPoint lastP;

	// for degenerate case!
	private boolean allCollinear;

	// the first and last triangles (used only for first step construction)
	private DtTriangle firstT, lastT, currT;

	// the triangle the fond (search start from
	private DtTriangle startTriangle;

	// the triangle the convex hull starts from
	public DtTriangle startTriangleHull;

	private int nPoints = 0; // number of points
	final private Set<DtPoint> points;
	final private List<DtTriangle> _triangles;

	// The triangles that were deleted in the last deletePoint iteration.
	private List<DtTriangle> deletedTriangles;
	// The triangles that were added in the last deletePoint iteration.
	private List<DtTriangle> addedTriangles;

	private int _modCount = 0, _modCount2 = 0;

	// the Bounding Box, {{x0,y0,z0} , {x1,y1,z1}}
	private DtPoint _bb_min, _bb_max;
	private DtRect boundingBox;

	/**
	 * Index for faster point location searches
	 */
	private DtGrid gridIndex = null;

	/**
	 * creates an empty Delaunay Triangulation.
	 */
	public DtTriangulation() {
		_modCount = 0;
		_modCount2 = 0;
		_bb_min = null;
		_bb_max = null;
		_triangles = new ArrayList<DtTriangle>();
		addedTriangles = new ArrayList<DtTriangle>();
		deletedTriangles = null;
		allCollinear = true;
		this.points = new TreeSet<DtPoint>();
	}

	public DtTriangulation(DtPoint[] ps) {
		this();
		for (int i = 0; i < ps.length; i++)
			this.addPoint(ps[i]);
	}

	public int size() {
		if (points == null) {
			return 0;
		}
		return points.size();
	}

	/**
	 * @return the number of triangles in the triangulation. <br />
	 *         Note: includes infinife faces!!.
	 */
	public int trianglesSize() {
		this.initTriangles();
		return _triangles.size();
	}

	/**
	 * returns the changes counter for this triangulation
	 */
	public int getModeCounter() {
		return this._modCount;
	}

	/**
	 * insert the point to this Delaunay Triangulation. Note: if p is null or already exist in this triangulation p is ignored.
	 * 
	 * @param p
	 *            new vertex to be inserted the triangulation.
	 */
	public void addPoint(DtPoint p) {
		if (this.points.contains(p))
			return;
		_modCount++;
		updateBoundingBox(p);
		this.points.add(p);
		DtTriangle t = insertPointSimple(p);
		if (t == null) //
			return;
		DtTriangle tt = t;
		currT = t; // recall the last point for - fast (last) update iterator.
		do {
			flip(tt, _modCount);
			tt = tt.canext;
		} while (tt != t && !tt.isHalfplane());

		// Update index with changed triangles
		if (gridIndex != null)
			gridIndex.updateIndex(getLastUpdatedTriangles());
	}

	/**
	 * Deletes the given point from this.
	 * 
	 * @param pointToDelete
	 *            The given point to delete.
	 * 
	 *            Implementation of the Mostafavia, Gold & Dakowicz algorithm (2002).
	 * 
	 *            By Eyal Roth & Doron Ganel (2009).
	 */
	public void deletePoint(DtPoint pointToDelete) {

		// Finding the triangles to delete.
		List<DtPoint> pointsVec = findConnectedVertices(pointToDelete, true);
		if (pointsVec == null) {
			return;
		}

		while (pointsVec.size() >= 3) {
			// Getting a triangle to add, and saving it.
			DtTriangle triangle = findTriangle(pointsVec, pointToDelete);
			addedTriangles.add(triangle);

			// Finding the point on the diagonal (pointToDelete,p)
			DtPoint p = findDiagonal(triangle, pointToDelete);

			for (DtPoint tmpP : pointsVec) {
				if (tmpP.equals(p)) {
					pointsVec.remove(tmpP);
					break;
				}
			}
		}
		//updating the trangulation
		deleteUpdate(pointToDelete);
		for (DtTriangle t : deletedTriangles) {
			if (t == startTriangle) {
				startTriangle = addedTriangles.get(0);
				break;
			}
		}
		_triangles.removeAll(deletedTriangles);
		_triangles.addAll(addedTriangles);
		points.remove(pointToDelete);
		nPoints = nPoints + addedTriangles.size() - deletedTriangles.size();
		addedTriangles.clear();
		deletedTriangles.clear();
	}

	/**
	 * return a point from the trangulation that is close to pointToDelete
	 * 
	 * @param pointToDelete
	 *            the point that the user wants to delete
	 * @return a point from the trangulation that is close to pointToDelete By Eyal Roth & Doron Ganel (2009).
	 */
	public DtPoint findClosePoint(DtPoint pointToDelete) {
		DtTriangle triangle = find(pointToDelete);
		DtPoint p1 = triangle.getPoint1();
		DtPoint p2 = triangle.getPoint2();
		double d1 = p1.distance(pointToDelete);
		double d2 = p2.distance(pointToDelete);
		if (triangle.isHalfplane()) {
			if (d1 <= d2) {
				return p1;
			} else {
				return p2;
			}
		} else {
			DtPoint p3 = triangle.getPoint3();

			double d3 = p3.distance(pointToDelete);
			if (d1 <= d2 && d1 <= d3) {
				return p1;
			} else if (d2 <= d1 && d2 <= d3) {
				return p2;
			} else {
				return p3;
			}
		}
	}

	//updates the trangulation after the triangles to be deleted and
	//the triangles to be added were found
	//by Doron Ganel & Eyal Roth(2009)
	private void deleteUpdate(DtPoint pointToDelete) {
		for (DtTriangle addedTriangle1 : addedTriangles) {
			//update between addedd triangles and deleted triangles
			for (DtTriangle deletedTriangle : deletedTriangles) {
				if (shareSegment(addedTriangle1, deletedTriangle)) {
					updateNeighbor(addedTriangle1, deletedTriangle, pointToDelete);
				}
			}
		}
		for (DtTriangle addedTriangle1 : addedTriangles) {
			//update between added triangles
			for (DtTriangle addedTriangle2 : addedTriangles) {
				if ((addedTriangle1 != addedTriangle2) && (shareSegment(addedTriangle1, addedTriangle2))) {
					updateNeighbor(addedTriangle1, addedTriangle2);
				}
			}
		}

		// Update index with changed triangles
		if (gridIndex != null)
			gridIndex.updateIndex(addedTriangles);

	}

	//checks if the 2 triangles shares a segment
	//by Doron Ganel & Eyal Roth(2009)
	private boolean shareSegment(DtTriangle t1, DtTriangle t2) {
		int counter = 0;
		DtPoint t1P1 = t1.getPoint1();
		DtPoint t1P2 = t1.getPoint2();
		DtPoint t1P3 = t1.getPoint3();
		DtPoint t2P1 = t2.getPoint1();
		DtPoint t2P2 = t2.getPoint2();
		DtPoint t2P3 = t2.getPoint3();

		if (t1P1.equals(t2P1)) {
			counter++;
		}
		if (t1P1.equals(t2P2)) {
			counter++;
		}
		if (t1P1.equals(t2P3)) {
			counter++;
		}
		if (t1P2.equals(t2P1)) {
			counter++;
		}
		if (t1P2.equals(t2P2)) {
			counter++;
		}
		if (t1P2.equals(t2P3)) {
			counter++;
		}
		if (t1P3.equals(t2P1)) {
			counter++;
		}
		if (t1P3.equals(t2P2)) {
			counter++;
		}
		if (t1P3.equals(t2P3)) {
			counter++;
		}
		if (counter >= 2)
			return true;
		else
			return false;
	}

	//update the neighbors of the addedTriangle and deletedTriangle
	//we assume the 2 triangles share a segment
	//by Doron Ganel & Eyal Roth(2009)
	private void updateNeighbor(DtTriangle addedTriangle, DtTriangle deletedTriangle, DtPoint pointToDelete) {
		DtPoint delA = deletedTriangle.getPoint1();
		DtPoint delB = deletedTriangle.getPoint2();
		DtPoint delC = deletedTriangle.getPoint3();
		DtPoint addA = addedTriangle.getPoint1();
		DtPoint addB = addedTriangle.getPoint2();
		DtPoint addC = addedTriangle.getPoint3();

		//updates the neighbor of the deleted triangle to point to the added triangle
		//setting the neighbor of the added triangle
		if (pointToDelete.equals(delA)) {
			deletedTriangle.getAdjacent23().switchAdjacent(deletedTriangle, addedTriangle);
			//AB-BC || BA-BC
			if ((addA.equals(delB) && addB.equals(delC)) || (addB.equals(delB) && addA.equals(delC))) {
				addedTriangle.abnext = deletedTriangle.getAdjacent23();
			}
			//AC-BC || CA-BC
			else if ((addA.equals(delB) && addC.equals(delC)) || (addC.equals(delB) && addA.equals(delC))) {
				addedTriangle.canext = deletedTriangle.getAdjacent23();
			}
			//BC-BC || CB-BC
			else {
				addedTriangle.bcnext = deletedTriangle.getAdjacent23();
			}
		} else if (pointToDelete.equals(delB)) {
			deletedTriangle.getAdjacent31().switchAdjacent(deletedTriangle, addedTriangle);
			//AB-AC || BA-AC
			if ((addA.equals(delA) && addB.equals(delC)) || (addB.equals(delA) && addA.equals(delC))) {
				addedTriangle.abnext = deletedTriangle.getAdjacent31();
			}
			//AC-AC || CA-AC
			else if ((addA.equals(delA) && addC.equals(delC)) || (addC.equals(delA) && addA.equals(delC))) {
				addedTriangle.canext = deletedTriangle.getAdjacent31();
			}
			//BC-AC || CB-AC
			else {
				addedTriangle.bcnext = deletedTriangle.getAdjacent31();
			}
		}
		//equals c
		else {
			deletedTriangle.getAdjacent12().switchAdjacent(deletedTriangle, addedTriangle);
			//AB-AB || BA-AB
			if ((addA.equals(delA) && addB.equals(delB)) || (addB.equals(delA) && addA.equals(delB))) {
				addedTriangle.abnext = deletedTriangle.getAdjacent12();
			}
			//AC-AB || CA-AB
			else if ((addA.equals(delA) && addC.equals(delB)) || (addC.equals(delA) && addA.equals(delB))) {
				addedTriangle.canext = deletedTriangle.getAdjacent12();
			}
			//BC-AB || CB-AB
			else {
				addedTriangle.bcnext = deletedTriangle.getAdjacent12();
			}
		}
	}

	//update the neighbors of the 2 added Triangle s
	//we assume the 2 triangles share a segment
	//by Doron Ganel & Eyal Roth(2009)
	private void updateNeighbor(DtTriangle addedTriangle1, DtTriangle addedTriangle2) {
		DtPoint A1 = addedTriangle1.getPoint1();
		DtPoint B1 = addedTriangle1.getPoint2();
		DtPoint C1 = addedTriangle1.getPoint3();
		DtPoint A2 = addedTriangle2.getPoint1();
		DtPoint B2 = addedTriangle2.getPoint2();
		DtPoint C2 = addedTriangle2.getPoint3();

		//A1-A2
		if (A1.equals(A2)) {
			//A1B1-A2B2
			if (B1.equals(B2)) {
				addedTriangle1.abnext = addedTriangle2;
				addedTriangle2.abnext = addedTriangle1;
			}
			//A1B1-A2C2
			else if (B1.equals(C2)) {
				addedTriangle1.abnext = addedTriangle2;
				addedTriangle2.canext = addedTriangle1;
			}
			//A1C1-A2B2
			else if (C1.equals(B2)) {
				addedTriangle1.canext = addedTriangle2;
				addedTriangle2.abnext = addedTriangle1;
			}
			//A1C1-A2C2
			else {
				addedTriangle1.canext = addedTriangle2;
				addedTriangle2.canext = addedTriangle1;
			}
		}
		//A1-B2
		else if (A1.equals(B2)) {
			//A1B1-B2A2
			if (B1.equals(A2)) {
				addedTriangle1.abnext = addedTriangle2;
				addedTriangle2.abnext = addedTriangle1;
			}
			//A1B1-B2C2
			else if (B1.equals(C2)) {
				addedTriangle1.abnext = addedTriangle2;
				addedTriangle2.bcnext = addedTriangle1;
			}
			//A1C1-B2A2
			else if (C1.equals(A2)) {
				addedTriangle1.canext = addedTriangle2;
				addedTriangle2.abnext = addedTriangle1;
			}
			//A1C1-B2C2
			else {
				addedTriangle1.canext = addedTriangle2;
				addedTriangle2.bcnext = addedTriangle1;
			}
		}
		//A1-C2
		else if (A1.equals(C2)) {
			//A1B1-C2A2
			if (B1.equals(A2)) {
				addedTriangle1.abnext = addedTriangle2;
				addedTriangle2.canext = addedTriangle1;
			}
			//A1B1-C2B2
			if (B1.equals(B2)) {
				addedTriangle1.abnext = addedTriangle2;
				addedTriangle2.bcnext = addedTriangle1;
			}
			//A1C1-C2A2
			if (C1.equals(A2)) {
				addedTriangle1.canext = addedTriangle2;
				addedTriangle2.canext = addedTriangle1;
			}
			//A1C1-C2B2
			else {
				addedTriangle1.canext = addedTriangle2;
				addedTriangle2.bcnext = addedTriangle1;
			}
		}
		//B1-A2
		else if (B1.equals(A2)) {
			//B1A1-A2B2
			if (A1.equals(B2)) {
				addedTriangle1.abnext = addedTriangle2;
				addedTriangle2.abnext = addedTriangle1;
			}
			//B1A1-A2C2
			else if (A1.equals(C2)) {
				addedTriangle1.abnext = addedTriangle2;
				addedTriangle2.canext = addedTriangle1;
			}
			//B1C1-A2B2
			else if (C1.equals(B2)) {
				addedTriangle1.bcnext = addedTriangle2;
				addedTriangle2.abnext = addedTriangle1;
			}
			//B1C1-A2C2
			else {
				addedTriangle1.bcnext = addedTriangle2;
				addedTriangle2.canext = addedTriangle1;
			}
		}
		//B1-B2
		else if (B1.equals(B2)) {
			//B1A1-B2A2
			if (A1.equals(A2)) {
				addedTriangle1.abnext = addedTriangle2;
				addedTriangle2.abnext = addedTriangle1;
			}
			//B1A1-B2C2
			else if (A1.equals(C2)) {
				addedTriangle1.abnext = addedTriangle2;
				addedTriangle2.bcnext = addedTriangle1;
			}
			//B1C1-B2A2
			else if (C1.equals(A2)) {
				addedTriangle1.bcnext = addedTriangle2;
				addedTriangle2.abnext = addedTriangle1;
			}
			//B1C1-B2C2
			else {
				addedTriangle1.bcnext = addedTriangle2;
				addedTriangle2.bcnext = addedTriangle1;
			}
		}
		//B1-C2
		else if (B1.equals(C2)) {
			//B1A1-C2A2
			if (A1.equals(A2)) {
				addedTriangle1.abnext = addedTriangle2;
				addedTriangle2.canext = addedTriangle1;
			}
			//B1A1-C2B2
			if (A1.equals(B2)) {
				addedTriangle1.abnext = addedTriangle2;
				addedTriangle2.bcnext = addedTriangle1;
			}
			//B1C1-C2A2
			if (C1.equals(A2)) {
				addedTriangle1.bcnext = addedTriangle2;
				addedTriangle2.canext = addedTriangle1;
			}
			//B1C1-C2B2
			else {
				addedTriangle1.bcnext = addedTriangle2;
				addedTriangle2.bcnext = addedTriangle1;
			}
		}
		//C1-A2
		else if (C1.equals(A2)) {
			//C1A1-A2B2
			if (A1.equals(B2)) {
				addedTriangle1.canext = addedTriangle2;
				addedTriangle2.abnext = addedTriangle1;
			}
			//C1A1-A2C2
			else if (A1.equals(C2)) {
				addedTriangle1.canext = addedTriangle2;
				addedTriangle2.canext = addedTriangle1;
			}
			//C1B1-A2B2
			else if (B1.equals(B2)) {
				addedTriangle1.bcnext = addedTriangle2;
				addedTriangle2.abnext = addedTriangle1;
			}
			//C1B1-A2C2
			else {
				addedTriangle1.bcnext = addedTriangle2;
				addedTriangle2.canext = addedTriangle1;
			}
		}
		//C1-B2
		else if (C1.equals(B2)) {
			//C1A1-B2A2
			if (A1.equals(A2)) {
				addedTriangle1.canext = addedTriangle2;
				addedTriangle2.abnext = addedTriangle1;
			}
			//C1A1-B2C2
			else if (A1.equals(C2)) {
				addedTriangle1.canext = addedTriangle2;
				addedTriangle2.bcnext = addedTriangle1;
			}
			//C1B1-B2A2
			else if (B1.equals(A2)) {
				addedTriangle1.bcnext = addedTriangle2;
				addedTriangle2.abnext = addedTriangle1;
			}
			//C1B1-B2C2
			else {
				addedTriangle1.bcnext = addedTriangle2;
				addedTriangle2.bcnext = addedTriangle1;
			}
		}
		//C1-C2
		else if (C1.equals(C2)) {
			//C1A1-C2A2
			if (A1.equals(A2)) {
				addedTriangle1.canext = addedTriangle2;
				addedTriangle2.canext = addedTriangle1;
			}
			//C1A1-C2B2
			if (A1.equals(B2)) {
				addedTriangle1.canext = addedTriangle2;
				addedTriangle2.bcnext = addedTriangle1;
			}
			//C1B1-C2A2
			if (B1.equals(A2)) {
				addedTriangle1.bcnext = addedTriangle2;
				addedTriangle2.canext = addedTriangle1;
			}
			//C1B1-C2B2
			else {
				addedTriangle1.bcnext = addedTriangle2;
				addedTriangle2.bcnext = addedTriangle1;
			}
		}
	}

	//finds the a point on the triangle that if connect it to "point" (creating a segment)
	//the other two points of the triangle will be to the left and to the right of the segment
	//by Doron Ganel & Eyal Roth(2009)
	private DtPoint findDiagonal(DtTriangle triangle, DtPoint point) {
		DtPoint p1 = triangle.getPoint1();
		DtPoint p2 = triangle.getPoint2();
		DtPoint p3 = triangle.getPoint3();

		if ((p1.pointLineTest(point, p3) == DtPoint.LEFT) && (p2.pointLineTest(point, p3) == DtPoint.RIGHT))
			return p3;
		if ((p3.pointLineTest(point, p2) == DtPoint.LEFT) && (p1.pointLineTest(point, p2) == DtPoint.RIGHT))
			return p2;
		if ((p2.pointLineTest(point, p1) == DtPoint.LEFT) && (p3.pointLineTest(point, p1) == DtPoint.RIGHT))
			return p1;
		return null;
	}

	public DtPoint[] calcVoronoiCell(DtTriangle triangle, DtPoint p) {
		// handle any full triangle		 
		if (!triangle.isHalfplane()) {

			// get all neighbors of given corner point
			List<DtTriangle> neighbors = findTriangleNeighborhood(triangle, p);

			Iterator<DtTriangle> itn = neighbors.iterator();
			DtPoint[] vertices = new DtPoint[neighbors.size()];

			// for each neighbor, including the given triangle, add
			// center of circumscribed circle to cell polygon
			int index = 0;
			while (itn.hasNext()) {
				DtTriangle tmp = itn.next();
				vertices[index++] = tmp.circumcircle().getCenter();
			}

			return vertices;
		}

		// handle half plane
		// in this case, the cell is a single line
		// which is the perpendicular bisector of the half plane line
		else {
			// local friendly alias			
			DtTriangle halfplane = triangle;
			// third point of triangle adjacent to this half plane
			// (the point not shared with the half plane)
			DtPoint third = null;
			// triangle adjacent to the half plane
			DtTriangle neighbor = null;

			// find the neighbor triangle
			if (!halfplane.getAdjacent12().isHalfplane()) {
				neighbor = halfplane.getAdjacent12();
			} else if (!halfplane.getAdjacent23().isHalfplane()) {
				neighbor = halfplane.getAdjacent23();
			} else if (!halfplane.getAdjacent23().isHalfplane()) {
				neighbor = halfplane.getAdjacent31();
			} else
				throw new IllegalStateException();

			// find third point of neighbor triangle
			// (the one which is not shared with current half plane)
			// this is used in determining half plane orientation
			if (!neighbor.getPoint1().equals(halfplane.getPoint1()) && !neighbor.getPoint1().equals(halfplane.getPoint2()))
				third = neighbor.getPoint1();
			if (!neighbor.getPoint2().equals(halfplane.getPoint1()) && !neighbor.getPoint2().equals(halfplane.getPoint2()))
				third = neighbor.getPoint2();
			if (!neighbor.getPoint3().equals(halfplane.getPoint1()) && !neighbor.getPoint3().equals(halfplane.getPoint2()))
				third = neighbor.getPoint3();

			// delta (slope) of half plane edge
			double halfplane_delta = (halfplane.getPoint1().getY() - halfplane.getPoint2().getY()) / (halfplane.getPoint1().getX() - halfplane.getPoint2().getX());

			// delta of line perpendicular to current half plane edge
			double perp_delta = (1.0 / halfplane_delta) * (-1.0);

			// determine orientation: find if the third point of the triangle
			// lies above or below the half plane
			// works by finding the matching y value on the half plane line equation
			// for the same x value as the third point
			double y_orient = halfplane_delta * (third.getX() - halfplane.getPoint1().getX()) + halfplane.getPoint1().getY();
			boolean above = true;
			if (y_orient > third.getY())
				above = false;

			// based on orientation, determine cell line direction
			// (towards right or left side of window)
			double sign = 1.0;
			if ((perp_delta < 0 && !above) || (perp_delta > 0 && above))
				sign = -1.0;

			// the cell line is a line originating from the circumcircle to infinity
			// x = 500.0 is used as a large enough value
			DtPoint circumcircle = neighbor.circumcircle().getCenter();
			double x_cell_line = (circumcircle.getX() + (500.0 * sign));
			double y_cell_line = perp_delta * (x_cell_line - circumcircle.getX()) + circumcircle.getY();

			DtPoint[] result = new DtPoint[2];
			result[0] = circumcircle;
			result[1] = new DtPoint(x_cell_line, y_cell_line);

			return result;
		}
	}

	public Collection<DtTriangle> getLastUpdatedTriangles() {
		List<DtTriangle> tmp = new ArrayList<DtTriangle>();
		if (this.trianglesSize() > 1) {
			DtTriangle t = currT;
			allTriangles(t, tmp, this._modCount);
		}
		return tmp;
	}

	private void allTriangles(DtTriangle curr, List<DtTriangle> front, int mc) {
		if (curr != null && curr._mc == mc && !front.contains(curr)) {
			front.add(curr);
			allTriangles(curr.abnext, front, mc);
			allTriangles(curr.bcnext, front, mc);
			allTriangles(curr.canext, front, mc);
		}
	}

	private DtTriangle insertPointSimple(DtPoint p) {
		nPoints++;
		if (!allCollinear) {
			DtTriangle t = find(startTriangle, p);
			if (t.isHalfplane())
				startTriangle = extendOutside(t, p);
			else
				startTriangle = extendInside(t, p);
			return startTriangle;
		}

		if (nPoints == 1) {
			firstP = p;
			return null;
		}

		if (nPoints == 2) {
			startTriangulation(firstP, p);
			return null;
		}

		switch (p.pointLineTest(firstP, lastP)) {
			case DtPoint.LEFT:
				startTriangle = extendOutside(firstT.abnext, p);
				allCollinear = false;
				break;
			case DtPoint.RIGHT:
				startTriangle = extendOutside(firstT, p);
				allCollinear = false;
				break;
			case DtPoint.ONSEGMENT:
				insertCollinear(p, DtPoint.ONSEGMENT);
				break;
			case DtPoint.INFRONTOFA:
				insertCollinear(p, DtPoint.INFRONTOFA);
				break;
			case DtPoint.BEHINDB:
				insertCollinear(p, DtPoint.BEHINDB);
				break;
		}
		return null;
	}

	private void insertCollinear(DtPoint p, int res) {
		DtTriangle t, tp, u;

		switch (res) {
			case DtPoint.INFRONTOFA:
				t = new DtTriangle(firstP, p);
				tp = new DtTriangle(p, firstP);
				t.abnext = tp;
				tp.abnext = t;
				t.bcnext = tp;
				tp.canext = t;
				t.canext = firstT;
				firstT.bcnext = t;
				tp.bcnext = firstT.abnext;
				firstT.abnext.canext = tp;
				firstT = t;
				firstP = p;
				break;
			case DtPoint.BEHINDB:
				t = new DtTriangle(p, lastP);
				tp = new DtTriangle(lastP, p);
				t.abnext = tp;
				tp.abnext = t;
				t.bcnext = lastT;
				lastT.canext = t;
				t.canext = tp;
				tp.bcnext = t;
				tp.canext = lastT.abnext;
				lastT.abnext.bcnext = tp;
				lastT = t;
				lastP = p;
				break;
			case DtPoint.ONSEGMENT:
				u = firstT;
				while (p.isGreater(u.a))
					u = u.canext;
				t = new DtTriangle(p, u.b);
				tp = new DtTriangle(u.b, p);
				u.b = p;
				u.abnext.a = p;
				t.abnext = tp;
				tp.abnext = t;
				t.bcnext = u.bcnext;
				u.bcnext.canext = t;
				t.canext = u;
				u.bcnext = t;
				tp.canext = u.abnext.canext;
				u.abnext.canext.bcnext = tp;
				tp.bcnext = u.abnext;
				u.abnext.canext = tp;
				if (firstT == u) {
					firstT = t;
				}
				break;
		}
	}

	private void startTriangulation(DtPoint p1, DtPoint p2) {
		DtPoint ps, pb;
		if (p1.isLess(p2)) {
			ps = p1;
			pb = p2;
		} else {
			ps = p2;
			pb = p1;
		}
		firstT = new DtTriangle(pb, ps);
		lastT = firstT;
		DtTriangle t = new DtTriangle(ps, pb);
		firstT.abnext = t;
		t.abnext = firstT;
		firstT.bcnext = t;
		t.canext = firstT;
		firstT.canext = t;
		t.bcnext = firstT;
		firstP = firstT.b;
		lastP = lastT.a;
		startTriangleHull = firstT;
	}

	private DtTriangle extendInside(DtTriangle t, DtPoint p) {

		DtTriangle h1, h2;
		h1 = treatDegeneracyInside(t, p);
		if (h1 != null)
			return h1;

		h1 = new DtTriangle(t.c, t.a, p);
		h2 = new DtTriangle(t.b, t.c, p);
		t.c = p;
		t.circumcircle();
		h1.abnext = t.canext;
		h1.bcnext = t;
		h1.canext = h2;
		h2.abnext = t.bcnext;
		h2.bcnext = h1;
		h2.canext = t;
		h1.abnext.switchAdjacent(t, h1);
		h2.abnext.switchAdjacent(t, h2);
		t.bcnext = h2;
		t.canext = h1;
		return t;
	}

	private DtTriangle treatDegeneracyInside(DtTriangle t, DtPoint p) {

		if (t.abnext.isHalfplane() && p.pointLineTest(t.b, t.a) == DtPoint.ONSEGMENT)
			return extendOutside(t.abnext, p);
		if (t.bcnext.isHalfplane() && p.pointLineTest(t.c, t.b) == DtPoint.ONSEGMENT)
			return extendOutside(t.bcnext, p);
		if (t.canext.isHalfplane() && p.pointLineTest(t.a, t.c) == DtPoint.ONSEGMENT)
			return extendOutside(t.canext, p);
		return null;
	}

	private DtTriangle extendOutside(DtTriangle t, DtPoint p) {

		if (p.pointLineTest(t.a, t.b) == DtPoint.ONSEGMENT) {
			DtTriangle dg = new DtTriangle(t.a, t.b, p);
			DtTriangle hp = new DtTriangle(p, t.b);
			t.b = p;
			dg.abnext = t.abnext;
			dg.abnext.switchAdjacent(t, dg);
			dg.bcnext = hp;
			hp.abnext = dg;
			dg.canext = t;
			t.abnext = dg;
			hp.bcnext = t.bcnext;
			hp.bcnext.canext = hp;
			hp.canext = t;
			t.bcnext = hp;
			return dg;
		}
		DtTriangle ccT = extendcounterclock(t, p);
		DtTriangle cT = extendclock(t, p);
		ccT.bcnext = cT;
		cT.canext = ccT;
		startTriangleHull = cT;
		return cT.abnext;
	}

	private DtTriangle extendcounterclock(DtTriangle t, DtPoint p) {

		t.halfplane = false;
		t.c = p;
		t.circumcircle();

		DtTriangle tca = t.canext;

		if (p.pointLineTest(tca.a, tca.b) >= DtPoint.RIGHT) {
			DtTriangle nT = new DtTriangle(t.a, p);
			nT.abnext = t;
			t.canext = nT;
			nT.canext = tca;
			tca.bcnext = nT;
			return nT;
		}
		return extendcounterclock(tca, p);
	}

	private DtTriangle extendclock(DtTriangle t, DtPoint p) {

		t.halfplane = false;
		t.c = p;
		t.circumcircle();

		DtTriangle tbc = t.bcnext;

		if (p.pointLineTest(tbc.a, tbc.b) >= DtPoint.RIGHT) {
			DtTriangle nT = new DtTriangle(p, t.b);
			nT.abnext = t;
			t.bcnext = nT;
			nT.bcnext = tbc;
			tbc.canext = nT;
			return nT;
		}
		return extendclock(tbc, p);
	}

	private void flip(DtTriangle t, int mc) {

		DtTriangle u = t.abnext, v;
		t._mc = mc;
		if (u.isHalfplane() || !u.circumcircle_contains(t.c))
			return;

		if (t.a == u.a) {
			v = new DtTriangle(u.b, t.b, t.c);
			v.abnext = u.bcnext;
			t.abnext = u.abnext;
		} else if (t.a == u.b) {
			v = new DtTriangle(u.c, t.b, t.c);
			v.abnext = u.canext;
			t.abnext = u.bcnext;
		} else if (t.a == u.c) {
			v = new DtTriangle(u.a, t.b, t.c);
			v.abnext = u.abnext;
			t.abnext = u.canext;
		} else {
			throw new RuntimeException("Error in flip: " + t + ", " + u);
		}

		v._mc = mc;
		v.bcnext = t.bcnext;
		v.abnext.switchAdjacent(u, v);
		v.bcnext.switchAdjacent(t, v);
		t.bcnext = v;
		v.canext = t;
		t.b = v.a;
		t.abnext.switchAdjacent(u, t);
		t.circumcircle();

		currT = v;
		flip(t, mc);
		flip(v, mc);
	}

	public DtTriangle find(DtPoint p) {

		DtTriangle searchTriangle;
		if (gridIndex != null) {
			DtTriangle indexTriangle = gridIndex.findCellTriangleOf(p);
			if (indexTriangle != null)
				searchTriangle = indexTriangle;
			else
				searchTriangle = startTriangle;
		} else
			searchTriangle = startTriangle;
		return find(searchTriangle, p);
	}

	public DtTriangle find(DtPoint p, DtTriangle start) {
		if (start == null)
			start = this.startTriangle;
		DtTriangle T = find(start, p);
		return T;
	}

	private static DtTriangle find(DtTriangle curr, DtPoint p) {
		if (p == null)
			return null;
		DtTriangle next_t;
		if (curr.isHalfplane()) {
			next_t = findnext2(p, curr);
			if (next_t == null || next_t.isHalfplane())
				return curr;
			curr = next_t;
		}
		int i = 0;
		while (true) {
			i++;
			next_t = findnext1(p, curr);

			if (next_t == null)
				return curr;
			if (next_t.isHalfplane())
				return next_t;
			curr = next_t;

			if (i > 1000000) {
				throw new RuntimeException("Surface Failed To Render");
			}
		}
	}
	/*
	 * assumes v is NOT an halfplane!
	 * returns the next triangle for find.
	 */
	private static DtTriangle findnext1(DtPoint p, DtTriangle v) {
		if (p.pointLineTest(v.a, v.b) == DtPoint.RIGHT && !v.abnext.isHalfplane())
			return v.abnext;
		if (p.pointLineTest(v.b, v.c) == DtPoint.RIGHT && !v.bcnext.isHalfplane())
			return v.bcnext;
		if (p.pointLineTest(v.c, v.a) == DtPoint.RIGHT && !v.canext.isHalfplane())
			return v.canext;
		if (p.pointLineTest(v.a, v.b) == DtPoint.RIGHT)
			return v.abnext;
		if (p.pointLineTest(v.b, v.c) == DtPoint.RIGHT)
			return v.bcnext;
		if (p.pointLineTest(v.c, v.a) == DtPoint.RIGHT)
			return v.canext;
		return null;
	}

	/** assumes v is an halfplane! - returns another (none halfplane) triangle */
	private static DtTriangle findnext2(DtPoint p, DtTriangle v) {
		if (v.abnext != null && !v.abnext.isHalfplane())
			return v.abnext;
		if (v.bcnext != null && !v.bcnext.isHalfplane())
			return v.bcnext;
		if (v.canext != null && !v.canext.isHalfplane())
			return v.canext;
		return null;
	}

	/* 
	 * Receives a point and returns all the points of the triangles that
	 * shares point as a corner (Connected vertices to this point).
	 * 
	 * Set saveTriangles to true if you wish to save the triangles that were found.
	 * 
	 * By Doron Ganel & Eyal Roth
	 */
	private List<DtPoint> findConnectedVertices(DtPoint point, boolean saveTriangles) {
		Set<DtPoint> pointsSet = new HashSet<DtPoint>();
		List<DtPoint> pointsVec = new ArrayList<DtPoint>();
		List<DtTriangle> triangles = null;
		// Getting one of the neigh
		DtTriangle triangle = find(point);

		// Validating find result.
		if (!triangle.isCorner(point)) {
			System.err.println("findConnectedVertices: Could not find connected vertices since the first found triangle doesn't" + " share the given point.");
			return null;
		}

		triangles = findTriangleNeighborhood(triangle, point);
		if (triangles == null) {
			System.err.println("Error: can't delete a point on the perimeter");
			return null;
		}
		if (saveTriangles) {
			deletedTriangles = triangles;
		}

		for (DtTriangle tmpTriangle : triangles) {
			DtPoint point1 = tmpTriangle.getPoint1();
			DtPoint point2 = tmpTriangle.getPoint2();
			DtPoint point3 = tmpTriangle.getPoint3();

			if (point1.equals(point) && !pointsSet.contains(point2)) {
				pointsSet.add(point2);
				pointsVec.add(point2);
			}

			if (point2.equals(point) && !pointsSet.contains(point3)) {
				pointsSet.add(point3);
				pointsVec.add(point3);
			}

			if (point3.equals(point) && !pointsSet.contains(point1)) {
				pointsSet.add(point1);
				pointsVec.add(point1);
			}
		}

		return pointsVec;
	}

	// Walks on a consistent side of triangles until a cycle is achieved.
	//By Doron Ganel & Eyal Roth
	// changed to public by Udi
	public List<DtTriangle> findTriangleNeighborhood(DtTriangle firstTriangle, DtPoint point) {
		List<DtTriangle> triangles = new ArrayList<DtTriangle>(30);
		triangles.add(firstTriangle);

		DtTriangle prevTriangle = null;
		DtTriangle currentTriangle = firstTriangle;
		DtTriangle nextTriangle = currentTriangle.nextNeighbor(point, prevTriangle);

		while (nextTriangle != firstTriangle) {
			//the point is on the perimeter
			if (nextTriangle.isHalfplane()) {
				return null;
			}
			triangles.add(nextTriangle);
			prevTriangle = currentTriangle;
			currentTriangle = nextTriangle;
			nextTriangle = currentTriangle.nextNeighbor(point, prevTriangle);
		}

		return triangles;
	}

	/*
	 * find triangle to be added to the triangulation
	 * 
	 * By: Doron Ganel & Eyal Roth
	 * 
	 */
	private DtTriangle findTriangle(List<DtPoint> pointsVec, DtPoint p) {
		DtPoint[] arrayPoints = new DtPoint[pointsVec.size()];
		pointsVec.toArray(arrayPoints);

		int size = arrayPoints.length;
		if (size < 3) {
			return null;
		}
		// if we left with 3 points we return the triangle
		else if (size == 3) {
			return new DtTriangle(arrayPoints[0], arrayPoints[1], arrayPoints[2]);
		} else {
			for (int i = 0; i <= size - 1; i++) {
				DtPoint p1 = arrayPoints[i];
				int j = i + 1;
				int k = i + 2;
				if (j >= size) {
					j = 0;
					k = 1;
				}
				//check IndexOutOfBound
				else if (k >= size)
					k = 0;
				DtPoint p2 = arrayPoints[j];
				DtPoint p3 = arrayPoints[k];
				//check if the triangle is not re-entrant and not encloses p
				DtTriangle t = new DtTriangle(p1, p2, p3);
				if ((calcDet(p1, p2, p3) >= 0) && !t.contains(p, true)) {
					if (!t.fallInsideCircumcircle(arrayPoints))
						return t;
				}
				//if there are only 4 points use contains that refers to point
				//on boundary as outside
				if (size == 4 && (calcDet(p1, p2, p3) >= 0) && !t.contains(p, false)) {
					if (!t.fallInsideCircumcircle(arrayPoints))
						return t;
				}
			}
		}
		return null;
	}

	// TODO: Move this to triangle.
	//checks if the triangle is not re-entrant
	private double calcDet(DtPoint A, DtPoint B, DtPoint P) {
		return (A.getX() * (B.getY() - P.getY())) - (A.getY() * (B.getX() - P.getX())) + (B.getX() * P.getY() - B.getY() * P.getX());
	}

	public boolean contains(DtPoint p) {
		DtTriangle tt = find(p);
		return !tt.isHalfplane();
	}

	public boolean contains(double x, double y) {
		return contains(new DtPoint(x, y));
	}

	public double getZValueAt(double x, double y) {
		DtPoint q = new DtPoint(x, y);
		DtTriangle t = find(q);
		return t.getZvalueAt(q);
	}

	private void updateBoundingBox(DtPoint p) {
		if (boundingBox == null)
			boundingBox = new DtRect(p, p);
		else
			boundingBox = boundingBox.unionWith(p);
		//if (_bb_min == null) {
		//_bb_min = p;
		//_bb_max = p;
		//} else {
		//if (x < _bb_min.getX())
		//_bb_min.x = x;
		//else if (x > _bb_max.getX())
		//_bb_max.x = x;
		//if (y < _bb_min.y)
		//_bb_min.y = y;
		//else if (y > _bb_max.getY())
		//_bb_max.y = y;
		//if (z < _bb_min.z)
		//_bb_min.z = z;
		//else if (z > _bb_max.getZ())
		//_bb_max.z = z;
		//}
	}
	/**
	 * @return The bounding rectange between the minimum and maximum coordinates
	 */
	public DtRect getBoundingBox() {
		//return new DtRect(_bb_min, _bb_max);
		return boundingBox;
	}

	/**
	 * return the min point of the bounding box of this triangulation {{x0,y0,z0}}
	 */
	public DtPoint minBoundingBox() {
		//return _bb_min;
		return boundingBox.getMinPoint();
	}

	/**
	 * return the max point of the bounding box of this triangulation {{x1,y1,z1}}
	 */
	public DtPoint maxBoundingBox() {
		//return _bb_max;
		return boundingBox.getMaxPoint();
	}

	/**
	 * computes the current set (vector) of all triangles and return an iterator to them.
	 * 
	 * @return an iterator to the current set of all triangles.
	 */
	public List<DtTriangle> getTriangles() {
		if (this.size() <= 2)
			_triangles.clear();
		initTriangles();
		return _triangles;
	}

	public List<DtPoint> getConvexHullPoints() {
		List<DtPoint> ans = new ArrayList<DtPoint>();
		DtTriangle curr = this.startTriangleHull;
		boolean cont = true;
		double x0 = _bb_min.getX(), x1 = _bb_max.getX();
		double y0 = _bb_min.getY(), y1 = _bb_max.getY();
		boolean sx, sy;
		while (cont) {
			sx = curr.getPoint1().getX() == x0 || curr.getPoint1().getX() == x1;
			sy = curr.getPoint1().getY() == y0 || curr.getPoint1().getY() == y1;
			if ((sx & sy) | (!sx & !sy)) {
				ans.add(curr.getPoint1());
			}
			if (curr.bcnext != null && curr.bcnext.isHalfplane())
				curr = curr.bcnext;
			if (curr == this.startTriangleHull)
				cont = false;
		}
		return ans;
	}

	public Collection<DtPoint> getPoints() {
		return this.points;
	}

	private void initTriangles() {
		if (_modCount == _modCount2)
			return;
		if (this.size() > 2) {
			if (startTriangle == null) // happens when there are more than 2 points and they are all collinear
				throw new NullPointerException("StartTriangle");
			_modCount2 = _modCount;
			List<DtTriangle> front = new ArrayList<DtTriangle>();
			_triangles.clear();
			front.add(this.startTriangle);
			while (front.size() > 0) {
				DtTriangle t = front.remove(0);
				if (t._mark == false) {
					t._mark = true;
					_triangles.add(t);
					if (t.abnext != null && !t.abnext._mark) {
						front.add(t.abnext);
					}
					if (t.bcnext != null && !t.bcnext._mark) {
						front.add(t.bcnext);
					}
					if (t.canext != null && !t.canext._mark) {
						front.add(t.canext);
					}
				}
			}
			// _triNum = _triangles.size();
			for (int i = 0; i < _triangles.size(); i++) {
				_triangles.get(i)._mark = false;
			}
		}
	}

	public void IndexData(int xCellCount, int yCellCount) {
		gridIndex = new DtGrid(this, xCellCount, yCellCount, getBoundingBox());
	}

	public void clearIndex() {
		gridIndex = null;
	}

	//	public static void main(String a[]) {
	//		DtPoint[] ps = new DtPoint[] { new DtPoint(5, 5, 5), new DtPoint(6, 6, 5), new DtPoint(5, 6, 5), new DtPoint(8, 2, 5) };
	//		DtTriangulation t = new DtTriangulation(ps);
	//		for (DtTriangle tri : t.getTriangles()) {
	//			System.out.println(tri);
	//		}
	//
	//	}

	public boolean allCollinear() {
		return allCollinear;
	}
}
