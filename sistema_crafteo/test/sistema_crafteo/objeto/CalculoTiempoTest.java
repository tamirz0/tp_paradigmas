package sistema_crafteo.objeto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

class CalculoTiempoTest {

	@Test
	void getTiempoCrafteoTotal_Crafteable3Nivel_retornoCorrecto() {
		//			A (15)
		//			|	 |	
		//	(x2)P(10)	 C (0)
		//		|
		//	(x4)M(3)
		//		|
		//		MC
		//T = 2 * (10 + 4 * 3) + 15 = 59
		
		int esperado = 59;
		
		IngredienteBasico mc = new IngredienteBasico("mc", " ");
		IngredienteBasico c = new IngredienteBasico("c", " ");
		Map<Item, Integer> ing = new HashMap<>();
		ing.put(mc, 1);
		
		Receta rec = new Receta(ing, null);
		
		ObjetoCrafteable m = new ObjetoCrafteable("m", " ", 3, rec);
		
		ing = new HashMap<>();
		ing.put(m, 4);
		rec = new Receta(ing, null);
		ObjetoCrafteable p = new ObjetoCrafteable("p", " ", 10, rec);
		
		ing = new HashMap<>();
		ing.put(c, 1);
		ing.put(p, 2);
		rec = new Receta(ing, null);
		ObjetoCrafteable a = new ObjetoCrafteable("a", " ", 15, rec);
		
		int actual = a.getTiempoCrafteoTotal();
		
		assertEquals(esperado, actual);
	}

}
