package ro.bcr.bita.model.decorator

import static org.hamcrest.MatcherAssert.*
import static org.hamcrest.Matchers.*

import ro.bcr.bita.model.ConnectionDetails
import ro.bcr.bita.model.MappingLine
import ro.bcr.bita.model.ModelFactoryTest
import ro.bcr.bita.model.SurrogationUsage
import ro.bcr.bita.model.TableDefinition
import ro.ns.test.infrastructure.HSQLInfrastructureProvider

import groovy.sql.Sql

import org.dbunit.JdbcDatabaseTester
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test


class QueryGeneratorForSurrogationUsageIntTest {

	private static final String ORIG_EXPRESSION = "ALO1"
	
	private static JdbcDatabaseTester tester;
	private static Sql sql;
	private static ConnectionDetails connectionConfig;
	private static HSQLInfrastructureProvider infrastructureProvider;
	private static SurrogationUsage srgUsg;
	private static ModelFactoryTest factory=new ModelFactoryTest();
	
	private static QueryGeneratorForSurrogationUsage subject

	private static final String USER = "TEST-USER"

	private static final String RELEASE = "TEST_RELEASE"

	private static final String VERSION = "TEST_VERSION"

	private static final String LAYER = "BASE"

	private static final String TARGET_TABLE = "TARGET_TABLE"

	private static final String TARGET_COLUMN = "TARGET_COLUMN"

	private static final String MAPPING_NAME = "test-mapping-name";
	
	private static final String MAPPING_LINE_TABLE="CMT_MAPPING_LINE";
	
	
	@BeforeClass
	public static void beforeClass() {
		infrastructureProvider=HSQLInfrastructureProvider.createProvider();
		sql=infrastructureProvider.createSqlGroovyObject();
		GString query="""
			drop table ${MAPPING_LINE_TABLE} if exists;
			create table ${MAPPING_LINE_TABLE}(
				"RELEASE_CD" VARCHAR2(50 BYTE) NOT NULL, 
				"DWH_VERSION_CD" VARCHAR2(50 BYTE) NOT NULL, 
				"MAPPING_NAME" VARCHAR2(250 BYTE) NOT NULL, 
				"TARGET_LAYER_NAME" VARCHAR2(100 BYTE) NOT NULL, 
				"TARGET_TABLE_NAME" VARCHAR2(100 BYTE) NOT NULL, 
				"TARGET_COLUMN_NAME" VARCHAR2(100 BYTE) NOT NULL, 
				"EXPRESSION" VARCHAR2(4000 BYTE), 
				"GROUP_BY_IND" CHAR(1 BYTE), 
				"DEFAULT_VALUE" VARCHAR2(4000 BYTE), 
				"DEFAULT_VALUE_FORMAT" VARCHAR2(250 BYTE), 
				"LD_USR" VARCHAR2(25 BYTE) NOT NULL, 
				"LD_DT" DATE, 
				"CRT_USR" VARCHAR2(25 BYTE), 
				"CRT_DT" DATE, 
				"MOD_USR" VARCHAR2(25 BYTE), 
				"MOD_DT" DATE, 
				PRIMARY KEY ("RELEASE_CD", "DWH_VERSION_CD", "MAPPING_NAME", "TARGET_LAYER_NAME", "TARGET_TABLE_NAME", "TARGET_COLUMN_NAME", "LD_USR")
			);

		""";
		
		String parsedQuery=query.toString();
		sql.execute(parsedQuery);
	
		tester = infrastructureProvider.createJdbcDatabaseTester();
		connectionConfig=(new ModelFactoryTest()).createConnectionObject();
		connectionConfig.setJdbcConnectionString(infrastructureProvider.getJdbcConnectionString());
		connectionConfig.setJdbcUser(infrastructureProvider.getJdbcUser());
		connectionConfig.setJdbcPassword(infrastructureProvider.getJdbcPassword());
			
		MappingLine mL=factory.createMappingLine(MAPPING_NAME);
		
		mL.getVersionInfo().setLoadUserName(USER);
		mL.getVersionInfo().setRelease(RELEASE);
		mL.getVersionInfo().setVersion(VERSION);
		
		mL.getTrgTable().setLayerName(LAYER);
		mL.getTrgTable().setName(TARGET_TABLE);
		mL.getTrgColumn().setName(TARGET_COLUMN);
		
		
		srgUsg=factory.createSurrogationUsage(mL,factory.createSurrogationDefinition("test-srg-def"));
	
		TableDefinition targetTableDefinitionForQuery=factory.createTableDefinition()
		targetTableDefinitionForQuery.setSchemaName("");
		targetTableDefinitionForQuery.setName(MAPPING_LINE_TABLE);
		
		
		subject=new QueryGeneratorForSurrogationUsage(srgUsg, targetTableDefinitionForQuery);
	}
	
	@Before
	public void beforeTest() {
		infrastructureProvider.createInitialDatbaseState(tester){
			cmt_mapping_line RELEASE_CD:RELEASE,DWH_VERSION_CD:VERSION,MAPPING_NAME:MAPPING_NAME,TARGET_LAYER_NAME:LAYER,TARGET_TABLE_NAME:TARGET_TABLE,TARGET_COLUMN_NAME:TARGET_COLUMN,EXPRESSION:ORIG_EXPRESSION,GROUP_BY_IND:"N",DEFAULT_VALUE:"A",DEFAULT_VALUE_FORMAT:"A",LD_USR:USER,LD_DT:"2009-09-12 01:20:44.166",CRT_USR:"ABC",CRT_DT:"2009-09-12 01:20:44.166",MOD_USR:USER,MOD_DT:"2009-09-12 01:20:44.166"
		}
	}
	
	@After
	public void afterTest() {
		infrastructureProvider.clearDatbaseState(tester);
	}
	
	@Test
	public void queryGeneratedShouldExecuteOnWithSuccess() {
		
		def CICI="CICI";
		srgUsg.getPaths().add(factory.createColumnPathWithConstant(CICI));
		def lst=subject.generateQueries();
		
		def rows=sql.rows("select * from cmt_mapping_line",1,1);
		assert rows[0]['expression']== ORIG_EXPRESSION
		
		sql.execute(lst[0].keySet()[0],lst[0].values()[0]);
		assert sql.updateCount == 1;
		
		rows=sql.rows("select * from cmt_mapping_line",1,1);
		assert rows[0]['expression']== "'$CICI'"
	}

}
