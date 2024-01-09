/**
 * Interfejs połączeniowy
 */
public interface NetConnection {
	public final String ODPOWIEDZ_DLA_OSZUSTA = "Figa";
	
	/**
	 * Metoda przekazuje poprawne hasło. Jest nim duża liczba całkowita zapisana
	 * jako ciąg znaków.
	 * 
	 * @param password poprawne hasło do serwisu
	 */
	public void password( String password );
	
	/**
	 * Metoda otwiera połączenie do serwera dostępnego protokołem TCP/IP pod adresem
	 * host i numerem portu TCP port.
	 * 
	 * @param host adres IP lub nazwa komputera
	 * @param port numer portu, na którym serwer oczekuje na połączenie
	 */
	public void connect(String host, int port);
}
