import java.io.BufferedReader;
import java.util.*;
import java.util.regex.Pattern;

enum Comparison {
	Eq {
		@Override
		boolean eval(Expression left, Expression right, Map<String, Integer> vars) {
			return left.intValue(vars) == right.intValue(vars);
		}
	}, Lt {
		@Override
		boolean eval(Expression left, Expression right, Map<String, Integer> vars) {
			return left.intValue(vars) < right.intValue(vars);
		}
	}, Gt {
		@Override
		boolean eval(Expression left, Expression right, Map<String, Integer> vars) {
			return left.intValue(vars) > right.intValue(vars);
		}
	};

	abstract boolean eval(Expression left, Expression right, Map<String, Integer> vars);
}

interface Instruction {
	static Instruction parse(String line) {
		var instRegex = Pattern.compile("^(?<inst>\\p{Alpha}+)(?<rest>.*?)$");

		try {
			var matcher = instRegex.matcher(line);
			matcher.matches();

			var inst = matcher.group("inst").toUpperCase(Locale.ROOT);
			var rest = matcher.group("rest").trim();

			switch (inst) {
				case "LET": {
					return Let.parse(rest);
				}
				case "PRINT": {
					return Print.parse(rest);
				}
				case "GOTO": {
					return Goto.parse(rest);
				}
				case "END": {
					return End.parse(rest);
				}
				case "IF": {
					return If.parse(rest);
				}
				case "INPUT": {
					return Input.parse(rest);
				}
				default: {
					throw new SyntaxError(line);
				}
			}
		} catch (SyntaxError e) {
			throw new SyntaxError(line, e.getMessage());
		} catch (Exception e) {
			throw new SyntaxError(line);
		}
	}

	int run(
			ProgrammableCalculatorInterface.LineReader stdin,
			ProgrammableCalculatorInterface.LinePrinter stdout,
			Map<String, Integer> vars
	) throws StopRun;

	class Let implements Instruction {
		String name;
		Expression value;

		static Instruction parse(String line) {
			var res = new Let();

			var instRegex = Pattern.compile("^(?<name>\\p{Alpha}+) = (?<expr>.+)$");

			try {
				var matcher = instRegex.matcher(line);
				matcher.matches();

				res.name = matcher.group("name").toLowerCase(Locale.ROOT);
				res.value = Expression.parse(matcher.group("expr"));

				return res;
			} catch (SyntaxError e) {
				throw new SyntaxError(line, e.getMessage());
			} catch (Exception e) {
				throw new SyntaxError(line);
			}
		}

		public int run(
				ProgrammableCalculatorInterface.LineReader stdin,
				ProgrammableCalculatorInterface.LinePrinter stdout,
				Map<String, Integer> vars
		) {
			vars.put(name, value.intValue(vars));
			return 0;
		}
	}

	class Print implements Instruction {
		Expression value;

		static Instruction parse(String line) {
			var res = new Print();

			try {
				res.value = Expression.parse(line);
				return res;
			} catch (SyntaxError e) {
				throw new SyntaxError(line, e.getMessage());
			}
		}

		public int run(
				ProgrammableCalculatorInterface.LineReader stdin,
				ProgrammableCalculatorInterface.LinePrinter stdout,
				Map<String, Integer> vars
		) {
			stdout.printLine(value.stringValue(vars));
			return 0;
		}
	}

	class Goto implements Instruction {
		int destination;

		static Instruction parse(String line) {
			var res = new Goto();

			try {
				res.destination = Integer.parseInt(line);
				return res;
			} catch (NumberFormatException e) {
				throw new SyntaxError(line, e.getMessage());
			}
		}

		public int run(
				ProgrammableCalculatorInterface.LineReader stdin,
				ProgrammableCalculatorInterface.LinePrinter stdout,
				Map<String, Integer> vars
		) {
			return destination;
		}
	}

	class End implements Instruction {
		static Instruction parse(String line) {
			if (!line.isEmpty()) {
				throw new SyntaxError(line);
			}

			return new End();
		}

		public int run(
				ProgrammableCalculatorInterface.LineReader stdin,
				ProgrammableCalculatorInterface.LinePrinter stdout,
				Map<String, Integer> vars
		) throws StopRun {
			throw new StopRun();
		}
	}

	class If implements Instruction {
		Expression left;
		Expression right;
		Comparison cmp;
		int destination;

