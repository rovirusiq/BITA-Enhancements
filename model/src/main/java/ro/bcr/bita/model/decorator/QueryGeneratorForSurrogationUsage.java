package ro.bcr.bita.model.decorator;

import ro.bcr.bita.model.ColumnPath;
import ro.bcr.bita.model.SurrogationUsage;
import ro.bcr.bita.model.TableDefinition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryGeneratorForSurrogationUsage implements IQueryGenerator {
	
	
	private final SurrogationUsage srgUsg;
	private String schemaName="";
	private String tableName="CMT_MAPPING_LINE";
	
	
	public QueryGeneratorForSurrogationUsage(SurrogationUsage srgUsg,TableDefinition targetTable) {
		this.srgUsg=srgUsg;
		if (targetTable!=null) {
			this.schemaName=targetTable.getSchemaName();
			this.tableName=targetTable.getName();
		}
	}
	
	public QueryGeneratorForSurrogationUsage(SurrogationUsage srgUsg) {
		this(srgUsg,null);
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
		String tablePrefix=((schemaName!=null) && (!"".equals(schemaName)) )? schemaName+".":"";
		rsp.append("UPDATE ").append(tablePrefix).append(tableName);
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
		params.add(srgUsg.getMappingLine().getVersionInfo().getRelease());
		params.add(srgUsg.getMappingLine().getVersionInfo().getVersion());
		params.add(srgUsg.getMappingLine().getTrgTable().getLayerName());
		params.add(srgUsg.getMappingLine().getTrgTable().getName());
		params.add(srgUsg.getMappingLine().getTrgColumn().getName());
		params.add(srgUsg.getMappingLine().getVersionInfo().getLoadUserName());
		
		Map<String,List<Object>> query=new HashMap<String,List<Object>>();
		query.put(rsp.toString(),params);
		
		List<Map<String,List<Object>>> queryContainer=new ArrayList<Map<String,List<Object>>>();
		queryContainer.add(query);
		return queryContainer;
	}

}
