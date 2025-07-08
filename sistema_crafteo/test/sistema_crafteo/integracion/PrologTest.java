package sistema_crafteo.integracion;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jpl7.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sistema_crafteo.inventario.Inventario;
import sistema_crafteo.logica.SistemaCrafteo;
import sistema_crafteo.objeto.IngredienteBasico;
import sistema_crafteo.objeto.Item;
import sistema_crafteo.objeto.ObjetoCrafteable;
import sistema_crafteo.objeto.Receta;

class PrologTest {

	SistemaCrafteo sistema;
	Inventario inventario;
	Set<Item> items;

	@BeforeEach
	void setup() {
		sistema = new SistemaCrafteo();
		ObjetoCrafteable espada = getEspada();
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

		inventario = new Inventario();
		inventario.setItems(itemsMiInventario);
		items = new HashSet<>();
		items.add(espada);
		items.addAll(getIngredientesALista(espada));
		items.addAll(getIngredientesBasicosALista(espada));
		sistema.registrarItem(roca);
		sistema.registrarItem(metal);
		sistema.registrarItem(madera);
		sistema.registrarItem(piedra);
		sistema.registrarItem(palo);
		sistema.registrarItem(espada);
	}
	
	@Test
	void cargarReglas_reglasValidas_cargaCorrectamente() {
		Prolog.cargarReglas();
		Prolog.cargarInventario(inventario);
		Prolog.cargarItem(getEspada());
		Query query = new Query("puedo_craftear(espada)");
		assertTrue(query.hasSolution());
	}

	@Test
	void cargarInventario_inventarioValido_cargaCorrectamente() {
		assertTrue(Prolog.cargarInventario(inventario));
		Query query = new Query("tengo(madera, 5)");
		assertTrue(query.hasSolution());
		query = new Query("tengo(roca, 10)");
		assertTrue(query.hasSolution());
		query = new Query("tengo(metal, 5)");
		assertTrue(query.hasSolution());
		query = new Query("tengo(palo, 7)");
		assertTrue(query.hasSolution());
		query = new Query("tengo(piedra, 3)");
		assertTrue(query.hasSolution());
	}
	
	@Test
	void cargarItem_itemComplejo_cargaCorrectamente() {
		Prolog.cargarItem(getEspada());
		Query query = new Query("crafteable(espada, 1)");
		assertTrue(query.hasSolution());
		query = new Query("ingrediente(espada, palo, 9, 0)");
		assertTrue(query.hasSolution());
		query = new Query("ingrediente(espada, piedra, 5, 0)");
		assertTrue(query.hasSolution());
		query = new Query("ingrediente(espada, metal, 2, 0)");
		assertTrue(query.hasSolution());

		query = new Query("ingrediente(espada, roca, 3, 1)");
		assertTrue(query.hasSolution());
		query = new Query("ingrediente(espada, metal, 3, 1)");
		assertTrue(query.hasSolution());

		query = new Query("crafteable(piedra, 2)");
		assertTrue(query.hasSolution());
		query = new Query("ingrediente(piedra, roca, 3, 0)");
		assertTrue(query.hasSolution());
		query = new Query("ingrediente(piedra, metal, 3, 0)");
		assertTrue(query.hasSolution());

		query = new Query("crafteable(palo, 3)");
		assertTrue(query.hasSolution());
		query = new Query("ingrediente(palo, madera, 2, 0)");
		assertTrue(query.hasSolution());

		query = new Query("elemento_basico(madera)");
		assertTrue(query.hasSolution());
		query = new Query("elemento_basico(roca)");
		assertTrue(query.hasSolution());
		query = new Query("elemento_basico(metal)");
		assertTrue(query.hasSolution());

		
	}
	
	@Test
	void limpiarBaseObjetos_baseConDatos_limpiaCorrectamente() {
		Prolog.cargarReglas();
		Prolog.cargarInventario(inventario);
		Prolog.cargarItem(getEspada());
		assertTrue(Prolog.puedoCraftear(getEspada()));
		Prolog.limpiarBaseObjetos();
		assertFalse(Prolog.puedoCraftear(getEspada()));
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

		ObjetoCrafteable espada = new ObjetoCrafteable("Espada", " ", 10, recetaEspada, recetaPiedra);
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

