package ro.bcr.bita.odi.template

import ro.bcr.bita.model.BitaSpockSpecification;
import ro.bcr.bita.model.IOdiMapping;
import ro.bcr.bita.odi.proxy.IOdiBasicPersistenceService;
import ro.bcr.bita.odi.proxy.IOdiOperationsService;
import ro.bcr.bita.odi.template.OdiCommandContext
import ro.bcr.bita.odi.template.OdiTemplateException

import oracle.odi.core.persistence.transaction.ITransactionStatus;
import oracle.odi.domain.mapping.Mapping
import oracle.odi.domain.mapping.finder.IMappingFinder
import oracle.odi.domain.project.OdiFolder

class OdiCommandContextTest extends BitaSpockSpecification {
	
	OdiCommandContext subject;
	IOdiBasicPersistenceService persistSrv;
	IOdiOperationsService opSrv;
	
	def setup() {//cheama automat fixture-ul dinainte
		persistSrv=Mock();
		opSrv=Mock();
		subject=new OdiCommandContext(stbOdiEntityFactory,opSrv,persistSrv);
	}
	
	
	def "inTransaction - test initial value"(){
		given:	"The objects from setup and no explicit setup of inTransaction value in another Thread"
				Boolean rsp=true;
		when:	"The inTransaction is read in another thread (so it can be a new one, a clean one)"
				Thread x=new Thread() {
					public void run() {
						rsp=subject.inTransaction();
					}
				}
				x.start();
				x.join();
		then:	"The returned result is false"
				rsp==false;
	}
	
	def "inTransaction - different threads values"(){
		given:	"The objects from setup and in current thread the inTransaction is set explicitly"
				subject.setInTransaction(true);
		when:	"The value is read in the current thread and in another"
				Boolean rsp1=subject.inTransaction();
				Boolean rsp2;
				Thread x=new Thread() {
					public void run() {
						rsp2=subject.inTransaction();
					}
				}
				x.start();
				x.join();
				
		then:	"The value returned in current thread is true, and in the other is false"
				rsp1==true;
				rsp2==false;
	}
	
	def "setTransaction - test initial value"(){
		given:	"The objects from setup and no explicit setup of TransactionStatus value in another thread "
				ITransactionStatus rsp=Mock();
		when:	"The TransactionStatus is read (so it can be a new one, a clean one)"
				Thread x=new Thread() {
					public void run() {
						rsp=subject.getTransactionStatus();
					}
				}
				x.start();
				x.join();
		then:	"The returned result is null"
				rsp==null;
	}
	
	def "setTransaction - different threads values"(){
		given:	"The objects from setup and in current thread the inTransaction is set explicitly"
				ITransactionStatus  mTrnStatus=Mock();
				subject.setTransactionStatus(mTrnStatus);
		when:	"The value is read in the current thread and in another"
				ITransactionStatus rsp1=subject.getTransactionStatus();
				ITransactionStatus rsp2;
				Thread x=new Thread() {
					public void run() {
						rsp2=subject.getTransactionStatus();
					}
				}
				x.start();
				x.join();
				
		then:	"The value returned in current thread is true, and in the other is false"
				rsp1.equals(mTrnStatus);
				rsp2==null;
	}
	

}
