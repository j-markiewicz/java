/**
 * Interfejs eksploratora labiryntów
 */
public interface Explorer {
	/**
	 * Czas (liczba kolejnych dozwolonych ruchów) pod wodą. 
	 * @param moves liczba ruchów.
	 */
	public void underwaterMovesAllowed(int moves);

	/**
	 * Przekazanie kontrolera gracza.
	 * @param controller kontroler gracza
	 */
	public void setPlayerController(PlayerController controller);
	
	/**
	 * Rozpoczęcie poszukiwania wyjścia. Można zacząć wykonywać metody move() z 
	 * interfejsu kontroler. 
	 */
	public void findExit();
}
