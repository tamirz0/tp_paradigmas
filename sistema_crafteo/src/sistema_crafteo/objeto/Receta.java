package sistema_crafteo.objeto;

import java.util.Map;

public class Receta {
	Map<Item, Integer> ingredientes;
	int cantidadProducida;
	MesaDeTrabajo mesaRequerida;

	public Receta(Map<Item, Integer> ingredientes, int cantidadProducida, MesaDeTrabajo mesaRequerida) {
		if (ingredientes == null) {
			throw new IllegalArgumentException("Lista de ingredientes nula en creacion receta");
		}
		if (cantidadProducida <= 0) {
			throw new IllegalArgumentException("Cantidad producida menor o igual a cero");
		}

		this.ingredientes = ingredientes;
		this.cantidadProducida = cantidadProducida;
		this.mesaRequerida = mesaRequerida;
	}

	/*
	 * Util para cuando implementemos JSON public Receta(int cantidadProducida,
	 * MesaDeTrabajo mesaRequerida, Map<String, Integer> ingredientes) {
	 * 
	 * }
	 */

}
