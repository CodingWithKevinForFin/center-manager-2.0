package com.vortex.web.portlet.grids;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.base.Action;
import com.f1.container.ResultMessage;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuDivider;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.peripheral.KeyEvent;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.HtmlPortlet;
import com.f1.suite.web.portal.impl.HtmlPortletListener;
import com.f1.suite.web.portal.impl.WebMenuListener;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletTextAreaField;
import com.f1.suite.web.portal.impl.text.FastTextPortlet;
import com.f1.suite.web.portal.impl.text.TextModel;
import com.f1.suite.web.portal.impl.text.TextPortletListener;
import com.f1.suite.web.util.WebHelper;
import com.f1.utils.AH;
import com.f1.utils.ByteArray;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.LongArrayList;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.agg.IntegerAggregator;
import com.f1.utils.structs.Tuple2;
import com.f1.vortexcommon.msg.agent.VortexAgentFile;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentFileSearchRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentFileSearchResponse;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyePassToAgentRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyePassToAgentResponse;
import com.vortex.client.VortexClientUtils;
import com.vortex.web.VortexWebEyeService;
import com.vortex.web.portlet.grids.VortexFileSearchDialogPortlet.SearchOptions;
import com.vortex.web.portlet.grids.VortexFileSearchDialogPortlet.SearchTemplate;

public class VortexFileTextPortlet extends GridPortlet implements TextModel, HtmlPortletListener, TextPortletListener, WebMenuListener {
	private static final Logger log = LH.get(VortexFileTextPortlet.class);

	private static final byte[] PENDING = new byte[0];

	private StringBuilder buf = new StringBuilder();

	//portlets
	private FastTextPortlet textPortlet;
	private VortexWebEyeService service;
	private HtmlPortlet searchPortlet;

	//caches
	private int linesInMemory;

	//current file info
	private String machineUid;
	private VortexAgentFile initFile;
	private boolean endsWithLineBreak;
	private long totalFileSize;
	private long[] rawLineBreaks = OH.EMPTY_LONG_ARRAY;
	private byte[][] rawBytes = new byte[0][];
	private boolean isLineWrap = true;

	private long[] visibleLineStarts = OH.EMPTY_LONG_ARRAY;
	private int[] visibleLabels = OH.EMPTY_INT_ARRAY;

	//request stuff
	private VortexAgentFileSearchRequest currentRequest;
	private VortexAgentFileSearchRequest pendingRequest;
	private int pendingTop;
	private int pendingBottom;

	//search
	private boolean isSearch = false;

	private long[] searchOffsetsHighlightWord;
	private SearchOptions[] searchTextsHighlightWord;
	private long[] searchOffsetsHighlightLine;
	private SearchOptions[] searchTextsHighlightLine;

	private String searchTemplateName = "";
	private boolean searchIsCaseSensitive;
	final private Map<String, SearchOptions> searchTextToOptions = new HashMap<String, SearchOptions>();

	final private List<SearchOptions> searchOptions = new ArrayList<SearchOptions>();
	private VortexAgentFileSearchRequest searchRequest;

	final private Map<String, long[]> oldSearchIndexes = new HashMap<String, long[]>();
	final private Map<String, long[]> searchIndexes = new HashMap<String, long[]>();

	private Map<String, SearchTemplate> searchTemplates;

	public VortexFileTextPortlet(PortletConfig config) {
		super(config);
		this.service = (VortexWebEyeService) getManager().getService(VortexWebEyeService.ID);
		this.textPortlet = new FastTextPortlet(generateConfig(), this);
		this.searchPortlet = new HtmlPortlet(generateConfig());
		this.searchPortlet.setCssClass("text_search");
		this.textPortlet.addListener(this);
		updateSearchPortlet();
		this.searchPortlet.addListener(this);
		this.textPortlet.setMaxCharsPerLine(25000);
		//this.textPortlet.setMaxCharsPerLine(100);
		addChild(searchPortlet, 0, 0);
		addChild(textPortlet, 0, 1);
		setRowSize(0, 22);
	}
	private void updateSearchPortlet() {
		if (this.isSearch) {
			buf.append("<div style='text-align:center;width:100%;right:50px;padding:2px 0px'>");
			int j = 0;
			for (SearchOptions i : this.searchOptions) {
				int count = this.searchIndexes.get(i.search).length;
				buf.append("&nbsp;<button style='height:18px;font-size:10px' onclick='parentNode.parentNode.callback(event,\"b_").append(j).append("\")' >&lt;</button>");
				buf.append("<span style='padding:0px 1px 1px 1px;color:").append(i.fgcolor).append(";background:").append(i.color).append("'>");
				WebHelper.escapeHtmlIncludeBackslash(i.search, buf);
				buf.append(" (").append(count).append(")");
				buf.append("</span>");
				buf.append("<button style='height:18px;font-size:10px' onclick='parentNode.parentNode.callback(event,\"n_").append(j).append("\")' >&gt;</button>&nbsp;");
				j++;
			}
			if (this.searchOptions.size() > 1) {
				buf.append("&nbsp;&nbsp;&nbsp;&nbsp;<button style='height:18px;font-size:10px' onclick='parentNode.parentNode.callback(event,\"b\")' >&lt;</button><b>Any</b>");
				buf.append("<button style='height:18px;font-size:10px' onclick='parentNode.parentNode.callback(event,\"n\")' >&gt;</button>&nbsp;");
			}
			buf.append("</div>");
		}
		buf.append("<button style='height:20px;font-size:10px' onclick='parentNode.callback(event,\"search\")' >search</button>");
		this.searchPortlet.setHtml(SH.toStringAndClear(buf));

	}
	@Override
	public int getNumberOfLines(FastTextPortlet target) {
		//TOO:
		return visibleLineStarts.length;
	}
	private int getNumberOfRawLines() {
		return rawLineBreaks.length;
	}

