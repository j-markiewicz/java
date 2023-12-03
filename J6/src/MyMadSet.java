import java.util.ArrayList;
import java.util.List;

public class MyMadSet implements MadSet {
	double maxDisallowed;
	DistanceMeasure measurer;
	List<Point> points = new ArrayList<>();

	/**
	 * Ustawienie narzędzia pozwalającego na wyliczenie odległości pomiędzy punktami
	 *
	 * @param measure obiekt odpowiedzialny za obliczanie odległości
	 * @throws TooCloseException zmiana sposobu liczenia odległości doprowadziła do
	 * usunięcia punktów ze zbioru
	 */
	@Override
	public void setDistanceMeasure(DistanceMeasure measure) throws TooCloseException {
		var tooClose = new ArrayList<Point>();
		var oldPoints = points;
		points = new ArrayList<>();
		measurer = measure;

		for (var p: oldPoints) {
			try {
				addPoint(p);
			} catch (TooCloseException e) {
				tooClose.addAll(e.removePoints());
			}
		}

		if (!tooClose.isEmpty()) {
			throw new TooCloseException(tooClose);
		}
	}

	/**
	 * Ustalenie minimalnego, dozwolonego dystansu pomiędzy punktami
	 *
	 * @param minAllowed minimalna dozwolona odległość pomiędzy punktami
	 * @throws TooCloseException zmiana dystansu doprowadziła do usunięcia punktów
	 * ze zbioru
	 */
	@Override
	public void setMinDistanceAllowed(double minAllowed) throws TooCloseException {
		// "A(id:2148)> Ustalmy że:
		// Usuwamy przy dystansie <= minAllowed
		// Dodajemy przy dystansie > minAllowed"

		var tooClose = new ArrayList<Point>();
		var oldPoints = points;
		points = new ArrayList<>();
		maxDisallowed = minAllowed;

		for (var p: oldPoints) {
			try {
				addPoint(p);
			} catch (TooCloseException e) {
				tooClose.addAll(e.removePoints());
			}
		}

		if (!tooClose.isEmpty()) {
			throw new TooCloseException(tooClose);
		}
	}

	/**
	 * Próba dodania punktu do zbioru. Punkt jest dodawany jeśli oddalony jest od
	 * każdego innego punktu w zbiorze co najmniej o minAllowed. Jeśli odległość
	 * punktu od istniejących w zbiorze nie jest wystarczająca, nowy punkt nie jest
	 * dodawany i <b>dodatkowo</b> ze zbioru usuwane są także wszystkie punkty,
	 * które z nowym sąsiadowałyby o odległość mniejszą niż limit.
	 *
	 * @param point punkt, który ma zostać dodany do zbioru
	 * @throws TooCloseException nowy punkt znajduje się zbyt blisko istniejących.
	 * Nowy punkt nie jest dodawany do zbioru. Oodatkowo
	 * usuwane są ze zbioru te punkty, dla których dodanie
	 * nowego spowodowałoby przekroczenie minimalnego
	 * dystansu. Nowy punkt również znajduje się na liście
	 * usuwanych punktów.
	 */
	@Override
	public void addPoint(Point point) throws TooCloseException {
		var tooClose = points.stream().filter((p) -> measurer.distance(point, p) <= maxDisallowed).toList();

		if (tooClose.isEmpty()) {
			points.add(point);
		} else {
			tooClose.forEach((p) -> points.remove(p));
			tooClose = new ArrayList<>(tooClose);
			tooClose.add(point);
			throw new TooCloseException(tooClose);
		}
	}

	/**
	 * Lista punktów w zbiorze. Lista zawiera punkty w kolejności ich dodawnia do
	 * zbioru.
	 *
	 * @return lista punktów w zbiorze
	 */
	@Override
	public List<Point> getPoints() {
		return points;
	}

	/**
	 * Lista punktów w zbiorze posortowana wg. rosnącej odległości od punktu
	 * odniesienia.
	 *
	 * @param referencePoint punkt odniesienia
	 * @return posortowana lista punktów
	 */
	@Override
	public List<Point> getSortedPoints(Point referencePoint) {
		return points.stream().sorted((a, b) -> (int) Math.signum(
			measurer.distance(referencePoint, a) - measurer.distance(referencePoint, b)
		)).toList();
	}
}
