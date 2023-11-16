import java.io.BufferedReader;

/**
 * Interfejs programowalnego kalkulatora
 */
public interface ProgrammableCalculatorInterface {

	/**
	 * Interfejs standardowego wejścia.
	 */
	public interface LineReader {
		/**
		 * Metoda zwraca pojedynczą linię odczytaną ze standardowego wejścia. Na końcu
		 * linii <b>nie ma</b> znaku przejścia do nowej linii.
		 * 
		 * @return ciąg znaków odczytany ze standardowego wejścia
		 */
		public String readLine();
	}

	/**
	 * Interfejs standardowego wyjścia.
	 */
	public interface LinePrinter {
		/**
		 * Metoda wyświetla na terminalu przekazany ciąg znaków. <b>Przejście do nowej
		 * linii następuje automatycznie</b>. Znaku nowej linii nie należy umieszczać w
		 * przekazywanym ciągu.
		 * 
		 * @param line ciąg znaków do przekazania na terminal
		 */
		public void printLine(String line);
	}

	/**
	 * Metoda ustawia BufferedReader, który pozwala na odczyt kodu źródłowego
	 * programu.
	 * 
	 * @param reader obiekt BufferedReader.
	 */
	public void programCodeReader(BufferedReader reader);

	/**
	 * Przekierowanie standardowego wejścia
	 * 
	 * @param input nowe standardowe wejście
	 */
	public void setStdin(LineReader input);

	/**
	 * Przekierowanie standardowego wyjścia
	 * 
	 * @param output nowe standardowe wyjście
	 */
	public void setStdout(LinePrinter output);

	/**
	 * Rozpoczęcie realizacji kodu programu od wskazanego numeru linii.
	 * 
	 * @param line numer linii programu
	 */
	public void run(int line);
}
