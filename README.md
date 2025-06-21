# tp_paradigmas
Trabajo Practico "Sistema de Crafteo" de la materia Paradigmas de Programacion. Grupo KAPPA 1C2025

## Acerca del sistema
Se busca representar recetas de crafteo, ingredientes básicos, objetos intermedios y un inventario del jugador, sin utilizar estructuras de grafos. Además, se espera que la solución pueda adaptarse fácilmente a la incorporación de nuevos objetos, ingredientes o reglas.

## Aclaracion seguimiento de avances 
- Se deben cumplir las funcionalidades agregadas en este documento.
- Estados posibles de las funcionalidades a desarrollar: 
    1. Pendiente
    2. En desarrollo
    3. Implementado
    4. Implementado y testeado

## Funcionalidades basicas
1. ¿Que necesito para craftear un objeto? 
    - Estado: ***Implementado y testeado***
    - Descripcion: Dado un objeto crafteable, mostrar la lista de ingredientes y cantidades necesarias (sólo el primer nivel de la receta, sin descomponer los ingredientes).
2. ¿Qué necesito para craftear un objeto desde cero?
    - Estado: ***Implementado y testeado***
    - Descripcion: Dado un objeto crafteable, mostrar todos los elementos básicos necesarios, con sus cantidades totales, considerando la descomposición completa de sus ingredientes en elementos básicos.
3. ¿Qué me falta para craftear un objeto?
    - Estado: ***Implementado y testeado (falta test del getIngredientesFaltantesTodos)***
    - Descripcion: Dado un objeto y un inventario, indicar qué ingredientes y en qué cantidad faltan para poder craftearlo (primer nivel solamente).

4. ¿Qué me falta para craftear un objeto desde cero?
    - Estado: ***Pendiente***
    - Descripcion: Lo mismo que el punto 3, pero considerando los elementos básicos necesarios para toda la cadena de crafteo.
5. ¿Cuántos puedo craftear?
    - Estado: ***Pendiente***
    - Descripcion: Dado un objeto y un inventario, indicar cuántas unidades del objeto se pueden fabricar utilizando los elementos del inventario (y fabricando los ingredientes intermedios si es necesario).
6. Realizar el crafteo indicado
    - Estado: ***Pendiente***
    - Descripcion: El inventario dispone de ciertos elementos y objetos en principio, y debe poderse ejecutar un crafteo. Esto es una acción que modifica el contenido del inventario, el cual debe actualizarse en consecuencia.
7. En todos los casos, indicar los tiempos necesarios
    - Estado: ***Implementado y testeado***
    - Descripcion: Craftear, por su naturaleza, requiere de un tiempo. No es instantáneo, y esos tiempos deben considerarse e informarse en cada una de las preguntas anteriores. Al realizar crafteos en cadena, los tiempos deben sumarse, y multiplicarse apropiadamente de acuerdo a la cantidad de unidades involucradas.
8. Historial de crafteos
    - Estado: ***Pendiente***
    - Descripcion: Registrar cada objeto que se ha crafteado, con sus ingredientes usados y la fecha o turno de creación.

## Datos de origen
1. recetas.json
    - Estado: ***Pendiente***
    - Descripcion: Archivo JSON que describa los elementos y los ítems, indicando también los ingredientes que lo forman (si no fueran elementos básicos). Es decir, todo lo necesario para tener la información para operar.
2. inventario.json
    - Estado: ***Pendiente***
    - Descripcion: Archivo JSON que especifique los elementos presentes en el inventario inicial del jugador.

## Integracion Prolog
- Estado: ***Pendiente***
- Se DEBE ejecutar desde Java directamente, idealmente el codigo directo generado por cada actualizacion en el inventario. NO desde un archivo .pl.

Realizar la integración con Prolog, para poder responder la siguiente pregunta:

¿Cuáles son todos los productos que podría generar con el inventario actual?
El sistema debe traducir el inventario actual a hechos en Prolog (tengo/2) y definir las reglas y recetas (ingrediente/3). Prolog responderá con la lista de objetos posibles, deduciendo automáticamente a partir de las recetas y cantidades disponibles.

Ejemplo:
```prolog
% Hechos

ingrediente(bastón, madera, 2).

ingrediente(espada, hierro, 3).

ingrediente(espada, bastón, 1).

elemento_basico(madera).

elemento_basico(hierro).


% Inventario

tengo(madera, 4).

tengo(hierro, 6).


% Reglas

puedo_craftear(Objeto) :-

    ingrediente(Objeto, Ing, Cant),

    tengo(Ing, CantDisponible),

    CantDisponible >= Cant.
```


## Funcionalidades extra
1. Mostrar el árbol de crafteos
    - Estado: ***Pendiente***
    - Descripcion: Mediante una interfaz de texto, mostrar la forma de realizar un crafteo determinado, informando adecuadamente cada paso necesario.
2. Recetas alternativas o variantes
    - Estado: ***En desarrollo***
    - Descripcion: Permitir que un mismo objeto pueda craftearse con diferentes combinaciones de ingredientes (por ejemplo, una antorcha puede hacerse con carbón mineral o carbón vegetal).
3. Inventario final
    - Estado: ***Pendiente***
    - Descripcion: Al cerrar el programa, crear un archivo inventario-out.json (o XML), que especifique los elementos presentes en el inventario final del jugador.
4. Mesas de trabajo
    - Estado: ***En desarrollo***
    - Descripcion: Las mesas de trabajo son herramientas opcionales que amplían el repertorio de recetas disponibles para fabricar. Cada mesa desbloquea entre 1 y N recetas adicionales. Deben existir múltiples tipos de mesas, y su presencia o ausencia en el inventario (0 o 1 por tipo) afecta directamente qué recetas están disponibles, modificando así el comportamiento general del sistema. Este ítem puede combinarse con el punto bonus 2, pero no es compatible con la mecánica de catalizadores (punto 4). (+1 pto) Si tuviera los ingredientes, puedo craftear mesas de trabajo.
