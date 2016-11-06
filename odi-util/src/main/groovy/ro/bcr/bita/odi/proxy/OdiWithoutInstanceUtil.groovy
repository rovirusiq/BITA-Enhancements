package ro.bcr.bita.odi.proxy

import java.sql.Connection;
import java.sql.SQLException;

import groovy.sql.GroovyRowResult;
import groovy.sql.Sql;
import oracle.odi.core.security.cryptography.CipherFactory;
import oracle.odi.core.security.cryptography.IOdiCipher;

class OdiWithoutInstanceUtil {
	
	public char[] retrieveEncryptedPassword(String username,Connection masterRepositoryConn,boolean closeConn=false) throws BitaOdiException {
		return retrieveEncryptedPassword(username,new Sql(masterRepositoryConn),closeConn);
	}
	
	public char[] retrieveEncryptedPassword(String username,Sql masterRepositoryConn,boolean closeConn=false) throws BitaOdiException {
		String encryptedPassword="";
		try {
			GroovyRowResult row=masterRepositoryConn.firstRow("select pass from snp_user where wuser_name = ?",[username]);
			if (row.isEmpty()) {
				throw new BitaOdiException("The query to identify the user in the repository returned 0 rows. $username was not found.");
				
			}
			encryptedPassword=row['pass'];
			if (closeConn) {
				masterRepositoryConn.close();
			}
		} catch (Exception e){
			if (e instanceof BitaOdiException){
				throw e;
			} else {
				throw new BitaOdiException("An exception occured when trying to retrieve the password for the user[$username]",e);
			}
		}
		
		return encryptedPassword.toCharArray();
	}
	
	public String retrieveWorkRepName(Connection workRepoConn,boolean closeConn=false) throws BitaOdiException {
		return retrieveWorkRepName(new Sql(workRepoConn),closeConn);
	}
	
	public String retrieveWorkRepName(Sql workRepoConn,boolean closeConn=false) throws BitaOdiException {
		String workRepName="";
		try {
			GroovyRowResult row=workRepoConn.firstRow("select rep_name from snp_loc_repw");
			if (row.isEmpty()) {
				throw new BitaOdiException("The query to identify the work repository name return 0 rows.");
				
			}
			workRepName=row['rep_name'];
			if (closeConn) {
				workRepoConn.close();
			}
		} catch (Exception e){
			if (e instanceof BitaOdiException){
				throw e;
			} else {
				throw new BitaOdiException("An exception occured when trying to retrieve the work repository name",e);
			}
		}
		
		return workRepName;
	}
	
	public char[] decryptPasswordWithoutOdiInstance(char[] encryptedPassword,Connection workRepoConn,boolean closeConn=false) throws BitaOdiException {
		char[] secret;
		try {
			IOdiCipher odiCipher = CipherFactory.getOdiCipherWithoutOdiInstance(workRepoConn, false);
			secret=odiCipher.decryptPassword(encryptedPassword);
			if (closeConn) {
				try {
					workRepoConn.close();
				} catch (SQLException ex) {
					throw new BitaOdiException("Cannot close SQL connection to work repostitory.",ex);
				}
			}
			if (secret==null) throw new BitaOdiException("The password[$encryptedPassword] is null after decryption");
		} catch (Exception e){
			if (e instanceof BitaOdiException){
				throw e;
			} else {
				throw new BitaOdiException("An exception occured when trying decrypt the password[$encryptedPassword]",e);
			}
		}
		return secret;
	}
	
	public char[] decryptPasswordWithoutOdiInstance(char[] encryptedPassword,Sql workRepoConn,boolean closeConn=false) {
		return decryptPasswordWithoutOdiInstance(encryptedPassword,workRepoConn.getConnection(),closeConn);
	}
	
	public String retrieveUsernameForSession(Connection masterRepositoryConn,String sessionNo,boolean closeConn=false) throws BitaOdiException {
		return retrieveUsernameForSession(new Sql(masterRepositoryConn),sessionNo,closeConn);
	}
	
	public String retrieveUsernameForSession(Sql masterRepositoryConn,String sessionNo,boolean closeConn=false) throws BitaOdiException {
		String username="";
		try {
			
			GroovyRowResult row=masterRepositoryConn.firstRow("select USER_NAME from SNP_SESSION where SESS_NO = ?",[sessionNo]);
			
			if (row.isEmpty()) {
				throw new BitaOdiException("The query to identify the user for for the running ODI session returned 0 rows.");
				
			}
			username=row['USER_NAME'];
			if (closeConn) {
				masterRepositoryConn.close();
			}
		} catch (Exception e){
			if (e instanceof BitaOdiException){
				throw e;
			} else {
				throw new BitaOdiException("An exception occured when trying to retrieve the username for the running ODI session.",e);
			}
		}
		
		return username;
	}
	

}
