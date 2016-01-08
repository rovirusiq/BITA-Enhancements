package ro.bcr.bita.model;

public enum JdbcDriverType {
	ORACLE_THIN("jdbc:oracle:thin");

	private static final String EXCEPTION_MESSAGE_PREFIX = "Illegal value for JdbcDriverType";
	private String code;

	JdbcDriverType(String code) {
		this.code = code;
	}

	public final String value() {
		return this.code;
	}

	public static final JdbcDriverType valueFrom(String code)
			throws IllegalArgumentException {

		if ((code == null) || ("".equals(code)))
			throw new IllegalArgumentException(EXCEPTION_MESSAGE_PREFIX + "["
					+ code + "]");

		JdbcDriverType rsp = null;

		for (JdbcDriverType it : JdbcDriverType.values()) {
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
