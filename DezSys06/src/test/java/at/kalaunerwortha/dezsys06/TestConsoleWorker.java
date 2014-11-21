package at.kalaunerwortha.dezsys06;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Testet den ConsoleWorker
 * 
 * @author Simon Wortha 4AHIT
 *
 */
public class TestConsoleWorker {

	/**
	 * testet den vsdbchat Command
	 */
	@Test
	public void testVSDB() {
		JMSChatHandler jmsChat = new JMSChatHandler();
		JMSMailHandler jmsMail = new JMSMailHandler();
		ConsoleWorker cw = new ConsoleWorker(jmsChat, jmsMail);

		cw.processLine("vsdbchat tcp://localhost:61616 name test");
		assertEquals(true, cw.isLoggedIn());
	}
	
	/**
	 * testet die {@code buildMessage()} Methode
	 */
	@Test
	public void testBuildMessage() {
		String[] input = { "bla", "blabla", "Nachricht1", "Nachricht2", "Nachricht3", "Nachricht4" };
		String output = ConsoleWorker.buildMessage(input, 2);
		assertEquals(" Nachricht1 Nachricht2 Nachricht3 Nachricht4", output);
	}	
}
