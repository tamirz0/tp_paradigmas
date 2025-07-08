package sistema_crafteo.logica;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import sistema_crafteo.inventario.Inventario;
import sistema_crafteo.objeto.IngredienteBasico;
import sistema_crafteo.objeto.Item;
import sistema_crafteo.objeto.ObjetoCrafteable;
import sistema_crafteo.objeto.Receta;

class SistemaCrafteoTest {

	@Nested
	class FaltantesCrearObjeto {
		SistemaCrafteo sistema = new SistemaCrafteo();
		ObjetoCrafteable espada = getEspada();
		Inventario miInventario = new Inventario();

		@BeforeEach
		void setup() {
			sistema.registrarItem(espada);
		}

		@Test
		void getIngredientesFaltantes_inventarioConMenosItemsQueReceta_retornaFaltantesCorrectos() {
			List<Item> ingredientesEspada = getIngredientesALista(espada);
			Map<Item, Integer> itemsMiInventario = new HashMap<Item, Integer>();

			Item palo = ingredientesEspada.get(0);
			Item metal = ingredientesEspada.get(1);
			Item piedra = ingredientesEspada.get(2);
			itemsMiInventario.put(palo, 9); 
			itemsMiInventario.put(metal, 5); 
			itemsMiInventario.put(piedra, 2); 

			sistema.registrarItem(espada);
			sistema.registrarItem(new IngredienteBasico("Prueba", " "));

			Inventario miInventario = new Inventario();
			miInventario.setItems(itemsMiInventario);

			int esperado = 3;
			int actual = sistema.getFaltantesNivel1Minimos(espada, miInventario).getOrDefault(piedra, 0);
			assertEquals(esperado, actual);
			assertNull(sistema.getFaltantesNivel1Minimos(espada, miInventario).get(palo));
			assertNull(sistema.getFaltantesNivel1Minimos(espada, miInventario).get(metal));
		}

		@Test
		void getIngredientesFaltantes_inventarioConMasItemsQueReceta_retornaMapVacio() {
			List<Item> ingredientesEspada = getIngredientesALista(espada);
			Map<Item, Integer> itemsMiInventario = new HashMap<Item, Integer>();

			Item palo = ingredientesEspada.get(0);
			Item metal = ingredientesEspada.get(1);
			Item piedra = ingredientesEspada.get(2);
			itemsMiInventario.put(palo, 99); // Palo
			itemsMiInventario.put(metal, 99); // Metal
			itemsMiInventario.put(piedra, 99); // Piedra

			miInventario.setItems(itemsMiInventario);

			boolean actual = sistema.getFaltantesNivel1Minimos(espada, miInventario).isEmpty();
			assertTrue(actual);
		}

		@Test
		void getIngredientesFaltantes_itemNoCrafteable_retornaNull() {
			IngredienteBasico a = new IngredienteBasico("prueba", "");
			assertNull(sistema.getFaltantesNivel1Minimos(a, miInventario));
		}

		@Test
		void getIngredientesBasicosFaltantes_inventarioConMenosItemsQueReceta_retornaMap() {
			List<Item> ingredientesEspada = getIngredientesBasicosALista(espada);
			Map<Item, Integer> itemsMiInventario = new HashMap<Item, Integer>();

			Item madera = ingredientesEspada.get(0);
			Item metal = ingredientesEspada.get(1);
			Item roca = ingredientesEspada.get(2);
			itemsMiInventario.put(madera, 3);
			itemsMiInventario.put(roca, 5);
			itemsMiInventario.put(metal, 15);

			miInventario.setItems(itemsMiInventario);

			Map<Item, Integer> actual = sistema.getBasicosFaltantesMinimos(espada, miInventario);
			assertFalse(actual.containsKey(metal));
			assertEquals(4, actual.get(roca));
			assertEquals(3, actual.get(madera));
		}

		@Test
		void getIngredientesBasicosFaltantes_inventarioConMasItemsQueReceta_retornaMapVacio() {
			List<Item> ingredientesEspada = getIngredientesBasicosALista(espada);
			Map<Item, Integer> itemsMiInventario = new HashMap<Item, Integer>();

			Item madera = ingredientesEspada.get(0);
			Item metal = ingredientesEspada.get(1);
			Item roca = ingredientesEspada.get(2);
			itemsMiInventario.put(madera, 99);
			itemsMiInventario.put(roca, 99);
			itemsMiInventario.put(metal, 99);

			miInventario.setItems(itemsMiInventario);

			Map<Item, Integer> actual = sistema.getBasicosFaltantesMinimos(espada, miInventario);
			assertTrue(actual.isEmpty());
		}

