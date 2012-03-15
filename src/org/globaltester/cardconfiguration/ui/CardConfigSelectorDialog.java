package org.globaltester.cardconfiguration.ui;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.globaltester.cardconfiguration.CardConfig;

public class CardConfigSelectorDialog extends Dialog {

	private Shell dialog;
	private CardConfigSelector selectorWidget;
	private CardConfigEditorWidget editorWidget;
	private int status;
	protected CardConfig selectedConfig;
	

	public CardConfigSelectorDialog(Shell parentShell) {
		super(parentShell);
	}

	public int open() {
		createDialog();
		status = Window.CANCEL;

		dialog.open();
		Display display = getParent().getDisplay();
		while (!dialog.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		return status;
	}

	private void createDialog() {
		dialog = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		dialog.setSize(400, 300);
		dialog.setText("Select CardConfig");
		configureLayout(dialog);
		createSelector(dialog);
		createEditorWidget(dialog);
		createButtons(dialog);
		
	}

	private void configureLayout(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		parent.setLayout(layout);
	}

	private void createSelector(Composite parent) {
		selectorWidget = new CardConfigSelector(parent, CardConfigSelector.BTN_NEW);
		selectorWidget.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		selectorWidget.addSelectionListener(new SelectionListener() {
		      public void widgetSelected(SelectionEvent e) {
		        editorWidget.setInput(selectorWidget.getSelectedConfig());
		      }

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		    });
		selectedConfig = selectorWidget.getSelectedConfig();
	}

	private void createEditorWidget(Composite parent) {
		editorWidget = new CardConfigEditorWidget(parent);
		editorWidget.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		editorWidget.setEditable(false);
		editorWidget.setInput(getSelectedCardConfig());
	}

	private void createButtons(Composite parent) {
		new Label(parent, SWT.NONE);
		
		Button okButton = new Button(parent, SWT.PUSH);
		okButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		okButton.setText("OK");
		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				status = Window.OK;
				selectedConfig = selectorWidget.getSelectedConfig();
				dialog.close();
			}
		});
	}

	public CardConfig getSelectedCardConfig() {
		return selectedConfig;
	}
}