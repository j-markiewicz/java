/**
 * Interfejs fabryki wątków.
 */
public interface ThreadFactory {
	/**
	 * Metoda zwraca nieuruchomiony wątek.
	 * 
	 * @param run obiekt przekazywany do konstruktora obiektu Thread
	 * @return nieuruchomiony wątek
	 */
	public Thread getThread(Runnable run);
}
