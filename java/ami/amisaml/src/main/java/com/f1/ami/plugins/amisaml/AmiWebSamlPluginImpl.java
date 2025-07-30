package com.f1.ami.plugins.amisaml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.security.KeyPair;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import javax.xml.namespace.QName;

import org.joda.time.DateTime;
import org.opensaml.core.config.InitializationService;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.schema.impl.XSAnyImpl;
import org.opensaml.core.xml.schema.impl.XSStringImpl;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.messaging.MessageException;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.decoder.MessageDecodingException;
import org.opensaml.messaging.encoder.MessageEncodingException;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.SignableSAMLObject;
import org.opensaml.saml.common.binding.BindingException;
import org.opensaml.saml.common.binding.SAMLBindingSupport;
import org.opensaml.saml.common.messaging.SAMLMessageSecuritySupport;
import org.opensaml.saml.common.messaging.context.SAMLBindingContext;
import org.opensaml.saml.common.messaging.context.SAMLEndpointContext;
import org.opensaml.saml.common.messaging.context.SAMLMessageInfoContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.binding.decoding.impl.HTTPRedirectDeflateDecoder;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AttributeStatement;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.NameIDPolicy;
import org.opensaml.saml.saml2.core.NameIDType;
import org.opensaml.saml.saml2.core.RequestAbstractType;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.StatusCode;
import org.opensaml.saml.saml2.core.StatusResponseType;
import org.opensaml.saml.saml2.core.Subject;
import org.opensaml.saml.saml2.metadata.SingleSignOnService;
import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.BasicCredential;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialSupport;
import org.opensaml.security.crypto.KeySupport;
import org.opensaml.security.x509.BasicX509Credential;
import org.opensaml.xmlsec.SignatureSigningParameters;
import org.opensaml.xmlsec.config.JavaCryptoValidationInitializer;
import org.opensaml.xmlsec.crypto.XMLSigningUtil;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.w3c.dom.Element;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.web.AmiWebProperties;
import com.f1.ami.web.AmiWebSamlPlugin;
import com.f1.ami.web.auth.AmiAuthUser;
import com.f1.ami.web.auth.BasicAmiAuthUser;
import com.f1.container.ContainerTools;
import com.f1.http.HttpRequestResponse;
import com.f1.suite.web.HttpRequestAction;
import com.f1.utils.CH;
import com.f1.utils.FastByteArrayInputStream;
import com.f1.utils.GuidHelper;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.f1.utils.encrypt.EncoderUtils;

import net.shibboleth.utilities.java.support.codec.Base64Support;
import net.shibboleth.utilities.java.support.collection.Pair;
import net.shibboleth.utilities.java.support.net.BasicURLComparator;
import net.shibboleth.utilities.java.support.net.URIException;
import net.shibboleth.utilities.java.support.net.URLBuilder;
import net.shibboleth.utilities.java.support.primitive.StringSupport;
import net.shibboleth.utilities.java.support.xml.ParserPool;
import net.shibboleth.utilities.java.support.xml.SerializeSupport;
import net.shibboleth.utilities.java.support.xml.XMLParserException;

public class AmiWebSamlPluginImpl implements AmiWebSamlPlugin {
	public static final String PROPERTY_SAML_AMI_GROUP_FIELD = "saml.ami.group.field";
	public static final String PROPERTY_SAML_AMI_GROUPS = "saml.ami.groups";

	private static final String AMISCRIPT_VARIABLE = "amiscript.variable.";
	private static final int DEFAULT_RSA_KEY_STRENGTH = 2048;
	private static final Logger log = LH.get();
	private String samlIpUrl;
	private String samlSpUrl;
	private String entityId;
	private String expectedResponsePath;
	private String relayState;
	private Set<String> amiAdmins;
	private Set<String> amiDevs;
	private String amiIsAdminField;
	private String amiIsDevField;
	//	final private String amiIsAdminValues;
	//	final private String amiIsDevValues;
	private File samlIpCertFile;
	private BasicX509Credential publicCredential;
	private int messageLifetime;
	private int clockSkew;
	private boolean samlDebug;
	private int noCertRsaKeyStrength;
	private String userNameField;
	private String groupField;
	private Map<String, String> groupDefaultLayout;
	private Map<String, Set<String>> groupLayouts;
	private LinkedHashSet<String> samlGroupsSet;
	private String nameIDFormat;

