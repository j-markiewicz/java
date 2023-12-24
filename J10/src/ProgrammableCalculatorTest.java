import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
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
	void crlf() {
		String program = "10 LET count = 0\n"
				+ "20 PRINT count\r\n"
				+ "30 IF count < 10 GOTO 20\n"
				+ "25 LET count = count + 1\r\n";

		var calc = new ProgrammableCalculator();
		var stdout = new StringLinePrinter();
		calc.setStdin(new StringLineReader(""));
		calc.setStdout(stdout);
		calc.programCodeReader(new BufferedReader(new StringReader(program)));

		calc.run(10);

		assert stdout.lines.equals(List.of(
				"0",
				"1",
				"2",
				"3",
				"4",
				"5",
				"6",
				"7",
				"8",
				"9"
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
	void let2() {
		String program = """
				1 INPUT a
				22 LET a = a + 1
				333 LET bb = 123 * a
				4444 LET ccc = 7 * 123
				55555 LET dddd = bb - ccc
				666666 PRINT dddd
				""";

		var calc = new ProgrammableCalculator();
		var stdout = new StringLinePrinter();
		calc.setStdin(new StringLineReader("1"));
		calc.setStdout(stdout);
		calc.programCodeReader(new BufferedReader(new StringReader(program)));

		calc.run(1);

		assert stdout.lines.equals(List.of("-615"));
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

	@org.junit.jupiter.api.Test
	void gosub1() {
		String program = """
				1 LET A = 1
				10 GOSUB 50
				15 PRINT A
				20 IF A < 3 GOTO 10
				25 END
				50 PRINT "POCZATEK PODPROGRAMU"
				60 LET A = A + 1
				70 RETURN
				""";

		var calc = new ProgrammableCalculator();
		var stdout = new StringLinePrinter();
		calc.setStdin(new StringLineReader(""));
		calc.setStdout(stdout);
		calc.programCodeReader(new BufferedReader(new StringReader(program)));

		calc.run(1);

		assert stdout.lines.equals(List.of("POCZATEK PODPROGRAMU", "2", "POCZATEK PODPROGRAMU", "3"));
	}

	@org.junit.jupiter.api.Test
	void gosub2() {
		String program = """
				10 GOTO 80
				20 LET A = A + 1
				25 LET B = B + 1
				30 RETURN
				40 PRINT "ZMIENNE"
				45 PRINT A
				50 PRINT B
				55 RETURN
				80 LET A = 100
				85 LET B = 120
				87 GOSUB 40
				90 GOSUB 20
				95 GOSUB 40
				""";

		var calc = new ProgrammableCalculator();
		var stdout = new StringLinePrinter();
		calc.setStdin(new StringLineReader(""));
		calc.setStdout(stdout);
		calc.programCodeReader(new BufferedReader(new StringReader(program)));

		calc.run(10);

		assert stdout.lines.equals(List.of("ZMIENNE", "100", "120", "ZMIENNE", "101", "121"));
	}

	@org.junit.jupiter.api.Test
	void gosub3() {
		String program = """
				20 LET A = A + 1
				25 LET B = B + 1
				30 RETURN
				40 PRINT "ZMIENNE"
				45 PRINT A
				50 PRINT B
				55 RETURN
				80 LET A = 100
				85 LET B = 120
				87 GOSUB 40
				90 GOSUB 20
				95 GOSUB 40
				""";

		var calc = new ProgrammableCalculator();
		var stdout = new StringLinePrinter();
		calc.setStdin(new StringLineReader(""));
		calc.setStdout(stdout);
		calc.programCodeReader(new BufferedReader(new StringReader(program)));

		calc.run(80);

		assert stdout.lines.equals(List.of("ZMIENNE", "100", "120", "ZMIENNE", "101", "121"));
	}

	@org.junit.jupiter.api.Test
	void gosubGoto() {
		String program = """
				20 LET A = A + 1
				21 GOTO 25
				22 GOTO 20
				25 LET B = B + 1
				30 RETURN
				31 END
				40 PRINT "ZMIENNE"
				45 PRINT A
				50 PRINT B
				55 RETURN
				80 LET A = 10
				85 LET B = 120
				87 GOSUB 40
				90 GOSUB 22
				95 GOSUB 40
				""";

		var calc = new ProgrammableCalculator();
		var stdout = new StringLinePrinter();
		calc.setStdin(new StringLineReader(""));
		calc.setStdout(stdout);
		calc.programCodeReader(new BufferedReader(new StringReader(program)));

		calc.run(80);

		assert stdout.lines.equals(List.of("ZMIENNE", "10", "120", "ZMIENNE", "11", "121"));
	}

	@org.junit.jupiter.api.Test
	void nestedGosub() {
		String program = """
				10 LET count = 1
				20 GOSUB 300
				30 GOSUB 200
				40 IF count < 20 GOTO 30
				50 PRINT "END"
				60 END
				100 PRINT count
				110 RETURN
				200 LET count = count * 2
				210 GOSUB 300
				220 GOSUB 100
				230 RETURN
				300 PRINT "count = "
				310 GOSUB 100
				320 RETURN
				""";

		var calc = new ProgrammableCalculator();
		var stdout = new StringLinePrinter();
		calc.setStdin(new StringLineReader(""));
		calc.setStdout(stdout);
		calc.programCodeReader(new BufferedReader(new StringReader(program)));

		calc.run(10);

		assert stdout.lines.equals(List.of(
			"count = ",
			"1",
			"count = ",
			"2",
			"2",
			"count = ",
			"4",
			"4",
			"count = ",
			"8",
			"8",
			"count = ",
			"16",
			"16",
			"count = ",
			"32",
			"32",
			"END"
		));
	}
}
