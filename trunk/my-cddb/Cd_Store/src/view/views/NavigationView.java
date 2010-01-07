package view.views;

import java.util.ArrayList;

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
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;

import view.Activator;

public class NavigationView extends ViewPart
{
	public static final String ID = "Cd_Store.navigationView";
	private TreeViewer viewer;

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
			FontDescriptor header_fdesc=FontDescriptor.createFrom("Tahoma",12,SWT.BOLD); 
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

		TreeParent p2 = new TreeParent("MUSIC GENRES");
		TreeObject to3 = new TreeObject("Country");
		TreeObject to4 = new TreeObject("Jazz");
		TreeObject to5 = new TreeObject("R&B");
		TreeObject to6 = new TreeObject("Rap & Hip Hop");
		TreeObject to7 = new TreeObject("Rock & Pop");
		TreeObject to8 = new TreeObject("Soundtracks");
		TreeObject to9 = new TreeObject("New Age");
		p2.addChild(to3);
		p2.addChild(to4);
		p2.addChild(to5);
		p2.addChild(to6);
		p2.addChild(to7);
		p2.addChild(to8);
		p2.addChild(to9);

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
				IHandlerService handlerService = (IHandlerService) getSite().getService(IHandlerService.class);
				try 
				{
					System.out.println("Double click works !!!" + "  " + viewer.getTree().getSelection()[0].getText());
					
					//handlerService.executeCommand("de.vogella.rcp.intro.editor.callEditor", null);
					
				} catch (Exception ex) 
				{
					throw new RuntimeException("de.vogella.rcp.intro.editor.callEditor not found");
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