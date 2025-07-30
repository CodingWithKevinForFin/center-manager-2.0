package com.f1.ami.relay.fh.sqs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;
import java.nio.file.Paths;

import com.f1.ami.relay.AmiRelayIn;
import com.f1.ami.relay.fh.AmiFHBase;
import com.f1.ami.relay.fh.AmiRelayMapToBytesConverter;
import com.f1.utils.EH;
import com.f1.utils.LH;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_Character;
import com.f1.utils.casters.Caster_Double;
import com.f1.utils.casters.Caster_Float;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_Long;
import com.f1.utils.casters.Caster_Short;
import com.f1.utils.converter.json2.ObjectToJsonConverter;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider.Builder;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.profiles.ProfileFile;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.ListQueuesRequest;
import software.amazon.awssdk.services.sqs.model.ListQueuesResponse;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.SqsException;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.auth.StsAssumeRoleCredentialsProvider;
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest;

public class AmiAwsSqsFH extends AmiFHBase  {
	private static final Logger log = LH.get();

	public AmiAwsSqsFH() {
	}

	public static final String PROP_TABLE_NAME = "tableName";
	public static final String PROP_TABLE_MAPPING = "tableMapping";
	public static final String PROP_SQS_PROFILE_NAME = "profileName";
	public static final String PROP_SQS_PROFILE_FILE = "profileFile";
	public static final String PROP_SQS_AWS_ROLE_ARN = "roleArn";
	public static final String PROP_SQS_AWS_ROLE_SESSION_NAME = "roleSessionName";
	public static final String PROP_SQS_QUEUE_NAME = "queueName";
	public static final String PROP_SQS_QUEUE_URL = "queueUrl";
	public static final String PROP_SQS_QUEUE_REGION = "queueRegion";
	public static final String PROP_SQS_DELETE_AFTER_READ = "deleteAfterRead";
	public static final String PROP_SQS_READ_COUNT = "readCount";
	public static final String PROP_SQS_ACCESS_KEY = "accessKey";
	public static final String PROP_SQS_SECRET_KEY = "secretKey";
	public static final String PROP_SQS_SESSION_DURATION = "sessionDuration";
	
	private static ObjectToJsonConverter INSTANCE = new ObjectToJsonConverter();
	private Thread t;
	private Map<String,FHSetter> columnMapping = null;
	
	private String queueUrl;
	private String queueName;
	private String amiTable;
	private SqsClient sqsClient;
	private StsClient stsClient;
	private AwsCredentialsProvider credentialsProvider;
	private AssumeRoleRequest roleRequest;
	private AmiRelayMapToBytesConverter converter = new AmiRelayMapToBytesConverter();
	private Boolean deleteAfterRead;
	private Integer readCount;
	private int sessionDuration;
	private boolean refreshSessions = false;
	private long refreshPeriod;
	private long lastRefresh;
	private Region region;

	@Override
	public void start() {
		super.start();
		try {
			startSQS();
			onStartFinish(true);
		} catch(Exception e) {
			log.severe("Failed to start up the FH: " + e.getMessage());
			onStartFinish(false);
		}
	}
	
