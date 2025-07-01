package sistema_crafteo.integracion;

import java.util.Map;
import java.util.Set;

import sistema_crafteo.inventario.Inventario;
import sistema_crafteo.objeto.Item;

public class Prolog {

	public String cargarItems(Set<Item> itemsRegistrados) {
		StringBuilder query = new StringBuilder();
		for (Item item : itemsRegistrados) {
			if (!item.esCrafteable()) {
				query.append(hechoElementoBasico(item));
			} else {
				query.append(hechoObjetoCrafteable(item));
			}
		}
		return query.toString();
	}

	public String cargarInventario(Inventario inventario) {
		StringBuilder query = new StringBuilder();
		for (Map.Entry<Item, Integer> entrada : inventario.getItems().entrySet()) {
			Item item = entrada.getKey();
			Integer valor = entrada.getValue();
			query.append("tengo(" + item.getNombre() + ", " + valor + "). ");
		}
		return query.toString();
	}

	public void ejecutarQuery(String query) {
		// ejecuta en prolog
	}

	private String hechoElementoBasico(Item ing) {
		return "";
	}

	private String hechoObjetoCrafteable(Item ing) {
		return "";
	}

}
