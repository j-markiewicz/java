import java.io.BufferedReader;
import java.util.*;
import java.util.regex.Pattern;

public class ProgrammableCalculator implements ProgrammableCalculatorInterface {
	static final boolean ASSERTIONS;
	private final ExecutionContext ctx = new ExecutionContext();
	private final ArrayList<MaybeUnparsedInstruction> program = new ArrayList<>(128);
	private final TreeMap<Integer, Integer> lineMappings = new TreeMap<>();
	private final Stack<Integer> stack = new Stack<>();

	static {
		boolean assertions = false;
		assert assertions = true;
		ASSERTIONS = assertions;
	}

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

		SortedMap<Integer, MaybeUnparsedInstruction> syncProgram = Collections.synchronizedSortedMap(new TreeMap<>());
		scanner.tokens().parallel().forEach((line) -> {
			var matcher = lineRegex.matcher(line);
			var matchRes = matcher.matches();
			assert matchRes;

			var index = Integer.valueOf(matcher.group("index"));
			var instruction = new MaybeUnparsedInstruction(matcher.group("inst"));

			if (ASSERTIONS && syncProgram.containsKey(index)) {
				throw new SyntaxError(index + " ...", "Duplicated line number");
			}

			syncProgram.put(index, instruction);
		});

		for (var entry: syncProgram.entrySet()) {
			lineMappings.put(entry.getKey(), program.size());
			program.add(entry.getValue());
		}

		new Thread(() -> program.parallelStream().forEach(inst -> {
			try {
				inst.parse(lineMappings);
			} catch (Exception ignored) {
				// Ignore all errors when pre-parsing
			}
		})).start();
	}

	/**
	 * Przekierowanie standardowego wejścia
	 *
	 * @param input nowe standardowe wejście
	 */
	@Override
	public void setStdin(LineReader input) {
		ctx.stdin = input;
	}

	/**
	 * Przekierowanie standardowego wyjścia
	 *
	 * @param output nowe standardowe wyjście
	 */
	@Override
	public void setStdout(LinePrinter output) {
		ctx.stdout = output;
	}

	/**
	 * Rozpoczęcie realizacji kodu programu od wskazanego numeru linii.
	 *
	 * @param line numer linii programu
	 */
	@Override
	public void run(int line) {
		int pc = lineMappings.get(line);

		while (pc < program.size()) {
			var upInstruction = program.get(pc);

			if (upInstruction == null) {
				return;
			}

			var instruction = upInstruction.get(lineMappings);

			try {
				var next = instruction.run(ctx);

				switch (next) {
					case Integer.MAX_VALUE -> {
						pc += 1;
					}
					case Integer.MIN_VALUE -> {
						try {
							pc = stack.pop();
						} catch (EmptyStackException e) {
							throw new ReturnError(pc);
						}
					}
					default -> {
						if (next >= 0) {
							pc = next;
						} else {
							stack.push(pc + 1);
							pc = -next - 1;
						}
					}
				}
			} catch (StopRun stop) {
				return;
			}
		}
	}
}

class MaybeUnparsedInstruction {
	String source;
	Instruction parsed;

	MaybeUnparsedInstruction(String source) {
		this.source = source;
		parsed = null;
	}

	Instruction get(NavigableMap<Integer, Integer> lineMappings) {
		if (parsed == null) {
			parse(lineMappings);
		}

		return parsed;
	}

	synchronized void parse(NavigableMap<Integer, Integer> lineMappings) {
		if (parsed == null) {
			parsed = Instruction.parse(source, lineMappings);
		}
	}
}

class ExecutionContext {
	ProgrammableCalculatorInterface.LineReader stdin;
	ProgrammableCalculatorInterface.LinePrinter stdout;
	HashMap<String, Integer> vars = new HashMap<>(32);

	int getVar(String name) {
		var value = vars.get(name);

		if (value == null) {
			throw new ExpressionValueException("no variable '" + name + "' defined");
		}

		return value;
	}

	void setVar(String name, int value) {
		vars.put(name, value);
	}

	String read() {
		return stdin.readLine();
	}

	void print(String line) {
		stdout.printLine(line);
	}
}

class PrecompiledRegexes {
	final static Pattern NEWLINE = Pattern.compile("(\r?\n)+");
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
		boolean eval(Expression left, Expression right, ExecutionContext ctx) {
			return left.intValue(ctx) == right.intValue(ctx);
		}
	}, Lt {
		@Override
		boolean eval(Expression left, Expression right, ExecutionContext ctx) {
			return left.intValue(ctx) < right.intValue(ctx);
		}
	}, Gt {
		@Override
		boolean eval(Expression left, Expression right, ExecutionContext ctx) {
			return left.intValue(ctx) > right.intValue(ctx);
		}
	};

	abstract boolean eval(Expression left, Expression right, ExecutionContext ctx);
}

