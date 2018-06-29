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

Argumentos de ejemplo para ejecutar simulated annealing:

-path "C:/Universidad/USACH/2018-1/Optimización en Ingeniería/Ayudantía/Metaheurísticas/Ayudantía 1/Datos/" F64.txt D64.txt 10 SA

Ultimo ejemplo linea de comando
-path "C:/gitWorkspace/swapoperator/swapOperator/datos/" chr22F.txt chr22D.txt 22 SA 1 350 0.99 1

Argumentos Simmulated Annealing instancia 64:
-path "C:/Users/SebaFuTzu/git/swapOperatorLocal/swapOperator/datos/" F64.txt D64.txt 2 SA 1 1000 0.95 1 0.3 2

Argumentos Tabu Search instancia de 64:
-path "C:/Users/SebaFuTzu/git/swapOperatorLocal/swapOperator/datos/" F64.txt D64.txt 2 TABU 22 500 18 false true

##Ultimas modificaciones al formato del CSV
Se agrega parametro con cantidad de iteraciones a correr del experimento
Se agrega parametro con mejor optimo encontrado 

-path;./datos/;F64.txt;D64.txt;2;TABU;50;20;18;true;true;5;116

-dataset ./dataset-TabuResearch.csv algoritmosGeneticos

-dataset ./dataset-TrabajoOptimizacion.csv trabajoOptimizacion

