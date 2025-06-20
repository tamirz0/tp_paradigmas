package sistema_crafteo.logica;

import java.util.Map;

public class OperacionesMap {
	
	public static <T> Map<T, Integer> sumarValores(Map<T, Integer> destino, Map<T, Integer> origen) {
		for (Map.Entry<T, Integer> entrada : origen.entrySet()) {
			int actual = destino.getOrDefault(entrada.getKey(), 0);
			destino.put(entrada.getKey(), actual + entrada.getValue());
		}
		return destino;
	}
	
}
