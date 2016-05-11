package org.globaltester.sampleconfiguration.ui;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;
import org.globaltester.sampleconfiguration.SampleConfig;
import org.globaltester.sampleconfiguration.SampleConfigManager;

public class SampleConfigEditor extends EditorPart implements IResourceChangeListener{

	public static final String ID = "org.globaltester.sampleconfiguration.ui.SampleConfigEditor";
	private SampleConfigEditorWidget widget;
	private Composite mainComposite;
	private SampleConfig config;

	@Override
	public void doSave(IProgressMonitor monitor) {
		widget.doSave();
	}

	@Override
	public void doSaveAs() {
		// XXX implement save as dialog
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		if ((input instanceof FileEditorInput)) {
			IFile file = ((FileEditorInput) input).getFile();
			if ((file == null) | (!file.exists())) {
				throw new PartInitException("Wrong input - File does not exist");
			}
			config = SampleConfigManager.get(file.getParent().getName());
			if(config == null) {
				throw new PartInitException(
						"Wrong Input - Selected resource does not represent a SampleConfiguration.");
			}
		}
		setSite(site);
		setInput(input);
		setDirty(false);
		setPartName(input.getName());
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
		
	}
	
	@Override
	public boolean isDirty() {
		return widget.wasChanged();
	}
	
	private void setDirty(boolean isDirty) {
		firePropertyChange(IEditorPart.PROP_DIRTY);
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout());

		mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout(1, false));
		mainComposite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));

		widget = new SampleConfigEditorWidget(mainComposite);
		widget.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		widget.setEditable(true);
		widget.setInput(getConfig());
		
	}
	
	private SampleConfig getConfig() {
		return config;
	}

	@Override
	public void setFocus() {
		mainComposite.setFocus();
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
//		widget.setInput(getConfig());
	}

}