interface Instruction {
	static Instruction parse(String line, NavigableMap<Integer, Integer> lineMappings) {
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
				case "GOTO" -> Goto.parse(rest, lineMappings);
				case "END" -> End.parse(rest);
				case "IF" -> If.parse(rest, lineMappings);
				case "INPUT" -> Input.parse(rest);
				case "GOSUB" -> Gosub.parse(rest, lineMappings);
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
	 * @param ctx The execution context
	 * @return An int with the following meanings:<br> <table>
	 * <tr> <td>value</td> <td>meaning</td> </tr>
	 * <tr> <td>MIN</td> <td>Return from a subroutine</td> </tr>
	 * <tr> <td>MAX</td> <td>Continue on the next line</td> </tr>
	 * <tr> <td>>= 0, < MAX</td> <td>Go to the returned line</td> </tr>
	 * <tr> <td>> MIN, < 0</td> <td>Go to a subroutine at line -return - 1</td> </tr>
	 * </table>
	 * @throws StopRun To stop the execution of the program
	 */
	int run(ExecutionContext ctx) throws StopRun;

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

		public int run(ExecutionContext ctx) {
			ctx.setVar(name, value.intValue(ctx));
			return Integer.MAX_VALUE;
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

		public int run(ExecutionContext ctx) {
			ctx.print(value.stringValue(ctx));
			return Integer.MAX_VALUE;
		}
	}

	class Goto implements Instruction {
		int destination;

		static Instruction parse(String line, NavigableMap<Integer, Integer> lineMappings) {
			var res = new Goto();

			try {
				var dest = Integer.parseInt(line);
				var mappedDest = lineMappings.get(dest);

				if (mappedDest == null) {
					throw new GotoError(dest);
				}

				res.destination = mappedDest;
				return res;
			} catch (NumberFormatException e) {
				throw new SyntaxError(line, e.getMessage());
			}
		}

		public int run(ExecutionContext ctx) {
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

		public int run(ExecutionContext ctx) throws StopRun {
			throw new StopRun();
		}
	}

	class If implements Instruction {
		Expression left;
		Expression right;
		Comparison cmp;
		int destination;

		static Instruction parse(String line, Map<Integer, Integer> lineMappings) {
			var res = new If();

			var instRegex = PrecompiledRegexes.IF;

			try {
				var matcher = instRegex.matcher(line);
				var matchRes = matcher.matches();
				assert matchRes;

				res.left = Expression.parse(matcher.group("left"));
				res.right = Expression.parse(matcher.group("right"));

				var dest = Integer.parseInt(matcher.group("dest"));
				var mappedDest = lineMappings.get(dest);

				if (mappedDest == null) {
					throw new SyntaxError(line, "No such destination: " + dest);
				}

				res.destination = mappedDest;

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

		public int run(ExecutionContext ctx) {
			if (cmp.eval(left, right, ctx)) {
				return destination;
			} else {
				return Integer.MAX_VALUE;
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

		public int run(ExecutionContext ctx) {
			var input = Integer.parseInt(ctx.read());
			ctx.setVar(name, input);
			return Integer.MAX_VALUE;
		}
	}

	class Gosub implements Instruction {
		int destination;

		static Instruction parse(String line, Map<Integer, Integer> lineMappings) {
			var res = new Gosub();

			try {
				var dest = Integer.parseInt(line);
				var mappedDest = lineMappings.get(dest);

				if (mappedDest == null) {
					throw new GotoError(dest);
				}

				res.destination = -mappedDest - 1;
				return res;
			} catch (NumberFormatException e) {
				throw new SyntaxError(line, e.getMessage());
			}
		}

		public int run(ExecutionContext ctx) {
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

		public int run(ExecutionContext ctx) {
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

	int intValue(ExecutionContext ctx);

	String stringValue(ExecutionContext ctx);

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

		public int intValue(ExecutionContext ctx) {
			return ctx.getVar(name);
		}

		public String stringValue(ExecutionContext ctx) {
			return Integer.toString(intValue(ctx));
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

		public int intValue(ExecutionContext ctx) {
			try {
				return Integer.parseInt(content);
			} catch (NumberFormatException e) {
				throw new ExpressionValueException("string '" + content + "' can not be converted to an integer");
			}
		}

		public String stringValue(ExecutionContext ctx) {
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

		public int intValue(ExecutionContext ctx) {
			return value;
		}

		public String stringValue(ExecutionContext ctx) {
			return Integer.toString(intValue(ctx));
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

		public int intValue(ExecutionContext ctx) {
			return operand.calc(left, right, ctx);
		}

		public String stringValue(ExecutionContext ctx) {
			return Integer.toString(intValue(ctx));
		}

		enum Operand {
			Add {
				@Override
				int calc(Expression left, Expression right, ExecutionContext ctx) {
					return left.intValue(ctx) + right.intValue(ctx);
				}
			}, Sub {
				@Override
				int calc(Expression left, Expression right, ExecutionContext ctx) {
					return left.intValue(ctx) - right.intValue(ctx);
				}
			}, Mul {
				@Override
				int calc(Expression left, Expression right, ExecutionContext ctx) {
					return left.intValue(ctx) * right.intValue(ctx);
				}
			}, Div {
				@Override
				int calc(Expression left, Expression right, ExecutionContext ctx) {
					return left.intValue(ctx) / right.intValue(ctx);
				}
			};

			abstract int calc(Expression left, Expression right, ExecutionContext ctx);
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
	GotoError(int destination) {
		super("GOTO error: no such destination " + destination);
	}
}

class GosubError extends RuntimeException {
	GosubError(int destination) {
		super("GOSUB error: no such destination " + destination);
	}
}

class ReturnError extends RuntimeException {
	ReturnError(int line) {
		super("RETURN error in line " + line + ": the stack is empty");
	}
}
