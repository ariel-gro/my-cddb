package view;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

import view.commands.OpenAdvancedQueryViewAction;
import view.commands.OpenDbConfigDialogAction;
import view.commands.OpenQueryViewAction;
import view.views.AdvancedQueryView;
import view.views.QueryView;

/**
 * An action bar advisor is responsible for creating, adding, and disposing of the
 * actions added to a workbench window. Each window will be populated with
 * new actions.
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

    // Actions - important to allocate these only in makeActions, and then use them
    // in the fill methods.  This ensures that the actions aren't recreated
    // when fillActionBars is called with FILL_PROXY.
//  private IWorkbenchAction exitAction;
//  private IWorkbenchAction aboutAction;
    private OpenQueryViewAction openQueryViewAction;
    private OpenAdvancedQueryViewAction openAdvancedQueryViewAction;
    private Action messagePopupAction;
    private Action dbConfigAction;
    

    public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
        super(configurer);
    }
    
    protected void makeActions(final IWorkbenchWindow window) {
        // Creates the actions and registers them.
        // Registering is needed to ensure that key bindings work.
        // The corresponding commands keybindings are defined in the plugin.xml file.
        // Registering also provides automatic disposal of the actions when
        // the window is closed.

     //   exitAction = ActionFactory.QUIT.create(window);
     //   register(exitAction);
        
    //    aboutAction = ActionFactory.ABOUT.create(window);
    //    register(aboutAction);
        
        openQueryViewAction = new OpenQueryViewAction(window, "Open Another Query View", QueryView.ID);
        register(openQueryViewAction);
        
        openAdvancedQueryViewAction = new OpenAdvancedQueryViewAction(window, "Open Another Advanced Query View", AdvancedQueryView.ID);
        register(openAdvancedQueryViewAction);
        
        messagePopupAction = new MessagePopupAction("Import DB", window);
        register(messagePopupAction);
        
        dbConfigAction = new OpenDbConfigDialogAction(window);
        register(dbConfigAction);
    }
}
