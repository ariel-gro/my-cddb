package view.commands;

import model.DbConfiguration;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import view.ICommandIds;
import view.views.DbConfigDialog;

public class OpenDbConfigDialogAction extends Action {

    public OpenDbConfigDialogAction(IWorkbenchWindow window) 
    {
        super();
      
        // The id is used to refer to the action in a menu or toolbar
        setId(ICommandIds.CMD_OPEN_DB_CONFIG_DIALOG);
        
        // Associate the action with a pre-defined command, to allow key bindings.
        setActionDefinitionId(ICommandIds.CMD_OPEN_DB_CONFIG_DIALOG);
        setImageDescriptor(view.Activator.getImageDescriptor("/icons/sample3.gif"));
    }

	public void run()
	{
		String serverIp;
		String serverPortString;
		int serverPort;
		String user;
		String password;
		String dbName;
		DbConfigDialog dbConfigDialog = new DbConfigDialog("DB Configuration", "Please enter your database connection details");

		boolean inputOk = false;
		while (!inputOk) {
			int result = dbConfigDialog.open();
			if (result == MessageDialog.OK) {
				try {
					serverIp = dbConfigDialog.getServerIp();
					serverPortString = dbConfigDialog.getServerPortString();
					user = dbConfigDialog.getUser();
					password = dbConfigDialog.getPassword();
					dbName = dbConfigDialog.getDbNameString();
					
					DbConfiguration.setIpAddress(serverIp);
					DbConfiguration.setUser(user);
					DbConfiguration.setPassword(password);
					DbConfiguration.setDb(dbName);
					
					if(isValidIp(serverIp) == false)
					{
						MessageDialog.openError(null, "Bad Input", "Server Ip Adderss is illegal: " + serverIp);
						continue;
					}
					
					try {
						serverPort = Integer.parseInt(serverPortString);
						if (serverPort > 65535 || serverPort < 0) 
						{
							MessageDialog.openError(null, "Bad Input", "Server port is illegal: " + serverPortString);
							continue;
						}
					} catch (Exception e) {
						MessageDialog.openError(null, "Bad Input", "Server port is illegal: " + serverPortString);
						continue;
					}
					
					DbConfiguration.setPort(serverPort);
					
					return;
				} catch (Exception connectionProblem) {
					MessageDialog.openError(null, "Error", "Some error occured. Please try again.");
				}
			} else {
				return; // user canceled
			}
		}
	}
	
	private boolean isValidIp(String theIp)
	{
		if (theIp == null || theIp == "") 
			return false;

		String[] fields = theIp.split("\\.");

		if (fields.length != 4) 
			return false;

		try
		{
			for (int i = 0; i < fields.length; i++)
			{
				int num = Integer.parseInt(fields[i]);
				if (num > 255 || num < 0) 
					return false;
			}
		}
		catch (NumberFormatException e)
		{
			return false;
		}
		
		return true;
	}
}