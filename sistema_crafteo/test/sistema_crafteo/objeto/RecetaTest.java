package sistema_crafteo.objeto;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RecetaTest {

	@Nested
	class CreacionReceta {
		Receta receta;

		@BeforeEach
		void setup() {
			IngredienteBasico item = new IngredienteBasico("Prueba", "Descripcion");
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
        void crearReceta_conCantidadGeneradaPersonalizada_creaCorrectamente() {
            Map<Item,Integer> ing = receta.getIngredientes();
            Receta r2 = new Receta(new HashMap<>(ing), null, 5);
            assertEquals(5, r2.getCantidadGenerada());
        }
		
		@Test
		void crearReceta_conMesaDeTrabajo_creaCorrectamente() {
			MesaDeTrabajo mesa = new MesaDeTrabajo("Mesa");
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
			Map<Item, Integer> ing = receta.getIngredientes();
			Exception e = assertThrows(IllegalArgumentException.class, () -> {
				 receta = new Receta(ing, null, -1);
			});
			
			assertEquals("Cantidad generada menor o igual a cero", e.getMessage());
		}

	}
	
    @Nested
    class FuncionalidadReceta {

        Receta receta;
        IngredienteBasico a, b;
        MesaDeTrabajo m1, m2;

        @BeforeEach
        void setup() {
            a = new IngredienteBasico("A", "item A");
            b = new IngredienteBasico("B", "item B");
            Map<Item,Integer> ing = new HashMap<>();
            ing.put(a, 2);  
            ing.put(b, 1);  
            m1 = new MesaDeTrabajo("M1");
            receta = new Receta(ing, m1, 2);	//Rquiere 2 de A, 1 de B y la mesa M1, genera 2
        }

        @Test
        void getTiempoReceta_sumaTiemposCorrecto() {
            // Deberia ser 0 porque son todos básicos
            assertEquals(0, receta.getTiempoReceta());
        }

        @Test
        void getRecetasBasicas_unLote() {
            Map<Item,Integer> basicos = receta.getRecetasBasicas(1);
            assertEquals(2, basicos.get(a));
            assertEquals(1, basicos.get(b));
        }

        @Test
        void getRecetasBasicas_multiplesLotes() {
            Map<Item,Integer> basicos = receta.getRecetasBasicas(3);
            // 3 lotes: 3*2 A =6, 3*1 B=3
            assertEquals(6, basicos.get(a));
            assertEquals(3, basicos.get(b));
        }

        @Test
        void getMesas_incluyeMesaPropiaYSinRepetir() {
            // creamos sub-receta con mesa2
            Map<Item,Integer> subIng = Map.of(a,1);
            MesaDeTrabajo m2 = new MesaDeTrabajo("M2");
            Receta subRec = new Receta(new HashMap<>(subIng), m2, 10);
            ObjetoCrafteable comp = new ObjetoCrafteable("C", "item C", 5, subRec);

            // nueva receta anidada
            Map<Item,Integer> ing2 = Map.of(comp,1);
            Receta rec2 = new Receta(new HashMap<>(ing2), m1);

            Set<MesaDeTrabajo> mesas = rec2.getMesas();
            assertEquals(2, mesas.size());
            assertTrue(mesas.contains(m1));
            assertTrue(mesas.contains(m2));
        }
        
        @Test
        void getTiempoReceta_recetaCompuesta_retornaCorrecto() {
        	// creamos una nueva receta compuesta, misma receta devuelve 2 unidades
        	ObjetoCrafteable comp = new ObjetoCrafteable("C", "item C", 5, receta);	// Tarda 5 segundos
        	ObjetoCrafteable comp2 = new ObjetoCrafteable("D", "item D", 2, receta);
            Map<Item,Integer> ing2 = new HashMap<>();
            ing2.put(a, 20);  
            ing2.put(comp, 6);
            ing2.put(comp2, 3);
        	Receta recetaCompuesta = new Receta(ing2, null, 10);
        	
        	// Para la receta necesito 3 crafteos de comp (que tardan 5 cada uno = 15 segundos)
        	// Despues necesito 2 crafteos de comp2 (que tardan 2 segundos cada uno = 4 segundos) 
        	// Total = 19 segundos
        	assertEquals(19, recetaCompuesta.getTiempoReceta());
        }

    }

}
