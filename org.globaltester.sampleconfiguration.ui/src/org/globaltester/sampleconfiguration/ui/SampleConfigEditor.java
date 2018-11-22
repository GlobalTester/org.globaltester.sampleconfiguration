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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;
import org.globaltester.logging.legacy.logger.GtErrorLogger;
import org.globaltester.sampleconfiguration.SampleConfig;

public class SampleConfigEditor extends EditorPart implements IResourceChangeListener{

	public static final String ID = "org.globaltester.sampleconfiguration.ui.SampleConfigEditor";
	private SampleConfigEditorWidget widget;
	private Composite mainComposite;
	private SampleConfig config;
	private boolean dirty;

	@Override
	public void doSave(IProgressMonitor monitor) {
		widget.doSave();
		setDirty(false);
	}

	@Override
	public void doSaveAs() {
		// IMPL implement save as dialog
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		if ((input instanceof FileEditorInput)) {
			IFile file = ((FileEditorInput) input).getFile();
			if ((file == null) | (!file.exists())) {
				throw new PartInitException("Wrong input - File does not exist");
			}
			config = SampleConfig.getSampleConfigForProject(file.getProject());
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
		return dirty;
	}
	
	private void setDirty(boolean isDirty) {
		dirty = isDirty;
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
		
		Listener listener = new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				setDirty(true);
			}
		};
		
		widget = new SampleConfigEditorWidget(mainComposite, listener);
		widget.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		widget.setActive(true);
		widget.setInput(getConfig());
		setDirty(false);

		// update editor contents once UI is ready (hopefully this is late enough for all ProtocolFactories to be present)
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					GtErrorLogger.log(Activator.PLUGIN_ID, e);
				}
				//do nothing if widgets are already disposed
				if (widget == null) {
					return;
				}
				widget.updateContents();
				setDirty(false);
			}
		});
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
		//SampleConfig is managed mostly in memory
	}

}
