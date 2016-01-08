package ro.bcr.bita.model;

public enum JdbcDriverList {
	ORACLE("oracle.jdbc.OracleDriver");

	private static final String EXCEPTION_MESSAGE_PREFIX = "Illegal value for JdbcDriverList";
	private String code;

	JdbcDriverList(String code) {
		this.code = code;
	}

	public final String value() {
		return this.code;
	}

	public static final JdbcDriverList valueFrom(String code)
			throws IllegalArgumentException {

		if ((code == null) || ("".equals(code)))
			throw new IllegalArgumentException(EXCEPTION_MESSAGE_PREFIX + "["
					+ code + "]");

		JdbcDriverList rsp = null;

		for (JdbcDriverList it : JdbcDriverList.values()) {
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
