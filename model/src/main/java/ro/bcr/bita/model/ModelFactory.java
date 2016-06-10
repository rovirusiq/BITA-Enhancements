package ro.bcr.bita.model;

public class ModelFactory{
	
	
	public ModelFactory() {
		
	}
	
	public ConnectionDetails createConnectionObject() {
		return new ConnectionDetails();
	}
	
	public SurrogationUsage createSurrogationUsage(MappingLine mL,SurrogationDefinition srgDef) {
		return new SurrogationUsage(mL,srgDef);
	}
	
	public MappingLine createMappingLine(String name) {
		return new MappingLine(name);
	}
	
	
	public ColumnPath createColumnPath() {
		return new ColumnPath();
	}
	
	public TableDefinition createTableDefinition(String name) {
		return new TableDefinition(name);
	}
	
	public SurrogationDefinition createSurrogationDefinition(String name) {
		return new SurrogationDefinition(name);
	}
	
	public SurrogationDefinition createExtendedSurrogationDefinition(String name) {
		return new SurrogationExtendedDefinition(name);
	}
}
