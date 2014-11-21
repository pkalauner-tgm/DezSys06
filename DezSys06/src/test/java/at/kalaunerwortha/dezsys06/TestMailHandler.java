package at.kalaunerwortha.dezsys06;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Test;

/**
 * Testet den Mail Handler
 * 
 * @author Paul Kalauner 4AHIT
 *
 */
public class TestMailHandler {

	/**
	 * testet das Senden und Empfangen von privaten Nachrichten
	 */
	@Test
	public void testSendAndReceive() {
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		String msg = "Das ist eine Testnachricht!";
		System.setOut(new PrintStream(outContent));
		JMSMailHandler mh = new JMSMailHandler();
		mh.connect("tcp://localhost:61616", "Name");
		mh.sendMail("Name@" + IPHelper.getIP(), msg);
		mh.receiveMail();
		System.out.println(outContent.toString());
		mh.close();
		assertEquals(true, outContent.toString().contains(msg));
	}
}
