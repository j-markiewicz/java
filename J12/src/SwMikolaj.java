import java.util.*;
import java.lang.reflect.*;

public class SwMikolaj implements Inwentaryzator {
	static List<String> INTERESTING_FIELDS = List.of("bombki", "lancuchy", "cukierki", "prezenty", "szpice", "lampki");

	/**
	 * Zlecenie inwentaryzacji klas o nazwach podanych jako kolejne pozycje
	 * przekazywanej listy. Inwentaryzacja polega na przeglądnięciu za pomocą
	 * mechanizmów reflekcji klas, i odszukanie w nich <b>publicznych, niestatycznych
	 * pól typu "int" o podanych poniżej nazwach</b>. Z pól należy odczytać ich wartość.
	 * Wartości pól o identycznych nazwach należy zsumować. Wynikiem jest mapa,
	 * której kluczem jest nazwa pola, wartością jest uzyskana suma dla pól o tej
	 * nazwie. Jeśli wśród testowanych klas w żadnej nie będzie pola o odpowiedniej nazwie i
	 * własnościach, to danej nazwy nie umieszcza się w mapie.
	 * <br>
	 * Lista interesujących pól:
	 * <ul>
	 * <li>bombki</li>
	 * <li>lancuchy</li>
	 * <li>cukierki</li>
	 * <li>prezenty</li>
	 * <li>szpice</li>
	 * <li>lampki</li>
	 * </ul>
	 *
	 * @param listaKlas klasy do intenteryzacji
	 * @return mapa będąca wynikiem inwentaryzacji
	 */
	@Override
	public Map<String, Integer> inwentaryzacja(List<String> listaKlas) {
		var res = new HashMap<String, Integer>();

		for (var name : listaKlas) {
			try {
				var clas = Class.forName(name);
				var constructor = clas.getDeclaredConstructor();
				constructor.setAccessible(true);
				Arrays.stream(clas.getFields())
						.filter(f -> f.getDeclaringClass().equals(clas))
						.filter(f -> INTERESTING_FIELDS.contains(f.getName()))
						.filter(f -> (f.getModifiers() | Modifier.PUBLIC) == f.getModifiers())
						.filter(f -> (f.getModifiers() | Modifier.STATIC) != f.getModifiers())
						.filter(f -> f.getType().equals(int.class))
						.forEach(f -> res.compute(f.getName(), (_k, v) -> {
							try {
								return Objects.requireNonNullElse(v, 0) + f.getInt(constructor.newInstance());
							} catch (Exception ignored) {
								return v;
							}
						}));
			} catch (Exception ignored) {
			}
		}

		return res;
	}
}
