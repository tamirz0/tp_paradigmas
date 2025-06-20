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
			if(receta != null) {
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
		return "a";
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
	public int getTiempoCrafteoTotal(int n) {
		if(recetas.isEmpty()) {
			throw new NullPointerException("El objeto no posee recetas");
		}
		
		if(n <= 0) {
			return 0;
		}
		
		int cantidadGenerada = recetas.get(0).getCantidadGenerada();
		int crafteos;
		
		if(n > cantidadGenerada) {
			int div = n / cantidadGenerada;
			crafteos = n % cantidadGenerada == 0 ? div : div + 1;
		}
		else{
			crafteos = 1;
		}
		
		
		return getTiempoCrafteoTotal() * crafteos;
	}
	
	public List<Map<Item,Integer>> getIngredientes() {
		List<Map<Item, Integer>> ingredientesPorReceta = new LinkedList<>();
		for (Receta receta : recetas) {
			ingredientesPorReceta.add(receta.getIngredientes());
		}
		return ingredientesPorReceta;
	}
	
}
