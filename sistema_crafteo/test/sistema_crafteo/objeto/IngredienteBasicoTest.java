package sistema_crafteo.objeto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class IngredienteBasicoTest {
	
	IngredienteBasico basico;
	
	@BeforeEach
	void setUp() {
		basico = new IngredienteBasico("Madera", "Descripcion");
	}
	
	@Test
	void getTiempoCrafteo_sinParametro_esCero() {
		int actual = basico.getTiempoCrafteo();
		assertEquals(0, actual);
	}
	
    @Test
    void toString_retornElFormatoCorrecto() {
        assertEquals("Basico Madera", basico.toString());
    }
    
    @Test
    void esCrafteable_retornaFalse() {
        assertFalse(basico.esCrafteable());
    }
    
    @Test
    void equals_conDistintoNombre_noSonIguales() {
        IngredienteBasico distinto = new IngredienteBasico("Piedra", "Roca");
        assertNotEquals(basico, distinto);
    }
    
    @Test
    void getIngredientesBasicos_devuelveMapaConSiMismo() {
        Map<Item,Integer> mapa = basico.getIngredientesBasicos();
        assertEquals(1, mapa.size());
        assertTrue(mapa.containsKey(basico));
        assertEquals(1, mapa.get(basico));
    }
}
