package ro.bcr.bita.server.service

import ro.bcr.bita.odi.proxy.IOdiRemoteOperations
import ro.bcr.bita.odi.proxy.OdiScenExecutionEnv
import ro.bcr.bita.odi.proxy.OdiScenExecutionStatus;

import groovy.transform.CompileStatic

import java.util.concurrent.atomic.AtomicInteger

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@CompileStatic
public class RestOdiOperationsService implements IRestOdiOperations{
	
	private Logger logger=LoggerFactory.getLogger(RestOdiOperationsService);
	
	private AtomicInteger counter=new AtomicInteger(0);
	
	private OdiScenExecutionEnv odiEnv;
		
	private ro.bcr.bita.odi.proxy.IOdiRemoteOperations odiService;
	
	public RestOdiOperationsService(OdiScenExecutionEnv env,IOdiRemoteOperations odiService) {
		this.odiEnv=env;
		this.odiService=odiService;
		
	}
	
	@Override
	public OdiScenarioExecutionResult executeScenario(String scenName,String scenVersion,String opDescription, HashMap<String,String> scenParams) {
		
		logger.info("Starting execution of scenario[$scenName] with version[$scenVersion]");
		
		//extra debug if it is the case
		
		OdiScenarioExecutionResult rsp=new OdiScenarioExecutionResult();
		
		OdiScenExecutionStatus info=odiService.executeScenario(odiEnv,scenName,scenVersion,opDescription,scenParams,"");
		
		logger.info("Finished execution of scenario[$scenName] with version[$scenVersion]");
		logger.info("ODI Session number: "+info.getSessionNumber());
		logger.info("ODI Execution Code: "+info.getCode());
			
		rsp.code=info.getCode();
		rsp.odiSessionNumber=info.getSessionNumber();
		rsp.message=info.getMessage();
		
		if (logger.isDebugEnabled()) {
			logger.debug("Response POJO has Code:"+rsp.code);
			logger.debug("Response POJO has SessionNumber:"+rsp.odiSessionNumber);
			logger.debug("Response POJO has Message:"+rsp.message);
		}
		
		return rsp;
		
	}

}
