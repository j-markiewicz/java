import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Interfejs eksperymentatora, który będzie przeprowadzać doświadczenia z
 * zakresu prawdopodobieństwa. W eksperymentach używana będzie kostka. Kostka do
 * gry może działać w sposób niestandardowy i szanse na wyrzucenie określonej
 * liczby oczek niekoniecznie muszą wynosić po 1/6.
 */
public interface Eksperymentator {

	/**
	 * Eksperymentatorowi przekazujemy kostkę do gry. Wszystkie eksperymenty należy
	 * przeprowadzić z zastosowaniem powierzonej tu kostki. Kostki nie wolno używać
	 * do innych celów niż wykonanie eksperymentów (wszystkie rzuty kostką muszą
	 * zostać uwzględnione w wyliczonych prawdopodobieństwach).
	 * 
	 * @param kostka kostka do gry
	 */
	public void użyjKostki(KostkaDoGry kostka);

	/**
	 * Ustalenie całkowitego czasu trwania eksperymentu w milisekundach.
	 * Prawdopodobieństwa mają być szacowane przez eksperymentatora jako iloraz
	 * liczby prób zakończonych sukcesem do liczby wszystkich prób. Na wykonanie
	 * wszystkich prób eksperymentator ma czasEksperymentu. W okresie
	 * czasEksperymentu należy wykonać możliwie dużo prób.
	 * 
	 * @param czasEksperymentu całkowity czas na wykonanie eksperymentu
	 */
	public void czasJednegoEksperymentu(long czasEksperymentu);

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
	 *         szansa na wystąpienie tej sumy oczek.
	 */
	public Map<Integer, Double> szansaNaWyrzucenieOczek(int liczbaKostek);

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
	public double szansaNaWyrzucenieKolejno(List<Integer> sekwencja);

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
	public double szansaNaWyrzucenieWDowolnejKolejności(Set<Integer> oczka);
}
