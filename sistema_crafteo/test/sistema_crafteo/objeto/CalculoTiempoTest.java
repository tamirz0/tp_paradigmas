package sistema_crafteo.objeto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

class CalculoTiempoTest {
	
	@Test
	void getTiempoCrafteoTotal_sinIngredientesConParametro_lanzaExcepcion() {
		ObjetoCrafteable o = new ObjetoCrafteable(" ", " ", 2, (Receta) null);
		
		Exception e = assertThrows(NullPointerException.class, () ->{
			o.getTiempoCrafteoTotal(2);
		});
		
		assertEquals("El objeto no posee recetas", e.getMessage());
	}
	
	@Test
	void getTiempoCrafteoTotal_conParametroNegativo_retornaCero() {
		Receta r = new Receta();
		ObjetoCrafteable o = new ObjetoCrafteable(" ", " ", 2, r);
		
		assertEquals(0, o.getTiempoCrafteoTotal(-1));
		
	}
	
	@Test
	void getTiempoCrafteoTotal_conCantidadGenerada_retornoCorrecto() {
		IngredienteBasico b = new IngredienteBasico("basico 1", "-");
		Map<Item, Integer> ing = new HashMap<Item, Integer>();
		ing.put(b, 1);
		
		Receta rec = new Receta(ing, null, 2);
		
		ObjetoCrafteable c = new ObjetoCrafteable("crafteable 1", "-", 10, rec);
		
		
		ing = new HashMap<Item, Integer>();
		
		ing.put(c, 3); //Depende de cuantos genere su receta aca tarda mas o menos tiempo
		
		//Como necesito 3 unidades y se generan de a 2, necesito 2 * 10 segundos = 20
		
		rec = new Receta(ing, null);
		
		ObjetoCrafteable c2 = new ObjetoCrafteable("c2", "-", 20, rec); //20 + 20 = 40
		
		int esperado = 40;
		int actual = c2.getTiempoCrafteoTotal();
		assertEquals(esperado, actual);
		
	}
	
	@Test
	void getTiempoCrafteoTotal_Crafteable3Nivel_retornoCorrecto() {
		//			A (15)
		//			|	 |	
		//	(x2)P(10)	 C (0)
		//		|
		//	(x4)M(3) -> Se genera en 4 crafteos
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
	
	@Test
	void getTiempoCrafteoTotal_Crafteable3NivelConNCantidadesGeneradas_retornaCorrectamente() {
		//			A (15)
		//			|	 |	
		//	(x3)P(10)	 C (0)
		//		| -> Se genera en 2 crafteos, porque fabrica de a 2.
		//	(x5)M(3) -> Se genera en 2 crafteos porque fabrica de a 4.
		//		|
		//		MC
		//T = 2 * (10 + 2 * 3) + 15 = 47
		
		int esperado = 47;
		
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
		ing.put(c, 1);
		ing.put(p, 3);
		rec = new Receta(ing, null);
		ObjetoCrafteable a = new ObjetoCrafteable("a", " ", 15, rec);
		
		int actual = a.getTiempoCrafteoTotal();
		
		assertEquals(esperado, actual);
	}

}
