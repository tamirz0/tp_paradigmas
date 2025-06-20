package sistema_crafteo.objeto;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RecetaTest {

	@Nested
	class CreacionReceta {

		class ItemPrueba extends Item {

			public ItemPrueba(String nombre, String descripcion) {
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

		}

		Receta receta;

		@BeforeEach
		void setup() {
			ItemPrueba item = new ItemPrueba("Prueba", "Descripcion");
			Map<Item, Integer> ingredientes = new HashMap<Item, Integer>();
			ingredientes.put(item, 3);
			receta = new Receta(ingredientes, null);
		}

		@Test
		void crearReceta_recetaValida_creaCorrectamente() {
			assertNotNull(receta);
			assertNotNull(receta.getIngredientes());
			assertNull(receta.getMesaRequerida());
			Set<Item> items = receta.getIngredientes().keySet();
			assertFalse(items.isEmpty());
			for (Item item : items) {
				assertEquals("Prueba", item.getNombre());
			}
		}

		@Test
		void crearReceta_conMesaDeTrabajo_creaCorrectamente() {
			MesaDeTrabajo mesa = new MesaDeTrabajo("Mesa", receta);
			Receta receta2 = new Receta(receta.getIngredientes(), mesa);

			assertNotNull(receta2.getMesaRequerida());
		}

		@Test
		void crearReceta_parametroVacio_lanzaExcepcion() {
			Exception e = assertThrows(IllegalArgumentException.class, () -> {
				receta = new Receta(null, null);
			});
			assertEquals("Parametro nulo", e.getMessage());
		}
		
		@Test
		void crearReceta_ingredientesVacio_lanzaExcepcion() {
			Map<Item, Integer> aux = new HashMap<>();
			Exception e = assertThrows(IllegalArgumentException.class, () -> {
				receta = new Receta(aux, null);
			});
			
			assertEquals("Sin ingredientes", e.getMessage());
		}
		
		@Test
		void crearReceta_cantidadGeneradaNegativa_lanzaExcepcion() {
			
		}

	}

}
