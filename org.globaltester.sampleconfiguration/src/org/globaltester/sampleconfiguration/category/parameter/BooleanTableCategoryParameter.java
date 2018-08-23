package org.globaltester.sampleconfiguration.category.parameter;

/**
 * Generic category parameter that defines a table with check boxes.
 * 
 * @author cstroh
 *
 */
public class BooleanTableCategoryParameter extends AbstractCategoryParameterDescription
		implements CategoryParameterDescription {
	
	private boolean[][] table;
	private String[] descrColumns;
	private String[] descrLines;

	/**
	 * @param categoryName name of the category
	 * @param name name of the parameter
	 * @param description parameter description
	 * @param descrColumns description of the columnes of the boolean table
	 * @param descrRows description of the rows of the boolean table
	 * @param table if element is true, then the corresponding check box element shall exist
	 */
	public BooleanTableCategoryParameter(String categoryName, String name, String description, String[] descrColumns, String[] descrRows, boolean[][] table) {
		super(categoryName, name, description);
		this.table = table;
		this.descrColumns = descrColumns;
		this.descrLines = descrRows;
	}
	
	public boolean[][] getTable(){
		return table;
	}
	
	public String[] getColumnDescription(){
		return descrColumns;
	}
	
	public String[] getLineDescription(){
		return descrLines;
	}

}
