package ro.bcr.bita.model.decorator

import ro.bcr.bita.model.ColumnPath
import ro.bcr.bita.model.ModelFactoryTest;
import ro.bcr.bita.model.SurrogationUsage;
import ro.bcr.bita.model.decorator.QueryGeneratorForSurrogationUsage;

import org.hamcrest.Matchers
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

import org.junit.Test;
import org.junit.rules.TestName

class QueryGeneratorForSurrogationUsageUnitTest {
	
	private SurrogationUsage srgUsg;
	private QueryGeneratorForSurrogationUsage subject;
	
	private static QueryGeneratorForSurrogationUsage.Context ctx;
	private static ModelFactoryTest factory=new ModelFactoryTest();

	private static final String SCHEMA_FOR_GENERATED_QUERY = "DUMMY_SCHEMA"

	private static final String LAYER = "STAGE"

	private static final String VERSION = "DUMMY_VERSION"

	private static final String RELEASE = "DUMMY_RELEASE"

	private static final String USER = "DUMMY_USER"

	private static final String MAPPING_NAME = "mapping-test"
	private static final String MAPPING_TABLE = "ML_TABLE"
	private static final String MAPPING_COLUMN = "ML_COLUMN"

	private static final String SRG_NAME = "srg-test"

	private static final String TABLE = "TABLE"

	private static final String JOIN_TYPE = "LJOIN"

	private static final String JOIN_NAME = "KK";
	
	private static final String RETURNED_QUERY="UPDATE ${SCHEMA_FOR_GENERATED_QUERY}.CMT_MAPPING_LINE SET expression=? WHERE mapping_name=? AND release_cd=? AND dwh_version_cd=? AND target_layer_name=? AND target_table_name=? AND target_column_name=? AND ld_usr=?";
	private static final String RETURNED_QUERY_WITHOUT_SCHEMA="UPDATE CMT_MAPPING_LINE SET expression=? WHERE mapping_name=? AND release_cd=? AND dwh_version_cd=? AND target_layer_name=? AND target_table_name=? AND target_column_name=? AND ld_usr=?";
	
	@Rule public TestName name=new TestName();
	
	
	
	
	@BeforeClass
	public static void beforeClass() {
		ctx=new QueryGeneratorForSurrogationUsage.Context();
		ctx.setSchemaName(SCHEMA_FOR_GENERATED_QUERY);
		ctx.setTargetLayerName(LAYER);
		ctx.setVersion(VERSION);
		ctx.setRelease(RELEASE);
		ctx.setLoadUserName(USER);
	}
	
	private ColumnPath createColumnPathWithConstant(String expr) {
		return factory.createColumnPathWithConstant(expr);
	}
	
	private ColumnPath createColumnPathWithColumn(String columnName) {
		return factory.createColumnPathWithColumn(TABLE,columnName,JOIN_TYPE,JOIN_NAME);
	}
	
	
	private ColumnPath createColumnPathWithoutJoin(String columnName) {
		return factory.createColumnPathWithoutJoin(TABLE,columnName);
	}
	
	private List createParams(String expression) {
		return [expression,MAPPING_NAME,RELEASE,VERSION,LAYER,MAPPING_TABLE,MAPPING_COLUMN,USER];
	}
	
	@Before
	public void beforeTest() {
		srgUsg=factory.createSurrogationUsage(factory.createMappingLine(MAPPING_NAME)
			,factory.createSurrogationDefinition(SRG_NAME)
		);
		
		srgUsg.getMappingLine().getTrgTable().setName(MAPPING_TABLE);
		srgUsg.getMappingLine().getTrgColumn().setName(MAPPING_COLUMN);
		subject=new QueryGeneratorForSurrogationUsage(srgUsg,ctx);
	}
	
	@After
	public void afterTest() {
		if (name.getMethodName().equals("emptySchemaNameShouldBeTreated")) {
			ctx.setSchemaName(SCHEMA_FOR_GENERATED_QUERY);
		}
	}
	
	@Test
	public void queryRetrunedShouldHaveTheRightFormatAndTheParameters1() {
		srgUsg.addPath(this.createColumnPathWithConstant("CONST"));
		def lst=subject.generateQueries();
		String expression="'CONST'";
		assertThat(lst[0],hasKey(equalTo(RETURNED_QUERY)));
		assertThat(lst[0],hasValue(equalTo(createParams(expression))));
	}
	@Test
	public void queryRetrunedShouldHaveTheRightFormatAndTheParameters2() {
		srgUsg.addPath(this.createColumnPathWithColumn("COL1"));
		def lst=subject.generateQueries();
		String expression=TABLE+".COL1["+JOIN_TYPE+":"+JOIN_NAME+"]";
		assertThat(lst[0],hasKey(equalTo(RETURNED_QUERY)));
		assertThat(lst[0],hasValue(equalTo(createParams(expression))));
	}
	@Test
	public void queryRetrunedShouldHaveTheRightFormatAndTheParameters3() {
		srgUsg.addPath(this.createColumnPathWithColumn("COL1"),this.createColumnPathWithConstant("CONST"));
		def lst=subject.generateQueries();
		String expression=TABLE+".COL1["+JOIN_TYPE+":"+JOIN_NAME+"] | 'CONST'";
		assertThat(lst[0],hasKey(equalTo(RETURNED_QUERY)));
		assertThat(lst[0],hasValue(equalTo(createParams(expression))));
	}
	@Test
	public void queryRetrunedShouldHaveTheRightFormatAndTheParameters4() {
		srgUsg.addPath(this.createColumnPathWithColumn("COL1"),this.createColumnPathWithConstant("CONST"),this.createColumnPathWithColumn("COL2"));
		def lst=subject.generateQueries();
		String expression=TABLE+".COL1["+JOIN_TYPE+":"+JOIN_NAME+"] | 'CONST' | "+TABLE+".COL2["+JOIN_TYPE+":"+JOIN_NAME+"]";
		assertThat(lst[0],hasKey(equalTo(RETURNED_QUERY)));
		assertThat(lst[0],hasValue(equalTo(createParams(expression))));
	}
	
	@Test
	public void emptySchemaNameShouldBeTreated() {
		//empty String
		ctx.setSchemaName("");
		def lst=subject.generateQueries();
		assertThat(lst[0],hasKey(equalTo(RETURNED_QUERY_WITHOUT_SCHEMA)));
		
		//null value
		ctx.setSchemaName(null);
		lst=subject.generateQueries();
		assertThat(lst[0],hasKey(equalTo(RETURNED_QUERY_WITHOUT_SCHEMA)));
	}
	
	@Test
	public void columnPathsWithoutJoinShouldbeTreated() {
		srgUsg.getPaths().add(this.createColumnPathWithoutJoin("CICI"));
		String expression=TABLE+".CICI";
		def lst=subject.generateQueries();
		assertThat(lst[0],hasValue(equalTo(createParams(expression))));
	}

}
