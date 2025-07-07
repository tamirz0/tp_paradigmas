package sistema_crafteo.objeto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

//nombreMetodo_EscenarioEsperado_ResultadoEsperado()

class ItemTest {

	IngredienteBasico item;

	@BeforeEach
	void setUp() {
		item = new IngredienteBasico("ItemTest", "Descripcion");
	}

	@Test
	void crearItem_itemValido_creaCorrectamente() {
		String nombre = "ItemTest";
		String descripcion = "Descripcion";
		assertEquals(nombre, item.getNombre());
		assertEquals(descripcion, item.getDescripcion());
	}

	@Test
	void crearItem_itemInvalido_lanzaExcepcion() {
		Exception e = assertThrows(IllegalArgumentException.class, () ->{
		
			item = new IngredienteBasico(null, null);
		
		});
		assertEquals("Parametro nulo", e.getMessage());
	}
	
	@Test
	void getTiempoCrafteo_sinParametros_retornaValorCorrecto() {
		int esperado = 0;
		int actual = item.getTiempoCrafteo();
		assertEquals(esperado, actual);
	}
	
	@Test
	void getTiempoCrafteoTotal_itemValido_retornaValorCorrecto() {
		int esperado = 0;
		int actual = item.getTiempoCrafteoTotal();
		assertEquals(esperado, actual);
	}
	
	@Test
	void esCrafteable_default_retornaFalse() {
		assertFalse(item.esCrafteable());
	}
	
	@Test
	void getArbolCrafteo_default_retornaNombre() {
		String esperado = item.getNombre();
		String actual = item.getArbolCrafteoBasicos();
		assertEquals(esperado, actual);
	}
	
}