	@Override
	public void init(ContainerTools tools, PropertyController props) {
		JavaCryptoValidationInitializer javaCryptoValidationInitializer = new JavaCryptoValidationInitializer();
		try {
			javaCryptoValidationInitializer.init();
			InitializationService.initialize();
		} catch (Exception e) {
			throw new RuntimeException("Failed to init SAML", e);
		}
		this.samlIpCertFile = props.getOptional(AmiWebProperties.PROPERTY_SAML_IP_CERT_FILE, File.class);
		if (this.samlIpCertFile != null) {
			String certData;
			try {
				certData = IOH.readText(this.samlIpCertFile);
			} catch (Exception e) {
				throw new RuntimeException(
						"Property '" + AmiWebProperties.PROPERTY_SAML_IP_CERT_FILE + "' refers to file that could not be read: " + IOH.getFullPath(this.samlIpCertFile), e);
			}
			try {
				CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
				X509Certificate certificate = (X509Certificate) certificateFactory.generateCertificate(new FastByteArrayInputStream(EncoderUtils.decodeCert(certData)));
				this.publicCredential = new BasicX509Credential(certificate);
			} catch (Exception e) {
				throw new RuntimeException(
						"Property '" + AmiWebProperties.PROPERTY_SAML_IP_CERT_FILE + "' refers to file that has invalid X.509 certificate: " + IOH.getFullPath(this.samlIpCertFile),
						e);
			}
		} else
			this.publicCredential = null;
		this.clockSkew = props.getOptional(AmiWebProperties.PROPERTY_SAML_CLOCK_SKEW_MS, 100);
		this.messageLifetime = props.getOptional(AmiWebProperties.PROPERTY_SAML_MESSAGE_LIFETIME_MS, 60000000);
		this.samlIpUrl = props.getRequired(AmiWebProperties.PROPERTY_SAML_IP_URL);
		this.samlSpUrl = props.getRequired(AmiWebProperties.PROPERTY_SAML_SP_URL);
		this.entityId = props.getRequired(AmiWebProperties.PROPERTY_SAML_ENTITYID);
		this.relayState = props.getOptional(AmiWebProperties.PROPERTY_SAML_RELAY_STATE);
		this.userNameField = props.getOptional(AmiWebProperties.PROPERTY_SAML_USERNAME_FIELD, "uid");
		this.samlDebug = props.getOptional(AmiWebProperties.PROPERTY_SAML_DEBUG, Boolean.TRUE);
		String nameIDVal = props.getOptional(AmiWebProperties.PROPERTY_SAML_NAME_ID_FORMAT, "transient");
		this.nameIDFormat = parseNameIDFormat(nameIDVal);
		this.amiIsAdminField = props.getOptional(AmiWebProperties.PROPERTY_SAML_AMI_ISADMIN_FIELD);
		this.amiIsDevField = props.getOptional(AmiWebProperties.PROPERTY_SAML_AMI_ISDEV_FIELD);
		String amiAdminsValues = props.getOptional(AmiWebProperties.PROPERTY_SAML_AMI_ISADMIN_VALUES);
		String amiDevValues = props.getOptional(AmiWebProperties.PROPERTY_SAML_AMI_ISDEV_VALUES);
		if (amiAdminsValues != null)
			this.amiAdmins = SH.splitToSet(",", amiAdminsValues);
		if (amiDevValues != null)
			this.amiDevs = SH.splitToSet(",", amiDevValues);

		this.groupField = props.getOptional(AmiWebSamlPluginImpl.PROPERTY_SAML_AMI_GROUP_FIELD);
		String samlGroups = props.getOptional(AmiWebSamlPluginImpl.PROPERTY_SAML_AMI_GROUPS);

		this.groupDefaultLayout = new HashMap<String, String>();
		this.groupLayouts = new HashMap<String, Set<String>>();
		this.samlGroupsSet = CH.s(new LinkedHashSet<String>(), SH.split(',', samlGroups));
		for (String samlGroup : this.samlGroupsSet) {
			PropertyController subProps = props.getSubPropertyController("saml.group." + samlGroup + '.');
			String defaultLayout = subProps.getOptional("ami.default.layout");
			String layouts = subProps.getOptional("ami.layouts");
			Set<String> layoutsSet = null;
			if (layouts != null)
				layoutsSet = SH.splitToSet(",", layouts);

			this.groupDefaultLayout.put(samlGroup, defaultLayout);
			this.groupLayouts.put(samlGroup, layoutsSet);
		}

		this.expectedResponsePath = SH.afterFirst(SH.afterFirst(samlSpUrl, "://", null), '/');
		this.noCertRsaKeyStrength = props.getOptional(AmiWebProperties.PROPERTY_SAML_NO_CERT_RSA_KEY_STRENGTH, DEFAULT_RSA_KEY_STRENGTH);
		if (this.noCertRsaKeyStrength < DEFAULT_RSA_KEY_STRENGTH)
			AmiUtils.logSecurityWarning("AMI WEB SAML PLUGIN has been configured with a weak RSA key strenght of `" + this.noCertRsaKeyStrength
					+ "`. This message can be ignored if the property" + AmiWebProperties.PROPERTY_SAML_IP_CERT_FILE + " is configured, otherwise update the property `"
					+ AmiWebProperties.PROPERTY_SAML_NO_CERT_RSA_KEY_STRENGTH + "`");
		if (expectedResponsePath == null)
			throw new RuntimeException("Error with " + AmiWebProperties.PROPERTY_SAML_SP_URL + "=" + samlSpUrl + " (should be in for protocol://host:port/....");
	}
	private String parseNameIDFormat(String input) {
		if ("email".contentEquals(input)) {
			return NameIDType.EMAIL;
		} else if ("unspecified".contentEquals(input)) {
			return NameIDType.UNSPECIFIED;
		} else if ("persistent".contentEquals(input)) {
			return NameIDType.PERSISTENT;
		} else {
			return NameIDType.TRANSIENT;
		}
	}
	@Override
	public String getExpectedResponsePath() {
		return expectedResponsePath;
	}
	@Override
	public String getLogoutRedirectPath() {
		return null;
	}

