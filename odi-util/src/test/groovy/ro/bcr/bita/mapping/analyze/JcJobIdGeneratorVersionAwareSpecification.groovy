package ro.bcr.bita.mapping.analyze

import spock.lang.Specification

class JcJobIdGeneratorVersionAwareSpecification extends Specification{
	
	JcJobIdGeneratorVersionAware subject;
	
	def "Test jobId generation for a result less than 100"(){
		given:	"our initialized subject"
				subject=new JcJobIdGeneratorVersionAware();
		when:	"asked to generate the job id"
				IJcGroupIdentificationStrategy mckStrategy=Stub();
				JcRequestContext ctx=new JcRequestContext("SOME_DWH_VERSION", mckStrategy)
				String jobId=subject.generateJobId(ctx,"mapping");
		then:	"the contactenated string between mapping name and version is returned"
				jobId=="SOME_DWH_VERSION_mapping";
				
	}
	def "Test jobId generation for a result greater than 100"(){
		given:	"our initialized subject"
				subject=new JcJobIdGeneratorVersionAware();
		when:	"asked to generate the job id"
				IJcGroupIdentificationStrategy mckStrategy=Stub();
				JcRequestContext ctx=new JcRequestContext("SOME_DWH_VERSION", mckStrategy)
				StringBuilder bld=new StringBuilder();
				(1..11).each{
					bld.append("A1B2C3D4E5");
				}
				String jobId=subject.generateJobId(ctx,bld.toString());
		then:	"the contactenated string between mapping name and version is returned"
				jobId.length()<=100;
				
	}

}
