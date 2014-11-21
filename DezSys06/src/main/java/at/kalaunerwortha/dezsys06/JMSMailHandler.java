package at.kalaunerwortha.dezsys06;

import java.io.Closeable;

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
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Zustaendig fuer das Empfangen und Senden der Mails
 * 
 * @author Paul Kalauner 4AHIT
 * @version 20141115.1
 *
 */
public class JMSMailHandler implements Closeable {
	private static final Logger LOG = LogManager.getLogger(JMSMailHandler.class);
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
			queue = session.createQueue(username + "@" + IPHelper.getIP());
			consumer = session.createConsumer(queue);
			return true;
		} catch (Exception e) {
			LOG.error("Fehler beim Verbinden");
			return false;
		}
	}

	/**
	 * Alle Mails in der Mailbox abrufen
	 */
	public void receiveMail() {
		System.out.println("Ihr Postfach:\n");
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
			LOG.error("Fehler beim Empfangen der Nachrichten", e);
		}
	}

	/**
	 * Eine Mail an einen Benutzer senden
	 * 
	 * @param receiver
	 *            Benutzername@IP
	 * @param messageStr
	 *            der Inhalt der Nachricht
	 */
	public void sendMail(String receiver, String messageStr) {
		Destination destination;
		MessageProducer producer;
		try {
			destination = session.createQueue(receiver);
			producer = session.createProducer(destination);
			String msg = username + "@" + IPHelper.getIP() + messageStr;
			TextMessage message = session.createTextMessage(msg);
			producer.send(message);
			producer.close();
		} catch (JMSException e) {
			LOG.error("Fehler beim Senden der Mail.", e);
		}
	}

	public void close() {
		if (connection == null)
			return;
		try {
			consumer.close();
			session.close();
			connection.close();
		} catch (JMSException e) {
			LOG.error("Fehler beim Schliessen der Connection", e);
		}
	}
}