	@Override
	public String getPluginId() {
		return "SAML_PLUGIN";
	}

	@Override
	public String buildAuthRequest(HttpRequestResponse req) throws Exception {
		return buildAuthnRequest(samlIpUrl, samlSpUrl, entityId);
	}

	@Override
	public AmiAuthUser processResponse(HttpRequestAction req) throws Exception {
		StringBuilder sink = new StringBuilder();
		req.getRequest().getRequestContentBuffer(sink);
		return process(sink.toString());
	}
	public AmiAuthUser process(String response) throws Exception {
		//TODO: is this needed?
		//		String responseRelayState = SH.trim(SH.beforeFirst(SH.afterFirst(response, "RelayState="), '&'));
		response = SH.trim(SH.beforeFirst(SH.afterFirst(response, '='), '&'));
		String signature = null;
		String reqUrl = this.samlSpUrl;
		HTTPRedirectDeflateDecoder t = new HTTPRedirectDeflateDecoder();
		MessageContext messageContext = new MessageContext();
		if (this.relayState != null)
			SAMLBindingSupport.setRelayState(messageContext, relayState);
		InputStream samlMessageIns = decodeMessage(response);
		if (samlDebug) {
			LH.info(log, AmiWebProperties.PROPERTY_SAML_DEBUG, "=true so debugging encoded response from IDP: ", response);
			LH.info(log, AmiWebProperties.PROPERTY_SAML_DEBUG, "=true so debugging decoded response from IDP: ", IOH.readText(decodeMessage(response)));
		}
		SAMLObject samlMessage = (SAMLObject) unmarshallMessage(samlMessageIns);
		messageContext.setMessage(samlMessage);
		populateBindingContext(messageContext, signature);
		checkEndpointURI(messageContext, reqUrl);
		checkLifetime(messageContext, this.clockSkew, this.messageLifetime, true);
		checkSigning(messageContext, true);
		Map<String, Object> r = processSamlResponse(samlMessage);

		String username = (String) r.remove(this.userNameField);
		return new BasicAmiAuthUser(username, r);

	}

