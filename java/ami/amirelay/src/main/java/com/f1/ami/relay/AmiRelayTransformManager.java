package com.f1.ami.relay;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.base.MappingEntry;
import com.f1.container.ContainerTools;
import com.f1.utils.CH;
import com.f1.utils.CachedFile;
import com.f1.utils.CachedFile.Cache;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.impl.StringCharReader;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.string.JavaExpressionParser;
import com.f1.utils.string.Node;
import com.f1.utils.string.node.BlockNode;
import com.f1.utils.string.node.DeclarationNode;
import com.f1.utils.string.node.OperationNode;
import com.f1.utils.string.node.VariableNode;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.table.derived.BasicDerivedCellParser;
import com.f1.utils.structs.table.derived.MethodFactoryManager;
import com.f1.utils.structs.table.stack.BasicCalcTypes;

//Responsible for parsing the relay.transform and *.relay.dictionary files
public class AmiRelayTransformManager {
	private static final Logger log = LH.get();

	final private CachedFile transformsFile;
	final private String transformsFileFullPath;
	final private Map<String, Tuple2<CachedFile, Cache>> dictionaryFiles = new HashMap<String, Tuple2<CachedFile, Cache>>();
	final private List<AmiRelayTransform> transforms = new ArrayList<AmiRelayTransform>();
	final private Map<String, AmiRelayDictionary> dictionaries = new TreeMap<String, AmiRelayDictionary>();
	private boolean transformsReferToDictionary;
	private Cache lastData;
	private String dictionaryFileMask;

	private long delay;

	private AmiRelayTransforms threadSafeTransforms;
	final private AmiRelayScriptManager scriptManager;

	private boolean debug;

	private Throwable parseException;

	public AmiRelayTransformManager(ContainerTools tools, AmiRelayScriptManager scriptManager, long delay) {
		this.scriptManager = scriptManager;
		this.debug = tools.getOptional(AmiRelayProperties.OPTION_AMI_RELAY_TRANSFORMS_DEBUG, Boolean.FALSE);
		this.threadSafeTransforms = new AmiRelayTransforms("", Collections.EMPTY_MAP, null, this.debug);
		this.delay = delay;
		File f = tools.getRequired(AmiRelayProperties.OPTION_AMI_RELAY_TRANSFORMS_FILE, File.class);
		this.dictionaryFileMask = tools.getOptional(AmiRelayProperties.OPTION_AMI_RELAY_DICTIONARY_FILES, String.class);
		if (f.isDirectory())
			throw new RuntimeException(AmiRelayProperties.OPTION_AMI_RELAY_TRANSFORMS_FILE + " can not point to a directory: " + IOH.getFullPath(f));
		if (!f.exists()) {
			try {
				IOH.writeText(f, IOH.readTextFromResource("relay.transforms.template"));
				LH.info(log, "Created default routing file at " + IOH.getFullPath(f));
			} catch (Exception e) {
				LH.info(log, "Could not create default relay routes file at ", IOH.getFullPath(f), ": ", e.getMessage());
			}
		}
		this.transformsFile = new CachedFile(f, delay);
		this.transformsFileFullPath = IOH.getFullPath(this.transformsFile.getFile());
	}
	public boolean parseIfChanged(boolean throwOnError) {
		try {
			boolean changed = false;
			boolean justParsedTransforms = false;
			if (lastData == null || lastData.isOld()) {
				if (lastData == null)
					lastData = this.transformsFile.getData();
				else
					lastData = lastData.getUpdated();
				this.parseTransforms();
				justParsedTransforms = true;
				changed = true;
			}
			if (transformsReferToDictionary) {
				List<File> files = AmiUtils.findFiles(this.dictionaryFileMask, false, false);
				boolean filesChanged = false;
				if (files.size() == this.dictionaryFiles.size()) {
					for (File f : files) {
						Tuple2<CachedFile, Cache> tuple = this.dictionaryFiles.get(f.getAbsolutePath());
						if (tuple == null || tuple.getB().isOld()) {
							filesChanged = true;
							break;
						}
					}
				} else
					filesChanged = true;
				if (filesChanged) {
					this.dictionaryFiles.clear();
					for (File f : files) {
						CachedFile fileCache = new CachedFile(f, delay);
						Cache cache = fileCache.getData();
						this.dictionaryFiles.put(f.getAbsolutePath(), new Tuple2<CachedFile, CachedFile.Cache>(fileCache, cache));
					}
					changed = true;
				}
			}
			if (changed) {
				if (!justParsedTransforms)
					this.parseTransforms();
				if (transformsReferToDictionary)
					this.parseDictionaries();
				bindTransformsAndDictionaries();

				Map<String, AmiRelayDictionary> t = new HashMap<String, AmiRelayDictionary>(this.dictionaries);
				this.threadSafeTransforms = new AmiRelayTransforms(this.transformsFileFullPath, t,
						this.transforms.isEmpty() ? null : this.transforms.toArray(new AmiRelayTransform[this.transforms.size()]), debug);
				this.parseException = null;
				return true;
			}
		} catch (Throwable t) {
			if (throwOnError)
				throw OH.toRuntime(t);
			this.parseException = t;
			LH.info(log, "Ignoring changes to relay transforms due to parsing error: ", t.getMessage(), t);
		}
		return false;
	}

