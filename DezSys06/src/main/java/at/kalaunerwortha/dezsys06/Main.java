package at.kalaunerwortha.dezsys06;

import java.util.Scanner;

/**
 * In dieser Klasse werden die Konsoleneingaben gelesen
 * 
 * @author Paul Kalauner 4AHIT
 * @version 20141115.1
 *
 */
public class Main {

	/**
	 * Eintrittspunkt des Programmes
	 * 
	 * @param args
	 *            nicht verwendet
	 */
	public static void main(String[] args) {
		JMSChatHandler jmsChat = new JMSChatHandler();
		JMSMailHandler jmsMail = new JMSMailHandler();
		ConsoleWorker cw = new ConsoleWorker(jmsChat, jmsMail);
		Scanner s = new Scanner(System.in);
		while (true) {
			String line = s.nextLine();
			if (line.equalsIgnoreCase("EXIT")) {
				jmsChat.close();
				jmsMail.close();
				s.close();
				System.exit(0);
			}
			cw.processLine(line);
		}
	}
}
