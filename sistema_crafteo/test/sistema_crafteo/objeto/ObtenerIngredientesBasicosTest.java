package sistema_crafteo.objeto;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class ObtenerIngredientesBasicosTest {
	
	@Test
	void getIngredientesBasicos_objetoCrafteable_retornoCorrecto() {
		//			A (15)
		//			|	 |	
		//	(x5)P(10) (2)C (0)
		//	2	| -> Se genera en 3 crafteos, porque fabrica de a 2.
		//	(x5)M(3) -> Se genera en 2 crafteos porque fabrica de a 4.
		//	4	|
		//		MC
		//
		// A = 5P + 2C
		// 2P = 5M
		// 5P = 15M -> Se redondea a 15M para fabircar 6P porque no puedo fabricar 5 exactamente
		// 4M = MC 
		// 15M = 4MC -> Se redondea a 4MC porque no puedo tener 3.7 MC
		
		// A = 2C + 5P
		// 5P = 15M
		// 15M = 4MC
		// 
		// Resultado Final: A = 2C + 4MC
		int esperadoC = 2;
		int esperadoMc = 4;
		
		IngredienteBasico mc = new IngredienteBasico("mc", " ");
		IngredienteBasico c = new IngredienteBasico("c", " ");
		Map<Item, Integer> ing = new HashMap<>();
		ing.put(mc, 1);
		
		Receta rec = new Receta(ing, null, 4); //Se generan 4 de un tiron
		
		ObjetoCrafteable m = new ObjetoCrafteable("m", " ", 3, rec);
		
		ing = new HashMap<>();
		ing.put(m, 5);
		rec = new Receta(ing, null, 2);
		ObjetoCrafteable p = new ObjetoCrafteable("p", " ", 10, rec);
		
		ing = new HashMap<>();
		ing.put(c, 2);
		ing.put(p, 5);
		rec = new Receta(ing, null);
		ObjetoCrafteable a = new ObjetoCrafteable("a", " ", 15, rec);
		
		Map<Item, Integer> actual = a.getIngredientesBasicos();
		

		
		assertEquals(2, actual.size());
		
		for (Map.Entry<Item, Integer> entrada: actual.entrySet()) {
			assertTrue(!entrada.getKey().esCrafteable());
		}
		
		assertEquals(esperadoC,actual.get(c));
		assertEquals(esperadoMc,actual.get(mc));
		
	}
}
