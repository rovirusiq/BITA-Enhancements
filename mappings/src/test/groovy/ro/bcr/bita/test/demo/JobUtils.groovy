package ro.bcr.bita.test.demo
/*
 * Simulates an ODI job
 */
import groovy.sql.Sql

import ro.ns.test.infrastructure.HSQLInfrastructureProvider;

class JobUtils {
	
	public static void executeJob(String name) {
		println "->Executing job";
		HSQLInfrastructureProvider infP=new HSQLInfrastructureProvider();
		Sql db=infP.createSqlGroovyObject();
		db.execute("""
			INSERT INTO TTABLEA (ID,DESC)
			select * from stable1;
		""");
		println "->Job was Executed";
	}

}
