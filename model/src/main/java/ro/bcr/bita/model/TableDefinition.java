package ro.bcr.bita.model;

public class TableDefinition extends BaseDefinition{
	
	private String layerName;
	private String schemaName;
	
	protected TableDefinition(String name) {
		this.setName(name);
	}
	
	protected TableDefinition() {
	}

	
	public String getLayerName() {
		return layerName;
	}

		public void setLayerName(String layerName) {
		this.layerName = layerName;
	}

	
	public String getSchemaName() {
		return schemaName;
	}

	
	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}
	
	
	
	

}
