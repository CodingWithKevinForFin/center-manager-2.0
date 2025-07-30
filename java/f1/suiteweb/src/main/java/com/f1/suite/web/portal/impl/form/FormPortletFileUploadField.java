package com.f1.suite.web.portal.impl.form;

import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.suite.web.JsFunction;
import com.f1.suite.web.portal.impl.ConfirmDialog;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletFileUploadField.FileData;
import com.f1.utils.CH;
import com.f1.utils.ContentType;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.encrypt.EncoderUtils;

public class FormPortletFileUploadField extends FormPortletField<FileData> implements ConfirmDialogListener {
	private static final Logger log = LH.get();

	private String uploadFileButtonText = null;
	private String uploadUrlButtonText = null;

	;

	public FormPortletFileUploadField(String title) {
		super(FileData.class, title);
	}

	@Override
	public boolean onUserValueChanged(Map<String, String> attributes) {
		String action = attributes.get("action");
		if ("urlClicked".equals(action)) {
			showUrlDialog();
			return false;
		} else if ("upload".equals(action)) {
			String type = CH.getOrThrow(Caster_String.INSTANCE, attributes, "fileType");
			String name = CH.getOrThrow(Caster_String.INSTANCE, attributes, "fileName");
			if (attributes.containsKey("fileData")) {
				String data = attributes.get("fileData");
				setValueNoFire(new FileData(name, type, data, data == null ? null : data.getBytes()));
			} else {
				byte[] data = (byte[]) ((Map) attributes).get("fileData64");
				setValueNoFire(new FileData(name, type, data == null ? null : new String(data), data));
			}
			return true;
		}
		return false;
	}

	private void showUrlDialog() {
		ConfirmDialogPortlet cdp = new ConfirmDialogPortlet(getForm().generateConfig(), "Url:", ConfirmDialogPortlet.TYPE_OK_CANCEL, this,
				new FormPortletTextField("").setMaxChars(10000).setWidth(FormPortletTextField.WIDTH_STRETCH));
		if (getValue() != null && SH.startsWithIgnoreCase(getValue().getName(), "http", 0))
			cdp.setInputFieldValue(getValue().getName());
		getForm().getManager().showDialog("Upload From Url", cdp);
	}

	@Override
	public String getjsClassName() {
		return "FileUploadField";
	}

	@Override
	public String getJsValue() {
		FileData v = getValue();
		return v == null ? "" : v.getName();
	}

	@Override
	public void updateJs(StringBuilder pendingJs) {
		if (hasChanged(MASK_CONFIG))
			new JsFunction(pendingJs, jsObjectName, "setButtonsText").addParamQuoted(this.uploadFileButtonText).addParamQuoted(this.uploadUrlButtonText).end();
		super.updateJs(pendingJs);
	}

	public String getData() {
		return this.getValue() == null ? null : this.getValue().getDataText();
	}

	public String getUploadFileButtonText() {
		return uploadFileButtonText;
	}

	public void setUploadFileButtonText(String uploadFileeButtonText) {
		if (OH.eq(this.uploadFileButtonText, uploadFileeButtonText))
			return;
		this.uploadFileButtonText = uploadFileeButtonText;
		flagConfigChanged();
	}

	public String getUploadUrlButtonText() {
		return uploadUrlButtonText;
	}

	public void setUploadUrlButtonText(String uploadUrlButtonText) {
		if (OH.eq(this.uploadUrlButtonText, uploadUrlButtonText))
			return;
		this.uploadUrlButtonText = uploadUrlButtonText;
		flagConfigChanged();
	}

	public static class FileData {
		private final String name;
		private final String contentType;
		private final String text;
		private byte[] data;
		private String encoded64;

		public FileData(String name, String contentType, String text, byte[] data) {
			this.name = name;
			this.contentType = contentType;
			this.data = data;
			this.text = text;
		}

		//Only supplied if type is text
		public String getDataText() {
			return text;
		}

		public String getDataEncoded64() {
			if (encoded64 == null && getData() != null)
				this.encoded64 = EncoderUtils.encode64(getData());
			return encoded64;
		}

		public String getName() {
			return name;
		}

		public String getContentType() {
			return contentType;
		}

		public byte[] getData() {
			if (data == null && text != null)
				data = text.getBytes();
			return data;
		}

	}

	@Override
	public boolean onButton(ConfirmDialog source, String id) {
		if (ConfirmDialog.ID_YES.equals(id)) {
			String url = (String) source.getInputFieldValue();
			String name = url;
			byte[] data;
			String type = ContentType.BINARY.getMimeType();
			if (SH.is(url)) {
				if (url.startsWith("data:")) {
					try {
						int semicolon = SH.indexOf(url, ";base64,", 5);
						type = SH.substring(url, 5, semicolon);
						data = EncoderUtils.decode64(url, semicolon + ";base64,".length(), url.length());
						name = "data:" + type;
					} catch (Exception e) {
						getForm().getManager().showAlert("Invalid data format", e);
						LH.info(log, "Could not download from: ", url, e);
						return false;
					}
				} else {
					try {
						Map<String, List<String>> headers = new HashMap<String, List<String>>();
						data = IOH.doGet(new URL(url), null, headers, true, 10000);
						List<String> types = headers.get("Content-Type");
						if (CH.isntEmpty(types))
							type = types.get(0);
					} catch (Exception e) {
						getForm().getManager().showAlert(e.getMessage());
						LH.info(log, "Could not download from: ", url, e);
						return false;
					}
				}
				setValue(new FileData(name, type, new String(data), data));
				getForm().fireFieldValueChangedTolisteners(this, Collections.EMPTY_MAP);
			}
		}
		return true;
	}

	@Override
	public FormPortletFileUploadField setName(String name) {
		super.setName(name);
		return this;
	}

}
