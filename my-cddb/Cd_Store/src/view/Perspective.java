package view;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Perspective implements IPerspectiveFactory {

	/**
	 * The ID of the perspective as specified in the extension.
	 */
	public static final String ID = "Cd_Store.perspective";

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		layout.setFixed(true);
		
		layout.addStandaloneView(NavigationView.ID,  false, IPageLayout.LEFT, 0.2f, editorArea);
		layout.addStandaloneView(UserView.ID,  false, IPageLayout.TOP, 0.05f, editorArea);	
		IFolderLayout folder = layout.createFolder("Welcome", IPageLayout.LEFT, 0.77f, editorArea);
		folder.addPlaceholder(QueryView.ID + ":*");
		folder.addView(View.ID);
		
		layout.addStandaloneView(ShoppingCartView.ID, false, IPageLayout.LEFT, 0.05f, editorArea);
		
	}
}
