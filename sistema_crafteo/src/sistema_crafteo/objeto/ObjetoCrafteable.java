package sistema_crafteo.objeto;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ObjetoCrafteable extends Item {
	private final List<Receta> recetas; 		// Cambiar en un futuro por un TreeSet ordenado por tiempo de receta
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
    public String getArbolCrafteo(int numReceta) {
        StringBuilder sb = new StringBuilder();
        sb.append("|- " + getCantidadGenerada(numReceta) + " ")
          .append(this.getNombre() + "\n");
     
        Map<Item,Integer> ingr = getIngredientes(numReceta);
        for (Map.Entry<Item,Integer> e : ingr.entrySet()) {
            sb.append("|- |- ")
              .append(e.getValue() + " " + e.getKey().getNombre() + "\n");
        }
        return sb.toString();
    }
	
	@Override
	public String getArbolCrafteoBasicos(int numReceta) {
		StringBuilder sb = new StringBuilder();
		armarArbol(this, numReceta, 1, "", sb);
		return sb.toString();
	}

	private static void armarArbol(Item item, int numReceta, int cantidad, String indent, StringBuilder sb) {
	    sb.append(indent)
	      .append("|- ")
	      .append(cantidad)
	      .append(" ")
	      .append(item.getNombre())
	      .append("\n");

	    if (item.esCrafteable()) {
	        for (Map.Entry<Item, Integer> e : item.getIngredientes(numReceta).entrySet()) {
	            armarArbol(e.getKey(), 1, e.getValue() * cantidad, indent + "|  ", sb);
	        }
	    }
	}
	
	@Override
	public List<Receta> getRecetas() {
		return recetas;
	}

	@Override
	public int getTiempoCrafteo() {
		return tiempoCrafteo;
	}

	@Override
	public int getTiempoCrafteoTotal() { 			// Teniendo en cuenta la receta default
		return tiempoCrafteo + recetas.get(0).getTiempoReceta();
	}
	
	//////////////////////
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
	//////////////////////

	/* Los ingredientes de la receta default */
	@Override
	public Map<Item, Integer> getIngredientes() {
		return getIngredientes(0);
	}
	
	/* Los ingredientes de la receta indicada */
	@Override
	public Map<Item, Integer> getIngredientes(int numReceta) {
		return recetas.isEmpty() ? null : recetas.get(numReceta).getIngredientes();
	}

	/* Los ingredientes de todas las recetas */
	@Override
	public List<Map<Item, Integer>> getIngredientesTodos() {
		List<Map<Item, Integer>> ingredientesPorReceta = new LinkedList<>();
		for (Receta receta : recetas) {
			ingredientesPorReceta.add(receta.getIngredientes());
		}
		return ingredientesPorReceta;
	}

	/* Los ingredientes básicos de la receta indicada */
	@Override
	public Map<Item, Integer> getIngredientesBasicos() {
		return recetas.isEmpty() ? null : recetas.get(0).getRecetasBasicas(1);
	}

	@Override
	protected Map<Item, Integer> getIngredientesBasicos(int cantidadUnidades) {
		int crafteos = cantidadCrafteos(cantidadUnidades);
		return recetas.isEmpty() ? null : recetas.get(0).getRecetasBasicas(crafteos);
	}

	/* Cantidad de crafteos necesarios para X unidades */
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
	public int getCantidadGenerada(int numReceta) {
		return this.recetas.get(numReceta).getCantidadGenerada();
	}
	
	@Override
	public Receta getReceta() {
		return getReceta(0);
	}
	
	@Override
	public Receta getReceta(int numReceta) {
		return recetas.get(numReceta);
	}
	
	@Override
	public List<Receta> getRecetaTodo(){
		return recetas;
	}
}
