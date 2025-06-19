package sistema_crafteo.objeto;

public abstract class Item {
	private final String nombre;
	private final String descripcion;

	public Item(String nombre, String descripcion) {
		if (nombre == null || descripcion == null) {
			throw new IllegalArgumentException("Parametro nulo");
		}
		this.nombre = nombre;
		this.descripcion = descripcion;
	}

	public abstract int getTiempoCrafteo();
	public abstract int getTiempoCrafteoTotal();
	

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

}
