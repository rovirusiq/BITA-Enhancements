package ro.bcr.bita.srg
import ro.bcr.bita.model.ConnectionDetails
import ro.bcr.bita.model.ModelFactory
import ro.bcr.bita.model.ModelFactoryTest;
import ro.bcr.bita.model.SurrogationUsage
import ro.bcr.bita.model.decorator.JdbcConnectionDetails;
import ro.bcr.bita.srg.SurrogationService;
import ro.ns.test.support.groovy.HSQLInfrastructureProvider;

import java.sql.SQLRecoverableException;

import groovy.sql.Sql;

import org.dbunit.JdbcDatabaseTester;
import org.dbunit.dataset.IDataSet
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.operation.DatabaseOperation;

import groovy.xml.StreamingMarkupBuilder;

import org.hamcrest.Matcher
import org.hamcrest.core.IsNull;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

class SurogationServiceIntTest {
	
	private static JdbcDatabaseTester tester;
	private static Sql sql;
	private static ConnectionDetails connectionConfig;
	private static HSQLInfrastructureProvider infrastructureProvider;
		
	
	private SurrogationService subject;
	
	
	
	def checkSurrogationUsageAgainstRecord(SurrogationUsage surrogationUsage,List<String>lst) {
		assertThat(surrogationUsage.mappingLine.name,equalTo(lst[0]));
		assertThat(surrogationUsage.mappingLine.trgTable.name,equalTo(lst[1]));
		assertThat(surrogationUsage.mappingLine.trgColumn.name,equalTo(lst[2]));
		assertThat(surrogationUsage.mappingLine.surrogationExpression,equalTo(lst[3]));
		assertThat(surrogationUsage.surrogationDefinition.name,equalTo(lst[4]));
	}
	
	
	@BeforeClass
	public static void beforeClass() {
		infrastructureProvider=HSQLInfrastructureProvider.createProvider();
		sql=infrastructureProvider.createSqlGroovyObject();
		sql.execute("""
			drop table if exists temp_table;
			create table temp_table(
				mapping_name varchar2(500),
				target_table varchar2(30),
				target_column varchar2(30),
				surrogation_expression varchar2(2000)
			);

		""");
	
		tester = infrastructureProvider.createJdbcDatabaseTester();
		connectionConfig=(new ModelFactoryTest()).createConnectionObject();
		connectionConfig.setJdbcConnectionString(infrastructureProvider.getJdbcConnectionString());
		connectionConfig.setJdbcUser(infrastructureProvider.getJdbcUser());
		connectionConfig.setJdbcPassword(infrastructureProvider.getJdbcPassword());
		
		
	}
	
	@AfterClass
	public static void afterClass() {
		sql?.close();
	}

	@Before
	public void beforeEachTest() {
		subject=new SurrogationService(new JdbcConnectionDetails(connectionConfig));
	}
	
