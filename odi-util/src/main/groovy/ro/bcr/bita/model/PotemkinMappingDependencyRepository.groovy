package ro.bcr.bita.model

import java.util.Set

class PotemkinMappingDependencyRepository implements
		IMappingDependencyRepositoryCyclicAware {

	@Override
	public void addTableToMapping(String tableName, String mappingName)
			throws BitaModelException {
		return;

	}

	@Override
	public void addMappingToTable(String mappingName, String tableName)
			throws BitaModelException {
		return;

	}

	@Override
	public Set<IDependency<String, String>> getMappingDependencies()
			throws BitaModelException {
		return [];
	}

	@Override
	public Set<IDependency<String, String>> getMappingDependenciesAndCheckCyclicDependencies()
			throws BitaModelException {
		return [];
	}

}
