package at.kalaunerwortha.dezsys06;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * Zustaendig fuer das Senden der Chat-Nachrichten
 * 
 * @author Paul Kalauner 4AHIT
 * @version 20141115.1
 *
 */
public class JMSChatHandler {
	private Session session;
	private MessageProducer producer;
	private Connection connection;
	private String username;
	private MessageConsumer consumer;
	private ReceiveWorker rw;

	/**
	 * stellt Verbindung zum message broker her und erstellt das Topic
	 * 
	 * @param ip
	 *            des Nessage Brokers
	 * @param username
	 *            Username mit dem der Benutzer chattet
	 * @param chatroom
	 *            Name des Chatrooms
	 */
	public void connect(String ip, String username, String chatroom) {
		this.username = username;
		try {
			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(username, ActiveMQConnection.DEFAULT_PASSWORD, ip);
			connection = connectionFactory.createConnection();
			connection.start();

			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Destination destination = session.createTopic(chatroom);
			consumer = session.createConsumer(destination);
			producer = session.createProducer(destination);
			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

			rw = new ReceiveWorker(this);
			new Thread(rw).start();

		} catch (Exception e) {
			System.out.println("Fehler beim Verbinden");
		}
	}

	/**
	 * sendet eine Nachricht an alle aktiven Benutzer des Chatrooms
	 * 
	 * @param messageStr
	 *            der Inhalt der Nachricht
	 */
	public void sendMessage(String messageStr) {
		TextMessage message;
		try {
			message = session.createTextMessage(username + " [xxx.xxx.xxx.xxx]: " + messageStr);
			producer.send(message);
		} catch (JMSException e) {
			e.printStackTrace();
			System.out.println("Fehler beim Senden der Nachricht");
		}
	}

	/**
	 * Sollte beim Abmelden des Benutzers
	 */
	public void close() {
		try {
			rw.setRunning(false);
			consumer.close();
			producer.close();
			session.close();
			connection.close();
		} catch (JMSException e) {
		}

	}

	/**
	 * liefert den MessageConsumer zurueck
	 * 
	 * @return der Messageconsumer fuer den spezifischen Chatroom
	 */
	public MessageConsumer getConsumer() {
		return consumer;
	}
}
