package view;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor
{

	private IWorkbenchWindow window;
	private TrayItem trayItem;
	private Image trayImage;
	private final static String COMMAND_ID = "Cd_Store.exitCommand";
	private String title = "My CD Store";

	public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

	public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer)
	{
		return new ApplicationActionBarAdvisor(configurer);
	}

	public void preWindowOpen()
	{
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setShowCoolBar(false);
		configurer.setShowStatusLine(false);
		configurer.setTitle(title);
	}

	@Override
	public void postWindowOpen()
	{
		super.postWindowOpen();

		// Set Maximized
		getWindowConfigurer().getWorkbenchConfigurer().getWorkbench().getWorkbenchWindows()[0].getShell().setMaximized(true);
		
		// Set status line
		//IStatusLineManager statusline = getWindowConfigurer().getActionBarConfigurer().getStatusLineManager();
		//statusline.setErrorMessage("Not connected to DB");
		
		// Setup minimize feature
		window = getWindowConfigurer().getWindow();
		trayItem = initTaskItem(window);
		if (trayItem != null) {
			createMinimize();
			// Create exit and about action on the icon
			hookPopupMenu();
		}
	}

	//** All the bellow methods are for the minimized feature **//
	private void createMinimize()
	{
		window.getShell().addShellListener(new ShellAdapter() {
			public void shellIconified(ShellEvent e)
			{
				window.getShell().setVisible(false);
			}
		});

		trayItem.addListener(SWT.DefaultSelection, new Listener() {
			public void handleEvent(Event event)
			{
				Shell shell = window.getShell();
				if (!shell.isVisible()) {
					shell.setVisible(true);
					window.getShell().setMinimized(false);
				}
			}
		});
	}

	private void hookPopupMenu()
	{
		trayItem.addListener(SWT.MenuDetect, new Listener() {
			public void handleEvent(Event event)
			{
				Menu menu = new Menu(window.getShell(), SWT.POP_UP);

				// Creates a new menu item that terminates the program
				// when selected
				MenuItem exit = new MenuItem(menu, SWT.NONE);
				exit.setText("Exit");
				exit.addListener(SWT.Selection, new Listener() {
					public void handleEvent(Event event)
					{
						// Lets call our command
						IHandlerService handlerService = (IHandlerService) window.getService(IHandlerService.class);
						try {
							handlerService.executeCommand(COMMAND_ID, null);
						} catch (Exception ex) {
							throw new RuntimeException(COMMAND_ID);
						}
					}
				});
				// We need to make the menu visible
				menu.setVisible(true);
			}
		});
	}

	private TrayItem initTaskItem(IWorkbenchWindow window)
	{
		final Tray tray = window.getShell().getDisplay().getSystemTray();
		TrayItem trayItem = new TrayItem(tray, SWT.NONE);
		trayImage = AbstractUIPlugin.imageDescriptorFromPlugin("Cd_Store", "/icons/itunes.jpg").createImage();
		trayItem.setImage(trayImage);
		trayItem.setToolTipText(title);
		return trayItem;

	}

	public void dispose()
	{
		if (trayImage != null) {
			trayImage.dispose();
			trayItem.dispose();
		}
	}

}
