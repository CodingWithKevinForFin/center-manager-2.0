package com.larkinpoint.salestool;

import com.f1.base.Day;

public class LarkinParameters {
	private String Title;
	private String SymbolName;
	private Day StartingQuoteDate;
	private Day EndingQuoteDate;
	private int StraddleChoice;

	private float UnderlyingValue;
	private double InvestmentValue;
	private float InvestmentPercentage;
	private double InvestedAmount;
	private float ManagementFee;

	private int straddleDaysUB;
	private int straddleDaysLB;
	private float straddleRatioUB;
	private float straddleRatioLB;
	private float straddleRatio;
	private float straddleCount;

	private int putBucket1DaysUB;
	private int putBucket1DaysLB;
	private float putBucket1cpRatio;
	private float putBucket1cpRatioUB;
	private float putBucket1cpRatioLB;
	private float putBucket1Count;

	private int putBucket2DaysLB;
	private int putBucket2DaysUB;
	private float putBucket2cpRatio;
	private float putBucket2cpRatioUB;
	private float putBucket2cpRatioLB;
	private float putBucket2Count;

	public LarkinParameters() {

		this.StraddleChoice = 0;

		this.UnderlyingValue = (float) 0.0;
		this.InvestmentValue = 0.0;
		this.InvestmentPercentage = (float) 0.0;
		this.InvestedAmount = 0.0;
		this.ManagementFee = (float) 0.0;

		this.straddleDaysUB = 0;
		this.straddleDaysLB = 0;
		this.straddleRatioUB = (float) 0.0;
		this.straddleRatioLB = (float) 0.0;
		this.straddleRatio = (float) 0.0;
		this.straddleCount = (float) 0.0;

		this.putBucket1DaysUB = 0;
		this.putBucket1DaysLB = 0;
		this.putBucket1cpRatio = (float) 0.0;
		this.putBucket1cpRatioUB = (float) 0.0;
		this.putBucket1cpRatioLB = (float) 0.0;
		this.putBucket1Count = (float) 0.0;

		this.putBucket2DaysLB = 0;
		this.putBucket2DaysUB = 0;
		this.putBucket2cpRatio = (float) 0.0;
		this.putBucket2cpRatioUB = (float) 0.0;
		this.putBucket2cpRatioLB = (float) 0.0;
		this.putBucket2Count = (float) 0.0;
	}
	public LarkinParameters(LarkinParameters params) {
		this.SymbolName = new String(params.getSymbolName());
		this.StraddleChoice = params.StraddleChoice;
		this.StartingQuoteDate = params.StartingQuoteDate;
		this.EndingQuoteDate = params.EndingQuoteDate;
		this.UnderlyingValue = params.UnderlyingValue;
		this.InvestmentValue = params.InvestmentValue;
		this.InvestmentPercentage = params.InvestmentPercentage;
		this.InvestedAmount = params.InvestedAmount;
		this.ManagementFee = params.ManagementFee;

		this.straddleDaysUB = params.straddleDaysUB;
		this.straddleDaysLB = params.straddleDaysLB;
		this.straddleRatioUB = params.straddleRatioUB;
		this.straddleRatioLB = params.straddleRatioLB;
		this.straddleRatio = params.straddleRatio;
		this.straddleCount = params.straddleCount;

		this.putBucket1DaysUB = params.putBucket1DaysUB;
		this.putBucket1DaysLB = params.putBucket1DaysLB;
		this.putBucket1cpRatio = params.putBucket1cpRatio;
		this.putBucket1cpRatioUB = params.putBucket1cpRatioUB;
		this.putBucket1cpRatioLB = params.putBucket1cpRatioLB;
		this.putBucket1Count = params.putBucket1Count;

		this.putBucket2DaysLB = params.putBucket2DaysLB;
		this.putBucket2DaysUB = params.putBucket2DaysUB;
		this.putBucket2cpRatio = params.putBucket2cpRatio;
		this.putBucket2cpRatioUB = params.putBucket2cpRatioUB;
		this.putBucket2cpRatioLB = params.putBucket2cpRatioLB;
		this.putBucket2Count = params.putBucket2Count;

	}
	public LarkinParameters copy(LarkinParameters params) {

		this.StraddleChoice = params.StraddleChoice;
		this.StartingQuoteDate = params.StartingQuoteDate;
		this.EndingQuoteDate = params.EndingQuoteDate;
		this.UnderlyingValue = params.UnderlyingValue;
		this.InvestmentValue = params.InvestmentValue;
		this.InvestmentPercentage = params.InvestmentPercentage;
		this.InvestedAmount = params.InvestedAmount;
		this.ManagementFee = params.ManagementFee;

		this.straddleDaysUB = params.straddleDaysUB;
		this.straddleDaysLB = params.straddleDaysLB;
		this.straddleRatioUB = params.straddleRatioUB;
		this.straddleRatioLB = params.straddleRatioLB;
		this.straddleRatio = params.straddleRatio;
		this.straddleCount = params.straddleCount;

		this.putBucket1DaysUB = params.putBucket1DaysUB;
		this.putBucket1DaysLB = params.putBucket1DaysLB;
		this.putBucket1cpRatio = params.putBucket1cpRatio;
		this.putBucket1cpRatioUB = params.putBucket1cpRatioUB;
		this.putBucket1cpRatioLB = params.putBucket1cpRatioLB;
		this.putBucket1Count = params.putBucket1Count;

		this.putBucket2DaysLB = params.putBucket2DaysLB;
		this.putBucket2DaysUB = params.putBucket2DaysUB;
		this.putBucket2cpRatio = params.putBucket2cpRatio;
		this.putBucket2cpRatioUB = params.putBucket2cpRatioUB;
		this.putBucket2cpRatioLB = params.putBucket2cpRatioLB;
		this.putBucket2Count = params.putBucket2Count;
		return this;
	}
	public float getPutBucket2Count() {
		return putBucket2Count;
	}