		@Test
		void getIngredientesBasicosFaltantes_inventarioConMenosCrafteablesPeroMasBasicos_retornaBasicosNecesarios() {
			List<Item> ingredientesEspada = getIngredientesALista(espada);
			List<Item> ingredientesBasicosEspada = getIngredientesBasicosALista(espada);
			Map<Item, Integer> itemsMiInventario = new HashMap<Item, Integer>();

			Item palo = ingredientesEspada.get(0);
			Item metal = ingredientesEspada.get(1);
			Item piedra = ingredientesEspada.get(2);
			Item madera = ingredientesBasicosEspada.get(0);
			Item roca = ingredientesBasicosEspada.get(2);

			itemsMiInventario.put(madera, 1);
			itemsMiInventario.put(roca, 1);
			itemsMiInventario.put(metal, 2);

			itemsMiInventario.put(palo, 8); // Palo
			itemsMiInventario.put(piedra, 4); // Piedra

			miInventario.setItems(itemsMiInventario);

			Map<Item, Integer> actual = sistema.getBasicosFaltantesMinimos(espada, miInventario);

			assertEquals(3, actual.get(metal));
			assertEquals(1, actual.get(madera));
			assertEquals(2, actual.get(roca));
		}
		
		@Test
		void getCantidadMaximaCrafteable_craftea1PorPrimerNivel1PorBasicos1PorPrimerNivel_retornaTres() {
			IngredienteBasico madera = new IngredienteBasico("Madera", " ");
			IngredienteBasico roca = new IngredienteBasico("Roca", " ");
			IngredienteBasico metal = new IngredienteBasico("Metal", " ");

			Receta recetaEspada;
			Receta recetaPalo;
			Receta recetaPiedra;

			Map<Item, Integer> ingredientes = new HashMap<>();

			ingredientes.put(madera, 2);

			recetaPalo = new Receta(ingredientes, null, 3);

			ingredientes = new HashMap<Item, Integer>();
			ingredientes.put(roca, 3);
			ingredientes.put(metal, 3);

			recetaPiedra = new Receta(ingredientes, null, 6);

			ObjetoCrafteable palo = new ObjetoCrafteable("Palo", " ", 5, recetaPalo);
			ObjetoCrafteable piedra = new ObjetoCrafteable("Piedra", " ", 10, recetaPiedra);

			ingredientes = new HashMap<Item, Integer>();
			ingredientes.put(metal, 2);
			ingredientes.put(piedra, 5);
			ingredientes.put(palo, 9);

			recetaEspada = new Receta(ingredientes, null, 1);

			ObjetoCrafteable espada2 = new ObjetoCrafteable("Espada", " ", 10, recetaEspada);
			Map<Item, Integer> itemsMiInventario = new HashMap<Item, Integer>();

			itemsMiInventario.put(madera, 10);
			itemsMiInventario.put(roca, 11);
			itemsMiInventario.put(metal, 10);

			itemsMiInventario.put(palo, 27); 
			itemsMiInventario.put(piedra, 9); 

			miInventario.setItems(itemsMiInventario);

			int actual = sistema.getCantidadMaxima(miInventario, espada2);
			assertEquals(3, actual);
		}
		
		@Test
		void getCantidadMaximaCrafteable_craftea1PorPrimerNivel1PorBasicos_retornaDos() {
			List<Item> ingredientesEspada = getIngredientesALista(espada);
			List<Item> ingredientesBasicosEspada = getIngredientesBasicosALista(espada);
			Map<Item, Integer> itemsMiInventario = new HashMap<Item, Integer>();

			Item palo = ingredientesEspada.get(0);
			Item metal = ingredientesEspada.get(1);
			Item piedra = ingredientesEspada.get(2);
			Item madera = ingredientesBasicosEspada.get(0);
			Item roca = ingredientesBasicosEspada.get(2);

			itemsMiInventario.put(madera, 10);
			itemsMiInventario.put(roca, 11);
			itemsMiInventario.put(metal, 8);

			itemsMiInventario.put(palo, 16); 
			itemsMiInventario.put(piedra, 9); 

			miInventario.setItems(itemsMiInventario);

			int actual = sistema.getCantidadMaxima(miInventario, espada);
			assertEquals(2, actual);
		}
		
