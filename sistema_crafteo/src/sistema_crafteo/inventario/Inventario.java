package sistema_crafteo.inventario;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import sistema_crafteo.logica.OperacionesMap;
import sistema_crafteo.objeto.Item;
import sistema_crafteo.objeto.MesaDeTrabajo;

public class Inventario {
	private Map<Item, Integer> items;
	private Set<MesaDeTrabajo> mesas;

	public Inventario() {
		items = new HashMap<>();
		mesas = new HashSet<>();
	}

	public void recolectarItem(Item item, int cantidad) {
		if(item == null) {
			throw new NullPointerException("Item nulo");
		}
		if (item.esCrafteable()) {
			throw new IllegalArgumentException("Item no recolectable");
		}

		if (cantidad <= 0) {
			throw new IllegalArgumentException("Cantidad recolectada menor/igual a cero");
		}

		OperacionesMap.sumarValor(items, item, cantidad);
	}

	public void removerItem(Item item, int cantidad) {
		if (cantidad <= 0) {
			throw new IllegalArgumentException("Cantidad a remover menor/igual a cero");
		}
		OperacionesMap.restarValor(items, item, cantidad);
	}

	public int getCantidad(Item item) {
		return items.getOrDefault(item, 0);
	}

	public boolean agregarMesa(MesaDeTrabajo mesa) {
		return mesas.add(mesa);
	}

	public boolean tieneMesa(MesaDeTrabajo mesa) {
		return mesas.contains(mesa);
	}

	public Map<Item, Integer> getItems() {
		return this.items;
	}

}