	public String getSymbolName() {
		return SymbolName;
	}

	public void setSymbolName(String symbolName) {
		SymbolName = symbolName;
	}

	public Day getStartingQuoteDate() {
		return StartingQuoteDate;
	}

	public void setStartingQuoteDate(Day startingQuoteDate) {
		StartingQuoteDate = startingQuoteDate;
	}

	public int getStraddleChoice() {
		return StraddleChoice;
	}

	public void setStraddleChoice(int straddleChoice) {
		StraddleChoice = straddleChoice;
	}

	public float getUnderlyingValue() {
		return UnderlyingValue;
	}

	public void setUnderlyingValue(float underlyingValue) {
		UnderlyingValue = underlyingValue;
	}

	public double getInvestmentValue() {
		return InvestmentValue;
	}

	public void setInvestmentValue(double investmentValue) {
		InvestmentValue = investmentValue;
	}

	public float getInvestmentPercentage() {
		return InvestmentPercentage;
	}

	public void setInvestmentPercentage(float investmentPercentage) {
		InvestmentPercentage = investmentPercentage;
	}

	public double getInvestedAmount() {
		return InvestedAmount;
	}

	public void setInvestedAmount(double investedAmount) {
		InvestedAmount = investedAmount;
	}

	public float getManagementFee() {
		return ManagementFee;
	}

	public void setManagementFee(float managementFee) {
		ManagementFee = managementFee;
	}

	public int getStraddleDaysUB() {
		return straddleDaysUB;
	}

	public void setStraddleDaysUB(int straddleDaysUB) {
		this.straddleDaysUB = straddleDaysUB;
	}

	public int getStraddleDaysLB() {
		return straddleDaysLB;
	}

	public void setStraddleDaysLB(int straddleDaysLB) {
		this.straddleDaysLB = straddleDaysLB;
	}

