package view.views;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;

import model.Disk;
import model.MainViewSearchId;
import model.SearchParameters;
import model.ShoppingCartContent;
import model.TableViewsMap;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;
import org.osgi.framework.Bundle;

import view.Activator;
import view.FindAndDownloadCdImage;
import view.ICommandIds;

public class View extends ViewPart
{
	static Composite recordsCoversArea = null;
	
	public static final String ID = "Cd_Store.view";

	public void createPartControl(Composite parent)
	{

		// Fonts
		final Font boldFont = JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT);
		
		FontRegistry registry = new FontRegistry();
		FontDescriptor header_fdesc=FontDescriptor.createFrom("Tahoma",16,SWT.BOLD); 
		Font headerBoldFont = header_fdesc.createFont(registry.defaultFont().getDevice());
		
		FontDescriptor content_fdesc=FontDescriptor.createFrom("Tahoma",12,SWT.BOLD); 
		final Font contentBoldFont = content_fdesc.createFont(registry.defaultFont().getDevice());
		
		FontDescriptor recordsHeadr_fdesc=FontDescriptor.createFrom("Tahoma",14,SWT.BOLD); 
		final Font recordsHeadr_BoldFont = recordsHeadr_fdesc.createFont(registry.defaultFont().getDevice());
		
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

		final Combo categoriesCombo = new Combo(banner, SWT.NONE);
		categoriesCombo.setItems(new String[] {"All Music Genres", "Blues", "Classical", "Country", "Data", "Folk", "Jazz", "New Age", "Reggae", "Rock", "Soundtrack", "Misc"});
		categoriesCombo.select(0);

		final Text text = new Text(banner, SWT.BORDER);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Button goButton = new Button(banner, SWT.PUSH);
		goButton.setImage(resize(Activator.getImageDescriptor("icons/Blue_Go.gif").createImage(), 25, 15));
		goButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				SearchParameters.setGenre(categoriesCombo.getText());
				SearchParameters.setSearchString(text.getText());
				
