package ro.bcr.bita.mapping.dependency.jc


class JcParameters {
		
		private JcParameters() {
			
		}
		
		private String dwhRelease="JC_SQL_RELEASE";
		private String dwhVersion="JC_SQL_VERSION";
		private  String jobGroupName="JC_BMF";		
		
		public static JcParameters newParameters(String jobGroupName,String dwhRelease,String dwhVersion) {
			JcParameters params=new JcParameters();
			params.dwhRelease=dwhRelease;
			params.dwhVersion=dwhVersion;
			params.jobGroupName=jobGroupName;
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
		
		
		
		

}
