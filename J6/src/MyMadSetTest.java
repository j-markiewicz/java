import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TestDistanceSquaredMeasure implements DistanceMeasure {
	/**
	 * Odległość pomiędzy punktami a i b.
	 *
	 * @param a punkt
	 * @param b punkt
	 * @return odległość a-b
	 */
	@Override
	public double distance(Point a, Point b) {
		return (a.x() - b.x()) * (a.x() - b.x()) + (a.y() - b.y()) * (a.y() - b.y());
	}
}

class TestDistanceMeasure implements DistanceMeasure {
	/**
	 * Odległość pomiędzy punktami a i b.
	 *
	 * @param a punkt
	 * @param b punkt
	 * @return odległość a-b
	 */
	@Override
	public double distance(Point a, Point b) {
		return Math.sqrt((a.x() - b.x()) * (a.x() - b.x()) + (a.y() - b.y()) * (a.y() - b.y()));
	}
}

class MyMadSetTest {
	@org.junit.jupiter.api.Test
	void setDistanceMeasure() {
		var set = new MyMadSet();
		try {
			set.setMinDistanceAllowed(4);
			set.setDistanceMeasure(new TestDistanceSquaredMeasure());
			set.addPoint(new Point(0, 0));
			set.addPoint(new Point(2, 2));
			set.addPoint(new Point(0, -4));
		} catch (TooCloseException e) {
			assert false;
		}

		try {
			set.setDistanceMeasure(new TestDistanceMeasure());
		} catch (TooCloseException e) {
			assert !e.removePoints().isEmpty();
			return;
		}
		assert false;
	}

	@org.junit.jupiter.api.Test
	void setMinDistanceAllowed() {
		var set = new MyMadSet();
		try {
			set.setMinDistanceAllowed(4);
			set.setDistanceMeasure(new TestDistanceSquaredMeasure());
			set.addPoint(new Point(0, 0));
			set.addPoint(new Point(2, 2));
			set.addPoint(new Point(0, -4));
		} catch (TooCloseException e) {
			assert false;
		}

		try {
			set.setMinDistanceAllowed(16);
		} catch (TooCloseException e) {
			assert !e.removePoints().isEmpty();
			return;
		}
		assert false;
	}

	@org.junit.jupiter.api.Test
	void addPoint() {
		var set = new MyMadSet();
		try {
			set.setMinDistanceAllowed(4);
			set.setDistanceMeasure(new TestDistanceMeasure());
			set.addPoint(new Point(0, 0));
			set.addPoint(new Point(5, 5));
		} catch (TooCloseException e) {
			assert false;
		}

		try {
			set.addPoint(new Point(0.5, 5));
		} catch (TooCloseException e) {
			assert false;
		}

		try {
			set.addPoint(new Point(7, 8));
		} catch (TooCloseException e) {
			assert e.removePoints().size() == 2;
			assert e.removePoints().containsAll(List.of(new Point(5, 5), new Point(7, 8)));
			return;
		}
		assert false;
	}

	@org.junit.jupiter.api.Test
	void addPointEqualsMinimum() {
		var set = new MyMadSet();
		try {
			set.setMinDistanceAllowed(4);
			set.setDistanceMeasure(new TestDistanceMeasure());
			set.addPoint(new Point(0, 0));
		} catch (TooCloseException e) {
			assert false;
		}

		try {
			set.addPoint(new Point(0, 4));
		} catch (TooCloseException e) {
			assert e.removePoints().size() == 2;
			assert e.removePoints().containsAll(List.of(new Point(0, 0), new Point(0, 4)));
			return;
		}
		assert false;
	}

	@org.junit.jupiter.api.Test
	void addPointMultiple() {
		var set = new MyMadSet();
		try {
			set.setMinDistanceAllowed(1.2);
			set.setDistanceMeasure(new TestDistanceMeasure());
			set.addPoint(new Point(10, 10));
			set.addPoint(new Point(1, 0));
			set.addPoint(new Point(-1, 0));
			set.addPoint(new Point(0, 1));
			set.addPoint(new Point(0, -1));
		} catch (TooCloseException e) {
			assert false;
		}

		try {
			set.addPoint(new Point(0, 0));
		} catch (TooCloseException e) {
			assert e.removePoints().size() == 5;
			assert e.removePoints().containsAll(List.of(
					new Point(0, 0),
					new Point(0, 1),
					new Point(0, -1),
					new Point(1, 0),
					new Point(-1, 0)
			));
			return;
		}
		assert false;
	}

	@org.junit.jupiter.api.Test
	void getPoints() {
		var set = new MyMadSet();
		try {
			set.setMinDistanceAllowed(4);
			set.setDistanceMeasure(new TestDistanceSquaredMeasure());
			set.addPoint(new Point(0, 0));
			set.addPoint(new Point(2, 2));
			set.addPoint(new Point(0, -4));
		} catch (TooCloseException e) {
			assert false;
		}

		assert set.getPoints().equals(List.of(new Point(0, 0), new Point(2, 2), new Point(0, -4)));
	}

	@org.junit.jupiter.api.Test
	void getSortedPoints() {
		var set = new MyMadSet();
		try {
			set.setMinDistanceAllowed(4);
			set.setDistanceMeasure(new TestDistanceSquaredMeasure());
			set.addPoint(new Point(0, 0));
			set.addPoint(new Point(2, 2));
			set.addPoint(new Point(0, -4));
		} catch (TooCloseException e) {
			assert false;
		}

		assert set.getSortedPoints(new Point(-10, -10)).equals(List.of(new Point(0, -4), new Point(0, 0), new Point(2, 2)));
	}
}
