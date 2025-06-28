package sistema_crafteo.logica;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import sistema_crafteo.objeto.Item;

public class Registro {
	private final String item;
	private final Map<String, Integer> ingredientes;
	private final String fecha;

	public Registro(Item item, Map<Item, Integer> ingredientes) {
		if (item == null) {
			throw new NullPointerException("Puntero a item nulo");
		}
		if(ingredientes == null) {
			throw new NullPointerException("Puntero a ingredientes nulo");
		}
		this.item = item.toString();
		Map<String, Integer> ingredientesString = new HashMap<>();
		for (Map.Entry<Item, Integer> entrada : ingredientes.entrySet()) {
			ingredientesString.put(entrada.getKey().toString(), entrada.getValue());
		}
		this.ingredientes = ingredientesString;
		this.fecha = LocalDateTime.now().toString();
	}

	public String getItem() {
		return item;
	}

	public Map<String, Integer> getIngredientes() {
		return ingredientes;
	}

	public String getFecha() {
		return fecha;
	}

	@Override
	public String toString() {
		return "[item=" + item + ", ingredientes=" + ingredientes + ", fecha=" + fecha + "]";
	}

}
