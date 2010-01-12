package view.views;

import model.DbConfiguration;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class DbImportWizardPageOne extends WizardPage
{
	private Text text;
	private Composite container;

	public DbImportWizardPageOne() {
		super("Import DB - First Page");
		setTitle("Import DB");
		setDescription("This wizard imports the archived freeDB file to the Database");
	}

	@Override
	public void createControl(Composite parent)
	{
		container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;
		Label label = new Label(container, SWT.NULL);
		label.setText("Select the file to import");
		GridData labelGd = new GridData();
		labelGd.horizontalSpan = 2;
		label.setLayoutData(labelGd);

		text = new Text(container, SWT.BORDER | SWT.SINGLE);
		text.setText("");
		text.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e)
			{
				if (!text.getText().isEmpty()) {
					setPageComplete(true);
				}		
			}
		});

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		text.setLayoutData(gd);

		final FileDialog fileDialog = new FileDialog(getShell());
		fileDialog.setText("Select File to Import");
		fileDialog.setFilterExtensions(new String[] { "*.tar" });
		fileDialog.setFilterNames(new String[] { "tar-archive file(*.tar)" });

		Button selectFileButton = new Button(container, SWT.PUSH);
		selectFileButton.setText("...");
		selectFileButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				String fileToImport = fileDialog.open();
				text.setText(fileToImport);
				DbConfiguration.setFileToImport(fileToImport);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{}
		});

		// Required to avoid an error in the system
		setControl(container);
		setPageComplete(false);
	}
}