package ro.bcr.bita.odi.script

import spock.lang.Specification
import java.util.logging.Level;

//TODO test de counter
class ScriptLoggerTest extends Specification {
	
	private ScriptLogger subject;
	
	def setup() {
		subject=new ScriptLogger();
	}
	
	def "Configuration is assimilated correctly"(){
		given:"A configuration for a name, messagePrefix and level INFO"
			ScriptLogger.Config conf=ScriptLogger.createBlankConfiguration();
			def name="TestLogger";
			def messagePrefix=">>>";
			
			conf.name=name;
			conf.level=Level.INFO.getName();
			conf.messagePrefix=messagePrefix;
			
		when:"Configuration is provide to the logger"
			subject.configure(conf);
		then:"Name, mesagePrefix and level should be the ones provided"
			subject.logger.level==Level.INFO;
			subject.logger.name==name;
			subject.messagePrefix==messagePrefix;
		
	}
	
	def "Invalid Configuration is rejected"(){
		given:"An invalid configuration with empty name"
			ScriptLogger.Config conf=ScriptLogger.createBlankConfiguration();
			
			conf.name="";
			conf.level=Level.INFO.getName();
			conf.messagePrefix=">>>";
			
		when:"The configuration is applied"
			subject.configure(conf);
		then:"An BitaScriptException is thrown"
			thrown BitaScriptException;
		when:"The configuration has null name"
			conf.name="";
			conf.level=Level.INFO.getName();
			conf.messagePrefix=">>>";
			subject.configure(conf);
		then:"An BitaScriptException is thrown"
			thrown BitaScriptException;
		when:"The configuration has invalid level"
			conf.name="Smth";
			conf.level="KKT";
			conf.messagePrefix=">>>";
			subject.configure(conf);
		then:"The default level is used"
			subject.logger.getLevel()==subject.getDefaultLevel();
		when:"The configuration has null level"
			conf.name="Smth";
			conf.level=null;
			conf.messagePrefix=">>>";
			subject.configure(conf);
		then:"The default level is used"
			subject.logger.getLevel()==subject.getDefaultLevel();
		when:"The configuration has empty prefix"
			conf.name="Smth";
			conf.level="INFO";
			conf.messagePrefix="";
			subject.configure(conf);
			subject.logInfo("No prefix");
		then:"The message is logged"
			subject.getMessagesLogged()==1;
			subject.setExplicitlyTheCounter(0);
		when:"The configuration has null prefix"
			conf.name="Smth";
			conf.level="INFO";
			conf.messagePrefix=null;
			subject.configure(conf);
			subject.logInfo("No prefix");
		then:"The message is logged"
			subject.getMessagesLogged()==1;
			subject.setExplicitlyTheCounter(0);
	}
	
	def "Logging of the mesages is done according to the level"(){
		given:"A configuration for a name, messagePrefix and level INFO"
			ScriptLogger.Config conf=ScriptLogger.createBlankConfiguration();
			def name="TestLogger";
			def messagePrefix=">>>";
			
			conf.name=name;
			conf.level=Level.INFO.getName();
			conf.messagePrefix=messagePrefix;
			
			subject.configure(conf);
		when:"A request to log a meesage with an equal priority than the one allowed is issued"
			
			subject.logInfo("Message with same log level");
			
		then:"The message is logged"
			
			subject.getMessagesLogged()==1;
			subject.setExplicitlyTheCounter(0);
		
		when:"A request to log a meesage with a greater priority than the one allowed is issued"
			
			subject.logWarning("Message with greater priority log level");
			subject.logError("Message with even greater priority log level");
			
		then:"The message is logged"
			
			subject.getMessagesLogged()==2;
			subject.setExplicitlyTheCounter(0);
		
		when:"A request to log a meesage with a lower priority than the one allowed is issued"
			
			subject.logDebug("Message with greater priority log level");
		
		then:"The message is not logged"
			subject.getMessagesLogged()==0;
			subject.setExplicitlyTheCounter(0);
	}
	
	def "When changing configuration during execution"(){
		given:	"A configuration for a name, messagePrefix and level INFO"
				ScriptLogger.Config conf=ScriptLogger.createBlankConfiguration();
				def name="TestLogger";
				def messagePrefix="uuu";
				
				conf.name=name;
				conf.level=Level.WARNING.getName();
				conf.messagePrefix=messagePrefix;
				
				ScriptLogger.Config  anotherConf;
				
				subject.configure(conf);
		when:	"A request to log a messages is issued and the prioroty of that message is higher than the current level"
				subject.logError("Something");
		
		then:	"The message is logged"
				subject.getMessagesLogged()==1;
				
		when:	"Configuration is retrieved from the Logger"
				anotherConf=subject.createCopyOfConfiguration();
				
		then:	"The configuration obtained matches the one of the ScriptLogger"
				anotherConf.name==subject.logger.name;
				anotherConf.messagePrefix==subject.messagePrefix;
				anotherConf.level==subject.logger.getLevel().getName();
		
				
	}

}
