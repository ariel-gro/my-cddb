package view;

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

public class UserView extends ViewPart
{
	public static final String ID = "Cd_Store.UserView";
	
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
		
		Label l = new Label(banner, SWT.WRAP);
		l.setText("Hello.");
		l.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		l.setFont(boldHeaderFont);
    
		final Link loginLink = new Link(banner, SWT.NONE);
		loginLink.setText("<a>Sign in</a>");
		loginLink.setFont(boldDefaultFont);
		loginLink.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		loginLink.addSelectionListener(new SelectionAdapter() {    
			public void widgetSelected(SelectionEvent e) {
				MessageDialog.openInformation(getSite().getShell(), "Sign in", "Imagine the sign in screen now.");
			}    
		});
		
		l = new Label(banner, SWT.NONE);
		l.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		l.setText("to get personalized recommendations. New customer?");
		//l.setFont(boldFont);
		
		final Link newCustomerLink = new Link(banner, SWT.WRAP);
		newCustomerLink.setText("<a>Start here.</a>");
		newCustomerLink.setFont(boldDefaultFont);
		newCustomerLink.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		newCustomerLink.addSelectionListener(new SelectionAdapter() {    
			public void widgetSelected(SelectionEvent e) {
				MessageDialog.openInformation(getSite().getShell(), "New User", "Imagine the new user screen now.");
			}    
		});
	}

	@Override
	public void setFocus()
	{}

}
