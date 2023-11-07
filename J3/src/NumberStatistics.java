import java.util.*;

public class NumberStatistics implements Statistics {
	Integer[] plane;
	int sideLength;

	int coordsToIdx(Position pos) {
		return pos.row() * sideLength + pos.col();
	}

	static int mod(int a, int b) {
		return a < 0 ? (a % b) + Math.abs(b) : a % b;
	}

	Position normalizePos(Position pos) {
		return new Position(mod(pos.col(), sideLength), mod(pos.row(), sideLength));
	}

	static int simpleDistanceSquared(Position from, Position to) {
		var y = to.row() - from.row();
		var x = to.col() - from.col();
		return x * x + y * y;
	}

	int distanceSquared(Position from, Position to) {
		var start = normalizePos(from);
		var end = normalizePos(to);

		var ends = List.of(new Position(end.col(), end.row()),
				new Position(end.col(), end.row() - sideLength),
				new Position(end.col() - sideLength, end.row()),
				new Position(end.col() - sideLength, end.row() - sideLength),
				new Position(end.col(), end.row() + sideLength),
				new Position(end.col() + sideLength, end.row()),
				new Position(end.col() + sideLength, end.row() + sideLength)
		);

		return ends.stream().map((pos) -> simpleDistanceSquared(
				start,
				pos
		)).min(Comparator.naturalOrder()).orElseThrow();
	}

	Integer get(Position pos) {
		return get(coordsToIdx(pos));
	}

	Integer get(int idx) {
		return plane[idx];
	}

	void set(Position pos, Integer value) {
		set(coordsToIdx(pos), value);
	}

	void set(int idx, Integer value) {
		plane[idx] = value;
	}

	/**
	 * Ustalenie długości boku płaszczyzny.
	 * Płaszczyzna jest kwadratem o boku length.
	 *
	 * @param length długość boku płaszczyzny.
	 */
	@Override
	public void sideLength(int length) {
		sideLength = length;
		plane = new Integer[length * length];
	}

	/**
	 * Do płaszczyzny dodajemy liczby.
	 *
	 * @param numberPositions mapa zawierająca jako klucz wartość liczby, zbiór
	 * zawiera położenia, w których liczbę należy umieścić.
	 */
	@Override
	public void addNumbers(Map<Integer, Set<Position>> numberPositions) {
		numberPositions.forEach((num, positions) -> {
			positions.forEach((pos) -> set(normalizePos(pos), num));
		});
	}

	/**
	 * Pobranie informacji o zajętych przez liczby sąsiednich polach. Wynik zwracany
	 * jest w postaci mapy map. Obliczenia prowadzone są niezależnie dla różnych
	 * wartości sąsiednich liczb. Zewnętrzna mapa zawiera klucz, który określa
	 * wartość liczby, wewnętrzna mapa to informacja dla jakiego kwadratu odległości
	 * znaleziono ile liczb.
	 *
	 * @param position położenie, którego sąsiedztwo jest badane.
	 * @param maxDistanceSquared maksymalny kwadrat odległości dla jakiej należy
	 * jeszcze odszukiwać sąsiadów.
	 * @return informacja o sąsiadach pola position.
	 */
	@Override
	public Map<Integer, Map<Integer, Integer>> neighbours(Position position, int maxDistanceSquared) {
		var res = new HashMap<Integer, Map<Integer, Integer>>();

		for (int i = 0; i < sideLength; i++) {
			for (int j = 0; j < sideLength; j++) {
				var tile = new Position(i, j);

				var dSq = distanceSquared(position, tile);
				if (dSq > maxDistanceSquared) {
					continue;
				}

				var val = get(tile);
				if (val == null) {
					continue;
				}

				res.putIfAbsent(val, new HashMap<>());
				res.compute(val, (_k, m) -> {
					m.putIfAbsent(dSq, 0);
					m.compute(dSq, (_k2, v) -> v + 1);
					return m;
				});
			}
		}

		return res;
	}
}
