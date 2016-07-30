package ro.bcr.bita.odi.proxy;

import ro.bcr.bita.model.IBitaModelFactory;
import ro.bcr.bita.model.IOdiMapping;
import ro.bcr.bita.odi.template.OdiTemplateException;

import groovy.transform.CompileStatic;

import java.util.List;

import oracle.odi.domain.mapping.Mapping;
import oracle.odi.domain.mapping.finder.IMappingFinder;


//TODO review and find a way to implementit right
@CompileStatic
public class OdiMappingUtil {
	
	
	private final IOdiEntityFactory odiEntityFactory;
	private final OdiPathUtil odiPathUtils;
	private final IBitaModelFactory bitaModelFactory;
	
	public OdiMappingUtil(IOdiEntityFactory odiEntityFactory,OdiPathUtil odiPathUtil,IBitaModelFactory bitaModelFactory) {
		this.odiEntityFactory=odiEntityFactory;
		this.odiPathUtils=odiPathUtil;
		this.bitaModelFactory=bitaModelFactory;
	}
	
	public List<IOdiMapping> findMappings(String... odiPaths) throws OdiTemplateException{
		try {
			
			IOdiProjectPaths rsp=this.odiPathUtils.extractProjectPaths(odiPaths);
			
			List<IOdiMapping> mps=[];
			
			
			IMappingFinder finder=this.odiEntityFactory.createMappingFinder();
			
			for (String prj:rsp.getProjects()) {
				
				for (String folder:rsp.getFoldersForProject(prj)) {
				
					mps.addAll(
						(finder.findByProject(prj,folder)).collect { Mapping it -> 
							return bitaModelFactory.newOdiMapping(it) 
						}
					);
				}
			}
			
			return mps;
		} catch (Exception ex) {
			throw new BitaOdiException("An exception occured when trying to identify mappings from the pats[$odiPaths].",ex);
		}
	}

}
