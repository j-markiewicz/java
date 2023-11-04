import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class NumberStatisticsTest {
	static Position p(int x, int y) {
		return new Position(x, y);
	}

	@org.junit.jupiter.api.Test
	void coordsToIdx() {
		var ns = new NumberStatistics();
		ns.sideLength(5);
		assert ns.coordsToIdx(new Position(4, 3)) == 19;
		assert ns.coordsToIdx(new Position(0, 1)) == 5;
		assert ns.coordsToIdx(new Position(1, 0)) == 1;
	}

	@org.junit.jupiter.api.Test
	void mod() {
		assert NumberStatistics.mod(4, 5) == 4;
		assert NumberStatistics.mod(4, 3) == 1;
		assert NumberStatistics.mod(-4, 3) == 2;
		assert NumberStatistics.mod(-4, 5) == 1;
		assert NumberStatistics.mod(-4, -3) == 2;
	}

	@org.junit.jupiter.api.Test
	void normalizePos() {
		var ns = new NumberStatistics();
		ns.sideLength(5);
		var normal = new Position(3, 4);
		var nonnormal = new Position(8, -1);
		assert ns.normalizePos(normal).equals(normal);
		assert ns.normalizePos(nonnormal).equals(normal);
	}

	@org.junit.jupiter.api.Test
	void simpleDistanceSquared() {
		var normal = new Position(3, 4);
		var nonnormal = new Position(8, -1);
		assert NumberStatistics.simpleDistanceSquared(normal, normal) == 0;
		assert NumberStatistics.simpleDistanceSquared(nonnormal, normal) == 50;
		assert NumberStatistics.simpleDistanceSquared(normal, new Position(3, 0)) == 16;
	}

	@org.junit.jupiter.api.Test
	void distanceSquared() {
		var ns = new NumberStatistics();
		ns.sideLength(5);
		var normal = new Position(3, 4);
		var nonnormal = new Position(8, -1);
		assert ns.distanceSquared(normal, normal) == 0;
		assert ns.distanceSquared(nonnormal, normal) == 0;
		assert ns.distanceSquared(normal, new Position(3, 0)) == 1;
	}

	@org.junit.jupiter.api.Test
	void getSet() {
		var ns = new NumberStatistics();
		ns.sideLength(5);
		assert ns.get(new Position(1, 2)) == null;
		assert ns.get(11) == null;
		assert ns.get(new Position(2, 0)) == null;
		assert ns.get(2) == null;
		ns.set(new Position(1, 2), 5);
		ns.set(2, 15);
		assert ns.get(new Position(1, 2)).equals(5);
		assert ns.get(11).equals(5);
		assert ns.get(new Position(2, 0)).equals(15);
		assert ns.get(2).equals(15);
	}

	@org.junit.jupiter.api.Test
	void sideLength() {
		var ns = new NumberStatistics();
		ns.sideLength(5);
		assert ns.sideLength == 5;
	}

	@org.junit.jupiter.api.Test
	void addNumbers() {
		var ns = new NumberStatistics();
		ns.sideLength(5);
		ns.addNumbers(Map.of(
				1, Set.of(p(1, 1)),
				2, Set.of(p(4, 1), p(4, 2), p(3, 4)),
				5, Set.of(p(2, 2))
		));
		assert ns.get(new Position(2, 2)).equals(5);
		assert ns.get(new Position(4, 2)).equals(2);
		assert ns.get(new Position(2, 1)) == null;
	}

	@org.junit.jupiter.api.Test
	void neighbours() {
		var ns = new NumberStatistics();
		ns.sideLength(9);
		ns.addNumbers(Map.of(
				1, Set.of(p(4, 3), p(0, 4), p(4, 5), p(2, 6), p(0, 8)),
				2, Set.of(p(8, 0), p(0, 3), p(3, 3), p(8, 4)),
				3, Set.of(p(5, 6), p(8, 8)),
				4, Set.of(p(0, 0), p(5, 4), p(6, 5))
		));

		var res = ns.neighbours(p(4, 4), 8);
		assert res.equals(Map.of(
				1, Map.of(1, 2, 8, 1),
				2, Map.of(2, 1),
				3, Map.of(5, 1),
				4, Map.of(1, 1, 5, 1)
		));
	}
}