	private void parseTransforms() {
		this.transformsReferToDictionary = false;
		if (!this.lastData.exists()) {
			return;
		}
		String text = this.lastData.getText();
		LH.info(log, "Parsing Transform Table from: " + transformsFileFullPath);
		String[] lines = SH.splitLines(text);
		//		List<AmiRelayRoute> routes = new ArrayList<AmiRelayRoute>();
		this.transforms.clear();
		List<MappingEntry> t = new ArrayList<MappingEntry>();
		for (int i = 0; i < lines.length; i++) {
			String line = SH.trim(lines[i]);
			if (SH.isnt(line) || SH.startsWith(line, '#'))
				continue;
			try {
				AmiRelayTransform me = new AmiRelayTransform(this.transformsFileFullPath, i + 1, line);
				this.transforms.add(me);
				if (me.refersToDictionary())
					this.transformsReferToDictionary = true;
			} catch (Exception e) {
				throw new RuntimeException("Error at line " + (i + 1) + " in " + transformsFileFullPath + ": " + e.getMessage(), e);
			}
		}
		Collections.sort(this.transforms);
	}

	private void bindTransformsAndDictionaries() {
		for (AmiRelayTransform i : this.transforms) {
			i.bindToDictionaries(this.dictionaries, this.scriptManager);
		}
	}
	private void parseDictionaries() {
		Map<String, AmiRelayDictionary> sink = new LinkedHashMap<String, AmiRelayDictionary>();
		for (Entry<String, Tuple2<CachedFile, Cache>> i : this.dictionaryFiles.entrySet()) {
			LH.info(log, "Parsing Dictionary from: " + IOH.getFullPath(i.getValue().getA().getFile()));
			Cache b = i.getValue().getB();
			if (b == null || SH.isnt(b.getText()))
				continue;
			try {
				parseDictionaries(this.scriptManager.getMethodFactory(), i.getKey(), b.getText(), sink);
			} catch (ExpressionParserException e) {
				e.setExpression(b.getText());
				throw new RuntimeException("Error in " + IOH.getFullPath(i.getValue().getA().getFile()), e);
			}
		}
		bind(sink);
		this.dictionaries.clear();
		this.dictionaries.putAll(sink);
	}

	private void bind(Map<String, AmiRelayDictionary> sink) {
		for (AmiRelayDictionary i : sink.values())
			i.link(sink);
		Set<String> hs = new HashSet<String>();
		for (AmiRelayDictionary i : sink.values()) {
			hs.clear();
			i.assertNoCyclicDependencis(hs);
		}
		Set<String> remaining = new LinkedHashSet<String>(sink.keySet());
		while (remaining.size() > 0) {
			Set<String> remaining2 = new LinkedHashSet<String>();
			for (String i : remaining) {
				AmiRelayDictionary t = sink.get(i);
				if (CH.containsAny(remaining, t.getExtendsNames()))//can't compile yet because dependencies have not all been compiled
					remaining2.add(i);
				else
					t.compile(this.scriptManager.getSqlProcessor().getParser(), this.scriptManager.getMethodFactory());
			}
			remaining = remaining2;
		}
	}

