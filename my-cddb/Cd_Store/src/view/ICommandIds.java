package view;

/**
 * Interface defining the application's command IDs.
 * Key bindings can be defined for specific commands.
 * To associate an action with a command, use IAction.setActionDefinitionId(commandId).
 *
 * @see org.eclipse.jface.action.IAction#setActionDefinitionId(String)
 */
public interface ICommandIds {

    public static final String CMD_OPEN_QUERY_VIEW = "Cd_Store.commands.openQueryViewAction";
    public static final String CMD_OPEN_ADVANCED_QUERY_VIEW = "Cd_Store.commands.openAdvancedQueryViewAction";
    public static final String CMD_OPEN_MESSAGE = "Cd_Store.commands.openMessage";
    public static final String CMD_UPDATE_SHOPPING_CART = "Cd_Store.commands.updateShoppingCart";
    
}
