package ro.bcr.bita.srg

import groovy.sql.Sql

import java.util.regex.Matcher

import ro.bcr.bita.model.ColumnPath
import ro.bcr.bita.model.MappingLine;
import ro.bcr.bita.model.ModelFactory;
import ro.bcr.bita.model.SurrogationDefinition
import ro.bcr.bita.model.SurrogationUsage
import ro.bcr.bita.model.decorator.JdbcConnectionDetails;

public class SurrogationService {
	
	private JdbcConnectionDetails connectionDetails;
	
	public SurrogationService(JdbcConnectionDetails connection) {
		this.connectionDetails=connection;
	}
		
	private Sql createSqlInstance() {
		return Sql.newInstance(connectionDetails.provideJdbcConnectionString(),connectionDetails.provideJdbcUsername(),connectionDetails.provideJdbcPassword());
	}
	
	private String removeComments (String strSrgExpr){
		String rsp=strSrgExpr;
		rsp = rsp.replaceAll("/\\*.*\\*/", "");
		rsp = rsp.replaceAll("\r\n", "");
		rsp = rsp.replaceAll("\n", "");
		rsp = rsp.trim();
		return rsp;
	};

	private Closure clRemoveCommentsFromSurrogationExpression= {srgExprString->
		String rsp=srgExprString;
		rsp = rsp.replaceAll("/\\*.*\\*/", "");
		rsp = rsp.replaceAll("\r\n", "");
		rsp = rsp.replaceAll("\n", "");
		rsp = rsp.trim();
		rsp;
		
	}
	public Closure CLS_NO_FILTER= {it -> true;};
	
	private Closure clsExtractSurrogationUsage= {srgExprString->
		Matcher m = (srgExprString =~ /^SRG.(NK|CK|TK)\['([A-Z_a-z0-9]+)'(?:\s*,\s*([A-Za-z'].+))?\]$/);
		return m;
		
	};

	private void workOnSurrogationUsageAddditionalDetails(SurrogationUsage srgUsg) throws SurrogationException {
		
		String input=srgUsg.getInputKeysConcatenated();
		if ((null==input) || ("".equals(input))) return;
		
		ModelFactory factory=new ModelFactory();
		
		def lstColumns=input.split(/\|/).collect{it->it.trim()};
		lstColumns.each{it->
			Matcher m1 = it =~ /^([a-zA-Z0-9_-]+)\.([a-zA-Z0-9_-]+)\s*(?:\[((?:L|R|I|X)?JOIN)\s*:\s*([a-zA-Z0-9_]+)\s*\])?$/;
			Matcher m2 = it =~ /^\s*'([a-zA-Z0-9_ .-]+)\s*'$/;
			ColumnPath p=factory.createColumnPath();
			if (m1) {
				p.setTableName(m1[0][1]);
				p.setColumnName(m1[0][2]);
				if (m1[0].size()>2) {
					p.setJoinType(m1[0][3]);
					p.setJoinName(m1[0][4]);
				}
			} else if (m2) {
				p.setConstantExpression(m2[0][1]);
			} else {
				throw new SurrogationException("Invalid Surrogation Additional Details:"+input);
			}
			srgUsg.addPath(p);
		};
	}


	private Matcher extractMatcherFromSurrogationExpression(String srgExprString) {
		Matcher m=null;
		if ( (srgExprString!=null) && (!"".equals(srgExprString))){
			m=(clsExtractSurrogationUsage << clRemoveCommentsFromSurrogationExpression).call(srgExprString);
		}
		return m;
	}
	
	def parseSurrogationExpressions(Closure additionalFilterOnMappingLineRow= CLS_NO_FILTER, Closure additionalFilterOnSurrogationUsageObject=CLS_NO_FILTER) {
		def rsp=[];
		Sql sql=createSqlInstance();
		sql.eachRow("select mapping_name,target_table,target_column,surrogation_expression from TEMP_TABLE"){row->
				String srgExprString=(String)row[3];
				if (additionalFilterOnMappingLineRow(row)) {	
					Matcher m=extractMatcherFromSurrogationExpression(srgExprString);
					if (m) {
						ModelFactory factory=new ModelFactory();
						MappingLine mL=factory.createMappingLine(row[0]);
						SurrogationDefinition sD=factory.createSurrogationDefinition(m[0][2]);
						SurrogationUsage sU=factory.createSurrogationUsage(mL,sD);
						
						mL.setSurrogationExpression(srgExprString);
						mL.getTrgTable().setName(row[1]);
						mL.getTrgColumn().setName(row[2]);
						
						sU.setDesiredKey(m[0][1]);
						if (m[0].size()>3) {
							sU.setInputKeysConcatenated(m[0][3]);
						}
						try {
							workOnSurrogationUsageAddditionalDetails(sU);					
							rsp << sU;
						} catch (SurrogationException ex) {
							//ignore it. it wont be added into the list
						}
					}
				}
		}
		rsp=rsp.findAll(additionalFilterOnSurrogationUsageObject);
		return rsp;
	}

}
