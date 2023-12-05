import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class LeniwyEksperymentator implements LeniwyBadaczKostekDoGry {
	ExecutorService executor;
	final ConcurrentMap<Integer, Future<Map<Integer, Integer>>> results = new ConcurrentHashMap<>();

	/**
	 * Metoda dostarcza obiektu, który będzie odpowiedzialny za realizację zadań.
	 * Zadania analizy pracy kostek muszą być realizowane przez dostarczony przez tą
	 * metodę ExecutorService.
	 *
	 * @param executorService serwis, do którego należy dostarczać zadania badania
	 * kostek do gry.
	 */
	@Override
	public void fabrykaWatkow(ExecutorService executorService) {
		executor = executorService;
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

		Future<Map<Integer, Integer>> fut = executor.submit(() -> {
			var res = new HashMap<Integer, Integer>();
			for (int i = 0; i < liczbaRzutow; i++) {
				res.compute(kostka.rzut(), (_k, v) -> v == null ? 1 : v + 1);
			}
			return res;
		});

		synchronized (results) {
			id = results.size();
			results.put(id, fut);
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
		return results.get(identyfikator).isDone();
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
		try {
			return results.get(identyfikator).get();
		} catch (InterruptedException e) {
			return null;
		} catch (ExecutionException e) {
			throw new RuntimeException(e.getCause());
		}
	}
}
