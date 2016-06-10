package ro.bcr.bita.model;

public enum MappingDependencyActor {
	MAPPING("Mapping");

	private static final String EXCEPTION_MESSAGE_PREFIX = "Illegal value for MappingDependencyActor";
	private String code;

	MappingDependencyActor(String code) {
		this.code = code;
	}

	public final String value() {
		return this.code;
	}

	public static final MappingDependencyActor valueFrom(String code)
			throws IllegalArgumentException {

		if ((code == null) || ("".equals(code)))
			throw new IllegalArgumentException(EXCEPTION_MESSAGE_PREFIX + "["
					+ code + "]");

		MappingDependencyActor rsp = null;

		for (MappingDependencyActor it : MappingDependencyActor.values()) {
			if (it.value().equals(code)) {
				rsp = it;
			}
		}

		if (rsp == null)
			throw new IllegalArgumentException(EXCEPTION_MESSAGE_PREFIX + "["
					+ code + "]");
		return rsp;
	}
}
