package sistema_crafteo.logica;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sistema_crafteo.inventario.Inventario;
import sistema_crafteo.objeto.Item;

public class SistemaCrafteo {
	private Set<Item> itemsRegistrados;
	private final List<HistorialCrafteo> historial;
	
	
	public SistemaCrafteo() {
		itemsRegistrados = new HashSet<>();
		historial = new LinkedList<>();
	}
	
	public boolean registrarItem(Item item) {
		if(item == null) {
			throw new NullPointerException("Puntero a Item nulo");
		}
		return itemsRegistrados.add(item);
	}

	public Map<Item, Integer> getIngredientesFaltantes(Item item, Inventario inventario){
		if(!item.esCrafteable()) {
			return null;
		}

		Map<Item, Integer> ingredientesItem = item.getIngredientes();
		Map<Item, Integer> ingredientesInventario = inventario.getItems();
		
		Map<Item, Integer> faltantes = OperacionesMap.restarTodo(ingredientesItem, ingredientesInventario);
		
		faltantes = OperacionesMap.quitarKeysConValorCero(faltantes);
		
		return faltantes;
	}
	
	public Set<Item> getItemsRegistrados() {
		return itemsRegistrados;
	}

	public List<HistorialCrafteo> getHistorial() {
		return historial;
	}
	
}
