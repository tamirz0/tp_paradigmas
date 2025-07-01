package sistema_crafteo.objeto;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ObjetoCrafteable extends Item {
	private final List<Receta> recetas; // Cambiar en un futuro por un TreeSet ordenado por tiempo de receta
	private final int tiempoCrafteo;

	public ObjetoCrafteable(String nombre, String descripcion, int tiempoCrafteo, Receta... recetas) {
		super(nombre, descripcion);

		if (tiempoCrafteo <= 0) {
			throw new IllegalArgumentException("Parametros negativos o cero");
		}

		this.recetas = new LinkedList<>();
		for (Receta receta : recetas) {
			if (receta != null) {
				this.recetas.add(receta);
			}
		}
		this.tiempoCrafteo = tiempoCrafteo;

	}

	@Override
	public String toString() {
		return "Crafteable " + nombre;
	}

	@Override
	public boolean esCrafteable() {
		return true;
	}

	@Override
	public String getArbolCrafteo() {
		StringBuilder sb = new StringBuilder();
		armarArbol(this, 1, "", sb);
		return sb.toString();
	}

	private static void armarArbol(Item item, int cantidad, String indent, StringBuilder sb) {
        sb.append(indent)
        .append(cantidad)
        .append(" ")
        .append(item.getNombre())
        .append("\n");

		if (item.esCrafteable()) {
			for (Map.Entry<Item, Integer> e : item.getIngredientes().entrySet()) {
				armarArbol(e.getKey(), e.getValue() * cantidad, indent + "  ", sb);
			}
		}
	}

	public List<Receta> getRecetas() {
		return recetas;
	}

	@Override
	public int getTiempoCrafteo() {
		return tiempoCrafteo;
	}

	@Override
	public int getTiempoCrafteoTotal() { // utiliza la primer receta guardada
		return tiempoCrafteo + recetas.get(0).getTiempoReceta();
	}

	@Override
	public int getTiempoCrafteoTotal(int cantidadPedida) {
		if (recetas.isEmpty()) {
			throw new NullPointerException("El objeto no posee recetas");
		}
		if (cantidadPedida <= 0) {
			return 0;
		}

		int crafteos = cantidadCrafteos(cantidadPedida);
		return getTiempoCrafteoTotal() * crafteos;
	}

	@Override
	public Map<Item, Integer> getIngredientes() {
		return recetas.isEmpty() ? null : recetas.get(0).getIngredientes();
	}

	@Override
	public List<Map<Item, Integer>> getIngredientesTodos() {
		List<Map<Item, Integer>> ingredientesPorReceta = new LinkedList<>();
		for (Receta receta : recetas) {
			ingredientesPorReceta.add(receta.getIngredientes());
		}
		return ingredientesPorReceta;
	}

	@Override
	public Map<Item, Integer> getIngredientesBasicos() {
		return recetas.isEmpty() ? null : recetas.get(0).getRecetasBasicas(1);
	}

	@Override
	protected Map<Item, Integer> getIngredientesBasicos(int cantidadUnidades) {
		int crafteos = cantidadCrafteos(cantidadUnidades);
		return recetas.isEmpty() ? null : recetas.get(0).getRecetasBasicas(crafteos);
	}

	@Override
	public List<Map<Item, Integer>> getIngredientesBasicosTodos() {
		List<Map<Item, Integer>> ingredientesPorReceta = new LinkedList<>();
		for (Receta receta : recetas) {
			ingredientesPorReceta.add(receta.getRecetasBasicas(1));
		}
		return ingredientesPorReceta;
	}

	@Override
	public int cantidadCrafteos(int cantidadPedida) {
		if (recetas.isEmpty()) {
			return 0;
		}

		if (cantidadPedida <= 0) {
			return 0;
		}

		int cantidadGenerada = recetas.get(0).getCantidadGenerada();
		int crafteos;

		if (cantidadPedida > cantidadGenerada) {
			int div = cantidadPedida / cantidadGenerada;
			crafteos = cantidadPedida % cantidadGenerada == 0 ? div : div + 1;
		} else {
			crafteos = 1;
		}

		return crafteos;
	}

	@Override
	public int getCantidadGenerada() {
		return this.recetas.get(0).getCantidadGenerada();
	}
	
	@Override
	public Receta getReceta() {
		return recetas.get(0);
	}
	
	@Override
	public List<Receta> getRecetaTodo(){
		return recetas;
	}
}
