package ro.bcr.bita.model

import oracle.odi.domain.mapping.Mapping;
import oracle.odi.domain.mapping.finder.IMappingFinder

import ro.bcr.bita.model.IOdiMapping
import ro.bcr.bita.odi.template.OdiCommandContext;
import ro.bcr.bita.odi.template.OdiTemplateException;
import ro.bcr.bita.proxy.odi.IOdiEntityFactory

import spock.lang.Specification

class OdiCommandContextTest extends BitaMockModelFactory {
	
	OdiCommandContext subject;
	
	def setup() {//cheama automat fixture-ul dinainte
		subject=new OdiCommandContext(stbOdiEntityFactory);
	}
	
	def "Find Mappings - no parameter provided"(){
		given:	"no projects, no folders and no mappings"
		when:	"find mappings is called without parameters"
				List<IOdiMapping> rsp=subject.findMappings();
		then:	"no exception is thrown"
				notThrown Exception;
		then:	"an empty list is returned"
				rsp!=null;
				rsp.size()==0;
	}
	
	def "Find Mappings - no mappings in ODI"(){
		given:	"several prpjects with folders, but no mappings"
				stbOdiFolderFinder.findByProject("PRJ_A") >> ["FOLDER_1","FOLDER_2"];
				stbOdiMappingFinder.findByProject("PRJ_A","FOLDER_1") >> [];
				stbOdiMappingFinder.findByProject("PRJ_A","FOLDER_2") >> [];
				stbOdiMappingFinder.findByProject("PRJ_B","FOLDER_1") >> ["XXX"];
				stbOdiMappingFinder.findByProject("PRJ_B","FOLDER_2") >> [];
		when:	"find mappings is called without parameters"
				List<IOdiMapping> rsp=subject.findMappings("+PRJ_A","PRJ_B:FOLDER_2");
		then:	"no exception is thrown, because the list of mappings from PRJ_B and FOLDER_1"
				notThrown Exception;
		then:	"an empty list is returned"
				rsp!=null
				rsp.size()==0;
				
	}
	
	def "Find Mappings - different include paths"(){
		given:	"several projects with folders, but no mappings"
				
				Mapping m1=BITA_MOCK_ODI_MAPPING("M1");
				Mapping m2=BITA_MOCK_ODI_MAPPING("M2");
				Mapping m3=BITA_MOCK_ODI_MAPPING("M3");
				Mapping m4=BITA_MOCK_ODI_MAPPING("M4");
				Mapping m5=BITA_MOCK_ODI_MAPPING("M5");
				Mapping m6=BITA_MOCK_ODI_MAPPING("M6");
				Mapping m8=BITA_MOCK_ODI_MAPPING("M7");
				Mapping m9=BITA_MOCK_ODI_MAPPING("M9");
		
				stbOdiMappingFinder.findByProject("PRJ_A","FOLDER_1") >> [m1,m2];
				stbOdiMappingFinder.findByProject("PRJ_B","FOLDER_1") >> [];
				stbOdiMappingFinder.findByProject("PRJ_B","FOLDER_2") >> [m4,m5,m3];
		when:	"find mappings is called without parameters"
				List<IOdiMapping> rsp=subject.findMappings("+PRJ_A:FOLDER_1","PRJ_B:FOLDER_2","PRJ_B:FOLDER_1");
		then:	"an empty list is returned"
				rsp!=null;
				rsp.size()==5;			
	}
	
	
	def "Find Mappings - different include and exclude paths"(){
		given:	"several projects with folders, with different mappings"
				
				Mapping m1=BITA_MOCK_ODI_MAPPING("M1");
				Mapping m2=BITA_MOCK_ODI_MAPPING("M2");
				Mapping m3=BITA_MOCK_ODI_MAPPING("M3");
				Mapping m4=BITA_MOCK_ODI_MAPPING("M4");
				Mapping m5=BITA_MOCK_ODI_MAPPING("M5");
				Mapping m6=BITA_MOCK_ODI_MAPPING("M6");
				Mapping m7=BITA_MOCK_ODI_MAPPING("M7");
				Mapping m8=BITA_MOCK_ODI_MAPPING("M8");
				Mapping m9=BITA_MOCK_ODI_MAPPING("M9");
		
				stbOdiMappingFinder.findByProject("PRJ_A","FOLDER_1") >> [m1,m2];
				stbOdiMappingFinder.findByProject("PRJ_A","FOLDER_2") >> [m6,m7,m8];
				stbOdiMappingFinder.findByProject("PRJ_B","FOLDER_1") >> [m9];
				stbOdiMappingFinder.findByProject("PRJ_B","FOLDER_2") >> [m4,m5,m3];
		when:	"find mappings is called without parameters"
				List<IOdiMapping> rsp=subject.findMappings("+PRJ_A:FOLDER_1","PRJ_A:FOLDER_2","+PRJ_B:FOLDER_2","PRJ_B:FOLDER_1","-PRJ_B:FOLDER_1");
		then:	"an empty list is returned"
				rsp!=null;
				rsp.size()==8;
	}
	
	def "Find Mappings - unexpected exception"(){
		given:	"several projects with folders, with different mappings"
				
				Mapping m1=BITA_MOCK_ODI_MAPPING("M1");
				Mapping m2=BITA_MOCK_ODI_MAPPING("M2");
				Mapping m3=BITA_MOCK_ODI_MAPPING("M3");
				Mapping m4=BITA_MOCK_ODI_MAPPING("M4");
				Mapping m5=BITA_MOCK_ODI_MAPPING("M5");
				Mapping m6=BITA_MOCK_ODI_MAPPING("M6");
				Mapping m7=BITA_MOCK_ODI_MAPPING("M7");
				Mapping m8=BITA_MOCK_ODI_MAPPING("M8");
				Mapping m9=BITA_MOCK_ODI_MAPPING("M9");
		
				stbOdiMappingFinder.findByProject("PRJ_A","FOLDER_1") >> [m1,m2];
				stbOdiMappingFinder.findByProject("PRJ_A","FOLDER_2") >> {throw new RuntimeException("Dummy Exception")};
				stbOdiMappingFinder.findByProject("PRJ_B","FOLDER_1") >> [m9]
				stbOdiMappingFinder.findByProject("PRJ_B","FOLDER_2") >> [m4,m5,m3];
		when:	"when an unexpected exception is thrown"
				List<IOdiMapping> rsp=subject.findMappings("+PRJ_A:FOLDER_1","PRJ_A:FOLDER_2","+PRJ_B:FOLDER_2","PRJ_B:FOLDER_1","-PRJ_B:FOLDER_1");
		then:	"an exception is thrown"
				thrown OdiTemplateException;
	}

}
