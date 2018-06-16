# swapoperator

Optimización de asignación cuadrática con distintas tecnicas

Como ejecutar el proyecto:

ejecutar desde la línea de comandos de la siguiente forma (JAR adjunto en carpeta "jar"):

`
java -jar ayudantia1metaheuristicas.jar -path [Ruta] [matriz funciones] [matriz distancias] [swaps] [ejercicio]
`

[Ruta] es la ruta donde están los txt de datos. Por ejemplo: "C:/Universidad/USACH/2018-1/Optimización en Ingeniería/Ayudantía/Metaheurísticas/Ayudantía 1/Datos/"

[matriz funciones] es el nombre del txt con la matriz de funciones, por ejemplo: F64.txt

[matriz distancias] es el nombre del txt con la matriz de distancias, por ejemplo: D64.txt

[swaps] numero de swaps, por ejemplo (no he probado con más): 2

[ejercicio] es uno de los 3 ejercicios que aparecen en el pdf de asignación cuadrática de la ayudantía, por ejemplo:
ejercicio1
ejercicio2
ejercicio3

También pueden ejecutarlo desde eclipse agregando estos argumentos en el "Run configuration", ejemplo:
`
-path "C:/Universidad/USACH/2018-1/Optimización en Ingeniería/Ayudantía/Metaheurísticas/Ayudantía 1/Datos/" F128.txt D128.txt 2 ejercicio3
`

## Tipos de ejercicio

ejercicio1: buscar 1 solución mejor que la inicial generada aleatoriamente, o hasta alcanzar el 50% de la vecindad

ejecicio2: generar el 30% de la vecindad y luego buscar el mejor resultado, a partir de una solución inicial generada aleatoriamente

ejercico3: generar el 100% de la vecindad y luego buscar el mejor resultado, a partir de una solución inicial generada aleatoriamente

SA: generar óptimos en base al algoritmo de simulated annealing (Parámetros dentro del método Main y de la clase SimulatedAnnealing)

TABU: genera optimos en base a tabu research

## Las instancias

Se adjuntan los datos de las instancias para las pruebas en la carpeta datos, deben tener prefijo F y D de la misma instancia, se obtuvieron de
[Fuente de datos](http://anjos.mgi.polymtl.ca/qaplib/inst.html) y se pueden agregar otros de alli.





## Dataset de pruebas

para hacer de forma mas automatica la toma de muestras se habilito el uso de un archivo que llamamos dataset de pruebas, que guarda los argumentos de las pruebas, por ello en un archivo csv se guardaran los argumentos separados por una letra de preferencia coma (,) y cada experimento separado por un salto de linea como se ve en el siguiente ejemplo que explica los datos en la columa 2
Si no ocupa Dataset de pruebas ignore el primer argumento

## Para Simulated Annealing
Para ejecutarlo por comando:
`
-path "D:/Proyecto Java/swapoperator/swapOperator/datos/" chr22F.txt chr22D.txt 10 SA 1 500 0.7 2 5 1
`


| Dato| ejemplo| Explicacion |
| ----- | ---- | ---- |
| PATH | -path | ---- |
| ruta | "D:/Proyecto Java/swapoperator/swapOperator/datos/" | ---- |
| dataset F | 64.txt | dataset usada, debe estar dentro de la carpeta datos | 
| dataset D | 64.txt | dataset usada, debe estar dentro de la carpeta datos | 
| cantidad | 2 | cantidad de vencidad |
| tipo | SimulatedAnnealing | Tipo de metaheuristica a usar (solo en caso de usar dataset) |
| funcion | swap | funcion que se utilizara para tratar a los vecinos, puede ser "swap", "insercion"*, "switch"* |

| tempMax | 350 |  |
| tempMin | 1 |  |
| funcion de enfriamiento | Geométrico |  |
| aceptacion | Aleatoria |  |
| Factor de decrecimiento | 1 |  |
| nombre del grafico | grafico | nombre del titulo del grafico |

* para pruebas manuales
** para pruebas con dataset

Este ejemplo seria en el archivo dataset.csv
`
SimulatedAnnealing;64.txt;swap;2;350;1;0;Aleatoria;50;1;grafico
`

## Para Tabu Search
Para ejecutarlo por comando:
`
-path "D:/Proyecto Java/swapoperator/swapOperator/datos/" chr22F.txt chr22D.txt 2 TABU 50 20
`
para ver en detalles abajo se explica en el dataset

Para ejecutarlo por dataset: 
`
-dataset "D:/Proyecto Java/swapoperator/swapOperator/datos/dataset-TabuResearch.csv"
`

el dataset tiene la siguiente forma:
| Dato| ejemplo| Explicacion |
| ----- | ---- | ---- |
| PATH | -path | comando para hacer la prueba unitaria |
| ruta | "D:/Proyecto Java/swapoperator/swapOperator/datos/" | ruta de la carpeta datos |
| dataset F | 64.txt | dataset usada, debe estar dentro de la carpeta datos | 
| dataset D | 64.txt | dataset usada, debe estar dentro de la carpeta datos | 
| cantidad | 2 | cantidad de vencidad |
| tipo | TABU | Tipo de metaheuristica a usar |
| duracionTabuList | 50 | duracion de la tabulist |
| iteraciones | 20 | cantidad de iteraciones para el experimento |
| titulo grafico* | "grafico bonito" | solo para el dataset de pruebas este dato, es el titulo del grafico a mostrar y del archivo log |

Este ejemplo seria en el archivo dataset.csv
`
SimulatedAnnealing;64.txt;swap;2;350;1;0;Aleatoria;50;1;grafico
`
