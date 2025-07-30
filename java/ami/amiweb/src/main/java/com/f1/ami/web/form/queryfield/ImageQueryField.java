package com.f1.ami.web.form.queryfield;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amiscript.AmiDebugMessage;
import com.f1.ami.web.AmiWebResource;
import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.factory.AmiWebFormImageFieldFactory;
import com.f1.base.Bytes;
import com.f1.suite.web.portal.impl.form.FormPortletDivField;
import com.f1.utils.CH;
import com.f1.utils.FastByteArrayInputStream;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.encrypt.EncoderUtils;

public class ImageQueryField extends QueryField<FormPortletDivField> {
	private static final Logger log = LH.get();
	public static final String TYPE_RESOURCE = "__RESOURCE";
	public static final String TYPE_BASE64 = "__BASE64";
	public static String RESOURCE_BASE_PATH;

	private String type = "";
	private String file = "";

	public ImageQueryField(AmiWebFormImageFieldFactory factory, AmiWebQueryFormPortlet form) {
		super(factory, form, new FormPortletDivField(""));
		this.updateHtml(null);
	}

	@Override
	public void init(Map<String, Object> initArgs) {
		super.init(initArgs);
		this.setType(CH.getOrThrow(Caster_String.INSTANCE, initArgs, "img_srctype"));
		this.setFile(CH.getOrThrow(Caster_String.INSTANCE, initArgs, "img_file"));
		this.updateHtml(null);
	}

	@Override
	public boolean setValue(String key, Object value) {
		if (!super.setValue(key, value))
			return false;
		this.updateHtml(value);
		return true;
	}

	@Override
	public Class<?> getValueType() {
		return BufferedImage.class;
	}

	@Override
	public boolean setValue(Object value) {
		if (!super.setValue(value))
			return false;
		this.updateHtml(value);
		return true;
	}

	@Override
	public BufferedImage getValue() {
		try {
			String src = parseSrcAttribute(AmiUtils.s(super.getValue()));
			if (src != null) {
				if (TYPE_RESOURCE.equals(getType())) {
					AmiWebResource rsc = getService().getResourcesManager().getWebResource(getName());
					if (rsc == null || rsc.getBytes() == null)
						return null;
					try {
						return ImageIO.read(new FastByteArrayInputStream(rsc.getBytes()));
					} catch (Exception e) {
						return null;
					}
				} else if (TYPE_BASE64.equals(getType())) {
					String base64string = parseBase64FromSrc(src);
					return base64string != null ? ImageIO.read(new FastByteArrayInputStream(EncoderUtils.decode64(base64string))) : null;
				}
			}
		} catch (FileNotFoundException e) {
			if (getService().getDebugManager().shouldDebug(AmiDebugMessage.SEVERITY_WARNING))
				getService().getDebugManager().addMessage(new AmiDebugMessage(AmiDebugMessage.SEVERITY_WARNING, AmiDebugMessage.TYPE_DYNAMIC_AMISCRIPT, getAri(), null,
						"image file not found", CH.m("Error", e.getMessage()), e));
			LH.warning(log, logMe(), ": ", e.getMessage());
		} catch (Exception e) {
			LH.warning(log, logMe(), ": Exception getting value from FormImageField ", e);
		}
		return null;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}
	public void updateHtml(Object value) {
		//If the file is not emptystring
		FormPortletDivField field = this.getField();
		if (!("".equals(SH.trim(this.file)))) {
			StringBuilder sb = new StringBuilder();
			sb.append("<img src='");
			if ("__RESOURCE".equals(type)) {
				sb.append("../resources/").append(file);
				setType(TYPE_RESOURCE);
			} else {
				sb.append(file);
			}
			sb.append("' style='width:100%;height:100%;'>");
			field.setValue(sb.toString());
		} else if (value != null && value instanceof Bytes) {
			Bytes bi = (Bytes) value;
			StringBuilder sb = new StringBuilder();
			sb.append("<img style=\"width:100%;height:100%;\" src=\"data:image;base64,");
			if (bi != null)
				EncoderUtils.encode64(bi.getBytes(), sb);
			sb.append("\">");

			field.setValue(sb.toString());
			setType(TYPE_BASE64);
			//			}
		} else {
			field.setValue("<img style=\"width:100%;height:100%;\">");
		}
	}

	@Override
	public Map<String, Object> getJson(Map<String, Object> sink) {
		sink.put("img_srctype", this.getType());
		sink.put("img_file", this.getFile());
		return super.getJson(sink);
	}
	private String parseSrcAttribute(String imgHtmlTag) {
		String[] tokens = SH.split(" ", imgHtmlTag);
		for (String token : tokens)
			if (SH.startsWith(token, "src"))
				return token;
		return null;
	}
	private String parseBase64FromSrc(String src) {
		String output = SH.beforeLast(SH.afterFirst(src, "base64,", null), '"');
		return SH.trim(output);
	}
}
