package at.kalaunerwortha.dezsys06;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * Zustaendig fuer das Empfangen und Senden der Mails
 * 
 * @author Paul Kalauner 4AHIT
 * @version 20141115.1
 *
 */
public class JMSMailHandler {
	private Session session;
	private Connection connection;
	private String username;
	private MessageConsumer consumer;
	private Destination queue;

	/**
	 * Stellt Verbndung zum Message Broker her und erstellt die Queue
	 * 
	 * @param ip
	 *            IP des Message Brokers
	 * @param username
	 *            Name mit dem Nachrichten gesendet werden
	 * @return true wenn erfolgreich
	 */
	public boolean connect(String ip, String username) {

		this.username = username;

		try {
			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(username, ActiveMQConnection.DEFAULT_PASSWORD, ip);
			connection = connectionFactory.createConnection();
			connection.start();

			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			// queue = session.createQueue(username + "@" + ip);
			queue = session.createQueue(username); // TODO
			consumer = session.createConsumer(queue);
			return true;
		} catch (Exception e) {
			System.out.println("Fehler beim Verbinden");
			return false;
		}
	}

	/**
	 * Alle Mails in der Mailbox abrufen
	 */
	public void receiveMail() {
		try {
			while (true) {
				// Timeout zwischen mehreren Messages setzen
				Message message = consumer.receive(400);

				// Wenn keine Mails mehr vorhanden sind, abbrechen
				if (message == null)
					return;

				TextMessage tm = (TextMessage) message;
				System.out.println(tm.getText());
			}
		} catch (JMSException e) {
			System.out.println("Fehler beim Empfangen der Nachrichten");
		}
	}

	/**
	 * Eine Mail an einen Benutzer senden
	 * 
	 * @param ip
	 *            die IP des Benutzers
	 * @param messageStr
	 *            der Inhalt der Nachricht
	 */
	public void sendMail(String ip, String messageStr) {
		Destination destination;
		MessageProducer producer;
		try {
			destination = session.createQueue(ip);
			producer = session.createProducer(destination);
			String msg = username + " [xxx.xxx.xxx.xxx]:" + messageStr;
			TextMessage message = session.createTextMessage(msg);
			producer.send(message);
			producer.close();
		} catch (JMSException e) {
			System.out.println("Fehler beim Senden der Mail.");
		}
	}

	/**
	 * Sollte beim Beenden aufgerufen werden
	 */
	public void close() {
		try {
			consumer.close();
			session.close();
			connection.close();
		} catch (JMSException e) {
		}
	}
}