		static Instruction parse(String line) {
			var res = new If();

			var instRegex = Pattern.compile(
					"^(?<left>\\p{Alpha}+|\\d+) (?<cmp>[=<>]) (?<right>\\p{Alpha}+|\\d+) GOTO (?<dest>\\d+)$");

			try {
				var matcher = instRegex.matcher(line);
				matcher.matches();

				res.left = Expression.parse(matcher.group("left"));
				res.right = Expression.parse(matcher.group("right"));
				res.destination = Integer.parseInt(matcher.group("dest"));

				switch (matcher.group("cmp")) {
					case "=": {
						res.cmp = Comparison.Eq;
						return res;
					}
					case ">": {
						res.cmp = Comparison.Gt;
						return res;
					}
					case "<": {
						res.cmp = Comparison.Lt;
						return res;
					}
					default: {
						throw new SyntaxError(line, "Unknown comparison operator '" + matcher.group("cmp") + "'");
					}
				}
			} catch (SyntaxError e) {
				throw new SyntaxError(line, e.getMessage());
			} catch (Exception e) {
				throw new SyntaxError(line);
			}
		}

		public int run(
				ProgrammableCalculatorInterface.LineReader stdin,
				ProgrammableCalculatorInterface.LinePrinter stdout,
				Map<String, Integer> vars
		) {
			if (cmp.eval(left, right, vars)) {
				return destination;
			} else {
				return 0;
			}
		}
	}

	class Input implements Instruction {
		String name;

		static Instruction parse(String line) {
			var res = new Input();

			var instRegex = Pattern.compile("^(?<name>\\p{Alpha}+)$");

			try {
				var matcher = instRegex.matcher(line);
				matcher.matches();

				res.name = matcher.group("name").toLowerCase(Locale.ROOT);
				return res;
			} catch (Exception e) {
				throw new SyntaxError(line);
			}
		}

		public int run(
				ProgrammableCalculatorInterface.LineReader stdin,
				ProgrammableCalculatorInterface.LinePrinter stdout,
				Map<String, Integer> vars
		) {
			var input = Integer.parseInt(stdin.readLine());
			vars.put(name, input);
			return 0;
		}
	}
}

interface Expression {
	static Expression parse(String expr) {
		if (expr.matches("^\\p{Alpha}+$")) {
			return Variable.parse(expr);
		} else if (expr.matches("^(?<quot>[\"']).*?\\k<quot>$")) {
			return Str.parse(expr);
		} else if (expr.matches("^\\d+$")) {
			return Int.parse(expr);
		} else if (expr.matches("^(\\p{Alpha}+|\\d+) [-+/*] (\\p{Alpha}+|\\d+)$")) {
			return Calculation.parse(expr);
		}

		throw new SyntaxError(expr);
	}

	int intValue(Map<String, Integer> vars);

	String stringValue(Map<String, Integer> vars);

	class Variable implements Expression {
		String name;

		static Variable parse(String expr) {
			var pattern = Pattern.compile("^(?<name>\\p{Alpha}+)$");

			try {
				var matcher = pattern.matcher(expr);
				matcher.matches();

				var res = new Variable();
				res.name = matcher.group("name").toLowerCase(Locale.ROOT);
				return res;
			} catch (Exception e) {
				throw new SyntaxError(expr);
			}
		}

		public int intValue(Map<java.lang.String, Integer> vars) {
			var value = vars.get(name);
			if (value == null) {
				throw new ExpressionValueException("no variable '" + name + "' defined");
			} else {
				return value;
			}
		}

		public String stringValue(Map<java.lang.String, Integer> vars) {
			return Integer.toString(intValue(vars));
		}
	}

	class Str implements Expression {
		String content;

		static Str parse(String expr) {
			var pattern = Pattern.compile("^(?<quot>[\"'])(?<content>.*?)\\k<quot>$");

			try {
				var matcher = pattern.matcher(expr);
				matcher.matches();

				var res = new Str();
				res.content = matcher.group("content");
				return res;
			} catch (Exception e) {
				throw new SyntaxError(expr);
			}
		}

		public int intValue(Map<java.lang.String, Integer> vars) {
			try {
				return Integer.parseInt(content);
			} catch (NumberFormatException e) {
				throw new ExpressionValueException("string '" + content + "' can not be converted to an integer");
			}
		}

		public String stringValue(Map<java.lang.String, Integer> vars) {
			return content;
		}
	}

	class Int implements Expression {
		int value;

		static Int parse(String expr) {
			var pattern = Pattern.compile("^(?<value>\\d+)$");

			try {
				var matcher = pattern.matcher(expr);
				matcher.matches();

				var res = new Int();
				res.value = Integer.parseInt(matcher.group("value"));
				return res;
			} catch (Exception e) {
				throw new SyntaxError(expr);
			}
		}

		public int intValue(Map<java.lang.String, Integer> vars) {
			return value;
		}

		public String stringValue(Map<java.lang.String, Integer> vars) {
			return Integer.toString(intValue(vars));
		}
	}

	class Calculation implements Expression {
		Expression left;
		Operand operand;
		Expression right;