	@Override
	public void prepareLines(FastTextPortlet target, int start, int count) {
		int linesCount = getNumberOfLines(target);
		IntegerAggregator missingIndexes = new IntegerAggregator();
		for (int i = start; i < start + count; i++) {
			//TOO:map to visible lines
			int rawLine = getRawLineForVisibleLine(i);
			byte[] cache = rawBytes[rawLine];
			if (cache == null)
				missingIndexes.add(rawLine);
		}

		if (missingIndexes.getCount() > 0 && linesCount > 0) {

			int top = Math.min(missingIndexes.getMin(), linesCount - 1);
			int bottom = Math.min(missingIndexes.getMax(), linesCount - 1);
			long topIndex = top == 0 ? 0 : (rawLineBreaks[top - 1] + 1);
			long botIndex = getBreakPositionForRawLine(bottom);
			while (botIndex - topIndex < 1024 * 1024) {
				boolean canUp = top > 0 && rawBytes[top - 1] == null;
				boolean canDn = bottom + 1 < rawBytes.length && rawBytes[bottom + 1] == null;
				if (!canUp && !canDn)
					break;
				if (canUp)
					top--;
				if (canDn)
					bottom++;
				topIndex = top == 0 ? 0 : (rawLineBreaks[top - 1] + 1);
				botIndex = getBreakPositionForRawLine(bottom);
			}
			sendRequest(topIndex, botIndex, top, bottom);
		}
	}

	private int getRawLineForVisibleLine(int i) {
		return MH.abs(this.visibleLabels[i]) - 1;
	}
	private long getBreakPositionForRawLine(int line) {
		return line == rawLineBreaks.length ? this.totalFileSize : rawLineBreaks[line];
	}
	private long getStartPositionForRawLine(int line) {
		return line == 0 ? 0 : rawLineBreaks[line - 1] + 1;
	}

	private long getBreakPositionForVisibleLine(int line) {
		if (line + 1 == visibleLineStarts.length) {
			if (this.endsWithLineBreak)
				return this.totalFileSize - 1;
			else
				return this.totalFileSize;
		}

		long r = visibleLineStarts[line + 1];
		if (visibleLabels[line + 1] > 0)//next line is a real line so we need to account for newline char
			r--;
		return r;
	}
	private long getStartPositionForVisibleLine(int line) {
		//TOO:map to visible line
		return line == 0 ? 0 : visibleLineStarts[line];
	}

	private int getVisibleLineAtOffset(long offset) {
		//TOO:map to visible line
		return AH.indexOfSortedLessThanEqualTo(offset, this.visibleLineStarts);
	}

	private boolean getIsAllInMemory() {
		return linesInMemory == getNumberOfRawLines();
	}

	@Override
	public int getLabelWidth(FastTextPortlet target) {
		int r = 8 * MH.getDigitsCount(getNumberOfLines(target), 10);
		return r;
	}

