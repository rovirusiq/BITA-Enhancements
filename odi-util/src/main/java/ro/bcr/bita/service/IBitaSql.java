package ro.bcr.bita.service;

import ro.bcr.bita.model.IMessageCollection;

public interface IBitaSql {
	
	public void executeInTransaction(IMessageCollection msgColl) throws BitaServiceException;
	public void executeInTransaction(IMessageCollection... msgColl) throws BitaServiceException;

}
