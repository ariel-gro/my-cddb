package view.commands;

import model.DbConfiguration;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.handlers.HandlerUtil;

import view.views.DbImportWizardMain;
import view.views.View;

public class OpenDbImportWizard extends AbstractHandler
{

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		if (DbConfiguration.isConnectedToDb())
		{
			DbImportWizardMain wizard = new DbImportWizardMain(false);
			WizardDialog dialog = new WizardDialog(HandlerUtil.getActiveShell(event), wizard);
			dialog.open();
		} else
		{
			View.displayErroMessage("You cannot do anything before you connect to the DB.\nPlease connect to the DB via Database --> DB Configuration.");
		}
		return null;
	}

}
