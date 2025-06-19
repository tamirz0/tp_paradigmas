package sistema_crafteo.objeto;

public class MesaDeTrabajo{ //Se craftean instantaneamente
	private final String nombre;
	private final Receta recetaCreacion;
	
	public String getNombre() {
		return nombre;
	}

	public Receta getRecetaCreacion() {
		return recetaCreacion;
	}

	public MesaDeTrabajo(String nombre, Receta receta) {
		if(nombre == null || receta == null) {
			throw new IllegalArgumentException("Parametros nulos");
		}
		
		this.nombre = nombre;
		this.recetaCreacion = receta;
	}
	
	
}
