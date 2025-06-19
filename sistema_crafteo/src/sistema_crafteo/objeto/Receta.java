package sistema_crafteo.objeto;

import java.util.HashMap;
import java.util.Map;

public class Receta {
	private final Map<Item, Integer> ingredientes;
	private MesaDeTrabajo mesaRequerida;

	public Receta(Map<Item, Integer> ingredientes, MesaDeTrabajo mesaRequerida) {
		if (ingredientes == null) {
			throw new IllegalArgumentException("Parametro nulo");
		}
		if (ingredientes.isEmpty()) {
			throw new IllegalArgumentException("Sin ingredientes");
		}

		this.ingredientes = ingredientes;
		this.mesaRequerida = mesaRequerida;
	}
	
	public Receta() {
		ingredientes = new HashMap<Item, Integer>();
		mesaRequerida = null;
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
			tiempo += key.getTiempoCrafteoTotal() * valor;
		}

		if (mesaRequerida != null) {
			tiempo += mesaRequerida.getRecetaCreacion().getTiempoReceta();
		}

		return tiempo;
	}

	public Map<Item, Integer> getIngredientes() {
		return ingredientes;
	}

	public MesaDeTrabajo getMesaRequerida() {
		return mesaRequerida;
	}

}
