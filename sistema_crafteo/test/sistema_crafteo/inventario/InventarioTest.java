package sistema_crafteo.inventario;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sistema_crafteo.objeto.Item;
import sistema_crafteo.objeto.MesaDeTrabajo;
import sistema_crafteo.objeto.Receta;

class InventarioTest {

	class Recolectable extends Item {

		public Recolectable(String nombre, String descripcion) {
			super(nombre, descripcion);
		}

		@Override
		public int getTiempoCrafteo() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getTiempoCrafteoTotal() {
			// TODO Auto-generated method stub
			return 0;
		}

	}

	class Crafteable extends Item {

		public Crafteable(String nombre, String descripcion) {
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

		@Override
		public boolean esCrafteable() {
			return true;
		}

	}

	Inventario inventario;

	@BeforeEach
	void setup() {
		inventario = new Inventario();
	}

	@Test
	void crearInventario_inventarioValido_creaCorrectamente() {
		assertNotNull(inventario);
	}

	@Test
	void recolectarItem_itemNoCrafteableNuevo_agregaCorrectamente() {
		Recolectable itemRecolectable = new Recolectable("prueba", "");
		assertFalse(inventario.getItems().containsKey(itemRecolectable));
		inventario.recolectarItem(itemRecolectable, 1);
		assertTrue(inventario.getItems().containsKey(itemRecolectable));
	}

	@Test
	void recolectarItem_itemNoCrafteableExistente_agregaCorrectamente() {
		Recolectable itemRecolectable = new Recolectable("prueba", " ");
		inventario.recolectarItem(itemRecolectable, 5);

		inventario.recolectarItem(itemRecolectable, 5);

		int esperado = 10;
		int actual = inventario.getCantidad(itemRecolectable);
		assertEquals(esperado, actual);
	}

	@Test
	void recolectarItem_itemCrafteable_lanzaExcepcion() {
		Crafteable itemCrafteable = new Crafteable("prueba", "");
		Exception e = assertThrows(IllegalArgumentException.class, () -> {
			inventario.recolectarItem(itemCrafteable, 2);
		});
		assertEquals("Item no recolectable", e.getMessage());
	}

	@Test
	void recolectarItem_itemNulo_lanzaExcepcion() {
		Exception e = assertThrows(NullPointerException.class, () -> {
			inventario.recolectarItem(null, 2);
		});
		assertEquals("Item nulo", e.getMessage());
	}

	@Test
	void recolectarItem_cantidadNegativa_lanzaExcepcion() {
		Recolectable itemRecolectable = new Recolectable("prueba", "");
		Exception e = assertThrows(IllegalArgumentException.class, () -> {
			inventario.recolectarItem(itemRecolectable, -1);
		});
		assertEquals("Cantidad recolectada menor/igual a cero", e.getMessage());
	}

	@Test
	void removerItem_cantidadMenorALaActual_restaCorrectamente() {
		Recolectable itemRecolectable = new Recolectable("Prueba", "");
		inventario.recolectarItem(itemRecolectable, 10);
		inventario.removerItem(itemRecolectable, 5);

		int esperado = 5;
		int actual = inventario.getCantidad(itemRecolectable);
		assertEquals(esperado, actual);
	}

	@Test
	void removerItem_cantidadIgualALaActual_eliminaItem() {
		Recolectable itemRecolectable = new Recolectable("Prueba", "");
		inventario.recolectarItem(itemRecolectable, 10);
		inventario.removerItem(itemRecolectable, 10);

		boolean actual = inventario.getItems().containsKey(itemRecolectable);
		assertFalse(actual);
	}

	@Test
	void removerItem_cantidadNegativa_lanzaExcepcion() {
		Recolectable itemRecolectable = new Recolectable("Prueba", "");

		assertThrows(IllegalArgumentException.class, () -> {
			inventario.removerItem(itemRecolectable, -1);
		});
	}
	
	@Test
	void removerItem_cantidadMayorALaActual_lanzaExcepcion() {
		Recolectable itemRecolectable = new Recolectable("Prueba", "");
		inventario.recolectarItem(itemRecolectable, 5);
		assertThrows(IllegalArgumentException.class, () -> {
			inventario.removerItem(itemRecolectable, 10);
		});
	}
	
	@Test
	void removerItem_itemNoExiste_lanzaExcepcion() {
		Recolectable itemRecolectable = new Recolectable("Prueba", "");
		assertThrows(IllegalArgumentException.class, () -> {
			inventario.removerItem(itemRecolectable, 1);
		});
	}
	
	@Test
	void getCantidad_itemNoExiste_retornaCero() {
		assertEquals(0, inventario.getCantidad(null));
	}
	
	@Test
	void agregarMesa_mesaValida_agregaCorrectamente() {
		MesaDeTrabajo mesa = new MesaDeTrabajo("Mesa", new Receta());
		inventario.agregarMesa(mesa);
		assertTrue(inventario.tieneMesa(mesa));
		assertFalse(inventario.agregarMesa(mesa));
	}

}
