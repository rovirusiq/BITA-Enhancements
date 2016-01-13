package ro.bcr.bita.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SurrogationUsage {
	
	private MappingLine mappingLine;
	private String desiredKey;
	private String inputKeysConcatenated;
	private SurrogationDefinition surrogationDefinition;
	private List<ColumnPath> paths=new ArrayList<ColumnPath>();
	
	
	
	protected SurrogationUsage(MappingLine mL, SurrogationDefinition srgDef) {
		this.mappingLine=mL;
		this.surrogationDefinition=srgDef;
	}
	
	/************************************************************************************************************
	 *
	 *G(S)etters
	 *
	 ************************************************************************************************************/
	/**
	 * @return the desiredKey
	 */
	public String getDesiredKey() {
		return desiredKey;
	}
	/**
	 * @return the inputKeysConcatenated
	 */
	public String getInputKeysConcatenated() {
		return inputKeysConcatenated;
	}
	/**
	 * @param desiredKey the desiredKey to set
	 */
	public void setDesiredKey(String desiredKey) {
		this.desiredKey = desiredKey;
	}
	/**
	 * @param inputKeysConcatenated the inputKeysConcatenated to set
	 */
	public void setInputKeysConcatenated(String inputKeysConcatenated) {
		this.inputKeysConcatenated = inputKeysConcatenated;
	}

	/**
	 * @return the mappingLine
	 */
	public MappingLine getMappingLine() {
		return mappingLine;
	}

	/**
	 * @return the surrogationDefinition
	 */
	public SurrogationDefinition getSurrogationDefinition() {
		return surrogationDefinition;
	}
	
	

	/**
	 * @return the paths
	 */
	public List<ColumnPath> getPaths() {
		return paths;
	}
	
	public void addPath(ColumnPath... newPaths) {
			this.paths.addAll(Arrays.asList(newPaths));
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SurrogationUsage [mappingLine=" + mappingLine.getName() + ", surrogationDefinition="
				+ surrogationDefinition.getName() + ", surrgationExpression="+mappingLine.getSurrogationExpression()+"]";
	}	
	

}
