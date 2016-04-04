package ro.bcr.bita.services;

public interface ISrgService {
	
	
	/**
	 * it removes duplicates from the CMT TABLE
	 * it also corrects some of the attributes that have wrong values in the underlying table
	 */
	public void cleanuUpMetadata();
	/**
	 * @return A ISrgRepository interfaces through which one can query the surrgoation defintions (i.e. surrogation objects)
	 * 
	 */
	public ISrgRepository createRepository();
	/**
	 * executes the necessary queries so all db users have the required grants
	 * it executes them for all the surrogation definitions (i.e. surrogation objects);
	 *
	 **/
	public void giveThemGrants();
	

}
