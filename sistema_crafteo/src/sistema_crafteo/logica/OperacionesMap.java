package sistema_crafteo.logica;

import java.util.HashMap;
import java.util.Map;

public class OperacionesMap {

	public static <T> void sumarValor(Map<T, Integer> destino, T key, Integer cantidadNueva) {
		int actual = destino.getOrDefault(key, 0);
		destino.put(key, actual + cantidadNueva);
	}

	public static <T> void restarValor(Map<T, Integer> destino, T key, Integer cantidadEliminada) {
		Integer actual = destino.get(key);
		if (actual == null) { // No existe la key
			throw new IllegalArgumentException("El item no existe");
		}

		if (actual < cantidadEliminada) {
			throw new IllegalArgumentException("Cantidad a eliminar mayor a la que se posee");
		}

		if (!destino.remove(key, cantidadEliminada)) { // actual == cantidadEliminada
			destino.put(key, actual - cantidadEliminada); // actual > cantidadEliminada
		}
	}

	public static <T> Map<T, Integer> sumarTodo(Map<T, Integer> destino, Map<T, Integer> origen) {
		for (Map.Entry<T, Integer> entrada : origen.entrySet()) {
			int actual = destino.getOrDefault(entrada.getKey(), 0);
			destino.put(entrada.getKey(), actual + entrada.getValue());
		}
		return destino;
	}

	public static <T> Map<T, Integer> restarTodo(Map<T, Integer> mayor, Map<T, Integer> menor) {
		Map<T, Integer> mapRet = new HashMap<>();
		mapRet.putAll(mayor);
		for (Map.Entry<T, Integer> entrada : mayor.entrySet()) {
			int valorARestar = menor.getOrDefault(entrada.getKey(), 0);
			int actual = entrada.getValue();
			int nuevoValor = actual > valorARestar ? actual - valorARestar : 0;
			mapRet.put(entrada.getKey(), nuevoValor);
		}
		return mapRet;
	}
	
	public static <T> Map<T, Integer> quitarKeysConValorCero(Map<T, Integer> dic){
		Map<T, Integer> nuevoDic = new HashMap<T, Integer>();
		nuevoDic.putAll(dic);
		for (T t : dic.keySet()) {
			nuevoDic.remove(t, 0);
		}
		return nuevoDic;
	}
	
}
