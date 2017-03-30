package ro.bcr.bita.mapping.analyze

import ro.bcr.bita.core.IBitaGlobals
import ro.bcr.bita.mapping.analyze.MappingDependencyAnalyzerProcessor
import ro.bcr.bita.sql.IBitaSqlExecutor

import groovy.transform.CompileStatic
import groovy.transform.TypeChecked

public class JcRequestContext implements IBitaGlobals {
	
		private String dwhRelease="JC_SQL_RELEASE";
		private String dwhVersion="JC_SQL_VERSION";
		private String scenarioVersion=BITA_ODI_SCENARIO_VERSION;
		private Boolean isParameterDeleteRequired=true;
		private Boolean isDependencyDeleteRequired=true;
		private String jcSchemaName="O_LDS_META";
		private IJcJobIdGenerator jobIdGenerator=new JcJobIdGenerator();
		private IJcGroupIdentificationStrategy groupIdentificationStrategy;

			
		public JcRequestContext(String dwhVersion,IJcGroupIdentificationStrategy grpIdentifcationStratgey) {
			this.dwhVersion=dwhVersion;
			this.dwhRelease=dwhVersion;
			this.groupIdentificationStrategy=grpIdentifcationStratgey;
			//add test to veryfy startegyu and version
			
		}
	
		private JcRequestContext() {
			
		}
		
		private JcRequestContext(JcRequestContext copyFromParams) {
			this.dwhRelease=copyFromParams.dwhRelease;
			this.dwhVersion=copyFromParams.dwhVersion;
			this.scenarioVersion=copyFromParams.scenarioVersion;
			this.jobIdGenerator=copyFromParams.jobIdGenerator;
			this.isParameterDeleteRequired=copyFromParams.isParameterDeleteRequired;
			this.isDependencyDeleteRequired=copyFromParams.isDependencyDeleteRequired;
			this.jcSchemaName=copyFromParams.jcSchemaName;
			this.groupIdentificationStrategy=copyFromParams.groupIdentificationStrategy;
			
		}
				
		public JcRequestContext newDwhRelease(String newDwhRelease) {
			JcRequestContext params=new JcRequestContext(this);
			params.dwhRelease=newDwhRelease;
			return params;
		}
		
		public JcRequestContext newJcSchemaName(String newJcSchemaName) {
			JcRequestContext params=new JcRequestContext(this);
			params.jcSchemaName=newJcSchemaName;
			return params;
		}
		
		public JcRequestContext newScenarioVersion(String newScenarioVersion) {
			JcRequestContext params=new JcRequestContext(this);
			params.scenarioVersion=newScenarioVersion;
			return params;
		}
		
		public JcRequestContext newJobIdGenerator(IJcJobIdGenerator newJobIdGenerator) {
			JcRequestContext params=new JcRequestContext(this);
			params.jobIdGenerator=newJobIdGenerator;
			return params;
		}
		
		public JcRequestContext newParameterDeleteRequired(Boolean newV) {
			JcRequestContext params=new JcRequestContext(this);
			params.isParameterDeleteRequired=newV;
			return params;
		}
		
		public JcRequestContext newDependencyDeleteRequired(Boolean newV) {
			JcRequestContext params=new JcRequestContext(this);
			params.isDependencyDeleteRequired=newV;
			return params;
		}
	
		/**
		 * @return the dwhRelease
		 */
		public String getDwhRelease() {
			return dwhRelease;
		}

		/**
		 * @return the dwhVersion
		 */
		public String getDwhVersion() {
			return dwhVersion;
		}

		/**
		 * @return the jobGroupName
		 */
		public String getJobGroupName() {
			return jobGroupName;
		}

		/**
		 * @return the scenarioVersion
		 */
		public String getScenarioVersion() {
			return scenarioVersion;
		}
		
		public boolean isScenarioVersionDefault() {
			return BITA_ODI_SCENARIO_VERSION.equals(this.scenarioVersion);
		}

		/**
		 * @return the isParameterDeleteRequired
		 */
		public Boolean getIsParameterDeleteRequired() {
			return isParameterDeleteRequired;
		}


		/**
		 * @return the jobIdGenerator
		 */
		public IJcJobIdGenerator getJobIdGenerator() {
			return jobIdGenerator;
		}
		
		protected String generateJobId(String mappingName) {
			return this.jobIdGenerator.generateJobId(this,mappingName);
		}
		
		protected void createGroups(MappingDependencyAnalyzerProcessor dependAnalyzer,IBitaSqlExecutor bitaSqlExecutor) {
			this.groupIdentificationStrategy.createGroups(this,dependAnalyzer,bitaSqlExecutor);
		}

		/**
		 * @return the jcSchemaName
		 */
		public String getJcSchemaName() {
			return jcSchemaName;
		}

		/**
		 * @return the groupIdentificationStrategy
		 */
		public IJcGroupIdentificationStrategy getGroupIdentificationStrategy() {
			return groupIdentificationStrategy;
		}

		/**
		 * @return the isDependencyDeleteRequired
		 */
		public Boolean getIsDependencyDeleteRequired() {
			return isDependencyDeleteRequired;
		}
		
		

		
		
		
		
		
		
		
		
		
		

}
