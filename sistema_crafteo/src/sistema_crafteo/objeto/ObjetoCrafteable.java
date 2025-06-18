package sistema_crafteo.objeto;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ObjetoCrafteable extends Item {
	List<Receta> recetas;
	int tiempoCrafteo;
	int cantidadProducida;

	public ObjetoCrafteable(String nombre, String descripcion, int tiempoCrafteo, Receta... recetas) {
		super(nombre, descripcion);
		this.recetas = new ArrayList<>();
		for (Receta receta : recetas) {
			this.recetas.add(receta);
		}

	}

	public List<Integer> getTiempoCrafteoTotal() {
		List<Integer> tiemposPorReceta = new LinkedList<>();

		for (Receta receta : recetas) {
			int tiempoReceta = tiempoCrafteo + receta.getTiempoReceta();
			tiemposPorReceta.add(tiempoReceta);
		}

		return tiemposPorReceta;
	}

	public int getTiempoCrafteo(int cantidadUnidades) {
		int cantidadCrafteos;
		if (cantidadUnidades < cantidadProducida) {
			cantidadCrafteos = 0;
		} else {
			cantidadCrafteos = cantidadUnidades / cantidadProducida;
		}
		return cantidadCrafteos * tiempoCrafteo;
	}

	@Override
	public int getTiempoCrafteo() {
		return tiempoCrafteo;
	}

	@Override
	public boolean esCrafteable() {
		return true;
	}

	@Override
	public String getArbolCrafteo() {
		return "a";
	}

}
