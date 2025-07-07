package sistema_crafteo.objeto;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import sistema_crafteo.logica.OperacionesMap;

public class Receta {
	private final Map<Item, Integer> ingredientes;
	private final MesaDeTrabajo mesaRequerida;
	private int cantidadGenerada;
	
	public Receta() {
		ingredientes = new HashMap<Item, Integer>();
		mesaRequerida = null;
		cantidadGenerada = 1;
	}

	public Receta(Map<Item, Integer> ingredientes, MesaDeTrabajo mesaRequerida) {
		this(ingredientes, mesaRequerida, 1);
	}

	public Receta(Map<Item, Integer> ingredientes, MesaDeTrabajo mesaRequerida, int cantidadGenerada) {
		if (ingredientes == null) {
			throw new IllegalArgumentException("Parametro nulo");
		}
		if (ingredientes.isEmpty()) {
			throw new IllegalArgumentException("Sin ingredientes");
		}
		if (cantidadGenerada <= 0) {
			throw new IllegalArgumentException("Cantidad generada menor o igual a cero");
		}

		this.cantidadGenerada = cantidadGenerada;
		this.ingredientes = ingredientes;
		this.mesaRequerida = mesaRequerida;
	}
	
	public int getTiempoReceta() {
		int tiempo = 0;
		for (Map.Entry<Item, Integer> entrada : ingredientes.entrySet()) {
			Item key = entrada.getKey();
			Integer valor = entrada.getValue();
			tiempo += key.getTiempoCrafteoTotal(valor);
		}

		return tiempo;
	}

	public Map<Item, Integer> getIngredientes() {
		return ingredientes;
	}

	public MesaDeTrabajo getMesaRequerida() {
		return mesaRequerida;
	}

	public int getCantidadGenerada() {
		return this.cantidadGenerada;
	}

	public Map<Item, Integer> getRecetasBasicas(int crafteosNecesarios) {
		Map<Item, Integer> basicos = new HashMap<>();
		Map<Item, Integer> aux;

		for (Map.Entry<Item, Integer> entrada : ingredientes.entrySet()) {
			Item key = entrada.getKey();
			Integer valor = entrada.getValue();

			aux = key.getIngredientesBasicos(crafteosNecesarios * valor); // es lo mismo que hacer getRecetasBasicas
			OperacionesMap.sumarTodo(basicos, aux);
		}

		return basicos;
	}
	
	public Set<MesaDeTrabajo> getMesas(){
		Set<MesaDeTrabajo> ret = new HashSet<>();
		ret.add(mesaRequerida);
		for(Map.Entry<Item, Integer> entrada : ingredientes.entrySet()) {
			Item key = entrada.getKey();
			Receta recetaKey = key.getReceta();
			if(recetaKey != null) {
				ret.addAll(key.getReceta().getMesas());
			}
		}
		ret.remove(null);
		return ret;
	}

}