	static private void parseDictionaries(MethodFactoryManager mf, String fileName, String text, Map<String, AmiRelayDictionary> sink) {
		StringCharReader scr = new StringCharReader(text);
		StringBuilder buf = new StringBuilder();
		JavaExpressionParser jep = new JavaExpressionParser();
		BasicDerivedCellParser dcp = new BasicDerivedCellParser(jep);
		for (;;) {
			JavaExpressionParser.sws(scr);
			if (scr.isEof())
				break;
			int position = scr.getCountRead();
			scr.readUntilAny(JavaExpressionParser.SPECIAL_CHARS_AND_DOT_NO_EOF, false, buf);
			String transformName = SH.trim(SH.toStringAndClear(buf));
			if (!AmiUtils.isValidVariableName(transformName, false, false))
				throw scr.newExpressionParserException("Invalid transform entry name: " + transformName);
			JavaExpressionParser.sws(scr);
			List<String> extendsList = new ArrayList<String>();
			if (scr.peak() != '{') {
				scr.readUntilAny(StringCharReader.WHITE_SPACE, true, buf);
				if (!SH.equalsIgnoreCase("extends", buf))
					throw scr.newExpressionParserException("Expecting { or extends ... ");
				buf.setLength(0);
				JavaExpressionParser.sws(scr);
				scr.readUntil('{', buf);
				for (String s : SH.split(',', SH.toStringAndClear(buf))) {
					s = SH.trim(s);
					if (!AmiUtils.isValidVariableName(s, false, false))
						throw scr.newExpressionParserException("Invalid entry in extends list: " + s);
					if (extendsList.contains(s))
						throw scr.newExpressionParserException("Duplicate entry in extends list: " + s);
					extendsList.add(s);
				}
			}
			BlockNode block = (BlockNode) jep.parseBlock(scr, false, false, false);
			BasicCalcTypes context = new BasicCalcTypes();
			Map<String, Class> types = new HashMap<String, Class>();
			//			Map<String, String> tgt2src = new HashMap<String, String>();
			Map<String, Node> tgt2expr = new HashMap<String, Node>();
			for (int i = 0; i < block.getNodesCount(); i++) {
				Node node = block.getNodeAt(i);
				if (node.getNodeCode() == Node.DECLARATION) {
					while (node != null) {
						DeclarationNode dn = (DeclarationNode) node;
						if (dn.getParam().getNodeCode() != Node.VARIABLE)
							throw new ExpressionParserException(node.getPosition(), "Declaration can not have assignment");
						String name = dn.getVarname();

						Class<?> type = dcp.forName(dn.getPosition(), mf, dn.getVartype());
						context.putType(name, type);
						types.put(name, type);
						node = dn.getNext();
					}
				} else if (node.getNodeCode() != Node.OPERATION) {
					throw new ExpressionParserException(node.getPosition(), "Expecting Operation");
				} else {
					OperationNode on = (OperationNode) node;
					if (on.getOp() != OperationNode.OP_EQ)
						throw new ExpressionParserException(node.getPosition(), "Expecting Assignment (=)");
					if (on.getLeft().getNodeCode() != Node.VARIABLE)
						throw new ExpressionParserException(node.getPosition(), "Expecting Assignment (=)");
					VariableNode left = (VariableNode) on.getLeft();
					//					if (on.getRight().getNodeCode() == Node.VARIABLE) {
					//						VariableNode right = (VariableNode) on.getRight();
					//						tgt2src.put(left.getVarname(), right.getVarname());
					//					} else {
					tgt2expr.put(left.getVarname(), on.getRight());
					//					}
				}
			}
			AmiRelayDictionary rd = new AmiRelayDictionary(fileName, text, position, transformName, extendsList, types, tgt2expr);
			AmiRelayDictionary existing = sink.get(rd.getName());
			if (existing != null)
				throw new ExpressionParserException(position, "Duplicate Dictionary '" + rd.getName() + "' in " + existing.getFileLocation());
			sink.put(rd.getName(), rd);
		}
	}
	public AmiRelayTransforms getThreadSafeTransforms() {
		return this.threadSafeTransforms;
	}
	public void setDebugMode(boolean b) {
		this.debug = b;
		this.threadSafeTransforms.setDebugMode(true);
	}
	public boolean getDebugMode() {
		return this.debug;
	}
	public Throwable getParseException() {
		return parseException;
	}

}
