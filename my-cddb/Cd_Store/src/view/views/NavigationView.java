package view.views;

import java.util.ArrayList;

import model.DbConfiguration;
import model.MainViewSearchId;
import model.RequestToQueryHandler;
import model.SearchesPriorityQueue;
import model.TableViewsMap;
import model.RequestToQueryHandler.MusicGenres;

import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.DefaultToolTip;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import controller.QueryId;

import view.Activator;


public class NavigationView extends ViewPart
{
	public static final String ID = "Cd_Store.navigationView";
	private TreeViewer viewer;
	private int dataTableId = 0;

	class TreeObject
	{
		private String name;
		private TreeParent parent;

		public TreeObject(String name) {
			this.name = name;
		}

		public String getName()
		{
			return name;
		}

		public void setParent(TreeParent parent)
		{
			this.parent = parent;
		}

		public TreeParent getParent()
		{
			return parent;
		}

		public String toString()
		{
			return getName();
		}
	}

	class TreeParent extends TreeObject
	{
		private ArrayList<TreeObject> children;

		public TreeParent(String name) {
			super(name);
			children = new ArrayList<TreeObject>();
		}

		public void addChild(TreeObject child)
		{
			children.add(child);
			child.setParent(this);
		}

		public void removeChild(TreeObject child)
		{
			children.remove(child);
			child.setParent(null);
		}

		public TreeObject[] getChildren()
		{
			return (TreeObject[]) children.toArray(new TreeObject[children.size()]);
		}

		public boolean hasChildren()
		{
			return children.size() > 0;
		}
	}

	class ViewContentProvider implements IStructuredContentProvider, ITreeContentProvider
	{
		public void inputChanged(Viewer v, Object oldInput, Object newInput)
		{}

		public void dispose()
		{}

		public Object[] getElements(Object parent)
		{
			return getChildren(parent);
		}

		public Object getParent(Object child)
		{
			if (child instanceof TreeObject) {
				return ((TreeObject) child).getParent();
			}
			return null;
		}

		public Object[] getChildren(Object parent)
		{
			if (parent instanceof TreeParent) {
				return ((TreeParent) parent).getChildren();
			}
			return new Object[0];
		}

		public boolean hasChildren(Object parent)
		{
			if (parent instanceof TreeParent)
				return ((TreeParent) parent).hasChildren();
			return false;
		}
	}

	class ViewLabelProvider extends LabelProvider implements ITableFontProvider, ITableColorProvider
	{
		FontRegistry registry = new FontRegistry();
		
		public String getText(Object obj)
		{
			return obj.toString();
		}

		public Image getImage(Object obj)
		{
			String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
			if (obj instanceof TreeParent)
				imageKey = ISharedImages.IMG_OBJ_FOLDER;
			return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
		}

		@Override
		public Font getFont(Object obj, int columnIndex)
		{
			FontDescriptor header_fdesc=FontDescriptor.createFrom("Tahoma",11,SWT.BOLD); 
			FontDescriptor regular_fdesc=FontDescriptor.createFrom("Tahoma",10,SWT.BOLD);
			
			if(obj instanceof TreeParent)
				return header_fdesc.createFont(registry.defaultFont().getDevice());	
			
			return regular_fdesc.createFont(registry.defaultFont().getDevice());
		}

		@Override
		public Color getBackground(Object element, int columnIndex)
		{
			return null;
		}

		@Override
		public Color getForeground(Object element, int columnIndex)
		{
			if(element instanceof TreeParent)
				return Display.getCurrent().getSystemColor(SWT.COLOR_DARK_CYAN);
			
			return null;	
		}
	}

