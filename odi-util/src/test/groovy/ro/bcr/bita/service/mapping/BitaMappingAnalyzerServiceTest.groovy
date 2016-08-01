package ro.bcr.bita.service.mapping

import ro.bcr.bita.model.BitaSpockSpecification;
import ro.bcr.bita.model.IBitaModelFactory;
import ro.bcr.bita.model.IOdiMapping;
import ro.bcr.bita.odi.proxy.IOdiBasicPersistenceService;
import ro.bcr.bita.odi.proxy.IOdiEntityFactory;
import ro.bcr.bita.odi.template.IOdiBasicCommand;
import ro.bcr.bita.odi.template.IOdiBasicTemplate
import ro.bcr.bita.odi.template.IOdiCommandContext;
import ro.bcr.bita.odi.template.OdiBasicTemplate;
import ro.bcr.bita.service.IMappingAnalyzeProcessor;

import spock.lang.Specification

class BitaMappingAnalyzerServiceTest extends BitaSpockSpecification {

	
	BitaMappingAnalyzerService subject;
	IOdiBasicTemplate odiTemplate;
	IOdiEntityFactory stOdiEntityFactory;
	IOdiCommandContext mCmdCtx;
	
	
	def setup() {
		stOdiEntityFactory=Stub()
		mCmdCtx=Mock();
		odiTemplate=new OdiBasicTemplate(stOdiEntityFactory);
		stOdiEntityFactory.newOdiTemplateCommandContext() >> mCmdCtx;
		subject=new BitaMappingAnalyzerService(odiTemplate);
		
	}
	
	def "Interactions with processors - on an empty collection of mappings"(){
		given:	"The objects fromt the setup, 2 IMappingAnalyzeProcessor, 0 mappings"
				IMappingAnalyzeProcessor p1=Mock();
				IMappingAnalyzeProcessor p2=Mock();
				subject.addAnalyzeProcessor(p1);
				subject.addAnalyzeProcessor(p2);
				
		when:	"The service is used"
				subject.analyzeMappingsFrom("PRJ_1:FLD_1");
				
		then:	"The OdiTemplate interaction is checked"
				1 * mCmdCtx.findMappings(_) >> []
	}
	
	def "Interactions with processors - with some mappings"(){
		given:	"The objects fromt the setup, 2 IMappingAnalyzeProcessor, 2 mappings"
				IMappingAnalyzeProcessor p1=Mock();
				IMappingAnalyzeProcessor p2=Mock();
				subject.addAnalyzeProcessor(p1);
				subject.addAnalyzeProcessor(p2);
				
				IOdiMapping mp1=Mock();
				IOdiMapping mp2=Mock();
				
		when:	"The service is used"
				subject.analyzeMappingsFrom("PRJ_1:FLD_1");
				
		then:	"The interactions with al objects is tested"
				1 * mCmdCtx.findMappings(_) >> [mp1,mp2]
				1 * p1.processMapping(mp1)
				1 * p1.processMapping(mp2)
				1 * p2.processMapping(mp1)
				1 * p2.processMapping(mp2)
				3 * mCmdCtx.clearPersistenceContext();//one for each mapping and one fior the odi without transaction
	}

	
}
