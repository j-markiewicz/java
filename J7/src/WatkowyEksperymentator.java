import java.util.*;
import java.util.concurrent.*;

public class WatkowyEksperymentator implements BadaczKostekDoGry {
	Semaphore concurrencyLimit;
	ThreadFactory threadFactory;
	final ConcurrentMap<Integer, Optional<Map<Integer, Integer>>> results = new ConcurrentHashMap<>();

	/**
	 * Metoda ustala liczbę wątków, które jednocześnie mogą badać kostki do gry.
	 * Ponieważ jedną kostkę badać może tylko jeden wątek metoda jednocześnie ustala
	 * liczbę jednocześnie testowanych kostek.
	 *
	 * @param limitWatkow dozwolona liczba wątków
	 */
	@Override
	public void dozwolonaLiczbaDzialajacychWatkow(int limitWatkow) {
		concurrencyLimit = new Semaphore(limitWatkow);
	}

	/**
	 * Metoda dostarcza obiektu, który będzie odpowiedzialny za produkcję wątków
	 * niezbędnych do pracy programu. Tylko wyprodukowane przez fabrykę wątki mogą
	 * używać kostek.
	 *
	 * @param fabryka referencja do obiektu produkującego wątki
	 */
	@Override
	public void fabrykaWatkow(ThreadFactory fabryka) {
		threadFactory = fabryka;
	}

	/**
	 * Metoda przekazuję kostkę do zbadania. Metoda nie blokuje wywołującego ją
	 * wątku na czas badania kostki. Metoda zwraca unikalny identyfikator zadania.
	 * Za pomocą tego identyfikatora użytkownik będzie mógł odebrać wynik zlecenia.
	 *
	 * @param kostka kostka do zbadania
	 * @param liczbaRzutow liczba rzutów, które należy wykonać w celu zbadania
	 * kostki
	 * @return unikalny identyfikator zadania.
	 */
	@Override
	public int kostkaDoZbadania(KostkaDoGry kostka, int liczbaRzutow) {
		int id;
		synchronized (results) {
			id = results.size();
			results.put(id, Optional.empty());
		}

		synchronized (threadFactory) {
			threadFactory.getThread(() -> {
				var res = new HashMap<Integer, Integer>();
				try {
					concurrencyLimit.acquire();
				} catch (InterruptedException e) {
					return;
				}

				for (int i = 0; i < liczbaRzutow; i++) {
					res.compute(kostka.rzut(), (_k, v) -> v == null ? 1 : v + 1);
				}

				concurrencyLimit.release();
				results.put(id, Optional.of(res));
			}).start();
		}

		return id;
	}

	/**
	 * Metoda pozwala użytkownikowi spawdzić, czy badanie kostki zostało zakończone.
	 * Zaraz po zakończeniu badania kostki użytkownik powinien uzyskać prawdę.
	 *
	 * @param identyfikator identyfikator zadania zwrócony przez metodę
	 * kostkaDoZbadania
	 * @return true - analiza kostki zakończona, w każdym innym przypadku false.
	 */
	@Override
	public boolean badanieKostkiZakonczono(int identyfikator) {
		return results.get(identyfikator).isPresent();
	}

	/**
	 * Wynik badania kostki. Zaraz po potwierdzeniu, że wynik jest gotowy użytkownik
	 * powinien uzyskać histogram zawierający wynik wszystkich rzutów kostką.
	 *
	 * @param identyfikator identyfikator zadania zwrócony przez metodę
	 * kostkaDoZbadania
	 * @return histogram - mapa, której kluczem jest liczba oczek, wartością liczba
	 * rzutów, w których otrzymano tą liczbę oczek.
	 */
	@Override
	public Map<Integer, Integer> histogram(int identyfikator) {
		return results.get(identyfikator).orElse(null);
	}
}
