package com.f1.suite.web.portal.impl.visual;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuDivider;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.peripheral.KeyEvent;
import com.f1.suite.web.portal.impl.visual.GraphPortlet.Node;

public class GraphPortletHelper {
	public static void createArrangeNodesMenu(BasicWebMenu sink, boolean enabled) {
		BasicWebMenu arrangeM = new BasicWebMenu("Rearrange ...", enabled);
		sink.add(arrangeM);
		if (!enabled)
			return;
		arrangeM.add(new BasicWebMenuLink("Stack Horizontally", true, "ph_stackx"));
		arrangeM.add(new BasicWebMenuLink("Stack Vertically", true, "ph_stacky"));
		arrangeM.add(new BasicWebMenuLink("Distribute Horizontally", true, "ph_distributex"));
		arrangeM.add(new BasicWebMenuLink("Distribute Vertically", true, "ph_distributey"));
		arrangeM.add(new BasicWebMenuDivider());
		arrangeM.add(new BasicWebMenuLink("Align Left", true, "ph_align_left"));
		arrangeM.add(new BasicWebMenuLink("Align Center", true, "ph_align_centerx"));
		arrangeM.add(new BasicWebMenuLink("Align Right", true, "ph_align_right"));
		arrangeM.add(new BasicWebMenuDivider());
		arrangeM.add(new BasicWebMenuLink("Align Top", true, "ph_align_top"));
		arrangeM.add(new BasicWebMenuLink("Align Middle", true, "ph_align_centery"));
		arrangeM.add(new BasicWebMenuLink("Align Bottom", true, "ph_align_bottom"));
	}
	public static void onArrangeMenuItem(String id, Collection<Node> selected) {
		if (id.equals("ph_align_centerx")) {
			GraphPortletHelper.alignCenter(selected);
		} else if (id.equals("ph_align_centery")) {
			GraphPortletHelper.alignMiddle(selected);
		} else if (id.equals("ph_align_left")) {
			GraphPortletHelper.alignLeft(selected);
		} else if (id.equals("ph_align_right")) {
			GraphPortletHelper.alignRight(selected);
		} else if (id.equals("ph_align_top")) {
			GraphPortletHelper.alignTop(selected);
		} else if (id.equals("ph_align_bottom")) {
			GraphPortletHelper.alignBottom(selected);
		} else if (id.equals("ph_distributex")) {
			GraphPortletHelper.distributeX(selected);
		} else if (id.equals("ph_distributey")) {
			GraphPortletHelper.distributeY(selected);
		} else if (id.equals("ph_stackx")) {
			GraphPortletHelper.stackX(selected);
		} else if (id.equals("ph_stacky")) {
			GraphPortletHelper.stackY(selected);
		}

	}
	public static boolean userShiftNodesEvent(KeyEvent keyEvent, Collection<Node> selected) {
		int mult = keyEvent.isShiftKey() ? 20 : 1;

		if (keyEvent.getKey().equals("ArrowLeft")) {
			shiftNodes(selected, -1 * mult, 0);
			return true;
		} else if (keyEvent.getKey().equals("ArrowRight")) {
			shiftNodes(selected, 1 * mult, 0);
			return true;
		} else if (keyEvent.getKey().equals("ArrowUp")) {
			shiftNodes(selected, 0, -1 * mult);
			return true;
		} else if (keyEvent.getKey().equals("ArrowDown")) {
			shiftNodes(selected, 0, 1 * mult);
			return true;
		} else
			return false;
	}
	public static void shiftNodes(Collection<Node> selected, int xDir, int yDir) {
		for (Node node : selected) {
			int x = node.getX() + xDir;
			int y = node.getY() + yDir;
			node.setXY(x, y);
		}
	}
	public static void move(Collection<Node> selected, int x, int y) {
		for (Node n : selected) {
			n.setXY(n.getX() + x, n.getY() + y);
		}
	}
	private static int getLeftBorder(Collection<Node> selected) {
		int ret = Integer.MAX_VALUE;
		for (Node n : selected) {
			if (n.getLeft() < ret) {
				ret = n.getLeft();
			}
		}
		return ret;
	}
	private static int getTopBorder(Collection<Node> selected) {
		int ret = Integer.MAX_VALUE;
		for (Node n : selected) {
			if (n.getTop() < ret) {
				ret = n.getTop();
			}
		}
		return ret;
	}
	private static int getRightBorder(Collection<Node> selected) {
		int ret = Integer.MIN_VALUE;
		for (Node n : selected) {
			if (n.getRight() > ret) {
				ret = n.getRight();
			}
		}
		return ret;
	}
	private static int getBottomBorder(Collection<Node> selected) {
		int ret = Integer.MIN_VALUE;
		for (Node n : selected) {
			if (n.getBottom() > ret) {
				ret = n.getBottom();
			}
		}
		return ret;
	}
	public static void alignCenter(Collection<Node> selected) {
		if (selected.size() > 1) {
			int center = (getLeftBorder(selected) + getRightBorder(selected)) / 2;
			for (Node n : selected) {
				n.setX(center);
			}
		}
	}
	public static void alignLeft(Collection<Node> selected) {
		if (selected.size() > 1) {
			int left = getLeftBorder(selected);
			for (Node n : selected) {
				n.setLeft(left);
			}
		}
	}
	public static void alignRight(Collection<Node> selected) {
		if (selected.size() > 1) {
			int right = getRightBorder(selected);
			for (Node n : selected) {
				n.setLeft(right - n.getWidth());
			}
		}
	}
	public static void alignMiddle(Collection<Node> selected) {
		if (selected.size() > 1) {
			int center = (getTopBorder(selected) + getBottomBorder(selected)) / 2;
			for (Node n : selected) {
				n.setY(center);
			}
		}
	}
	public static void alignTop(Collection<Node> selected) {
		if (selected.size() > 1) {
			int top = getTopBorder(selected);
			for (Node n : selected) {
				n.setTop(top);
			}
		}
	}
	public static void alignBottom(Collection<Node> selected) {
		if (selected.size() > 1) {
			int bottom = getBottomBorder(selected);
			for (Node n : selected) {
				n.setTop(bottom - n.getHeight());
			}
		}
	}
	public static void distributeX(Collection<Node> selected) {
		if (selected.size() > 1) {
			List<Node> sorted = new ArrayList<Node>();
			int totalWidth = 0;
			for (Node n : selected) {
				sorted.add(n);
				totalWidth += n.getWidth();
			}
			Collections.sort(sorted, Node.compareLeft);
			double leftPosition = sorted.get(0).getLeft();
			double spacing = (sorted.get(sorted.size() - 1).getRight() - leftPosition - totalWidth) / (sorted.size() - 1);
			for (Node n : sorted) {
				n.setLeft((int) leftPosition);
				leftPosition += spacing + n.getWidth();
			}
		}
	}
	public static void distributeY(Collection<Node> selected) {
		if (selected.size() > 1) {
			List<Node> sorted = new ArrayList<Node>();
			int totalHeight = 0;
			for (Node n : selected) {
				sorted.add(n);
				totalHeight += n.getHeight();
			}
			Collections.sort(sorted, Node.compareTop);
			double topPosition = sorted.get(0).getTop();
			double spacing = (sorted.get(sorted.size() - 1).getBottom() - topPosition - totalHeight) / (sorted.size() - 1);
			for (Node n : sorted) {
				n.setTop((int) topPosition);
				topPosition += spacing + n.getHeight();
			}
		}
	}
	public static void stackX(Collection<Node> selected) {
		if (selected.size() > 1) {
			alignMiddle(selected);
			List<Node> sorted = new ArrayList<Node>();
			int totalWidth = 0;
			for (Node n : selected) {
				sorted.add(n);
				totalWidth += n.getWidth();
			}
			Collections.sort(sorted, Node.compareLeft);
			int leftPosition = (sorted.get(0).getLeft() + sorted.get(sorted.size() - 1).getRight() - totalWidth) / 2;
			for (Node n : sorted) {
				n.setLeft(leftPosition);
				leftPosition += n.getWidth();
			}
		}
	}
	public static void stackY(Collection<Node> selected) {
		if (selected.size() > 1) {
			alignCenter(selected);
			List<Node> sorted = new ArrayList<Node>();
			int totalHeight = 0;
			for (Node n : selected) {
				sorted.add(n);
				totalHeight += n.getHeight();
			}
			Collections.sort(sorted, Node.compareTop);
			int topPosition = (sorted.get(0).getTop() + sorted.get(sorted.size() - 1).getBottom() - totalHeight) / 2;
			for (Node n : sorted) {
				n.setTop(topPosition);
				topPosition += n.getHeight();
			}
		}
	}
}