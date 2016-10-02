package ro.bcr.bita.server.web;

import ro.bcr.bita.server.BitaRestException;
import ro.bcr.bita.server.service.OdiScenarioExecutionResult;
import ro.bcr.bita.server.service.RestOdiOperationsService

import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.http.HttpServletRequest;

import groovy.transform.CompileStatic;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@CompileStatic
@RequestMapping(value="/")
public class OdiController {
	
	
	private Logger logger=org.slf4j.LoggerFactory.getLogger(OdiController);
	
	@Autowired
	@Qualifier("restOdiService")
	private RestOdiOperationsService restOdiService;
	
	
	private AtomicLong counter;
	
	@RequestMapping(value="odi-scenario-execution",method=RequestMethod.POST,produces= ["application/xml","application/json"])
	@ResponseBody
	//TODO check if pN.size()==pV.size()
	public OdiScenarioExecutionResult odiMapping(
			 @RequestParam(name="scenName") String scenName
			,@RequestParam(name="scenVersion") String scenVersion
			,@RequestParam(name="opDescription",defaultValue="") String opDescription
			,@RequestParam(name="paramn",defaultValue="")String[] pN
			,@RequestParam(name="paramv",defaultValue="")String[] pV
			) {
		
		logger.info("Starting method execution - odiMapping");
		
		boolean isSizeEqual=(pN.length==pV.length);
		
		if (logger.isDebugEnabled()) {
			logger.debug("Parameter Names received:$pN");
			logger.debug("Parameter Values received:$pV");
			logger.debug("Size is equal:$isSizeEqual");
		}
		
		if (!isSizeEqual) throw new BitaRestException("Number of Parameter Names and number of Parameter Values do not match");
				
		if ("".equals(opDescription)) {
			logger.debug("Apply algorithm to calculate defult value for Odi Operator description");
			opDescription="REST call - "+scenName + "-"+counter;
			logger.debug("Odi Operator Description:"+opDescription);
			counter.compareAndSet(Long.MAX_VALUE-1,0);
		}
		
		
		try {
			HashMap<String,String> scenParams=[:];
			
			pN.eachWithIndex  {String e,Integer idx->
				if (!"".equals(pN)) {
					scenParams.put(e,pV[idx]);
				}
			}
			
			if (logger.isDebugEnabled()) logger.debug("Parameters harvested from request: $scenParams");
			
			OdiScenarioExecutionResult rsp=restOdiService.executeScenario(scenName,scenVersion,opDescription,scenParams);
			
			logger.info("Finished method execution - odiMapping");
			
			return rsp;
		} catch (Exception ex) {
			throw new BitaRestException("Rest Call for scenario execution[$scenName;$scenVersion] ended with exception.", ex);
		}
	}
			
	@ExceptionHandler(BitaRestException)
	@ResponseBody	
	public OdiScenarioExecutionResult handleError(HttpServletRequest req, Exception e) {
		logger.error("Exception Encountered:"+e);
		OdiScenarioExecutionResult rsp=new OdiScenarioExecutionResult();
		rsp.setCode("ERROR");
		rsp.setMessage(e.getMessage());
		return rsp;
	}

}
