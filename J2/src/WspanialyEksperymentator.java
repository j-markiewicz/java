import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class WspanialyEksperymentator implements Eksperymentator {
	KostkaDoGry die;
	Duration experimentTimeout;

	int throwDie() {
		if (die == null) {
			throw new NoDieException();
		}

		var res = die.rzut();
		if (res < 1 || res > 6) {
			throw new WeirdDieException();
		}
		return res;
	}

	Instant getExperimentDeadline(Instant experimentStart) {
		if (experimentTimeout == null) {
			throw new NoTimeoutException();
		}

		return experimentStart.plus(experimentTimeout);
	}

	/**
	 * Eksperymentatorowi przekazujemy kostkę do gry. Wszystkie eksperymenty należy
	 * przeprowadzić z zastosowaniem powierzonej tu kostki. Kostki nie wolno używać
	 * do innych celów niż wykonanie eksperymentów (wszystkie rzuty kostką muszą
	 * zostać uwzględnione w wyliczonych prawdopodobieństwach).
	 *
	 * @param kostka kostka do gry
	 */
	@Override
	public void użyjKostki(KostkaDoGry kostka) {
		die = kostka;
	}

	/**
	 * Ustalenie całkowitego czasu trwania eksperymentu w milisekundach.
	 * Prawdopodobieństwa mają być szacowane przez eksperymentatora jako iloraz
	 * liczby prób zakończonych sukcesem do liczby wszystkich prób. Na wykonanie
	 * wszystkich prób eksperymentator ma czasEksperymentu. W okresie
	 * czasEksperymentu należy wykonać możliwie dużo prób.
	 *
	 * @param czasEksperymentu całkowity czas na wykonanie eksperymentu
	 */
	@Override
	public void czasJednegoEksperymentu(long czasEksperymentu) {
		experimentTimeout = Duration.ofMillis(czasEksperymentu);
	}

	/**
	 * Metoda zwraca prawdopodobieństwo wyrzucenia określonej, sumarycznej liczby
	 * oczek przy rzucie pewną liczbaKostek. W tym eksperymencie przez
	 * czasEksperymentu rzucamy liczbaKostek. Metoda stara się oszacować szansę na
	 * wyrzucenie określonej sumy oczek, zliczamy więc wylosowane liczby oczek.
	 * Znając liczbę wszystkich rzutów (każdy to rzut liczbaKostek kostek) i ile
	 * razy wylosowała się określona suma można wyznaczyć poszukiwane
	 * prawdopodobieństwa.
	 *
	 * @param liczbaKostek liczba kostek używana w jedym rzucie
	 * @return mapa, w której kluczem jest sumaryczna liczba oczek a wartością
	 * szansa na wystąpienie tej sumy oczek.
	 */
	@Override
	public Map<Integer, Double> szansaNaWyrzucenieOczek(int liczbaKostek) {
		var count = 0;
		var res = new HashMap<Integer, Double>();

		var start = Instant.now();
		while (Instant.now().isBefore(getExperimentDeadline(start))) {
			count++;
			var total = 0;

			for (var i = 0; i < liczbaKostek; i++) {
				total += throwDie();
			}

			res.compute(total, (_key, current) -> current == null ? 1 : current + 1);
		}

		final var amount = count;
		res.entrySet().forEach((entry) -> entry.setValue(entry.getValue() / amount));
		return res;
	}

	/**
	 * Metoda sprawdza szansę na wyrzucenie określonej sekwencji oczek. Zadaną
	 * sekwencją może być np. 1, 2 i 4. Jeśli w kolejnych rzutach kostką otrzymamy
	 * przykładowo:
	 *
	 * <pre>
	 * 1 2 5
	 * 3 4 1  &lt;- w tej i kolejnej linijce mamy łącznie 1 2 i 4, ale tu nie zliczamy trafienia
	 * 2 4 1
	 * <b>1 2 4</b>
	 * </pre>
	 * to szansa na wyrzucenie tej sekwencji to: 1/5 czyli 0.2.
	 *
	 * @param sekwencja lista kolejnych liczb oczek jakie mają zostać wyrzucone
	 * @return szansa na wyrzucenie wskazanej sekwencji.
	 */
	@Override
	public double szansaNaWyrzucenieKolejno(List<Integer> sekwencja) {
		var occurences = 0;
		var attempts = 0;
		var start = Instant.now();

		var result = new ArrayList<Integer>(sekwencja.size());
		while (Instant.now().isBefore(getExperimentDeadline(start))) {
			result.clear();

			for (var i = 0; i < sekwencja.size(); i++) {
				result.add(throwDie());
			}

			attempts++;
			if (sekwencja.equals(result)) {
				occurences++;
			}
		}

		return (double) occurences / (double) attempts;
	}

	/**
	 * Metoda sprawdza szansę na wyrzucenie określonych liczb oczek w dowolnej
	 * kolejności. Zadanym zbiorem może być np. 1, 2 i 4. Jeśli w kolejnych rzutach
	 * kostką otrzymamy przykładowo:
	 *
	 * <pre>
	 * <b>2 1 4</b>
	 * 3 4 1  &lt;- w tej i kolejnej linijce mamy łącznie 1 2 i 4, ale tu nie zliczamy trafienia
	 * 2 4 5
	 * <b>1 2 4</b>
	 * </pre>
	 * to szansa na wyrzucenie tej sekwencji to: 2/5 czyli 0.4.
	 *
	 * @param oczka liczba oczek jakie mają zostać wyrzucone
	 * @return szansa na wyrzucenie wskazanych liczb oczek
	 */
	@Override
	public double szansaNaWyrzucenieWDowolnejKolejności(Set<Integer> oczka) {
		var occurences = 0;
		var attempts = 0;
		var start = Instant.now();

		var result = new HashSet<Integer>(oczka.size());
		while (Instant.now().isBefore(getExperimentDeadline(start))) {
			result.clear();

			for (var i = 0; i < oczka.size(); i++) {
				result.add(throwDie());
			}

			attempts++;
			if (oczka.equals(result)) {
				occurences++;
			}
		}

		return (double) occurences / (double) attempts;
	}
}

class WeirdDieException extends ArithmeticException { }
class NoDieException extends NullPointerException { }
class NoTimeoutException extends NullPointerException { }
