package sistema_crafteo.logica;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sistema_crafteo.integracion.GestorArchivo;
import sistema_crafteo.integracion.Prolog;
import sistema_crafteo.inventario.Inventario;
import sistema_crafteo.objeto.Item;
import sistema_crafteo.objeto.Receta;

public class SistemaCrafteo {
	private Set<Item> itemsRegistrados;
	private final HistorialCrafteo historial;

	public SistemaCrafteo() {
		itemsRegistrados = new HashSet<>();
		historial = new HistorialCrafteo();
		Prolog.cargarReglas();
	}

	public boolean registrarItem(Item item) {
		if (item == null) {
			throw new NullPointerException("Puntero a Item nulo");
		}
		return itemsRegistrados.add(item);
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
				ret.add(ingredientesFaltantes);
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

	public Inventario cargarDatos(String pathRecetas, String pathInventario) {
		GestorArchivo gestor = new GestorArchivo(Paths.get(pathInventario), Paths.get(pathRecetas));
		Inventario inventario = null;
		try {
			Map<String, Item> items = gestor.cargarItems();
			for (Item item : items.values()) {
				registrarItem(item);
			}
			inventario = gestor.cargarInventario(items);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return inventario;
	}

	/**
	 * Verifica si se puede craftear un item usando la lógica de Prolog. Las reglas
	 * se cargan en el constructor, solo se recarga el inventario.
	 */
	public boolean puedeCraftearProlog(Inventario inventario, Item item) {
		if (!item.esCrafteable()) {
			return false;
		}

		Prolog.limpiarBaseObjetos();
		Prolog.cargarInventario(inventario);

		for (Item itemRegistrado : itemsRegistrados) {
			Prolog.cargarItem(itemRegistrado);
		}

		return Prolog.puedoCraftear(item);
	}

	public Set<Item> getItemsRegistrados() {
		return itemsRegistrados;
	}

	public HistorialCrafteo getHistorial() {
		return historial;
	}

	public Item buscarItem(String nombre) {
		for (Item item : itemsRegistrados) {
			if (item.getNombre().equalsIgnoreCase(nombre)) {
				return item;
			}
		}
		return null;
	}

	/////////////////////////////////////////////////////

	private Receta elegirSubRecetaViable(Item item, int qty, Map<Item, Integer> simInv, Inventario invReal) {
		for (Receta cand : item.getRecetas()) {
			int upLote = cand.getCantidadGenerada();
			int lotes = (qty + upLote - 1) / upLote;

			Map<Item, Integer> copia = new HashMap<>(simInv);;
			boolean ok = true;
			for (int i = 0; i < lotes; i++) {
				if (!consumirRecursivo(cand, copia, invReal)) {
					ok = false;
					break;
				}
			}
			if (ok)
				return cand;
		}
		return null;

	}

	private boolean consumirRecursivo(Receta rec, Map<Item, Integer> simInv, Inventario invReal) {
		// Por cada receta que tenga el item/ingrediente

		if (rec.getMesaRequerida() != null && !invReal.tieneMesa(rec.getMesaRequerida()))
			return false;

		for (var entry : rec.getIngredientes().entrySet()) {
			Item ing = entry.getKey();
			int cant = entry.getValue();

			int disponible = simInv.getOrDefault(ing, 0);
			if (disponible >= cant) {
				simInv.put(ing, disponible - cant);
				continue;
			}

			if (!ing.esCrafteable())
				return false;

			int faltante = cant - disponible;

			Receta subRec = elegirSubRecetaViable(ing, faltante, simInv, invReal);

			if (subRec == null) {
				return false;
			}

			// Si llegamos aca, se puede realizar el crafteo. Actualizamos el inventario
			// real (el recibido por parametro)
			int unidadesPorLote = subRec.getCantidadGenerada();
			int lotes = (faltante + unidadesPorLote - 1) / unidadesPorLote;
			for (int i = 0; i < lotes; i++) {
				if (!consumirRecursivo(subRec, simInv, invReal)) {
					return false; // debería no ocurrir, pero por seguridad
				}

				simInv.put(ing, simInv.getOrDefault(ing, 0) + unidadesPorLote);
			}

			simInv.put(ing, simInv.getOrDefault(ing, 0) - cant);
		}
		// Si se llego hasta aca, se recorrieron todos los ingredientes y se puede
		// craftear el objeto
		return true;

	}

	public Map<Item, Integer> getBasicosFaltantesMinimos(Item item, Inventario inv) {
		Map<Item, Integer> mejor = new HashMap<>();
		int minTotal = Integer.MAX_VALUE;

		List<Map<Item, Integer>> faltantesBasicos = getIngredientesBasicosFaltantesTodos(item, inv);

		for (Map<Item, Integer> ingredientes : faltantesBasicos) {
			int total = ingredientes.values().stream().mapToInt(i -> i).sum();
			if (total < minTotal) {
				minTotal = total;
				mejor = ingredientes;
				if (minTotal == 0)
					break;
			}
		}
		return mejor;
	}

	/**
	 * Calcula los básicos faltantes para UNA receta específica.
	 */
	public Map<Item, Integer> getBasicosFaltantesParaReceta(Inventario inv, Item item, int recetaIndex) {
		if (recetaIndex < 0 || recetaIndex >= item.getRecetas().size()) {
			throw new IllegalArgumentException("Índice de receta inválido");
		}

		return getIngredientesBasicosFaltantesTodos(item, inv).get(recetaIndex);
	}

	/**
	 * Recorre todas las recetas de 'item' y devuelve el mapa de ingredientes de
	 * primer nivel faltantes mínimo entre ellas.
	 */
	public Map<Item, Integer> getFaltantesNivel1Minimos(Item item, Inventario inv) {
		if(!item.esCrafteable())
			return null;
		
		Map<Item, Integer> mejor = new HashMap<>();
		int minTotal = Integer.MAX_VALUE;
		List<Map<Item, Integer>> faltantesNivel1 = getIngredientesFaltantesTodos(item, inv);

		for (Map<Item, Integer> ingredientes : faltantesNivel1) {
			int total = ingredientes.values().stream().mapToInt(i -> i).sum();
			if (total < minTotal) {
				minTotal = total;
				mejor = ingredientes;
				if (minTotal == 0)
					break;
			}
		}

		return mejor;
	}

	/**
	 * Devuelve el mapa de ingredientes de primer nivel faltantes para craftear 1
	 * unidad de 'item' usando únicamente la receta en posición 'recetaIndex'.
	 */
	public Map<Item, Integer> getFaltantesNivel1(Item item, int recetaIndex, Inventario inv) {
		if (recetaIndex < 0 || recetaIndex >= item.getRecetas().size()) {
			throw new IllegalArgumentException("Índice de receta inválido");
		}
		return getIngredientesFaltantesTodos(item, inv).get(recetaIndex);

	}

	/**
	 * Calcula cuántas unidades como máximo se pueden craftear de 'item' usando
	 * únicamente la receta en posición 'recetaIndex'. Devuelve 0 si no es
	 * crafteable o el índice es inválido.
	 */
	public int getCantidadMaximaParaReceta(Inventario inv, Item item, int recetaIndex1) {
		if (!item.esCrafteable()) {
			return 0;
		}
		
		
		int n = item.getRecetas().size();
		if (recetaIndex1 < 0 || recetaIndex1 >= n) {
			throw new IllegalArgumentException("Índice de receta inválido");
		}
		Receta receta = item.getRecetas().get(recetaIndex1);

		// Clonamos inventario para simular
		Map<Item, Integer> simInv = new HashMap<>(inv.getItems());
		int lotes = 0;

		// Intentamos consumir lotes hasta que no quepa
		while (consumirRecursivo(receta, simInv, inv)) {
			lotes++;
		}
		// Cada lote produce 'cantidadGenerada'
		return lotes * receta.getCantidadGenerada();
	}

	/**
	 * Calcula la máxima cantidad crafteable de 'item' probando todas sus recetas y
	 * devolviendo la mayor.
	 */
	public int getCantidadMaxima(Inventario inv, Item item) {
		if (!item.esCrafteable()) {
			return 0;
		}
		int mejor = 0;
		int n = item.getRecetas().size();
		for (int i = 0; i < n; i++) {
			int cant = getCantidadMaximaParaReceta(inv, item, i);
			if (cant > mejor) {
				mejor = cant;
			}
		}
		return mejor;
	}

	/**
	 * Intenta craftear 'item' usando automáticamente la primera receta viable.
	 */
	public boolean craftear(Inventario inv, Item item) {
		// 1) buscamos índice 1-based de la primera receta viable
		int nRec = item.getRecetas().size();
		for (int idx1 = 0; idx1 < nRec; idx1++) {
			if (craftear(inv, item, idx1)) {
				return true;
			}
		}
		return false;
	}

	public boolean craftear(Inventario inv, Item item, int recetaIndex1) {
		if (!item.esCrafteable())
			return false;

		int nRec = item.getRecetas().size();
		// Validaciones básicas
		if (recetaIndex1 < 0 || recetaIndex1 >= nRec) {
			throw new IllegalArgumentException("Índice de receta inválido");
		}

		if (!puedeCraftearProlog(inv, item)) {
			return false;
		}

		Receta receta = item.getRecetas().get(recetaIndex1);
		// 1) simulamos primero para asegurarnos de que cabe
		Map<Item, Integer> objetos = new HashMap<>(inv.getItems());
		if (!consumirRecursivo(receta, objetos, inv)) {
			return false;
		}
		// 2) sobre el inventario real: consumimos definitivamente
		consumirRecursivo(receta, inv.getItems(), inv);
		// 3) agregamos el objeto final
		OperacionesMap.sumarValor(objetos, item, item.getCantidadGenerada(recetaIndex1));
		objetos = OperacionesMap.quitarKeysConValorCero(objetos);
		inv.setItems(objetos);

		historial.registrarCrafteo(item, item.getIngredientes(recetaIndex1));
		return true;
	}

}
