package view;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;

public class View extends ViewPart
{

	public static final String ID = "Cd_Store.view";

	public void createPartControl(Composite parent)
	{

		// setup bold font
		Font boldFont = JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT);

		Composite top = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.numColumns = 1;
		top.setLayout(layout);

		// Top banner
		Composite banner = new Composite(top, SWT.NONE);
		banner.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		layout = new GridLayout();
		layout.marginHeight = 5;
		layout.marginWidth = 10;
		layout.numColumns = 6;
		banner.setLayout(layout);

		Label l = new Label(banner, SWT.WRAP);
		l.setText("Search: ");
		l.setFont(boldFont);

		Combo categoriesCombo = new Combo(banner, SWT.NONE);
		categoriesCombo.setItems(new String[] { "All Music Genres", "Country", "Jazz", "R&B", "Rap & Hip Hop", "Rock & Pop", "Soundtracks" });
		categoriesCombo.select(0);

		Text text = new Text(banner, SWT.BORDER);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Button goButton = new Button(banner, SWT.PUSH);
		goButton.setImage(resize(Activator.getImageDescriptor("icons/Blue_Go.gif").createImage(), 25, 15));
		goButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				IHandlerService handlerService = (IHandlerService) getSite().getService(IHandlerService.class);
				try {
					handlerService.executeCommand(ICommandIds.CMD_OPEN, null);
				} catch (Exception ex) {
					throw new RuntimeException(ICommandIds.CMD_OPEN + " command not found");
				}

			}
		});

		@SuppressWarnings("unused")
		Label separator = new Label(banner, SWT.BORDER);

		
		final Link loginLink = new Link(banner, SWT.NONE);
		loginLink.setText("<a>Advanced Search</a>");
		loginLink.setFont(boldFont);
		//loginLink.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		loginLink.addSelectionListener(new SelectionAdapter() {    
			public void widgetSelected(SelectionEvent e) 
			{
				IHandlerService handlerService = (IHandlerService) getSite().getService(IHandlerService.class);
				try {
					handlerService.executeCommand(ICommandIds.CMD_OPEN, null);
				} catch (Exception ex) {
					throw new RuntimeException(ICommandIds.CMD_OPEN + " command not found");
				}	
			}    
		});
		
		// Main area
		Composite mainArea = new Composite(top, SWT.BORDER);
		mainArea.setLayoutData(new GridData(GridData.FILL_BOTH));
		mainArea.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.numColumns = 1;
		mainArea.setLayout(layout);
		
		Composite textArea = new Composite(mainArea, SWT.BORDER);
		layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.numColumns = 1;
		textArea.setLayout(layout);
		textArea.setLayoutData(new GridData(GridData.FILL_BOTH));
		textArea.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		Label generalTextLabel = new Label(textArea, SWT.WRAP);
		generalTextLabel.setBackgroundImage(Activator.getImageDescriptor("icons/music040.gif").createImage());
		generalTextLabel.setText("Here we'll write all sorts of crap.\nbla\nbla\nbla\nbla\nbla\nbla\nbla\n\n\n\n\n\n\n\n\n\n\n");
		generalTextLabel.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Composite recordsArea = new Composite(mainArea, SWT.BORDER);
		recordsArea.setLayoutData(new GridData(GridData.FILL_BOTH));
		recordsArea.setBackgroundImage(Activator.getImageDescriptor("icons/music040.gif").createImage());
		//recordsArea.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.numColumns = 1;
		recordsArea.setLayout(layout);
		
		Composite recordsCoversArea = new Composite(recordsArea, SWT.CENTER);
		recordsCoversArea.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
		recordsCoversArea.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
		layout = new GridLayout();
		layout.numColumns = 5;	
		layout.makeColumnsEqualWidth = true;
		recordsCoversArea.setLayout(layout);
		for (int i = 1; i <= 10; i++) 
		{
			Label lab = new Label(recordsCoversArea, SWT.CENTER);
			lab.setImage(resize(Activator.getImageDescriptor("album covers/images_" + i + ".jpg").createImage(), 110, 110));
			lab.setToolTipText("images_" + i + ".jpg");
		}		
	}

	private Image resize(Image image, int width, int height)
	{
		Image scaled = new Image(Display.getDefault(), width, height);
		GC gc = new GC(scaled);
		gc.setAntialias(SWT.ON);
		gc.setInterpolation(SWT.HIGH);
		gc.drawImage(image, 0, 0, image.getBounds().width, image.getBounds().height, 0, 0, width, height);
		gc.dispose();
		image.dispose(); // don't forget about me!
		return scaled;
	}

	public void setFocus()
	{}
}
