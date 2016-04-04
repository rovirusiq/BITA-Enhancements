package ro.bcr.bita.odi.script
import java.util.logging.Logger;
import java.util.logging.Level;

class ScriptLogger {
	
	private Logger logger;
	private String messagePrefix="";
	private long counter=0;
	private defaultLevel=Level.INFO;
	
	
	public ScriptLogger() {
		this.configure(new Config());
	}
	
	public static class Config{
		
		def String name="AnnonymousLogger";
		def String messagePrefix="###";
		def String level="";
		
	}
	
	public void configure(ScriptLogger.Config conf) throws BitaScriptException{
		boolean invalidParams=false;
		if ((conf.name==null) || ("".equals(conf.name))){
			invalidParams=true;
		}
		if (invalidParams) throw new BitaScriptException("Invalid configuration. Name must not be null or empty.");
		
		this.logger=Logger.getLogger(conf.name);
		this.messagePrefix=conf.messagePrefix?:"";
		try {
			this.logger.setLevel(Level.parse(conf.level));	
		} catch (Exception ex) {
			this.logger.setLevel(this.defaultLevel);
		}
	}
	
	public void logInfo(String msg) {
		if (this.logger.getLevel().intValue()<=Level.INFO.intValue()) {
			increaseCounter();
			this.logger.info(messagePrefix+msg);
		}
	}
	public void logDebug(String msg){
		if (this.logger.getLevel().intValue()<=Level.FINE.intValue()) {
			increaseCounter();
			this.logger.log(Level.FINE,messagePrefix+msg);
		}
	}
	public void logWarning(String msg) {
		if (this.logger.getLevel().intValue()<=Level.WARNING.intValue()) {
			increaseCounter();
			this.logger.warning(messagePrefix+msg);
		}
	}
	public void logError(String msg) {
		increaseCounter();
		this.logger?.log(Level.SEVERE,messagePrefix+msg);
	}
	
	public ScriptLogger.Config createCopyOfConfiguration() {
		ScriptLogger.Config c=new ScriptLogger.Config();
		c.name=this.logger?.getName();
		c.level=this.logger?.getLevel()?.getName();
		c.messagePrefix=this.messagePrefix;
		return c;
	}
	
	private void increaseCounter(){
		if (counter==Long.MAX_VALUE) {
			counter=0;
		}
		counter++;
	}
	
	protected void setExplicitlyTheCounter(long newValue){
		this.counter=newValue;
	}
	
	protected long getMessagesLogged(){
		return counter;
	}
	
	protected Level getDefaultLevel() {
		return this.defaultLevel;
	}
	
	public static createBlankConfiguration() {
		return new ScriptLogger.Config();
	}

}