	public Map<String, Object> processSamlResponse(Object samlMessage) {
		Map<String, Object> r = new HashMap<String, Object>();
		Response res = (Response) samlMessage;
		//Assertions mujltiple???
		// each has attributes
		boolean isAdmin = false;
		boolean isDev = false;
		Set<String> groups = new HashSet<String>();
		for (Assertion i : res.getAssertions()) {
			Subject subject = i.getSubject();
			if (subject != null) {
				NameID name = subject.getNameID();
				addAttribute(r, "NameID", name.getValue(), res);
			}
			for (AttributeStatement s : i.getAttributeStatements()) {
				for (Attribute q : s.getAttributes()) {
					List<XMLObject> attributeValues = q.getAttributeValues();
					if (attributeValues == null || attributeValues.size() == 0)
						continue;

					String name = q.getFriendlyName();
					if (name == null)
						name = q.getName();

					Object value;
					if (attributeValues.size() == 1) {
						value = getValuesFromXML(attributeValues.get(0));
						addAttribute(r, name, value, res);
						addAmiscriptVariableAttribute(r, name, value, res);

						if (name == null)
							LH.info(log, "Could not extract: ", attributeValues.get(0));
					} else {
						value = getValuesFromXML(attributeValues);
						addAttribute(r, name, value, res);
						addAmiscriptVariableAttribute(r, name, value, res);
						if (name == null)
							LH.info(log, "Could not extract: ", attributeValues);
					}

					//Handle AmiAdmins
					if (this.amiIsAdminField != null && this.amiAdmins != null && !isAdmin && SH.equals(this.amiIsAdminField, name)) {
						if (value instanceof String) {
							String vs = (String) value;
							if (vs != null && this.amiAdmins.contains(vs))
								isAdmin = true;
						} else if (value instanceof List) {
							List<String> vl = (List<String>) value;
							if (vl != null && CH.containsAny(this.amiAdmins, vl))
								isAdmin = true;
						}

					}

					//Handle AmiDevs
					if (this.amiIsDevField != null && this.amiDevs != null && !isDev && SH.equals(this.amiIsDevField, name)) {
						if (value instanceof String) {
							String vs = (String) value;
							if (vs != null && this.amiDevs.contains(vs))
								isDev = true;
						} else if (value instanceof List) {
							List<String> vl = (List<String>) value;
							if (vl != null && CH.containsAny(this.amiDevs, vl))
								isDev = true;
						}
					}

					//Handle Group
					if (this.groupField != null && SH.equals(this.groupField, name)) {
						if (value instanceof String) {
							String vs = (String) value;
							groups.add(vs);
						} else if (value instanceof List) {
							List<String> ls = (List<String>) value;
							if (ls != null)
								groups.addAll(ls);
						}
					}
				}
			}
		}
		if (this.amiIsAdminField != null)
			addAttribute(r, AmiAuthUser.PROPERTY_ISADMIN, isAdmin ? "true" : "false", res);
		if (this.amiIsDevField != null)
			addAttribute(r, AmiAuthUser.PROPERTY_ISDEV, isDev ? "true" : "false", res);
		if (this.groupField != null) {
			LinkedHashSet<String> amiUserGroups = new LinkedHashSet<String>();
			CH.comm(this.samlGroupsSet, groups, false, false, true, amiUserGroups);
			String defaultLayout = null;
			String layouts = null;
			Set<String> groupLayouts = new LinkedHashSet<String>();
			for (String group : amiUserGroups) {
				if (defaultLayout == null)
					defaultLayout = this.groupDefaultLayout.get(group);
				Set<String> setLayouts = this.groupLayouts.get(group);
				if (setLayouts != null)
					CH.addAll(groupLayouts, setLayouts);
			}
			layouts = SH.join(",", groupLayouts);
			addAttribute(r, AmiAuthUser.PROPERTY_DEFAULT_LAYOUT, defaultLayout, res);
			addAttribute(r, AmiAuthUser.PROPERTY_LAYOUTS, layouts, res);
		}

		if (samlDebug)
			LH.info(log, "Process Saml Response processed attributes:", r);
		return r;
	}

