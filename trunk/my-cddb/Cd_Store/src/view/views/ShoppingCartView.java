package view.views;

import java.util.ArrayList;
import model.Disk;
import model.ShoppingCartContent;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.OwnerDrawLabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;

import view.Activator;

public class ShoppingCartView extends ViewPart
{

	public static final String ID = "Cd_Store.shoppingCartView";
	private TableViewer viewer;
	private LineEntry[] entries;

	public void createPartControl(Composite parent)
	{
		Image backgroundImage = Activator.getImageDescriptor("icons/music013.gif").createImage();
		
		ShoppingCartContent.setContent(new ArrayList<Disk>());
		Composite top = new Composite(parent, SWT.NONE);
		top.setBackgroundImage(backgroundImage);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.numColumns = 1;
		top.setLayout(layout);

		// paint cart image
		Label l = new Label(top, SWT.CENTER);
		l.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		l.setBackgroundImage(backgroundImage);
		l.setImage(Activator.getImageDescriptor("icons/shop_cart.gif").createImage());

		// draw line
		Label shadow_sep = new Label(top, SWT.SEPARATOR | SWT.SHADOW_OUT | SWT.HORIZONTAL);
		shadow_sep.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Button proceedToCheckoutButton = new Button(top, SWT.PUSH);
		proceedToCheckoutButton.setImage(resize(Activator.getImageDescriptor("icons/proceed-to-checkout.gif").createImage(), 180, 28));
		proceedToCheckoutButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		proceedToCheckoutButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e)
			{
				ShoppingCartContent.clearContent();
				viewer.refresh();
			}
		});
		
		Button removeSelectedItemsButton = new Button(top, SWT.PUSH);
		removeSelectedItemsButton.setImage(resize(Activator.getImageDescriptor("icons/remove-from-cart.gif").createImage(), 186, 33));
		removeSelectedItemsButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		removeSelectedItemsButton.addSelectionListener(new SelectionAdapter() 
		{
			@SuppressWarnings("unchecked")
			public void widgetSelected(SelectionEvent e)
			{
				TableItem[] shoppingCartTableItems  = viewer.getTable().getItems();
				ArrayList tempList = new ArrayList<Boolean>();
				for (int i = 0; i < shoppingCartTableItems.length; i++) 
				{
					tempList.add(new Boolean(false));
					if(shoppingCartTableItems[i].getChecked())
						tempList.set(i, new Boolean(true));
				}
				
				boolean gotMoreItemsToRemove = true;
				while(gotMoreItemsToRemove)
				{
					gotMoreItemsToRemove = false;
					for (int j = 0; j < tempList.size(); j++) 
					{
						if((Boolean) tempList.get(j))
						{
							ShoppingCartContent.removeFromContent(j);
							tempList.remove(j);
							gotMoreItemsToRemove = true;
							break;
						}
					}
				}
				
				viewer.refresh();
			}
		});
		
		// The cart items table
		viewer = new TableViewer(top, SWT.CHECK | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		viewer.getTable().setBackgroundImage(backgroundImage);
		viewer.setContentProvider(new MyContentProvider());
		viewer.setLabelProvider(new MyLabelProvider());
		createColumns();
		viewer.setInput(ShoppingCartContent.getContent());

		GridData data = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.FILL_BOTH);
		viewer.getControl().setLayoutData(data);
		OwnerDrawLabelProvider.setUpOwnerDraw(viewer);
	}
	
	public TableViewer getViewer() {
		return viewer;
	}

	private class MyContentProvider implements IStructuredContentProvider
	{
		public Object[] getElements(Object inputElement)
		{
			
			entries = new LineEntry[ShoppingCartContent.getContent().size()];
			for (int i = 0; i < ShoppingCartContent.getContent().size(); i++) 
			{
				entries[i] = new LineEntry(ShoppingCartContent.getContent().get(i).toStringShort(), ShoppingCartContent.getContent().get(i).getCoverImage(), 100);
			}
	
			return entries;
		}

		public void dispose()
		{}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{}
	}
	
	public class MyLabelProvider extends OwnerDrawLabelProvider
	{		
		@Override
		protected void measure(Event event, Object element)
		{
			LineEntry line = (LineEntry) element;
			Point size = event.gc.textExtent(line.line);
			event.width = viewer.getTable().getColumn(event.index).getWidth();
			int lines = size.x / event.width + 1;
			event.height = (size.y * lines)+7;
		}

		@Override
		protected void paint(Event event, Object element)
		{
			LineEntry entry = (LineEntry) element;
			event.gc.setFont(entry.getFont());
			event.gc.drawImage(resize(Activator.getImageDescriptor(entry.image).createImage(), entry.getHeight(event), entry.getHeight(event)), event.x, event.y);
			event.gc.drawText(entry.line, event.x+entry.getHeight(event)+5, event.y, true);
		}
}
	
	class LineEntry
	{
		FontRegistry registry = new FontRegistry();
		FontDescriptor regular_fdesc=FontDescriptor.createFrom("Tahoma",9,SWT.BOLD);
		String line;
		String image;

		int columnWidth;

		/**
		 * Create a new instance of the receiver with name text constrained to a
		 * column of width.
		 * 
		 * @param text
		 * @param width
		 */
		LineEntry(String text, String image, int width) {
			line = text;
			this.image = image;
			columnWidth = width;
		}

		/**
		 * Get the height of the event.
		 * 
		 * @param index
		 * @return int
		 */
		public int getHeight(Event event)
		{
			event.gc.setLineWidth(columnWidth);
			return event.gc.textExtent(line).y;

		}

		/**
		 * Get the width of the event.
		 * 
		 * @param index
		 * @return
		 */
		public int getWidth(Event event)
		{
			return columnWidth;
		}

		/**
		 * Get the font we are using.
		 * 
		 * @return Font
		 */
		protected Font getFont()
		{
			return regular_fdesc.createFont(registry.defaultFont().getDevice());
		}
	}
	
	/**
	 * Create the columns to be used in the tree.
	 */
	private void createColumns() {
		TableLayout layout = new TableLayout();
		viewer.getTable().setLayout(layout);
		layout.addColumnData(new ColumnPixelData(220));
		viewer.getTable().setLinesVisible(true);
		TableColumn tc = new TableColumn(viewer.getTable(), SWT.NONE, 0);	
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
