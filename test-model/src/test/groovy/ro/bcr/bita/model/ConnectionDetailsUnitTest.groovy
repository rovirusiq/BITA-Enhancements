package ro.bcr.bita.model;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import ro.bcr.bita.model.ConnectionDetails;
import ro.bcr.bita.model.IncompleteSpecifiedModelException;
import ro.bcr.bita.model.JdbcDriverList;
import ro.bcr.bita.model.JdbcDriverType;
import ro.bcr.bita.model.ModelFactory;
import ro.bcr.bita.model.decorator.JdbcConnectionDetails;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class ConnectionDetailsUnitTest {
	
	private static final String MY_SID = "MY_SID";
	private static final String MY_PORT = "1521";
	private static final String MY_HOST = "MY_HOST";
	private static final String IDIOTIC_TEXT = "CICI";
	
	private ConnectionDetails connection;
	private JdbcConnectionDetails subject;
	private ModelFactory factory=new ModelFactory();

	
	@Rule
    public ExpectedException thrown= ExpectedException.none();
	
	
	
	@Before
	public void setUp() {
		connection=factory.createConnectionObject();;
	}

	@Test
	public void validComponentsShouldBeReturnedAsConnectionString() {
		connection.setHost(MY_HOST);
		connection.setPort(MY_PORT);
		connection.setSid(MY_SID);
		subject=new JdbcConnectionDetails(connection);
		//implicitly it checks the default value for jdbcDriverType
		assertThat(subject.provideJdbcConnectionString(),is(equalTo(JdbcDriverType.ORACLE_THIN.value()+":@"+MY_HOST+":"+MY_PORT+":"+MY_SID)));
	}
	
	@Test
	public void jdbcDriverShouldHaveDefaultValue(){
		subject=new JdbcConnectionDetails(connection);
		assertThat(connection.getJdbcDriverClassName(),is(equalTo(JdbcDriverList.ORACLE.value())));
	}

	@Test
	public void validUsernameShouldBeSuccessfullyRetrived() {
		connection.setJdbcUser(IDIOTIC_TEXT);
		subject=new JdbcConnectionDetails(connection);
		assertThat(connection,hasProperty("jdbcUser", equalTo(IDIOTIC_TEXT)));
		assertThat(subject.provideJdbcUsername(), equalTo(IDIOTIC_TEXT));
	}
	
	@Test
	public void validPasswordShouldBeSuccessfullyRetrived() {
		connection.setJdbcPassword(IDIOTIC_TEXT);
		subject=new JdbcConnectionDetails(connection);
		assertThat(connection,hasProperty("jdbcPassword", equalTo(IDIOTIC_TEXT)));
		assertThat(subject.provideJdbcPassword(), equalTo(IDIOTIC_TEXT));
	}

	@DataPoints
	public  static String[][] invalidValuesForComponents() {
		return [
					["","",""],
					[MY_HOST,MY_PORT,null],
					[MY_HOST,MY_PORT,""],
					[MY_HOST,"",MY_SID],
					[MY_HOST,null,MY_SID],
					["",MY_PORT,MY_SID],
					[null,MY_PORT,MY_SID],
				]; 
	};
	
	@Theory
	public void invalidComponentsShouldThrowExceptionWhenConnectionStringIsRead(String[] args) {
		thrown.expect(IncompleteSpecifiedModelException.class);
		connection.setHost(args[0]);
		connection.setPort(args[1]);
		connection.setSid(args[2]);
		subject=new JdbcConnectionDetails(connection);
		//implicitly it checks the default value for jdbcDriverType
		assertThat(subject.provideJdbcConnectionString(),is(equalTo(JdbcDriverType.ORACLE_THIN.value()+":@"+MY_HOST+":"+MY_PORT+":"+MY_SID)));
	}
	
	
	@DataPoints
	public  static String[] invalidValuesForUserNameAndPassword() {return ["",null]; };
	
	@Theory
	public void missingPasswordsShouldThrowAnException(String password) {
		thrown.expect(IncompleteSpecifiedModelException.class);
		connection.setJdbcPassword(password);
		subject=new JdbcConnectionDetails(connection);
		subject.provideJdbcPassword();
	}
	
	@Theory
	public void missingUserNameShouldThrowAnException(String username) {
		thrown.expect(IncompleteSpecifiedModelException.class);
		connection.setJdbcUser(username);
		subject=new JdbcConnectionDetails(connection);
		subject.provideJdbcUsername();
	}
	

}
