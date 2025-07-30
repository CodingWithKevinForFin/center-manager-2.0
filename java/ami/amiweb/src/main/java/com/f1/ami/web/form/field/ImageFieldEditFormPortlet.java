package com.f1.ami.web.form.field;

import java.util.Map;

import com.f1.ami.web.AmiWebResource;
import com.f1.ami.web.AmiWebResourcesManagerListener;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.factory.AmiWebFormImageFieldFactory;
import com.f1.ami.web.form.queryfield.ImageQueryField;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField.Option;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class ImageFieldEditFormPortlet extends BaseEditFieldPortlet<ImageQueryField> implements AmiWebResourcesManagerListener {
	private static long FILESELECT_SELECTFILE = -1L;
	private static long FILESELECT_FILENOTFOUND = -2L;
	private FormPortletSelectField<Long> fileSelectField;

	public ImageFieldEditFormPortlet(AmiWebFormImageFieldFactory factory, AmiWebQueryFormPortlet queryFormPortlet, int fieldX, int fieldY) {
		super(factory, queryFormPortlet, fieldX, fieldY);
		fileSelectField = new FormPortletSelectField<Long>(Long.class, "Image File");
		fileSelectField.setHasButton(true);

		getSettingsForm().addField(fileSelectField);
		fileSelectField.setLeftPosPx(BaseEditFieldPortlet.COL1_HORIZONTAL_POS_PX).setWidthPx(220);
		fileSelectField.setTopPosPx(380).setHeightPx(25);

		getService().getResourcesManager().addListener(this);
		rebuildImageSelectField();
	}
	private void rebuildImageSelectField() {
		Option<Long> option = fileSelectField.getValueOption();
		String selected = option == null ? null : option.getName();
		fileSelectField.clearOptions();
		fileSelectField.addOption(FILESELECT_SELECTFILE, "--Select a file--");
		long next = 1L;
		for (AmiWebResource i : getService().getResourcesManager().getWebResources())
			if (i.getName().endsWith(".svg") || (i.getImageHeight() != -1 && i.getImageHeight() != -1)) {
				fileSelectField.addOption(next, i.getName()).setCorrelationData(i);
				if (OH.eq(selected, i.getName()))
					fileSelectField.setValue(next);
				next++;
			}
	}
	@Override
	public void readFromField(ImageQueryField field) {
		String file = field.getFile();
		if (SH.is(file)) {
			boolean found = false;
			Iterable<Option<Long>> options = fileSelectField.getOptions();
			for (Option<Long> option : options) {
				if (option.getName().equals(field.getFile())) {
					found = true;
					fileSelectField.setValue(option.getKey());
					break;
				}
			}
			if (!found && fileSelectField.getValue() != FILESELECT_FILENOTFOUND) {
				fileSelectField.addOption(FILESELECT_FILENOTFOUND, "--File Not Found--" + file);
				fileSelectField.setValue(FILESELECT_FILENOTFOUND);
			}
		}
	}
	@Override
	public void writeToField(ImageQueryField queryField) {
		this.updateImage(queryField);
	}

	@Override
	public void close() {
		getService().getResourcesManager().removeListener(this);
		super.close();
	}

	public void resetDefaultImageSize() {
		byte vert = this.getVerticalSettingsToggle();
		byte horiz = this.getHorizontalSettingsToggle();
		boolean vstretch = this.getVerticalStretchCheckbox();
		boolean hstretch = this.getHorizontalStretchCheckbox();

		if (!vstretch && !hstretch && vert < 3 && horiz < 3) {
			Long fileId = fileSelectField.getValue();
			AmiWebService service = AmiWebUtils.getService(this.getManager());
			if (fileId != FILESELECT_SELECTFILE && fileId != FILESELECT_FILENOTFOUND) {
				if (fileId > 0) {
					service.getResourcesManager().getWebResources();
					AmiWebResource rsc = service.getResourcesManager().getWebResource(fileSelectField.getValueOption().getName());
					if (rsc != null && rsc.getImageHeight() != -1 && rsc.getImageWidth() != -1)
						this.setFieldDimensions(rsc.getImageWidth(), rsc.getImageHeight());
				} else {
					service.getResourcesManager().getResourcesDirectory();
				}
			}
		}
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (this.fileSelectField == field) {
			if (this.queryField != null) {
				this.updateImage(this.queryField);
				this.resetDefaultImageSize();
			}
		} else
			super.onFieldValueChanged(portlet, field, attributes);
	}
	private void updateImage(ImageQueryField queryField) {
		String type = "__RESOURCE";
		Long fileId = fileSelectField.getValue();
		if (fileId == FILESELECT_FILENOTFOUND) {
			String name = SH.afterFirst(this.fileSelectField.getValueOption().getName(), "--File Not Found--");
			queryField.setType(type);
			queryField.setFile(name);
			queryField.updateHtml(null);
			this.dmExpressionField.setVisible(false);
			this.dmExpressionField.setDisabled(true);
		} else if (fileId == FILESELECT_SELECTFILE) {
			this.dmExpressionField.setVisible(true);
			this.dmExpressionField.setDisabled(false);
		} else {
			String name = fileSelectField.getValueOption().getName();
			queryField.setType(type);
			queryField.setFile(name);
			queryField.updateHtml(null);
			this.dmExpressionField.setVisible(false);
			this.dmExpressionField.setDisabled(true);
		}
	}

	@Override
	public WebMenu createMenu(FormPortlet formPortlet, FormPortletField<?> field, int cursorPosition) {
		WebMenu r = super.createMenu(formPortlet, field, cursorPosition);
		if (field == this.fileSelectField) {
			if (r == null)
				r = new BasicWebMenu();
			r.add(new BasicWebMenuLink("Open Resource Manager (to upload images)", true, "resource_manager"));
		}
		return r;
	}

	@Override
	public void onContextMenu(FormPortlet portlet, String action, FormPortletField node) {
		if (node == this.fileSelectField) {
			AmiWebService service = AmiWebUtils.getService(this.getManager());
			service.getDesktop().showResourceManagerPortlet();
		}
		super.onContextMenu(portlet, action, node);
	}
	@Override
	public void onResourcesChanged() {
		rebuildImageSelectField();
	}

}