	public void initFromFile(VortexAgentFile file, String machineUid) {
		this.oldSearchIndexes.clear();
		this.currentRequest = null;
		this.initFile = file;
		this.linesInMemory = 0;
		this.machineUid = machineUid;
		boolean maintainSearch;
		this.textPortlet.clearScrollMarks();
		if (file == null || !CH.comm(file.getSearchOffsets().keySet(), this.getActiveSearches(), false, true, false).isEmpty()) {
			resetSearch();
			maintainSearch = false;
		} else
			maintainSearch = true;
		this.oldSearchIndexes.clear();
		textPortlet.forceRefresh();
		this.textPortlet.selectLines(-1, -1);
		if (file == null) {
			this.rawLineBreaks = OH.EMPTY_LONG_ARRAY;
			this.rawBytes = new byte[0][];
			this.visibleLabels = OH.EMPTY_INT_ARRAY;
			this.visibleLineStarts = OH.EMPTY_LONG_ARRAY;
			this.totalFileSize = 0;
			return;
		}
		this.totalFileSize = file.getSize();
		long[] lines = file.getSearchOffsets().get("\n");
		this.rawLineBreaks = AH.sort(lines);
		this.endsWithLineBreak = AH.last(this.rawLineBreaks, -1) == file.getSize() - 1;
		this.rawBytes = new byte[(endsWithLineBreak ? 0 : 1) + this.rawLineBreaks.length][];
		initVisibleLines();
		processData(file);
		file.setData(null);
		if (maintainSearch)
			this.applySearchResults(file.getSearchOffsets());
	}
	private void initVisibleLines() {
		if (this.getNumberOfRawLines() == 0) {
			visibleLineStarts = OH.EMPTY_LONG_ARRAY;
			visibleLabels = OH.EMPTY_INT_ARRAY;
			return;
		}
		int max = this.textPortlet.getMaxCharsPerLine();
		int linesCount = 0;
		for (int i = 0; i < this.rawLineBreaks.length; i++) {
			long rawLineLength = this.rawLineBreaks[i] - (i > 0 ? this.rawLineBreaks[i - 1] + 1 : 0);
			if (rawLineLength <= max)
				linesCount++;
			else
				linesCount += (int) ((rawLineLength + max - 1) / max);
		}
		if (!this.endsWithLineBreak) {
			long rawLineLength = this.totalFileSize - AH.last(this.rawLineBreaks, -1);
			if (rawLineLength <= max)
				linesCount++;
			else
				linesCount += (int) ((rawLineLength + max - 1) / max);
		}
		this.visibleLabels = new int[linesCount];
		this.visibleLineStarts = new long[linesCount];
		int visPos = 0;
		for (int i = 0; i < this.rawLineBreaks.length; i++) {
			long start = i > 0 ? rawLineBreaks[i - 1] + 1 : 0;
			long end = rawLineBreaks[i];
			this.visibleLabels[visPos] = i + 1;
			this.visibleLineStarts[visPos] = start;
			for (;;) {
				visPos++;
				if ((start += max) >= end)
					break;
				this.visibleLabels[visPos] = -(i + 1);
				this.visibleLineStarts[visPos] = start;
			}
		}
		if (!this.endsWithLineBreak) {
			long start = AH.last(rawLineBreaks, -1) + 1;
			long end = this.totalFileSize;
			this.visibleLabels[visPos] = this.rawLineBreaks.length + 1;
			this.visibleLineStarts[visPos] = start;
			for (;;) {
				visPos++;
				if ((start += max) >= end)
					break;
				this.visibleLabels[visPos] = -(this.rawLineBreaks.length + 1);
				this.visibleLineStarts[visPos] = start;
			}
		}
		OH.assertEq(visPos, this.visibleLineStarts.length);
	}
	private void resetSearch() {
		this.searchOffsetsHighlightWord = null;
		this.searchTextsHighlightWord = null;
		this.searchOffsetsHighlightLine = null;
		this.searchTextsHighlightLine = null;
		this.searchTextToOptions.clear();
		this.searchIndexes.clear();
		this.isSearch = false;
		updateSearchPortlet();
	}

	private void processData(VortexAgentFile file) {
		long dataOffset = file.getDataOffset();
		VortexClientUtils.decompressFile(file);
		byte[] data = file.getData();
		int index = AH.indexOfSortedLessThanEqualTo(dataOffset, this.rawLineBreaks);
		index++;

		long last = index > 0 ? this.rawLineBreaks[index - 1] + 1 : 0;
		for (;;) {
			try {
				long next;
				if (index > this.rawLineBreaks.length || getBreakPositionForRawLine(index) > data.length + dataOffset)
					break;
				if (index == this.rawLineBreaks.length) {
					if (this.endsWithLineBreak || last >= data.length + dataOffset)
						break;
					next = dataOffset + data.length;
				} else
					next = this.rawLineBreaks[index];
				int start = (int) (last - dataOffset);
				int end = (int) (next - dataOffset);
				if (rawBytes[index] != PENDING && rawBytes[index] != null)
					LH.warning(log, "duplicate data at index: ", index);
				rawBytes[index] = Arrays.copyOfRange(data, start, end);
				this.linesInMemory++;

				//TOO: map to visible lines
				int top = getVisibleLineAtOffset(getStartPositionForRawLine(index));
				int bot = getVisibleLineAtOffset(getBreakPositionForRawLine(index));
				for (int i = top; i <= bot; i++)
					textPortlet.resetLinesAt(i);
				last = next + 1;
				index++;
			} catch (Exception e) {
				throw new RuntimeException("at index: " + index, e);
			}
		}

	}

