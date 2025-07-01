package sistema_crafteo.logica;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sistema_crafteo.inventario.Inventario;
import sistema_crafteo.objeto.Item;
import sistema_crafteo.objeto.MesaDeTrabajo;
import sistema_crafteo.objeto.Receta;

public class SistemaCrafteo {
	private Set<Item> itemsRegistrados;
	private final HistorialCrafteo historial;

	public SistemaCrafteo() {
		itemsRegistrados = new HashSet<>();
		historial = new HistorialCrafteo();
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
			return itemsFaltantes;
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
		Map<Item, Integer> resultado = getIngredientesFaltantes(item, inventario);
		if(resultado == null) {
			return false;
		}
		
		if(!inventario.tieneMesa(item.getReceta().getMesaRequerida())) {
			return false;
		}
		
		return resultado.isEmpty();
	}

	public boolean puedeCraftearBasicos(Inventario inventario, Item item) {
		Map<Item, Integer> resultado = getIngredientesBasicosFaltantes(item, inventario);
		if(resultado == null) {
			return false;
		}
		
		Set<MesaDeTrabajo> mesasRequeridas = item.getReceta().getMesas();
		
		for (MesaDeTrabajo mesa : mesasRequeridas) {
			if(!inventario.tieneMesa(mesa)) {
				return false;
			}
		}
		
		return resultado.isEmpty();
	}

	public int getCantidadMaximaCrafteable(Inventario inventario, Item item) {
		if (!item.esCrafteable()) {
			return 0;
		}

		int cantidad = 0;
		Inventario modificable = new Inventario();
		Map<Item, Integer> copiaItemsInventario = new HashMap<Item, Integer>();
		copiaItemsInventario.putAll(inventario.getItems());
		modificable.setItems(copiaItemsInventario);

		while (ejecutarCrafteo(modificable, item)) {
			cantidad++;
		}

		return cantidad;
	}

	public boolean craftear(Inventario inventario, Item item) {
		if(!ejecutarCrafteo(inventario, item)) {
			return false;
		}
		
		historial.registrarCrafteo(item, item.getIngredientes());
		return true;
	}

	private boolean ejecutarCrafteo(Inventario inventario, Item item) {
		if (!item.esCrafteable()) {
			return false;
		}

		if (!puedeCraftearBasicos(inventario, item)) {
			return false;
		}

		Map<Item, Integer> objetos = inventario.getItems();

		if (!puedeCraftear(inventario, item)) {
			Map<Item, Integer> faltantes = getIngredientesFaltantes(item, inventario);
			Map<Item, Integer> faltantesABasicos = obtenerBasicos(faltantes);

			for (Map.Entry<Item, Integer> entrada : faltantes.entrySet()) {
				Item ingrediente = entrada.getKey();
				Integer cantidadFaltante = entrada.getValue();
				OperacionesMap.sumarValor(objetos, ingrediente,
						ingrediente.getCantidadGenerada() * ingrediente.cantidadCrafteos(cantidadFaltante));
			}
			
			objetos = OperacionesMap.restarTodo(objetos, faltantesABasicos);
		}

		objetos = OperacionesMap.restarTodo(objetos, item.getIngredientes());
		OperacionesMap.sumarValor(objetos, item, item.getCantidadGenerada());
		objetos = OperacionesMap.quitarKeysConValorCero(objetos);
		inventario.setItems(objetos);
		
		
		return true;
	}

	private Map<Item, Integer> obtenerBasicos(Map<Item, Integer> ing) {
		if (ing == null || ing.isEmpty()) {
			return new HashMap<Item, Integer>();
		}
		Receta aux = new Receta(ing, null);
		return aux.getRecetasBasicas(1);
	}

	public Set<Item> getItemsRegistrados() {
		return itemsRegistrados;
	}

	public HistorialCrafteo getHistorial() {
		return historial;
	}
}
