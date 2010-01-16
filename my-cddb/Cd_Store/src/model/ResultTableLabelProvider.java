package model;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class ResultTableLabelProvider extends LabelProvider implements ITableLabelProvider 
{

	@Override
	public Image getColumnImage(Object element, int columnIndex) 
	{
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) 
	{
		String[] resultLine = (String[]) element;
		
		return resultLine[columnIndex];
	}

}
