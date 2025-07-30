package com.sjls.f1.sjlscommon;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class LicenseCheck
{
    public static final String CVS_ID = "$Id: LicenseCheck.java,v 1.6 2013/02/07 20:50:29 nhudson Exp $";
    private static final SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
    private static final Logger m_logger = Logger.getLogger(LicenseCheck.class);

    /**
     * Returns the number of days left for the f1 license to expire. 
     * 0 or less means the license has expired.
     * A positive value of X means the license will expire in X days
     * @return
     * @throws IOException 
     * @throws ParseException 
     */
    public static Integer checkForDaysLeftOnLicense() throws IOException, ParseException
    {
        final String licenseFile = System.getProperty("f1.license.file");
        if(licenseFile != null && licenseFile.length() > 0)
        {
            File file = new File(licenseFile);
            if(file.exists())
            {
                final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                final String line = reader.readLine();
                reader.close();
                if(line != null && line.length() > 0)
                {
                    final String[] toks = line.split("\\|");
                    final String sLicenseDate = toks[toks.length-2];
                    // System.out.println("sLicenseDate=" + sLicenseDate);
                    Calendar licenseDate = Calendar.getInstance();
                    licenseDate.setTime(df.parse(sLicenseDate));
                    Calendar nowDate = Calendar.getInstance();
                    //System.out.printf("Now=%tF,  license=%tF\n", nowDate.getTime(), licenseDate.getTime());
                    final double daysToExpire =  (licenseDate.getTimeInMillis() - nowDate.getTimeInMillis()) / 86400000.0;
                    return (int) Math.ceil( daysToExpire );
                }
            }
        }
        return null;
    }

    public static void log()
    {
        log(Level.ERROR);
    }

    public static void log(Level level)
    {
        try
        {
            final Integer retval = checkForDaysLeftOnLicense();
            if (retval == null) {
                m_logger.log(level, "Cannot determine F1LICENSE EXPIRATION...");
                System.out.println("Cannot determine F1LICENSE EXPIRATION...");
            }
            else if(retval <= 0) {
                m_logger.log(level, "F1LICENSE EXPIRED " + Math.abs(retval) + " day(s) ago!");
                System.out.println("F1LICENSE EXPIRED " + Math.abs(retval) + " day(s) ago!");
            }
            else  {
                m_logger.log(level, "F1LICENSE EXPIRES IN " + retval + " day(s)");
                System.out.println("F1LICENSE EXPIRES IN " + retval + " day(s)");
            }
        }
        catch (Exception e)
        {
            m_logger.log(level, "LicenseCheck exception " + e.getMessage());
            e.printStackTrace();
        }

    }

    public static boolean isDevMode()
    {
        boolean retval = false;
        String licenseMode = System.getProperty("f1.license.mode");
        String licenseFile = System.getProperty("f1.license.file");
        if(licenseMode != null && licenseMode.equals("dev") && licenseFile == null)
            retval = true;

        return retval;
    }

    public static void main(String[] args) throws Exception
    {
        System.setProperty("log4j", "conf/log4j.properties");
        if(System.getProperty("f1.license.file") == null) {
            System.setProperty("f1.license.file", "/START/license/f1license.txt");
        }
        log();

        m_logger.info("isDevMode=" + isDevMode());
        System.out.println("isDevMode=" + isDevMode());
    }
}