				IHandlerService handlerService = (IHandlerService) getSite().getService(IHandlerService.class);
				try {
					handlerService.executeCommand(ICommandIds.CMD_OPEN_QUERY_VIEW, null);
				} catch (Exception ex) {
					throw new RuntimeException(ICommandIds.CMD_OPEN_QUERY_VIEW + " command not found");
				}

			}
		});

		@SuppressWarnings("unused")
		Label separator = new Label(banner, SWT.BORDER);

		
		Link loginLink = new Link(banner, SWT.NONE);
		loginLink.setText("<a>Advanced Search</a>");
		loginLink.setFont(boldFont);
		loginLink.addSelectionListener(new SelectionAdapter() {    
			public void widgetSelected(SelectionEvent e) 
			{
				IHandlerService handlerService = (IHandlerService) getSite().getService(IHandlerService.class);
				try {
					handlerService.executeCommand(ICommandIds.CMD_OPEN_ADVANCED_QUERY_VIEW, null);
				} catch (Exception ex) {
					throw new RuntimeException(ICommandIds.CMD_OPEN_ADVANCED_QUERY_VIEW + " command not found");
				}	
			}    
		});
		
		// Main area
		final Composite mainArea = new Composite(top, SWT.BORDER);
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
		textLabel.setText("\n Choose some predifined searches on your left.\n Create your own simple or advanced searches above.\n When you find your CD, you can add it to the Shopping Cart on your right just by clicking it.\n\n");	
		textLabel.setFont(contentBoldFont);
		textLabel.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		
		// ********************* This whole block is Temporary ******************************//
		final Button tempButton = new Button(mainArea, SWT.PUSH);
		tempButton.setText(" Temp Update map ");
		tempButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		tempButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				String[][] temp = new String[10][];
				temp[0] = new String[]{"id"+0, "Susan Boyle", "I Dreamed A Dream", "2009", "Rap", "2019", "9.99"};		
				temp[1] = new String[]{"id"+1, "Glee Cast", "Glee: The Music", "2009", "Rap", "2019", "9.99"};
				temp[2] = new String[]{"id"+2, "Vampire Weekend", "Contra", "2009", "Rap", "2019", "9.99"};
				temp[3] = new String[]{"id"+3, "Michael Buble", "Crazy Love", "2009", "Rap", "2019", "9.99"};
				temp[4] = new String[]{"id"+4, "Andrea Bocelli", "My Christmas", "2009", "Rap", "2019", "9.99"};
				temp[5] = new String[]{"id"+5, "The Beatles", "The Beatles Stereo Box Set", "2009", "Rap", "2019", "9.99"};
				temp[6] = new String[]{"id"+6, "Norah Jones", "The Fall", "2009", "Rap", "2019", "9.99"};
				temp[7] = new String[]{"id"+7, "Alicia Keys", "The Element of Freedom", "2009", "Rap", "2019", "9.99"};
				temp[8] = new String[]{"id"+8, "Taylor Swift", "Fearless", "2009", "Rap", "2019", "9.99"};
				temp[9] = new String[]{"id"+9, "Lady Antebellum", "Need You Now", "2009", "Rap", "2019", "9.99"};

				TableViewsMap.addTable(MainViewSearchId.getId(), temp);
			}
		});
		
		
		final Composite mainRecordsCoversArea = new Composite(mainArea, SWT.CENTER);
		mainRecordsCoversArea.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
		mainRecordsCoversArea.setBackgroundImage(backgroundImage);
		layout = new GridLayout();
		layout.numColumns = 1;
		mainRecordsCoversArea.setLayout(layout);
		
		createRecordsArea(boldFont, mainRecordsCoversArea, MainViewSearchId.getId(), recordsHeadr_BoldFont);	

		final ProgressBar progressBar = new ProgressBar(mainArea, SWT.SMOOTH);
		progressBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		progressBar.setMaximum(30);
		progressBar.setVisible(false);

		final int maximum = progressBar.getMaximum();
		new Thread() {
			public void run()
			{
				while (true)
				{
					while (MainViewSearchId.getId() == -1)
					{
						try
						{
							Thread.sleep(200);
						} catch (Throwable th)
						{}
					}

					mainArea.getDisplay().asyncExec(new Runnable() {
						public void run()
						{		
							progressBar.setVisible(true);
						}
					});
								
					final int dataTableId = MainViewSearchId.getId();
					boolean update = TableViewsMap.getUpdate(dataTableId);
					while (update == TableViewsMap.getUpdate(dataTableId))
					{
						for (final int[] i = new int[1]; i[0] <= maximum; i[0]++)
						{
							try
							{
								Thread.sleep(100);
							} catch (Throwable th)
							{
							}
							if (mainArea.getDisplay().isDisposed())
								return;
							mainArea.getDisplay().asyncExec(new Runnable() {
								public void run()
								{
									if (progressBar.isDisposed())
										return;
									progressBar.setSelection(i[0]);
								}
							});
						}
					}
						
					mainArea.getDisplay().asyncExec(new Runnable() {
						public void run()
						{
							createRecordsArea(boldFont, mainRecordsCoversArea, dataTableId, recordsHeadr_BoldFont);
							progressBar.setVisible(false);
						}
					});
					
					if(dataTableId == MainViewSearchId.getId())
						MainViewSearchId.setId(-1);
				}
			}
		}.start();
	}
	
	public static void displayErroMessage(final String errorMessage)
	{
		recordsCoversArea.getDisplay().asyncExec(new Runnable() {
			public void run()
			{
				Shell theShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
				MessageDialog.openError(theShell, "Error", errorMessage);
			}
		});
		
	}

	private void createRecordsArea(Font boldFont, Composite mainRecordsCoversArea, int searchId, Font font)
	{
		Disk[] dummyDisks = getDisks(searchId);
		
		if(recordsCoversArea!=null && recordsCoversArea.isDisposed() == false)
			recordsCoversArea.dispose();
	/*	
		Label recordsHaeadeLabel = new Label(mainRecordsCoversArea, SWT.WRAP);
		recordsHaeadeLabel.setText("Bla Bla Bla");	
		recordsHaeadeLabel.setFont(font);
		//recordsHaeadeLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_CYAN));
		GridData rhlgd = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		rhlgd.horizontalSpan = 1;
		recordsHaeadeLabel.setLayoutData(rhlgd);
	*/
		
		GridLayout layout;
		recordsCoversArea = new Composite(mainRecordsCoversArea, SWT.CENTER);
		recordsCoversArea.setLayoutData(new GridData(GridData.FILL_BOTH));
		recordsCoversArea.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_CYAN));
		layout = new GridLayout();
		layout.numColumns = 5;	
		layout.makeColumnsEqualWidth = true;
		recordsCoversArea.setLayout(layout);
		recordsCoversArea.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		for (int i = 0; i < dummyDisks.length; i++) 
		{
			Composite recordComposite = new Composite(recordsCoversArea, SWT.NONE);
			layout = new GridLayout();
			recordComposite.setLayout(layout);
			recordComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			Label lab = new Label(recordComposite, SWT.NONE);
			Image image;
			try
			{
				image = Activator.getImageDescriptor(dummyDisks[i].getCoverImage()).createImage();
			}
			catch (Exception e) 
			{
				image = Activator.getImageDescriptor("album covers/empty_disk.jpg").createImage();
			}
				
			lab.setImage(resize(image, 110, 110));
			lab.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			if(searchId != -1)
			{
				lab.setToolTipText("Title: " + dummyDisks[i].getTitle() + "\nArtist: " + dummyDisks[i].getArtist());
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
								handlerService.executeCommand(ICommandIds.CMD_UPDATE_SHOPPING_CART, null);
								
							} catch (Exception ex) 
							{
								throw new RuntimeException(ICommandIds.CMD_UPDATE_SHOPPING_CART + " not found");
							}
						}	
					}
				});
				
				Link recordLink = new Link(recordComposite, SWT.NONE);
				recordLink.setText("<a>" + dummyDisks[i].getTitle() + "</a>");
				recordLink.setToolTipText(dummyDisks[i].getTitle());
				recordLink.setFont(boldFont);
				recordLink.setData(dummyDisks[i]);
				recordLink.setLayoutData(new GridData(GridData.FILL));;
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
			
			//recordComposite.pack();
			recordComposite.layout();	
		}	
		
		recordsCoversArea.layout();
		//recordsCoversArea.pack();
		
		mainRecordsCoversArea.layout();
		//mainRecordsCoversArea.pack();
	}

	private Image resize(Image image, int width, int height)
	{
		Image scaled = new Image(Display.getDefault(), width, height);
		GC gc = new GC(scaled);
		gc.setAntialias(SWT.ON);
		gc.setInterpolation(SWT.HIGH);
		gc.drawImage(image, 0, 0, image.getBounds().width, image.getBounds().height, 0, 0, width, height);
		gc.dispose();
		image.dispose();
		return scaled;
	}

	private Disk[] getDisks(int searchId)
	{
		URL fileUrl = null;
		Disk[] myDisks = new Disk[10];
		
		Bundle bundle = Activator.getDefault().getBundle();
		Path path = new Path("album covers/empty_disk.jpg");
		URL localUrl = FileLocator.find(bundle, path, Collections.EMPTY_MAP);	
		try 
		{
			fileUrl = FileLocator.toFileURL(localUrl);
		} catch (IOException e) {}
		String localPath = fileUrl.getPath();
		localPath = localPath.substring(1);
		localPath = localPath.substring(0, localPath.lastIndexOf("/"));
		final String localPathFinal = localPath;
		
		if(searchId == -1)
		{
			for (int i = 0; i < myDisks.length; i++) 
			{
				myDisks[i] = new Disk("","", "", "", "", "", "", "album covers/empty_disk.jpg", null);
			}
		}
		else
		{
			String[][] allDisksAsString = TableViewsMap.getData(searchId);
			FindAndDownloadCdImage[] searchImage = new FindAndDownloadCdImage[10];
			for (int i = 0; i < 10; i++) 
			{
				searchImage[i] = new FindAndDownloadCdImage("\"" + allDisksAsString[i][1] + "\" + \"" +  allDisksAsString[i][2]  + "\"", allDisksAsString[i][0] + ".jpg");
				searchImage[i].start();
			}
			
			for (int i = 0; i < 10; i++) 
			{
				try
				{
					searchImage[i].join(700);
				} catch (InterruptedException e)
				{}
			}	
			
			for (int i = 0; i < 10; i++) 
			{
				if(new File(localPathFinal + "/" + allDisksAsString[i][0] + ".jpg").exists())		
					myDisks[i] = new Disk(allDisksAsString[i][0], allDisksAsString[i][2], allDisksAsString[i][1], allDisksAsString[i][4], allDisksAsString[i][3], allDisksAsString[i][5], allDisksAsString[i][6], "album covers/" + allDisksAsString[i][0] + ".jpg", null);
				else
					myDisks[i] = new Disk(allDisksAsString[i][0], allDisksAsString[i][2], allDisksAsString[i][1], allDisksAsString[i][4], allDisksAsString[i][3], allDisksAsString[i][5], allDisksAsString[i][6], "album covers\\empty_disk.jpg", null);				
			}
		}
			
		return myDisks;	
	}
	
	public void setFocus()
	{}
}
