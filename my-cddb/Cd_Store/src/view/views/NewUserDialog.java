package view.views;

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

public class NewUserDialog extends MessageDialog
{
	String user = "";
	String password = "";
	String verifyPassword = "";

	public NewUserDialog(String title, String dialogMessage) {
		super(null, title, null, dialogMessage, QUESTION, new String[] { "Add User", "Exit",}, 0);
	}

	protected Control createCustomArea(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout(2, false));

		Label label = new Label(composite, SWT.NULL);
		label.setText("User Name:");

		final Text userText = new Text(composite, SWT.BORDER);
		userText.setTextLimit(30);
		userText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		userText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e)
			{
				user = userText.getText();
			}
		});

		label = new Label(composite, SWT.NULL);
		label.setText("Enter Password:");

		final Text passwordText = new Text(composite, SWT.BORDER | SWT.PASSWORD);
		passwordText.setTextLimit(20);
		passwordText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		passwordText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e)
			{
				password = passwordText.getText();
			}
		});
		
		label = new Label(composite, SWT.NULL);
		label.setText("Verify Password:");

		final Text verifyPasswordText = new Text(composite, SWT.BORDER | SWT.PASSWORD);
		verifyPasswordText.setTextLimit(20);
		verifyPasswordText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		verifyPasswordText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e)
			{
				verifyPassword = verifyPasswordText.getText();
			}
		});
		
		return null;
	}

	public String getUser()
	{
		return user;
	}

	public String getPassword()
	{
		return password;
	}
	
	public String getVerifyPassword()
	{
		return verifyPassword;
	}

	public void setMessage(String message)
	{
		messageLabel.setText(message);
	}
}