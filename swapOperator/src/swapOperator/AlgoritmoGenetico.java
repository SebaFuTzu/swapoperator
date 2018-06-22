package swapOperator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Random;

public class AlgoritmoGenetico {
	static int[] hijo;
	static int[] solucionMutada;
	static int[] solucionInicial;
	static ArrayList<IndividuoAlgoritmoGenetico> padresCrossOver;
	static ArrayList<IndividuoAlgoritmoGenetico> madresCrossOver;
	static double costoSolucionInicial;
	static ArrayList<IndividuoAlgoritmoGenetico> padresSeleccionados;
	static double probabilidadSolucion;
	static double mejorCostoIteracionAnterior;
	static double mejorCostoIteracionActual;
	static double mejorCostoHistorico;
	static int[] mejorSolucionHistorica;
	static double fitnessPromedio;
	static ArrayList<IndividuoAlgoritmoGenetico> memoriaMejoresSolucionesOrdenada;
	static ArrayList<IndividuoAlgoritmoGenetico> poblacionActual;
	static ArrayList<IndividuoAlgoritmoGenetico> nuevaPoblacion;
	static ArrayList<IndividuoAlgoritmoGenetico> copiaPoblacionActual;
	static ArrayList<IndividuoAlgoritmoGenetico> copiaNuevaPoblacion;
	static ArrayList<IndividuoAlgoritmoGenetico> poblacionSalida;
	static double sumaTotalCostosPoblacion;
	static Random random;
	static int[] puntosCrossOver;
	static IndividuoAlgoritmoGenetico mejorSolucion;
	static int cantidadSwappingsAG;
	static double totalFitness;
	
	public static final int CRITERIO_PARADA_NUMERO_FIJO_GENERACIONES = 0;
	public static final int CRITERIO_PARADA_ADAPTATIVO = 1;
	
