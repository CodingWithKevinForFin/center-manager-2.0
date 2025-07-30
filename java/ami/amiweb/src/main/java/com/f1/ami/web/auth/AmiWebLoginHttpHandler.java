package com.f1.ami.web.auth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.f1.ami.amicommon.msg.AmiWebLoginRequest;
import com.f1.ami.amicommon.msg.AmiWebLoginResponse;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.headless.AmiWebHeadlessManager;
import com.f1.ami.web.headless.AmiWebHeadlessSession;
import com.f1.ami.web.headless.AmiWebHeadlessWebState;
import com.f1.ami.web.headless.AmiWebModCountHttpHandler;
import com.f1.ami.web.pages.AmiWebPages;
import com.f1.base.Password;
import com.f1.container.ContainerServices;
import com.f1.container.RequestOutputPort;
import com.f1.http.HttpRequestResponse;
import com.f1.http.HttpSession;
import com.f1.http.HttpUtils;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.suite.web.WebStatesManager;
import com.f1.suite.web.portal.PortletManager;
import com.f1.suite.web.portal.impl.BasicPortletManager;
import com.f1.suite.web.util.WebHelper;
import com.f1.utils.ContentType;
import com.f1.utils.FastByteArrayInputStream;
import com.f1.utils.FastPrintStream;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class AmiWebLoginHttpHandler extends AbstractHttpHandler {
	private static final String INCORRECT_USERNAME_OR_PASSWORD = "Incorrect username or password";
	private static final Logger log = LH.get();
	private static final String updatedCSP = "default-src 'self'; font-src 'self' data:; img-src 'self' blob:; media-src 'self' blob:;";
	final public RequestOutputPort<AmiWebLoginRequest, AmiWebLoginResponse> loginPort;//newRequestOutputPort(AmiWebLoginRequest.class, AmiWebLoginResponse.class);

	private long timeoutMs;

	public AmiWebLoginHttpHandler(RequestOutputPort<AmiWebLoginRequest, AmiWebLoginResponse> lp) {
		this(5000, lp);
	}
	public AmiWebLoginHttpHandler(long timeoutMs, RequestOutputPort<AmiWebLoginRequest, AmiWebLoginResponse> lp) {
		this.timeoutMs = timeoutMs;
		this.loginPort = lp;
		this.loginPort.setConnectionOptional(true);
		this.putOverrideResponseHeader(HttpUtils.CONTENT_SECURITY_POLICY, updatedCSP);
	}

	private String indexPage = "/index.htm";

	@Override
	public void handle(HttpRequestResponse request2) throws IOException {
		super.handle(request2);
		Map<String, String> params = request2.getParams();
		final String password = params.get("password");
		final String username = params.get("username");
		if (username == null || password == null) {
			try {
				request2.forward("/");
				return;
			} catch (IOException e) {
				LH.warning(log, "Unexpected error handling login request", e);
				// Do we want to do the below;
				//	request.getRequest().getOutputStream().print("Critical Error Handling Login Request.");
			}
		}
		final WebStatesManager wsm = WebStatesManager.get(request2.getSession(false));
		if (wsm != null) {
			request2.putResponseHeader("Content-Security-Policy", "default-src 'self' 'unsafe-inline'; font-src 'self' data:; img-src 'self' blob:; media-src 'self' blob:");
			request2.forward(AmiWebPages.PAGE_ALREADY_LOGGED_IN);
			return;
		}
		String userAgent = request2.getHeader().get("User-Agent");
		final String tac = params.get("accept_agreement");
		String expectedTac = (String) request2.getHttpServer().getAttributes().get("termsSignature");
		if (expectedTac != null) {
			if (tac == null) {
				error(request2, username, "Must accept the agreement below");
				return;
			} else if (OH.ne(expectedTac, tac)) {
				error(request2, username, "Wrong agreement accepted");
				return;
			}
		}
		final AmiWebLoginRequest r = loginPort.getTools().nw(AmiWebLoginRequest.class);
		r.setUserName(username);
		r.setPassword(new Password(password));
		r.setClientLocation(request2.getRemoteHost());
		r.setClientAgent(userAgent);
		final AmiWebLoginResponse response;
		try {
			response = loginPort.requestWithFuture(r, null).getResult(timeoutMs, TimeUnit.MILLISECONDS).getAction();
		} catch (Exception e) {
			LH.warning(log, "Error logging in", e);
			request2.getAttributes().put("error", "Internal Error");
			try {
				request2.forward(indexPage);
			} catch (IOException e1) {
				LH.warning(log, "Error forwarding", e);
			}
			return;
		}
		if (evaluateResponse(response, username, request2)) {
			success(log, loginPort.getTools().getServices(), request2, response.getUsername(), response.getAttributes(), tac);
		}
	}

	public static void success(Logger log, ContainerServices services, HttpRequestResponse request, String username, Map<String, Object> attributes, String tac) {
		if (tac != null)
			LH.info(log, "User '", username, "' checked box 'I accept the agreement below' for: '", tac, "'");
		HttpSession session = request.getSession(true);
		AmiWebStatesManager sm = new AmiWebStatesManager(session, username, attributes, services);
		session.getAttributes().putIfAbsent(WebStatesManager.ID, sm);
		//		if (existing != null)
		//			throw new RuntimeException("Unexpected Concurrency exception for user: " + username);
		sm.logIn(services.getTools().getNow());
		sendToDashboard(request);

	}
	public static void sendToDashboard(HttpRequestResponse request) {
		FastPrintStream out = request.getOutputStream();
		request.setContentType(ContentType.HTML.getMimeType());
		request.putResponseHeader("Content-Security-Policy", "default-src 'self' 'unsafe-inline'; font-src 'self' data:; img-src 'self' blob:; media-src 'self' blob:");
		out.println("<script>");
		out.println("  var t=sessionStorage.getItem('" + BasicPortletManager.PRELOGINID + "');");
		out.println("  sessionStorage.removeItem('" + BasicPortletManager.PRELOGINID + "');");
		out.println("  if(t)");
		out.println("    window.location.href='" + BasicPortletManager.URL_START + "?" + BasicPortletManager.PRELOGINID + "='+t;");
		out.println("  else");
		out.println("    window.location.href='" + BasicPortletManager.URL_START + "';");
		out.println("</script>");
		request.setResponseType(HttpRequestResponse.HTTP_200_OK);
	}
	private boolean evaluateResponse(AmiWebLoginResponse response, String username, HttpRequestResponse httpRequest) {
		byte status = response.getStatus();
		String message = response.getMessage();
		if (SH.isnt(message)) {
			switch (status) {
				case AmiAuthResponse.STATUS_OKAY:
					return true;
				case AmiAuthResponse.STATUS_BAD_PASSWORD:
					message = INCORRECT_USERNAME_OR_PASSWORD;
					break;
				case AmiAuthResponse.STATUS_BAD_USERNAME:
					message = INCORRECT_USERNAME_OR_PASSWORD;
					break;
				case AmiAuthResponse.STATUS_GENERAL_ERROR:
					message = "Internal error, please try again";
					break;
				case AmiAuthResponse.STATUS_ACCOUNT_LOCKED:
					message = "Your account is locked";
					break;
				case AmiAuthResponse.STATUS_USER_COUNT_EXCEEDED:
					message = "User Count for this server exceeded";
					break;
				case AmiAuthResponse.STATUS_BAD_CREDENTIALS:
					message = "Credentials not found";
					break;
				case AmiAuthResponse.STATUS_SERVICE_DISABLED:
					message = "Service Disabled";
					break;
				default:
					message = "Unknown error, status code=" + status;

			}
		}
		error(httpRequest, username, message);
		return false;
	}
	private void error(HttpRequestResponse req, String username, String message) {
		req.getAttributes().put("username", username);
		req.getAttributes().put("error", message);
		try {
			req.forward(indexPage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String encode(String text) {
		try {
			return text == null ? null : SH.toString(IOH.checkSumBsdLong(new FastByteArrayInputStream(text.getBytes())));
		} catch (IOException e) {
			throw OH.toRuntime(e);
		}
	}

	public String getIndexPage() {
		return indexPage;
	}

	public void setIndexPage(String indexPage) {
		this.indexPage = indexPage;
	}

	//called by portal.jsp
	public static boolean isLoggedIn(HttpRequestResponse req) {
		HttpSession s = req.getSession(false);
		req.getAttributes().remove("pages");
		req.getAttributes().put("isLoggedIn", false);
		if (s != null) {
			AmiWebStatesManager wsm = (AmiWebStatesManager) WebStatesManager.get(s);
			if (wsm != null) {
				if (wsm.isLoggedIn()) {
					req.getAttributes().put("isLoggedIn", true);
				}
				List<Page> pages = new ArrayList<Page>();
				List<Page> headless = new ArrayList<Page>();
				for (String pgid : wsm.getPgIds()) {
					Page page = toPage((AmiWebState) wsm.getState(pgid));
					if (page != null)
						pages.add(page);
				}
				int hmc = 0;
				if (wsm.isDev()) {
					AmiWebHeadlessManager hm = wsm.getServices().getService(AmiWebHeadlessManager.SERVICE_ID, AmiWebHeadlessManager.class);
					for (String sessionName : hm.getSessionNames()) {
						AmiWebHeadlessSession session = hm.getSessionByName(sessionName);
						if (!session.isAlive() || session.getWebState().getWebStatesManager() == wsm)
							continue;//I already own this headless session
						Page page = toPage(session.getWebState());
						headless.add(page);
					}
					hmc = hm.getModCount();
				}
				Collections.sort(pages);
				Collections.sort(headless);
				req.getAttributes().put("canAddSession", wsm.canAddSession());
				req.getAttributes().put("pagesModCount", AmiWebModCountHttpHandler.toModCount(wsm.getModCount(), hmc));
				req.getAttributes().put("activePages", pages);
				req.getAttributes().put("headlessPages", headless);
				req.getAttributes().put("hasHeadlessPages", !headless.isEmpty());
				req.getAttributes().put("username", wsm.getUserName());
				return true;
			}
		}
		return false;
	}

	public static class Page implements Comparable<Page> {

		final private AmiWebState state;
		final private String owner;
		final private String comment;

		public Page(AmiWebState state) {
			this.state = state;
			AmiWebStatesManager webStatesManager = state.getWebStatesManager();
			this.owner = webStatesManager != null ? webStatesManager.getUser().getUserName() : null;
			this.comment = this.owner == null ? null : "(Owned by " + this.owner + ")";

		}

		public String getPgId() {
			return state.getPgId();
		}
		public String getName() {
			return this.state.getName();
		}
		public String getOwner() {
			return this.owner;
		}
		public String getLabel() {
			String r = this.state.getLabel();
			return WebHelper.escapeHtml(r);
		}
		public String getLayout() {
			return this.state.getLayout();
		}
		public boolean getIsHeadless() {
			return this.state instanceof AmiWebHeadlessWebState;
		}

		@Override
		public int compareTo(Page o) {
			return OH.compare(state.getStartedTime(), o.state.getStartedTime());
		}

	}

	public static Page toPage(AmiWebState state) {
		PortletManager pm = state.getPortletManager();
		if (pm == null)
			return null;
		AmiWebService service = (AmiWebService) pm.getServiceNoThrow(AmiWebService.ID);
		if (service == null)
			return null;
		return new Page(state);
	}
}
