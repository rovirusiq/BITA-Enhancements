package ro.bcr.bita.app

import ro.bcr.bita.core.IBitaGlobals
import ro.bcr.bita.mapping.analyze.BitaMappingAnalyzerService
import ro.bcr.bita.mapping.analyze.IJcGroupIdentificationStrategy
import ro.bcr.bita.mapping.analyze.IMappingAnalyzerService
import ro.bcr.bita.mapping.analyze.JcDalGroupStrategy
import ro.bcr.bita.mapping.analyze.JcJobIdGenerator
import ro.bcr.bita.mapping.analyze.JcLdsGroupStrategy
import ro.bcr.bita.mapping.analyze.JcRequestContext
import ro.bcr.bita.mapping.analyze.JcSqlCommandsGenerator
import ro.bcr.bita.mapping.analyze.MappingDependencyAnalyzerProcessor
import ro.bcr.bita.model.BitaDomainFactory
import ro.bcr.bita.model.BitaModelFactory
import ro.bcr.bita.model.IBitaDomainFactory
import ro.bcr.bita.model.IBitaModelFactory
import ro.bcr.bita.model.IMappingDependencyRepositoryCyclicAware
import ro.bcr.bita.model.IMessageCollection
import ro.bcr.bita.model.MappingDependencyRepository
import ro.bcr.bita.odi.proxy.IOdiEntityFactory
import ro.bcr.bita.odi.proxy.OdiEntityFactory
import ro.bcr.bita.model.BitaDomainFactoryForMappingsWithMultipleTargets
import ro.bcr.bita.app.OraclePhysicalServerConnectionProvider
import oracle.odi.core.OdiInstance

class BitaAppFactory implements IBitaGlobals{
	
	private final IBitaModelFactory bitaModelFactory=BitaModelFactory.newInstance();
	private final IOdiEntityFactory odiEntityFactory;
	private final IBitaDomainFactory bitaDomainFactory;
	
	private BitaAppFactory(OdiInstance odiInstance) {
		this.odiEntityFactory=OdiEntityFactory.newInstance(odiInstance);
		this.bitaDomainFactory=BitaDomainFactoryForMappingsWithMultipleTargets.newInstance(bitaModelFactory,this.odiEntityFactory);
	}
	/********************************************************************************************
	 *
	 *Static factory method initialization
	 *
	 ********************************************************************************************/
	public static BitaAppFactory newInstance(OdiInstance odiInstance) {
		return new BitaAppFactory(odiInstance);
	}
	
	/********************************************************************************************
	 *
	 *Private/Protected helper methods 
	 *
	 ********************************************************************************************/
	
	protected IMappingDependencyRepositoryCyclicAware createMappingDependencyRepository() {
		return new MappingDependencyRepository();
	}
	
	protected JcSqlCommandsGenerator newJcSqlCommandGenerator() {
		return new JcSqlCommandsGenerator();
	}
	
	protected MappingDependencyAnalyzerProcessor newMappingDependencyAnalyzerProcessor() {
		return new MappingDependencyAnalyzerProcessor(this.createMappingDependencyRepository());
	}
	
	protected IBitaDomainFactory getBitaDomainFactory() {
		return this.bitaDomainFactory;
	}
	
	protected IOdiEntityFactory getOdiEntityFactory() {
		return this.odiEntityFactory;
	}
	
	/********************************************************************************************
	 *
	 *Full Functional Medium level components
	 *
	 ********************************************************************************************/
	public IMappingAnalyzerService newMappingAnalyzerService() {
		IMappingAnalyzerService rsp=new BitaMappingAnalyzerService(this.bitaDomainFactory.newOdiTemplate());
		return rsp;
	}
	
	public IMessageCollection newMessageCollection(String id) {
		return bitaModelFactory.newMessageCollection(id);
	}
	
	public JcRequestContext newJcRequestContext(String dwhVersion,IJcGroupIdentificationStrategy grpIdentifcationStrategy) {
		return new JcRequestContext(dwhVersion,grpIdentifcationStrategy);
	}
	
	public IBitaJdbcConnectionProvider newOracleConnectionProvider(String topologyServerName) {
		return new OraclePhysicalServerConnectionProvider(topologyServerName,odiEntityFactory)
	}
	
	
	/********************************************************************************************
	 *
	 *High Level Functions
	 *
	 ********************************************************************************************/
	private JcGroupsCreator newConfiguredJcGroupsCreator(String dwhVersionCd,String topologyServerName) {
		JcGroupsCreator jcGroups=new JcGroupsCreator(bitaDomainFactory,odiEntityFactory);
		jcGroups.setJdbcProvider(newOracleConnectionProvider(topologyServerName));
		jcGroups.setDwhVersionCode(dwhVersionCd);
		jcGroups.setJobIdPolicy(new JcJobIdGenerator());
		return jcGroups;
	}
	
	public IJcJobGroupsCreator newJcJobGroupsCreatorForBase(String dwhVersionCd,String topologyServerName) {
		JcGroupsCreator jcGroups=newConfiguredJcGroupsCreator(dwhVersionCd,topologyServerName);
		IJcGroupIdentificationStrategy grpS=new JcDalGroupStrategy(newJcSqlCommandGenerator());	
		jcGroups.setJcGroupIdentificationStrategy(grpS);
		return jcGroups;
	}
	public IJcJobGroupsCreator newJcJobGroupsCreatorForLds(String jobGroupName,String dwhVersionCd,String topologyServerName) {
		JcGroupsCreator jcGroups=newConfiguredJcGroupsCreator(dwhVersionCd,topologyServerName);
		IJcGroupIdentificationStrategy grpS=new JcLdsGroupStrategy(jobGroupName,newJcSqlCommandGenerator());
		jcGroups.setJcGroupIdentificationStrategy(grpS);
		return jcGroups;
	}
	
}
