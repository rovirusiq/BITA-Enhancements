package ro.bcr.bita.common;

import spock.lang.Specification

class ComposedDelegatorTest extends Specification{

	def myWrappedObject1=new ToBeWrappedObject1();
	def myWrappedObject2=new ToBeWrappedObject2();
	ComposedDelegator subject;
	
	
	def "Delegating a method"(){
		given: "Our Subject under test and the wrapped objects"
			subject=new ComposedDelegator([myWrappedObject1,myWrappedObject2]);
		when:"A method that exsists on 1st wraped object is called"
			def l=subject.length1();
		then:"It is delegated to the 1st wrapped object"
			l==myWrappedObject1.length1();
		when:"A method that exsists on 2st wraped object is called"
			l=subject.length2();
		then:"It is delegated to the 2st wrapped object"
			l==myWrappedObject2.length2();
			
	}
	
	def "when delegating a method,  priority should be considered"(){	
		when:"When a method that exists on more than one objects is called"
			subject=new ComposedDelegator([myWrappedObject1,myWrappedObject2]);
			def l=subject.commonMethod();
		then:"The order of the wrapped objects is taken into account"
			l==myWrappedObject1.commonMethod();
		when:"When a method that exists on more than one objects is called, but the order of the wrapped objects has changed"
			subject=new ComposedDelegator([myWrappedObject2,myWrappedObject1]);
			l=subject.commonMethod();
		then:"It is delegated to the 2st wrapped object"
			l==myWrappedObject2.commonMethod();
	}
	
	def "Delegating a property"(){
		given: "Our Subject under test and the wrapped objects"
			subject=new ComposedDelegator([myWrappedObject1,myWrappedObject2]);
		when:"A propert that exsists on 1st wraped object is accessed"
			def l=subject.publicProperty1;
		then:"It is delegated to the 1st wrapped object"
			l==myWrappedObject1.publicProperty1;
		when:"A propertyhat exsists on 2st wraped object is accessed"
			l=subject.publicProperty2;
		then:"The order of the wrapped objects is taken into account and it is delegated to the first object"
			l==myWrappedObject2.publicProperty2;
	
	}
	
	
	def "Delegating a property assignment"(){
		given: "Our Subject under test and the wrapped objects"
			subject=new ComposedDelegator([myWrappedObject1,myWrappedObject2]);
		when:"A property that exsists on 1st wraped object is accessed"
			subject.publicProperty1="New Value";
		then:"It is delegated to the 1st wrapped object"
			"New Value"==myWrappedObject1.publicProperty1;
		when:"A propertyhat exsists on 2st wraped object is accessed"
			subject.publicProperty2="Another New Value";
		then:"The order of the wrapped objects is taken into account and it is delegated to the first object"
			"Another New Value"==myWrappedObject2.publicProperty2;
	
	}
	
	def "Delegating a property assignment and priority"(){
		given: "Our Subject under test and the wrapped objects"
			subject=new ComposedDelegator([myWrappedObject1,myWrappedObject2]);
		when:"A property that exsists on both wrapped objects is accessed"
			def rsp=subject.commonProperty;
		then:"It is delegated to the 1st wrapped object"
			rsp==myWrappedObject1.commonProperty;
		when:"The order of the objects is changed and the common property is once again accesed"
			subject=new ComposedDelegator([myWrappedObject2,myWrappedObject1]);
			rsp=subject.commonProperty;
		then:"The order of the wrapped objects is taken into account and it is delegated to the first object"
			rsp==myWrappedObject2.commonProperty;
	}
	
	def "when delegating a property, priority should be considered"(){
		when:"When a property that exists on more than one objects is called"
			subject=new ComposedDelegator([myWrappedObject1,myWrappedObject2]);
			def l=subject.commonProperty;
		then:"The order of the wrapped objects is taken into account and it is delegated to the first object"
			l==myWrappedObject1.commonProperty;
		when:"When a property that exists on more than one objects is called, but the order of the wrapped objects has changed"
			subject=new ComposedDelegator([myWrappedObject2,myWrappedObject1]);
			l=subject.commonProperty;
		then:"The order of the wrapped objects is taken into account and it is delegated to the first object"
			l==myWrappedObject2.commonProperty;
	}
	

}
