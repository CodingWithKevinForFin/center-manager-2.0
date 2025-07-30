package com.f1.pofo.refdata;

import com.f1.base.Message;
import com.f1.base.VID;

/**
 * the fundamentals for a particular security
 * 
 */
@VID("F1.RD.FU")
public interface Fundamentals extends Message {

	/**
	 * @return the unique id representing these fundamentals
	 */
	public int getFundamentalsId();

	public void setFundamentalsId(int fundamentalId);

	/**
	 * @return the historical volume
	 */
	public double getHistoricalVolume();

	public void setHistoricalVolume(double thirtyDayAverageVolume);

	/**
	 * @return the price to earning ratio (1.0 indicates 1 to 1 ratio)
	 */
	public double getTrailingPriceToEarnings();

	public void setTrailingPriceToEarnings(double trailingPriceToErnings);

	/**
	 * 
	 * @return the forward price to earnings ratio (1.0 indicates a 1 to 1
	 *         ratio)
	 */
	public double getForwardPriceToEarnings();

	public void setForwardPriceToEarnings(double forwardPriceToEarnings);

	/**
	 * 
	 * @return the peg ratio (1.0 indicates a 1 to 1 ratio)
	 */
	public double getPegRatio();

	public void setPegRatio(double pegRatio);

	/**
	 * 
	 * @return the price to sales ratio (1.0 indicates a 1 to 1 ratio)
	 */
	public double getPriceToSales();

	public void setPriceToSales(double priceToSales);

	/**
	 * @return the price to book ratio (1.0 indicates a 1 to 1 ratio)
	 */
	public double getPriceToBook();

	public void setPriceToBook(double priceToBook);

	/**
	 * @return full name of the company
	 */
	public String getCompanyName();

	public void setCompanyName(String companyName);

	/**
	 * @return sector of the company
	 */
	public String getSector();

	public void setSector(String sector);

	/**
	 * @return primary industry group
	 */
	public String getIndustryGroup();

	public void setIndustryGroup(String industryGroup);

	/**
	 * @return primary industry
	 */
	public String getIndustry();

	public void setIndustry(String industry);

	/**
	 * @return subindustry
	 */
	public String getSubIndustry();

	public void setSubIndustry(String subIndustry);

	/**
	 * @return number of shares outstanding
	 */
	public void setSharesOutstanding(long sharesOutstanding);

	public long getSharesOutstanding();

}