	@After
	public void afterEachTest() {
		infrastructureProvider.clearDatbaseState(tester);
	}
	
		
	@Test
	public void extractWithoutFilterShouldReturnAllRecordsFromTheTable() {
		infrastructureProvider.createInitialDatbaseState(tester){
			temp_table mapping_name:"MPL1", target_table:"TABLE1", target_column:"COLUMN1", surrogation_expression:"SRG.NK['LC_HR_ENTITY_2_AU',LC_HR_ENTITY.ENT_ID | 'Number of employees']";
			temp_table mapping_name:"MPL2", target_table:"TABLE2", target_column:"COLUMN2", surrogation_expression:"SRG.NK['LC_HR_ENTITY_2_BAU',LC_HR_ENTITY.ENT_ID | LC_HT_ENTITY.OTH_FIELD]";
		}
		def lst=subject.parseSurrogationExpressions();
		//check number of valid records
		assertThat(lst.size(),equalTo(2));
		//check valid records
		checkSurrogationUsageAgainstRecord(lst[0],["MPL1","TABLE1","COLUMN1","SRG.NK['LC_HR_ENTITY_2_AU',LC_HR_ENTITY.ENT_ID | 'Number of employees']","LC_HR_ENTITY_2_AU"]);
		checkSurrogationUsageAgainstRecord(lst[1],["MPL2","TABLE2","COLUMN2","SRG.NK['LC_HR_ENTITY_2_BAU',LC_HR_ENTITY.ENT_ID | LC_HT_ENTITY.OTH_FIELD]","LC_HR_ENTITY_2_BAU"]);
	}
	
	
	@Test
	public void extractWithoutFilterShouldReturnAllRecordsFromTheTablEvenIfTheListIsNotASet() {
		infrastructureProvider.createInitialDatbaseState(tester){
			temp_table mapping_name:"MPL1", target_table:"TABLE1", target_column:"COLUMN1", surrogation_expression:"SRG.NK['LC_HR_ENTITY_2_AU',LC_HR_ENTITY.ENT_ID | 'Number of employees']";
			temp_table mapping_name:"MPL2", target_table:"TABLE2", target_column:"COLUMN2", surrogation_expression:"SRG.NK['LC_HR_ENTITY_2_BAU',LC_HR_ENTITY.ENT_ID | LC_HT_ENTITY.OTH_FIELD]";
			//add row identical with the first
			temp_table mapping_name:"MPL1", target_table:"TABLE1", target_column:"COLUMN1", surrogation_expression:"SRG.NK['LC_HR_ENTITY_2_AU',LC_HR_ENTITY.ENT_ID | 'Number of employees']";
		}
		def lst=subject.parseSurrogationExpressions();
		//check number of valid records
		assertThat(lst.size(),equalTo(3));
		//check valid records
		checkSurrogationUsageAgainstRecord(lst[0],["MPL1","TABLE1","COLUMN1","SRG.NK['LC_HR_ENTITY_2_AU',LC_HR_ENTITY.ENT_ID | 'Number of employees']","LC_HR_ENTITY_2_AU"]);
		checkSurrogationUsageAgainstRecord(lst[1],["MPL2","TABLE2","COLUMN2","SRG.NK['LC_HR_ENTITY_2_BAU',LC_HR_ENTITY.ENT_ID | LC_HT_ENTITY.OTH_FIELD]","LC_HR_ENTITY_2_BAU"]);
		checkSurrogationUsageAgainstRecord(lst[2],["MPL1","TABLE1","COLUMN1","SRG.NK['LC_HR_ENTITY_2_AU',LC_HR_ENTITY.ENT_ID | 'Number of employees']","LC_HR_ENTITY_2_AU"]);
	}
	
	@Test
	public void extractWithFilterShouldReturnOnlyTheIdentifiedRecords() {
		infrastructureProvider.createInitialDatbaseState(tester){
			temp_table mapping_name:"MPL1", target_table:"TABLE1", target_column:"COLUMN1", surrogation_expression:"SRG.NK['LC_HR_ENTITY_2_AU',LC_HR_ENTITY.ENT_ID | 'Number of employees']";
			temp_table mapping_name:"MPL2", target_table:"TABLE2", target_column:"COLUMN2", surrogation_expression:"SRG.NK['LC_HR_ENTITY_2_BAU',LC_HR_ENTITY.ENT_ID | LC_HT_ENTITY.OTH_FIELD]";
			temp_table mapping_name:"MPL3", target_table:"TABLE3", target_column:"COLUMN3", surrogation_expression:"SRG.NK['LC_HR_ENTITY_3_CAU',LC_HR_ENTITY.ENT_ID | 'Number of employees2']";
		}
		int contor=0;
		def lst=subject.parseSurrogationExpressions{it-> contor++;(contor!=2)};
		//one record, the second one should be eliminated
		assertThat(lst.size(),equalTo(2));
		checkSurrogationUsageAgainstRecord(lst[0],["MPL1","TABLE1","COLUMN1","SRG.NK['LC_HR_ENTITY_2_AU',LC_HR_ENTITY.ENT_ID | 'Number of employees']","LC_HR_ENTITY_2_AU"]);
		checkSurrogationUsageAgainstRecord(lst[1],["MPL3","TABLE3","COLUMN3","SRG.NK['LC_HR_ENTITY_3_CAU',LC_HR_ENTITY.ENT_ID | 'Number of employees2']","LC_HR_ENTITY_3_CAU"]);
	}
	
