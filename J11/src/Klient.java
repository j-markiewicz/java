import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Klient implements NetConnection {
	static final ByteBuffer REQUEST = ByteBuffer.wrap("Program\n".getBytes(StandardCharsets.UTF_8));

	BigInteger password;

	/**
	 * Metoda przekazuje poprawne hasło. Jest nim duża liczba całkowita zapisana
	 * jako ciąg znaków.
	 *
	 * @param password poprawne hasło do serwisu
	 */
	@Override
	public void password(String password) {
		this.password = new BigInteger(password);
	}

	/**
	 * Metoda otwiera połączenie do serwera dostępnego protokołem TCP/IP pod adresem
	 * host i numerem portu TCP port.
	 *
	 * @param host adres IP lub nazwa komputera
	 * @param port numer portu, na którym serwer oczekuje na połączenie
	 */
	@Override
	public void connect(String host, int port) {
		try (var socket = new Socket(host, port)) {
			socket.getOutputStream().write(REQUEST.array());
			var reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			var sum = reader.lines().takeWhile(l -> !l.equals("EOD")).filter(l -> l.matches("^\\d+$")).map(BigInteger::new).reduce(BigInteger.TWO,
					BigInteger::add
			);
			sum = sum.add(password);
			var res = new BigInteger(reader.lines().map(l -> Pattern.compile(".*?(\\d+).*\\?").matcher(l)).filter(
					Matcher::matches).findFirst().orElseThrow().group(1));

			if (sum.equals(res)) {
				socket.getOutputStream().write(sum.toString().getBytes(StandardCharsets.UTF_8));
			} else {
				socket.getOutputStream().write(ODPOWIEDZ_DLA_OSZUSTA.getBytes(StandardCharsets.UTF_8));
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
