package view;

import model.Disk;
import model.ShoppingCartContent;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
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

		// Fonts
		Font boldFont = JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT);
		
		FontRegistry registry = new FontRegistry();
		FontDescriptor header_fdesc=FontDescriptor.createFrom("Tahoma",16,SWT.BOLD); 
		Font headerBoldFont = header_fdesc.createFont(registry.defaultFont().getDevice());
		
		Image backgroundImage = Activator.getImageDescriptor("icons/music013.gif").createImage();

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

		
		Link loginLink = new Link(banner, SWT.NONE);
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
		mainArea.setBackgroundImage(backgroundImage);
		layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.numColumns = 1;
		mainArea.setLayout(layout);
		
		Label haeadeLabel = new Label(mainArea, SWT.WRAP);
		haeadeLabel.setBackgroundImage(backgroundImage);
		haeadeLabel.setText("Welcome to the best CD store in the world !!!");	
		haeadeLabel.setFont(headerBoldFont);
		haeadeLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_CYAN));
		haeadeLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		
		Label textLabel = new Label(mainArea, SWT.WRAP);
		textLabel.setBackgroundImage(backgroundImage);
		textLabel.setText("\nBla Bla \nBla Bla\nBla Bla\n\n\n\n\n");	
		textLabel.setFont(boldFont);
		textLabel.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Composite recordsArea = new Composite(mainArea, SWT.BORDER);
		recordsArea.setLayoutData(new GridData(GridData.FILL_BOTH));
		recordsArea.setBackgroundImage(backgroundImage);
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
		
		Disk[] dummyDisks = getDummyDisks();
		for (int i = 0; i < dummyDisks.length; i++) 
		{
			Composite recordComposite = new Composite(recordsCoversArea, SWT.NONE);
			recordsCoversArea.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
			layout = new GridLayout();
			recordComposite.setLayout(layout);
			
			Label lab = new Label(recordComposite, SWT.CENTER);
			lab.setImage(resize(Activator.getImageDescriptor(dummyDisks[i].getCoverImage()).createImage(), 110, 110));
			lab.setToolTipText(dummyDisks[i].getTitle() + "\n" + dummyDisks[i].getArtist());
			lab.setData(dummyDisks[i]);
			lab.addMouseListener(new MouseListener() {
				@Override
				public void mouseDoubleClick(MouseEvent e)
				{}

				@Override
				public void mouseDown(MouseEvent e)
				{		
				}

				@Override
				public void mouseUp(MouseEvent e)
				{
					Label selectedLabel = (Label) e.getSource();
					Disk selectedDisk = (Disk)selectedLabel.getData();
					if(new CustomMessageDialog(getSite().getShell(), "Add disk to Shopping Cart?", resize(Activator.getImageDescriptor(selectedDisk.getCoverImage()).createImage(), 140, 140), "Would you like to add the following disk to your Shopping Cart?\n\n" + selectedDisk.toString()).open() == IDialogConstants.YES_ID)
					{
						IHandlerService handlerService = (IHandlerService) getSite().getService(IHandlerService.class);
						ShoppingCartContent.addToContent(selectedDisk);
						try 
						{		
							handlerService.executeCommand("Cd_Store.updateShoppingCart", null);
							
						} catch (Exception ex) 
						{
							throw new RuntimeException("Cd_Store.updateShoppingCart not found");
						}
					}	
				}
			});
			
			Link recordLink = new Link(recordComposite, SWT.NONE);
			recordLink.setText("<a>" + dummyDisks[i].getTitle() + "</a>");
			recordLink.setFont(boldFont);
			recordLink.setData(dummyDisks[i]);
			recordLink.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
			recordLink.addSelectionListener(new SelectionAdapter() {    
				public void widgetSelected(SelectionEvent e) 
				{			
					IHandlerService handlerService = (IHandlerService) getSite().getService(IHandlerService.class);
					Link selectedLink = (Link) e.getSource();
					Disk selectedDisk = (Disk)selectedLink.getData();

					if(new CustomMessageDialog(getSite().getShell(), "Add disk to Shopping Cart?", resize(Activator.getImageDescriptor(selectedDisk.getCoverImage()).createImage(), 140, 140), "Would you like to add the following disk to your Shopping Cart?\n\n" + selectedDisk.toString()).open() == IDialogConstants.YES_ID)
					{
						ShoppingCartContent.addToContent(selectedDisk);
						try 
						{		
							handlerService.executeCommand("Cd_Store.updateShoppingCart", null);
							
						} catch (Exception ex) 
						{
							throw new RuntimeException("Cd_Store.updateShoppingCart not found");
						}
					}
				}    
			});
			
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

	private Disk[] getDummyDisks()
	{
		Disk[] myDisks = new Disk[10];
		
		for (int i = 0; i < myDisks.length; i++) 
		{
			myDisks[i] = new Disk("id" + i,"title" + i, "artist" + i, "genre" + i, "subGenre" + i, "year" + i, "totalTime" + i, "price" + i, "album covers/images_" + i + ".jpg", null);
		}
			
		return myDisks;	
	}
	
	public void setFocus()
	{}
}
