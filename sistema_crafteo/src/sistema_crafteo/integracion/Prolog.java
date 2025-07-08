package sistema_crafteo.integracion;

import java.util.Map;

import org.jpl7.Query;

import sistema_crafteo.inventario.Inventario;
import sistema_crafteo.objeto.Item;

public class Prolog {



	public static boolean cargarInventario(Inventario inventario) {
		// Cargar todo el inventario de una vez para optimizar
		StringBuilder hechos = new StringBuilder();
		for (Map.Entry<Item, Integer> entrada : inventario.getItems().entrySet()) {
			Item item = entrada.getKey();
			Integer cantidad = entrada.getValue();
			String nombre = item.getNombre().toLowerCase().trim().replace(" ", "_");
			hechos.append(String.format("assertz(tengo(%s, %d)), ", nombre, cantidad));
		}
		
		if (hechos.length() > 0) {
			hechos.setLength(hechos.length() - 2); // Remover la última coma y espacio
			return new Query(hechos.toString()).hasSolution();
		}
		return true;
	}

	public static boolean cargarItem(Item item) {
		if (item.esCrafteable()) {
			// Cargar todas las recetas del item
			boolean resultado = false;
			for (int i = 0; i < item.getRecetas().size(); i++) {
				resultado = cargarObjetoCrafteable(item, i) || resultado;
			}
			return resultado;
		}
		return cargarElementoBasico(item);
	}

	private static boolean cargarElementoBasico(Item item) {
	    String nombre = item.getNombre().trim().toLowerCase().replace(" ", "_");

	    // Agregar el hecho - siempre agregar, no verificar existencia
	    String hecho = String.format("assertz(elemento_basico(%s)).", nombre);
	    Query query = new Query(hecho);
	    return query.hasSolution();
	}

	public static boolean cargarObjetoCrafteable(Item item, int nroReceta) {
	    String nombre = item.getNombre().trim().toLowerCase().replace(" ", "_");

	    // Agregar crafteable(nombre, cantidad) - siempre agregar, no verificar existencia
	    String hecho = String.format("assertz(crafteable(%s, %d)).", nombre, item.getCantidadGenerada(0));
	    Query query = new Query(hecho);

	    // Cargar ingredientes (se verifica existencia dentro de cargarItem)
	    for (Map.Entry<Item, Integer> entrada : item.getReceta(nroReceta).getIngredientes().entrySet()) {
	        Item ingrediente = entrada.getKey();
	        Integer cantidad = entrada.getValue();
	        cargarItem(ingrediente);
	        cargarIngrediente(item, ingrediente, cantidad, nroReceta);
	    }

	    return query.hasSolution();
	}

	private static boolean cargarIngrediente(Item padre, Item hijo, int cantidad, int numeroReceta) {
	    String nombrePadre = padre.getNombre().trim().toLowerCase().replace(" ", "_");
	    String nombreHijo = hijo.getNombre().trim().toLowerCase().replace(" ", "_");

	    // Agregar el hecho - siempre agregar, no verificar existencia
	    String hecho = String.format("assertz(ingrediente(%s, %s, %d, %d)).", nombrePadre, nombreHijo, cantidad, numeroReceta);
	    return new Query(hecho).hasSolution();
	}

	public static void cargarReglas() {
	    // Cargar todas las reglas de una vez para optimizar
	    String reglas = 
	        "assertz((puedo_craftear(Objeto) :- crafteable(Objeto, _), puedo_craftear_receta(Objeto, _))), " +
	        "assertz((puedo_craftear_receta(Objeto, Receta) :- ingrediente(Objeto, _, _, Receta), ingredientes_suficientes_receta(Objeto, Receta, 1))), " +
	        "assertz((ingredientes_suficientes_receta(Objeto, Receta, N) :- findall((Ing, Cant), ingrediente(Objeto, Ing, Cant, Receta), Ingredientes), verificar_ingredientes(Ingredientes, N))), " +
	        "assertz((verificar_ingredientes([], _))), " +
	        "assertz((verificar_ingredientes([(Ing, Cant)|T], Veces) :- CantTotal is Cant * Veces, disponible(Ing, CantTotal), verificar_ingredientes(T, Veces))), " +
	        "assertz((disponible(Ing, CantNecesaria) :- tengo(Ing, CantTengo), CantTengo >= CantNecesaria)), " +
	        "assertz((disponible(Ing, CantNecesaria) :- crafteable(Ing, CantPorCrafteo), Veces is ceiling(CantNecesaria / CantPorCrafteo), puedo_craftear_n_veces(Ing, Veces))), " +
	        "assertz((puedo_craftear_n_veces(Objeto, Veces) :- crafteable(Objeto, _), puedo_craftear_receta(Objeto, _)))";
	    
	    new Query(reglas).hasSolution();
	}
	
	public static boolean puedoCraftear(Item item) {
		String nombre = item.getNombre().toLowerCase().trim().replace(" ", "_");
		Query consulta = new Query(String.format("puedo_craftear(%s)", nombre));
		return consulta.hasSolution();
	}
	
	public static void limpiarBaseObjetos() {
		// Limpiar todo de una vez para optimizar
		new Query("retractall(crafteable(_, _))").hasSolution();
		new Query("retractall(ingrediente(_, _, _, _))").hasSolution();
		new Query("retractall(elemento_basico(_))").hasSolution();
		new Query("retractall(tengo(_, _))").hasSolution();
	}



}
