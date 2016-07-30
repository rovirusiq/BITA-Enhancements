package ro.bcr.bita.model

import groovy.transform.CompileStatic;
import groovy.transform.TypeChecked;

@CompileStatic
class MessageCollectionUtil {
	
	
	public void print(IMessageCollection msgColl,PrintStream toWriter) {
		
		if (!(msgColl instanceof MessageCollection)) throw new RuntimeException("Cannot treat this type of IMessageCollection");
		
		((MessageCollection)msgColl).each{String k,List<String> msgs ->
			toWriter.append("--$k").append(System.getProperty("line.separator"));
			msgs.each{String it->
				toWriter.append("$it;").append(System.getProperty("line.separator"));
			}
		}
		toWriter.flush();
	}

}
