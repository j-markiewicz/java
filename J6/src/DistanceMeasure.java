/**
 * Interfejs systemu pomiaru odległości pomiędzy punktami
 */
public interface DistanceMeasure {
	/**
	 * Odległość pomiędzy punktami a i b.
	 * @param a punkt
	 * @param b punkt
	 * @return odległość a-b
	 */
	public double distance(Point a, Point b);
}
