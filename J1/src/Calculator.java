/*
# Idea zadania

Zadanie polega na napisaniu kodu programu (klasy), który będzie prostym
kalkulatorem wzbogaconym o pamięć i stos.

# Niepoprawne operacje

Zakładamy, że użytkownik będzie używać kalkulatora zgodnie z przeznaczeniem
poszczególnych metod. Użytkownik nie będzie próbował wykonywać operacji np. z
użyciem ujemnych indeksów dla pamięci, czy przepełniających stos.

# Indeksowanie pamięci

Pamięć kalkulatora indeksowana jest w sposób naturalny (tak jak tablice w
Java). Jeśli pamięć ma N pozycji, to pierwszą poprawną jest 0, ostatnią N-1.

# Stan początkowy

Stan początkowy obiektu kalkulatora to:

- akumulator - ustawiony na 0
- pamięć - wypełniona zerami
- stos - nieużywany

# Konstruktor

Klasę będącą rozwiązaniem można wyposażyć w konstruktor. Ja będę używać jej
(tworzyć jej obiekty) wyłącznie poprzez konstruktor bezparametrowy.

# Wprowadzanie danych

Państwa kod będzie używany poprzez mój program. Nie ma potrzeby wprowadzania
do niego jakiegokolwiek interfejsu użytkownika wczytującego dane np. z
terminala. Użycie będzie opierać się wyłącznie o wywoływanie poszczególnych
metod.

# Elementy statyczne

Należy mieć na względzie, że w trakcie testu utworzonych zostanie wiele
obiektów. Operacje zlecone jednemu z nich nie mogą wpływać na pozostałe.
*/

import java.util.Arrays;

public class Calculator extends CalculatorOperations {
	int accumulator;
	int[] memory;
	SizedStack stack;

	public Calculator() {
		accumulator = 0;
		memory = new int[MEMORY_SIZE];
		Arrays.fill(memory, 0);
		stack = new SizedStack(STACK_SIZE);
	}

	/**
	 * Zwraca wartość zapisaną w akumulatorze
	 *
	 * @return zawartość akumulatora
	 */
	@Override
	public int getAccumulator() {
		return accumulator;
	}

	/**
	 * Zapisuje podaną wartość w akumulatorze.
	 *
	 * @param value wartość do zapisania w akumulatorze
	 */
	@Override
	public void setAccumulator(int value) {
		accumulator = value;
	}

	/**
	 * Zwraca zawartość pamięci na pozycji index.
	 *
	 * @param index pozycja w pamięci
	 * @return wartość znajdująca się pod wskazanym indeksem
	 */
	@Override
	public int getMemory(int index) {
		return memory[index];
	}

	/**
	 * Zapisuje zawartość akumulatora pod pozycją index pamięci
	 *
	 * @param index pozycja w pamięci
	 */
	@Override
	public void accumulatorToMemory(int index) {
		memory[index] = accumulator;
	}

	/**
	 * Do akumulatora dodaje przekazaną wartość
	 *
	 * @param value wartość do dodania do akumulatora
	 */
	@Override
	public void addToAccumulator(int value) {
		accumulator += value;
	}

	/**
	 * Odejmuje przekazaną wartość od akumulatora
	 *
	 * @param value wartość odejmowana od akumulatora
	 */
	@Override
	public void subtractFromAccumulator(int value) {
		accumulator -= value;
	}

	/**
	 * Dodaje zawartość wskazanej pozycji pamięci do akumulatora
	 *
	 * @param index pozycja w pamięci
	 */
	@Override
	public void addMemoryToAccumulator(int index) {
		addToAccumulator(getMemory(index));
	}

	/**
	 * Odejmuje zawartość wskazanej pozycji pamięci od akumulatora
	 *
	 * @param index pozycja w pamięci
	 */
	@Override
	public void subtractMemoryFromAccumulator(int index) {
		subtractFromAccumulator(getMemory(index));
	}

	/**
	 * Przywraca ustawienia początkowe - akumulator ustawiony na 0,
	 * na każdej pozycji pamięci 0, stos pusty.
	 */
	@Override
	public void reset() {
		accumulator = 0;
		Arrays.fill(memory, 0);
		stack.clear();
	}

	/**
	 * Wymienia zawartość wskazanej pozycji pamięci z akumulatorem
	 *
	 * @param index pozycja w pamięci
	 */
	@Override
	public void exchangeMemoryWithAccumulator(int index) {
		var element = getMemory(index);
		accumulatorToMemory(index);
		setAccumulator(element);
	}

	/**
	 * Zapisuje zawartość akumulatora na szczycie stosu. <b>Zawartość akumulatora
	 * nie ulega zmianie</b>.
	 */
	@Override
	public void pushAccumulatorOnStack() {
		stack.push(accumulator);
	}

	/**
	 * Zdejmuje ze szczytu stosu zawartość akumulatora.
	 */
	@Override
	public void pullAccumulatorFromStack() {
		setAccumulator(stack.pop());
	}
}

final class StackFullException extends IndexOutOfBoundsException {
}

final class StackEmptyException extends IndexOutOfBoundsException {
}

class SizedStack {
	final int capacity;
	int[] elements;
	int top;

	public SizedStack(int CAPACITY) {
		capacity = CAPACITY;
		elements = new int[capacity];
		top = 0;
	}

	void push(int element) {
		if (top >= capacity) {
			throw new StackFullException();
		}

		elements[top] = element;
		top++;
	}

	int pop() throws StackEmptyException {
		if (top <= 0) {
			throw new StackEmptyException();
		}

		top--;
		return elements[top];
	}

	void clear() {
		top = 0;
	}
}
