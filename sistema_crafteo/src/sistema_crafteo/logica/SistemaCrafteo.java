package sistema_crafteo.logica;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sistema_crafteo.inventario.Inventario;
import sistema_crafteo.objeto.Item;
import sistema_crafteo.objeto.Receta;

public class SistemaCrafteo {
	private Set<Item> itemsRegistrados;
	private final List<HistorialCrafteo> historial;

	public SistemaCrafteo() {
		itemsRegistrados = new HashSet<>();
		historial = new LinkedList<>();
	}

	public boolean registrarItem(Item item) {
		if (item == null) {
			throw new NullPointerException("Puntero a Item nulo");
		}
		return itemsRegistrados.add(item);
	}

	public Map<Item, Integer> getIngredientesFaltantes(Item item, Inventario inventario) {
		if (!item.esCrafteable()) {
			return null;
		}

		Map<Item, Integer> ingredientesItem = item.getIngredientes();
		Map<Item, Integer> ingredientesInventario = inventario.getItems();

		Map<Item, Integer> faltantes = OperacionesMap.restarTodo(ingredientesItem, ingredientesInventario);

		faltantes = OperacionesMap.quitarKeysConValorCero(faltantes);

		return faltantes;
	}

	public List<Map<Item, Integer>> getIngredientesFaltantesTodos(Item item, Inventario inventario) {
		if (!item.esCrafteable()) {
			return null;
		}
		List<Map<Item, Integer>> ret = new LinkedList<>();
		List<Map<Item, Integer>> ingredientesPorReceta = item.getIngredientesTodos();

		for (Map<Item, Integer> map : ingredientesPorReceta) {
			Map<Item, Integer> interseccion = OperacionesMap.restarTodo(map, inventario.getItems());
			interseccion = OperacionesMap.quitarKeysConValorCero(interseccion);
			ret.add(interseccion);
		}

		return ret;
	}

	public Map<Item, Integer> getIngredientesBasicosFaltantes(Item item, Inventario inventario) {
		if (!item.esCrafteable()) {
			return null;
		}

		Map<Item, Integer> itemsFaltantes = getIngredientesFaltantes(item, inventario);
		if (itemsFaltantes.isEmpty()) {
			return null;
		}
		Receta recetaFaltantes = new Receta(itemsFaltantes, null);

		Map<Item, Integer> basicosFaltantesItem = recetaFaltantes.getRecetasBasicas(1);
		Map<Item, Integer> ingredientesInventario = inventario.getItems();

		ingredientesInventario = OperacionesMap.restarTodo(ingredientesInventario, item.getIngredientes());
		Map<Item, Integer> faltantes = OperacionesMap.restarTodo(basicosFaltantesItem, ingredientesInventario);

		faltantes = OperacionesMap.quitarKeysConValorCero(faltantes);

		return faltantes;
	}

	public List<Map<Item, Integer>> getIngredientesBasicosFaltantesTodos(Item item, Inventario inventario) {
		if (!item.esCrafteable()) {
			return null;
		}
		List<Map<Item, Integer>> ret = new LinkedList<>();
		List<Map<Item, Integer>> ingredientesPorReceta = getIngredientesFaltantesTodos(item, inventario);
		List<Map<Item, Integer>> recetas = item.getIngredientesTodos();

		for (int i = 0; i < recetas.size(); i++) {
			Map<Item, Integer> ingredientesFaltantes = ingredientesPorReceta.get(i);
			if (ingredientesFaltantes.isEmpty()) {
				continue;
			}
			Receta recetaFaltantes = new Receta(ingredientesFaltantes, null);
			Map<Item, Integer> basicosFaltantes = recetaFaltantes.getRecetasBasicas(1);
			Map<Item, Integer> itemsInventario = inventario.getItems();

			itemsInventario = OperacionesMap.restarTodo(itemsInventario, recetas.get(i));
			Map<Item, Integer> faltantes = OperacionesMap.restarTodo(basicosFaltantes, itemsInventario);
			faltantes = OperacionesMap.quitarKeysConValorCero(faltantes);

			ret.add(faltantes);
		}

		return ret;
	}

	public boolean puedeCraftear(Inventario inventario, Item item) {
		return getIngredientesFaltantes(item, inventario).isEmpty();
	}

	public boolean puedeCraftearBasicos(Inventario inventario, Item item) {
		return getIngredientesBasicosFaltantes(item, inventario).isEmpty();
	}

	public int getCantidadMaximaCrafteable(Inventario inventario, Item item) {
		int cantidad = 0;

		return cantidad;
	}

	public Set<Item> getItemsRegistrados() {
		return itemsRegistrados;
	}

	public List<HistorialCrafteo> getHistorial() {
		return historial;
	}
}
