package sistema_crafteo.objeto;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class Item {
	protected final String nombre;
	protected final String descripcion;

	public Item(String nombre, String descripcion) {
		if (nombre == null || descripcion == null) {
			throw new IllegalArgumentException("Parametro nulo");
		}
		this.nombre = nombre;
		this.descripcion = descripcion;
	}

	public abstract int getTiempoCrafteo();
	public abstract int getTiempoCrafteoTotal();
	
	public int getTiempoCrafteoTotal(int n) {
		return 0;
	}

	public boolean esCrafteable() {
		return false; // Por defecto NO es crafteable
	}
	
	public String getArbolCrafteo() {
		return this.nombre; // Por defecto se retorna a si mismo unicamente
	}
	
	public String getArbolCrafteo(int numReceta) {
		return this.nombre; // Por defecto se retorna a si mismo unicamente
	}
	
	public String getArbolCrafteoBasicos() {
		return this.nombre; // Por defecto se retorna a si mismo unicamente
	}
	
	public String getArbolCrafteoBasicos(int numReceta) {
		return this.nombre; // Por defecto se retorna a si mismo unicamente
	}

	public String getNombre() {
		return nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}
	
	public Map<Item, Integer> getIngredientes(){
		return null;
	}
	
	public Map<Item, Integer> getIngredientes(int numReceta){
		return null;
	}
	
	public List<Map<Item, Integer>> getIngredientesTodos(){
		return null;
	}
	
	public Map<Item, Integer> getIngredientesBasicos(){
		Map<Item, Integer> ingrediente = new HashMap<>();
		ingrediente.put(this, 1);
		return ingrediente;
	}
	
	protected Map<Item, Integer> getIngredientesBasicos(int cantidadPedida){
		Map<Item, Integer> ingrediente = new HashMap<>();
		ingrediente.put(this, cantidadPedida);
		return ingrediente;
	}
	
	public List<Map<Item, Integer>> getIngredientesBasicosTodos(){
		List<Map<Item, Integer>> lista = new LinkedList<>();
		lista.add(getIngredientesBasicos());
		return lista;
	}
	
	public int cantidadCrafteos(int cantidadPedida) {
		return 0;
	}
	
	public int getCantidadGenerada() {
		return 0;
	}
	
	public int getCantidadGenerada(int numReceta) {
		return 0;
	}
	
	public Receta getReceta() {
		return null;
	}
	
	public List<Receta> getRecetas() {
		return null;
	}
	
	
	public Receta getReceta(int numReceta) {
		return null;
	}
	
	public List<Receta> getRecetaTodo(){
		return null;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(nombre);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Item other = (Item) obj;
		return Objects.equals(nombre, other.nombre);
	}
	
	
}
