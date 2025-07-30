package com.f1.ami.web;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.container.ContainerTools;
import com.f1.utils.CH;
import com.f1.utils.FontReader;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.concurrent.HasherSet;
import com.f1.utils.impl.CaseInsensitiveHasher;

public class AmiWebFontsManager {

	private static final Logger log = LH.get();
	final private Map<CharSequence, String> fontNamesNoCase = new HasherMap<CharSequence, String>(CaseInsensitiveHasher.INSTANCE);
	final private TreeSet<String> fontNames = new TreeSet<String>();
	final private HashMap<String, Font> fontsToFont = new HashMap<String, Font>();
	final private HashMap<String, byte[]> fontData = new HashMap<String, byte[]>();
	final private Font defaultFont;
	final boolean graphicsAvailable;
	final private Map<String, String> fontMappings;

	public AmiWebFontsManager(ContainerTools tools) {

		Map<String, String> fm = SH.splitToMap(',', '=', tools.getOptional(AmiWebProperties.PROPERTY_AMI_FONT_JAVA_MAPPINGS, ""));
		HasherMap<String, String> fm2 = new HasherMap<String, String>(CaseInsensitiveHasher.INSTANCE);
		for (Entry<String, String> e : fm.entrySet())
			fm2.put(SH.trim(e.getKey()), SH.trim(e.getValue()));
		this.fontMappings = Collections.unmodifiableMap(fm2);
		final GraphicsEnvironment ge;
		final Font df;
		try {
			LH.info(log, "Testing Graphics...");
			ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			df = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB).createGraphics().getFont();
			LH.info(log, "Graphics Succesfully loaded.");
		} catch (Throwable e) {
			LH.warning(log, "Graphics Failed to load:", e);
			graphicsAvailable = false;
			this.defaultFont = null;
			return;
		}
		this.defaultFont = df;
		graphicsAvailable = true;
		List<File> fontFiles = AmiUtils.findFiles(tools.getOptional(AmiWebProperties.PROPERTY_AMI_FONT_FILES), true, false);
		Set<String> fontsInBrowser = (Set) new HasherSet<CharSequence>(CaseInsensitiveHasher.INSTANCE);
		CH.s(fontsInBrowser, SH.trimArray(SH.split(',', tools.getOptional(AmiWebProperties.PROPERTY_AMI_FONTS_IN_BROWSER))));
		this.fontNames.addAll(fontsInBrowser);
		for (File file : fontFiles) {
			try {
				final byte[] bytes = IOH.readData(file);
				Font font = FontReader.createFont(bytes);
				String fontName = font.getFontName();
				CH.putOrThrow(this.fontsToFont, fontName, font);
				ge.registerFont(font);
				if (!fontsInBrowser.contains(font.getFamily())) {
					this.fontNames.add(font.getFamily());
					CH.putOrThrow(this.fontData, fontName, bytes);
					LH.info(log, "Registered font '", fontName, "' at ", IOH.getFullPath(file) + " (will be loaded in browser)");
				} else
					LH.info(log, "Registered font '", fontName, "' at ", IOH.getFullPath(file));
			} catch (Exception e) {
				LH.info(log, "Registing font at ", IOH.getFullPath(file), " failed", e);
			}
		}
		for (String f : fontNames)
			this.fontNamesNoCase.put(f, f);

	}
	public boolean isGraphicsAvailable() {
		return this.graphicsAvailable;
	}

	public Font getDefaultFont() {
		return this.defaultFont;
	}

	public String findFont(String name) {
		return this.fontNamesNoCase.get(name);
	}

	public Set<String> getFonts() {
		return this.fontNames;
	}

	public Font getFont(String name) {
		return this.fontsToFont.get(name);
	}

	public Map<String, byte[]> getFontData() {
		return this.fontData;
	}

	//must be a threadsafe map
	public Map<String, String> getJavaFontMappings() {
		return fontMappings;
	}

}
