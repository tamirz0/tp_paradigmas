package sistema_crafteo.integracion;

import java.util.Map;

import org.jpl7.Query;
import org.jpl7.Term;

import sistema_crafteo.inventario.Inventario;
import sistema_crafteo.objeto.Item;

public class Prolog {

	public static void consultarInventario() {
		Query consulta;
		consulta = new Query("tengo(X, Y)");
		while (consulta.hasMoreSolutions()) {
			Map<String, Term> solucion = consulta.nextSolution();
			System.out.println("Elemento en inventario: " + solucion.get("X") + " " + solucion.get("Y"));
		}
	}

	public static void consultarIngredientes() {
		Query consulta;

		consulta = new Query("ingrediente(X, Y, Z)");
		while (consulta.hasMoreSolutions()) {
			Map<String, Term> solucion = consulta.nextSolution();
			System.out.println(
					"Ingrediente: " + solucion.get("X") + " -> " + solucion.get("Y") + " " + solucion.get("Z"));
		}
	}

	public static void consultarItems() {
		Query consulta;

		consulta = new Query("elemento_basico(X)");
		while (consulta.hasMoreSolutions()) {
			Map<String, Term> solucion = consulta.nextSolution();
			System.out.println("Elemento basico: " + solucion.get("X"));
		}

		consulta = new Query("crafteable(X, Y)");
		while (consulta.hasMoreSolutions()) {
			Map<String, Term> solucion = consulta.nextSolution();
			System.out.println("Objeto crafteable: " + solucion.get("X") + " " + solucion.get("Y"));
		}
	}

	public static boolean cargarInventario(Inventario inventario) {
		new Query("retractall(tengo(_, _))").hasSolution(); // Reiniciar inventario
		for (Map.Entry<Item, Integer> entrada : inventario.getItems().entrySet()) {
			Item item = entrada.getKey();
			Integer cantidad = entrada.getValue();

			String nombre = item.getNombre().toLowerCase().trim();
			String hecho = String.format("assertz(tengo(%s, %d)).", nombre, cantidad);
			Query query = new Query(hecho);
			if (!query.hasSolution()) {
				return false;
			}
		}
		return true;
	}

	public static boolean cargarItem(Item item) {
		if (item.esCrafteable()) {
			return cargarObjetoCrafteable(item);
		}
		return cargarElementoBasico(item);
	}

	private static boolean cargarElementoBasico(Item item) {
	    String nombre = item.getNombre().trim().toLowerCase();

	    // Verificar si ya está definido
	    Query existe = new Query("elemento_basico(" + nombre + ")");
	    if (existe.hasSolution()) {
	        return false; // Ya existe, no lo agregamos
	    }

	    // Agregar el hecho
	    String hecho = String.format("assertz(elemento_basico(%s)).", nombre);
	    Query query = new Query(hecho);
	    return query.hasSolution();
	}

	private static boolean cargarObjetoCrafteable(Item item) {
		return cargarObjetoCrafteable(item, 0);
	}

	public static boolean cargarObjetoCrafteable(Item item, int nroReceta) {
	    String nombre = item.getNombre().trim().toLowerCase();

	    // Verificar si ya existe
	    Query existe = new Query("crafteable(" + nombre + ", _)");
	    if (existe.hasSolution()) {
	        return false;
	    }

	    // Agregar crafteable(nombre, cantidad)
	    String hecho = String.format("assertz(crafteable(%s, %d)).", nombre, item.getCantidadGenerada());
	    Query query = new Query(hecho);

	    // Cargar ingredientes (se verifica existencia dentro de cargarItem)
	    for (Map.Entry<Item, Integer> entrada : item.getReceta(nroReceta).getIngredientes().entrySet()) {
	        Item ingrediente = entrada.getKey();
	        Integer cantidad = entrada.getValue();
	        cargarItem(ingrediente);
	        cargarIngrediente(item, ingrediente, cantidad);
	    }

	    return query.hasSolution();
	}

