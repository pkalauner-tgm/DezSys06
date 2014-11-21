package at.kalaunerwortha.dezsys06;

import java.io.Closeable;

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
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Zustaendig fuer das Senden der Chat-Nachrichten
 * 
 * @author Paul Kalauner 4AHIT
 * @version 20141115.1
 *
 */
public class JMSChatHandler implements Closeable {
	private static final Logger LOG = LogManager.getLogger(JMSChatHandler.class);
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
	 * @return true wenn erfolgreich
	 */
	public boolean connect(String ip, String username, String chatroom) {
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
			sendMessage("hat den Chatraum betreten");
			return true;
		} catch (Exception e) {
			LOG.error("Fehler beim Verbinden");
			return false;
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
			message = session.createTextMessage(username + "@" + IPHelper.getIP() + " " + messageStr);
			producer.send(message);
		} catch (JMSException e) {
			LOG.error("Fehler beim Senden der Nachricht");
		}
	}

	public void close() {
		if (connection == null)
			return;
		try {
			sendMessage("hat den Chatraum verlassen");
			rw.setRunning(false);
			consumer.close();
			producer.close();
			session.close();
			connection.close();
		} catch (JMSException e) {
			LOG.error("Fehler beim Schliessen der Connection");
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
