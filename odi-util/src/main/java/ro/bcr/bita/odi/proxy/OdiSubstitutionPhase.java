package ro.bcr.bita.odi.proxy;

public enum OdiSubstitutionPhase {
	NONE("N/A",0)
	,PHASE_1("%",1)
	,PHASE_2("?",2)
	,PHASE_3("$",3)
	,PHASE_4("@",4);

	private static final String EXCEPTION_MESSAGE_PREFIX = "Illegal value for OdiSubstitutionPhase";
	private String code;
	private Integer order;

	OdiSubstitutionPhase(String code,Integer order) {
		this.code = code;
		this.order=order;
	}

	public final String value() {
		return this.code;
	}
	
	public final Integer getOrder() {
		return this.order;
	}
	
	public final OdiSubstitutionPhase previousPhase() {
		Integer o=this.getOrder()-1;
		if (o<=0) return NONE;
		
		return OdiSubstitutionPhase.valueFrom(o);
	}
	
	public final OdiSubstitutionPhase nextPhase() {
		Integer o=this.getOrder()+1;
		if (o>=PHASE_4.getOrder()) return NONE;
		return OdiSubstitutionPhase.valueFrom(o);
	}

	public static final OdiSubstitutionPhase valueFrom(String code)
			throws IllegalArgumentException {

		if ((code == null) || ("".equals(code)))
			throw new IllegalArgumentException(EXCEPTION_MESSAGE_PREFIX + "["
					+ code + "]");

		OdiSubstitutionPhase rsp = null;

		for (OdiSubstitutionPhase it : OdiSubstitutionPhase.values()) {
			if (it.value().equals(code)) {
				rsp = it;
			}
		}

		if (rsp == null)
			throw new IllegalArgumentException(EXCEPTION_MESSAGE_PREFIX + "code=["
					+ code + "]");
		return rsp;
	}
	
	public static final OdiSubstitutionPhase valueFrom(Integer order)
			throws IllegalArgumentException {
		
		if ((order == null))
			throw new IllegalArgumentException(EXCEPTION_MESSAGE_PREFIX + "order=["
					+ order + "]");

		OdiSubstitutionPhase rsp = null;

		for (OdiSubstitutionPhase it : OdiSubstitutionPhase.values()) {
			if (it.getOrder()==order) {
				rsp = it;
			}
		}

		if (rsp == null)
			throw new IllegalArgumentException(EXCEPTION_MESSAGE_PREFIX + "order["
					+ order + "]");
		return rsp;
	}
}
