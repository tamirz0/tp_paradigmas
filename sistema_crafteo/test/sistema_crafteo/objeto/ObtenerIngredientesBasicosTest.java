package sistema_crafteo.objeto;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class ObtenerIngredientesBasicosTest {
	
	private static boolean sonBasicos(Map<Item, Integer> dic) {
		for (Map.Entry<Item, Integer> entrada : dic.entrySet()) {
			if(entrada.getKey().esCrafteable()) {
				return false;
			}
		}
		return true;
	}
	
	@Test
	void getIngredientesBasicos_nivel3_retornoCorrecto() {
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
		assertTrue(sonBasicos(actual));
		assertEquals(esperadoC,actual.get(c));
		assertEquals(esperadoMc,actual.get(mc));
		
	}
	
	@Test
	void getIngredientesBasicos_nivel3ConSumaDeCantidades_retornaCorrecto() {
		int esperadoMadera = 6;
		int esperadoMetal = 11;
		int esperadoRoca = 9;
		
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
		
		Map<Item, Integer> actual = espada.getIngredientesBasicos();
		
		assertTrue(sonBasicos(actual));
		
		assertEquals(esperadoMadera, actual.get(madera));
		assertEquals(esperadoMetal, actual.get(metal));
		assertEquals(esperadoRoca, actual.get(roca));
	}
	
}
