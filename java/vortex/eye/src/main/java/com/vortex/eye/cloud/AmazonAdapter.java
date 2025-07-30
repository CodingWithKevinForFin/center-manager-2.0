package com.vortex.eye.cloud;

import java.net.URL;
import java.net.URLEncoder;
import java.security.SignatureException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import ch.ethz.ssh2.crypto.Base64;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.GroupIdentifier;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceNetworkInterface;
import com.amazonaws.services.ec2.model.InstanceNetworkInterfaceSpecification;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.f1.base.Getter;
import com.f1.container.impl.AbstractContainerScope;
import com.f1.utils.CH;
import com.f1.utils.IOH;
import com.f1.utils.xml.XmlElement;
import com.f1.utils.xml.XmlParser;
import com.f1.vortexcommon.msg.eye.VortexEyeCloudInterface;
import com.f1.vortexcommon.msg.eye.VortextEyeCloudMachineInfo;

public class AmazonAdapter implements CloudAdapter {

	public static void main(String a[]) throws Exception {
		String secretAccessKey = "/sqV2wwWXLxNL60Zn5j9ar6NvDzIUsMeMw1kjsjG";
		String endpoint = "ec2.us-east-1.amazonaws.com";
		String accessId = "AKIAIZKTZ26KJD67AXYA";
		System.out.println(new AmazonAdapter().getInterfaces(endpoint, accessId, secretAccessKey));
	}

