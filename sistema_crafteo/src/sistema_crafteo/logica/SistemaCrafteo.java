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

	public boolean puedeCraftear(Inventario inventario, Item item) {
		Map<Item, Integer> resultado = getIngredientesFaltantes(item, inventario);
		if (resultado == null) {
			return false;
		}

		if (!inventario.tieneMesa(item.getReceta().getMesaRequerida())) {
			return false;
		}

		return resultado.isEmpty();
	}

	public boolean puedeCraftearBasicos(Inventario inventario, Item item) {
		Map<Item, Integer> resultado = getIngredientesBasicosFaltantes(item, inventario);
		if (resultado == null) {
			return false;
		}

		Set<MesaDeTrabajo> mesasRequeridas = item.getReceta().getMesas();

		for (MesaDeTrabajo mesa : mesasRequeridas) {
			if (!inventario.tieneMesa(mesa)) {
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

	/*
	 * public boolean craftear(Inventario inventario, Item item) { if
	 * (!ejecutarCrafteo(inventario, item)) { return false; }
	 * 
	 * historial.registrarCrafteo(item, item.getIngredientes()); return true; }
	 */
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

	public void cargarProlog() {
		for (Item item : itemsRegistrados) {
			Prolog.cargarItem(item);
		}
		Prolog.cargarReglas();
	}

	public boolean puedeCraftearProlog(Inventario inventario, Item item) {
		Prolog.cargarInventario(inventario);
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

	public static void mostrarFuncionamiento() {
		try {
			// 1. Rutas a archivos JSON
			String recetasJson = "files/recetas.json"; // Ajustar ruta si es necesario
			String inventarioJson = "files/inventario.json"; // Ajustar ruta si es necesario
			
			// 2. Cargar items definidos en recetas
			GestorArchivo gestor = new GestorArchivo(Paths.get(inventarioJson), Paths.get(recetasJson));
			Map<String, Item> items = gestor.cargarItems();
			System.out.println("Items cargados: " + items.keySet());

			// 3. Crear e inicializar inventario
			Inventario inventario = gestor.cargarInventario(items);
			System.out.println("Inventario inicial: " + inventario.getItems());

			// 4. Registrar items en el sistema
			SistemaCrafteo sistema = new SistemaCrafteo();
			for (Item item : items.values()) {
				sistema.registrarItem(item);
			}

			// 5. Ejemplo de crafteo
			// Primero hago la mesa si no la tengo (en este caso ya la tengo)
			// MesaDeTrabajo mesaEscudo =
			// items.get("escudo").getReceta().getMesaRequerida();
			// inventario.agregarMesa(mesaEscudo);
			
			System.out.println(inventario.tieneMesa(new MesaDeTrabajo("mesa_carbonizo")));
			
			String nombreObjetivo = "escudo"; // Cambiar por el item a craftear
			Item objetivo = items.get(nombreObjetivo);
			if (objetivo == null) {
				System.err.println("Item no encontrado: " + nombreObjetivo);
				return;
			}

			System.out
					.println("Ingredientes (default) " + nombreObjetivo + " " + objetivo.getIngredientes().toString());
			System.out.println(
					"Ingredientes (receta 3) " + nombreObjetivo + " " + objetivo.getIngredientes(2).toString() + "\n");

			if (sistema.puedeCraftearConReceta(inventario, objetivo, 2))
				System.out.println("Se puede craftear escudo.");
			else
				System.out.println("No se puede craftear escudo.");

			System.out.println("Basicos faltantes escudo: " + sistema.getBasicosFaltantesMinimos(inventario, objetivo));

			System.out.println("Ingredientes nivel1 faltantes escudo (receta 3): "
					+ sistema.getFaltantesNivel1(objetivo, 2, inventario));
			System.out.println("Ingredientes nivel1 faltantes escudo (para todos): "
					+ sistema.getBasicosFaltantesMinimos(inventario, objetivo));

			// System.out.println(objetivo.getArbolCrafteoBasicos(3) + "\n");
			// System.out.println(objetivo.getArbolCrafteo(3));

			System.out.println("Cantidad maxima de escudo (receta 3): "
					+ sistema.getCantidadMaximaParaReceta(inventario, objetivo, 2));
			System.out.println("Cantidad maxima de escudo: " + sistema.getCantidadMaxima(inventario, objetivo));

			boolean exito = sistema.craftear(inventario, objetivo, 2);
			if (exito) {
				System.out.println("Crafteado exitosamente: " + nombreObjetivo);
			} else {
				System.out.println("No se pudo craftear: " + nombreObjetivo);
			}
/*
			nombreObjetivo = "piedra premium";
			objetivo = items.get("piedra premium");

			System.out.println(
					"Faltantes nivel1 para piedra: " + sistema.getFaltantesNivel1Minimos(objetivo, inventario));

			exito = sistema.craftearConReceta(inventario, objetivo, 1);
			if (exito) {
				System.out.println("Crafteado exitosamente: " + nombreObjetivo);
			} else {
				System.out.println("No se pudo craftear: " + nombreObjetivo);
			}
*/
			// 6. Mostrar estado final
			System.out.println("Inventario final: " + inventario.getItems());
			HistorialCrafteo historial = sistema.getHistorial();
			System.out.println("\nHistorial de crafteos: \n" + historial.getRegistros());

			gestor.guardarInventario(Paths.get("files/inventario-out.json"), inventario);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/////////////////////////////////////////////////////

	public boolean puedeCraftearConReceta(Inventario inv, Item item, int numRecetaNivel1) {
		if (!item.esCrafteable())
			return false;
		
		// Se duplica inventario
		Map<Item, Integer> copiaInv = new HashMap<Item, Integer>();
		copiaInv.putAll(inv.getItems());
		
		Receta rec = item.getReceta(numRecetaNivel1);
		if(!inv.tieneMesa(rec.getMesaRequerida())) {
			return false;
		}
		
		// Inicia recursion
		return consumirRecursivo(item.getReceta(numRecetaNivel1), copiaInv, inv);
	}
	
	private Receta elegirSubRecetaViable(Item item, int qty, Map<Item, Integer> simInv, Inventario invReal) {
		for (Receta cand : item.getRecetas()) {
			int upLote = cand.getCantidadGenerada();
			int lotes = (qty + upLote - 1) / upLote;

			Map<Item, Integer> copia = cloneMap(simInv);
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
		
		if(rec.getMesaRequerida() != null && !invReal.tieneMesa(rec.getMesaRequerida()))
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
			
			// Si llegamos aca, se puede realizar el crafteo. Actualizamos el inventario real (el recibido por parametro)
			int unidadesPorLote = subRec.getCantidadGenerada();
			int lotes = (faltante + unidadesPorLote - 1) / unidadesPorLote;
			for (int i = 0; i < lotes; i++) {
				if (!consumirRecursivo(subRec, simInv, invReal)) {
					return false; // debería no ocurrir, pero por seguridad
				}
				
				simInv.put(ing, simInv.getOrDefault(ing,0) + unidadesPorLote);
			}
		
			simInv.put(ing, simInv.getOrDefault(ing,0) - cant);
		}
		// Si se llego hasta aca, se recorrieron todos los ingredientes y se puede craftear el objeto
		return true;

	}

	public Map<Item, Integer> getBasicosFaltantesMinimos(Inventario inv, Item item) {
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

	private Map<Item, Integer> cloneMap(Map<Item, Integer> orig) {
		return new HashMap<>(orig);
	}

	/**
	 * Recorre todas las recetas de 'item' y devuelve el mapa de ingredientes de
	 * primer nivel faltantes mínimo entre ellas.
	 */
	public Map<Item, Integer> getFaltantesNivel1Minimos(Item item, Inventario inv) {
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
		// Validar índice 1-based
		int n = item.getRecetas().size();
		if (recetaIndex1 < 0 || recetaIndex1 > n) {
			throw new IllegalArgumentException("Índice de receta inválido");
		}
		Receta receta = item.getRecetas().get(recetaIndex1);

		// Clonamos inventario para simular
		Map<Item, Integer> simInv = cloneMap(inv.getItems());
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
			if (puedeCraftearConReceta(inv, item, idx1)) {
				// 2) crafteamos con esa receta
				return craftear(inv, item, idx1);
			}
		}
		return false;
	}

	public boolean craftear(Inventario inv, Item item, int recetaIndex1) {
		// Validaciones básicas
		if (!item.esCrafteable())
			return false;
		int nRec = item.getRecetas().size();
		if (recetaIndex1 < 0 || recetaIndex1 > nRec) {
			throw new IllegalArgumentException("Índice de receta inválido");
		}
		
		Receta receta = item.getRecetas().get(recetaIndex1);
		// 1) simulamos primero para asegurarnos de que cabe
		Map<Item, Integer> objetos = cloneMap(inv.getItems());
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