	private void addAmiscriptVariableAttribute(Map<String, Object> attributes, String key, Object val, Response samlResponse) {
		key = AMISCRIPT_VARIABLE + key;
		if (attributes.containsKey(key))
			if (samlDebug)
				LH.info(log, "Attributes key `", key, "` already exists. Attributes:  ", attributes, " for saml response: ", samlResponse);
			else
				LH.info(log, "Attributes key `", key, "` already exists. ");

		if (val instanceof String)
			val = SH.doubleQuote((String) val);
		attributes.put(key, val);
	}

	private void addAttribute(Map<String, Object> attributes, String key, Object val, Response samlResponse) {
		if (attributes.containsKey(key))
			if (samlDebug)
				LH.info(log, "Attributes key `", key, "` already exists. Attributes:  ", attributes, " for saml response: ", samlResponse);
			else
				LH.info(log, "Attributes key `", key, "` already exists. ");

		attributes.put(key, val);
	}
	private static Object getValuesFromXML(XMLObject v) {
		if (v instanceof XSStringImpl)
			return ((XSStringImpl) v).getValue();
		else if (v instanceof XSAnyImpl)
			return ((XSAnyImpl) v).getTextContent();
		LH.info(log, "Cant handle ", v.getClass().getName());
		throw new ClassCastException("Cant handle " + v.getClass().getName());
	}
	private static Object getValuesFromXML(List<XMLObject> l) {
		if (l == null || l.size() == 0)
			return new ArrayList<String>();
		else {
			List<Object> r = new ArrayList<Object>();

			for (int i = 0, sz = l.size(); i < sz; i++)
				r.add(getValuesFromXML(l.get(i)));
			return r;
		}
	}

	static protected InputStream decodeMessage(String message) throws MessageDecodingException, IOException {
		byte decodedBytes[];
		message = SH.decodeUrl(message);
		decodedBytes = EncoderUtils.decode64(message);
		if (decodedBytes == null) {
			throw new MessageDecodingException("Unable to Base64 decode incoming message");
		}
		ByteArrayInputStream bytesIn = new ByteArrayInputStream(decodedBytes);
		return bytesIn;
	}
	protected static XMLObject unmarshallMessage(InputStream messageStream) throws MessageDecodingException, XMLParserException, UnmarshallingException {
		ParserPool parserPool = XMLObjectProviderRegistrySupport.getParserPool();
		XMLObject message = XMLObjectSupport.unmarshallFromInputStream(parserPool, messageStream);
		return message;
	}
	static protected void populateBindingContext(MessageContext messageContext, String signature) {
		SAMLBindingContext bindingContext = (SAMLBindingContext) messageContext.getSubcontext(org.opensaml.saml.common.messaging.context.SAMLBindingContext.class, true);
		bindingContext.setBindingUri(getBindingURI());
		bindingContext.setHasBindingSignature(SH.is(signature));
		bindingContext.setIntendedDestinationEndpointURIRequired(SAMLBindingSupport.isMessageSigned(messageContext));
	}
	static String getBindingURI() {
		return "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect";
	}
	static public void checkEndpointURI(MessageContext messageContext, String receiverEndpoint) throws URIException, MessageException {
		BasicURLComparator comparator = new BasicURLComparator();
		String messageDestination = StringSupport.trimOrNull(SAMLBindingSupport.getIntendedDestinationEndpointURI(messageContext));
		boolean bindingRequires = SAMLBindingSupport.isIntendedDestinationEndpointURIRequired(messageContext);
		if (messageDestination == null) {
			if (bindingRequires)
				throw new MessageHandlerException("SAML message intended destination (required by binding) was not present");
			else
				return;
		}
		boolean matched = comparator.compare(messageDestination, receiverEndpoint);
		if (!matched)
			throw new MessageHandlerException(
					"SAML message failed received endpoint check, messageDestination: " + messageDestination + " does not match receiverEndpoint: " + receiverEndpoint);
	}