	public void sendRequest(long start, long end, int top, int bottom) {
		if (initFile == null)
			return;
		VortexAgentFileSearchRequest agentRequest = nw(VortexAgentFileSearchRequest.class);
		agentRequest.setRecurse(false);
		agentRequest.setMaxDataSize(end - start);
		agentRequest.setDataOffset(start);
		agentRequest.setIncludeDataExpression("*");
		agentRequest.setIncludeSearchPositionsExpression("");
		agentRequest.setIncludeChecksumExpression("");
		agentRequest.setSearchInFileExpressions(null);
		agentRequest.setIsSearchCaseSensitive(false);
		agentRequest.setRootPaths(CH.l(initFile.getPath()));
		if (this.currentRequest == null) {
			for (int i = top; i <= bottom; i++)
				rawBytes[i] = PENDING;
			this.currentRequest = agentRequest;
			VortexEyePassToAgentRequest eyeReq = nw(VortexEyePassToAgentRequest.class);
			eyeReq.setAgentMachineUid(machineUid);
			eyeReq.setAgentRequest(agentRequest);
			service.sendRequestToBackend(getPortletId(), eyeReq);
		} else {
			this.pendingTop = top;
			this.pendingBottom = bottom;
			this.pendingRequest = agentRequest;
		}
	}
	@Override
	public void onBackendResponse(ResultMessage<Action> result) {
		VortexAgentRequest req = ((VortexEyePassToAgentRequest) result.getRequestMessage().getAction()).getAgentRequest();
		if (req == this.currentRequest) {
			try {
				VortexEyePassToAgentResponse p2a = (VortexEyePassToAgentResponse) result.getAction();
				if (!p2a.getOk()) {
					getManager().showAlert(p2a.getMessage());
					return;
				}
				VortexAgentFileSearchResponse res = (VortexAgentFileSearchResponse) p2a.getAgentResponse();
				if (res.getOk()) {
					List<VortexAgentFile> files = res.getFiles();
					if (files.size() == 1) {
						VortexAgentFile file = files.get(0);
						processData(file);
					} else
						getManager().showAlert("No files in response");
				} else if (res.getMessage() != null) {
					getManager().showAlert(res.getMessage());
				} else {
					getManager().showAlert("Backend error: " + res.getClass().getName());
				}
			} finally {
				this.currentRequest = null;
				if (this.pendingRequest != null) {
					VortexEyePassToAgentRequest eyeReq = nw(VortexEyePassToAgentRequest.class);
					eyeReq.setAgentMachineUid(machineUid);
					eyeReq.setAgentRequest(pendingRequest);
					service.sendRequestToBackend(getPortletId(), eyeReq);
					for (int i = pendingTop; i <= pendingBottom; i++)
						rawBytes[i] = PENDING;
					this.currentRequest = pendingRequest;
					this.pendingRequest = null;
				}
			}
		} else if (req == this.searchRequest) {
			try {
				VortexEyePassToAgentResponse p2a = (VortexEyePassToAgentResponse) result.getAction();
				if (!p2a.getOk()) {
					getManager().showAlert(p2a.getMessage());
					return;
				}
				VortexAgentFileSearchResponse res = (VortexAgentFileSearchResponse) p2a.getAgentResponse();
				if (res.getOk()) {
					applySearchResults(res.getFiles().get(0).getSearchOffsets());
				} else if (res.getMessage() != null) {
					getManager().showAlert(res.getMessage());
				} else {
					getManager().showAlert("Backend error: " + res.getClass().getName());
				}
			} finally {
				this.searchRequest = null;
			}
		}
	}
	@Override
	public void onUserClick(HtmlPortlet portlet) {

	}
	@Override
	public void onUserCallback(HtmlPortlet portlet, String id, int mouseX, int mouseY, Map<String, String> attributes) {

		if (portlet == this.searchPortlet && "search".equals(id)) {
			this.searchTemplates = VortexFileSearchDialogPortlet.loadTemplates(getManager());
			if (CH.isntEmpty(searchTemplates)) {
				WebMenu menu = new BasicWebMenu();
				int i = 0;
				for (String template : searchTemplates.keySet()) {
					BasicWebMenuLink link = new BasicWebMenuLink(template, true, "template_" + (i++));
					if (OH.eq(this.searchTemplateName, template))
						link.setCssStyle("style.fontWeight=bold");
					menu.add(link);
				}
				menu.add(new BasicWebMenuDivider());
				menu.add(new BasicWebMenuLink("Open Search Dialog", true, "search").setCssStyle("style.fontStyle=italic"));
				getManager().showContextMenu(menu, this);
			} else {
				VortexFileSearchDialogPortlet p = new VortexFileSearchDialogPortlet(this);
				if (this.searchTextToOptions != null) {
					p.initSearch(searchTemplateName, this.searchTextToOptions.values(), this.searchIsCaseSensitive);
				}
				getManager().showDialog("Search", p);
			}
		} else {
			if (id.startsWith("b_")) {
				SearchOptions search = this.searchOptions.get(Integer.parseInt(SH.stripPrefix(id, "b_", true)));
				long[] offsets = this.searchIndexes.get(search.search);
				int curline = textPortlet.getTopLineSelected();
				if (curline == -1)
					curline = textPortlet.getTopLineVisible();
				long startPosition = getStartPositionForVisibleLine(curline) - 1;
				int next = AH.indexOfSortedLessThanEqualTo(startPosition, offsets);
				if (next == -1)
					return;
				int line = getVisibleLineAtOffset(offsets[next]);
				if (line == -1)
					return;
				textPortlet.ensureLineVisible(line, 5);
				textPortlet.selectLines(line, line);
			} else if (id.startsWith("n_")) {
				SearchOptions search = this.searchOptions.get(Integer.parseInt(SH.stripPrefix(id, "n_", true)));
				long[] offsets = this.searchIndexes.get(search.search);
				int curline = textPortlet.getTopLineSelected();
				if (curline == -1)
					curline = textPortlet.getTopLineVisible();
				long startPosition = getBreakPositionForVisibleLine(curline) + 1;
				int next = AH.indexOfSortedGreaterThanEqualTo(startPosition, offsets);
				if (next == -1)
					return;
				int line = getVisibleLineAtOffset(offsets[next]);
				if (line == -1)
					return;
				textPortlet.ensureLineVisible(line, 5);
				textPortlet.selectLines(line, line);
			} else if (id.equals("n")) {
				int curline = textPortlet.getTopLineSelected();
				if (curline == -1)
					curline = textPortlet.getTopLineVisible();
				int line = -1;
				long[] offset = null;
				long startPosition = getBreakPositionForVisibleLine(curline) + 1;
				for (long[] offsets : this.searchIndexes.values()) {
					int i = AH.indexOfSortedGreaterThanEqualTo(startPosition, offsets);
					if (i == -1)
						continue;
					int line2 = getVisibleLineAtOffset(offsets[i]);
					if (line2 == -1)
						continue;
					if (line == -1 || line2 < line) {
						line = line2;
						offset = offsets;
					}
				}
				if (offset != null) {
					if (line == -1)
						return;
					textPortlet.ensureLineVisible(line, 5);
					textPortlet.selectLines(line, line);
				}
			} else if (id.equals("b")) {
				int curline = textPortlet.getTopLineSelected();
				if (curline == -1)
					curline = textPortlet.getTopLineVisible();
				int line = -1;
				long[] offset = null;
				long startPosition = getStartPositionForVisibleLine(curline) + 1;
				for (long[] offsets : this.searchIndexes.values()) {
					int i = AH.indexOfSortedLessThanEqualTo(startPosition, offsets);
					if (i == -1)
						continue;
					int line2 = getVisibleLineAtOffset(offsets[i]);
					if (line2 == -1)
						continue;
					if (line == -1 || line2 < line) {
						line = line2;
						offset = offsets;
					}
				}
				if (offset != null) {
					if (line == -1)
						return;
					textPortlet.ensureLineVisible(line, 5);
					textPortlet.selectLines(line, line);
				}
			}
		}
	}
	public void doSearch(String templateName, List<SearchOptions> textAndColors, boolean caseSensistive) {

		resetSearch();
		this.searchTextToOptions.clear();
		this.searchOptions.clear();
		this.searchOptions.addAll(textAndColors);
		for (SearchOptions e : this.searchOptions)
			searchTextToOptions.put(e.search, e);
		this.searchTemplateName = templateName;
		if (this.searchIsCaseSensitive != caseSensistive) {
			this.searchIsCaseSensitive = caseSensistive;
			this.oldSearchIndexes.clear();
		}
		if (getIsAllInMemory()) {
			Map<String, long[]> indexes = new HashMap<String, long[]>();
			LongArrayList indexList = new LongArrayList();
			for (SearchOptions e : textAndColors) {
				String text = e.search;
				if (oldSearchIndexes.containsKey(text))
					continue;
				byte[] textBytes = text.getBytes();
				if (!caseSensistive)
					SH.uppercaseInplace(textBytes);
				indexList.clear();
				for (int lineNum = 0; lineNum < getNumberOfRawLines(); lineNum++) {
					byte[] line = this.rawBytes[lineNum];
					if (!caseSensistive)
						SH.uppercaseInplace(line = line.clone());
					long offset = this.getStartPositionForRawLine(lineNum);
					int i = 0;
					for (;;) {
						i = AH.indexOf(line, textBytes, i);
						if (i == -1)
							break;
						indexList.add(offset + i);
						i++;
					}
				}
				indexes.put(text, indexList.toLongArray());
			}
			applySearchResults(indexes);
		} else {

			List<String> searches = new ArrayList<String>();
			for (String s : this.searchTextToOptions.keySet())
				if (!oldSearchIndexes.containsKey(s))
					searches.add(s);
			if (searches.isEmpty())
				applySearchResults(Collections.EMPTY_MAP);
			else {
				VortexAgentFileSearchRequest agentRequest = nw(VortexAgentFileSearchRequest.class);
				agentRequest.setRecurse(false);
				agentRequest.setIncludeDataExpression("");
				agentRequest.setIncludeSearchPositionsExpression("*");
				agentRequest.setIncludeChecksumExpression("");
				agentRequest.setSearchInFileExpressions(searches);
				agentRequest.setIsSearchCaseSensitive(caseSensistive);
				agentRequest.setRootPaths(CH.l(initFile.getPath()));
				VortexEyePassToAgentRequest eyeReq = nw(VortexEyePassToAgentRequest.class);
				eyeReq.setAgentMachineUid(machineUid);
				eyeReq.setAgentRequest(agentRequest);
				this.searchRequest = agentRequest;
				service.sendRequestToBackend(getPortletId(), eyeReq);
			}
		}
	}

