package view.views;

import model.DbConfiguration;
import model.RequestToQueryHandler;
import model.SearchesPriorityQueue;
import model.TableViewsMap;
import model.UserPassword;
import model.RequestToQueryHandler.SearchType;
import model.SqlStatement.QueryType;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.part.ViewPart;

import controller.QueryId;

public class UserView extends ViewPart
{
	public static final String ID = "Cd_Store.UserView";
	Link loginLink;
	Label textLabel;
	Link newCustomerLink;
	
	@Override
	public void createPartControl(Composite parent)
	{
		// setup bold font and white color
		Font boldHeaderFont = JFaceResources.getFontRegistry().getBold(JFaceResources.HEADER_FONT);	
		Font boldDefaultFont = JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT);
		
		Composite user = new Composite(parent, SWT.NONE);
		user.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		user.setLayout(layout);
		
		//user banner
		Composite banner = new Composite(user, SWT.NONE);
		banner.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL, GridData.VERTICAL_ALIGN_BEGINNING, true, false));
		banner.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		layout = new GridLayout();
		layout.marginHeight = 5;
		layout.marginWidth = 10;
		layout.numColumns = 4;
		banner.setLayout(layout);
		
		textLabel = new Label(banner, SWT.WRAP);
		textLabel.setText("Hello.");
		textLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		textLabel.setFont(boldHeaderFont);
    
		loginLink = new Link(banner, SWT.NONE);
		loginLink.setText("<a>Sign in</a>");
		loginLink.setFont(boldDefaultFont);
		loginLink.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		loginLink.addSelectionListener(new SelectionAdapter() {    
			public void widgetSelected(SelectionEvent e) {
				if (DbConfiguration.isConnectedToDb())
				{
					checkLogin();
				} else
				{
					View.displayErroMessage("You cannot do anything before you connect to the DB.\nPlease connect to the DB via Database --> DB Configuration.");
				}
			}    
		});
		
		textLabel = new Label(banner, SWT.NONE);
		textLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		textLabel.setText("to get personalized recommendations. New customer?");
		//l.setFont(boldFont);
		
		newCustomerLink = new Link(banner, SWT.WRAP);
		newCustomerLink.setText("<a>Start here.</a>");
		newCustomerLink.setFont(boldDefaultFont);
		newCustomerLink.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		newCustomerLink.addSelectionListener(new SelectionAdapter() {    
			public void widgetSelected(SelectionEvent e) {			
				if (DbConfiguration.isConnectedToDb())
				{
					addUser();
				} else
				{
					View.displayErroMessage("You cannot do anything before you connect to the DB.\nPlease connect to the DB via Database --> DB Configuration.");
				}
			}
		});
	}

	public boolean checkLogin()
	{
		final int dataTableId = QueryId.getId();
		TableViewsMap.addTable(dataTableId, null);
		RequestToQueryHandler regularSearch = new RequestToQueryHandler(dataTableId, RequestToQueryHandler.Priority.HIGH_PRIORITY, SearchType.GET_USERS);
		SearchesPriorityQueue.addSearch(regularSearch);
		final boolean update = TableViewsMap.getUpdate(dataTableId);
		
		LoginDialog loginDialog = new LoginDialog("Sign in", "Please login to the CD Store:");
		
		boolean login = false;
		while (!login) {
			int result = loginDialog.open();
			if (result == MessageDialog.OK) {
				try 
				{	
					if(loginDialog.getUser().equals("") || loginDialog.getPassword().equals(""))
					{
						MessageDialog.openInformation(null, "Could not login user", "User or Password cannot be empty - please try again");
					}
					else
					{	
						int time=0;
						while (update == TableViewsMap.getUpdate(dataTableId))
						{
							Thread.sleep(100);
							time++;
							
							if(time==50)
								break;
						}
							
						if(time<50)
						{
							String[][] allUsers = TableViewsMap.getData(dataTableId);
							for (int i = 0; i < allUsers.length; i++)
							{
								for (int j = 0; j < allUsers[i].length; j++)
								{
									System.out.println(allUsers[i][j]);
								}
							}
							for (int i = 0; i < allUsers.length; i++)
							{
								if(allUsers[i][1].toLowerCase().equals(loginDialog.getUser().toLowerCase()))
									if(allUsers[i][2].toLowerCase().equals(loginDialog.getPassword().toLowerCase()))
									{
										login = true;
										UserPassword.setLoggedIn(true);
										UserPassword.setId(Integer.parseInt(allUsers[i][0]));
										UserPassword.setUser(loginDialog.getUser());
										UserPassword.setPassword(loginDialog.getPassword());
										loginLink.setText("<a>" + UserPassword.getUser() + "</a>");
										textLabel.setText("");
										newCustomerLink.setText("");
									}
							}
							
							if(!login)
								MessageDialog.openInformation(null, "Could not login user", "Could not login - please try again");
						}
						else
						{
							MessageDialog.openInformation(null, "Timed Out", "Timed out while trying to authenticate user - please try again");
						}
					}
				} catch (Exception connectionProblem) {
					MessageDialog.openInformation(null, "Could not login user", connectionProblem.toString());

					return false;
				}
			} else 
			{
				return false; // user cancelled
			}
		}
		return true;
	}
	
	public boolean addUser()
	{
		NewUserDialog newUserDialog = new NewUserDialog("New User", "Please fill your details:");

		boolean login = false;
		while (!login) {
			int result = newUserDialog.open();
			if (result == MessageDialog.OK) {
				try 
				{	
					if(newUserDialog.getUser().equals("") || newUserDialog.getPassword().equals("") || newUserDialog.getVerifyPassword().equals(""))
					{
						MessageDialog.openInformation(null, "Could not Add User", "User or Password cannot be empty - please try again");
					}
					else
					{	
						if(newUserDialog.getPassword().equals(newUserDialog.getVerifyPassword()) == false)
						{
							MessageDialog.openError(null, "Passwords don't match", "The passwords you've entered do not match. Please try again.");
							continue;
						}
						
						UserPassword.setUser(newUserDialog.getUser());
						UserPassword.setPassword(newUserDialog.getPassword());
						
						int dataTableId = QueryId.getId();
						
						System.out.println("Sending single insert to qh from GUI: ID="+dataTableId+" User="+UserPassword.getUser() + " Password="+UserPassword.getPassword());
						RequestToQueryHandler regularSearch = new RequestToQueryHandler(dataTableId, RequestToQueryHandler.Priority.HIGH_PRIORITY,
								QueryType.INSERT_SINGLE, RequestToQueryHandler.SingleInsertType.ADD_USER, new String[]{UserPassword.getUser(), UserPassword.getPassword()});
						SearchesPriorityQueue.addSearch(regularSearch);
						
						return true;
					}
					
				} catch (Exception connectionProblem) {
					MessageDialog.openInformation(null, "Could not Add User", connectionProblem.toString());
					return false;
				}
			} else {
				return false; // user canceled
			}
		}
		return true;
	}
	
	@Override
	public void setFocus()
	{}

}