	public float getStraddleRatioUB() {
		return straddleRatioUB;
	}

	public void setStraddleRatioUB(float straddleRatioUB) {
		this.straddleRatioUB = straddleRatioUB;
	}

	public float getStraddleRatioLB() {
		return straddleRatioLB;
	}

	public void setStraddleRatioLB(float straddleRatioLB) {
		this.straddleRatioLB = straddleRatioLB;
	}

	public float getStraddleRatio() {
		return straddleRatio;
	}

	public void setStraddleRatio(float straddleRatio) {
		this.straddleRatio = straddleRatio;
	}

	public float getStraddleCount() {
		return straddleCount;
	}

	public void setStraddleCount(float straddleCount) {
		this.straddleCount = straddleCount;
	}

	public int getPutBucket1DaysUB() {
		return putBucket1DaysUB;
	}

	public void setPutBucket1DaysUB(int putBucket1DaysUB) {
		this.putBucket1DaysUB = putBucket1DaysUB;
	}

	public int getPutBucket1DaysLB() {
		return putBucket1DaysLB;
	}

	public void setPutBucket1DaysLB(int putBucket1DaysLB) {
		this.putBucket1DaysLB = putBucket1DaysLB;
	}

	public float getPutBucket1cpRatio() {
		return putBucket1cpRatio;
	}

	public void setPutBucket1cpRatio(float putBucket1cpRatio) {
		this.putBucket1cpRatio = putBucket1cpRatio;
	}

	public float getPutBucket1cpRatioUB() {
		return putBucket1cpRatioUB;
	}

	public void setPutBucket1cpRatioUB(float putBucket1cpRatioUB) {
		this.putBucket1cpRatioUB = putBucket1cpRatioUB;
	}

	public float getPutBucket1cpRatioLB() {
		return putBucket1cpRatioLB;
	}

	public void setPutBucket1cpRatioLB(float putBucket1cpRatioLB) {
		this.putBucket1cpRatioLB = putBucket1cpRatioLB;
	}

	public float getPutBucket1Count() {
		return putBucket1Count;
	}

	public void setPutBucket1Count(float putBucket1Count) {
		this.putBucket1Count = putBucket1Count;
	}

	public int getPutBucket2DaysLB() {
		return putBucket2DaysLB;
	}

	public void setPutBucket2DaysLB(int putBucket2DaysLB) {
		this.putBucket2DaysLB = putBucket2DaysLB;
	}

	public int getPutBucket2DaysUB() {
		return putBucket2DaysUB;
	}

	public void setPutBucket2DaysUB(int putBucket2DaysUB) {
		this.putBucket2DaysUB = putBucket2DaysUB;
	}

	public float getPutBucket2cpRatio() {
		return putBucket2cpRatio;
	}

	public void setPutBucket2cpRatio(float putBucket2cpRatio) {
		this.putBucket2cpRatio = putBucket2cpRatio;
	}

	public float getPutBucket2cpRatioUB() {
		return putBucket2cpRatioUB;
	}

	public void setPutBucket2cpRatioUB(float putBucket2cpRatioUB) {
		this.putBucket2cpRatioUB = putBucket2cpRatioUB;
	}

	public float getPutBucket2cpRatioLB() {
		return putBucket2cpRatioLB;
	}

	public void setPutBucket2cpRatioLB(float putBucket2cpRatioLB) {
		this.putBucket2cpRatioLB = putBucket2cpRatioLB;
	}

	public void setPutBucket2Count(float putBucket2Count) {
		this.putBucket2Count = putBucket2Count;
	}
	public String getTitle() {
		return Title;
	}
	public void setTitle(String title) {
		Title = title;
	}
	public Day getEndingQuoteDate() {
		return EndingQuoteDate;
	}
	public void setEndingQuoteDate(Day endingQuoteDate) {
		EndingQuoteDate = endingQuoteDate;
	}

}
