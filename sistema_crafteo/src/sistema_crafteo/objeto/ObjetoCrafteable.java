package sistema_crafteo.objeto;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ObjetoCrafteable extends Item {
	private final List<Receta> recetas;
	private final int tiempoCrafteo;
	private final int cantidadProducida;

	public ObjetoCrafteable(String nombre, String descripcion, int tiempoCrafteo, int cantidadProducida,
			Receta... recetas) {
		super(nombre, descripcion);

		if (cantidadProducida <= 0 || tiempoCrafteo <= 0) {
			throw new IllegalArgumentException("Parametros negativos o cero");
		}

		this.recetas = new ArrayList<>();
		for (Receta receta : recetas) {
			this.recetas.add(receta);
		}

		this.cantidadProducida = cantidadProducida;
		this.tiempoCrafteo = tiempoCrafteo;

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
		if (cantidadUnidades <= 0) {
			return 0;
		}

		int cantidadCrafteos;
		if (cantidadUnidades < cantidadProducida) {
			cantidadCrafteos = 1;
		} else {
			int res = cantidadUnidades / cantidadProducida;
			cantidadCrafteos = cantidadUnidades % cantidadProducida == 0 ? res : res + 1;
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

	public List<Receta> getRecetas() {
		return recetas;
	}

	public int getCantidadProducida() {
		return cantidadProducida;
	}

}
