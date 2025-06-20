package sistema_crafteo.objeto;

import java.util.HashMap;
import java.util.Map;

public class IngredienteBasico extends Item {

	public IngredienteBasico(String nombre, String descripcion) {
		super(nombre, descripcion);
	}

	@Override
	public int getTiempoCrafteo() {
		return 0;
	}

	@Override
	public int getTiempoCrafteoTotal() {
		return 0;
	}

	@Override
	public String toString() {
		return "Basico " + nombre;
	}
	
	@Override
	public Map<Item, Integer> getIngredientesBasicos(){
		Map<Item, Integer> ingrediente = new HashMap<>();
		ingrediente.put(this, 1);
		return ingrediente;
	}
	
	@Override
	public Map<Item, Integer> getIngredientesBasicos(int cantidadPedida){
		Map<Item, Integer> ingrediente = new HashMap<>();
		ingrediente.put(this, cantidadPedida);
		return ingrediente;
	}
	
}
