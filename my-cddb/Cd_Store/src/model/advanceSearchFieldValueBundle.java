package model;

import model.RequestToQueryHandler.AdvancedSearchFields;

public class advanceSearchFieldValueBundle
{
	public enum Relation {GREATER, EQUALS, LESSER}
	
	private AdvancedSearchFields searchField;
	private Relation relation;
	private String value;
	
	public advanceSearchFieldValueBundle(AdvancedSearchFields searchField, Relation relation, String value) 
	{
		this.searchField = searchField;
		this.relation = relation;
		this.value = value;
	}

	public AdvancedSearchFields getSearchField()
	{
		return searchField;
	}

	public Relation getRelation()
	{
		return relation;
	}

	public String getValue()
	{
		return value;
	}	
}