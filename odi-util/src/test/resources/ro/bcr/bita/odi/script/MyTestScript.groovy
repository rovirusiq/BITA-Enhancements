evaluate(ro.bcr.bita.odi.script.BitaScriptEnv.baseScript());

def String messageFromMyScript="Message From MyScript";

config{
	
	configLog{
		messagePrefix=">>>";
	}
	
	configProp{
		myVar="Uraaaa";
	}
	
	configMessages{
		msgCeva;
	}
}

execute{
	logInfo("Alo1");
	doAs "Ceva":{
		logInfo("Alo2");
		doAs "Altceva":{
			logInfo("Alo3");
			withoutOdiTransaction{
				logInfo("Alo4:"+myVar);
			}
		}
		inOdiTransaction{
			logInfo("Alo5");
		}
	}
	
}