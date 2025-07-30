package com.f1.tcartsim.preparer;

import java.util.HashMap;
import java.util.Random;

import com.f1.utils.MH;

public class TcartDistributionGenerator {

	private static final int RANDOM_RANGE = 1000000;
	private int max;
	private double avg;
	private HashMap<Integer, Integer> solutions;
	private Random rand;
	private int min;

	public TcartDistributionGenerator(int max, double avg, int min, Random r) {
		this.max = max;
		this.avg = avg;
		this.min = min;
		int total = 0;
		int next = 0;
		this.rand = r;
		this.solutions = new HashMap<Integer, Integer>();
		int i = 0;
		while (i < RANDOM_RANGE) {
			if (i == 0) {
				solutions.put(i, max);
				i++;
				total += max;
			} else {
				if (total * 1D / i > avg)
					next = min + r.nextInt((int) MH.round(avg + 1, MH.ROUND_DOWN) - min);
				else
					next = (int) (r.nextInt((int) (max + 1 - MH.round(avg, MH.ROUND_UP))) + MH.round(avg, MH.ROUND_UP));

				solutions.put(i, next);
				total += next;
				i++;
			}
		}
	}

	public int getValueFromDistribution() {
		return solutions.get(rand.nextInt(RANDOM_RANGE));
	}

}
