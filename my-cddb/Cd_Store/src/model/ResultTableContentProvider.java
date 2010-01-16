package model;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class ResultTableContentProvider implements IStructuredContentProvider {

	@Override
	public Object[] getElements(Object inputElement) 
	{
		String[][] results = (String[][]) inputElement;
		return results;
	}

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

}