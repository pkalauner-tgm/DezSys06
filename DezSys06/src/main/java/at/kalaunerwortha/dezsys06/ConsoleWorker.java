package at.kalaunerwortha.dezsys06;

/**
 * Zustaendig fuer die Auswerung der Konsoleneingaben
 * 
 * @author Paul Kalauner 4AHIT
 * @version 20141115.1
 *
 */
public class ConsoleWorker {
	private JMSChatHandler jmsChat;
	private JMSMailHandler jmsMail;
	private boolean loggedIn;

	/**
	 * Initialisierung des ConsoleWorkers mit Angabe des Chat- und Mailhandlers
	 * 
	 * @param jmsChat
	 *            der zu verwendende Chathandler
	 * @param jmsMail
	 *            der zu verwendende Mailhandler
	 */
	public ConsoleWorker(JMSChatHandler jmsChat, JMSMailHandler jmsMail) {
		this.jmsChat = jmsChat;
		this.jmsMail = jmsMail;
		this.loggedIn = false;
	}

	/**
	 * Verarbeitung einer Konsoleneingabe
	 * 
	 * @param line
	 *            die Konsoleneingabe
	 */
	public void processLine(String line) {
		String[] args = line.split(" ");
		if (args[0].equalsIgnoreCase("vsdbchat")) {
			if (args.length < 4) {
				System.out.println("Ungültige Argumente:\nvsdbchat <ip_message_broker> <benutzername> <chatroom>");
			} else {
				loggedIn = jmsChat.connect(args[1], args[2], args[3]) && jmsMail.connect(args[1], args[2]);
				if (loggedIn)
					System.out.println("Willkommen im Chatroom " + args[3]
							+ "!\nUm private Nachrichten zu senden, benutzen Sie bitte das MAIL Kommando:\nMAIL <Benutzername_des_Empfängers@IP_des_Empfängers> <nachricht>\nUm Ihre Nachrichten abzurufen, verwenden sie MAILBOX");
			}
		} else if (loggedIn) {
			if (args[0].equalsIgnoreCase("MAIL")) {
				if (args.length < 3) {
					System.out.println("Ungültige Argumente:\nMAIL <Benutzername@IP> <nachricht>");
				} else
					jmsMail.sendMail(args[1], buildMessage(args, 2));
			} else if (args[0].equalsIgnoreCase("MAILBOX")) {
				jmsMail.receiveMail();
			} else
				jmsChat.sendMessage(line);
		} else
			System.out.println("Bitte melden Sie sich zuerst mit folgendem Befehl an:\nvsdbchat <ip_message_broker> <benutzername> <chatroom>\n(Standardport: 61616)");
	}

	/**
	 * Baut aus den Kommandolineargumenten einen String
	 * 
	 * @param args
	 *            String Array von Woertern
	 * @param startIndex
	 *            Index, wo die Nachricht beginnt
	 * @return Zusammengebauter String
	 */
	private static String buildMessage(String[] args, int startIndex) {
		StringBuilder sb = new StringBuilder();
		for (int i = startIndex; i < args.length; i++)
			sb.append(" " + args[i]);
		return sb.toString();
	}
}