	@Test
	public void parseShouldReturnOnlyValidSurrogationExpressions() {
		infrastructureProvider.createInitialDatbaseState(tester){
			temp_table mapping_name:"MPL1", target_table:"TABLE1", target_column:"COLUMN1", surrogation_expression:"SRG.NK['LC_HR_ENTITY_2_AU',LC_HR_ENTITY.ENT_ID | 'Number of employees']";
			temp_table mapping_name:"MPL4", target_table:"TABLE4", target_column:"COLUMN4", surrogation_expression:"SRG.NK['LC_HR_ENTITY_4_AU' LC_HR_ENTITY.ENT_ID | 'Number of employees']";
			temp_table mapping_name:"MPL2", target_table:"TABLE2", target_column:"COLUMN2", surrogation_expression:"SRG.NK['LC_HR_ENTITY_2_BAU',LC_HR_ENTITY.ENT_ID | LC_HT_ENTITY.OTH_FIELD]";
			temp_table mapping_name:"MPL3", target_table:"TABLE3", target_column:"COLUMN3", surrogation_expression:"SRG.NKXXXX";
		}
		def lst=subject.parseSurrogationExpressions();
		assertThat(lst.size(),equalTo(2));//valid indexes from initial dataset:0,2
		checkSurrogationUsageAgainstRecord(lst[0],["MPL1","TABLE1","COLUMN1","SRG.NK['LC_HR_ENTITY_2_AU',LC_HR_ENTITY.ENT_ID | 'Number of employees']","LC_HR_ENTITY_2_AU"]);
		checkSurrogationUsageAgainstRecord(lst[1],["MPL2","TABLE2","COLUMN2","SRG.NK['LC_HR_ENTITY_2_BAU',LC_HR_ENTITY.ENT_ID | LC_HT_ENTITY.OTH_FIELD]","LC_HR_ENTITY_2_BAU"]);
	}
	
	@Test
	public void parseShouldIgnoreSurrogationUsageWithInvalidAdditionalDetails() {
		infrastructureProvider.createInitialDatbaseState(tester){
			temp_table mapping_name:"MPL1", target_table:"TABLE1", target_column:"COLUMN1", surrogation_expression:"SRG.NK['LC_HR_ENTITY_2_AU',-LC_HR_ENTITY.ENT_ID]";
			temp_table mapping_name:"MPL1", target_table:"TABLE1", target_column:"COLUMN1", surrogation_expression:"SRG.NK['LC_HR_ENTITY_2_AU',LC_HR_ENTITY]";
			temp_table mapping_name:"MPL1", target_table:"TABLE1", target_column:"COLUMN1", surrogation_expression:"SRG.NK['LC_HR_ENTITY_2_AU',LC_HR_ENTITY.]";
			temp_table mapping_name:"MPL1", target_table:"TABLE1", target_column:"COLUMN1", surrogation_expression:"SRG.NK['LC_HR_ENTITY_2_AU','LC_HR_ENTITY]";//missing '
			temp_table mapping_name:"MPL1", target_table:"TABLE1", target_column:"COLUMN1", surrogation_expression:"SRG.NK['LC_HR_ENTITY_2_AU',LC_HR_ENTITY[LJOIN:CEVA]";//missing ]
			temp_table mapping_name:"MPL1", target_table:"TABLE1", target_column:"COLUMN1", surrogation_expression:"SRG.NK['LC_HR_ENTITY_2_AU',LC_HR_ENTITY.CEVA[DEF]";//missing joinType
			temp_table mapping_name:"MPL1", target_table:"TABLE1", target_column:"COLUMN1", surrogation_expression:"SRG.NK['LC_HR_ENTITY_2_AU',LC_HR_ENTITY.CEVA[LJOIN]";//missing joinName
			temp_table mapping_name:"MPL1", target_table:"TABLE1", target_column:"COLUMN1", surrogation_expression:"SRG.NK['LC_HR_ENTITY_2_AU',LC_HR_ENTITY.CEVA[CJOIN:DEF]";//incorrect joinName
		}
		def lst=subject.parseSurrogationExpressions();
		assertThat(lst.size(),equalTo(0));
	}
	
