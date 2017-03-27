package ro.bcr.bita.app;

public enum DatabaseUpdatePolicyEnum {
	STREAM("STREAM")
	,BATCH("BATCH");

	private static final String EXCEPTION_MESSAGE_PREFIX = "Illegal value for DatabaseUpdatePolicyEnum";
	private String code;

	DatabaseUpdatePolicyEnum(String code) {
		this.code = code;
	}

	public final String value() {
		return this.code;
	}

	public static final DatabaseUpdatePolicyEnum valueFrom(String code)
			throws IllegalArgumentException {

		if ((code == null) || ("".equals(code)))
			throw new IllegalArgumentException(EXCEPTION_MESSAGE_PREFIX + "["
					+ code + "]");

		DatabaseUpdatePolicyEnum rsp = null;

		for (DatabaseUpdatePolicyEnum it : DatabaseUpdatePolicyEnum.values()) {
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
