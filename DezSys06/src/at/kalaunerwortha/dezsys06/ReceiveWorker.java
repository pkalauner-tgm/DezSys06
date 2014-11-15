package at.kalaunerwortha.dezsys06;

import javax.jms.JMSException;
import javax.jms.TextMessage;

/**
 * Zustaendig fuer das Empfangen der Nachrichten
 * 
 * @author Paul Kalauner 4AHIT
 * @version 20141115.1
 *
 */
public class ReceiveWorker implements Runnable {
	private JMSChatHandler jmsChat;
	private boolean running;

	/**
	 * Initialisiert den Worker mit Angabe des ChatHandlers
	 * 
	 * @param jmsChat
	 *            der ChatHandler
	 */
	public ReceiveWorker(JMSChatHandler jmsChat) {
		this.jmsChat = jmsChat;
		this.running = true;
	}

	@Override
	public void run() {
		while (running) {
			TextMessage message;
			try {
				message = (TextMessage) jmsChat.getConsumer().receive();
				if (message != null) {
					System.out.println(message.getText());
					message.acknowledge();
				}
			} catch (JMSException e) {
				System.out.println("Fehler beim Empfangen der Nachricht");
			}
		}
	}

	/**
	 * setzt den Zustand
	 * 
	 * @param running
	 *            true wenn der Worker Nachrichten empfangen soll
	 */
	public void setRunning(boolean running) {
		this.running = running;
	}

}
