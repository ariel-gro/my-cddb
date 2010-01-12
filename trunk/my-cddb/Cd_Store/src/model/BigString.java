package model;

/**
 * The class enable to create large strings in efficient way.
 * when creating big strings using the str=str + "...." a new location in
 * memory is allocated when both of the strigs are being copyed. when using large
 * strings this causes a real overhead.
 * The class locate a big chucnk of memory and copy each string to the end of it.
 * this saves most of copys because usually the added string is small. 
 */
public class BigString
{

	private final static int DEFUALT_CHARS = 1000;

	protected char[] charsArray = null;

	protected int index = 0;

	public BigString()
	{
		charsArray = new char[DEFUALT_CHARS];
	}

	public BigString(int size)
	{
		charsArray = new char[size];
	}

	public void addString(String theString)
	{
		char[] charArr = theString.toCharArray();
		addCharArr(charArr,charArr.length);
	}
	
	public void addCharArr(char[] theCharArr)
	{
		addCharArr(theCharArr,theCharArr.length);
	}
	
	public void addCharArr(char[] theCharArr,int length)
	{
		char[] theStringAsChars = theCharArr;
		if (length != theCharArr.length)
		{
			theStringAsChars = new char[length];
			System.arraycopy(theCharArr,0,theStringAsChars,0,length);
		}
		
		while (index + theStringAsChars.length > charsArray.length)
		{
			doubleArray();
		}

		System.arraycopy(theStringAsChars, 0, charsArray, index, theStringAsChars.length);
		index += theStringAsChars.length;
	}
	
	public void addChar(char c)
	{
		if (index>=charsArray.length)
			doubleArray();
		
		charsArray[index++] = c;
	}
	
    /**
     * @return the number of chars currently in the string.
     */
	public int getSize()
	{
		return index;
	}

	private void doubleArray()
	{

		char[] tmp = new char[charsArray.length * 2];

		for (int i = 0; i < charsArray.length; i++)
		{
			tmp[i] = charsArray[i];
		}

		charsArray = tmp;
	}

	public String getAsString()
	{
		if (index == 0)
			return "";
		else
			return new String(charsArray, 0, index );
	}

	public String toString()
	{
		return getAsString();
	}
	
	public void clear()
	{
		index = 0;
	}

}