package sistema_crafteo.objeto;

import java.util.Objects;

public class MesaDeTrabajo { // Se craftean instantaneamente, solo acepta recetaCreacion con ingredientes
								// basicos. Por un tema de complejidad.
	private final String nombre;
	private final Receta recetaCreacion;

	public String getNombre() {
		return nombre;
	}

	public Receta getRecetaCreacion() {
		return recetaCreacion;
	}

	public MesaDeTrabajo(String nombre, Receta receta) {
		if (nombre == null || receta == null) {
			throw new IllegalArgumentException("Parametros nulos");
		}

		for (Item item : receta.getIngredientes().keySet()) {
			if (item.esCrafteable()) {
				throw new IllegalArgumentException("Mesa de trabajo solo puede ser creada con ingredientes basicos");
			}
		}

		this.nombre = nombre;
		this.recetaCreacion = receta;
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
		MesaDeTrabajo other = (MesaDeTrabajo) obj;
		return Objects.equals(nombre, other.nombre);
	}

}