	static public void checkLifetime(MessageContext messageContext, long clockSkew, long messageLifetime, boolean requiredRule) throws MessageHandlerException {
		SAMLMessageInfoContext msgInfoContext = (SAMLMessageInfoContext) messageContext.getSubcontext(org.opensaml.saml.common.messaging.context.SAMLMessageInfoContext.class,
				true);
		if (msgInfoContext.getMessageIssueInstant() == null) {
			if (requiredRule)
				throw new MessageHandlerException("Inbound SAML message issue instant not present in message context");
			else
				return;
		}
		DateTime issueInstant = msgInfoContext.getMessageIssueInstant();
		DateTime now = new DateTime();
		DateTime latestValid = now.plus(clockSkew);
		DateTime expiration = issueInstant.plus(clockSkew + messageLifetime);
		// if request time is later than respond time
		if (issueInstant.isAfter(latestValid))
			throw new MessageHandlerException("Message was rejected because was issued in the future (clock skew is " + clockSkew + "ms)" + " issue time " + issueInstant.toString()
					+ " --- valid time: " + latestValid.toString());
		if (expiration.isBefore(now))
			throw new MessageHandlerException("Message was rejected due to issue instant expiration (clock skew is " + clockSkew + "ms)" + " issue time " + issueInstant.toString()
					+ " --- valid time: " + latestValid.toString());
	}
	private static void checkSigning(MessageContext messageContext, boolean signErrorResponses) throws SecurityException, MarshallingException, SignatureException {
		final SignatureSigningParameters signingParameters = SAMLMessageSecuritySupport.getContextSigningParameters(messageContext);
		if (signingParameters != null) {
			if (!signErrorResponses && isErrorResponse(messageContext.getMessage())) {
				return;
			} else {
				SAMLMessageSecuritySupport.signMessage(messageContext);
			}
		}

	}
	private static boolean isErrorResponse(Object message) {
		if (message != null) {
			if (message instanceof Response) {
				if (((Response) message).getStatus() != null) {
					StatusCode s1 = ((Response) message).getStatus().getStatusCode();
					return s1 != null && s1.getValue() != null && !org.opensaml.saml.saml1.core.StatusCode.SUCCESS.equals(s1.getValue());
				}
			} else if (message instanceof StatusResponseType) {
				if (((StatusResponseType) message).getStatus() != null) {
					final org.opensaml.saml.saml2.core.StatusCode s2 = ((StatusResponseType) message).getStatus().getStatusCode();
					return s2 != null && s2.getValue() != null && !org.opensaml.saml.saml2.core.StatusCode.SUCCESS.equals(s2.getValue());
				}
			}
		}

		return false;
	}

