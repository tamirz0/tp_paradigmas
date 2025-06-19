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
			objeto = new ObjetoCrafteable("Objeto", "Descripcion", 5, 2, (Receta) null);
		}

		@Test
		void crearObjeto_objetoValido_creaCorrectamente() {
			assertEquals(5, objeto.getTiempoCrafteo());
			assertEquals(2, objeto.getCantidadProducida());
			assertNotNull(objeto.getRecetas());
		}

		@Test
		void crearObjeto_objetoInvalido_lanzaExcepcion() {
			Exception e = assertThrows(IllegalArgumentException.class, () -> {
				objeto = new ObjetoCrafteable("nombre", "desc", 0, 0, (Receta) null);
			});

			assertEquals("Parametros negativos o cero", e.getMessage());
		}
		
		@Test
		void getTiempoCrafteo_conParametroMayorIgualACantidadProducida_calculaCorrectamente() {
			int esperado = 20;
			int actual = objeto.getTiempoCrafteo(7);
			assertEquals(esperado, actual);
		}
		
		@Test
		void getTiempoCrafteo_conParametroMenorACantidadProducida_retorna1() {
			int esperado = 5;
			int actual = objeto.getTiempoCrafteo(1);
			assertEquals(esperado, actual);
		}
		
		@Test
		void getTiempoCrafteo_conParametroMenorIgualCero_retorna0() {
			int esperado = 0;
			int actual = objeto.getTiempoCrafteo(-10);
			assertEquals(esperado, actual);
		}
		
		@Test
		void esCrafteable_objeto_retornaTrue() {
			assertTrue(objeto.esCrafteable());
		}
		
	}
}