	@Test
	public void parseShouldAcceptSurrogationUsageWithValidAdditionalDetails1() {
		infrastructureProvider.createInitialDatbaseState(tester){
			temp_table mapping_name:"MPL1", target_table:"TABLE1", target_column:"COLUMN1", surrogation_expression:"SRG.NK['LC_HR_ENTITY_2_AU',LC_HR_ENTITY.ENT_ID]";
		}
		def lst=subject.parseSurrogationExpressions();
		assertThat(lst.size(),equalTo(1));
		Matcher isTableNameCorrect=hasProperty("tableName",equalTo("LC_HR_ENTITY"));
		Matcher isColumnNameCorrect=hasProperty("columnName",equalTo("ENT_ID"));
		assertThat(lst[0].getPaths(),contains( allOf(isTableNameCorrect,isColumnNameCorrect)));
	}
	@Test
	public void parseShouldAcceptSurrogationUsageWithValidAdditionalDetails2() {
		infrastructureProvider.createInitialDatbaseState(tester){
			temp_table mapping_name:"MPL1", target_table:"TABLE1", target_column:"COLUMN1", surrogation_expression:"SRG.NK['LC_HR_ENTITY_2_AU','CEVA']";
		}
		def lst=subject.parseSurrogationExpressions();
		assertThat(lst.size(),equalTo(1));
		Matcher isConstantCorrect=hasProperty("constantExpression",equalTo("CEVA"));		
		assertThat(lst[0].getPaths(),contains( allOf(isConstantCorrect)));
	}
	@Test
	public void parseShouldAcceptSurrogationUsageWithValidAdditionalDetails3() {
		infrastructureProvider.createInitialDatbaseState(tester){
			temp_table mapping_name:"MPL1", target_table:"TABLE1", target_column:"COLUMN1", surrogation_expression:"SRG.NK['LC_HR_ENTITY_2_AU',LC_HR_ENTITY.ENT_ID[JOIN:DEF]]";
		}
		def lst=subject.parseSurrogationExpressions();
		assertThat(lst.size(),equalTo(1));
		Matcher isTableNameCorrect=hasProperty("tableName",equalTo("LC_HR_ENTITY"));
		Matcher isColumnNameCorrect=hasProperty("columnName",equalTo("ENT_ID"));
		Matcher isJoinTypeCorrect=hasProperty("joinType",equalTo("JOIN"));
		Matcher isJoinNameCorrect=hasProperty("joinName",equalTo("DEF"));
		assertThat(lst[0].getPaths(),contains( allOf(isTableNameCorrect,isColumnNameCorrect,isJoinTypeCorrect,isJoinNameCorrect)));
	}
	@Test
	public void parseShouldAcceptSurrogationUsageWithValidAdditionalDetails4() {
		infrastructureProvider.createInitialDatbaseState(tester){
			temp_table mapping_name:"MPL1", target_table:"TABLE1", target_column:"COLUMN1", surrogation_expression:"SRG.NK['LC_HR_ENTITY_2_AU',LC_HR_ENTITY.ENT_ID[LJOIN:DEF]]";
		}
		def lst=subject.parseSurrogationExpressions();
		assertThat(lst.size(),equalTo(1));
		Matcher isTableNameCorrect=hasProperty("tableName",equalTo("LC_HR_ENTITY"));
		Matcher isColumnNameCorrect=hasProperty("columnName",equalTo("ENT_ID"));
		Matcher isJoinTypeCorrect=hasProperty("joinType",equalTo("LJOIN"));
		Matcher isJoinNameCorrect=hasProperty("joinName",equalTo("DEF"));
		assertThat(lst[0].getPaths(),contains( allOf(isTableNameCorrect,isColumnNameCorrect,isJoinTypeCorrect,isJoinNameCorrect)));
	}
	@Test
	public void parseShouldAcceptSurrogationUsageWithValidAdditionalDetails5() {
		infrastructureProvider.createInitialDatbaseState(tester){
			temp_table mapping_name:"MPL1", target_table:"TABLE1", target_column:"COLUMN1", surrogation_expression:"SRG.NK['LC_HR_ENTITY_2_AU',LC_HR_ENTITY.ENT_ID[RJOIN:DEF]]";
		}
		def lst=subject.parseSurrogationExpressions();
		assertThat(lst.size(),equalTo(1));
		Matcher isTableNameCorrect=hasProperty("tableName",equalTo("LC_HR_ENTITY"));
		Matcher isColumnNameCorrect=hasProperty("columnName",equalTo("ENT_ID"));
		Matcher isJoinTypeCorrect=hasProperty("joinType",equalTo("RJOIN"));
		Matcher isJoinNameCorrect=hasProperty("joinName",equalTo("DEF"));
		assertThat(lst[0].getPaths(),contains( allOf(isTableNameCorrect,isColumnNameCorrect,isJoinTypeCorrect,isJoinNameCorrect)));
	}
	@Test
	public void parseShouldAcceptSurrogationUsageWithValidAdditionalDetails6() {
		infrastructureProvider.createInitialDatbaseState(tester){
			temp_table mapping_name:"MPL1", target_table:"TABLE1", target_column:"COLUMN1", surrogation_expression:"SRG.NK['LC_HR_ENTITY_2_AU',LC_HR_ENTITY.ENT_ID[XJOIN:DEF]]";
		}
		def lst=subject.parseSurrogationExpressions();
		assertThat(lst.size(),equalTo(1));
		Matcher isTableNameCorrect=hasProperty("tableName",equalTo("LC_HR_ENTITY"));
		Matcher isColumnNameCorrect=hasProperty("columnName",equalTo("ENT_ID"));
		Matcher isJoinTypeCorrect=hasProperty("joinType",equalTo("XJOIN"));
		Matcher isJoinNameCorrect=hasProperty("joinName",equalTo("DEF"));
		assertThat(lst[0].getPaths(),contains( allOf(isTableNameCorrect,isColumnNameCorrect,isJoinTypeCorrect,isJoinNameCorrect)));
	}
	@Test
	public void parseShouldAcceptSurrogationUsageWithValidAdditionalDetails7() {
		infrastructureProvider.createInitialDatbaseState(tester){
			temp_table mapping_name:"MPL1", target_table:"TABLE1", target_column:"COLUMN1", surrogation_expression:"SRG.NK['LC_HR_ENTITY_2_AU',LC_HR_ENTITY.ENT_ID[XJOIN:DEF]|'CUTU']";
		}
		def lst=subject.parseSurrogationExpressions();
		assertThat(lst.size(),equalTo(1));
		Matcher isTableNameCorrect=hasProperty("tableName",equalTo("LC_HR_ENTITY"));
		Matcher isColumnNameCorrect=hasProperty("columnName",equalTo("ENT_ID"));
		Matcher isJoinTypeCorrect=hasProperty("joinType",equalTo("XJOIN"));
		Matcher isJoinNameCorrect=hasProperty("joinName",equalTo("DEF"));
		Matcher isConstantCorrect=hasProperty("constantExpression",equalTo("CUTU"));
		assertThat(lst[0].getPaths(),containsInAnyOrder(allOf(isTableNameCorrect,isColumnNameCorrect,isJoinTypeCorrect,isJoinNameCorrect),isConstantCorrect));
	}
	@Test
	public void parseShouldAcceptSurrogationUsageWithValidAdditionalDetails8() {
		infrastructureProvider.createInitialDatbaseState(tester){
			temp_table mapping_name:"MPL1", target_table:"TABLE1", target_column:"COLUMN1", surrogation_expression:"SRG.NK['LC_HR_ENTITY_2_AU',LC_HR_ENTITY.ENT_ID[XJOIN:DEF]|'CUTU']";
			temp_table mapping_name:"MPL2", target_table:"TABLE2", target_column:"COLUMN2", surrogation_expression:"SRG.NK['LC_HR_ENTITY_3_AU',AAA.BBB[JOIN:AOLEU]|CCC.DDD | 'MIMI']";
		}
		def lst=subject.parseSurrogationExpressions();
		assertThat(lst.size(),equalTo(2));
		
		Matcher isTableName1Correct=hasProperty("tableName",equalTo("LC_HR_ENTITY"));
		Matcher isColumnName1Correct=hasProperty("columnName",equalTo("ENT_ID"));
		Matcher isJoinType1Correct=hasProperty("joinType",equalTo("XJOIN"));
		Matcher isJoinName1Correct=hasProperty("joinName",equalTo("DEF"));
		Matcher isConstant1Correct=hasProperty("constantExpression",equalTo("CUTU"));
		
		assertThat(lst[0].getPaths(),containsInAnyOrder(
			allOf(
				isTableName1Correct,isColumnName1Correct,isJoinType1Correct,isJoinName1Correct
			)
			,isConstant1Correct
		));
		
		Matcher isTableName2Correct=hasProperty("tableName",equalTo("AAA"));
		Matcher isColumnName2Correct=hasProperty("columnName",equalTo("BBB"));
		Matcher isJoinType2Correct=hasProperty("joinType",equalTo("JOIN"));
		Matcher isJoinName2Correct=hasProperty("joinName",equalTo("AOLEU"));
		
		Matcher isTableName3Correct=hasProperty("tableName",equalTo("CCC"));
		Matcher isColumnName3Correct=hasProperty("columnName",equalTo("DDD"));
		Matcher isJoinType3Correct=hasProperty("joinType",nullValue());
		Matcher isJoinName3Correct=hasProperty("joinName",nullValue());
		Matcher isConstant2Correct=hasProperty("constantExpression",equalTo("MIMI"));
		
		assertThat(lst[1].getPaths(),containsInAnyOrder(
			allOf(
				isTableName2Correct,isColumnName2Correct,isJoinType2Correct,isJoinName2Correct
			)
			,allOf(
				isTableName3Correct,isColumnName3Correct,isJoinType3Correct,isJoinName3Correct
			)
			,isConstant2Correct
		));
	}
	