	/**
	 * We will set up a dummy model to initialize tree heararchy. In real code,
	 * you will connect to a real model and expose its hierarchy.
	 */
	private TreeObject createDummyModel()
	{
		TreeParent p1 = new TreeParent("POPULAR FEATURES");
		TreeObject to1 = new TreeObject("Latest CDs");
		TreeObject to2 = new TreeObject("Most Popular CDs");
		p1.addChild(to1);
		p1.addChild(to2);
		
		TreeParent p2 = new TreeParent("LATEST BY MUSIC GENRES");
		TreeObject to3 = new TreeObject("Blues");
		TreeObject to4 = new TreeObject("Classical");
		TreeObject to5 = new TreeObject("Country");
		TreeObject to6 = new TreeObject("Data");
		TreeObject to7 = new TreeObject("Folk");
		TreeObject to8 = new TreeObject("Jazz");
		TreeObject to9 = new TreeObject("NewAge");
		TreeObject to10 = new TreeObject("Reggae");
		TreeObject to11 = new TreeObject("Rock");
		TreeObject to12 = new TreeObject("Soundtrack");
		TreeObject to13 = new TreeObject("Misc");
		p2.addChild(to3);
		p2.addChild(to4);
		p2.addChild(to5);
		p2.addChild(to6);
		p2.addChild(to7);
		p2.addChild(to8);
		p2.addChild(to9);
		p2.addChild(to10);
		p2.addChild(to11);
		p2.addChild(to12);
		p2.addChild(to13);

		TreeParent root = new TreeParent("");
		root.addChild(p1);
		root.addChild(p2);
		return root;
	}

	
	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent)
	{	
		viewer = new TreeViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		viewer.getTree().setBackgroundImage(Activator.getImageDescriptor("icons/music013.gif").createImage());
		TreeColumn column = new TreeColumn(viewer.getTree(),SWT.NONE);
		column.setWidth(250);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setAutoExpandLevel(3);
		viewer.setInput(createDummyModel());
		
		if(viewer.getTree().getItem(0).getItem(0) != null)
			viewer.getTree().setSelection(viewer.getTree().getItem(0).getItem(0));
		
		hookDoubleClickCommand();
		
		DefaultToolTip toolTip = new DefaultToolTip(viewer.getControl(), ToolTip.RECREATE, false);
		toolTip.setText("Double click on the item to get the predefined query");
	}		

	private void hookDoubleClickCommand()
	{
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event)
			{
				if (DbConfiguration.isConnectedToDb())
				{
					dataTableId = QueryId.getId();
					TableViewsMap.addTable(dataTableId, null);
					MainViewSearchId.setId(dataTableId);
					
					if (viewer.getTree().getSelection()[0].getParentItem() != null)
					{
						if (viewer.getTree().getSelection()[0].getParentItem().getText().equals("POPULAR FEATURES"))
						{
							if (viewer.getTree().getSelection()[0].getText().equals("Latest CDs"))
							{
								RequestToQueryHandler top10LatestSearch = new RequestToQueryHandler(dataTableId,
										RequestToQueryHandler.Priority.HIGH_PRIORITY, RequestToQueryHandler.SearchType.TOP_10,
										RequestToQueryHandler.Top10Type.LATEST);
								
								System.out.println("SENDING NEW REQUEST FROM GUI:");
								System.out.println("ID = " + top10LatestSearch.getId());
								System.out.println("Query Type = " + top10LatestSearch.getTheQueryType());
								System.out.println("Search Type = " + top10LatestSearch.getSearchType());
								System.out.println("Top 10 Type = " + top10LatestSearch.getTop10Type());
								
								SearchesPriorityQueue.addSearch(top10LatestSearch);
							} else if (viewer.getTree().getSelection()[0].getText().equals("Most Popular CDs"))
							{
								RequestToQueryHandler top10PopularSearch = new RequestToQueryHandler(dataTableId,
										RequestToQueryHandler.Priority.HIGH_PRIORITY, RequestToQueryHandler.SearchType.TOP_10,
										RequestToQueryHandler.Top10Type.MOST_POPULAR);
								
								System.out.println("SENDING NEW REQUEST FROM GUI:");
								System.out.println("ID = " + top10PopularSearch.getId());
								System.out.println("Query Type = " + top10PopularSearch.getTheQueryType());
								System.out.println("Search Type = " + top10PopularSearch.getSearchType());
								System.out.println("Top 10 Type = " + top10PopularSearch.getTop10Type());
								
								SearchesPriorityQueue.addSearch(top10PopularSearch);
							}
						} else if (viewer.getTree().getSelection()[0].getParentItem().getText().equals("LATEST BY MUSIC GENRES"))
						{
							MusicGenres selectedMusicGenre = null;
							MusicGenres[] allMusicGenresd = RequestToQueryHandler.MusicGenres.values();
							for (int i = 0; i < allMusicGenresd.length; i++)
							{
								if (allMusicGenresd[i].toString().toLowerCase().equals(viewer.getTree().getSelection()[0].getText().toLowerCase()))
								{
									selectedMusicGenre = allMusicGenresd[i];
								}
							}
	
							RequestToQueryHandler top10Search = new RequestToQueryHandler(dataTableId, RequestToQueryHandler.Priority.HIGH_PRIORITY,
									RequestToQueryHandler.SearchType.TOP_10, selectedMusicGenre);
							
							System.out.println("SENDING NEW REQUEST FROM GUI:");
							System.out.println("ID = " + top10Search.getId());
							System.out.println("Query Type = " + top10Search.getTheQueryType());
							System.out.println("Search Type = " + top10Search.getSearchType());
							System.out.println("Top 10 Type = " + top10Search.getTop10Type());
							System.out.println("Genres = " + top10Search.getMusicGenre());
							
							SearchesPriorityQueue.addSearch(top10Search);
						}
					}
				} else
				{
					View.displayErroMessage("You cannot do anything before you connect to the DB.\nPlease connect to the DB via Database --> DB Configuration.");
				}
			}
		});
	}
	
	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus()
	{
		viewer.getControl().setFocus();
	}
}