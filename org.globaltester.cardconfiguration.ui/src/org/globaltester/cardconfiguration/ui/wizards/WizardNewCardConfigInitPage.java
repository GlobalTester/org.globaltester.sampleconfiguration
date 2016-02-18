package org.globaltester.cardconfiguration.ui.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.globaltester.cardconfiguration.CardConfig;
import org.globaltester.cardconfiguration.ui.CardConfigEditorWidget;

public class WizardNewCardConfigInitPage extends WizardPage {

	private CardConfig cardConfig;
	private CardConfigEditorWidget editorWidget;
	private WizardNewProjectCreationPage projectCreationPage;

	public WizardNewCardConfigInitPage(String pageName) {
		super(pageName);

		cardConfig = new CardConfig();
	}

	@Override
	public void createControl(Composite parent) {
		
		cardConfig.setName(projectCreationPage.getProjectName());
		
		// create main composite for this page
		Composite container = new Composite(parent, SWT.NONE);
		setControl(container);
		container.setLayout(new FillLayout());

		editorWidget = new CardConfigEditorWidget(container);
		editorWidget.setEditable(true);
		editorWidget.setNameEditable(false);
		editorWidget.setInput(cardConfig);
	}
	
	

	public CardConfig getCardConfig() {
		editorWidget.doSave();
		return cardConfig;
	}

	public void setProjectCreationPage(
			WizardNewProjectCreationPage newProjectCreationPage) {
		this.projectCreationPage = newProjectCreationPage;
		
	}

	public void setCardConfigName(String newName) {
		cardConfig.setName(newName);
		editorWidget.updateContents();
	}
	

}