		static Calculation parse(String expr) {
			var pattern = Pattern.compile("^(?<left>\\p{Alpha}+|\\d+) (?<op>[-+/*]) (?<right>\\p{Alpha}+|\\d+)$");

			try {
				var matcher = pattern.matcher(expr);
				matcher.matches();

				var res = new Calculation();
				res.left = Expression.parse(matcher.group("left"));
				res.right = Expression.parse(matcher.group("right"));

				switch (matcher.group("op")) {
					case "+": {
						res.operand = Operand.Add;
						return res;
					}
					case "-": {
						res.operand = Operand.Sub;
						return res;
					}
					case "*": {
						res.operand = Operand.Mul;
						return res;
					}
					case "/": {
						res.operand = Operand.Div;
						return res;
					}
					default: {
						throw new SyntaxError(expr, "Unknown operand '" + matcher.group("op") + "'");
					}
				}
			} catch (SyntaxError e) {
				throw new SyntaxError(expr, e.getMessage());
			} catch (Exception e) {
				throw new SyntaxError(expr);
			}
		}

		public int intValue(Map<String, Integer> vars) {
			return operand.calc(left, right, vars);
		}

		public String stringValue(Map<String, Integer> vars) {
			return Integer.toString(intValue(vars));
		}

		enum Operand {
			Add {
				@Override
				int calc(Expression left, Expression right, Map<String, Integer> vars) {
					return left.intValue(vars) + right.intValue(vars);
				}
			}, Sub {
				@Override
				int calc(Expression left, Expression right, Map<String, Integer> vars) {
					return left.intValue(vars) - right.intValue(vars);
				}
			}, Mul {
				@Override
				int calc(Expression left, Expression right, Map<String, Integer> vars) {
					return left.intValue(vars) * right.intValue(vars);
				}
			}, Div {
				@Override
				int calc(Expression left, Expression right, Map<String, Integer> vars) {
					return left.intValue(vars) / right.intValue(vars);
				}
			};

			abstract int calc(Expression left, Expression right, Map<String, Integer> vars);
		}
	}
}

public class ProgrammableCalculator implements ProgrammableCalculatorInterface {
	LineReader stdin;
	LinePrinter stdout;
	int pc = 1;
	SortedMap<Integer, Instruction> program = new TreeMap<>();
	Map<String, Integer> vars = new HashMap<>();

	/**
	 * Metoda ustawia BufferedReader, który pozwala na odczyt kodu źródłowego
	 * programu.
	 *
	 * @param reader obiekt BufferedReader.
	 */
	@Override
	public void programCodeReader(BufferedReader reader) {
		var scanner = new Scanner(reader).useDelimiter("(\r?\n)+");
		var lineRegex = Pattern.compile("(?<index>\\d+) (?<inst>.+)");

		while (scanner.hasNext(lineRegex)) {
			var line = lineRegex.matcher(scanner.next(lineRegex));
			assert line.matches();

			var index = Integer.valueOf(line.group("index"));
			var instruction = Instruction.parse(line.group("inst"));

			program.put(index, instruction);
		}

		if (scanner.hasNext(".+")) {
			throw new SyntaxError(scanner.next(".+"));
		}
	}

	/**
	 * Przekierowanie standardowego wejścia
	 *
	 * @param input nowe standardowe wejście
	 */
	@Override
	public void setStdin(LineReader input) {
		stdin = input;
	}

	/**
	 * Przekierowanie standardowego wyjścia
	 *
	 * @param output nowe standardowe wyjście
	 */
	@Override
	public void setStdout(LinePrinter output) {
		stdout = output;
	}

	/**
	 * Rozpoczęcie realizacji kodu programu od wskazanego numeru linii.
	 *
	 * @param line numer linii programu
	 */
	@Override
	public void run(int line) {
		pc = line;

		if (!program.containsKey(pc)) {
			throw new RuntimeException("Start line " + pc + " is not in the program");
		}

		while (pc <= program.lastKey()) {
			var instruction = program.get(pc);

			try {
				var next = instruction.run(stdin, stdout, vars);

				if (next == 0) {
					do {
						pc++;
					} while (!program.containsKey(pc) && pc <= program.lastKey());
				} else {
					if (!program.containsKey(next)) {
						throw new GotoError(pc, next);
					}

					pc = next;
				}
			} catch (StopRun stop) {
				return;
			}
		}
	}
}

class StopRun extends Exception {
}

class ExpressionValueException extends RuntimeException {
	ExpressionValueException(String msg) {
		super("Invalid BASIC expression: " + msg);
	}
}

class SyntaxError extends RuntimeException {
	SyntaxError(String line) {
		super("BASIC syntax error in '" + line + "'");
	}

	SyntaxError(String line, String context) {
		super("BASIC syntax error in '" + line + "'\nCaused by: " + context);
	}
}

class GotoError extends RuntimeException {
	GotoError(int line, int destination) {
		super("GOTO error in line " + line + ": no such destination " + destination);
	}
}