	private static boolean cargarIngrediente(Item padre, Item hijo, int cantidad) {
	    String nombrePadre = padre.getNombre().trim().toLowerCase();
	    String nombreHijo = hijo.getNombre().trim().toLowerCase();

	    // Verificar si ya existe ese ingrediente con la misma cantidad
	    String consulta = String.format("ingrediente(%s, %s, %d)", nombrePadre, nombreHijo, cantidad);
	    Query existe = new Query(consulta);
	    if (existe.hasSolution()) {
	        return false;
	    }

	    // Agregar el hecho
	    String hecho = String.format("assertz(ingrediente(%s, %s, %d)).", nombrePadre, nombreHijo, cantidad);
	    return new Query(hecho).hasSolution();
	}

	public static void cargarReglas() {
	    StringBuilder reglas = new StringBuilder();

	    // Regla principal puedo_craftear/1
	    reglas.append("assertz((puedo_craftear(Objeto) :- ")
	          .append("crafteable(Objeto, _), ")
	          .append("ingredientes_suficientes(Objeto, 1)))");
	    new Query(reglas.toString()).hasSolution();

	    // Regla ingredientes_suficientes/2
	    reglas.setLength(0);
	    reglas.append("assertz((ingredientes_suficientes(Objeto, N) :- ")
	          .append("findall((Ing, Cant), ingrediente(Objeto, Ing, Cant), Ingredientes), ")
	          .append("verificar_ingredientes(Ingredientes, N)))");
	    new Query(reglas.toString()).hasSolution();

	    // Caso base verificar_ingredientes/2
	    reglas.setLength(0);
	    reglas.append("assertz((verificar_ingredientes([], _)))");
	    new Query(reglas.toString()).hasSolution();

	    // Caso recursivo verificar_ingredientes/2
	    reglas.setLength(0);
	    reglas.append("assertz((verificar_ingredientes([(Ing, Cant)|T], Veces) :- ")
	          .append("CantTotal is Cant * Veces, ")
	          .append("disponible(Ing, CantTotal), ")
	          .append("verificar_ingredientes(T, Veces)))");
	    new Query(reglas.toString()).hasSolution();

	    // Caso 1 de disponible/2: lo tengo directamente
	    reglas.setLength(0);
	    reglas.append("assertz((disponible(Ing, CantNecesaria) :- ")
	          .append("tengo(Ing, CantTengo), ")
	          .append("CantTengo >= CantNecesaria))");
	    new Query(reglas.toString()).hasSolution();

	    // Caso 2a de disponible/2: tengo algo pero no suficiente
	    reglas.setLength(0);
	    reglas.append("assertz((disponible(Ing, CantNecesaria) :- ")
	          .append("crafteable(Ing, CantPorCrafteo), ")
	          .append("tengo(Ing, CantTengo), ")
	          .append("CantTengo < CantNecesaria, ")
	          .append("CantFaltante is CantNecesaria - CantTengo, ")
	          .append("Veces is ceiling(CantFaltante / CantPorCrafteo), ")
	          .append("puedo_craftear_n_veces(Ing, Veces)))");
	    new Query(reglas.toString()).hasSolution();

	    // Caso 2b de disponible/2: no tengo nada del ítem
	    reglas.setLength(0);
	    reglas.append("assertz((disponible(Ing, CantNecesaria) :- ")
	          .append("crafteable(Ing, CantPorCrafteo), ")
	          .append("\\+ tengo(Ing, _), ")
	          .append("Veces is ceiling(CantNecesaria / CantPorCrafteo), ")
	          .append("puedo_craftear_n_veces(Ing, Veces)))");
	    new Query(reglas.toString()).hasSolution();

	    // Regla puedo_craftear_n_veces/2
	    reglas.setLength(0);
	    reglas.append("assertz((puedo_craftear_n_veces(Objeto, Veces) :- ")
	          .append("crafteable(Objeto, _), ")
	          .append("ingredientes_suficientes(Objeto, Veces)))");
	    new Query(reglas.toString()).hasSolution();
	}
	
	public static boolean puedoCraftear(Item item) {
		String nombre = item.getNombre().toLowerCase().trim();
		Query consulta = new Query(String.format("puedo_craftear(%s)", nombre));
		return consulta.hasSolution();
	}
	
	public static void limpiarBaseObjetos() {
		new Query("retractall(crafteable(_, _))").hasSolution(); // Reiniciar crafteables
		new Query("retractall(ingrediente(_, _,_))").hasSolution(); // Reiniciar ingredientes
		new Query("retractall(elemento_basico(_))").hasSolution(); // Reiniciar Ingredientes basicos
	}

}
