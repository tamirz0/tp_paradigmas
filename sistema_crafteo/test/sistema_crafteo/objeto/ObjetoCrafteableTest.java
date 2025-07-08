package sistema_crafteo.objeto;

import static org.junit.Assert.assertSame;
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
		
		@Test
		void toString_objeto_muestraNombreCorrecto() {
			assertEquals("Crafteable Objeto", objeto.toString());
		}
		
	}
	
	
	@Nested
	class InteraccionObjeto{
        IngredienteBasico a, b;
        Receta r1, r2;
        ObjetoCrafteable o;

        @BeforeEach
        void setUp() {
            a = new IngredienteBasico("A","");  
            b = new IngredienteBasico("B","");
            // receta 0: 2 A genera 1
            Map<Item,Integer> m1 = Map.of(a,2);
            r1 = new Receta(new HashMap<>(m1), null, 1);
            // receta 1: 3 B genera 2
            Map<Item,Integer> m2 = Map.of(b,3);
            r2 = new Receta(new HashMap<>(m2), null, 2);
            o = new ObjetoCrafteable("X","",5, r1, r2);
        }
        
        @Test
        void getRecetas_retornLas2() {
            List<Receta> listas = o.getRecetas();
            assertEquals(2, listas.size());
            assertSame(r1, listas.get(0));
            assertSame(r2, listas.get(1));
        }
        
        @Test
        void getIngredientesTodos_contieneAmbas() {
        	Map<Item,Integer> m1 = Map.of(a,2);
        	Map<Item,Integer> m2 = Map.of(b,3);
            List<Map<Item,Integer>> todos = o.getIngredientesTodos();
            assertTrue(todos.contains(m1));
            assertTrue(todos.contains(m2));
        }
        
        @Test
        //@Disabled
        void cantidadCrafteos_calculaCorrecto() {
            // r1 genera 1 → para 5 unidades necesita 5 crafteos
            assertEquals(5, o.cantidadCrafteos(5));
        }
        
        @Test
        void getArbolCrafteo_muestraCorrecto() {
            String ar = o.getArbolCrafteo(1);
            // debe contener "2 B" anidado
            assertTrue(ar.contains("|- 2 X"));
            assertTrue(ar.contains("|- |- 3 B"));
        }

        @Test
        void getArbolCrafteoBasicos_primerNivel() {
            String ar = o.getArbolCrafteoBasicos(0);
            // solo nivel 1 de r1: "2 A"
            assertTrue(ar.contains("|- 1 X"));
            assertTrue(ar.contains("|  |- 2 A"));
        }
        
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
			
			List<Map<Item,Integer>> ingredientes = o2.getIngredientesTodos();
			
			assertTrue(o1.getIngredientesTodos().contains(ing1));
			assertTrue(ingredientes.contains(ing1) && ingredientes.contains(ing2));
		}
		
	}
}
