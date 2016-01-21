package ro.bcr.bita.test.odi

import ro.ns.test.support.groovy.HSQLInfrastructureProvider;

import groovy.sql.Sql;

import org.dbunit.Assertion;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.dataset.IDataSet
import org.dbunit.dataset.ITable
import org.dbunit.dataset.excel.XlsDataSet;
import org.dbunit.dataset.filter.DefaultColumnFilter;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

class OdiJobTest {
	
	private static JdbcDatabaseTester tester;
	private static Sql db;
	private static HSQLInfrastructureProvider infrastructureProvider;
	
	@BeforeClass
	public static void beforeClass() {
		infrastructureProvider=new HSQLInfrastructureProvider();
		tester=infrastructureProvider.createJdbcDatabaseTester();
		db=infrastructureProvider.createSqlGroovyObject();
		
		new File("src/test/resources/ddl.sql").withReader {rdr->
			db.execute(rdr.getText());
		};	
	}
	
	@AfterClass
	public static void afterClass() {
		db?.close();
	}
	
	
	@Test
	public void firstTest() {
		XlsDataSet inputDataSet=new XlsDataSet(new File("src/test/resources/STABLE1_2_TTABLEA_INPUT.xls"));
		XlsDataSet expectedDataSet=new XlsDataSet(new File("src/test/resources/STABLE1_2_TTABLEA_EXPECTED.xls"));
		tester.setDataSet(inputDataSet);
		tester.onSetup();
		println "===STABLE1=====================";
		db.eachRow("select id,desc from stable1"){r->
			println r;
		}
		println "===============================";
		println "===TTABLEA=====================";
		db.eachRow("select id,desc,upd_dt from ttablea"){r->
			println r;
		}
		println "===============================";
		JobUtils.executeJob("My_ODI_JOB_UNDER_TEST");
		println "===TTABLEA AFTER JOB=============";
		db.eachRow("select id,desc,upd_dt from ttablea"){r->
			println r;
		}
		println "===============================";
		
		ITable expectedTable=expectedDataSet.getTable("ttablea");
		
		IDataSet actualDataSet=tester.getConnection().createDataSet("ttablea");
		ITable actualTable=actualDataSet.getTable("ttablea");
		ITable filteredActualTable = DefaultColumnFilter.includedColumnsTable(actualTable,expectedTable.getTableMetaData().getColumns());
		
		
		//Assertion.assertEquals(expectedTable,actualTable);
		Assertion.assertEquals(expectedTable,filteredActualTable);
		
	}

}
