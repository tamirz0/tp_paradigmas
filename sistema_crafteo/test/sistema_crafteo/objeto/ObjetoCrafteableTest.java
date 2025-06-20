package sistema_crafteo.objeto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	
	@Nested
	class InteraccionObjeto{
		
		@Test
		void getIngredientes_objeto_retornoCorrecto() {
			Map<Item, Integer> ing1 = new HashMap<>();
			Map<Item, Integer> ing2 = new HashMap<>();
			
			ing1.put(new IngredienteBasico("Madera", " "), 2);
			
			Receta r1 = new Receta(ing1, null);
			
			ObjetoCrafteable o1 = new ObjetoCrafteable("Palo", " ", 1, r1);
			
			ing2.put(o1, 3);
			ing2.put(new IngredienteBasico("Carbon", " "), 10);
			
			Receta r2 = new Receta(ing2, null);
			
			ObjetoCrafteable o2 = new ObjetoCrafteable("Antorcha", " ", 1, r2, r1);
			
			List<Map<Item,Integer>> ingredientes = o2.getIngredientes();
			
			assertTrue(o1.getIngredientes().contains(ing1));
			assertTrue(ingredientes.contains(ing1) && ingredientes.contains(ing2));
		}
		
	}
}
