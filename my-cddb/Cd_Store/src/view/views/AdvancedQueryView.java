package view.views;

import java.util.ArrayList;
import java.util.List;

import model.RequestToQueryHandler;
import model.SearchesPriorityQueue;
import model.advanceSearchFieldValueBundle;
import model.tableViewsMap;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import controller.QueryId;

public class AdvancedQueryView extends ViewPart
{
	public static final String ID = "Cd_Store.advancedQueryView";
	Font boldFont = JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT);
	private static TableViewer queryResultsViewer;
	private int dataTableId = 0;

	public void createPartControl(Composite parent)
	{
		Composite mainComposite = new Composite(parent, SWT.BORDER);
		mainComposite.setLayout(new GridLayout(1, true));
		mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
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
		albumText.setTextLimit(50);
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
		Button goButton = new Button(topQueryComposite1, SWT.PUSH);
		goButton.setText("Go Go Go");
		goButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		Label pad6 = new Label(topQueryComposite1, SWT.NONE);
		pad6.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		goButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				dataTableId = QueryId.getId();
				List<advanceSearchFieldValueBundle> advancedSearchParaeters = new ArrayList<advanceSearchFieldValueBundle>();
				
				if(albumText.getText().equals("")==false)
					advancedSearchParaeters.add(new advanceSearchFieldValueBundle(RequestToQueryHandler.AdvancedSearchFields.ALBUM_TITLE, advanceSearchFieldValueBundle.Relation.EQUALS, albumText.getText()));
				if(trackText.getText().equals("")==false)
					advancedSearchParaeters.add(new advanceSearchFieldValueBundle(RequestToQueryHandler.AdvancedSearchFields.TRACK_TITLE, advanceSearchFieldValueBundle.Relation.EQUALS, trackText.getText()));
				if(artistText.getText().equals("")==false)
					advancedSearchParaeters.add(new advanceSearchFieldValueBundle(RequestToQueryHandler.AdvancedSearchFields.ARTIST_NAME, advanceSearchFieldValueBundle.Relation.EQUALS, artistText.getText()));
				
				advancedSearchParaeters.add(new advanceSearchFieldValueBundle(RequestToQueryHandler.AdvancedSearchFields.GENRE, advanceSearchFieldValueBundle.Relation.EQUALS, albumText.getText()));						
				advancedSearchParaeters.add(new advanceSearchFieldValueBundle(RequestToQueryHandler.AdvancedSearchFields.YEAR, (relationCombo.getSelectionIndex()==0 ? advanceSearchFieldValueBundle.Relation.GREATER : relationCombo.getSelectionIndex()==1 ? advanceSearchFieldValueBundle.Relation.EQUALS : advanceSearchFieldValueBundle.Relation.LESSER) , yearText.getText()));
							
				RequestToQueryHandler advanceSearch = new RequestToQueryHandler(dataTableId, RequestToQueryHandler.Priority.LOW_PRIORITY, RequestToQueryHandler.SearchType.ADVANCED, advancedSearchParaeters);
				SearchesPriorityQueue.addSearch(advanceSearch);
				
				tableViewsMap.addTable(dataTableId, new String[][]{new String[]{}});		
			}
		});
			
		Composite tableComposite = new Composite(mainComposite, SWT.BORDER);
		tableComposite.setLayout(new GridLayout(1, false));
		tableComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		queryResultsViewer = new TableViewer(tableComposite, SWT.FULL_SELECTION | SWT.BORDER);
		queryResultsViewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		
	}

	public void setFocus()
	{
	}

}