	private void applySearchResults(Map<String, long[]> results) {
		this.textPortlet.forceRefresh();
		this.oldSearchIndexes.putAll(results);
		this.searchIndexes.clear();
		for (String s : this.searchTextToOptions.keySet()) {
			long[] result = results.get(s);
			if (result != null)
				searchIndexes.put(s, result);
			else
				searchIndexes.put(s, CH.getOrThrow(oldSearchIndexes, s));
		}

		Tuple2<long[], SearchOptions[]> arrays = buildSearchArrays(SearchOptions.TYPE_HIGHLIGHT_WORD);
		this.searchOffsetsHighlightWord = arrays.getA();
		this.searchTextsHighlightWord = arrays.getB();

		arrays = buildSearchArrays(SearchOptions.TYPE_HIGHLIGHT_LINE);
		this.searchOffsetsHighlightLine = arrays.getA();
		this.searchTextsHighlightLine = arrays.getB();

		isSearch = true;
		updateSearchPortlet();
		this.textPortlet.getTopLineVisible();

		this.textPortlet.clearScrollMarks();
		for (Entry<String, long[]> e : this.searchIndexes.entrySet()) {
			int lastline = -1;
			for (long pos : e.getValue()) {
				int line = getVisibleLineAtOffset(pos);
				if (line != lastline) {
					this.textPortlet.addScrollbarMark(line, this.searchTextToOptions.get(e.getKey()).color);
					lastline = line;
				}
			}
		}
		if (this.searchOffsetsHighlightWord.length != 0) {
			int line = getVisibleLineAtOffset(this.searchOffsetsHighlightWord[0]);
			this.textPortlet.selectLines(line, line);
			this.textPortlet.ensureLineVisible(line, 5);
		}
	}

