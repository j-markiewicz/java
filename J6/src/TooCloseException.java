import java.util.List;

/**
 * Wyjątek informujący o zbyt małej odległości pomiędzy punktami.
 */
public class TooCloseException extends Exception {

	private static final long serialVersionUID = 4304209154421503650L;
	private final List<Point> removedPoints;

	/**
	 * Punkty zbyt blisko siebie
	 * @param removedPoints lista punktów, które są zbyt blisko
	 */
	public TooCloseException(List<Point> removedPoints) {
		this.removedPoints = removedPoints;
	}

	/**
	 * Lista usuniętych punktów
	 * 
	 * @return usunięte punkty
	 */
	public List<Point> removePoints() {
		return removedPoints;
	}
}