	@Test
	public void parseShouldAcceptSurrogationUsageWithValidAdditionalDetailsCheckWithSimpleCount() {
		infrastructureProvider.createInitialDatbaseState(tester){
			temp_table mapping_name:"MPL1", target_table:"TABLE1", target_column:"COLUMN1", surrogation_expression:"SRG.NK['LC_HR_ENTITY_1_AU',LC_HR_ENTITY.ENT_ID[XJOIN:DEF]|'CUTU']";
			temp_table mapping_name:"MPL2", target_table:"TABLE2", target_column:"COLUMN2", surrogation_expression:"SRG.NK['LC_HR_ENTITY_2_AU',AAA.BBB[JOIN:AOLEU]|CCC.DDD | 'MIMI']";
			temp_table mapping_name:"MPL3", target_table:"TABLE3", target_column:"COLUMN3", surrogation_expression:"SRG.NK['LC_HR_ENTITY_3_AU','CUTU']";
			temp_table mapping_name:"MPL4", target_table:"TABLE4", target_column:"COLUMN4", surrogation_expression:"SRG.NK['LC_HR_ENTITY_4_AU',AAA.BBB]";
			temp_table mapping_name:"MPL5", target_table:"TABLE5", target_column:"COLUMN5", surrogation_expression:"SRG.NK['LC_HR_ENTITY_5_AU',AAA.BBB|CCC.DDD | EEEE.DDDD[RJOIN:VVVV]|'CVB']";
		}
		def lst=subject.parseSurrogationExpressions();
		assertThat(lst.size(),equalTo(5));
	}
	