	private Tuple2<long[], SearchOptions[]> buildSearchArrays(byte type) {
		int tot = 0;
		for (Entry<String, long[]> e : this.searchIndexes.entrySet())
			if (this.searchTextToOptions.get(e.getKey()).type == type)
				tot += e.getValue().length;
		LongToString[] values = new LongToString[tot];
		int i = 0;
		for (Entry<String, long[]> e : this.searchIndexes.entrySet()) {
			SearchOptions option = this.searchTextToOptions.get(e.getKey());
			if (option.type == type)
				for (long v : e.getValue())
					values[i++] = new LongToString(v, option);
		}

		Arrays.sort(values);
		long[] offsets = new long[tot];
		SearchOptions[] texts = new SearchOptions[tot];
		for (i = 0; i < tot; i++) {
			offsets[i] = values[i].longVal;
			texts[i] = values[i].option;
		}
		return new Tuple2<long[], SearchOptions[]>(offsets, texts);
	}

	private static class LongToString implements Comparable<LongToString> {

		private final long longVal;
		private final SearchOptions option;

		public LongToString(long longVal, SearchOptions option) {
			this.longVal = longVal;
			this.option = option;
		}

		public long getLongVal() {
			return longVal;
		}

		public SearchOptions getOption() {
			return option;
		}

