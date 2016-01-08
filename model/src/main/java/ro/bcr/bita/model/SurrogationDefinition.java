package ro.bcr.bita.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SurrogationDefinition {
	private String name;
	private TableDefinition srcTable=new TableDefinition();
	private TableDefinition trgTable=new TableDefinition();
	private List<String> pkColumns=new ArrayList<String>();
	
	
	/************************************************************************************************************
	 *
	 *Constructors
	 *
	 ************************************************************************************************************/
	protected SurrogationDefinition(String name) {
		this.name=name;
	}


	/************************************************************************************************************
	 *
	 *Getters and Setters
	 *
	 ************************************************************************************************************/
	
	/**
	 * @return the srcTable
	 */
	public TableDefinition getSrcTable() {
		return srcTable;
	}

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
	
	public List<String> getPkColumns() {
		return pkColumns;
	}
	/************************************************************************************************************
	 *
	 *Public API
	 *
	 ************************************************************************************************************/
	public void addPkColumn(String... columnNames) {
		pkColumns.addAll(Arrays.asList(columnNames));
	}
	
	
	
	public String getPkColumnsAsMappingExpression() {
		StringBuilder rsp=new StringBuilder("");
		boolean ok=false;
		for (String pkName : pkColumns) {
			if (!ok) {ok=true;} else {rsp.append("||");};
			rsp.append(trgTable.getName()).append(".").append(pkName);
		}
		return rsp.toString();
	}
	
	
	
	
	
}
