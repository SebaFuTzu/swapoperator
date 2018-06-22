package swapOperator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class AlgoritmoGenetico {
	static int[] hijo;
	static int[] solucionMutada;
	static int[] solucionInicial;
	static double costoSolucionInicial;
	static ArrayList<IndividuoAlgoritmoGenetico> padresSeleccionados;
	static double probabilidadSolucion;
	static double mejorCostoIteracionAnterior;
	static double mejorCostoIteracionActual;
	static double fitnessPromedio;
	static ArrayList<IndividuoAlgoritmoGenetico> memoriaMejoresSolucionesOrdenada;
	static ArrayList<IndividuoAlgoritmoGenetico> poblacionActual;
	static ArrayList<IndividuoAlgoritmoGenetico> nuevaPoblacion;
	static ArrayList<IndividuoAlgoritmoGenetico> copiaPoblacionActual;
	static ArrayList<IndividuoAlgoritmoGenetico> copiaNuevaPoblacion;
	static double sumaTotalCostosPoblacion;
	static Random random;
	static int[] puntosCrossOver;
	static IndividuoAlgoritmoGenetico mejorSolucion;
	static int cantidadSwappingsAG;
	static double totalFitness;
	
	public static final int CRITERIO_PARADA_NUMERO_FIJO_GENERACIONES = 0;
	public static final int CRITERIO_PARADA_ADAPTATIVO = 1;
	
	//Poblaci�n inicial
	public static void generarPoblacionInicial(int[] solucionSemillaInicial, int cantidadSwappings, int tamanoPoblacion, Swap swap) {
		solucionInicial = solucionSemillaInicial.clone();//guardo la semilla para despu�s (restarts)
		poblacionActual = new ArrayList<>();
		for(int i=0;i<tamanoPoblacion;i++) {//genero la poblaci�n inicial a partir de una soluci�n inicial aleatoria como semilla 
			solucionSemillaInicial = swap.swapping(solucionSemillaInicial, cantidadSwappings);//avanzo dos espacios para generar un nuevo vecino a partior de la �ltima soluci�n generada
			poblacionActual.add(new IndividuoAlgoritmoGenetico(solucionSemillaInicial.clone(), swap.evaluarCostoSolucion(solucionSemillaInicial)));
		}
	}
	
	//Selecci�n: para obtener a los padres (individuos seleccionados por ruleta)
	public static ArrayList<IndividuoAlgoritmoGenetico> seleccionarPorRuleta(ArrayList<IndividuoAlgoritmoGenetico> poblacionEntrada, Swap swap) {
		padresSeleccionados = new ArrayList<IndividuoAlgoritmoGenetico>();
		
		//sumamos todos los costos para armar ruleta
		sumaTotalCostosPoblacion = calcularCostosTotalesPoblacion(poblacionEntrada);//Calculo los costos totales de la poblaci�n (suma de todos los costos)
		
		//ruleta con calculo de fitness incluido
		random = new Random();
		double fitness = 0;
		for(int k=0;k<poblacionEntrada.size();k++) {
			double fitnessActual = evaluarFitnessIndividuo(poblacionEntrada.get(k), sumaTotalCostosPoblacion);
			double cotaMenor = fitness;
			double cotaMayor = fitness + fitnessActual;
			if(lanzarBolaRuleta(cotaMenor, cotaMayor, random.nextInt((int)sumaTotalCostosPoblacion))) {
				padresSeleccionados.add(new IndividuoAlgoritmoGenetico(poblacionEntrada.get(k).getSolucion().clone(), swap.evaluarCostoSolucion(poblacionEntrada.get(k).getSolucion())));
			}
			fitness+=fitnessActual;
		}
		return padresSeleccionados;
	}
	
	//Reproducci�n (Cross-over)
	public static int[] generarNuevoIndividuoCrossOver(int[] padre, int[] madre, int cantidadCrossOvers) {
		hijo = new int[padre.length];
		//seteo las posiciones donde se le realizar� el crossover al padre para fijar esas caracter�sticas
		random = new Random();
		puntosCrossOver = new int[cantidadCrossOvers];
		for(int i=0;i<cantidadCrossOvers;i++) {
			int puntoCrossOver = random.nextInt(padre.length);
			boolean esta = false;
			for(int o=0;o<puntosCrossOver.length;o++) {
				if(puntosCrossOver[o]==puntoCrossOver) {
					esta = true;//verifico que no haya generado aleatoriamente el mismo punto dos o m�s veces
				}
			}
			if(!esta) {
				puntosCrossOver[i] = puntoCrossOver;
			}
		}
		Arrays.sort(puntosCrossOver);
		//hago el crossover en las posiciones antes determinadas (por el momento solo hago 2: modificar el c�digo si se quiere hacer m�s de 2)
		if(puntosCrossOver.length>1) {
			//saco caracter�sticas del padre
			for(int k=0;k<puntosCrossOver[0];k++) {//punto inicial de crossover
				hijo[k] = padre[k];//las agrego al hijo
			}
			for(int j=puntosCrossOver[1];j<padre.length;j++) {//segundo punto de crossover
				hijo[j] = padre[j];//las agrego al hijo
			}
			//saco caracter�sticas de la madre
			for(int h=puntosCrossOver[0];h<puntosCrossOver[1];h++) {
				for(int m=0;m<madre.length;m++) {
					boolean esta = false;
					for(int g=0;g<hijo.length;g++) {
						if(hijo[g]==madre[m]) {
							esta = true;//verifico que no est� la caracter�stica ya en el hijo
						}
					}
					if(!esta) {
						hijo[h] = madre[m];//las agrego al hijo
					}
				}
			}
		}else{
			System.out.println("debe fijar 2 puntos para el crossover");
		}
		return hijo;
	}
	
	//Reproducci�n (operador swap)
	public static int[] generarNuevoIndividuoMutacion(int[] solucion, int cantidadSwappings, Swap swap) {
		return swap.swapping(solucion, cantidadSwappings);
		//***Aqu� puedo incluir una metaheur�stica de soluci�n �nica que mejore la mutaci�n
	}
	
	//M�todo que clasifica a los padres de acuerdo a su costo. A los 25% mejores y 25% peores se les aplicar� mutaci�n
	//de 2 swap. Al 50% del medio se les har� crossover
	public static ArrayList<IndividuoAlgoritmoGenetico> obtenerNuevaPoblacion(ArrayList<IndividuoAlgoritmoGenetico> poblacionEntrada, int cantidadSwappings, Swap swap){
		nuevaPoblacion = new ArrayList<>();//inicializo la nueva poblaci�n
		poblacionEntrada.sort(Comparator.comparingDouble(IndividuoAlgoritmoGenetico::getCosto));//Se ordenan de menor costo a mayor costo
		//Marco los cortes que se haran a la colecci�n de padres
		int corteMejores = (int)(poblacionEntrada.size()*0.25);
		int cortePeores = (int)(poblacionEntrada.size()*0.75);
		
		//Aplico 2-swap a los padres 25% mejores y agrego al arraylist de nueva poblaci�n
		for(int i=0;i<corteMejores;i++) {
			nuevaPoblacion.add(new IndividuoAlgoritmoGenetico(generarNuevoIndividuoMutacion(poblacionEntrada.get(i).getSolucion(), cantidadSwappings, swap), swap.evaluarCostoSolucion(poblacionEntrada.get(i).getSolucion())));
		}
		
		//Aplico crossover entre los padres del 50% del medio
		for(int k=corteMejores;k<cortePeores;k++) {
			
		}
		
		//Aplico 2-swap a los padres 25% peores y agrego al arraylist de nueva poblaci�n
		for(int j=cortePeores;j<poblacionEntrada.size();j++) {
			nuevaPoblacion.add(new IndividuoAlgoritmoGenetico(generarNuevoIndividuoMutacion(poblacionEntrada.get(j).getSolucion(), cantidadSwappings, swap), swap.evaluarCostoSolucion(poblacionEntrada.get(j).getSolucion())));
		}
		
		return nuevaPoblacion;
	}
	
	public static double calcularCostosTotalesPoblacion(ArrayList<IndividuoAlgoritmoGenetico> poblacionEntrada) {
		sumaTotalCostosPoblacion = 0;
		for(int i=0; i<poblacionEntrada.size(); i++) {
			//la poblaci�n de entrada ya viene con los costos seteados			
			sumaTotalCostosPoblacion+=poblacionEntrada.get(i).getCosto();			
		}
		return sumaTotalCostosPoblacion;
	}	
	
	public static boolean lanzarBolaRuleta(double cotaMenor, double cotaMayor, int lanzamiento) {
		return (cotaMenor<=lanzamiento) && (lanzamiento<cotaMayor);
	}
	
	public static double evaluarFitnessIndividuo(IndividuoAlgoritmoGenetico individuo, double sumaTotalCostosPoblacion) {
		return sumaTotalCostosPoblacion - individuo.getCosto();
	}
	
	//calculo el fitness promedio de todos los individuos de la poblaci�n
	public static double evaluarFitnessPromedioPoblacion(ArrayList<IndividuoAlgoritmoGenetico> poblacion) {
		totalFitness = 0;
		sumaTotalCostosPoblacion = calcularCostosTotalesPoblacion(poblacion);
		for(int i=0;i<poblacion.size();i++) {
			totalFitness += evaluarFitnessIndividuo(poblacion.get(i), sumaTotalCostosPoblacion);
		}
		return totalFitness/poblacion.size();
	}
	
	//Reemplazo: �a qui�n dejo para la siguiente iteraci�n?
	//calculo el fitness promedio de la poblaci�n actual y nueva. Si el fitness de la nueva es mejor que el de la poblaci�n actual, la reemplazo por la nueva. Si no, no
	public static ArrayList<IndividuoAlgoritmoGenetico> reemplazarPoblacionActual(ArrayList<IndividuoAlgoritmoGenetico> poblacionActual, ArrayList<IndividuoAlgoritmoGenetico> nuevaPoblacion) {
		if(evaluarFitnessPromedioPoblacion(nuevaPoblacion)>evaluarFitnessPromedioPoblacion(poblacionActual)) {
			return nuevaPoblacion;
		}else {
			return poblacionActual;
		}
	}
	
	//Restart
	public static void realizarRestartPoblacion(ArrayList<IndividuoAlgoritmoGenetico> poblacionActual, int[] solucionInicial, double porcentajeAGuardarMejoresSolucionesEnMemoria, int tamanoPoblacion, Swap swap) {
		poblacionActual.sort(Comparator.comparingDouble(IndividuoAlgoritmoGenetico::getCosto));
		int tamanoPoblacionActual = poblacionActual.size();
		double cantidadAGuardar = (int)((porcentajeAGuardarMejoresSolucionesEnMemoria/100)*tamanoPoblacionActual);
		memoriaMejoresSolucionesOrdenada = new ArrayList<>();//conservo el X% mejor definido por el parametro porcentajeAGuardarMejoresSolucionesEnMemoria
		for(int i=0;i<cantidadAGuardar;i++) {
			memoriaMejoresSolucionesOrdenada.add(new IndividuoAlgoritmoGenetico(poblacionActual.get(i).getSolucion().clone(), swap.evaluarCostoSolucion(poblacionActual.get(i).getSolucion())));
		}
		//reinicio la poblaci�n
		generarPoblacionInicial(swap.generarSolcuionInicial(swap.getMatrizF()), cantidadSwappingsAG, tamanoPoblacion, swap);
	}
	
	//M�todo que calcula el mejor costo a partir de todas las soluciones de una poblaci�n
	public static double obtenerMejorCostoPoblacion(ArrayList<IndividuoAlgoritmoGenetico> poblacion) {
		double mejorCostoPoblacion = -1;//menos 1 si no hay elementos en la poblaci�n
		poblacion.sort(Comparator.comparingDouble(IndividuoAlgoritmoGenetico::getCosto));//ordeno la poblaci�n de menor a mayor costo
		if(poblacion.size()>0) {
			mejorCostoPoblacion = poblacion.get(0).getCosto();//retorno el costo del primer individuo de la poblaci�n ordenada de menor a mayor costo 
		}
		return mejorCostoPoblacion;
	}
	
	@SuppressWarnings("unchecked")
	public static ArrayList<Costos> algoritmoGenetico(int[] solucionSemillaInicial, int cantidadSwappings, int tamanoPoblacion, int criterioParada, int valorDetencion, Swap swap) {
		ArrayList<Costos> costos = new ArrayList<>();
		cantidadSwappingsAG = cantidadSwappings;
		long startTime = System.nanoTime();// Contador de tiempo
		
		//genero poblaci�n incial
		generarPoblacionInicial(solucionSemillaInicial, cantidadSwappings, tamanoPoblacion, swap);
		
		//itero hasta alcanzar el n�mero de generaciones prefijado (fijo)
		//o hasta que pasen 20 generaciones sin cambio (adaptativo)
		int contadorGeneraciones = 0;
		int contadorMismoCosto = 0;
		while(true) {
			contadorGeneraciones++;
			
			//guardo el costo de la iteraci�n anterior
			copiaPoblacionActual = (ArrayList<IndividuoAlgoritmoGenetico>) poblacionActual.clone();
			mejorCostoIteracionAnterior = obtenerMejorCostoPoblacion(copiaPoblacionActual);//Evaluaci�n de la poblaci�n P(t)
			//***Aqu� puedo poner una metaheur�stica de soluci�n �nica para mejorar la mejor soluci�n obtenida hasta ahora
			
			//Selecciono a los padres que cruzar� para obtener la poblaci�n que eventualmente reemplazar� a la actual
			padresSeleccionados = seleccionarPorRuleta(poblacionActual, swap);
			
			//Reproduzco los padres entre s� para obtener la poblaci�n P'(t)
			
			
			
			//guardo el costo de la iteraci�n actual
			copiaNuevaPoblacion = (ArrayList<IndividuoAlgoritmoGenetico>) nuevaPoblacion.clone();
			mejorCostoIteracionActual = obtenerMejorCostoPoblacion(copiaNuevaPoblacion);//Evaluaci�n de la poblaci�n P'(t)
					
			//Criterios de parada
			if(criterioParada==CRITERIO_PARADA_NUMERO_FIJO_GENERACIONES) {			
				if(contadorGeneraciones==valorDetencion) {//Criterio numero de generaciones fijo
					break;
				}
			}else if(criterioParada==CRITERIO_PARADA_ADAPTATIVO) {
				//seteo el contador para el criterio de parada adaptativo
				if(mejorCostoIteracionAnterior==mejorCostoIteracionActual) {
					contadorGeneraciones++;
				}else if(mejorCostoIteracionAnterior>mejorCostoIteracionActual){
					contadorGeneraciones=0;
				}
				
				if(contadorMismoCosto==20) {//Criterio 20 generaciones sin encontrar un mejor costo
					break;
				}
			}
		}
		
		// tiempo de ejecuci�n
		long endTime = System.nanoTime();
		long totalTime = (endTime - startTime) / 1000000;
		//System.out.println("tiempo ejecuci�n: " + totalTime + " milisegundos");
				
		return costos;//Fin
	}
}
