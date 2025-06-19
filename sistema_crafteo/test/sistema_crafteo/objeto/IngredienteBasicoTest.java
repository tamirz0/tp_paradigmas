package sistema_crafteo.objeto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class IngredienteBasicoTest {
	
	IngredienteBasico basico;
	
	@BeforeEach
	void setUp() {
		basico = new IngredienteBasico("Basico", "Descripcion");
	}
	
	@Test
	void getTiempoCrafteo_sinParametro_esCero() {
		int actual = basico.getTiempoCrafteo();
		assertEquals(0, actual);
	}
	

}
