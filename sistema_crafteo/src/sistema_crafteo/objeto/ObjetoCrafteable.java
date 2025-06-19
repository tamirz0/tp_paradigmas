package sistema_crafteo.objeto;

import java.util.LinkedList;
import java.util.List;

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
			this.recetas.add(receta);
		}
		this.tiempoCrafteo = tiempoCrafteo;

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

}
