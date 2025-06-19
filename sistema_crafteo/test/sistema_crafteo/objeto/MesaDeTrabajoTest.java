package sistema_crafteo.objeto;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MesaDeTrabajoTest {
	
	class ItemPrueba extends Item{

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
	MesaDeTrabajo mesa;
	
	@BeforeEach
	void setup() {
		ItemPrueba item = new ItemPrueba("Prueba", "Descripcion");
		Map<Item, Integer> ingredientes = new HashMap<Item, Integer>();
		ingredientes.put(item, 3);
		receta = new Receta(ingredientes, null);
		mesa = new MesaDeTrabajo("Mesa Prueba", receta);
	}
	
	@Test
	void crearMesa_mesaValida_seCreaCorrectamente() {
		assertNotNull(mesa);
		assertNotNull(mesa.getRecetaCreacion());
		assertEquals(receta, mesa.getRecetaCreacion());
		assertEquals("Mesa Prueba", mesa.getNombre());
		
	}
	
	@Test
	void crearMesa_parametrosVacios_lanzaExcepcion() {
		Exception e1 = assertThrows(IllegalArgumentException.class, () -> {
			mesa = new MesaDeTrabajo(null, receta);
		});
		
		Exception e2 = assertThrows(IllegalArgumentException.class, () -> {
			mesa = new MesaDeTrabajo("test", null);
		});
		
		String esperado = "Parametros nulos";
		assertEquals(esperado, e1.getMessage());
		assertEquals(esperado, e2.getMessage());
	}
	
	@Test
	void crearMesa_recetaConIngredientesCrafteables_lanzaExcepcion() {
		class ItemCrafteable extends Item{
			public ItemCrafteable() {
				super("Crafteable", "-");
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
			
			@Override
			public boolean esCrafteable() {
				return true;
			}
		}
		ItemCrafteable crafteable = new ItemCrafteable();
		Map<Item, Integer> ingredientes = new HashMap<>();
		ingredientes.put(crafteable, 100);
		Receta recetaConCrafteable = new Receta(ingredientes, null);
		
		Exception e = assertThrows(IllegalArgumentException.class, () -> {
			mesa = new MesaDeTrabajo("mesa", recetaConCrafteable);
		});
		
		assertEquals("Mesa de trabajo solo puede ser creada con ingredientes basicos", e.getMessage());
	}
	
	
	
}
