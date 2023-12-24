import java.io.BufferedReader;
import java.util.*;
import java.util.regex.Pattern;

public class ProgrammableCalculator implements ProgrammableCalculatorInterface {
	LineReader stdin;
	LinePrinter stdout;
	int pc;
	NavigableMap<Integer, Instruction> program = new TreeMap<>();
	Map<String, Integer> vars = new HashMap<>();
	Stack<Integer> stack = new Stack<>();

	/**
	 * Metoda ustawia BufferedReader, który pozwala na odczyt kodu źródłowego
	 * programu.
	 *
	 * @param reader obiekt BufferedReader.
	 */
	@Override
	public void programCodeReader(BufferedReader reader) {
		var scanner = new Scanner(reader).useDelimiter(PrecompiledRegexes.NEWLINE);
		var lineRegex = PrecompiledRegexes.LINE;

		var syncProgram = Collections.synchronizedSortedMap(program);
		scanner.tokens().parallel().forEach((line) -> {
			var matcher = lineRegex.matcher(line);
			var matchRes = matcher.matches();
			assert matchRes;

			var index = Integer.valueOf(matcher.group("index"));
			var instruction = Instruction.parse(matcher.group("inst"));

			assert !syncProgram.containsKey(index);
			syncProgram.put(index, instruction);
		});
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

		assert !program.containsKey(0);
		assert program.containsKey(pc);
		assert pc > 0;

		while (true) {
			var instruction = program.get(pc);

			if (instruction == null) {
				return;
			}

			try {
				var next = instruction.run(stdin, stdout, vars);

				switch (next) {
					case 0 -> {
						pc = Objects.requireNonNullElse(program.higherKey(pc), 0);
					}
					case Integer.MIN_VALUE -> {
						try {
							pc = stack.pop();
						} catch (EmptyStackException e) {
							throw new ReturnError(pc);
						}
					}
					default -> {
						if (next > 0) {
							if (!program.containsKey(next)) {
								throw new GotoError(pc, next);
							}

							pc = next;
						} else {
							if (!program.containsKey(-next)) {
								throw new GosubError(pc, -next);
							}

							stack.push(Objects.requireNonNullElse(program.higherKey(pc), 0));
							pc = -next;
						}
					}
				}
			} catch (StopRun stop) {
				pc = 0;
			}
		}
	}
}

