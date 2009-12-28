package view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

public class ShoppingCartView extends ViewPart
{

	public static final String ID = "Cd_Store.shoppingCartView";

	public void createPartControl(Composite parent)
	{
		Composite top = new Composite(parent, SWT.NONE);
		top.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.numColumns = 1;
		top.setLayout(layout);

		//paint cart image
		Label l = new Label(top, SWT.CENTER);
		l.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		l.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		l.setImage(Activator.getImageDescriptor("icons/shop_cart.gif").createImage());
		
		//draw line
		Label shadow_sep = new Label(top, SWT.SEPARATOR | SWT.SHADOW_OUT | SWT.HORIZONTAL);
		shadow_sep.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		//paint add to cart button
		Button addToCartButton = new Button(top, SWT.PUSH | SWT.CENTER);
		addToCartButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		addToCartButton.setImage(resize(Activator.getImageDescriptor("icons/add-to-cart.gif").createImage(), 168, 32));
		
		// message contents
		Text text = new Text(top, SWT.MULTI | SWT.WRAP);
		text.setText("Shopping Cart View");
		text.setLayoutData(new GridData(GridData.FILL_BOTH));
		text.setBackgroundImage(Activator.getImageDescriptor("icons/music040.gif").createImage());
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
