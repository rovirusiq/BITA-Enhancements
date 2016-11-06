package ro.bcr.bita.odi.proxy

import java.sql.Connection;

import oracle.odi.core.OdiInstance

class OdiSubstitutionApiUtil {
	
	/********************************************************************************************
	 *
	 *Private methods for generating code
	 *
	 ********************************************************************************************/
	
	private static String SUBST_PHASE_1="<%";
	private static String SUBST_PHASE_2="<?";
	private static String SUBST_PHASE_3="<\$";
	private static String SUBST_PHASE_4="<@";
	
	public static String isSubstitutionPhase(com.sunopsis.dwg.snpreference.SnpReferenceInterne odiRef,String... allowed) {
		String rsp=odiRef.getInfo("SRC_ENCODED_PASS");
		if (rsp.length()<2) return "$rsp:false";
		
		String ch1=rsp.substring(0,1);
		String ch2=rsp.substring(1,2);
		
		if ("<".equals("ch")) return "$ch2:false";
		
		allowed=allowed as List;
		
		boolean found=false;
		
		OdiSubstitutionPhase next=OdiSubstitutionPhase.valueFrom(ch2);
		OdiSubstitutionPhase current=next.previousPhase();
		found=allowed.contains(current.value());

		return ch2+":"+current.value()+":"+found;
	}
	
	private static OdiEntityFactory.OdiInstanceProperties createOdiInstanceProperties(
		com.sunopsis.dwg.snpreference.SnpReferenceInterne odiRef
		,String jdbcUrl, String jdbcDriver, String jdbcUsername
		,String technology="SRC"
		){
		String jdbcEncodedPass;
		String odiUsername;
		Connection masterRepoConn;
		Connection workRepoConn;
		
		if ("SRC".equals(technology)) {
			jdbcEncodedPass = odiRef.getInfo("SRC_ENCODED_PASS");
			masterRepoConn=odiRef.getJDBCConnection("SRC");
		}  else if ("DEST".equals(technology)) {
			jdbcEncodedPass = odiRef.getInfo("DEST_ENCODED_PASS");
			masterRepoConn=odiRef.getJDBCConnection("DEST");
		} else {
			throw new BitaOdiException("When trying to create a IOdiEntityFactory from odiRef, you must specify what is the technology(SRC|DEST) which has the connection to the master repository. The scond parameter must be SRC or DEST");
		}
		
		workRepoConn=odiRef.getJDBCConnection("WORKREP");
		
		OdiWithoutInstanceUtil util=new OdiWithoutInstanceUtil();
		
		odiUsername=util.retrieveUsernameForSession(workRepoConn,odiRef.getSession("SESS_NO"));
		char[] odiPasswordEncrypted=util.retrieveEncryptedPassword(odiUsername,masterRepoConn,true);
		String workRepoName=util.retrieveWorkRepName(workRepoConn);
		
		
		char[] odiPasswordDecrypted=util.decryptPasswordWithoutOdiInstance(odiPasswordEncrypted,workRepoConn);
		char[] masterPasswordDecrypted=util.decryptPasswordWithoutOdiInstance(jdbcEncodedPass.toCharArray(),workRepoConn);
		
		
		String p1=new String(odiPasswordDecrypted);
		String p2=new String(masterPasswordDecrypted);
		String p3=new String(odiPasswordEncrypted);
		
		OdiEntityFactory.OdiInstanceProperties odiProps=new OdiEntityFactory.OdiInstanceProperties();
		odiProps.setJdbcDriverClassName(jdbcDriver);
		odiProps.setJdbcUrl(jdbcUrl);
		odiProps.setMasterRepositoryUsername(jdbcUsername);
		odiProps.setMasterRepositoryPassword(masterPasswordDecrypted);
		odiProps.setOdiUsername(odiUsername);
		odiProps.setOdiPassword(odiPasswordDecrypted);
		odiProps.setWorkRepositoryName(workRepoName);
		
		return odiProps;
	}
	
	
	private static String generateCode(
		boolean isOdiEntityFactory,boolean isSubstitutionPhase,String nameOfObject,String technology="SRC"
		,com.sunopsis.dwg.snpreference.SnpReferenceInterne odiRef
		) {
		String jdbcUrl;
		String jdbcDriver;
		String jdbcUsername;
		
		if ("SRC".equals(technology)) {
			jdbcUrl = odiRef.getInfo("SRC_JAVA_URL");
			jdbcDriver = odiRef.getInfo("SRC_JAVA_DRIVER");
			jdbcUsername = odiRef.getInfo("SRC_USER_NAME");
		} else if ("DEST".equals(technology)) {
			jdbcUrl = odiRef.getInfo("DEST_JAVA_URL");
			jdbcDriver = odiRef.getInfo("DEST_JAVA_DRIVER");
			jdbcUsername = odiRef.getInfo("DEST_USER_NAME");
		} else {
			throw new BitaOdiException("When trying to create a IOdiEntityFactory from odiRef, you must specify what is the technology(SRC|DEST) which has the connection to the master repository. The scond parameter must be SRC or DEST");
		}
		
		StringBuilder codeToBeGenerated=new StringBuilder();
		if (isSubstitutionPhase) codeToBeGenerated.append("<@").append(String.format("%n"));
		codeToBeGenerated.append("import ro.bcr.bita.odi.proxy.*;").append(String.format("%n"));
		codeToBeGenerated.append("import oracle.odi.core.*;").append(String.format("%n"));
		
		if (isOdiEntityFactory) {
			codeToBeGenerated.append(generateCodeForFactory(nameOfObject,jdbcUrl,jdbcDriver,jdbcUsername,technology));
		} else {
			codeToBeGenerated.append(generateCodeForOdiInstance(nameOfObject,jdbcUrl,jdbcDriver,jdbcUsername,technology));
		}
		codeToBeGenerated.append(String.format("%n"));
		if (isSubstitutionPhase) codeToBeGenerated.append("@>");
		
		return codeToBeGenerated.toString();
		
	}
		
