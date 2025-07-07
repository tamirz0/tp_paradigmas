package sistema_crafteo.objeto;

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
	public int getTiempoCrafteoTotal(int n) {
		return 0;
	}
	
	@Override
	public String toString() {
		return "Basico " + nombre;
	}
}