	@Test
	public void parseShouldApplyCorrectlyTheDefaultFilterBasedOnSurrogationUsageObject() {
		infrastructureProvider.createInitialDatbaseState(tester){
			temp_table mapping_name:"MPL1", target_table:"TABLE1", target_column:"COLUMN1", surrogation_expression:"SRG.NK['LC_HR_ENTITY_1_AU',LC_HR_ENTITY.ENT_ID[XJOIN:DEF]|'CUTU']";
			temp_table mapping_name:"MPL2", target_table:"TABLE2", target_column:"COLUMN2", surrogation_expression:"SRG.NK['LC_HR_ENTITY_2_AU',AAA.BBB[JOIN:AOLEU]|CCC.DDD | 'MIMI']";
		}
		def lst=subject.parseSurrogationExpressions();
		assertThat(lst.size(),equalTo(2));
	}
	
	@Test
	public void parseShouldApplyCorrectlyTheProvidedFilterBasedOnSurrogationUsageObject() {
		infrastructureProvider.createInitialDatbaseState(tester){
			temp_table mapping_name:"MPL1", target_table:"TABLE1", target_column:"COLUMN1", surrogation_expression:"SRG.NK['LC_HR_ENTITY_1_AU',LC_HR_ENTITY.ENT_ID[XJOIN:DEF]|'CUTU']";
			temp_table mapping_name:"MPL2", target_table:"TABLE2", target_column:"COLUMN2", surrogation_expression:"SRG.NK['LC_HR_ENTITY_2_AU',AAA.BBB[JOIN:AOLEU]|CCC.DDD | 'MIMI']";
		}
		def lst=subject.parseSurrogationExpressions(subject.CLS_NO_FILTER,{su->(su.getPaths().size()!=3)});//should eliminate the second one
		assertThat(lst.size(),equalTo(1));
		assertThat(lst[0].mappingLine.name,equalTo("MPL1"));
	}
	
	@Test
	public void parseShouldAcceptSurrogationUsageWithValidAdditionalDetails_WithoutJoin() {
		infrastructureProvider.createInitialDatbaseState(tester){
			temp_table mapping_name:"MPL1", target_table:"TABLE1", target_column:"COLUMN1", surrogation_expression:"SRG.NK['LC_HR_ENTITY_2_AU',LC_HR_ENTITY.ENT_ID|'CUTU']";
		}
		def lst=subject.parseSurrogationExpressions();
		assertThat(lst.size(),equalTo(1));
		Matcher isTableNameCorrect=hasProperty("tableName",equalTo("LC_HR_ENTITY"));
		Matcher isColumnNameCorrect=hasProperty("columnName",equalTo("ENT_ID"));
		Matcher isJoinTypeCorrect=hasProperty("joinType",nullValue());
		Matcher isJoinNameCorrect=hasProperty("joinName",nullValue());
		Matcher isConstantCorrect=hasProperty("constantExpression",equalTo("CUTU"));
		assertThat(lst[0].getPaths(),containsInAnyOrder(allOf(isTableNameCorrect,isColumnNameCorrect,isJoinTypeCorrect,isJoinNameCorrect),isConstantCorrect));
	}
}
