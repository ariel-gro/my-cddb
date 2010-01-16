package view;

/**
 * Interface defining the application's command IDs.
 * Key bindings can be defined for specific commands.
 * To associate an action with a command, use IAction.setActionDefinitionId(commandId).
 *
 * @see org.eclipse.jface.action.IAction#setActionDefinitionId(String)
 */
public interface ICommandIds {

    public static final String CMD_OPEN_QUERY_VIEW = "Cd_Store.view.commands.openQueryViewAction";
    public static final String CMD_OPEN_ADVANCED_QUERY_VIEW = "Cd_Store.view.commands.openAdvancedQueryViewAction";
    public static final String CMD_OPEN_MESSAGE = "Cd_Store.view.commands.openMessage";
    public static final String CMD_UPDATE_SHOPPING_CART = "Cd_Store.view.commands.updateShoppingCart";
    public static final String CMD_UPDATE_ADVANCED_QUERY_VIEW = "Cd_Store.view.commands.updateAdvancedQueryView";
    public static final String CMD_OPEN_DB_CONFIG_DIALOG = "Cd_Store.view.commands.openDbConfigDialogAction"; 
    public static final String CMD_OPEN_DB_IMPORT_WIZARD= "Cd_Store.view.commands.openDbImportWizard";
}
