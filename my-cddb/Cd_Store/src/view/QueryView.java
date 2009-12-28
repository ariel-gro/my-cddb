package view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

public class QueryView extends ViewPart
{

public static final String ID = "Cd_Store.queryView";
	
	public void createPartControl(Composite parent) {
		Composite top = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		top.setLayout(layout);
		
		// message contents
		Text text = new Text(top, SWT.MULTI | SWT.WRAP);
		text.setText("Query View");
		text.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	public void setFocus() {}

}