	//Poblaci�n inicial
	public static void generarPoblacionInicial(int[] solucionSemillaInicial, int cantidadSwappings, int tamanoPoblacion, Swap swap, boolean incluirMemoriaPrevia) {
		solucionInicial = solucionSemillaInicial.clone();//guardo la semilla para despu�s (restarts)
		poblacionActual = new ArrayList<>();
		for(int i=0;i<tamanoPoblacion;i++) {//genero la poblaci�n inicial a partir de una soluci�n inicial aleatoria como semilla 
			solucionSemillaInicial = swap.swapping(solucionSemillaInicial, cantidadSwappings);//avanzo dos espacios para generar un nuevo vecino a partior de la �ltima soluci�n generada
			poblacionActual.add(new IndividuoAlgoritmoGenetico(solucionSemillaInicial.clone(), swap.evaluarCostoSolucion(solucionSemillaInicial)));
		}
		
		//incluyo la memoria de iteraciones previas
		if(incluirMemoriaPrevia) {
			poblacionActual.addAll((ArrayList<IndividuoAlgoritmoGenetico>) memoriaMejoresSolucionesOrdenada.clone());
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
	public static ArrayList<IndividuoAlgoritmoGenetico> reproducir(ArrayList<IndividuoAlgoritmoGenetico> poblacionEntrada, int cantidadSwappings, Swap swap, double porcentajeCorteMenor, double porcentajeCorteMayor){
		nuevaPoblacion = new ArrayList<>();//inicializo la nueva poblaci�n
		poblacionEntrada.sort(Comparator.comparingDouble(IndividuoAlgoritmoGenetico::getCosto));//Se ordenan de menor costo a mayor costo
		//Marco los cortes que se haran a la colecci�n de padres
		int corteMejores = (int)(poblacionEntrada.size()*porcentajeCorteMenor);
		int cortePeores = (int)(poblacionEntrada.size()*porcentajeCorteMayor);
		
		//Aplico 2-swap a los padres 25% mejores y agrego al arraylist de nueva poblaci�n
		for(int i=0;i<corteMejores;i++) {
			nuevaPoblacion.add(new IndividuoAlgoritmoGenetico(generarNuevoIndividuoMutacion(poblacionEntrada.get(i).getSolucion(), cantidadSwappings, swap), 0.0));
		}
		
		//Aplico crossover entre los padres del 50% del medio
		padresCrossOver = new ArrayList<>();
		madresCrossOver = new ArrayList<>();
		for(int k=corteMejores;k<cortePeores;k++) {//separo a los pap�s de las mam�s
			if(k%2==0) {
				padresCrossOver.add(new IndividuoAlgoritmoGenetico(generarNuevoIndividuoMutacion(poblacionEntrada.get(k).getSolucion(), cantidadSwappings, swap), 0.0));
			}else {
				madresCrossOver.add(new IndividuoAlgoritmoGenetico(generarNuevoIndividuoMutacion(poblacionEntrada.get(k).getSolucion(), cantidadSwappings, swap), 0.0));
			}
		}
		//cruzo al padre x con la madre x
		if(padresCrossOver.size()>madresCrossOver.size()) {//si la cantidad de padres es distinta a la de madres, tomo la menor y no cruzo al individuo que sobra
			for(int x=0;x<madresCrossOver.size();x++) {
				nuevaPoblacion.add(new IndividuoAlgoritmoGenetico(generarNuevoIndividuoCrossOver(padresCrossOver.get(x).getSolucion(), madresCrossOver.get(x).getSolucion(), 2), 0.0));
			}
		}else if(padresCrossOver.size()<=madresCrossOver.size()) {
			for(int x=0;x<padresCrossOver.size();x++) {
				nuevaPoblacion.add(new IndividuoAlgoritmoGenetico(generarNuevoIndividuoCrossOver(padresCrossOver.get(x).getSolucion(), madresCrossOver.get(x).getSolucion(), 2), 0.0));
			}
		}
		
		//Aplico 2-swap a los padres 25% peores y agrego al arraylist de nueva poblaci�n
		for(int j=cortePeores;j<poblacionEntrada.size();j++) {
			nuevaPoblacion.add(new IndividuoAlgoritmoGenetico(generarNuevoIndividuoMutacion(poblacionEntrada.get(j).getSolucion(), cantidadSwappings, swap), 0.0));
		}
		
		return nuevaPoblacion;
	}
	
	public static ArrayList<IndividuoAlgoritmoGenetico> evaluarCostosDeCadaIndividuoDeLaPoblacion(ArrayList<IndividuoAlgoritmoGenetico> poblacionEntrada, Swap swap){
		for(int i=0;i<poblacionEntrada.size();i++) {
			poblacionEntrada.get(i).setCosto(swap.evaluarCostoSolucion(poblacionEntrada.get(i).getSolucion()));
		}
		return poblacionEntrada;
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
	public static void realizarRestartPoblacion(ArrayList<IndividuoAlgoritmoGenetico> poblacionActual, int[] solucionInicial, double porcentajeAGuardarMejoresSolucionesEnMemoria, int tamanoPoblacion, Swap swap, boolean incluirMemoriaPrevia) {
		poblacionActual.sort(Comparator.comparingDouble(IndividuoAlgoritmoGenetico::getCosto));
		int tamanoPoblacionActual = poblacionActual.size();
		double cantidadAGuardar = (int)(porcentajeAGuardarMejoresSolucionesEnMemoria*tamanoPoblacionActual);
		memoriaMejoresSolucionesOrdenada = new ArrayList<>();//conservo el X% mejor definido por el parametro porcentajeAGuardarMejoresSolucionesEnMemoria
		for(int i=0;i<cantidadAGuardar;i++) {
			memoriaMejoresSolucionesOrdenada.add(new IndividuoAlgoritmoGenetico(poblacionActual.get(i).getSolucion().clone(), swap.evaluarCostoSolucion(poblacionActual.get(i).getSolucion())));
		}
		//reinicio la poblaci�n
		generarPoblacionInicial(swap.generarSolcuionInicial(swap.getMatrizF()), cantidadSwappingsAG, tamanoPoblacion, swap, incluirMemoriaPrevia);
	}
	
	//M�todo que calcula el mejor costo a partir de todas las soluciones de una poblaci�n
	public static double obtenerMejorCostoPoblacion(ArrayList<IndividuoAlgoritmoGenetico> poblacion) {
		double mejorCostoPoblacion = -1;//menos 1 si no hay elementos en la poblaci�n
		poblacion.sort(Comparator.comparingDouble(IndividuoAlgoritmoGenetico::getCosto));//ordeno la poblaci�n de menor a mayor costo
		if(poblacion.size()>0) {
			mejorCostoPoblacion = poblacion.get(0).getCosto();//retorno el costo del primer individuo de la poblaci�n ordenada de menor a mayor costo
			if(mejorCostoHistorico>mejorCostoPoblacion) {
				mejorSolucionHistorica = poblacion.get(0).getSolucion();//guardo la mejor soluci�n de esta poblaci�n
			}
		}
		return mejorCostoPoblacion;
	}
	
	@SuppressWarnings("unchecked")
	public static ArrayList<Double> algoritmoGenetico(int[] solucionSemillaInicial, int cantidadSwappings, int tamanoPoblacion, int criterioParada, int valorDetencion, Swap swap, double porcentajeCorteMenor, double porcentajeCorteMayor, boolean incluirMemoriaPrevia, int numeroDeRestarts, double porcentajeAGuardarMejoresSolucionesEnMemoria) {
		ArrayList<Double> costos = new ArrayList<>();
		cantidadSwappingsAG = cantidadSwappings;
		long startTime = System.nanoTime();// Contador de tiempo
		
		//genero poblaci�n incial
		generarPoblacionInicial(solucionSemillaInicial, cantidadSwappings, tamanoPoblacion, swap, incluirMemoriaPrevia);
		
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
			nuevaPoblacion = reproducir(padresSeleccionados, cantidadSwappings, swap, porcentajeCorteMenor, porcentajeCorteMayor);
			
			//Eval�o la nueva poblaci�n
			nuevaPoblacion = evaluarCostosDeCadaIndividuoDeLaPoblacion(nuevaPoblacion,swap);//retorno la nueva poblaci�n con la evaluaci�n de los costos de cada individuo
			
			//guardo el costo de la iteraci�n actual
			copiaNuevaPoblacion = (ArrayList<IndividuoAlgoritmoGenetico>) nuevaPoblacion.clone();
			mejorCostoIteracionActual = obtenerMejorCostoPoblacion(copiaNuevaPoblacion);//Evaluaci�n de la poblaci�n P'(t)
			if(mejorCostoHistorico==0.0 || mejorCostoHistorico>mejorCostoIteracionActual) {
				mejorCostoHistorico=mejorCostoIteracionActual;
			}
			costos.add(mejorCostoIteracionActual);//guardo el mejor costo	
			
			//eval�o si reemplazo la poblaci�n actual por la nueva poblaci�n
			poblacionActual = reemplazarPoblacionActual(copiaPoblacionActual, copiaNuevaPoblacion);
			
			//Criterios de parada
			if(criterioParada==CRITERIO_PARADA_NUMERO_FIJO_GENERACIONES) {			
				if(contadorGeneraciones==valorDetencion) {//Criterio numero de generaciones fijo
					if(numeroDeRestarts>-1) {
						if(numeroDeRestarts>0) {
							realizarRestartPoblacion(copiaPoblacionActual, solucionInicial, porcentajeAGuardarMejoresSolucionesEnMemoria, tamanoPoblacion, swap, incluirMemoriaPrevia);
							numeroDeRestarts--;
						}else {
							break;
						}					
					}else {
						break;
					}
				}
			}else if(criterioParada==CRITERIO_PARADA_ADAPTATIVO) {
				//seteo el contador para el criterio de parada adaptativo
				if(mejorCostoIteracionAnterior==mejorCostoIteracionActual) {
					contadorGeneraciones++;
				}else if(mejorCostoIteracionAnterior>mejorCostoIteracionActual){
					contadorGeneraciones=0;
				}
				
				if(contadorMismoCosto==20) {//Criterio 20 generaciones sin encontrar un mejor costo
					if(numeroDeRestarts>-1) {
						if(numeroDeRestarts>0) {
							realizarRestartPoblacion(copiaPoblacionActual, solucionInicial, porcentajeAGuardarMejoresSolucionesEnMemoria, tamanoPoblacion, swap, incluirMemoriaPrevia);
							numeroDeRestarts--;
						}else {
							break;
						}					
					}else {
						break;
					}
				}
			}		
		}
		
		System.out.println("Mejor costo hist�rico encontrado: " + swap.evaluarCostoSolucion(mejorSolucionHistorica));
		System.out.print("Mejor soluci�n hist�rica encontrada: ");
		swap.toStringSolucion(mejorSolucionHistorica,1);

		// tiempo de ejecuci�n
		long endTime = System.nanoTime();
		long totalTime = (endTime - startTime) / 1000000;
		//System.out.println("tiempo ejecuci�n: " + totalTime + " milisegundos");
				
		return costos;//Fin
	}
}
