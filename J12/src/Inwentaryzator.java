import java.util.List;
import java.util.Map;

/**
 * Interfejs intwentaryzatora.
 */
public interface Inwentaryzator {
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
	public Map<String, Integer> inwentaryzacja(List<String> listaKlas);
}
