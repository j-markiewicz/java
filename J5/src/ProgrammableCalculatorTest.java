import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

class StringLineReader implements ProgrammableCalculatorInterface.LineReader {
	List<String> lines;

	StringLineReader(String input) {
		lines = new ArrayList<>(input.lines().toList());
	}

	@Override
	public String readLine() {
		assert !lines.isEmpty();
		return lines.remove(0);
	}
}

class StringLinePrinter implements ProgrammableCalculatorInterface.LinePrinter {
	List<String> lines = new ArrayList<>();

	@Override
	public void printLine(String line) {
		lines.add(line);
	}
}

class ProgrammableCalculatorTest {
	@org.junit.jupiter.api.Test
	void run() {
		String program = """
				10 LET count = 0
				20 PRINT "Hello, World!"
				30 IF count < 10 GOTO 20
				25 LET count = count + 1
				""";

		var calc = new ProgrammableCalculator();
		var stdout = new StringLinePrinter();
		calc.setStdin(new StringLineReader(""));
		calc.setStdout(stdout);
		calc.programCodeReader(new BufferedReader(new StringReader(program)));

		calc.run(10);

		assert stdout.lines.equals(List.of(
				"Hello, World!",
				"Hello, World!",
				"Hello, World!",
				"Hello, World!",
				"Hello, World!",
				"Hello, World!",
				"Hello, World!",
				"Hello, World!",
				"Hello, World!",
				"Hello, World!"
		));
	}

	@org.junit.jupiter.api.Test
	void negative() {
		String program = """
				100000 LET count = -10
				200000 PRINT "Hello, World!"
				300000 LET count = count - -1
				400000 IF count < -5 GOTO 200000
				""";

		var calc = new ProgrammableCalculator();
		var stdout = new StringLinePrinter();
		calc.setStdin(new StringLineReader(""));
		calc.setStdout(stdout);
		calc.programCodeReader(new BufferedReader(new StringReader(program)));

		calc.run(100000);

		assert stdout.lines.equals(List.of(
				"Hello, World!",
				"Hello, World!",
				"Hello, World!",
				"Hello, World!",
				"Hello, World!"
		));
	}

	@org.junit.jupiter.api.Test
	void case_() {
		String program = """
				110 lEt a = 1
				111 prInt a
				112 eNd
				""";

		var calc = new ProgrammableCalculator();
		var stdout = new StringLinePrinter();
		calc.setStdin(new StringLineReader(""));
		calc.setStdout(stdout);
		calc.programCodeReader(new BufferedReader(new StringReader(program)));

		calc.run(110);

		assert stdout.lines.equals(List.of("1"));
	}

	@org.junit.jupiter.api.Test
	void let() {
		String program = """
				190 LET a = 1
				195 LET b = 2
				200 LET a = a + 1
				210 LET a = a * b
				""";

		var calc = new ProgrammableCalculator();
		var stdout = new StringLinePrinter();
		calc.setStdin(new StringLineReader(""));
		calc.setStdout(stdout);
		calc.programCodeReader(new BufferedReader(new StringReader(program)));

		calc.run(190);

		assert stdout.lines.isEmpty();
	}

	@org.junit.jupiter.api.Test
	void print() {
		String program = """
				10 LET a = 100
				11 PRINT "Czesc a = "
				12 PRINT a""";

		var calc = new ProgrammableCalculator();
		var stdout = new StringLinePrinter();
		calc.setStdin(new StringLineReader(""));
		calc.setStdout(stdout);
		calc.programCodeReader(new BufferedReader(new StringReader(program)));

		calc.run(10);

		assert stdout.lines.equals(List.of("Czesc a = ", "100"));
	}

	@org.junit.jupiter.api.Test
	void goto_() {
		String program = """
				5 GOTO 8
				6 PRINT "NIE"
				8 GOTO 30
				10 PRINT "CZESC"
				20 GOTO 5
				30 PRINT "AAAAA"
				""";

		var calc = new ProgrammableCalculator();
		var stdout = new StringLinePrinter();
		calc.setStdin(new StringLineReader(""));
		calc.setStdout(stdout);
		calc.programCodeReader(new BufferedReader(new StringReader(program)));

		calc.run(10);

		assert stdout.lines.equals(List.of("CZESC", "AAAAA"));
	}

	@org.junit.jupiter.api.Test
	void end() {
		String program = """
				10 PRINT "A"
				20 END
				30 PRINT "B"
				""";

		var calc = new ProgrammableCalculator();
		var stdout = new StringLinePrinter();
		calc.setStdin(new StringLineReader(""));
		calc.setStdout(stdout);
		calc.programCodeReader(new BufferedReader(new StringReader(program)));

		calc.run(10);

		assert stdout.lines.equals(List.of("A"));
	}

	@org.junit.jupiter.api.Test
	void if_() {
		String program = """
				10 LET a = 10
				20 LET b = 20
				30 IF a < b GOTO 50
				40 PRINT "a nie jest < od b"
				45 GOTO 60
				50 PRINT "a < od b"
				60 END
				""";

		var calc = new ProgrammableCalculator();
		var stdout = new StringLinePrinter();
		calc.setStdin(new StringLineReader(""));
		calc.setStdout(stdout);
		calc.programCodeReader(new BufferedReader(new StringReader(program)));

		calc.run(10);

		assert stdout.lines.equals(List.of("a < od b"));
	}

	@org.junit.jupiter.api.Test
	void input() {
		String program = """
				10 INPUT a
				20 INPUT b
				30 IF a < b GOTO 50
				40 PRINT "a nie jest < od b"
				45 GOTO 60
				50 PRINT "a < od b"
				60 END
				""";

		var calc = new ProgrammableCalculator();
		var stdout = new StringLinePrinter();
		calc.setStdin(new StringLineReader("2\n2"));
		calc.setStdout(stdout);
		calc.programCodeReader(new BufferedReader(new StringReader(program)));

		calc.run(10);

		assert stdout.lines.equals(List.of("a nie jest < od b"));
	}
}
