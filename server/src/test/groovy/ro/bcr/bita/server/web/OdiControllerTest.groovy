package ro.bcr.bita.server.web


import ro.bcr.bita.server.ServerApplication;

import spock.lang.Specification;

import org.junit.runner.RunWith;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@SpringApplicationConfiguration(ServerApplication)
class OdiControllerTest extends Specification{
	
	def setup() {
		
	}
	
	def "Test context creation"(){
		given:
		when:
			def a=3;
		then:
			1>0;
	}

}
