package quickfix;

import java.util.List;

public class GroupAccess {
	public static Group getFirstGroup(final Message msg, int groupTag) {
		List<Group> groups = msg.getGroups(groupTag);
		if (null != groups) {
			return groups.get(0);
		}
		return null;
	}

	public static int[] getFieldOrder(final Message msg, int groupTag) {
		List<Group> groups = msg.getGroups(groupTag);
		if (null != groups) {
			return groups.get(0).getFieldOrder();
		}
		return new int[0];
	}

}
