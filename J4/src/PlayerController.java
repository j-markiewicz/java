
/**
 * Kontroler gracza
 */
public interface PlayerController {
	/**
	 * Metoda pozwala na zlecenie wykonania pojadynczego ruchu w określonym
	 * kierunku. O ile jest to tylko możliwe, położenie gracza ulega zmianie.
	 * Wyjątkiem jest wystąpienie wyjątku Wall, który oznacza, że ruch nie nastąpił,
	 * bo na pozycji decelowej znajduje się ściana.
	 * 
	 * @param direction kierunek ruchu
	 * @throws OnFire  ruch wykonano, ale docelowe pomieszczenie płonie. Należy
	 *                 natychmiast (w kolejnym ruchu) z niego uciekać.
	 * @throws Flooded pomieszczenie zalane wodą. Należy kontrolować czas (liczbę
	 *                 dozwolonych kroków) przebywania pod wodą.
	 * @throws Wall    ruchu nie wykonano, bo natrafiono na ścianę
	 * @throws Exit    Sukces! Odnaleziono wyjście. Koniec gry!
	 */
	public void move(Direction direction) throws OnFire, Flooded, Wall, Exit;
}