	private static String generateCodeForFactory(String nameOfObject,String jdbcUrl, String jdbcDriver, String jdbcUsername,String technology) {
		return "IOdiEntityFactory ${nameOfObject}=OdiSubstitutionApiUtil.createOdiFactory(odiRef,\"$jdbcUrl\",\"$jdbcDriver\",\"$jdbcUsername\",\"$technology\");";
	}
	
	private static String generateCodeForOdiInstance(String nameOfObject,String jdbcUrl, String jdbcDriver, String jdbcUsername,String technology) {
		return "OdiInstance ${nameOfObject}=OdiSubstitutionApiUtil.createOdiInstance(odiRef,\"$jdbcUrl\",\"$jdbcDriver\",\"$jdbcUsername\",\"$technology\");";
	}
	
	/********************************************************************************************
	 *
	 *Private method for creating OdiFactory
	 *
	 ********************************************************************************************/
	
	/********************************************************************************************
	 *
	 * ODI Instance in Substitution Phase
	 *
	 ********************************************************************************************/
	
	public static OdiInstance createOdiInstance(
		
		com.sunopsis.dwg.snpreference.SnpReferenceInterne odiRef
		,String jdbcUrl, String jdbcDriver, String jdbcUsername
		,String technology="SRC"
		) {
	
		OdiEntityFactory.OdiInstanceProperties odiProps=OdiSubstitutionApiUtil.createOdiInstanceProperties(odiRef,jdbcUrl,jdbcDriver,jdbcUsername,technology);
		
		return OdiEntityFactory.createOdiInstanceFromProperties(odiProps);
	
	}
	
	public static String createOdiInstanceInSubstitutionPhaseFromTargetTechnology(com.sunopsis.dwg.snpreference.SnpReferenceInterne odiRef,String nameOfObject) {
		return createOdiInstanceInSubstitutionPhase(odiRef,nameOfObject,"DEST");
	}
	
