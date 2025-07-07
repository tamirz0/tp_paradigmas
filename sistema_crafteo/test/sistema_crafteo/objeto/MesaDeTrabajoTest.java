package sistema_crafteo.objeto;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MesaDeTrabajoTest {
	
	class ItemPrueba extends Item{

		public ItemPrueba (String nombre, String descripcion) {
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
		public int getTiempoCrafteoTotal(int n) {
			return 0;
		}	
	}
	
	Receta receta;
	MesaDeTrabajo mesa;
	
	@BeforeEach
	void setup() {
		mesa = new MesaDeTrabajo("Mesa Prueba");
	}
	
	@Test
	void crearMesa_mesaValida_seCreaCorrectamente() {
		assertNotNull(mesa);
		assertEquals("Mesa Prueba", mesa.getNombre());
		
	}
	
    @Test
    void crearMesa_nombreNull_lanzaExcepcion() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            new MesaDeTrabajo(null);
        });
        assertEquals("Parametro nulos", ex.getMessage());
    }
	
    @Test
    void equals_mismoNombre_sonIguales() {
        MesaDeTrabajo otra = new MesaDeTrabajo("Mesa Prueba");
        assertEquals(mesa, otra);
        assertEquals(mesa.hashCode(), otra.hashCode());
    }
    
    @Test
    void equals_distintoNombre_noSonIguales() {
        MesaDeTrabajo diferente = new MesaDeTrabajo("OtraMesa");
        assertNotEquals(mesa, diferente);
    }
	

    @Test
    void toString_devuelveNombre() {
        assertEquals("Mesa Prueba", mesa.toString());
    }
	
}
