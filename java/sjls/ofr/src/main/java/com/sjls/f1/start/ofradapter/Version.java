package com.sjls.f1.start.ofradapter;

public class Version {
    public final static String CVS_ID = "$Id: Version.java,v 1.3 2014/12/01 23:26:20 olu Exp $";
    
    /** This will change for every new version */
    public final static String ENGINE_VERSION = "4.10 (20141201)";
    public final static String changeReason = "Added Login and Error History";        

    public static String getVersion() {
        return String.format("Version [%s] ChangeReason=[%s] CVS_ID=[%s]", ENGINE_VERSION, changeReason, CVS_ID);
    }
}
