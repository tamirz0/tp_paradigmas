package sistema_crafteo.integracion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.fasterxml.jackson.databind.ObjectMapper;

import sistema_crafteo.inventario.Inventario;
import sistema_crafteo.logica.HistorialCrafteo;
import sistema_crafteo.objeto.IngredienteBasico;
import sistema_crafteo.objeto.Item;
import sistema_crafteo.objeto.MesaDeTrabajo;
import sistema_crafteo.objeto.ObjetoCrafteable;
import sistema_crafteo.objeto.Receta;

class GestorArchivoTest {

    @TempDir
    Path tempDir;

    private ObjectMapper mapper;
    String jsonRecetas = """
    {
      "madera": {
        "tipo": "basico",
        "descripcion": "Trozo de madera",
        "tiempo": 0,
        "recetas": null
      },
      "palo": {
        "tipo": "crafteable",
        "descripcion": "Palo",
        "tiempo": 5,
        "recetas": [
          {
            "ingredientes": { "madera": 2 },
            "cantidadGenerada": 3,
            "mesaRequerida": null
          }
        ]
      }
    }
    """;
    
    String jsonInventario = """
    {
      "items": {
        "madera": 10,
        "palo": 5
      },
      "mesas": ["mesaA", "mesaB"]
    }
    """;
    
    
    @BeforeEach
    void setup()  {
        mapper = new ObjectMapper();
    }

    @Test
    void cargarItems_conDosTipos_creaCorrectamente() throws IOException {
        // Preparamos recetas.json
        Path recetasFile = tempDir.resolve("recetas.json");
        java.nio.file.Files.writeString(recetasFile, jsonRecetas);

        GestorArchivo gestor = new GestorArchivo(null, recetasFile);
        Map<String, Item> items = gestor.cargarItems();
        
        assertEquals(2, items.size());
        assertTrue(items.get("madera") instanceof IngredienteBasico);
        Item palo = items.get("palo");
        assertTrue(palo instanceof ObjetoCrafteable);
        
        assertEquals(1, palo.getRecetas().size());
        Receta r = palo.getRecetas().get(0);
        assertEquals(3, r.getCantidadGenerada());
        assertEquals(2, r.getIngredientes().get(items.get("madera")));
    }

    @Test
    void cargarInventario_conItemsYMesas_cargaCorrectamente() throws IOException {
        // Caragamos las recetas y los items primero
        Path recetasFile = tempDir.resolve("recetas.json");
        java.nio.file.Files.writeString(recetasFile, jsonRecetas);
        
        Path invFile = tempDir.resolve("inventario.json");
        java.nio.file.Files.writeString(invFile, jsonInventario);
        
        GestorArchivo gestor = new GestorArchivo(invFile, recetasFile);
        
        Map<String, Item> items = gestor.cargarItems();
        Inventario inv = gestor.cargarInventario(items);
        
        Item palo = items.get("palo");
        Item madera = items.get("madera");
        
        Map<Item, Integer> invEsperado = new HashMap<>();
        invEsperado.put(palo, 5);
        invEsperado.put(madera, 10);
        
        Set<MesaDeTrabajo> mesas = new HashSet<>();
        mesas.add(new MesaDeTrabajo("mesaA"));
        mesas.add(new MesaDeTrabajo("mesaB"));
        
        assertEquals(mesas, inv.getMesas());
        assertEquals(invEsperado, inv.getItems());
    }

    @Test
    void guardarInventario_yLeerJSON_coincideConEntrada() throws IOException {
        Inventario inv = new Inventario();
        IngredienteBasico m = new IngredienteBasico("madera"," ");
        ObjetoCrafteable palo = new ObjetoCrafteable("palo"," ",1, new Receta(Map.of(m,2), null));

        inv.recolectarItem(m, 4);
        inv.recolectarItem(m, 6);
        inv.setItems(Map.of(m,10, palo,3));
        inv.agregarMesa(new MesaDeTrabajo("mesaX"));

        Path out = tempDir.resolve("out-inv.json");
        new GestorArchivo(null,null).guardarInventario(out, inv);

        // Ahora leemos con ObjectMapper 
        var node = mapper.readTree(out.toFile());
        assertEquals(10, node.get("items").get("madera").asInt());
        assertEquals(3, node.get("items").get("palo").asInt());
        var mesas = node.get("mesas");
        assertTrue(mesas.isArray());
        assertEquals("mesaX", mesas.get(0).asText());
    }

    @Test
    void guardarHistorial_escribeListaDeRegistrosCorrectamente() throws IOException {
        HistorialCrafteo hist = new HistorialCrafteo();
        IngredienteBasico m = new IngredienteBasico("madera","");
        hist.registrarCrafteo(m, Map.of(m,2));
        hist.registrarCrafteo(m, Map.of(m,3));

        Path out = tempDir.resolve("hist.json");
        new GestorArchivo(null,null).guardarHistorial(out, hist);

        // leer como lista de registros
        var list = mapper.readValue(out.toFile(), List.class);
        assertEquals(2, list.size());
        // cada entrada debería tener campo "item" y "ingredientes"
        
        @SuppressWarnings("unchecked")
		Map<String,Object> rec0 = (Map<String,Object>) list.get(0);
        assertTrue(rec0.containsKey("item"));
        assertTrue(rec0.containsKey("ingredientes"));
    }
}
