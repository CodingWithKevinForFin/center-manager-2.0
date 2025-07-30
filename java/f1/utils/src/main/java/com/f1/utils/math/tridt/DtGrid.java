package com.f1.utils.math.tridt;

import java.util.Collection;

public class DtGrid {

	final private DtTriangulation triangulation;
	private double xSize;
	private double ySize;
	private DtRect indexRegion;
	private DtTriangle[][] grid;

	public DtGrid(DtTriangulation triangluation, int xCellCount, int yCellCount, DtRect region) {
		triangulation = triangluation;
		init(xCellCount, yCellCount, region);
	}

	private void init(int xCellCount, int yCellCount, DtRect region) {
		indexRegion = region;
		xSize = region.getWidth() / yCellCount;
		ySize = region.getHeight() / xCellCount;
		grid = new DtTriangle[xCellCount][yCellCount];

		DtTriangle colStartTriangle = triangulation.find(middleOfCell(0, 0));
		updateCellValues(0, 0, xCellCount - 1, yCellCount - 1, colStartTriangle);
	}

	public DtTriangle findCellTriangleOf(DtPoint point) {
		int x_index = (int) ((point.getX() - indexRegion.getMinX()) / xSize);
		int y_index = (int) ((point.getY() - indexRegion.getMinY()) / ySize);
		return grid[x_index][y_index];
	}

	public void updateIndex(Collection<DtTriangle> updatedTriangles) {

		// Gather the bounding box of the updated area
		DtRect updatedRegion = DtRect.NULL;

		for (DtTriangle tri : updatedTriangles)
			updatedRegion = updatedRegion.unionWith(tri.getBoundingBox());

		if (!updatedRegion.isNull()) {

			if (!indexRegion.contains(updatedRegion)) {
				init((int) (indexRegion.getWidth() / xSize), (int) (indexRegion.getHeight() / ySize), indexRegion.unionWith(updatedRegion));
			} else {
				// Find the cell region to be updated
				int minInvalidCellX = getCellX(updatedRegion.getMinX());
				int minInvalidCellY = getCellY(updatedRegion.getMinY());
				int maxInvalidCellX = getCellX(updatedRegion.getMaxX());
				int maxInvalidCellY = getCellY(updatedRegion.getMaxY());

				// And update it with fresh triangles
				DtTriangle adjacentValidTriangle = findValidTriangle(minInvalidCellX, minInvalidCellY);
				updateCellValues(minInvalidCellX, minInvalidCellY, maxInvalidCellX, maxInvalidCellY, adjacentValidTriangle);
			}
		}
	}

	private void updateCellValues(int startXCell, int startYCell, int lastXCell, int lastYCell, DtTriangle startTriangle) {
		// Go over each grid cell and locate a triangle in it to be the cell's
		// starting search triangle. Since we only pass between adjacent cells
		// we can search from the last triangle found and not from the start.

		// Add triangles for each column cells
		for (int i = startXCell; i <= lastXCell; i++) {
			// Find a triangle at the begining of the current column
			startTriangle = triangulation.find(middleOfCell(i, startYCell), startTriangle);
			grid[i][startYCell] = startTriangle;
			DtTriangle prevRowTriangle = startTriangle;

			// Add triangles for the next row cells
			for (int j = startYCell + 1; j <= lastYCell; j++) {
				grid[i][j] = triangulation.find(middleOfCell(i, j), prevRowTriangle);
				prevRowTriangle = grid[i][j];
			}
		}
	}

	private DtTriangle findValidTriangle(int x, int y) {
		if (x == 0 && y == 0)
			return triangulation.find(middleOfCell(x, y), null);
		else
			return grid[Math.min(0, x)][Math.min(0, y)];
	}

	private int getCellX(double x) {
		return (int) ((x - indexRegion.getMinX()) / xSize);
	}
	private int getCellY(double y) {
		return (int) ((y - indexRegion.getMinY()) / ySize);
	}

	private DtPoint middleOfCell(int xInd, int yInd) {
		final double x = indexRegion.getMinX() + xInd * xSize + xSize / 2;
		final double y = indexRegion.getMinY() + yInd * ySize + ySize / 2;
		return new DtPoint(x, y);
	}
}
