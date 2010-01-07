package view.views;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IconAndMessageDialog;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.swt.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

class CustomMessageDialog extends IconAndMessageDialog
{
	FontRegistry registry = new FontRegistry();
	FontDescriptor regular_fdesc=FontDescriptor.createFrom("Tahoma",9,SWT.BOLD);
	private Image image;
	private String title;

	/**
	 * MessageDialog constructor
	 * 
	 * @param parent the parent shell
	 */
	public CustomMessageDialog(Shell parent, String title, Image image, String message) {
		super(parent);

		this.message = message;
		this.image = image;
		this.title = title;
	}

	/**
	 * Closes the dialog
	 * 
	 * @return boolean
	 */
	public boolean close()
	{
		if (image != null)
			image.dispose();
		return super.close();
	}

	/**
	 * Creates the dialog area
	 * 
	 * @param parent the parent composite
	 * @return Control
	 */
	protected Control createDialogArea(Composite parent)
	{
		createMessageArea(parent);

		// Create a composite to hold the label
		Composite composite = new Composite(parent, SWT.NONE);
		GridData data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 2;
		composite.setLayoutData(data);
		composite.setLayout(new FillLayout());

		composite.getShell().setText(title);
		
		return composite;
	}

	protected Control createMessageArea(Composite composite) 
	{	
		super.createMessageArea(composite);
		messageLabel.setFont(regular_fdesc.createFont(registry.defaultFont().getDevice()));
		
		return composite;
	}
	
	/**
	 * Creates the buttons
	 * 
	 * @param parent the parent composite
	 */
	protected void createButtonsForButtonBar(Composite parent)
	{
		createButton(parent, IDialogConstants.YES_ID, IDialogConstants.YES_LABEL, true);
		createButton(parent, IDialogConstants.NO_ID, IDialogConstants.NO_LABEL, false);
	}

	/**
	 * Handles a button press
	 * 
	 * @param buttonId  the ID of the pressed button
	 */
	protected void buttonPressed(int returnCode)
	{
		setReturnCode(returnCode);
		close();
	}

	/**
	 * Gets the image to use
	 */
	protected Image getImage()
	{
		return image;
	}
}