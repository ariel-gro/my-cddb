package view;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import controller.connectionManager;
import controller.queryHandler;

/**
 * This workbench advisor creates the window advisor, and specifies
 * the perspective id for the initial window.
 */
public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {
	
    public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        return new ApplicationWorkbenchWindowAdvisor(configurer);
    }

	public String getInitialWindowPerspectiveId() {
		return Perspective.ID;
	} 
	
	public boolean preShutdown()
	{  	  
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();  
		String dialogBoxTitle = "Question";  
		String question = "Are you sure you want to close the CD Store application?";  
		
		if(MessageDialog.openQuestion(shell, dialogBoxTitle, question))
		{
			connectionManager.quit();
			queryHandler.quit();
			try
			{
				Thread.sleep(1500);
			} catch (InterruptedException e)
			{}
			return true;
		}
		else
		{
			return false;
		}	  
	} 
	
}