	private void startSQS() {
		Runnable r = new Runnable() {
			@Override
			public void run() {
				try {
		            ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
			                .queueUrl(queueUrl)
			                .maxNumberOfMessages(readCount)
			                .build();
					//start listening for data
					while (true && !Thread.interrupted()) {
				        try {
				        	if (refreshSessions) {
				        		long now = EH.currentTimeMillis();
				        		if (now - lastRefresh > refreshPeriod) {
				        			log.fine("Refreshing session credentials...");
				        			credentialsProvider = StsAssumeRoleCredentialsProvider.builder().stsClient(stsClient).refreshRequest(roleRequest).build();
				        			sqsClient = SqsClient.builder()
				        	 	            .region(region)
				        	 	            .credentialsProvider(credentialsProvider)
				        	 	            .build();
				        			lastRefresh = EH.currentTimeMillis();
				        		}
				        	}
				        	
				            List<Message> messages = sqsClient.receiveMessage(receiveMessageRequest).messages();
				            
				            for (final Message m: messages) {
				            	try {
				            		if (deleteAfterRead) {
				    	                DeleteMessageRequest deleteMessageRequest = DeleteMessageRequest.builder()
				    		                    .queueUrl(queueUrl)
				    		                    .receiptHandle(m.receiptHandle())
				    		                    .build();
				    		                sqsClient.deleteMessage(deleteMessageRequest);
				            		}
				            			
				            		String body = m.body();
				            		@SuppressWarnings("unchecked")
									Map<String, Object> jsonMap = (Map<String, Object>)INSTANCE.stringToObject(body);
				            		converter.clear();
				            		for (final Entry<String, Object> entry: jsonMap.entrySet()) {
				            			final Object o = entry.getValue();
			            				//If object is null, ignore (FH does not support writing/reading nulls for types)
				            			if (o == null)
				            				continue;
				            			final FHSetter setter = columnMapping != null ? columnMapping.get(entry.getKey()) : null;
				            			if (setter != null)				            						            				
				            					setter.set(converter, o);
				            			else
				            				converter.appendString(entry.getKey(), SH.toString(o));
				            		}
				            		
				            		publishObjectToAmi(-1, m.messageId(), amiTable, 0, converter.toBytes());			            		
				            	} catch (Exception e) {
				            		log.warning("Exception: " + e.getMessage() + ", while handling: " + m);
				            	}
				            }

				        } catch (SqsException e) {
				            throw new Exception(e.awsErrorDetails().errorMessage());
				        }
					}
				} catch (Exception e) {
					log.severe(e.getMessage());
					onFailed("Connection to SQS has been closed");
				} finally {
					if (sqsClient != null)
						sqsClient.close();
				}
			}
		};

		t = getManager().getThreadFactory().newThread(r);
		t.setDaemon(true);
		t.setName("SQSFH-" + this.getId() + this.queueName);
		t.start();
	}

	
	@Override
	public void init(int id, String name, PropertyController sysProps, PropertyController props, AmiRelayIn amiServer) {
		super.init(id, name, sysProps, props, amiServer);
		
		this.amiTable = props.getRequired(PROP_TABLE_NAME);
		this.readCount = props.getOptional(PROP_SQS_READ_COUNT, 5);
		this.queueUrl = this.props.getOptional(PROP_SQS_QUEUE_URL, "");
		this.queueName = this.props.getOptional(PROP_SQS_QUEUE_NAME, "");
		this.deleteAfterRead = this.props.getOptional(PROP_SQS_DELETE_AFTER_READ, false);
		
		String regionStr = this.props.getRequired(PROP_SQS_QUEUE_REGION);
		region = getRegion(regionStr);
		String profileName = this.props.getOptional(PROP_SQS_PROFILE_NAME, "");
		String profileFilepath = this.props.getOptional(PROP_SQS_PROFILE_FILE, "");
		String roleArn = this.props.getOptional(PROP_SQS_AWS_ROLE_ARN, "");
		String roleSessionName = this.props.getOptional(PROP_SQS_AWS_ROLE_SESSION_NAME, "");
		String accessKey = this.props.getOptional(PROP_SQS_ACCESS_KEY, "");
		String secretKey = this.props.getOptional(PROP_SQS_SECRET_KEY, "");
		sessionDuration = this.props.getOptional(PROP_SQS_SESSION_DURATION, 900);
		
		if (!SH.isEmpty(roleArn) && !SH.isEmpty(roleSessionName)) {
			log.info("Using AWS STS for credentials - session arn: " + roleArn + ", session name: " + roleSessionName);
			//Check to see if default credentials can be resolved (see DefaultCredentialsProvider javadoc for details)
			
			AwsCredentialsProvider stsCredentials;
			
			if (!SH.isEmpty(accessKey) && !SH.isEmpty(secretKey)) {
				log.fine("Resolving credentials using provided access and secret key");
				stsCredentials = StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey));
			} else {
				log.fine("Resolving credentials using aws default credentials provider");
				stsCredentials = DefaultCredentialsProvider.create();
			}
			
            stsClient = StsClient.builder()
                    .region(region)
                    .credentialsProvider(stsCredentials).build();

            roleRequest = AssumeRoleRequest.builder()
                    .roleArn(roleArn)
                    .roleSessionName(roleSessionName)
                    .durationSeconds(sessionDuration)
                    .build();

            //Attempt to assume role and terminate early if not possible
            stsClient.assumeRole(roleRequest);
            
            credentialsProvider = StsAssumeRoleCredentialsProvider.builder().stsClient(stsClient).refreshRequest(roleRequest).build();
            
