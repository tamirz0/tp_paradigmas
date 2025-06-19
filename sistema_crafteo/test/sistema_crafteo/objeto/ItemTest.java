package sistema_crafteo.objeto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

//nombreMetodo_EscenarioEsperado_ResultadoEsperado()

class ItemTest {

	class ItemPrueba extends Item {
		public ItemPrueba(String nombre, String descripcion) {
			super(nombre, descripcion);
		}

		@Override
		public int getTiempoCrafteo() {
			return 0;
		}

		@Override
		public int getTiempoCrafteo(int cantidadUnidades) {
			// TODO Auto-generated method stub
			return 1;
		}

	}

	ItemPrueba item;

	@BeforeEach
	void setUp() {
		item = new ItemPrueba("ItemTest", "Descripcion");
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
		
			item = new ItemPrueba(null, null);
		
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
	void getTiempoCrafteo_conParametros_retornaValorCorrecto() {
		int esperado = 1;
		int actual = item.getTiempoCrafteo(100);
		assertEquals(esperado, actual);
	}
	
	@Test
	void esCrafteable_default_retornaFalse() {
		assertFalse(item.esCrafteable());
	}
	
	@Test
	void getArbolCrafteo_default_retornaNombre() {
		String esperado = item.getNombre();
		String actual = item.getArbolCrafteo();
		assertEquals(esperado, actual);
	}
	
}
