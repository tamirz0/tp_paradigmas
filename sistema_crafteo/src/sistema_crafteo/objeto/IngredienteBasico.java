package sistema_crafteo.objeto;

public class IngredienteBasico extends Item {
	int tiempoCrafteo;

	public IngredienteBasico(String nombre, String descripcion, int tiempoCrafteo) {
		super(nombre, descripcion);
		if (tiempoCrafteo < 0) {
			throw new IllegalArgumentException("Tiempo de crafteo menor a cero");
		}

		this.tiempoCrafteo = tiempoCrafteo;
	}

	@Override
	public int getTiempoCrafteo() {
		return tiempoCrafteo;
	}
}
