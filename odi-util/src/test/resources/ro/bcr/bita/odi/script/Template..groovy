evaluate(ro.bcr.bita.odi.script.BitaScriptEnv.baseScript());

config{
	configLog{
			messagePrefix=">>>";
	}
	
	configProp{
	}
	
	configMessages{
	  msgCeva;
	}
}

execute{
	logInfo("Start Script");
	doAs "Ceva":{
			
	}
	logInfo("End Script");
}