package at.kalaunerwortha.dezsys06;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Diese Klasse liest die Public IP des Benutzers von
 * http://checkip.amazonaws.com/
 * 
 * @author Paul Kalauner 4AHIT
 *
 */
public class IPHelper {
	private static final Logger LOG = LogManager.getLogger(IPHelper.class);
	private static String ip;

	/**
	 * Liefert die oeffentliche IP
	 * 
	 * @return String mit der IP
	 */
	public static String getIP() {
		if (ip == null) {
			try {
				URL whatismyip = new URL("http://checkip.amazonaws.com/");
				BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
				ip = in.readLine();
			} catch (IOException e) {
				LOG.error("Couldn't get public IP", e);
			}
		}
		return ip;
	}
}
