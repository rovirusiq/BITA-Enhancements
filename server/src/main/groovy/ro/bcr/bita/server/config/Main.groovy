package ro.bcr.bita.server.config

import ro.bcr.bita.odi.proxy.IOdiRemoteEntityFactory
import ro.bcr.bita.odi.proxy.IOdiRemoteOperations
import ro.bcr.bita.odi.proxy.OdiRemoteEntityFactory
import ro.bcr.bita.odi.proxy.OdiRemoteOperationsService
import ro.bcr.bita.odi.proxy.OdiScenExecutionEnv
import ro.bcr.bita.server.service.RestOdiOperationsService

import groovy.transform.CompileStatic

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@CompileStatic
class Main {
	
	
	@Bean
	protected  BitaOdiConfig bitaRestConfig() {
		return new BitaOdiConfig();
	}
	
	@Autowired
	private BitaOdiConfig bitaOdiConfig;
	
	@Bean
	public IOdiRemoteEntityFactory odiRuntimeEntityFactory() {
		return OdiRemoteEntityFactory.newInstance();
	}
	
	@Bean
	public RestOdiOperationsService restOdiService() {
		
		IOdiRemoteEntityFactory fact=OdiRemoteEntityFactory.newInstance();
		
		OdiScenExecutionEnv env=fact.newOdiScenExecutionEnv(
			bitaOdiConfig.getAgentUrl(),
			bitaOdiConfig.getUsername(),
			bitaOdiConfig.getPassword(),
			bitaOdiConfig.getContext(),
			bitaOdiConfig.getLogLevel(),
			bitaOdiConfig.getWorkRepositoryName(),
			);
		
		IOdiRemoteOperations odiService=new OdiRemoteOperationsService(fact);
		
		
		return new RestOdiOperationsService(env,odiService)
		
	}
	 

}
