package sistema_crafteo.objeto;

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

	public String getNombre() {
		return nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}
	
	public Map<Item, Integer> getIngredientesBasicos(){
		return null;
	}
	
	protected Map<Item, Integer> getIngredientesBasicos(int n){
		return null;
	}
	
	public int cantidadCrafteos(int n) {
		return 1;
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
