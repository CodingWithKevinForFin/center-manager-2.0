package app.fix42;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.logging.Logger;

import com.sjls.algos.eo.utils.CSVFileIterator;
import com.sjls.algos.eo.utils.DirectoryIterator;

/**
 * A class that represents all venues used by ISP This is an execution Venue, e.g. INET, NOT a broker!
 * 
 * @author Olu Emuleomo, 2010-07-27
 * 
 */
public class DOPVenuesFile {
	public static String CVS_ID = "$Id: DOPVenuesFile.java,v 1.1.1.1 2011/12/06 23:26:23 olu Exp $";
	private static Logger m_logger = Logger.getLogger(DOPVenuesFile.class.getName());

	// #<Field Names>VenueID,m,q,T
	// Venue1,10000,0.4,1

	public static class VenueParams implements Comparable<VenueParams> {
		public final String venueID;

		/** Minimal number of shares accepted by this venue */
		public final int minShares;

		/** venue popularity */
		public final double popularity;

		/** Timeout for temporary orders */
		public final int tempOrderTimeout;

		public VenueParams(final String venueID, final int m, final double q, final int T) {
			this.venueID = venueID;
			this.minShares = m;
			this.popularity = q;
			this.tempOrderTimeout = T;
		}

		@Override
		public String toString() {
			return String.format("%s: m=%s, q=%s, T=%s", venueID, minShares, popularity, tempOrderTimeout);
		}

		@Override
		public int hashCode() {
			return venueID.hashCode();
		}

		/** Compare using minShares */
		public int compareTo(final VenueParams o) {
			return minShares < o.minShares ? -1 : (minShares > o.minShares ? 1 : 0);
		}

		@Override
		public boolean equals(final Object o) {
			if (o == null || !(o instanceof VenueParams))
				return false;
			return compareTo((VenueParams) o) == 0;
		}
	}

	final int NUM_FLDS = 4;
	private final HashMap<String, VenueParams> m_map = new HashMap<String, VenueParams>();
	private final VenueParams[] m_sortedArray;

	public final static String FilePrefix = "DOP_VENUES_";

	public DOPVenuesFile(final DirectoryIterator dirIter) throws IOException {
		final File file = dirIter.getLatestFile(FilePrefix);
		m_logger.info(String.format("Reading DOP Venues file [%s]", file.getAbsolutePath()));
		final CSVFileIterator csvFile = new CSVFileIterator(file);

		String flds[];
		while ((flds = csvFile.getNext()) != null) {
			if (flds.length != NUM_FLDS) {
				throw new IOException(String.format("DOP Venues file [%s] is corrupt!", file.getAbsolutePath()));
			}
			int n = 0;
			final String venueID = flds[n++];
			final int m = Integer.valueOf(flds[n++]);
			final Double q = getDouble(flds[n++]);
			final int T = Integer.valueOf(flds[n++]);
			final VenueParams v = new VenueParams(venueID, m, q, T);
			m_map.put(v.venueID, v);
			m_logger.info("Stored venue: -->" + v);
		}
		// Now lets's sort the venues desc by popularity
		final ArrayList<VenueParams> list = new ArrayList<VenueParams>();
		list.addAll(m_map.values());
		Collections.sort(list);
		Collections.reverse(list);
		m_sortedArray = list.toArray(new VenueParams[list.size()]);
	}

	private static double getDouble(final String strIn) {
		final String str = strIn.trim();
		if (str.equals("") || str.equalsIgnoreCase("NA"))
			return 0;
		return Double.valueOf(str.trim());
	}

	/**
	 * Return an array of VenueParams, sorted desc by minShares
	 * 
	 * @return
	 */
	public VenueParams[] getSortedArray() {
		return m_sortedArray;
	}

	/**
	 * Returns venue assoc with this ID or null if not found
	 * 
	 * @param venueID
	 * @return Venue
	 */
	public VenueParams getVenue(final String venueID) {
		return m_map.get(venueID);
	}
}
