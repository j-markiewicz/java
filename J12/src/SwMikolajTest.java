import java.util.List;
import java.util.Map;

class ShouldBeCounted {
	public int bombki = 3;

	public ShouldBeCounted() { }
}

class NotInt {
	public Integer bombki = 300;
	public short cukierki = 400;
	public long lancuchy = Long.MAX_VALUE;

	public NotInt() { }
}

class AlmostAll {
	public int bombki = 1;
	public int lancuchy = 0;
	public int cukierki = 3;
	public int prezenty = 4;
	public int szpice = 5;
}

class NonPublic {
	protected int cukierki = 3;
	protected int lampki = 6;
	int bombki = 1;
	int prezenty = 4;
	private int lancuchy = 2;
	private int szpice = 5;

	private NonPublic() { }
}

class AllStatic {
	public static int bombki = 1;
	public static int szpice = 5;
	public static int lampki = 6;
	protected static int cukierki = 3;
	static int prezenty = 4;
	private static int lancuchy = 2;
}

class Inherited extends AlmostAll {
}

class NoNoArgsConstructor {
	public int bombki = 10;

	public NoNoArgsConstructor(int amount) {
		bombki = amount;
	}
}

class MultipleConstructors {
	public int cukierki = 2;

	MultipleConstructors() {
		cukierki = 3;
	}

	MultipleConstructors(int n) {
		cukierki = n;
	}

	MultipleConstructors(int x, int y) {
		if (x == y) {
			cukierki = x + y;
		}
	}

	MultipleConstructors(boolean runForever) {
		while (runForever) {
			// nothing
		}
	}
}

class NotCounted {
	public int cukierki = 100;
}

class SwMikolajTest {
	class Inner {
		public int cukierki = 200;
	}

	static class InnerStatic {
		public int cukierki = 10;
	}

	@org.junit.jupiter.api.Test
	void inwentaryzacja() {
		var inv = new SwMikolaj();
		var _notCounted = NotCounted.class.getName();
		var res = inv.inwentaryzacja(List.of(
				ShouldBeCounted.class.getName(),
				NotInt.class.getName(),
				AlmostAll.class.getName(),
				NonPublic.class.getName(),
				AllStatic.class.getName(),
				Inherited.class.getName(),
				NoNoArgsConstructor.class.getName(),
				MultipleConstructors.class.getName(),
				InnerStatic.class.getName(),
				Inner.class.getName()
		));

		System.err.println(res);
		assert res.equals(Map.of(
				"bombki", 4,
				"lancuchy", 0,
				"cukierki", 16,
				"prezenty", 4,
				"szpice", 5
		));
	}
}
