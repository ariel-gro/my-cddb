package view.views;

import org.eclipse.jface.wizard.Wizard;

public class DbImportWizardMain extends Wizard
{

	private DbImportWizardPageOne one;
	private DbImportWizardPageTwo two;

	public DbImportWizardMain() {
		super();
		setNeedsProgressMonitor(true);
	}

	@Override
	public void addPages()
	{
		one = new DbImportWizardPageOne();
		two = new DbImportWizardPageTwo();
		addPage(one);
		addPage(two);
	}

	@Override
	public boolean performFinish()
	{

		// What to do when finish is pressed.

		return true;
	}
}