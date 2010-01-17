package view.views;

import model.DbConfiguration;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import controller.connectionManager;

public class DbConfigDialog extends MessageDialog
{
	String serverIp = "";
	String serverPortString = "";
	String user = "";
	String password = "";

	public DbConfigDialog(String title, String dialogMessage) {
		super(null, title, null, dialogMessage, INFORMATION, new String[] { "OK", "Test Connection", "Exit",}, 0);
	}

	protected Control createCustomArea(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout(2, false));

		Label label = new Label(composite, SWT.NULL);
		label.setText("Server IP Address:");
		final Text serverIpText = new Text(composite, SWT.BORDER);
		serverIpText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		serverIpText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e)
			{
				serverIp = serverIpText.getText();
			}
		});

		label = new Label(composite, SWT.NULL);
		label.setText("Server Port:");
		final Text serverPort = new Text(composite, SWT.BORDER);
		serverPort.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		serverPort.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e)
			{
				serverPortString = serverPort.getText();
			}
		});
		
		label = new Label(composite, SWT.NULL);
		label.setText("User:");	
		final Text userText = new Text(composite, SWT.BORDER);
		userText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		userText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e)
			{
				user = userText.getText();
			}
		});

		label = new Label(composite, SWT.NULL);
		label.setText("Password:");
		final Text passwordText = new Text(composite, SWT.BORDER);
		passwordText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		passwordText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e)
			{
				password = passwordText.getText();
			}
		});
		
		// Set initial values
		if(DbConfiguration.getIpAddress().equals("") == false)
			serverIpText.setText(DbConfiguration.getIpAddress());
		if(DbConfiguration.getPort() != 0)
			serverPort.setText(DbConfiguration.getPort() + "");
		if(DbConfiguration.getUser().equals("") == false)
			userText.setText(DbConfiguration.getUser());
		if(DbConfiguration.getPassword().equals("") == false)
			passwordText.setText(DbConfiguration.getPassword());
			
		return null;
	}

	protected void buttonPressed(int buttonId)
	{
		setReturnCode(buttonId);
		
		switch (buttonId) {
		case 0:
			//connectionManager.start();		
			break;
		case 1:
			//connectionManager.start();	
			break;
		case 2:
			break;
		default:
			break;
		}

		close();	
	}

	public synchronized String getServerIp()
	{
		return serverIp;
	}

	public synchronized String getServerPortString()
	{
		return serverPortString;
	}

	public synchronized String getUser()
	{
		return user;
	}

	public synchronized String getPassword()
	{
		return password;
	}

	public void setMessage(String message)
	{
		messageLabel.setText(message);
	}
}