		@Test
		void craftear_conIngredientesNecesariosPrimerNivel_crafteaCorrectamente() {
			List<Item> ingredientesEspada = getIngredientesALista(espada);
			Map<Item, Integer> itemsMiInventario = new HashMap<Item, Integer>();

			Item palo = ingredientesEspada.get(0);
			Item metal = ingredientesEspada.get(1);
			Item piedra = ingredientesEspada.get(2);
			
			itemsMiInventario.put(palo, 9);
			itemsMiInventario.put(piedra, 5);
			itemsMiInventario.put(metal, 2);
			
			miInventario.setItems(itemsMiInventario);

			
			sistema.craftear(miInventario, espada, 0);
			Map<Item, Integer> esperado = new HashMap<Item, Integer>();
			esperado.put(espada, 1);
			
			Map<Item, Integer> actual = miInventario.getItems();
			assertEquals(esperado, actual);
		}
		
		@Test
		void craftear_fabricandoIngredientesIntermedios_crafteaCorrectamente() {
			List<Item> ingredientesEspada = getIngredientesALista(espada);
			List<Item> ingredientesBasicosEspada = getIngredientesBasicosALista(espada);
			Map<Item, Integer> itemsMiInventario = new HashMap<Item, Integer>();

			Item palo = ingredientesEspada.get(0);
			Item metal = ingredientesEspada.get(1);
			Item piedra = ingredientesEspada.get(2);
			Item madera = ingredientesBasicosEspada.get(0);
			Item roca = ingredientesBasicosEspada.get(2);

			itemsMiInventario.put(madera, 5);
			itemsMiInventario.put(roca, 10);
			itemsMiInventario.put(metal, 5);

			itemsMiInventario.put(palo, 7); 
			itemsMiInventario.put(piedra, 3); 

			miInventario.setItems(itemsMiInventario);
			
			sistema.craftear(miInventario, espada, 0);
			Map<Item, Integer> actual = miInventario.getItems();
			Map<Item, Integer> esperado = new HashMap<Item, Integer>();
			esperado.put(madera, 3);
			esperado.put(roca, 7);
			esperado.put(palo, 1);
			esperado.put(espada, 1);
			
			assertEquals(esperado, actual);
		}
		
		private static ObjetoCrafteable getEspada() {

			IngredienteBasico madera = new IngredienteBasico("Madera", " ");
			IngredienteBasico roca = new IngredienteBasico("Roca", " ");
			IngredienteBasico metal = new IngredienteBasico("Metal", " ");

			Receta recetaEspada;
			Receta recetaPalo;
			Receta recetaPiedra;

			Map<Item, Integer> ingredientes = new HashMap<>();

			ingredientes.put(madera, 2);

			recetaPalo = new Receta(ingredientes, null, 3);

			ingredientes = new HashMap<Item, Integer>();
			ingredientes.put(roca, 3);
			ingredientes.put(metal, 3);

			recetaPiedra = new Receta(ingredientes, null, 2);

			ObjetoCrafteable palo = new ObjetoCrafteable("Palo", " ", 5, recetaPalo);
			ObjetoCrafteable piedra = new ObjetoCrafteable("Piedra", " ", 10, recetaPiedra);

			ingredientes = new HashMap<Item, Integer>();
			ingredientes.put(metal, 2);
			ingredientes.put(piedra, 5);
			ingredientes.put(palo, 9);

			recetaEspada = new Receta(ingredientes, null, 1);

			ObjetoCrafteable espada = new ObjetoCrafteable("Espada", " ", 10, recetaEspada);
			return espada;
		}

		private static List<Item> getIngredientesALista(ObjetoCrafteable obj) {
			List<Item> ingredientesEspada = new ArrayList<>(obj.getIngredientes().keySet());
			return ingredientesEspada;
		}

		private static List<Item> getIngredientesBasicosALista(ObjetoCrafteable obj) {
			List<Item> ingredientes = new ArrayList<>(obj.getIngredientesBasicos().keySet());
			return ingredientes;
		}

	}

}
