package sistema_crafteo.logica;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import sistema_crafteo.objeto.Item;

public class Registro {
	private final Item item;
	private final Map<Item, Integer> ingredientes;
	private final LocalDateTime fecha;

	public Registro(Item item, Map<Item, Integer> ingredientes) {
		if (item == null) {
			throw new NullPointerException("Puntero a item nulo");
		}

		this.item = item;
		this.ingredientes = ingredientes == null ? new HashMap<>() : ingredientes;
		this.fecha = LocalDateTime.now();
	}

	public Item getItem() {
		return item;
	}

	public Map<Item, Integer> getIngredientes() {
		return ingredientes;
	}

	public LocalDateTime getFecha() {
		return fecha;
	}

	@Override
	public String toString() {
		return "[item=" + item + ", ingredientes=" + ingredientes + ", fecha=" + fecha + "]";
	}

}
