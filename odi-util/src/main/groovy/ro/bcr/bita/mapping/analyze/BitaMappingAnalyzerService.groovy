package ro.bcr.bita.mapping.analyze

import ro.bcr.bita.model.IOdiMapping;
import ro.bcr.bita.model.OdiMapping;
import ro.bcr.bita.odi.proxy.OdiEntityFactory
import ro.bcr.bita.odi.template.IOdiBasicCommand
import ro.bcr.bita.odi.template.IOdiBasicTemplate;
import ro.bcr.bita.odi.template.IOdiCommandContext;
import ro.bcr.bita.odi.template.OdiCommandContext;

import oracle.odi.domain.IOdiEntity;
import groovy.transform.CompileStatic;


//TODO add options- readonly, check for modification, frequency for commit/clear
@CompileStatic
class BitaMappingAnalyzerService implements IMappingAnalyzerService{
	private List<IMappingAnalyzeProcessor> procs=[];
	private final IOdiBasicTemplate odiTemplate;
	
	public BitaMappingAnalyzerService(IOdiBasicTemplate odiTemplate) {
		this.odiTemplate=odiTemplate;
	}

	@Override
	public void addAnalyzeProcessor(IMappingAnalyzeProcessor processor) {
		procs<<processor;
	}

	@Override
	public void analyzeMappingsFrom(String... odiPaths) {
		IOdiBasicCommand cmd={IOdiCommandContext ctx ->
			ctx.findMappings(odiPaths).each{IOdiMapping m->
				
				this.procs.each{IMappingAnalyzeProcessor p->
					p.processMapping(m);
				}
				/*
				 * clear the context after the processing of each mapping
				 * it helps keep the memory at a low level
				 */
				ctx.clearPersistenceContext();
				m=null;
			}
			
			
		} as IOdiBasicCommand;
		
		this.odiTemplate.executeWithoutTransaction(cmd);
	}

}
