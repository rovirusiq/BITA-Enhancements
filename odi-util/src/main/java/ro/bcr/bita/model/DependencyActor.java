package ro.bcr.bita.model;

public enum DependencyActor {
	MAPPING("Mapping");

	private static final String EXCEPTION_MESSAGE_PREFIX = "Illegal value for DependencyActor";
	private String code;

	DependencyActor(String code) {
		this.code = code;
	}

	public final String value() {
		return this.code;
	}

	public static final DependencyActor valueFrom(String code)
			throws IllegalArgumentException {

		if ((code == null) || ("".equals(code)))
			throw new IllegalArgumentException(EXCEPTION_MESSAGE_PREFIX + "["
					+ code + "]");

		DependencyActor rsp = null;

		for (DependencyActor it : DependencyActor.values()) {
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
