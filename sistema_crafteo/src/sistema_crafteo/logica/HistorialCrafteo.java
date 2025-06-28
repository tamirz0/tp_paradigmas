package sistema_crafteo.logica;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import sistema_crafteo.objeto.Item;

public class HistorialCrafteo {
	private final List<Registro> registros;
	
	public HistorialCrafteo() {
		registros = new LinkedList<>();
	}
	
	public void registrarCrafteo(Item item, Map<Item, Integer> ingredientes) {
		registros.add(new Registro(item, ingredientes));
	}
	
	public List<Registro> getRegistros(){
		return registros;
	}
}
