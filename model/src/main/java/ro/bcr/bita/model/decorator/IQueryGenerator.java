package ro.bcr.bita.model.decorator;

import java.util.List;
import java.util.Map;

public interface IQueryGenerator {

	public List<Map<String,List<Object>>> generateQueries();
	
}
