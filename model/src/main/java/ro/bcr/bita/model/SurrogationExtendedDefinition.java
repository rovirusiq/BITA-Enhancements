package ro.bcr.bita.model;
public class SurrogationExtendedDefinition extends SurrogationDefinition {
	
	private TableDefinition pk2nkTable=new TableDefinition();
	private TableDefinition srgTable=new TableDefinition();

	protected SurrogationExtendedDefinition(String name) {
		super(name);
	}
	
	
	public TableDefinition getPk2NkTable() {
		return this.pk2nkTable;
	}
	
	public TableDefinition getSrgTable() {
		return this.srgTable;
	}

}
