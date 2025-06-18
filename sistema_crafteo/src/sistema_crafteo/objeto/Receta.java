package sistema_crafteo.objeto;

import java.util.Map;

public class Receta {
	Map<Item, Integer> ingredientes;
	MesaDeTrabajo mesaRequerida;

	public Receta(Map<Item, Integer> ingredientes, MesaDeTrabajo mesaRequerida) {
		if (ingredientes == null) {
			throw new IllegalArgumentException("Lista de ingredientes nula en creacion receta");
		}

		this.ingredientes = ingredientes;
		this.mesaRequerida = mesaRequerida;
	}

	/*
	 * Util para cuando implementemos JSON public Receta(int cantidadProducida,
	 * MesaDeTrabajo mesaRequerida, Map<String, Integer> ingredientes) {
	 * 
	 * }
	 */

	public int getTiempoReceta() {
		int tiempo = 0;
		for (Map.Entry<Item, Integer> entrada : ingredientes.entrySet()) {
			Item key = entrada.getKey();
			Integer valor = entrada.getValue();
			tiempo += key.getTiempoCrafteo(valor);
		}
		tiempo += mesaRequerida.recetaCreacion.getTiempoReceta();
		return tiempo;
	}

}
