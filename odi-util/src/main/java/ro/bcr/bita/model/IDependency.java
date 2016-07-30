package ro.bcr.bita.model;


public interface IDependency<H,O> {
	
	public enum DependencyRole{
		/**
		 * The Dependent 
		 */
		WHO("WHO"),
		
		/**
		 * The Dependency 
		 */
		ON("ON");
		
		private static final String EXCEPTION_MESSAGE_PREFIX = "Illegal value for Direction";
		private String code;

		DependencyRole(String code) {
			this.code = code;
		}

		public final String value() {
			return this.code;
		}

		public static final DependencyRole valueFrom(String code)
				throws IllegalArgumentException {

			if ((code == null) || ("".equals(code)))
				throw new IllegalArgumentException(EXCEPTION_MESSAGE_PREFIX + "["
						+ code + "]");

			DependencyRole rsp = null;

			for (DependencyRole it : DependencyRole.values()) {
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
	
	
	
	public H who();
	public O on();

}
