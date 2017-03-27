package ro.ns.spock.base.odi;

public interface IInitSubjectTestHelper {
	
	public static final Closure checkParamAndReturnValue={Map params,String key,Object defaultValue->
		if (params.containskey(key)) return params.key;
		return defaultValue;
	}
	
	public static final Closure checkParam={Map params,String key,Closure defaultValue->
		if (params.containsKey(key)) return params.get(key);
		return defaultValue.call();
	}

}