	public static String createOdiInstanceInSubstitutionPhaseFromSourceTechnology(com.sunopsis.dwg.snpreference.SnpReferenceInterne odiRef,String nameOfObject) {
		return createOdiInstanceInSubstitutionPhase(odiRef,nameOfObject,"SRC");
	}
	
	public static String createOdiInstanceInSubstitutionPhase(com.sunopsis.dwg.snpreference.SnpReferenceInterne odiRef,String nameOfObject,String technology="SRC") {
		return generateCode(false,true,nameOfObject,technology,odiRef);
		
	}
	
	/********************************************************************************************
	 *
	 * ODI Factory in Substitution Phase
	 *
	 ********************************************************************************************/
	public static IOdiEntityFactory createOdiFactory(
		com.sunopsis.dwg.snpreference.SnpReferenceInterne odiRef
		,String jdbcUrl, String jdbcDriver, String jdbcUsername
		,String technology="SRC"
		) {
	
		OdiEntityFactory.OdiInstanceProperties odiProps=OdiSubstitutionApiUtil.createOdiInstanceProperties(odiRef,jdbcUrl,jdbcDriver,jdbcUsername,technology);
		
		return OdiEntityFactory.createInstanceFromProperties(odiProps);
		
		
	}
	
	
	public static String createOdiFactoryInSubstitutionPhaseFromTargetTechnology(com.sunopsis.dwg.snpreference.SnpReferenceInterne odiRef,String nameOfObject) {
		return createOdiFactoryInSubstitutionPhase(odiRef,nameOfObject,"DEST");
	}
	
	public static String createOdiFactoryInSubstitutionPhaseFromSourceTechnology(com.sunopsis.dwg.snpreference.SnpReferenceInterne odiRef,String nameOfObject) {
		return createOdiFactoryInSubstitutionPhase(odiRef,nameOfObject,"SRC");
	}
	
	public static String createOdiFactoryInSubstitutionPhase(com.sunopsis.dwg.snpreference.SnpReferenceInterne odiRef,String nameOfObject,String technology="SRC") {
		return generateCode(true,true,nameOfObject,technology,odiRef);
	}
	
	/********************************************************************************************
	 *
	 * ODI Instance in Generated Code
	 *
	 ********************************************************************************************/
	
	
	
	public static String createOdiInstanceInGeneratedCodeFromTargetTechnology(com.sunopsis.dwg.snpreference.SnpReferenceInterne odiRef,String nameOfObject) {
		return createOdiInstanceInGeneratedCode(odiRef,nameOfObject,"DEST");
	}
	
	public static String createOdiInstanceInGeneratedCodeFromSourceTechnology(com.sunopsis.dwg.snpreference.SnpReferenceInterne odiRef,String nameOfObject) {
		return createOdiInstanceInGeneratedCode(odiRef,nameOfObject,"SRC");
	}
	
	
	public static String createOdiInstanceInGeneratedCode(com.sunopsis.dwg.snpreference.SnpReferenceInterne odiRef,String nameOfObject,String technology="SRC") {
		return generateCode(false,false,nameOfObject,technology,odiRef);
	}
	
	
	/********************************************************************************************
	 *
	 * ODI Factory in Generated Code
	 *
	 ********************************************************************************************/
	public static String createOdiFactoryInGeneratedCodeFromTargetTechnology(com.sunopsis.dwg.snpreference.SnpReferenceInterne odiRef,String nameOfObject) {
		createOdiFactoryInGeneratedCode(odiRef,nameOfObject,"DEST");
	}
	
	public static String createOdiFactoryInGeneratedCodeFromSourceTechnology(com.sunopsis.dwg.snpreference.SnpReferenceInterne odiRef,String nameOfObject) {
		createOdiFactoryInGeneratedCode(odiRef,nameOfObject,"SRC");
	}
	
	public static String createOdiFactoryInGeneratedCode(com.sunopsis.dwg.snpreference.SnpReferenceInterne odiRef,String nameOfObject,String technology="SRC") {
		return generateCode(true,false,nameOfObject,technology,odiRef);
	}
	
}