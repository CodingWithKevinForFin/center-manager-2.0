package com.f1.utils;

import java.util.List;
import java.util.Set;

public interface Labeler {
	int OPTION_INCLUDE_ITEMLESS_LABELS = 1;
	int OPTION_REPEAT_LABELS = 2;
	int OPTION_SORT_LABELS = 4;
	int OPTION_BLANK_LINE_BETWEEN_LABELS = 8;
	int OPTION_LEFT_ALIGN = 16;
	int OPTION_HIDE_PASSWORDS = 32;

	void addDivider(String label);

	void addLabel(String label, boolean reuseIfExists);

	void addItem(String label, Object item);

	void addItem(String label, Object item, boolean appendIfLabelExists);

	String toString(String prefix, String delim, int options);

	void addItem(Object item);

	Set<String> getLabels();

	List<String> getItems(String label);

	void addItems(String label, List<String> values);

}
