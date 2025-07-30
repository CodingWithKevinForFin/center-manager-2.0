package com.f1.website;

import java.util.List;
import java.util.logging.Logger;

import com.f1.base.Clock;
import com.f1.base.Table;
import com.f1.utils.AH;
import com.f1.utils.LH;
import com.f1.utils.LocaleFormatter;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.encrypt.ScryptUtils;
import com.f1.utils.structs.Tuple2;

public class WebsiteUser {
	private static final Logger log = LH.get();

	public static final String NEW = "NEW";
	public static final String VERIFIED = "VERIFIED";
	public static final String STARTED_TRIAL = "STARTED_TRIAL";
	public static final String ENTERPRISE = "ENTERPRISE";
	public static final String ADMIN = "ADMIN";
	public static final String ENDED_TRIAL = "ENDED_TRIAL";
	public static final byte STATUS_NEW = 101;
	public static final byte STATUS_VERIFIED = 102;
	public static final byte STATUS_STARTED_TRIAL = 103;
	public static final byte STATUS_ENDED_TRIAL = 104;
	public static final byte STATUS_ENTERPRISE = 105;
	public static final byte STATUS_ADMIN = 106;
	private static final long NULL_TIME = -1L;

	private Tuple2<String, byte[]>[] tmpAttachments = new Tuple2[3];

	final private TfWebsiteUser user;
	final private LocaleFormatter formatter;
	private String homeDirectory;
	private Table files;
	private Table licenses;

	final private List<String> licenseApps;
	final private List<String> licenseInstances;
	final private Clock clock;

	private String fileSorting;

	private String selectedLicense;

	public WebsiteUser(TfWebsiteUser user, Clock now, LocaleFormatter formatter) {
		this.user = user;
		this.clock = now;
		this.formatter = formatter;
		this.licenseApps = SH.splitToList(",", user.getLicenseApps());
		this.licenseInstances = SH.splitToList(",", user.getlicenseInstances());
	}

	public List<String> getLicenseApps() {
		return licenseApps;
	}

	public List<String> getLicenseInstances() {
		return licenseInstances;
	}

	public int getLicenseDaysLength() {
		return user.getLicenseDaysLength();
	}
	public int getLicenseExpiresDate() {
		return user.getLicenseExpiresDate();
	}

	public String getUserName() {
		return user.getUsername();
	}

	//	public void setHomeDirectory(String homeDirectory) {
	//		this.homeDirectory = homeDirectory;
	//	}

	public String getHomeDirectory() {
		return getUserName();
	}

	public boolean getEnabled() {
		return this.user.getEnabled();
	}

	public LocaleFormatter getFormatter() {
		return formatter;
	}

	public void setFiles(Table t) {
		this.files = t;
	}

	public Table getFiles() {
		return files;
	}

	public String getFirstName() {
		return this.user.getFirstName();
	}

	public String getLastName() {
		return this.user.getLastName();
	}

	public String getCompany() {
		return this.user.getCompany();
	}

	public String getPhone() {
		return this.user.getPhone();
	}

	public String getEmail() {
		return this.user.getEmail();
	}

	public void setLicenses(Table licenses) {
		this.licenses = licenses;
	}

	public Table getLicenses() {
		return licenses;
	}

	public int getStatus() {
		return this.user.getStatus();
	}
	public String getStatusText() {
		if (this.user.getStatus() == STATUS_STARTED_TRIAL && getTrialExpiresIn() == 0L)
			return ENDED_TRIAL;
		return toStatusString(this.user.getStatus());
	}

	public static String toStatusString(byte type) {
		switch (type) {
			case STATUS_NEW:
				return NEW;
			case STATUS_VERIFIED:
				return VERIFIED;
			case STATUS_STARTED_TRIAL:
				return STARTED_TRIAL;
			case STATUS_ENDED_TRIAL:
				return ENDED_TRIAL;
			case STATUS_ENTERPRISE:
				return ENTERPRISE;
			case STATUS_ADMIN:
				return ADMIN;
			default:
				throw new RuntimeException("Unknown: " + SH.toString(type));
		}
	}
	public static int fromStatusString(String type) {
		if (NEW.equals(type))
			return STATUS_NEW;
		else if (VERIFIED.equals(type))
			return STATUS_VERIFIED;
		else if (STARTED_TRIAL.equals(type))
			return STATUS_STARTED_TRIAL;
		else if (ENDED_TRIAL.equals(type))
			return STATUS_ENDED_TRIAL;
		else if (ENTERPRISE.equals(type))
			return STATUS_ENTERPRISE;
		else if (ADMIN.equals(type))
			return STATUS_ADMIN;
		throw new RuntimeException("Unknown: " + type);
	}

	public long getTrialExpiresOn() {
		return this.user.getTrialExpiresOn();
	}
	public long getTrialExpiresIn() {
		long trialExpiresOn = getTrialExpiresOn();
		if (trialExpiresOn == NULL_TIME)
			return NULL_TIME;
		return Math.max(0L, trialExpiresOn - clock.getNow());
	}

	public Clock getClock() {
		return this.clock;
	}

	public boolean matchesPassword(String password) {
		try {
			return ScryptUtils.check(password, this.user.getPassword());
		} catch (Exception e) {
			LH.info(log, "Error Verifying password for user ", getId(), ", username='", getUserName(), "'");
			return false;
		}
	}

	public long getId() {
		return user.getId();
	}

	public TfWebsiteUser getUser() {
		return this.user;
	}
	public String getRole() {
		return this.user.getRole();
	}

	public String getIntendedUse() {
		return this.user.getIntendedUse();
	}

	public String formatDateTime(long expires) {
		return getFormatter().getDateFormatter(LocaleFormatter.MMDDYYYY_HHMMSSA).format(expires);
	}

	public void setFileSorting(String sorting) {
		this.fileSorting = sorting;
	}
	public String getFileSorting() {
		return this.fileSorting;
	}

	public void putTempAttachment(int pos, String fileName, byte[] data) {
		OH.assertBetween(pos, 0, 2);
		this.tmpAttachments[pos] = new Tuple2<String, byte[]>(fileName, data);
	}
	public void removeTempAttachment(int pos) {
		OH.assertBetween(pos, 0, 2);
		this.tmpAttachments[pos] = null;

	}
	public Tuple2<String, byte[]>[] getTempAttachments() {
		return AH.removeAll(this.tmpAttachments, null);
	}

	public String getClearTempAttachments() {
		AH.fill(tmpAttachments, null);
		return "";
	}

	public void setSelectedLicense(String selectedLicense) {
		this.selectedLicense = selectedLicense;
	}
	public String getSelectedLicense() {
		return this.selectedLicense;
	}
	public String getClearSelectedLicense() {
		this.selectedLicense = null;
		return "";
	}
}
