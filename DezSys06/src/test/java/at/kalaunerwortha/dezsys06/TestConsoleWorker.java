package at.kalaunerwortha.dezsys06;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestConsoleWorker {
	
	@Test
	public void testVSDB() {
		JMSChatHandler jmsChat = new JMSChatHandler();
		JMSMailHandler jmsMail = new JMSMailHandler();
		ConsoleWorker cw = new ConsoleWorker(jmsChat, jmsMail);
		
		cw.processLine("vsdbchat tcp://localhost:61616 name test");
		assertEquals(true, cw.isLoggedIn());
	}
}