		@Override
		public int compareTo(LongToString o) {
			return OH.compare(longVal, o.longVal);
		}

	}

	private void toLineSearch(int line, ByteArray txt, StringBuilder sink) {
		int rawLine = getRawLineForVisibleLine(line);
		int n = AH.indexOfSortedGreaterThanEqualTo(getStartPositionForRawLine(rawLine), this.searchOffsetsHighlightLine);
		if (n != -1) {
			long offset = this.searchOffsetsHighlightLine[n];
			if (offset < getBreakPositionForRawLine(rawLine)) {
				SearchOptions color = this.searchTextsHighlightLine[n];
				//sink.append("<span style=\\'color:white; background:").append(searchTextToOptions.get(color).color).append("\\'>");
				sink.append("style.color=").append(color.fgcolor).append("|style.background=").append(color.color);
			}
		}
	}
	private void toWordSearch(int line, ByteArray txt, StringBuilder sink) {

		//Highlight words
		int len = txt.length();
		final long start = getStartPositionForVisibleLine(line);
		long end = getBreakPositionForVisibleLine(line);
		int i = AH.indexOfSortedGreaterThanEqualTo(start, this.searchOffsetsHighlightWord);
		if (end - start > len)
			end = start + len;
		int lastPos = 0;
		if (this.visibleLabels[line] < 0) {//this is a wrapped line, hence there could a search trailing from the prior... what a pain
			int iPrior = AH.indexOfSortedLessThanEqualTo(start - 1, this.searchOffsetsHighlightWord);
			if (iPrior != -1) {
				SearchOptions priorSearch = this.searchTextsHighlightWord[iPrior];
				long priorSearchStart = this.searchOffsetsHighlightWord[iPrior];
				int overlap = (int) (priorSearchStart + priorSearch.search.length() - start);
				if (overlap > 0) {
					lastPos = Math.min(len, overlap);
					sink.append("<span style=\\'color:").append(priorSearch.fgcolor).append("; background:").append(priorSearch.color).append("\\'>");
					WebHelper.escapeHtml(txt, 0, lastPos, true, sink);
					sink.append("</span>");
				}
			}
		}
		if ((i == -1 || this.searchOffsetsHighlightWord[i] >= end || this.searchOffsetsHighlightWord[i] < start)) {
			WebHelper.escapeHtml(txt, lastPos, len, true, sink);//no change
			return;
		}
		outer: for (;;) {
			int pos = (int) (this.searchOffsetsHighlightWord[i] - start);
			if (pos > len)
				break;
			SearchOptions searchText = this.searchTextsHighlightWord[i];
			WebHelper.escapeHtml(txt, lastPos, pos, true, sink);
			sink.append("<span style=\\'color:").append(searchText.fgcolor).append("; background:").append(searchText.color).append("\\'>");
			WebHelper.escapeHtml(txt, pos, Math.min(len, pos + searchText.search.length()), true, sink);
			sink.append("</span>");
			lastPos = pos + searchText.search.length();
			do {
				i++;
				if (i >= this.searchOffsetsHighlightWord.length || this.searchOffsetsHighlightWord[i] >= end)
					break outer;
			} while (this.searchOffsetsHighlightWord[i] < lastPos + start);
		}
		WebHelper.escapeHtml(txt, lastPos, len, true, sink);
	}
	private ByteArray tmpByteArray = new ByteArray();

	@Override
	public void formatText(FastTextPortlet target, int line, StringBuilder sink) {
		if (!getTextForLine(line, tmpByteArray))
			return;
		int max = this.textPortlet.getMaxCharsPerLine();
		if (isSearch) {
			toWordSearch(line, tmpByteArray, sink);
		} else {
			WebHelper.escapeHtmlIncludeBackslash(tmpByteArray, sink);
		}
		sink.append("</span>");
	}
	private boolean getTextForLine(int line, ByteArray sink) {
		int rawLine = getRawLineForVisibleLine(line);
		byte[] data = this.rawBytes[rawLine];
		if (data == null || data == PENDING)
			return false;
		long rs = getStartPositionForRawLine(rawLine);
		long vs = getStartPositionForVisibleLine(line);
		long vb = getBreakPositionForVisibleLine(line);
		try {
			sink.reset(data, (int) (vs - rs), (int) (vb - rs));
		} catch (Exception e) {
			throw new RuntimeException("bad value at line: " + line, e);
		}
		return true;
	}
	@Override
	public void formatLabel(FastTextPortlet target, int line, StringBuilder sink) {
		int max = this.textPortlet.getMaxCharsPerLine();
		int label = this.visibleLabels[line];
		if (label > 0)
			sink.append(label);
	}

