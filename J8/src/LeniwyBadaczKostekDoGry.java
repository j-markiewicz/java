import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * Interfejs systemu badania kostek do gry.
 */
public interface LeniwyBadaczKostekDoGry {
	/**
	 * Metoda dostarcza obiektu, który będzie odpowiedzialny za realizację zadań.
	 * Zadania analizy pracy kostek muszą być realizowane przez dostarczony przez tą
	 * metodę ExecutorService.
	 * 
	 * @param executorService serwis, do którego należy dostarczać zadania badania
	 *                        kostek do gry.
	 */
	public void fabrykaWatkow(ExecutorService executorService);

	/**
	 * Metoda przekazuję kostkę do zbadania. Metoda nie blokuje wywołującego ją
	 * wątku na czas badania kostki. Metoda zwraca unikalny identyfikator zadania.
	 * Za pomocą tego identyfikatora użytkownik będzie mógł odebrać wynik zlecenia.
	 * 
	 * @param kostka       kostka do zbadania
	 * @param liczbaRzutow liczba rzutów, które należy wykonać w celu zbadania
	 *                     kostki
	 * @return unikalny identyfikator zadania.
	 */
	public int kostkaDoZbadania(KostkaDoGry kostka, int liczbaRzutow);

	/**
	 * Metoda pozwala użytkownikowi spawdzić, czy badanie kostki zostało zakończone.
	 * Zaraz po zakończeniu badania kostki użytkownik powinien uzyskać prawdę.
	 * 
	 * @param identyfikator identyfikator zadania zwrócony przez metodę
	 *                      kostkaDoZbadania
	 * @return true - analiza kostki zakończona, w każdym innym przypadku false.
	 */
	public boolean badanieKostkiZakonczono(int identyfikator);

	/**
	 * Wynik badania kostki. Zaraz po potwierdzeniu, że wynik jest gotowy użytkownik
	 * powinien uzyskać histogram zawierający wynik wszystkich rzutów kostką.
	 * 
	 * @param identyfikator identyfikator zadania zwrócony przez metodę
	 *                      kostkaDoZbadania
	 * @return histogram - mapa, której kluczem jest liczba oczek, wartością liczba
	 *         rzutów, w których otrzymano tą liczbę oczek.
	 */
	public Map<Integer, Integer> histogram(int identyfikator);

}
