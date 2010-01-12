package view.views;

import java.util.ArrayList;
import model.DbConfiguration;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import controller.MainExtractFileThread;

public class DbImportWizardPageTwo extends WizardPage
{
	private static TableViewer v;
	private static Composite parent;
	private static ProgressBar bar;
	final static ArrayList<String> messages = new ArrayList<String>();

	public DbImportWizardPageTwo() 
	{
		super("Import DB - Second Page");
		setTitle("Import DB");
		setDescription("This wizard imports the archived freeDB file to the Database");
	}

	@Override
	public void createControl(final Composite parent)
	{
		DbImportWizardPageTwo.parent = parent;
		final Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(1, false));

		v = new TableViewer(comp, SWT.FULL_SELECTION | SWT.BORDER);
		v.setContentProvider(new ArrayContentProvider());
		v.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		v.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event)
			{
				getWizard().getContainer().updateButtons();
			}
		});

		final Composite barContainer = new Composite(comp, SWT.NONE);
		barContainer.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		barContainer.setLayout(new GridLayout(2, false));

		Label l = new Label(barContainer, SWT.NONE);
		l.setText("Loading Data");

		final ProgressBar bar = new ProgressBar(barContainer, SWT.NONE);
		bar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		bar.setMaximum(34);
		DbImportWizardPageTwo.bar = bar;

		setControl(comp);
		setPageComplete(false);

		Thread t = new Thread() {

			public void run()
			{	
				while(isCurrentPage() == false)
				{
					try {Thread.sleep(1000);} catch (InterruptedException e) {}
				}
							
				MainExtractFileThread myExtractFile = new MainExtractFileThread(DbConfiguration.getFileToImport());
				myExtractFile.start();
							
				parent.getDisplay().syncExec(new Runnable() {
					public void run()
					{
						v.setInput(messages);
					}
				});

				try {
					myExtractFile.join();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}

				parent.getDisplay().asyncExec(new Runnable() {

					public void run()
					{
						((GridData) barContainer.getLayoutData()).exclude = true;
						comp.layout(true);
					}

				});

				parent.getDisplay().syncExec(new Runnable() {
					public void run()
					{
						getWizard().getContainer().updateButtons();
					}
				});
				
				setPageComplete(true);
			}
		};

		t.start();
	}
	
	public static void updateProgress(final String message, final int progress)
	{
		if (v.getTable().isDisposed()) {
			return;
		}
		parent.getDisplay().asyncExec(new Runnable() {

			public void run()
			{
				if(message.equals("") == false)
				{
					v.add(message);
					messages.add(message);
				}
				bar.setSelection(progress);
			}
		});
	}
}