	@Override
	public void formatStyle(FastTextPortlet portlet, int lineNumber, StringBuilder sink) {
		if (isSearch) {
			toLineSearch(lineNumber, tmpByteArray, sink);
		}
	}

	@Override
	public void onTextContextMenu(FastTextPortlet portlet, String id) {
		if ("copy".equals(id)) {
			FormPortlet fp = new FormPortlet(generateConfig());
			boolean missing = false;
			int count = 0;
			for (int lineNum : textPortlet.getSelectedLines()) {

				//TOO: map from visible
				if (!getTextForLine(lineNum, tmpByteArray))
					//byte[] line = rawBytes[lineNum];
					//if (line == null)
					missing = true;
				else {
					buf.append(tmpByteArray);
					if (lineNum + 1 == visibleLabels.length || visibleLabels[lineNum + 1] > 0)
						buf.append('\n');
				}
				//SH.appendBytes(line, 0, line.length, buf).append('\n');
				count++;
				if (buf.length() > 1024 * 100) {
					getManager().showAlert("Too much data selected. Result has been truncated to 100K");
					buf.setLength(1024 * 100);
					break;
				}
			}
			int w = (int) (.8 * getManager().getRoot().getWidth());
			int h = (int) (.8 * getManager().getRoot().getHeight());
			FormPortletTextAreaField field = fp.addField(new FormPortletTextAreaField("")).setValue(SH.toStringAndClear(buf)).setHeight(h - 50);
			fp.setLabelsWidth(0);
			getManager().showDialog("copy", fp, w, h);
			if (missing)
				getManager().showAlert("Not all selected data was available");
		} else if ("wrap".equals(id)) {
			this.isLineWrap = true;
			setColumnsVisible(this.textPortlet.getColumnsVisible());
		} else if ("nowrap".equals(id)) {
			this.isLineWrap = false;
			this.textPortlet.setMaxCharsPerLine(25000);
			initVisibleLines();
		}
	}
	@Override
	public WebMenu createMenu(FastTextPortlet fastTextPortlet) {
		BasicWebMenu r = new BasicWebMenu();
		r.addChild(new BasicWebMenuLink("View for Copy to clipboard", true, "copy"));
		if (this.isLineWrap)
			r.addChild(new BasicWebMenuLink("Don't Wrap Lines", true, "nowrap"));
		else
			r.addChild(new BasicWebMenuLink("Wrap Lines", true, "wrap"));
		return r;
	}
	@Override
	public void setColumnsVisible(int columns) {
		if (this.isLineWrap) {
			int top = this.textPortlet.getTopLineVisible();
			this.textPortlet.setMaxCharsPerLine(columns);
			initVisibleLines();
			if (this.getNumberOfRawLines() > 0) {
				int rawLine = getRawLineForVisibleLine(top);
				int top2 = getVisibleLineAtOffset(getStartPositionForRawLine(rawLine));
				this.textPortlet.moveToLineTop(top2);
			}
		}
	}
	public Set<String> getActiveSearches() {
		return this.searchTextToOptions.keySet();
	}
	public boolean getActiveSearchesAreCaseSensitive() {
		return this.searchIsCaseSensitive;
	}
	@Override
	public void onMenuItem(String id) {
		if (id.startsWith("template_")) {
			int j = SH.parseInt(SH.stripPrefix(id, "template_", true));
			int i = 0;
			for (SearchTemplate template : searchTemplates.values()) {
				if (i++ == j) {
					VortexFileSearchDialogPortlet p = new VortexFileSearchDialogPortlet(this);
					doSearch(template.templateName, template.searchOptions, template.isCaseSensitive);
					break;
				}
			}
		} else if (id.equals("search")) {
			VortexFileSearchDialogPortlet p = new VortexFileSearchDialogPortlet(this);
			if (this.searchTextToOptions != null) {
				p.initSearch(searchTemplateName, this.searchTextToOptions.values(), this.searchIsCaseSensitive);
			}
			getManager().showDialog("Search", p);
		}
	}
	@Override
	public void onMenuDismissed() {
		// TODO Auto-generated method stub

	}
	@Override
	public void onHtmlChanged(String old, String nuw) {
		// TODO Auto-generated method stub

	}
	@Override
	public boolean onTextUserKeyEvent(FastTextPortlet portlet, KeyEvent keyEvent) {
		return false;
	}
}
