package ro.bcr.bita.model.decorator;

import ro.bcr.bita.model.ColumnPath;
import ro.bcr.bita.model.SurrogationUsage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryGeneratorForSurrogationUsage implements IQueryGenerator {
	
	
	private final Context ctx;
	private final SurrogationUsage srgUsg;
	
	
	public static class Context {
		private String schemaName;
		private String version;
		private String release;
		private String targetLayerName;
		private String loadUserName;
		
		
		/**
		 * @return the schemaName
		 */
		public String getSchemaName() {
			return schemaName;
		}
		/**
		 * @param schemaName the schemaName to set
		 */
		public void setSchemaName(String schemaName) {
			this.schemaName = schemaName;
		}
		/**
		 * @return the version
		 */
		public String getVersion() {
			return version;
		}
		/**
		 * @return the release
		 */
		public String getRelease() {
			return release;
		}
		/**
		 * @return the targetLayerName
		 */
		public String getTargetLayerName() {
			return targetLayerName;
		}
		/**
		 * @return the loadUserName
		 */
		public String getLoadUserName() {
			return loadUserName;
		}
		/**
		 * @param version the version to set
		 */
		public void setVersion(String version) {
			this.version = version;
		}
		/**
		 * @param release the release to set
		 */
		public void setRelease(String release) {
			this.release = release;
		}
		/**
		 * @param targetLayerName the targetLayerName to set
		 */
		public void setTargetLayerName(String targetLayerName) {
			this.targetLayerName = targetLayerName;
		}
		/**
		 * @param loadUserName the loadUserName to set
		 */
		public void setLoadUserName(String loadUserName) {
			this.loadUserName = loadUserName;
		}
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "Context [version=" + version + ", release=" + release
					+ ", targetLayerName=" + targetLayerName
					+ ", loadUserName=" + loadUserName + "]";
		}
		
		
		
	}
	
	
	public QueryGeneratorForSurrogationUsage(SurrogationUsage srgUsg, Context ctx) {
		this.srgUsg=srgUsg;
		this.ctx=ctx;
	}
	

	private String generateMappingExpresssion() {
		StringBuilder rsp=new StringBuilder();
		for (ColumnPath p : srgUsg.getPaths()) {
			boolean notFirst=(rsp.length()>0);
			if (notFirst) rsp.append(" | ");
			if ( (null!=p.getConstantExpression()) && (!"".equals(p.getConstantExpression())) ){
				rsp.append("'").append(p.getConstantExpression()).append("'");
			} else {
				rsp.append(p.getTableName()).append(".").append(p.getColumnName());
				if ((null!=p.getJoinName()) && (!"".equals(p.getTableName()))) {
					rsp.append("[").append(p.getJoinType()).append(":").append(p.getJoinName()).append("]");
				}
			}
		}
		return rsp.toString();
	}
	
	
	@Override
	public List<Map<String, List<Object>>> generateQueries() {
		StringBuilder rsp=new StringBuilder();
		String tablePrefix=((ctx.getSchemaName()!=null) && (!"".equals(ctx.getSchemaName())) )? ctx.getSchemaName()+".":"";
		rsp.append("UPDATE ").append(tablePrefix).append("CMT_MAPPING_LINE");
		rsp.append(" SET expression=?");
		rsp.append(" WHERE mapping_name=?");
		rsp.append(" AND release_cd=?");
		rsp.append(" AND dwh_version_cd=?");
		rsp.append(" AND target_layer_name=?");
		rsp.append(" AND target_table_name=?");
		rsp.append(" AND target_column_name=?");
		rsp.append(" AND ld_usr=?");
		
		List<Object> params=new ArrayList<Object>();
		params.add(generateMappingExpresssion());
		params.add(srgUsg.getMappingLine().getName());
		params.add(ctx.getRelease());
		params.add(ctx.getVersion());
		params.add(ctx.getTargetLayerName());
		params.add(srgUsg.getMappingLine().getTrgTable().getName());
		params.add(srgUsg.getMappingLine().getTrgColumn().getName());
		params.add(ctx.getLoadUserName());
		
		Map<String,List<Object>> query=new HashMap<String,List<Object>>();
		query.put(rsp.toString(),params);
		
		List<Map<String,List<Object>>> queryContainer=new ArrayList<Map<String,List<Object>>>();
		queryContainer.add(query);
		return queryContainer;
	}

}
