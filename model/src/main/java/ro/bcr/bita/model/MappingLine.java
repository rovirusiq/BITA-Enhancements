package ro.bcr.bita.model;

public class MappingLine {
	
	private String name="";
	private TableDefinition trgTable=new TableDefinition();
	private ColumnDefinition trgColumn=new ColumnDefinition();
	private String expression="";
	private String surrogationExpression="";
	private VersionInfo versionInfo=new VersionInfo();
	/**
	 * @param name
	 */
	protected MappingLine(String name) {
		super();
		this.name = name;
	}
	
	
	/************************************************************************************************************
	 *
	 *G(S)etters
	 *
	 ************************************************************************************************************/
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @return the trgTable
	 */
	public TableDefinition getTrgTable() {
		return trgTable;
	}
	/**
	 * @return the trgColumn
	 */
	public ColumnDefinition getTrgColumn() {
		return trgColumn;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}


	/**
	 * @return the expression
	 */
	public String getExpression() {
		return expression;
	}


	/**
	 * @return the surrogationExpression
	 */
	public String getSurrogationExpression() {
		return surrogationExpression;
	}


	/**
	 * @param expression the expression to set
	 */
	public void setExpression(String expression) {
		this.expression = expression;
	}


	/**
	 * @param surrogationExpression the surrogationExpression to set
	 */
	public void setSurrogationExpression(String surrogationExpression) {
		this.surrogationExpression = surrogationExpression;
	}


	/**
	 * @return the versionInfo
	 */
	public VersionInfo getVersionInfo() {
		return versionInfo;
	}
	
	
}
