package sistema_crafteo.main;

import java.io.IOException;
import java.nio.file.Paths;

import sistema_crafteo.integracion.GestorArchivo;
import sistema_crafteo.inventario.Inventario;
import sistema_crafteo.logica.SistemaCrafteo;
import sistema_crafteo.objeto.Item;
import sistema_crafteo.objeto.MesaDeTrabajo;

public class Main {

	public static void main(String[] args) {
		SistemaCrafteo sistema = new SistemaCrafteo();
		Inventario miInventario;
		// DATOS DE ORIGEN
		miInventario = sistema.cargarDatos("files/recetas.json", "files/inventario.json");
		Item escudo = sistema.buscarItem("escudo");

		System.out.println("===¿Que necesito para craftear un objeto? ===");
		for (int i = 0; i < escudo.getRecetas().size(); i++) {
			System.out.println(escudo.getArbolCrafteo(i));
		}

		System.out.println("\n===¿Qué necesito para craftear un objeto desde cero?===");
		for (int i = 0; i < escudo.getRecetas().size(); i++) {
			System.out.println(escudo.getArbolCrafteoBasicos(i));
		}

		System.out.println("\n===¿Qué me falta para craftear un objeto?===");
		System.out.println(sistema.getIngredientesFaltantesTodos(escudo, miInventario));

		System.out.println("\n===¿Qué me falta para craftear un objeto desde cero?===");
		System.out.println(sistema.getIngredientesBasicosFaltantesTodos(escudo, miInventario));

		System.out.println("\n===¿Cuántos puedo craftear?===");
		System.out.println(sistema.getCantidadMaxima(miInventario, escudo));

		System.out.println("\n===Realizar el crafteo indicado===");
		System.out.println("Inventario previo: " + miInventario.getItems());
		System.out.println("Tiempo crafteo = " + escudo.getTiempoCrafteoTotal());
		// INTEGRACION CON PROLOG INCLUIDA EN METODO CRAFTEAR
		// UTILIZA PUEDECRAFTEAR RECURSIVO EJECUTADO EN SWISH PROLOG
		System.out.println(
				sistema.craftear(miInventario, escudo) == true ? "Escudo Crafteado correctamente" : "Error crafteo");
		System.out.println("Inventario posterior: " + miInventario.getItems());

		System.out.println("\n===Mesas de trabajo + Recetas alternativas");
		Item antorcha = sistema.buscarItem("antorcha");
		MesaDeTrabajo mesaAntorcha = antorcha.getReceta(1).getMesaRequerida();
		miInventario.recolectarItem(sistema.buscarItem("madera"), 20);
		miInventario.recolectarItem(sistema.buscarItem("carbon"), 1);
		miInventario.recolectarItem(sistema.buscarItem("aceite"), 1);

		System.out.println(
				sistema.craftear(miInventario, antorcha, 1) == false ? "No pudo craftear Antorcha, no posee mesa"
						: "Pudo craftear antorcha");
		System.out.println(miInventario.getItems());

		System.out.println(miInventario.agregarMesa(mesaAntorcha) == true ? mesaAntorcha + " Obtenida"
				: "No se pudo obtener la mesa");

		System.out.println(sistema.craftear(miInventario, antorcha, 1) == false ? "No pudo craftear Antorcha"
				: "Pudo craftear antorcha");
		System.out.println(miInventario.getItems());

		System.out.println("\n===Receta mas viable Escudo===");
		System.out.println(sistema.getBasicosFaltantesMinimos(escudo, miInventario));
		
		System.out.println("\n===Historial de crafteo===");
		System.out.println(sistema.getHistorial().getRegistros());
		
		System.out.println("\n===Inventario Final===");
		System.out.println("Inventario pre-guardado: " + miInventario.getItems());
		System.out.println("Mesas pre-guardado: " + miInventario.getMesas());
		GestorArchivo gestor = new GestorArchivo(null, null);
		try {
			gestor.guardarInventario(Paths.get("files/inventario-out.json"), miInventario);
			System.out.println("\nSe guarda el inventario-out.json...");
		} catch (IOException e) {
			e.printStackTrace();
		}
		sistema = new SistemaCrafteo();
		Inventario inventarioJson = sistema.cargarDatos("files/recetas.json", "files/inventario-out.json");
		System.out.println("Se carga el inventario-out.json...\n");
		System.out.println("Inventario post-guardado: " + inventarioJson.getItems());
		System.out.println("Mesas post-guardado: " + inventarioJson.getMesas());

		System.out.println(
				miInventario.getItems().equals(inventarioJson.getItems()) == true ? "\nLos inventarios son iguales"
						: "\nLos inventarios No son iguales");
	}

}
