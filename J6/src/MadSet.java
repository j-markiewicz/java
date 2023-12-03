import java.util.List;

/**
 * Interfejs szalonego zbioru punktów
 */
public interface MadSet {
	/**
	 * Ustawienie narzędzia pozwalającego na wyliczenie odległości pomiędzy punktami
	 * 
	 * @param measure obiekt odpowiedzialny za obliczanie odległości
	 * @throws TooCloseException zmiana sposobu liczenia odległości doprowadziła do
	 *                           usunięcia punktów ze zbioru
	 */
	public void setDistanceMeasure(DistanceMeasure measure) throws TooCloseException;

	/**
	 * Ustalenie minimalnego, dozwolonego dystansu pomiędzy punktami
	 * 
	 * @throws TooCloseException zmiana dystansu doprowadziła do usunięcia punktów
	 *                           ze zbioru
	 * @param minAllowed minimalna dozwolona odległość pomiędzy punktami
	 */
	public void setMinDistanceAllowed(double minAllowed) throws TooCloseException;

	/**
	 * Próba dodania punktu do zbioru. Punkt jest dodawany jeśli oddalony jest od
	 * każdego innego punktu w zbiorze co najmniej o minAllowed. Jeśli odległość
	 * punktu od istniejących w zbiorze nie jest wystarczająca, nowy punkt nie jest
	 * dodawany i <b>dodatkowo</b> ze zbioru usuwane są także wszystkie punkty,
	 * które z nowym sąsiadowałyby o odległość mniejszą niż limit.
	 * 
	 * @param point punkt, który ma zostać dodany do zbioru
	 * @throws TooCloseException nowy punkt znajduje się zbyt blisko istniejących.
	 *                           Nowy punkt nie jest dodawany do zbioru. Oodatkowo
	 *                           usuwane są ze zbioru te punkty, dla których dodanie
	 *                           nowego spowodowałoby przekroczenie minimalnego
	 *                           dystansu. Nowy punkt również znajduje się na liście
	 *                           usuwanych punktów.
	 */
	public void addPoint(Point point) throws TooCloseException;

	/**
	 * Lista punktów w zbiorze. Lista zawiera punkty w kolejności ich dodawnia do
	 * zbioru.
	 * 
	 * @return lista punktów w zbiorze
	 */
	public List<Point> getPoints();

	/**
	 * Lista punktów w zbiorze posortowana wg. rosnącej odległości od punktu
	 * odniesienia.
	 * 
	 * @param referencePoint punkt odniesienia
	 * @return posortowana lista punktów
	 */
	public List<Point> getSortedPoints(Point referencePoint);
}