	public BasicCredential generateCredentials() throws NoSuchAlgorithmException, NoSuchProviderException {
		if (this.publicCredential == null) {
			KeyPair keyPair = KeySupport.generateKeyPair("RSA", this.noCertRsaKeyStrength, null);
			return CredentialSupport.getSimpleCredential(keyPair.getPublic(), keyPair.getPrivate());
		} else {
			return CredentialSupport.getSimpleCredential(this.publicCredential.getPublicKey(), this.publicCredential.getPrivateKey());
		}
	}
	public String buildAuthnRequest(String ipUrl, String spUrl, String entityId) throws IllegalArgumentException, SecurityException, IllegalAccessException, NoSuchFieldException,
			NoSuchAlgorithmException, NoSuchProviderException, MessageEncodingException, BindingException, UnsupportedEncodingException, MarshallingException, IOException,
			org.opensaml.security.SecurityException, CertificateException, KeyStoreException, UnrecoverableEntryException {
		AuthnRequest authnRequest = buildSAMLObject(AuthnRequest.class);
		authnRequest.setIssueInstant(new DateTime());
		authnRequest.setProtocolBinding(SAMLConstants.SAML2_POST_BINDING_URI);
		String acsUrl = spUrl;
		String ipdssoDestination = ipUrl;
		Credential credentials = generateCredentials();
		authnRequest.setAssertionConsumerServiceURL(acsUrl);
		StringBuilder id = new StringBuilder("A").append(GuidHelper.getGuid(62));
		authnRequest.setID(id.toString());
		Issuer issuer = buildSAMLObject(Issuer.class);
		issuer.setValue(entityId);
		authnRequest.setIssuer(issuer);
		NameIDPolicy nameIDPolicy = buildSAMLObject(NameIDPolicy.class);
		nameIDPolicy.setFormat(this.nameIDFormat);
		nameIDPolicy.setAllowCreate(true);
		authnRequest.setNameIDPolicy(nameIDPolicy);
		authnRequest.setDestination(ipdssoDestination);
		if (samlDebug)
			LH.info(log, "buildAuthnRequest destination : ", authnRequest.getDestination());
		MessageContext context = new MessageContext();
		//		SignatureSigningParameters signatureSigningParameters = new SignatureSigningParameters();
		//		signatureSigningParameters.setSigningCredential(credentials);
		//		signatureSigningParameters.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);
		//
		//		SignatureBuilder signFactory = new SignatureBuilder();
		//		SignatureImpl signature = signFactory.buildObject();
		//		signature.setSigningCredential(signatureSigningParameters.getSigningCredential());
		//		authnRequest.setSignature(signature);
		context.setMessage(authnRequest);
		SAMLPeerEntityContext peerEntityContext = context.getSubcontext(SAMLPeerEntityContext.class, true);
		SAMLEndpointContext endpointContext = peerEntityContext.getSubcontext(SAMLEndpointContext.class, true);

		SingleSignOnService endpoint = buildSAMLObject(SingleSignOnService.class);
		endpoint.setBinding(SAMLConstants.SAML2_REDIRECT_BINDING_URI);
		endpoint.setLocation(ipdssoDestination);
		endpointContext.setEndpoint(endpoint);
		String redirect = encodeRedirect(context, samlDebug);

		return redirect;

	}
	public static <T> T buildSAMLObject(final Class<T> clazz) throws IllegalArgumentException, SecurityException, IllegalAccessException, NoSuchFieldException {
		XMLObjectBuilderFactory builderFactory = XMLObjectProviderRegistrySupport.getBuilderFactory();
		QName defaultElementName = (QName) clazz.getDeclaredField("DEFAULT_ELEMENT_NAME").get(null);
		T object = (T) builderFactory.getBuilder(defaultElementName).buildObject(defaultElementName);
		return object;
	}
	static public String encodeRedirect(MessageContext messageContext, boolean samlDebug)
			throws MessageEncodingException, BindingException, UnsupportedEncodingException, MarshallingException, IOException, org.opensaml.security.SecurityException {
		SAMLObject outboundMessage = (SAMLObject) messageContext.getMessage();
		String endpointURL = getEndpointURL(messageContext).toString();
		removeSignature(outboundMessage);
		String encodedMessage = deflateAndBase64Encode(outboundMessage);
		if (samlDebug) {
			LH.info(log, "encodeRedirect endpointURL ", endpointURL);
			try {
				LH.info(log, "encodeRedirect intendedDestinationEndpoint ", SAMLBindingSupport.getIntendedDestinationEndpointURI(messageContext));
			} catch (MessageException e) {
				e.printStackTrace();
			}
		}
		String redirectURL = buildRedirectURL(messageContext, endpointURL, encodedMessage);
		if (samlDebug)
			LH.info(log, "encodeRedirect redirectURL ", redirectURL);

		return redirectURL;
	}
	static protected URI getEndpointURL(MessageContext messageContext) throws MessageEncodingException, BindingException {
		return SAMLBindingSupport.getEndpointURL(messageContext);
	}
	static protected String deflateAndBase64Encode(SAMLObject message) throws MessageEncodingException, MarshallingException, UnsupportedEncodingException, IOException {
		ByteArrayOutputStream bytesOut;
		Element element = XMLObjectSupport.marshall(message);
		String messageStr = SerializeSupport.nodeToString(element);
		bytesOut = new ByteArrayOutputStream();
		Deflater deflater = new Deflater(8, true);
		DeflaterOutputStream deflaterStream = new DeflaterOutputStream(bytesOut, deflater);
		deflaterStream.write(messageStr.getBytes("UTF-8"));
		deflaterStream.finish();
		return Base64Support.encode(bytesOut.toByteArray(), false);
	}
	static protected void removeSignature(SAMLObject message) {
		if (message instanceof SignableSAMLObject) {
			SignableSAMLObject signableMessage = (SignableSAMLObject) message;
			if (signableMessage.isSigned()) {
				signableMessage.setSignature(null);
			}
		}
	}
	static protected String buildRedirectURL(MessageContext messageContext, String endpoint, String message)
			throws MessageEncodingException, org.opensaml.security.SecurityException {
		LH.info(log, "Building URL to redirect client to");
		URLBuilder urlBuilder = null;
		try {
			urlBuilder = new URLBuilder(endpoint);
		} catch (MalformedURLException e) {
			throw new MessageEncodingException((new StringBuilder()).append("Endpoint URL ").append(endpoint).append(" is not a valid URL").toString(), e);
		}
		List queryParams = urlBuilder.getQueryParams();
		//	queryParams.clear();
		SAMLObject outboundMessage = (SAMLObject) messageContext.getMessage();
		if (outboundMessage instanceof RequestAbstractType)
			queryParams.add(new Pair("SAMLRequest", message));
		else if (outboundMessage instanceof StatusResponseType)
			queryParams.add(new Pair("SAMLResponse", message));
		else
			throw new MessageEncodingException("SAML message is neither a SAML RequestAbstractType or StatusResponseType");
		String relayState = SAMLBindingSupport.getRelayState(messageContext);
		if (SAMLBindingSupport.checkRelayState(relayState))
			queryParams.add(new Pair("RelayState", relayState));
		SignatureSigningParameters signingParameters = SAMLMessageSecuritySupport.getContextSigningParameters(messageContext);
		if (signingParameters != null && signingParameters.getSigningCredential() != null) {
			String sigAlgURI = getSignatureAlgorithmURI(signingParameters);
			Pair sigAlg = new Pair("SigAlg", sigAlgURI);
			queryParams.add(sigAlg);
			String sigMaterial = urlBuilder.buildQueryString();
			queryParams.add(new Pair("Signature", generateSignature(signingParameters.getSigningCredential(), sigAlgURI, sigMaterial)));
		} else {
			LH.info(log, "No signing credential was supplied, skipping HTTP-Redirect DEFLATE signing");
		}
		return urlBuilder.buildURL();
	}
	static protected String getSignatureAlgorithmURI(SignatureSigningParameters signingParameters) throws MessageEncodingException {
		if (signingParameters.getSignatureAlgorithm() != null)
			return signingParameters.getSignatureAlgorithm();
		else
			throw new MessageEncodingException("The signing algorithm URI could not be determined");
	}

	static protected String generateSignature(Credential signingCredential, String algorithmURI, String queryString)
			throws MessageEncodingException, org.opensaml.security.SecurityException {
		String b64Signature = null;
		try {
			byte rawSignature[] = XMLSigningUtil.signWithURI(signingCredential, algorithmURI, queryString.getBytes("UTF-8"));
			b64Signature = Base64Support.encode(rawSignature, false);
			LH.info(log, "Generated digital signature value (base64-encoded) {}", b64Signature);
		} catch (SecurityException e) {
			LH.warning(log, "Error during URL signing process", e);
			throw new MessageEncodingException("Unable to sign URL query string", e);
		} catch (UnsupportedEncodingException e) {
		}
		return b64Signature;
	}
	@Override
	public String handleLogout(HttpRequestResponse req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
