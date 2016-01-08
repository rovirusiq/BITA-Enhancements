package ro.bcr.bita.model;

public class ColumnPath {

	private String tableName;
	private String columnName;
	private String joinName;
	private String joinType;
	private String constantExpression;
	
	protected ColumnPath() {
		
	}
	
	/**
	 * @return the tableName
	 */
	public String getTableName() {
		return tableName;
	}
	/**
	 * @return the tableColumn
	 */
	public String getColumnName() {
		return columnName;
	}
	/**
	 * @return the joinName
	 */
	public String getJoinName() {
		return joinName;
	}
	/**
	 * @return the joinType
	 */
	public String getJoinType() {
		return joinType;
	}
	/**
	 * @return the constantExpression
	 */
	public String getConstantExpression() {
		return constantExpression;
	}
	/**
	 * @param tableName the tableName to set
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	/**
	 * @param tableColumn the tableColumn to set
	 */
	public void setColumnName(String tableColumn) {
		this.columnName = tableColumn;
	}
	/**
	 * @param joinName the joinName to set
	 */
	public void setJoinName(String joinName) {
		this.joinName = joinName;
	}
	/**
	 * @param joinType the joinType to set
	 */
	public void setJoinType(String joinType) {
		this.joinType = joinType;
	}
	/**
	 * @param constantExpression the constantExpression to set
	 */
	public void setConstantExpression(String constantExpression) {
		this.constantExpression = constantExpression;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ColumnPath [" + tableName + "."
				+ columnName + "[" + joinType + ":"
				+ joinName + "]" + "|'"+constantExpression + "']";
	}
	
	
	
}
