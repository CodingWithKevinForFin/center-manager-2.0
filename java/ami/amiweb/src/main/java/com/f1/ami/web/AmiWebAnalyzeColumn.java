package com.f1.ami.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.f1.utils.CH;
import com.f1.utils.SH;

public class AmiWebAnalyzeColumn {

	private List<Comparable> colList = new ArrayList<Comparable>();
	private List<Double> numberList = new ArrayList<Double>();

	public void addValue(Comparable value) {
		colList.add(value);
		if (value instanceof Number) {
			numberList.add(((Number) value).doubleValue());
		}
	}

	public List<Double> getNumberList() {
		return numberList;
	}

	public List<Comparable> getColList() {
		return colList;
	}

	public Double getSum() {
		if (this.numberList.size() == 0)
			return null;
		else {
			Double sum = 0.0;
			for (int i = 0; i < this.numberList.size(); i++) {
				sum = sum + this.numberList.get(i).doubleValue();
			}
			return sum;
		}
	}

	public Comparable getMin() {
		if (this.numberList.size() == 0) {
			int pos = CH.minIndex(this.colList, SH.COMPARATOR_CASEINSENSITIVE);
			return pos == -1 ? null : this.colList.get(pos);
		} else {
			Double min = Double.MAX_VALUE;
			for (int i = 0; i < this.numberList.size(); i++) {
				double element = this.numberList.get(i).doubleValue();
				if (element < min)
					min = element;
			}
			return min;
		}
	}

	public Comparable getMax() {
		if (this.numberList.size() == 0) {
			int pos = CH.maxIndex(this.colList, SH.COMPARATOR_CASEINSENSITIVE);
			return pos == -1 ? null : this.colList.get(pos);
		} else {
			Double max = -Double.MAX_VALUE;
			for (int i = 0; i < this.numberList.size(); i++) {
				double element = this.numberList.get(i).doubleValue();
				if (element > max)
					max = element;
			}
			return max;
		}
	}

	public Integer getCount() {
		return colList.size();
	}

	public Double getAverage() {
		if (this.numberList.size() == 0)
			return null;
		else {
			Double sum = this.getSum();
			Double average = sum / this.numberList.size();
			return average;
		}
	}

	public Double getStdDev() {
		if (this.numberList.size() == 0)
			return null;
		else {
			// Here, standard deviation is computed 
			// as the square root of the difference 
			// between the average of the squared values
			// in numberList and the squared average 
			// of the values in numberList. 
			Double average = this.getAverage();
			Double averageSquared = average * average;

			double curElement;
			double sumOfSquares = 0.0;
			// Square each element in numberList and sum
			for (int i = 0; i < this.numberList.size(); i++) {
				curElement = this.numberList.get(i).doubleValue();
				sumOfSquares = sumOfSquares + curElement * curElement;
			}

			return Math.sqrt(sumOfSquares / this.numberList.size() - averageSquared);
		}
	}

	public Double getMedian() {
		if (this.numberList.size() == 0)
			return null;
		else {
			List<Double> sortedList = new ArrayList<Double>();
			sortedList = this.numberList;
			Collections.sort(sortedList);
			if (sortedList.size() % 2 == 0) { // If list has even number of elements
				// Note: If numberList has even number of elements, 
				// calculate median as the average of the two 
				// middle numbers. 
				Double high = sortedList.get(sortedList.size() / 2);
				Double low = sortedList.get(sortedList.size() / 2 - 1);
				double median = (low.doubleValue() + high.doubleValue()) / 2.0;
				return median;
			} else { // If list has odd number of elements
				double median = sortedList.get((sortedList.size() - 1) / 2);
				return median;
			}
		}
	}

	public Integer getDistinct() {
		Set<Comparable> colSet = new HashSet<Comparable>(this.colList);
		return colSet.size();
	}

}
