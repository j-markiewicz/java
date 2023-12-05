import java.util.Map;

/**
 * Interfejs systemu badania kostek do gry.
 */
public interface BadaczKostekDoGry {
	/**
	 * Metoda ustala liczbę wątków, które jednocześnie mogą badać kostki do gry.
	 * Ponieważ jedną kostkę badać może tylko jeden wątek metoda jednocześnie ustala
	 * liczbę jednocześnie testowanych kostek.
	 * 
	 * @param limitWatkow dozwolona liczba wątków
	 */
	public void dozwolonaLiczbaDzialajacychWatkow(int limitWatkow);

	/**
	 * Metoda dostarcza obiektu, który będzie odpowiedzialny za produkcję wątków
	 * niezbędnych do pracy programu. Tylko wyprodukowane przez fabrykę wątki mogą
	 * używać kostek.
	 * 
	 * @param fabryka referencja do obiektu produkującego wątki
	 */
	public void fabrykaWatkow(ThreadFactory fabryka);

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