            //Refresh session credentials a minute before expiry
            refreshSessions = true;
            lastRefresh = EH.currentTimeMillis();
            refreshPeriod = (sessionDuration - 60) * 1000;
            
		} else {
			log.info("Using AWS Profile for credentials");
			Builder b  = ProfileCredentialsProvider.builder();
      		 if(!profileFilepath.isEmpty()) {
      			 log.fine("Taking profile file from: " + profileFilepath);
      			 b = b.profileFile(ProfileFile.builder().content(Paths.get(profileFilepath)).build());
      		 }
      		 if (!profileName.isEmpty()) {
      			log.fine("Using profile: " + profileName);
      			 b = b.profileName(profileName);
      		 }
      		credentialsProvider = b.build();     
		}
		            
		sqsClient = SqsClient.builder()
 	            .region(region)
 	            .credentialsProvider(credentialsProvider)
 	            .build();
		
		//Attempt to resolve url from name
		if (queueUrl.isEmpty()) {
			if (queueName.isEmpty())
				throw new RuntimeException("Either one of queueUrl or queueName property must be specified!");
			
			try {
	            ListQueuesRequest listQueuesRequest = ListQueuesRequest.builder().queueNamePrefix(queueName).build();
	            ListQueuesResponse listQueuesResponse = sqsClient.listQueues(listQueuesRequest);
	            for (String url : listQueuesResponse.queueUrls()) {
	                this.queueUrl = url;
	                log.info("Using url for sqs: " + this.queueUrl);
	                break;
	            }

	        } catch (SqsException e) {
	            log.severe(e.awsErrorDetails().errorMessage());
	            super.onFailed(e.getMessage());
	        }
		}
		
		if (queueUrl.isEmpty())
			super.onFailed("Could not get sqs's queue url");
		
		String mapping = props.getOptional(PROP_TABLE_MAPPING, "");
		if (!mapping.isEmpty())
			buildTableSchema(mapping);
		
		getAmiRelayIn().onConnection(EMPTY_PARAMS);
		login();
	}
	
	//Expected format: col1=int,col2=string,col3=double,...
	private void buildTableSchema(final String mapping) {
		columnMapping = new HashMap<String, FHSetter>();
		List<String> colMaps = SH.splitToList(",", mapping);
		for (String colMap: colMaps) {
			//Extract column name and column type
			List<String> colType = SH.splitToList("=", colMap);
			if (colType.size() != 2)
				throw new RuntimeException("Failed to parse column name and column type: " + colMap);
			final String columnName = SH.trim(colType.get(0));
			final String columnType = SH.toLowerCase(SH.trim(colType.get(1)));
			switch (columnType) {
			case "string":
			case "str":
				columnMapping.put(columnName, new String_FHSetter(columnName));
				break;
			case "integer":
			case "int":
				columnMapping.put(columnName, new Int_FHSetter(columnName));
				break;
			case "short":
				columnMapping.put(columnName, new Short_FHSetter(columnName));
				break;
			case "long":
				columnMapping.put(columnName,  new Long_FHSetter(columnName));
				break;
			case "float":
				columnMapping.put(columnName,  new Float_FHSetter(columnName));
				break;
			case "double":
				columnMapping.put(columnName,  new Double_FHSetter(columnName));
				break;
			case "char":
			case "character":
				columnMapping.put(columnName,  new Char_FHSetter(columnName));
				break;
			case "bool":
			case "boolean":
				columnMapping.put(columnName,  new Bool_FHSetter(columnName));
				break;
			default:
				throw new UnsupportedOperationException("Unsupported column type: " + columnType);
			}
		}
	}
	
	private Region getRegion(final String region) {
		for (final Region r: Region.regions()) {
			if (r.id().equals(region))
				return r;
		}
		throw new RuntimeException("Invalid region provided: " + region + ", available regions: " + Region.regions().toString());
	}
	    
	//Setter classes
    private static class Double_FHSetter extends FHSetter {
		
		public Double_FHSetter(final String key) {
			super(key);
		}
		public void set(final AmiRelayMapToBytesConverter c, final Object val) {
			c.appendDouble(key, Caster_Double.INSTANCE.cast(val));
		}
	}
	
	private static class Float_FHSetter extends FHSetter {
		
		public Float_FHSetter(final String key) {
			super(key);
		}
		
		public void set(final AmiRelayMapToBytesConverter c, final Object val) {
			c.appendFloat(key, Caster_Float.INSTANCE.cast(val));
		}
	}
	
	private static class Long_FHSetter extends FHSetter {

		public Long_FHSetter(final String key) {
			super(key);
		}
		public void set(final AmiRelayMapToBytesConverter c, final Object val) {
			c.appendLong(key, Caster_Long.INSTANCE.cast(val));
		}
	}
	
	private static class Short_FHSetter extends FHSetter {
		
		public Short_FHSetter(final String key) {
			super(key);
		}
		public void set(final AmiRelayMapToBytesConverter c, final Object val) {
			c.appendShort(key, Caster_Short.INSTANCE.cast(val));
		}
	}
	
	private static class Bool_FHSetter extends FHSetter {
		
		public Bool_FHSetter(final String key) {
			super(key);
		}
		
		public void set(final AmiRelayMapToBytesConverter c, final Object val) {
			c.appendBoolean(key, Caster_Boolean.INSTANCE.cast(val));
		}
	}
	
	private static class Char_FHSetter extends FHSetter {
		
		public Char_FHSetter(final String key) {
			super(key);
		}
		
		public void set(final AmiRelayMapToBytesConverter c, final Object val) {
			c.appendChar(key, Caster_Character.INSTANCE.cast(val));
		}
	}
	
	private static class Int_FHSetter extends FHSetter {
		
		public Int_FHSetter(final String key) {
			super(key);
		}
		
		public void set(final AmiRelayMapToBytesConverter c, final Object val) {
			c.appendInt(key, Caster_Integer.INSTANCE.cast(val));
		}
	}
	
	private static class String_FHSetter extends FHSetter {
		
		public String_FHSetter(final String key) {
			super(key);
		}
		
		public void set(final AmiRelayMapToBytesConverter c, final Object val) {
			c.appendString(key, val.toString());
		}
	}
	
	private static abstract class FHSetter {
		
		final String key;
		
		public FHSetter(final String key) {
			this.key = key;
		}
		public abstract void set(final AmiRelayMapToBytesConverter c, final Object val);		
	}
	
}
