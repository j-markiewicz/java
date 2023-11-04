import java.util.Map;
import java.util.Set;

/**
 * Interfejs systemu analizującego sąsiedztwa pól na płaszczyźnie.
 */
public interface Statistics {
	/**
	 * Ustalenie długości boku płaszczyzny. 
	 * Płaszczyzna jest kwadratem o boku length.
	 * @param length długość boku płaszczyzny.
	 */
	public void sideLength( int length );

	/**
	 * Do płaszczyzny dodajemy liczby.
	 * 
	 * @param numberPositions mapa zawierająca jako klucz wartość liczby, zbiór
	 *                        zawiera położenia, w których liczbę należy umieścić.
	 */
	public void addNumbers(Map<Integer, Set<Position>> numberPositions);

	/**
	 * Pobranie informacji o zajętych przez liczby sąsiednich polach. Wynik zwracany
	 * jest w postaci mapy map. Obliczenia prowadzone są niezależnie dla różnych
	 * wartości sąsiednich liczb. Zewnętrzna mapa zawiera klucz, który określa
	 * wartość liczby, wewnętrzna mapa to informacja dla jakiego kwadratu odległości
	 * znaleziono ile liczb.
	 * 
	 * @param position           położenie, którego sąsiedztwo jest badane.
	 * @param maxDistanceSquared maksymalny kwadrat odległości dla jakiej należy
	 *                           jeszcze odszukiwać sąsiadów.
	 * @return informacja o sąsiadach pola position.
	 */
	public Map<Integer, Map<Integer, Integer>> neighbours(Position position, int maxDistanceSquared);
}
