/**
 * Klasa bazowa dla kalkulatora.
 * Kalkulator wykonuje proste operacje na liczbach całkowitych.
 * Wyposażony jest w pamięć o rozmiarze MEMORY_SIZE pozycji
 * oraz stos o rozmiarze STACK_SIZE. Początkowy stan
 * akumulatora i pamięci to zera.
 */
abstract public class CalculatorOperations {
	/**
	 * Rozmiar pamięci
	 */
	public static final int MEMORY_SIZE = 16;

	/**
	 * Głębokość stosu
	 */
	public static final int STACK_SIZE = 4;

	/**
	 * Zapisuje podaną wartość w akumulatorze.
	 *
	 * @param value wartość do zapisania w akumulatorze
	 */
	abstract public void setAccumulator(int value);

	/**
	 * Zwraca wartość zapisaną w akumulatorze
	 *
	 * @return zawartość akumulatora
	 */
	abstract public int getAccumulator();

	/**
	 * Zwraca zawartość pamięci na pozycji index.
	 *
	 * @param index pozycja w pamięci
	 * @return wartość znajdująca się pod wskazanym indeksem
	 */
	abstract public int getMemory(int index);

	/**
	 * Zapisuje zawartość akumulatora pod pozycją index pamięci
	 *
	 * @param index pozycja w pamięci
	 */
	abstract public void accumulatorToMemory(int index);

	/**
	 * Do akumulatora dodaje przekazaną wartość
	 *
	 * @param value wartość do dodania do akumulatora
	 */
	abstract public void addToAccumulator(int value);

	/**
	 * Odejmuje przekazaną wartość od akumulatora
	 *
	 * @param value wartość odejmowana od akumulatora
	 */
	abstract public void subtractFromAccumulator(int value);

	/**
	 * Dodaje zawartość wskazanej pozycji pamięci do akumulatora
	 *
	 * @param index pozycja w pamięci
	 */
	abstract public void addMemoryToAccumulator(int index);

	/**
	 * Odejmuje zawartość wskazanej pozycji pamięci od akumulatora
	 *
	 * @param index pozycja w pamięci
	 */
	abstract public void subtractMemoryFromAccumulator(int index);

	/**
	 * Przywraca ustawienia początkowe - akumulator ustawiony na 0,
	 * na każdej pozycji pamięci 0, stos pusty.
	 */
	abstract public void reset();

	/**
	 * Wymienia zawartość wskazanej pozycji pamięci z akumulatorem
	 *
	 * @param index pozycja w pamięci
	 */
	abstract public void exchangeMemoryWithAccumulator(int index);

	/**
	 * Zapisuje zawartość akumulatora na szczycie stosu. <b>Zawartość akumulatora
	 * nie ulega zmianie</b>.
	 */
	abstract public void pushAccumulatorOnStack();

	/**
	 * Zdejmuje ze szczytu stosu zawartość akumulatora.
	 */
	abstract public void pullAccumulatorFromStack();
}
