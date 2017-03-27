package ro.bcr.bita.odi.proxy

import oracle.odi.core.OdiInstance;
import oracle.odi.domain.adapter.topology.IConnectionInfo;
import oracle.odi.domain.adapter.topology.ILogicalSchema;
import oracle.odi.domain.topology.OdiContext;
import oracle.odi.domain.topology.OdiDataServer;
import oracle.odi.domain.topology.OdiLogicalSchema;
import oracle.odi.domain.topology.OdiPhysicalSchema;
import oracle.odi.domain.topology.finder.IOdiContextFinder;
import oracle.odi.domain.topology.finder.IOdiDataServerFinder;
import oracle.odi.domain.topology.finder.IOdiLogicalSchemaFinder;
import groovy.sql.Sql
import groovy.transform.CompileStatic;
import com.sunopsis.dwg.DwgObject;

//TODO sa caut in contextul default

@CompileStatic
class SqlUtil {
	
	private IOdiEntityFactory odiEntityFactory;
	
	public SqlUtil(IOdiEntityFactory odiFactory) {
		this.odiEntityFactory=odiFactory;
	}
	 
	private Sql generateSqlFrom(OdiDataServer dbServer,String userName,String password) {
		IConnectionInfo jdbcInfo=dbServer.getConnectionInfo();
		if (("".equals(userName)) || (userName==null)) {
			userName=dbServer.getUsername();
			password=DwgObject.snpsDecypher(dbServer.getPassword().toString(),((OdiEntityFactory)this.odiEntityFactory).getOdiInstance());
			//throw new RuntimeException("user=${userName}; pass=${password}; url=${jdbcInfo.getJdbcUrl()}; drvName=${jdbcInfo.getDriverName()}");
		}		
		return Sql.newInstance(jdbcInfo.getJdbcUrl(),userName,password,jdbcInfo.getDriverName());
	}
	
	
	private Sql newSqlInstanceFromContextObject(OdiContext ctx,String logicalSchemaName,String username,String password) {
		IOdiLogicalSchemaFinder lSchemaFinder=this.odiEntityFactory.createLogicalSchemaFinder();
		OdiLogicalSchema lSch=lSchemaFinder.findByName(logicalSchemaName);
		if (lSch==null) throw new BitaOdiException("Cannot find logical schema["+logicalSchemaName+"] in the Topology. Please check that you are running the script on the correct environment!");
		OdiPhysicalSchema lPhSch=lSch.getPhysicalSchema(ctx);
		if (lPhSch==null) throw new BitaOdiException("Cannot find physical schema associated with logical schema[$logicalSchemaName] in the context[${ctx.code}]. Please check that you are running the script on the correct environment!");
		OdiDataServer dbServer=lPhSch.getDataServer();
		return this.generateSqlFrom(dbServer,username,password);
	}
	
	public Sql newSqlInstanceFromContext(String contextCode,String logicalSchemaName,String username,String password) {	
		IOdiContextFinder ctxFinder=this.odiEntityFactory.createContextFinder();
		OdiContext ctx=ctxFinder.findByCode(contextCode);
		if (ctx==null) throw new BitaOdiException("Cannot find contex code["+contextCode+"] in the Topology. Please check that you are running the script on the correct environment!");
		return this.newSqlInstanceFromContextObject(ctx,logicalSchemaName,username,password);
	}
	
	public Sql newSqlInstanceFromDefaultContext(String logicalSchemaName,String username,String password) {
		IOdiContextFinder ctxFinder=this.odiEntityFactory.createContextFinder();
		OdiContext ctx=ctxFinder.findDefaultContext();
		if (ctx==null) throw new BitaOdiException("Cannot find default context in the Topology. Please check that you are running the script on the correct environment!");
		return this.newSqlInstanceFromContextObject(ctx,logicalSchemaName,username,password);
	}
	
	public Sql newSqlInstanceFromServer(String topologyServer,String username,String password) {
		IOdiDataServerFinder dServerFinder=this.odiEntityFactory.createDataServerFinder();
		OdiDataServer server=dServerFinder.findByName(topologyServer);
		if (server==null) throw new BitaOdiException("Cannot find server["+topologyServer+"] in the Topology. Please check that you are running the script on the correct environment!");
		return this.generateSqlFrom(server,username,password);
	}
	
	public Sql newSqlInstanceFromServer(String topologyServer,String password) {
		return newSqlInstanceFromServer(topologyServer,"",password);
	}
	
	public Sql newSqlInstanceFromServer(String topologyServer) {
		return newSqlInstanceFromServer(topologyServer,null,null);
	}

}
