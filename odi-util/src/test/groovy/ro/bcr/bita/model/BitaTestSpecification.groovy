package ro.bcr.bita.model

import ro.bcr.bita.odi.proxy.IOdiRemoteEntityFactory;
import ro.bcr.bita.odi.proxy.OdiScenExecutionEnv;
import ro.bcr.bita.odi.proxy.OdiScenExecutionStatus;

import spock.lang.Shared;
import spock.lang.Specification

class BitaTestSpecification extends Specification {
	
	protected IOdiRemoteEntityFactory stbOdiRemoteEntityFactory=Stub();
	
	protected setup() {
		configure(stbOdiRemoteEntityFactory);
	}

	/********************************************************************************************
	 *Configuration methods
	 ********************************************************************************************/
	private configure(IOdiRemoteEntityFactory stbOdiRemoteEntityFactory) {
		stbOdiRemoteEntityFactory.newOdiScenExecutionEnv(_ as String,_ as String,_ as char[],_ as String,_ as String,_ as Integer,_ as String)>>{String agentUrl,String odiUserName,char[] odiPassword,String odiContext,Integer odiAgentLogLevel,String workRepName ->
			return new OdiScenExecutionEnv(agentUrl,odiUserName,odiPassword,odiContext,odiAgentLogLevel,workRepName);
		}

		stbOdiRemoteEntityFactory.newOdiScenExecutionStatus(_ as String, _ as String, _ as String)>>{String sessionNumber="",String code="",String message=""->
			return new OdiScenExecutionStatus(""+sessionNumber,code,message);
		}
	}
}
