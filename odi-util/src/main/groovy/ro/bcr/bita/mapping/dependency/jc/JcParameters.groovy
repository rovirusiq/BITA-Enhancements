package ro.bcr.bita.mapping.dependency.jc

import ro.bcr.bita.core.IBitaGlobals;


class JcParameters implements IBitaGlobals {
		
		private JcParameters() {
			
		}
		
		private String dwhRelease="JC_SQL_RELEASE";
		private String dwhVersion="JC_SQL_VERSION";
		private  String jobGroupName="JC_BMF";
		private String scenarioVersion=BITA_ODI_SCENARIO_VERSION;
		
		public static JcParameters newParameters(String jobGroupName,String dwhRelease,String dwhVersion,String scenarioVersion=BITA_ODI_SCENARIO_VERSION) {
			JcParameters params=new JcParameters();
			params.dwhRelease=dwhRelease;
			params.dwhVersion=dwhVersion;
			params.jobGroupName=jobGroupName;
			params.scenarioVersion=scenarioVersion;
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
		
		
		
		
		
		

}
