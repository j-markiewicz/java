import java.time.Instant;
import java.util.*;

class AlwaysNDie implements KostkaDoGry {
	int n;

	AlwaysNDie(int always) {
		n = always;
	}

	public int rzut() {
		return n;
	}
}

class AOrBDie implements KostkaDoGry {
	int a;
	int b;
	boolean next_is_a;

	AOrBDie(int roll_a, int roll_b) {
		a = roll_a;
		b = roll_b;
		next_is_a = true;
	}

	public int rzut() {
		if (next_is_a) {
			next_is_a = false;
			return a;
		} else {
			next_is_a = true;
			return b;
		}
	}
}

class WspanialyEksperymentatorTest {
	@org.junit.jupiter.api.Test
	void użyjKostki() {
		var eks = new WspanialyEksperymentator();
		eks.użyjKostki(new AlwaysNDie(1));
		assert eks.die.rzut() == 1;
		eks.użyjKostki(new AlwaysNDie(1));
		assert eks.die.rzut() == 1;
	}

	@org.junit.jupiter.api.Test
	void czasJednegoEksperymentu() {
		var eks = new WspanialyEksperymentator();
		eks.czasJednegoEksperymentu(100);
		assert eks.experimentTimeout.toMillis() == 100;
		eks.czasJednegoEksperymentu(20);
		assert eks.experimentTimeout.toMillis() == 20;
		eks.czasJednegoEksperymentu(1000);
		eks.użyjKostki(new AlwaysNDie(1));
		var start = Instant.now();
		eks.szansaNaWyrzucenieOczek(5);
		var end = Instant.now();
		assert Math.abs(end.toEpochMilli() - start.toEpochMilli() - 1000) < 5;
	}

	@org.junit.jupiter.api.Test
	void szansaNaWyrzucenieOczek() {
		var eks = new WspanialyEksperymentator();
		eks.czasJednegoEksperymentu(100);
		eks.użyjKostki(new AlwaysNDie(3));
		assert Objects.equals(eks.szansaNaWyrzucenieOczek(10), Map.of(30, 1.0));
		eks.użyjKostki(new AOrBDie(1, 2));
		var res = eks.szansaNaWyrzucenieOczek(10);
		assert res.get(5) == null;
		assert res.get(15) == 1.0;
		assert eks.die.rzut() == 1;
		var res2 = eks.szansaNaWyrzucenieOczek(3);
		assert res2.get(4) > 0.49;
		assert res2.get(4) < 0.51;
		assert res2.get(5) > 0.49;
		assert res2.get(5) < 0.51;
	}

	@org.junit.jupiter.api.Test
	void szansaNaWyrzucenieKolejno() {
		var eks = new WspanialyEksperymentator();
		eks.czasJednegoEksperymentu(100);
		eks.użyjKostki(new AlwaysNDie(3));
		assert eks.szansaNaWyrzucenieKolejno(List.of(3, 3, 3, 3, 3)) == 1.0;
		eks.użyjKostki(new AOrBDie(1, 2));
		assert eks.szansaNaWyrzucenieKolejno(List.of(3, 3, 3, 3, 3)) == 0.0;
		assert eks.szansaNaWyrzucenieKolejno(List.of(1, 2, 1)) > 0.49;
		assert eks.szansaNaWyrzucenieKolejno(List.of(1, 2, 1)) < 0.51;
	}

	@org.junit.jupiter.api.Test
	void szansaNaWyrzucenieWDowolnejKolejności() {
		var eks = new WspanialyEksperymentator();
		eks.czasJednegoEksperymentu(100);
		eks.użyjKostki(new AlwaysNDie(3));
		assert eks.szansaNaWyrzucenieWDowolnejKolejności(Set.of(3)) == 1.0;
		assert eks.szansaNaWyrzucenieWDowolnejKolejności(Set.of(1, 2, 3, 4, 5, 6)) == 0.0;
		eks.użyjKostki(new AOrBDie(1, 2));
		assert eks.szansaNaWyrzucenieWDowolnejKolejności(Set.of(3)) == 0.0;
		assert eks.szansaNaWyrzucenieWDowolnejKolejności(Set.of(1, 2)) == 1.0;
		assert eks.szansaNaWyrzucenieWDowolnejKolejności(Set.of(1, 2, 3)) == 0.0;
	}
}
