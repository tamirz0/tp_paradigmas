package sistema_crafteo.objeto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ObjetoCrafteableTest {

	@Nested
	class CreacionObjeto {

		ObjetoCrafteable objeto;

		@BeforeEach
		void setup() {
			objeto = new ObjetoCrafteable("Objeto", "Descripcion", 5, (Receta) null);
		}

		@Test
		void crearObjeto_objetoValido_creaCorrectamente() {
			assertEquals(5, objeto.getTiempoCrafteo());
			assertNotNull(objeto.getRecetas());
		}

		@Test
		void crearObjeto_objetoInvalido_lanzaExcepcion() {
			Exception e = assertThrows(IllegalArgumentException.class, () -> {
				objeto = new ObjetoCrafteable("nombre", "desc", 0, (Receta) null);
			});

			assertEquals("Parametros negativos o cero", e.getMessage());
		}
		
		@Test
		void esCrafteable_objeto_retornaTrue() {
			assertTrue(objeto.esCrafteable());
		}
		
	}
}