class PrecompiledRegexes {
	final static Pattern NEWLINE = Pattern.compile("(\r?\n)+");
	final static Pattern ANY = Pattern.compile(".+");
	final static Pattern LINE = Pattern.compile("(?<index>\\d+) (?<inst>.+)");
	final static Pattern INSTRUCTION = Pattern.compile("^(?<inst>\\p{Alpha}+)(?<rest>.*?)$");
	final static Pattern LET = Pattern.compile("^(?<name>\\p{Alpha}+) = (?<expr>.+)$");
	final static Pattern IF = Pattern.compile("^(?<left>\\p{Alpha}+|-?\\d+) (?<cmp>[=<>]) (?<right>\\p{Alpha}+|-?\\d+) GOTO (?<dest>\\d+)$");
	final static Pattern INPUT = Pattern.compile("^(?<name>\\p{Alpha}+)$");
	final static Pattern VARIABLE = Pattern.compile("^(?<name>\\p{Alpha}+)$");
	final static Pattern STR = Pattern.compile("^(?<quot>[\"'])(?<content>.*?)\\k<quot>$");
	final static Pattern INT = Pattern.compile("^(?<value>-?\\d+)$");
	final static Pattern CALCULATION = Pattern.compile("^(?<left>\\p{Alpha}+|-?\\d+) (?<op>[-+/*]) (?<right>\\p{Alpha}+|-?\\d+)$");
}

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
		var instRegex = PrecompiledRegexes.INSTRUCTION;

		try {
			var matcher = instRegex.matcher(line);
			var matchRes = matcher.matches();
			assert matchRes;

			var inst = matcher.group("inst").toUpperCase(Locale.ROOT);
			var rest = matcher.group("rest").trim();

			return switch (inst) {
				case "LET" -> Let.parse(rest);
				case "PRINT" -> Print.parse(rest);
				case "GOTO" -> Goto.parse(rest);
				case "END" -> End.parse(rest);
				case "IF" -> If.parse(rest);
				case "INPUT" -> Input.parse(rest);
				case "GOSUB" -> Gosub.parse(rest);
				case "RETURN" -> Return.parse(rest);
				default -> throw new SyntaxError(line);
			};
		} catch (SyntaxError e) {
			throw new SyntaxError(line, e.getMessage());
		} catch (Exception e) {
			throw new SyntaxError(line);
		}
	}

	/**
	 * Run the instruction
	 *
	 * @param stdin The program's standard input
	 * @param stdout The program's standard output
	 * @param vars A map of all variables
	 * @return An int with the following meanings:<br> <table>
	 * <tr> <td>value</td> <td>meaning</td> </tr>
	 * <tr> <td>MIN</td> <td>Return from a subroutine</td> </tr>
	 * <tr> <td>>MIN, <0</td> <td>Go to a subroutine at line -return</td> </tr>
	 * <tr> <td>0</td> <td>Continue on the next line</td> </tr>
	 * <tr> <td>>0</td> <td>Go to the returned line</td> </tr>
	 * </table>
	 * @throws StopRun To stop the execution of the program
	 */
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

			var instRegex = PrecompiledRegexes.LET;

			try {
				var matcher = instRegex.matcher(line);
				var matchRes = matcher.matches();
				assert matchRes;

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

			var instRegex = PrecompiledRegexes.IF;

			try {
				var matcher = instRegex.matcher(line);
				var matchRes = matcher.matches();
				assert matchRes;

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

			var instRegex = PrecompiledRegexes.INPUT;

			try {
				var matcher = instRegex.matcher(line);
				var matchRes = matcher.matches();
				assert matchRes;

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

	class Gosub implements Instruction {
		int destination;

		static Instruction parse(String line) {
			var res = new Gosub();

			try {
				res.destination = -Integer.parseInt(line);
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

	class Return implements Instruction {
		static Instruction parse(String line) {
			if (!line.isEmpty()) {
				throw new SyntaxError(line);
			}

			return new Return();
		}

		public int run(
				ProgrammableCalculatorInterface.LineReader stdin,
				ProgrammableCalculatorInterface.LinePrinter stdout,
				Map<String, Integer> vars
		) {
			return Integer.MIN_VALUE;
		}
	}
}

interface Expression {
	static Expression parse(String expr) {
		if (expr.matches("^\\p{Alpha}+$")) {
			return Variable.parse(expr);
		} else if (expr.matches("^(?<quot>[\"']).*?\\k<quot>$")) {
			return Str.parse(expr);
		} else if (expr.matches("^-?\\d+$")) {
			return Int.parse(expr);
		} else if (expr.matches("^(\\p{Alpha}+|-?\\d+) [-+/*] (\\p{Alpha}+|-?\\d+)$")) {
			return Calculation.parse(expr);
		}

		throw new SyntaxError(expr);
	}

	int intValue(Map<String, Integer> vars);

	String stringValue(Map<String, Integer> vars);

	class Variable implements Expression {
		String name;

		static Variable parse(String expr) {
			var pattern = PrecompiledRegexes.VARIABLE;

			try {
				var matcher = pattern.matcher(expr);
				var matchRes = matcher.matches();
				assert matchRes;

				var res = new Variable();
				res.name = matcher.group("name").toLowerCase(Locale.ROOT);
				return res;
			} catch (Exception e) {
				throw new SyntaxError(expr);
			}
		}

		public int intValue(Map<String, Integer> vars) {
			var value = vars.get(name);
			if (value == null) {
				throw new ExpressionValueException("no variable '" + name + "' defined");
			}

			return value;
		}

		public String stringValue(Map<String, Integer> vars) {
			return Integer.toString(intValue(vars));
		}
	}

	class Str implements Expression {
		String content;

		static Str parse(String expr) {
			var pattern = PrecompiledRegexes.STR;

			try {
				var matcher = pattern.matcher(expr);
				var matchRes = matcher.matches();
				assert matchRes;

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
			var pattern = PrecompiledRegexes.INT;

			try {
				var matcher = pattern.matcher(expr);
				var matchRes = matcher.matches();
				assert matchRes;

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
			var pattern = PrecompiledRegexes.CALCULATION;

			try {
				var matcher = pattern.matcher(expr);
				var matchRes = matcher.matches();
				assert matchRes;

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

class GosubError extends RuntimeException {
	GosubError(int line, int destination) {
		super("GOSUB error in line " + line + ": no such destination " + destination);
	}
}

class ReturnError extends RuntimeException {
	ReturnError(int line) {
		super("RETURN error in line " + line + ": the stack is empty");
	}
}
