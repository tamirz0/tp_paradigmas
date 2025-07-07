package sistema_crafteo.inventario;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sistema_crafteo.objeto.IngredienteBasico;
import sistema_crafteo.objeto.Item;
import sistema_crafteo.objeto.MesaDeTrabajo;
import sistema_crafteo.objeto.ObjetoCrafteable;

class InventarioTest {
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
		IngredienteBasico itemRecolectable = new IngredienteBasico("prueba", "p1");
		assertFalse(inventario.getItems().containsKey(itemRecolectable));
		inventario.recolectarItem(itemRecolectable, 1);
		assertTrue(inventario.getItems().containsKey(itemRecolectable));
	}

	@Test
	void recolectarItem_itemNoCrafteableExistente_agregaCorrectamente() {
		IngredienteBasico itemRecolectable = new IngredienteBasico("prueba", "p1");
		inventario.recolectarItem(itemRecolectable, 5);

		inventario.recolectarItem(itemRecolectable, 5);

		int esperado = 10;
		int actual = inventario.getItems().getOrDefault(itemRecolectable, 0);
		assertEquals(esperado, actual);
	}

	@Test
	void recolectarItem_itemCrafteable_lanzaExcepcion() {
		ObjetoCrafteable itemCrafteable = new ObjetoCrafteable("prueba", "p2",1);
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
		IngredienteBasico itemRecolectable = new IngredienteBasico("prueba", "p1");
		Exception e = assertThrows(IllegalArgumentException.class, () -> {
			inventario.recolectarItem(itemRecolectable, -1);
		});
		assertEquals("Cantidad recolectada menor/igual a cero", e.getMessage());
	}

	
	@Test
	void agregarMesa_mesaValida_agregaCorrectamente() {
		MesaDeTrabajo mesa = new MesaDeTrabajo("Mesa");
		inventario.agregarMesa(mesa);
		assertTrue(inventario.tieneMesa(mesa));
		assertFalse(inventario.agregarMesa(mesa));
	}
	
    @Test
    void tieneMesa_sinAgregar_retornaFalse() {
        MesaDeTrabajo mesa = new MesaDeTrabajo("mesaX");
        assertFalse(inventario.tieneMesa(mesa));
    }

    @Test
    void getMesas_inicialmente_vacio() {
        Set<MesaDeTrabajo> mesas = inventario.getMesas();
        assertTrue(mesas.isEmpty());
    }
    
    @Test
    void agregarMesa_multiplesMesas_seRegistranCorrectamente() {
        MesaDeTrabajo m1 = new MesaDeTrabajo("M1");
        MesaDeTrabajo m2 = new MesaDeTrabajo("M2");
        assertTrue(inventario.agregarMesa(m1));
        assertTrue(inventario.agregarMesa(m2));
        assertTrue(inventario.tieneMesa(m1));
        assertTrue(inventario.tieneMesa(m2));
        assertEquals(2, inventario.getMesas().size());
    }
    
    @Test
    void setItems_mapaValido_reemplazaContenido() {
    	IngredienteBasico a = new IngredienteBasico("A", "a1");
    	IngredienteBasico b = new IngredienteBasico("B", "b2");
        Map<Item,Integer> nuevo = new HashMap<>();
        nuevo.put(a, 3);
        nuevo.put(b, 5);
        inventario.setItems(nuevo);
        // getItems debe devolver exactamente 'nuevo'
        assertEquals(2, inventario.getItems().size());
        assertTrue(inventario.getItems().containsKey(a));
        assertTrue(inventario.getItems().containsKey(b));
        assertEquals(3, (int)inventario.getItems().get(a));
        assertEquals(5, (int)inventario.getItems().get(b));
    }
    
    @Test
    void getItems_modificarMapaExterno_afectaInventario() {
    	IngredienteBasico x = new IngredienteBasico("X", "x1");
        inventario.recolectarItem(x, 2);
        Map<Item,Integer> ref = inventario.getItems();
        ref.put(x, 10);
        // como getItems devuelve la referencia interna, el cambio debe verse
        assertEquals(10, (int)inventario.getItems().get(x));
    }
}