	public List<String> getInterfaces(String endpoint, String accessId, String secretAccessKey) throws Exception {
		XmlElement xml = get("DescribeInstances", endpoint, accessId, secretAccessKey);
		XmlElement rs = xml.getFirstElement("reservationSet");
		List<String> ips = new ArrayList<String>();
		for (XmlElement item : rs.getElements("item")) {
			XmlElement is = item.getFirstElement("instancesSet");
			for (XmlElement item2 : is.getElements("item")) {
				String ip = item2.getFirstElement("ipAddress").getInnerAsString();
				ips.add(ip);
			}
		}
		return ips;
	}
	private XmlElement get(String action, String endpoint, String accessKeyId, String secretAccessKey) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sszzz");//TODO:optimize
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		StringBuilder sb = new StringBuilder();
		sb.append("AWSAccessKeyId=");
		sb.append(accessKeyId);
		sb.append("&Action=");
		sb.append(action);
		sb.append("&SignatureMethod=HmacSHA256");
		sb.append("&SignatureVersion=2");
		sb.append("&Timestamp=");
		sb.append(URLEncoder.encode(sdf.format(new Date())));
		sb.append("&Version=2013-02-01");
		String signature = toSignature(endpoint, sb.toString(), secretAccessKey);
		String url = "https://" + endpoint + "/?" + sb + "&Signature=" + signature;
		byte[] data = IOH.download(new URL(url));
		return new XmlParser().parseDocument(new String(data));
	}

	private static String toSignature(String endpoint, String params, String key) throws SignatureException {
		return calculateRFC2104HMAC("GET\n" + endpoint + "\n/\n" + params, key);
	}

	private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";

	private static String calculateRFC2104HMAC(String data, String key) throws java.security.SignatureException {
		try {
			SecretKeySpec signingKey = new SecretKeySpec(key.getBytes("UTF8"), HMAC_SHA256_ALGORITHM);
			Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
			mac.init(signingKey);
			byte[] rawHmac = mac.doFinal(data.getBytes("UTF8"));
			String before;
			String after = URLEncoder.encode(before = new String(Base64.encode(rawHmac)));
			return after;
		} catch (Exception e) {
			throw new SignatureException("Failed to generate HMAC : " + e.getMessage());
		}
	}

	private AmazonEC2Client connect(VortexEyeCloudInterface ci) throws Exception {
		Map<String, String> param = ci.getParameters();
		AmazonEC2Client c = new AmazonEC2Client(new BasicAWSCredentials(param.get("accessid"), param.get("accesskey")));
		c.setEndpoint(param.get("endpoint"));

		return c;
	}

	@Override
	public List<String> getMachinesInCloud(VortexEyeCloudInterface ci) throws Exception {
		AmazonEC2Client c = connect(ci);

		DescribeInstancesResult ir = c.describeInstances();

		List<String> r = new LinkedList<String>();
		for (Reservation res : ir.getReservations()) {
			for (Instance i : res.getInstances()) {
				if (i.getPublicIpAddress() != null)
					r.add(i.getPublicIpAddress());
			}
		}

		return r;

		//		return getInterfaces(param.get("endpoint"), param.get("accessid"), param.get("accesskey"));
	}

	@Override
	public List<VortextEyeCloudMachineInfo> getMachineInfoList(AbstractContainerScope scope, VortexEyeCloudInterface ci) throws Exception {
		AmazonEC2Client c = connect(ci);

		DescribeInstancesResult ir = c.describeInstances();

		List<VortextEyeCloudMachineInfo> r = new LinkedList<VortextEyeCloudMachineInfo>();
		for (Reservation res : ir.getReservations()) {
			for (Instance i : res.getInstances()) {
				r.add(getMI(scope, ci, i));
			}
		}

		return r;
	}

	private static final Getter<Tag, String> tagKeyGetter = new Getter<Tag, String>() {
		@Override
		public String get(Tag tag) {
			return tag.getKey();
		}
	};
	private static final Getter<Tag, String> tagValueGetter = new Getter<Tag, String>() {
		@Override
		public String get(Tag tag) {
			return tag.getValue();
		}
	};

	private VortextEyeCloudMachineInfo getMI(AbstractContainerScope scope, VortexEyeCloudInterface ci, Instance i) {
		VortextEyeCloudMachineInfo mi = scope.nw(VortextEyeCloudMachineInfo.class);
		mi.setCIId(ci.getId());
		mi.setCIName(ci.getDescription());
		mi.setInstanceId(i.getInstanceId());
		mi.setInstanceType(i.getInstanceType());
		mi.setCreateTime(i.getLaunchTime().getTime());
		mi.setKeyName(i.getKeyName());
		mi.setPrivateIP(i.getPrivateIpAddress());
		mi.setPublicIP(i.getPublicIpAddress());
		mi.setStatus(i.getState().getName());
		mi.setOS(i.getPlatform());
		Map<String, String> tm = CH.m(i.getTags(), tagKeyGetter, tagValueGetter);
		mi.setName(tm.get("Name"));
		mi.setAsOf(scope.getTools().getNow());

		return mi;
	}

	@Override
	public void stopMachine(VortextEyeCloudMachineInfo mi, VortexEyeCloudInterface ci) throws Exception {
		AmazonEC2Client c = connect(ci);
		c.stopInstances(new StopInstancesRequest(CH.l(mi.getInstanceId())));
	}

	@Override
	public void startMachine(VortextEyeCloudMachineInfo mi, VortexEyeCloudInterface ci) throws Exception {
		AmazonEC2Client c = connect(ci);
		StartInstancesRequest req = new StartInstancesRequest(CH.l(mi.getInstanceId()));
		c.startInstances(req);
	}

	private static final Getter<GroupIdentifier, String> groupIdGetter = new Getter<GroupIdentifier, String>() {
		@Override
		public String get(GroupIdentifier key) {
			return key.getGroupId();
		}
	};

	private static final Getter<Instance, String> idGetter = new Getter<Instance, String>() {
		@Override
		public String get(Instance key) {
			return key.getInstanceId();
		}
	};

	private static final Getter<InstanceNetworkInterface, InstanceNetworkInterfaceSpecification> netInterfaceGetter = new Getter<InstanceNetworkInterface, InstanceNetworkInterfaceSpecification>() {

		@Override
		public InstanceNetworkInterfaceSpecification get(InstanceNetworkInterface key) {
			return new InstanceNetworkInterfaceSpecification().withSubnetId(key.getSubnetId()).withAssociatePublicIpAddress(true);
		}
	};

	@Override
	public void startMoreLikeThis(VortextEyeCloudMachineInfo mi, VortexEyeCloudInterface ci, String name, int numberOfInstances) throws Exception {
		AmazonEC2Client c = connect(ci);
		DescribeInstancesResult r = c.describeInstances(new DescribeInstancesRequest().withInstanceIds(mi.getInstanceId()));
		if (CH.isntEmpty(r.getReservations()) && CH.isntEmpty(r.getReservations().get(0).getInstances())) {
			Instance inst = r.getReservations().get(0).getInstances().get(0);
			RunInstancesRequest rir = new RunInstancesRequest().withImageId(inst.getImageId()).withInstanceType(inst.getInstanceType()).withKeyName(inst.getKeyName())
					.withSecurityGroupIds(CH.l(inst.getSecurityGroups(), groupIdGetter)).withMinCount(numberOfInstances).withMaxCount(numberOfInstances)
					.withSubnetId(inst.getSubnetId());
			//					.withNetworkInterfaces(CH.l(inst.getNetworkInterfaces(), netInterfaceGetter));

			RunInstancesResult result = c.runInstances(rir);

			//set tags
			for (int i = 0; i < result.getReservation().getInstances().size(); i++)
				c.createTags(new CreateTagsRequest().withTags(CH.l(CH.l(inst.getTags(), new Tag("Name", i == 0 ? name : name + "_" + i)))).withResources(
						result.getReservation().getInstances().get(i).getInstanceId()));
		} else {
			throw new Exception("Couldn't find amazon instance to clone");
		}
	}

	@Override
	public void terminateMachine(VortextEyeCloudMachineInfo mi, VortexEyeCloudInterface ci) throws Exception {
		AmazonEC2Client c = connect(ci);
		c.terminateInstances(new TerminateInstancesRequest().withInstanceIds(mi.getInstanceId()));
	}
}
