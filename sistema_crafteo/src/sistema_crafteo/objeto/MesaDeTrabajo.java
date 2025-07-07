package sistema_crafteo.objeto;

import java.util.Objects;

public class MesaDeTrabajo { 
	private final String nombre;

	public String getNombre() {
		return nombre;
	}
	public MesaDeTrabajo(String nombre) {
		if(nombre == null) {
			throw new IllegalArgumentException("Parametro nulos");
		}
		
		this.nombre = nombre;
	}

	@Override
	public int hashCode() {
		return Objects.hash(nombre);
	}

	@Override
	public String toString() {
		return nombre;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MesaDeTrabajo other = (MesaDeTrabajo) obj;
		return Objects.equals(nombre, other.nombre);
	}

}
