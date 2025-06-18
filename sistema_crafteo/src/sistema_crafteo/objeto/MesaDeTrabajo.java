package sistema_crafteo.objeto;

public class MesaDeTrabajo{
	String nombre;
	Receta recetaCreacion;
	
	public MesaDeTrabajo(String nombre, Receta receta) {
		if(nombre == null || receta == null) {
			throw new IllegalArgumentException("Parametros nulos");
		}
		
		this.nombre = nombre;
		this.recetaCreacion = receta;
	}
	
	
}
