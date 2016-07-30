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

@CompileStatic
class SqlUtil {
	
	private IOdiEntityFactory odiEntityFactory;
	
	public SqlUtil(IOdiEntityFactory odiFactory) {
		this.odiEntityFactory=odiFactory;
	}
	 
	private Sql generateSqlFrom(OdiDataServer dbServer,String userName,String password) {
		IConnectionInfo jdbcInfo=dbServer.getConnectionInfo();		
		return Sql.newInstance(jdbcInfo.getJdbcUrl(),userName,password,jdbcInfo.getDriverName());
	}
	
	public Sql newSqlInstanceFromContext(String contextCode,String logicalSchemaName,String password) {	
		IOdiLogicalSchemaFinder lSchemaFinder=this.odiEntityFactory.createLogicalSchemaFinder();
		OdiLogicalSchema lSch=lSchemaFinder.findByName(logicalSchemaName);
		if (lSch==null) throw new BitaOdiException("Cannot find logical schema["+logicalSchemaName+"] in the Topology. Please check that you are running the script on the correct environment!");
		IOdiContextFinder ctxFinder=this.odiEntityFactory.createContextFinder();
		OdiContext ctx=ctxFinder.findByCode(contextCode);
		if (ctx==null) throw new BitaOdiException("Cannot find contex code["+contextCode+"] in the Topology. Please check that you are running the script on the correct environment!");
		OdiPhysicalSchema lPhSch=lSch.getPhysicalSchema(ctx);
		if (lPhSch==null) throw new BitaOdiException("Cannot find physical schema associated with logical schema[$logicalSchemaName] in the context[$contextCode]. Please check that you are running the script on the correct environment!");
		OdiDataServer dbServer=lPhSch.getDataServer();	
		return this.generateSqlFrom(dbServer,lPhSch.getSchemaName(),password);
	}
	
	public Sql newSqlInstanceFromServer(String topologyServer,String username,String password) {
		IOdiDataServerFinder dServerFinder=this.odiEntityFactory.createDataServerFinder();
		OdiDataServer server=dServerFinder.findByName(topologyServer);
		if (server==null) throw new BitaOdiException("Cannot find server["+topologyServer+"] in the Topology. Please check that you are running the script on the correct environment!");
		try {
			if (("".equals(username)) || (username==null)) username=server.getDefaultPhysicalSchema().getSchemaName();
			return this.generateSqlFrom(server,username,password);
		} catch (RuntimeException ex) {
			throw new BitaOdiException("Unexpected excpetion encountere when creatin database connection for server[$topologyServer].",ex);
		}
		return this.generateSqlFrom(server,username,password);
	}
	
	public Sql newSqlInstanceFromServer(String topologyServer,String password) {
		return newSqlInstanceFromServer(topologyServer,"",password);
	}

}
