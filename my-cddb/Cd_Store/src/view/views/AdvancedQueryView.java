package view.views;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.Disk;
import model.RequestToQueryHandler;
import model.ResultTableContentProvider;
import model.ResultTableLabelProvider;
import model.SearchesPriorityQueue;
import model.ShoppingCartContent;
import model.advanceSearchFieldValueBundle;
import model.TableViewsMap;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
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
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;
import org.osgi.framework.Bundle;

import view.Activator;
import view.FindAndDownloadCdImage;
import view.ICommandIds;
import controller.QueryId;

public class AdvancedQueryView extends ViewPart
{
	public static final String ID = "Cd_Store.advancedQueryView";
	Font boldFont = JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT);
	private TableViewer queryResultsViewer;
	private int dataTableId = 0;

	public void createPartControl(final Composite parent)
	{
		Composite mainComposite = new Composite(parent, SWT.BORDER);
		mainComposite.setLayout(new GridLayout(1, true));
		mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));	
		URL fileUrl = null;
		
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
		
		Composite topQueryComposite1 = new Composite(mainComposite, SWT.BORDER);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.numColumns = 3;
		topQueryComposite1.setLayout(layout);
		topQueryComposite1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label seperator = new Label(topQueryComposite1, SWT.NONE);
		GridData seperatorGd = new GridData();
		seperatorGd.horizontalSpan = 3;
		seperator.setLayoutData(seperatorGd);

		Label album = new Label(topQueryComposite1, SWT.NONE);
		album.setText("  Album Title: ");
		album.setFont(boldFont);
		final Text albumText = new Text(topQueryComposite1, SWT.BORDER | SWT.WRAP);
		albumText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		albumText.setTextLimit(30);
		Label pad1 = new Label(topQueryComposite1, SWT.NONE);
		pad1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label track = new Label(topQueryComposite1, SWT.NONE);
		track.setText("  Track Title: ");
		track.setFont(boldFont);
		final Text trackText = new Text(topQueryComposite1, SWT.BORDER | SWT.WRAP);
		trackText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		trackText.setTextLimit(50);
		Label pad3 = new Label(topQueryComposite1, SWT.NONE);
		pad3.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label artist = new Label(topQueryComposite1, SWT.NONE);
		artist.setText("  Artist Name: ");
		artist.setFont(boldFont);
		final Text artistText = new Text(topQueryComposite1, SWT.BORDER | SWT.WRAP);
		artistText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		artistText.setTextLimit(50);
		Label pad2 = new Label(topQueryComposite1, SWT.NONE);
		pad2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label genre = new Label(topQueryComposite1, SWT.NONE);
		genre.setText("  Genre: ");
		genre.setFont(boldFont);
		final Combo genreCombo = new Combo(topQueryComposite1, SWT.READ_ONLY);
		genreCombo.setItems(new String[] {"All_Music_Genres", "Blues", "Classical", "Country", "Data", "Folk", "Jazz", "NewAge", "Reggae", "Rock", "Soundtrack", "Misc"});
		genreCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		genreCombo.select(0);
		Label pad4 = new Label(topQueryComposite1, SWT.NONE);
		pad4.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Label year = new Label(topQueryComposite1, SWT.NONE);
		year.setText("  Year: ");
		year.setFont(boldFont);
		final Combo relationCombo = new Combo(topQueryComposite1, SWT.READ_ONLY);
		relationCombo.setItems(new String[] { "Greater Than", "Equals To", "Lesser Than" });
		relationCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		relationCombo.select(1);
		final Text yearText = new Text(topQueryComposite1, SWT.BORDER | SWT.WRAP);
		yearText.setTextLimit(4);
		
		Label seperator2 = new Label(topQueryComposite1, SWT.NONE);
		GridData seperatorGd2 = new GridData();
		seperatorGd2.horizontalSpan = 3;
		seperator2.setLayoutData(seperatorGd);
		
		Label pad5 = new Label(topQueryComposite1, SWT.NONE);
		pad5.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		final Button goButton = new Button(topQueryComposite1, SWT.PUSH);
		goButton.setText(" Go Go Go ");
		goButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		Label pad6 = new Label(topQueryComposite1, SWT.NONE);
		pad6.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		final ProgressBar progressBar = new ProgressBar (mainComposite, SWT.SMOOTH);
		progressBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		progressBar.setMaximum(50);
		progressBar.setVisible(false);
		
		Label message = new Label(mainComposite, SWT.NONE);
		message.setText("* After the search is complete, double click on the disk you want to add to the Shopping Cart");
		message.setFont(boldFont);
		GridData seperatorGd3 = new GridData();
		seperatorGd3.horizontalSpan = 3;
		message.setLayoutData(seperatorGd3);
		
		goButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				if(albumText.getText().equals("") && trackText.getText().equals("") && artistText.getText().equals("") && yearText.getText().equals(""))
				{
					View.displayErroMessage("You must fill at least one of the search criteria !!!");
				}
				else
				{
					dataTableId = QueryId.getId();
					TableViewsMap.addTable(dataTableId, null);
					List<advanceSearchFieldValueBundle> advancedSearchParaeters = new ArrayList<advanceSearchFieldValueBundle>();
					
					if(albumText.getText().equals("")==false)
						advancedSearchParaeters.add(new advanceSearchFieldValueBundle(RequestToQueryHandler.AdvancedSearchFields.ALBUM_TITLE, advanceSearchFieldValueBundle.Relation.EQUALS, albumText.getText()));
					if(trackText.getText().equals("")==false)
						advancedSearchParaeters.add(new advanceSearchFieldValueBundle(RequestToQueryHandler.AdvancedSearchFields.TRACK_TITLE, advanceSearchFieldValueBundle.Relation.EQUALS, trackText.getText()));
					if(artistText.getText().equals("")==false)
						advancedSearchParaeters.add(new advanceSearchFieldValueBundle(RequestToQueryHandler.AdvancedSearchFields.ARTIST_NAME, advanceSearchFieldValueBundle.Relation.EQUALS, artistText.getText()));
					
					advancedSearchParaeters.add(new advanceSearchFieldValueBundle(RequestToQueryHandler.AdvancedSearchFields.GENRE, advanceSearchFieldValueBundle.Relation.EQUALS, genreCombo.getText()));						
					
					if(yearText.getText().equals("") == false)
						advancedSearchParaeters.add(new advanceSearchFieldValueBundle(RequestToQueryHandler.AdvancedSearchFields.YEAR, (relationCombo.getSelectionIndex()==0 ? advanceSearchFieldValueBundle.Relation.GREATER : relationCombo.getSelectionIndex()==1 ? advanceSearchFieldValueBundle.Relation.EQUALS : advanceSearchFieldValueBundle.Relation.LESSER) , yearText.getText()));
								
					RequestToQueryHandler advanceSearch = new RequestToQueryHandler(dataTableId, RequestToQueryHandler.Priority.LOW_PRIORITY, RequestToQueryHandler.SearchType.ADVANCED, advancedSearchParaeters);
					SearchesPriorityQueue.addSearch(advanceSearch);
					
					progressBar.setVisible(true);
					goButton.setEnabled(false);
					final int maximum = progressBar.getMaximum();
					new Thread() {
						public void run()
						{
							boolean update = TableViewsMap.getUpdate(dataTableId);
							while(update == TableViewsMap.getUpdate(dataTableId))
							{
								for (final int[] i = new int[1]; i[0] <= maximum; i[0]++)
								{
									try
									{
										Thread.sleep(100);
									} catch (Throwable th)
									{
									}
									if (parent.getDisplay().isDisposed())
										return;
									parent.getDisplay().asyncExec(new Runnable() {
										public void run()
										{
											if (progressBar.isDisposed())
												return;
											progressBar.setSelection(i[0]);
										}
									});
								}
							}
							
							parent.getDisplay().asyncExec(new Runnable() {
								public void run()
								{		
									String[][] results = TableViewsMap.getData(dataTableId);
									if(results != null)
									{
										createColumns(queryResultsViewer, results);
										queryResultsViewer.setContentProvider(new ResultTableContentProvider());
										queryResultsViewer.setLabelProvider(new ResultTableLabelProvider());
										queryResultsViewer.setInput(results);
										queryResultsViewer.refresh();
									}
									else
									{
										View.displayErroMessage("Encountered problem with the requested search. Please try again.");
									}
									
									progressBar.setVisible(false);
								}
							});				
						}
					}.start();			
				}
			}
		});	
		
		// ********************* This whole block is Temporary
		/*Label pad8 = new Label(topQueryComposite1, SWT.NONE);
		pad8.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		final Button tempButton = new Button(topQueryComposite1, SWT.PUSH);
		tempButton.setText(" Temp Update map ");
		tempButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		Label pad9 = new Label(topQueryComposite1, SWT.NONE);
		pad9.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
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
				
				TableViewsMap.addTable(dataTableId, temp);				
			}
		});		
		*/
		
		Composite tableComposite = new Composite(mainComposite, SWT.BORDER);
		tableComposite.setLayout(new GridLayout(1, false));
		tableComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		queryResultsViewer = new TableViewer(tableComposite, SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		queryResultsViewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		queryResultsViewer.addDoubleClickListener(new IDoubleClickListener() {
			
			@Override
			public void doubleClick(DoubleClickEvent event)
			{
				IStructuredSelection sel = (IStructuredSelection) event.getSelection();
				String[] temp = (String[]) sel.getFirstElement();

				if(new File(localPathFinal + "/" + temp[0] + ".jpg").exists() == false)
				{
					FindAndDownloadCdImage find = new FindAndDownloadCdImage("\"" + temp[1] + "\" + \"" +  temp[2]  + "\"", temp[0] + ".jpg");
					find.start();	
					try{ find.join(4000); } catch (InterruptedException e){}
				}
	
				Disk selectedDisk;
				if(new File(localPathFinal + "/" + temp[0] + ".jpg").exists())		
					selectedDisk = new Disk(temp[0], temp[2], temp[1], temp[4], temp[3], temp[5], temp[6], "album covers\\" + temp[0] + ".jpg", null);
				else
					selectedDisk = new Disk(temp[0], temp[2], temp[1], temp[4], temp[3], temp[5], temp[6], "album covers\\empty_disk.jpg", null);;
				
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
	}

	// This will create the columns for the table
	private void createColumns(TableViewer viewer, String[][] data) {

		if(data != null)
		{			
			for (int i = 0; i < data[0].length; i++) 
			{
				TableViewerColumn column = new TableViewerColumn(viewer, SWT.NONE);
				
				if(i==0)
					column.getColumn().setText("Disk ID");
				else if(i==1)
					column.getColumn().setText("Name");
				else if(i==2)
					column.getColumn().setText("Artist");
				else if(i==3)
					column.getColumn().setText("Year");
				else if(i==4)
					column.getColumn().setText("Genre");
				else if(i==5)
					column.getColumn().setText("Length");
				else if(i==6)
					column.getColumn().setText("Price");
				
				if(i==0||i==3||i==5||i==6) 
					column.getColumn().setWidth(50);
				else if (i==1||i==2)
					column.getColumn().setWidth(190);
				else
					column.getColumn().setWidth(100);
				column.getColumn().setResizable(true);
				column.getColumn().setMoveable(true);
			}
			Table table = viewer.getTable();
			table.setHeaderVisible(true);
			table.setLinesVisible(true);
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
		image.dispose();
		return scaled;
	}
	
	public TableViewer getViewer() 
	{
		return queryResultsViewer;
	}
		
	public void setFocus()
	{
	}

}
