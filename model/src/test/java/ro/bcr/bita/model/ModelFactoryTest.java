package ro.bcr.bita.model;

import ro.bcr.bita.model.ModelFactory;

public class ModelFactoryTest extends ModelFactory{
	
	public ModelFactoryTest() {
		
	}
	
	public ColumnPath createColumnPathWithConstant(String constantValue) {
		ColumnPath p=this.createColumnPath();
		p.setConstantExpression(constantValue);
		return p;
	}
	
	public ColumnPath createColumnPathWithColumn(String tableName,String columnName,String joinType,String joinName) {
		ColumnPath p=this.createColumnPath();
		p.setTableName(tableName);
		p.setColumnName(columnName);
		p.setJoinType(joinType);
		p.setJoinName(joinName);
		return p;
	}
	
	
	public ColumnPath createColumnPathWithoutJoin(String tableName, String columnName) {
		ColumnPath p=this.createColumnPath();
		p.setTableName(tableName);
		p.setColumnName(columnName);
		return p;
	}

}
