class CalculatorTest {
	@org.junit.jupiter.api.Test
	void getAccumulator() {
		var calc = new Calculator();
		assert calc.getAccumulator() == 0;
		calc.setAccumulator(5);
		assert calc.getAccumulator() == 5;
	}

	@org.junit.jupiter.api.Test
	void setAccumulator() {
		var calc = new Calculator();
		calc.setAccumulator(5);
		assert calc.getAccumulator() == 5;
		calc.setAccumulator(53);
		assert calc.getAccumulator() == 53;
	}

	@org.junit.jupiter.api.Test
	void getMemory() {
		var calc = new Calculator();
		assert calc.getMemory(2) == 0;
		calc.setAccumulator(10);
		calc.accumulatorToMemory(2);
		assert calc.getMemory(2) == 10;
	}

	@org.junit.jupiter.api.Test
	void accumulatorToMemory() {
		var calc = new Calculator();
		assert calc.getMemory(2) == 0;
		assert calc.getMemory(3) == 0;
		calc.setAccumulator(10);
		calc.accumulatorToMemory(2);
		assert calc.getMemory(2) == 10;
		assert calc.getMemory(3) == 0;
		assert calc.getAccumulator() == 10;
	}

	@org.junit.jupiter.api.Test
	void addToAccumulator() {
		var calc = new Calculator();
		calc.setAccumulator(10);
		assert calc.getAccumulator() == 10;
		calc.addToAccumulator(5);
		assert calc.getAccumulator() == 15;
		calc.addToAccumulator(-2);
		assert calc.getAccumulator() == 13;
	}

	@org.junit.jupiter.api.Test
	void subtractFromAccumulator() {
		var calc = new Calculator();
		calc.setAccumulator(10);
		assert calc.getAccumulator() == 10;
		calc.subtractFromAccumulator(5);
		assert calc.getAccumulator() == 5;
		calc.subtractFromAccumulator(-2);
		assert calc.getAccumulator() == 7;
	}

	@org.junit.jupiter.api.Test
	void addMemoryToAccumulator() {
		var calc = new Calculator();
		calc.setAccumulator(10);
		calc.accumulatorToMemory(5);
		calc.setAccumulator(4);
		assert calc.getAccumulator() == 4;
		calc.addMemoryToAccumulator(3);
		assert calc.getAccumulator() == 4;
		calc.addMemoryToAccumulator(5);
		assert calc.getAccumulator() == 14;
	}

	@org.junit.jupiter.api.Test
	void subtractMemoryFromAccumulator() {
		var calc = new Calculator();
		calc.setAccumulator(10);
		calc.accumulatorToMemory(5);
		calc.setAccumulator(4);
		assert calc.getAccumulator() == 4;
		calc.subtractMemoryFromAccumulator(3);
		assert calc.getAccumulator() == 4;
		calc.subtractMemoryFromAccumulator(5);
		assert calc.getAccumulator() == -6;
	}

	@org.junit.jupiter.api.Test
	void reset() {
		var calc = new Calculator();
		calc.setAccumulator(10);
		calc.accumulatorToMemory(1);
		calc.setAccumulator(20);
		assert calc.getAccumulator() == 20;
		calc.reset();
		assert calc.getAccumulator() == 0;
	}

	@org.junit.jupiter.api.Test
	void exchangeMemoryWithAccumulator() {
		var calc = new Calculator();
		calc.setAccumulator(10);
		calc.accumulatorToMemory(0);
		calc.setAccumulator(20);
		assert calc.getAccumulator() == 20;
		calc.exchangeMemoryWithAccumulator(0);
		assert calc.getAccumulator() == 10;
		calc.exchangeMemoryWithAccumulator(1);
		assert calc.getAccumulator() == 0;
	}

	@org.junit.jupiter.api.Test
	void pushAccumulatorOnStack() {
		var calc = new Calculator();
		calc.setAccumulator(10);
		calc.pushAccumulatorOnStack();
		assert calc.getAccumulator() == 10;
		calc.setAccumulator(20);
		calc.pushAccumulatorOnStack();
		assert calc.getAccumulator() == 20;
		calc.setAccumulator(30);
		calc.pushAccumulatorOnStack();
		assert calc.getAccumulator() == 30;
		calc.pullAccumulatorFromStack();
		calc.pullAccumulatorFromStack();
		assert calc.getAccumulator() == 20;
		calc.pushAccumulatorOnStack();
		calc.pushAccumulatorOnStack();
		calc.pushAccumulatorOnStack();
		try {
			calc.pushAccumulatorOnStack();
			assert false;
		} catch (StackFullException e) {
			// success
		}
	}

	@org.junit.jupiter.api.Test
	void pullAccumulatorFromStack() {
		var calc = new Calculator();
		calc.setAccumulator(10);
		calc.pushAccumulatorOnStack();
		calc.setAccumulator(20);
		calc.pushAccumulatorOnStack();
		calc.setAccumulator(30);
		calc.pushAccumulatorOnStack();
		calc.pullAccumulatorFromStack();
		assert calc.getAccumulator() == 30;
		calc.pullAccumulatorFromStack();
		assert calc.getAccumulator() == 20;
		calc.pullAccumulatorFromStack();
		assert calc.getAccumulator() == 10;
		try {
			calc.pullAccumulatorFromStack();
			assert false;
		} catch (StackEmptyException e) {
			// success
		}
	}
}
