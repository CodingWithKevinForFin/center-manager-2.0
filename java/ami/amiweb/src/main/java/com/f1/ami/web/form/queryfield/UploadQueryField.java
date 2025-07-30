package com.f1.ami.web.form.queryfield;

import java.util.Map;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.factory.AmiWebFormFileUploadFieldFactory;
import com.f1.base.Bytes;
import com.f1.suite.web.portal.impl.form.FormPortletFileUploadField;
import com.f1.suite.web.portal.impl.form.FormPortletFileUploadField.FileData;
import com.f1.utils.CH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.encrypt.EncoderUtils;

public class UploadQueryField extends QueryField<FormPortletFileUploadField> {
	public static final String SUFFIXES[] = new String[] { AmiWebQueryFormPortlet.SUFFIX_FILENAME, AmiWebQueryFormPortlet.SUFFIX_FILETYPE, AmiWebQueryFormPortlet.SUFFIX_FILETEXT,
			AmiWebQueryFormPortlet.SUFFIX_FILEDATA, AmiWebQueryFormPortlet.SUFFIX_FILEDATA64 };
	public static final Class CLASSES[] = new Class[] { String.class, String.class, String.class, byte[].class, String.class };

	public UploadQueryField(AmiWebFormFileUploadFieldFactory factory, AmiWebQueryFormPortlet form) {
		super(factory, form, new FormPortletFileUploadField(""));
	}

	@Override
	public void init(Map<String, Object> initArgs) {
		super.init(initArgs);
		getField().setUploadFileButtonText(CH.getOr(Caster_String.INSTANCE, initArgs, "ufbt", null));
		getField().setUploadUrlButtonText(CH.getOr(Caster_String.INSTANCE, initArgs, "uubt", null));
	}

	@Override
	public int getVarsCount() {
		return 5;
	}

	@Override
	public String getSuffixNameAt(int i) {
		return SUFFIXES[i];
	}
	@Override
	public Class<?> getVarTypeAt(int i) {
		switch (i) {
			case 0:
				return String.class;
			case 1:
				return String.class;
			case 2:
				return String.class;
			case 3:
				return Bytes.class;
			case 4:
				return String.class;
			default:
				return super.getVarTypeAt(i);
		}
	}

	@Override
	public Object getValue(int i) {
		FileData fdata = getField().getValue();
		if (fdata == null)
			return null;

		switch (i) {
			case 0:
				return fdata.getName();
			case 1:
				return fdata.getContentType();
			case 2:
				return fdata.getDataText();
			case 3:
				return Bytes.valueOf(fdata.getData());
			case 4:
				return fdata.getDataEncoded64();
			default:
				return super.getValue(i);
		}
	}
	@Override
	public boolean setValue(String key, Object value) {
		String key2 = SH.stripPrefix(key, getName(), true);
		FileData current = getField().getValue();
		String contentType = null;
		Bytes data = null;
		String name = null;
		if (current != null) {
			name = current.getName();
			contentType = current.getContentType();
			data = Bytes.valueOf(current.getData());
		}
		if (AmiWebQueryFormPortlet.SUFFIX_FILENAME.equals(key2)) {
			name = AmiUtils.snn(value, "null");
		} else if (AmiWebQueryFormPortlet.SUFFIX_FILETYPE.equals(key2)) {
			contentType = AmiUtils.snn(value, "null");
		} else if (AmiWebQueryFormPortlet.SUFFIX_FILETEXT.equals(key2)) {
			data = Bytes.valueOf(AmiUtils.snn(value, "null").getBytes());
		} else if (AmiWebQueryFormPortlet.SUFFIX_FILEDATA.equals(key2)) {
			data = (Bytes) value;
		} else if (AmiWebQueryFormPortlet.SUFFIX_FILEDATA64.equals(key2)) {
			data = Bytes.valueOf(EncoderUtils.decode64((CharSequence) value));
		}
		byte[] bytes = Bytes.getBytes(data);
		this.getField().setValue(new FileData(name, contentType, bytes == null ? null : new String(bytes), bytes));
		return true;
	}
	@Override
	public Map<String, Object> getJson(Map<String, Object> sink) {
		FormPortletFileUploadField field = getField();
		CH.putNoNull(sink, "ufbt", field.getUploadFileButtonText());
		CH.putNoNull(sink, "uubt", field.getUploadUrlButtonText());
		return super.getJson(sink);
	}

}