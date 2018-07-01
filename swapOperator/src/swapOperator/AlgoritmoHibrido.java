package swapOperator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Random;

public class AlgoritmoHibrido {
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
	static double mejorCostoHistoricoRestarts;
	static int[] mejorSolucionHistorica;
	static int[] mejorSolucionHistoricaRestarts;
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
	static TabuSearch tabuSearchHibrido;
	
	public static final int CRITERIO_PARADA_NUMERO_FIJO_GENERACIONES = 0;
	public static final int CRITERIO_PARADA_ADAPTATIVO = 1;
	
	public static void reset() {
		hijo=null;
		solucionMutada=null;
		solucionInicial=null;
		padresCrossOver=null;
		madresCrossOver=null;
		costoSolucionInicial=0;
		padresSeleccionados=null;
		probabilidadSolucion=0;
		mejorCostoIteracionAnterior=0;
		mejorCostoIteracionActual=0;
		mejorCostoHistorico=0;
		mejorCostoHistoricoRestarts=0;
		mejorSolucionHistorica=null;
		mejorSolucionHistoricaRestarts=null;
		fitnessPromedio=0;
		memoriaMejoresSolucionesOrdenada=null;
		poblacionActual=null;
		nuevaPoblacion=null;
		copiaPoblacionActual=null;
		poblacionSalida=null;
		sumaTotalCostosPoblacion=0;
		random=null;
		puntosCrossOver=null;
		mejorSolucion=null;
		cantidadSwappingsAG=0;
		totalFitness=0;
		tabuSearchHibrido=null;
	}
	
	//Poblaci�n inicial
	public static void generarPoblacionInicial(int[] solucionSemillaInicial, int cantidadSwappings, int tamanoPoblacion, Swap swap, boolean incluirMemoriaPrevia) {
		solucionInicial = solucionSemillaInicial.clone();//guardo la semilla para despu�s (restarts)
		poblacionActual = new ArrayList<>();
		for(int i=0;i<tamanoPoblacion;i++) {//genero la poblaci�n inicial a partir de una soluci�n inicial aleatoria como semilla 
			solucionSemillaInicial = swap.swapping(solucionSemillaInicial, cantidadSwappings);//avanzo dos espacios para generar un nuevo vecino a partior de la �ltima soluci�n generada
			poblacionActual.add(new IndividuoAlgoritmoGenetico(solucionSemillaInicial.clone(), swap.evaluarCostoSolucion(solucionSemillaInicial)));
		}
		
		//incluyo la memoria de iteraciones previas
		if(incluirMemoriaPrevia && memoriaMejoresSolucionesOrdenada!=null) {
			poblacionActual.addAll((ArrayList<IndividuoAlgoritmoGenetico>) memoriaMejoresSolucionesOrdenada.clone());
		}
	}
	
	//Selecci�n: para obtener a los padres (individuos seleccionados por ruleta)
	public static ArrayList<IndividuoAlgoritmoGenetico> seleccionarPorRuleta(ArrayList<IndividuoAlgoritmoGenetico> poblacionEntrada, Swap swap, double ponderadorCantidadDePadres) {
		padresSeleccionados = new ArrayList<IndividuoAlgoritmoGenetico>();
		
		//sumamos todos los costos para armar ruleta
		sumaTotalCostosPoblacion = calcularCostosTotalesPoblacion(poblacionEntrada);//Calculo los costos totales de la poblaci�n (suma de todos los costos)
		
		//ruleta con calculo de fitness incluido
		random = new Random();
		int[] ruleta = new int[poblacionEntrada.size()];
		int acumulado = 0;
		for(int k=0;k<poblacionEntrada.size();k++) {
			acumulado += (int)sumaTotalCostosPoblacion - (int)evaluarFitnessIndividuo(poblacionEntrada.get(k), sumaTotalCostosPoblacion);
			ruleta[k] = acumulado;
		}
		
		for(int x=0;x<poblacionEntrada.size()*ponderadorCantidadDePadres;x++) {//cuantos padres seleccionamos
			int lanzamiento = random.nextInt((int)sumaTotalCostosPoblacion);
			for(int i=0;i<poblacionEntrada.size()-1;i++) {
				if(ruleta[i]<lanzamiento && lanzamiento<=ruleta[i+1]) {
					padresSeleccionados.add(new IndividuoAlgoritmoGenetico(poblacionEntrada.get(i+1).getSolucion().clone(), swap.evaluarCostoSolucion(poblacionEntrada.get(i+1).getSolucion())));
				}
			}
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
	public static ArrayList<IndividuoAlgoritmoGenetico> reproducir(ArrayList<IndividuoAlgoritmoGenetico> poblacionEntrada, int cantidadSwappings, Swap swap, double porcentajeCorteMenor, double porcentajeCorteMayor, int tamanoPoblacion){
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
				padresCrossOver.add(new IndividuoAlgoritmoGenetico(poblacionEntrada.get(k).getSolucion(), 0.0));
			}else {
				madresCrossOver.add(new IndividuoAlgoritmoGenetico(poblacionEntrada.get(k).getSolucion(), 0.0));
			}
		}
		//cruzo al padre x con la madre x
		if(padresCrossOver.size()>madresCrossOver.size()) {//si la cantidad de padres es distinta a la de madres, tomo la menor y no cruzo al individuo que sobra
			for(int x=0;x<madresCrossOver.size();x++) {
				nuevaPoblacion.add(new IndividuoAlgoritmoGenetico(generarNuevoIndividuoCrossOver(padresCrossOver.get(x).getSolucion(), madresCrossOver.get(x).getSolucion(), 2), 0.0));
				nuevaPoblacion.add(new IndividuoAlgoritmoGenetico(generarNuevoIndividuoCrossOver(madresCrossOver.get(x).getSolucion(), padresCrossOver.get(x).getSolucion(), 2), 0.0));
			}
		}else if(padresCrossOver.size()<=madresCrossOver.size()) {
			for(int x=0;x<padresCrossOver.size();x++) {
				nuevaPoblacion.add(new IndividuoAlgoritmoGenetico(generarNuevoIndividuoCrossOver(padresCrossOver.get(x).getSolucion(), madresCrossOver.get(x).getSolucion(), 2), 0.0));
				nuevaPoblacion.add(new IndividuoAlgoritmoGenetico(generarNuevoIndividuoCrossOver(madresCrossOver.get(x).getSolucion(), padresCrossOver.get(x).getSolucion(), 2), 0.0));
			}
		}
		
		//Aplico 2-swap a los padres 25% peores y agrego al arraylist de nueva poblaci�n
		for(int j=cortePeores;j<poblacionEntrada.size();j++) {
			nuevaPoblacion.add(new IndividuoAlgoritmoGenetico(generarNuevoIndividuoMutacion(poblacionEntrada.get(j).getSolucion(), cantidadSwappings, swap), 0.0));
		}
		
		while(nuevaPoblacion.size()<=tamanoPoblacion) {
			nuevaPoblacion.add(new IndividuoAlgoritmoGenetico(swap.generarSolcuionInicial(swap.getMatrizF()), 0.0));
		}
//			if(tamanoPoblacion>nuevaPoblacion.size()) {
//				for(int a=0;a<tamanoPoblacion-nuevaPoblacion.size();a++) {
//					nuevaPoblacion.add(new IndividuoAlgoritmoGenetico(swap.generarSolcuionInicial(swap.getMatrizF()), 0.0));
//				}
//			}
		
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
	
	public static boolean lanzarBolaRuleta(double cotaMenor, double cotaMayor, int lanzamiento, double sumarRuleta) {
		return (cotaMenor<=lanzamiento) && (lanzamiento<(cotaMayor+sumarRuleta));
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
	public static ArrayList<IndividuoAlgoritmoGenetico> reemplazarPoblacionActualFitnessPromedio(ArrayList<IndividuoAlgoritmoGenetico> poblacionActual, ArrayList<IndividuoAlgoritmoGenetico> nuevaPoblacion) {
		if(evaluarFitnessPromedioPoblacion(nuevaPoblacion)>evaluarFitnessPromedioPoblacion(poblacionActual)) {
			return nuevaPoblacion;
		}else {
			return poblacionActual;
		}
	}
	
	//Reemplazo por mejor costo obtenido con la poblaci�n
	public static ArrayList<IndividuoAlgoritmoGenetico> reemplazarPoblacionActualMejorCosto(ArrayList<IndividuoAlgoritmoGenetico> poblacionActual, ArrayList<IndividuoAlgoritmoGenetico> nuevaPoblacion) {
		if(obtenerMejorCostoPoblacion(nuevaPoblacion)<obtenerMejorCostoPoblacion(poblacionActual)) {
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
			if(mejorCostoHistorico==0.0 || mejorCostoHistorico>mejorCostoPoblacion) {
				mejorSolucionHistorica = poblacion.get(0).getSolucion();//guardo la mejor soluci�n de esta poblaci�n				
			}
			if(mejorCostoHistoricoRestarts==0.0 || mejorCostoHistoricoRestarts>mejorCostoPoblacion) {
				mejorSolucionHistoricaRestarts = poblacion.get(0).getSolucion();
			}
		}
		return mejorCostoPoblacion;
	}
	
	public static ArrayList<IndividuoAlgoritmoGenetico> realizarTabuSearchPoblacion(ArrayList<IndividuoAlgoritmoGenetico> nuevaPoblacion, Swap swap, int duracionTabuList, int iteraciones, int profundidadIntensificacion, boolean intensificacion, boolean diversificacion, int cantidadDiversificacion) {
		for(int i=0;i<nuevaPoblacion.size();i++) {
			//System.out.println("Individuo de la poblaci�n en Tabu search: "+i);
			nuevaPoblacion.get(i).setSolucion(tabuSearchHibrido.TabuSearchHibrido(nuevaPoblacion.get(i).getSolucion(), swap, duracionTabuList, iteraciones, profundidadIntensificacion, intensificacion, diversificacion, i, cantidadDiversificacion));
		}
		return nuevaPoblacion;
	}
	
	@SuppressWarnings("unchecked")
	public static ArrayList<Double> algoritmoHibrido(int[] solucionSemillaInicial, int cantidadSwappings, int tamanoPoblacion, int criterioParada, int valorDetencion, Swap swap, double porcentajeCorteMenor, double porcentajeCorteMayor, boolean incluirMemoriaPrevia, int numeroDeRestarts, double porcentajeAGuardarMejoresSolucionesEnMemoria, double ponderadorCantidadDePadres, int maximoIteracionesAdaptativo, int duracionTabuList, int iteraciones, int profundidadIntensificacion, boolean intensificacion, boolean diversificacion, int iteracionesIntensificacion) {
		ArrayList<Double> costos = new ArrayList<>();
		cantidadSwappingsAG = cantidadSwappings;
		int restarts = numeroDeRestarts;
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
			copiaPoblacionActual = (ArrayList<IndividuoAlgoritmoGenetico>) poblacionActual.clone();
			padresSeleccionados = seleccionarPorRuleta(copiaPoblacionActual, swap, ponderadorCantidadDePadres);
			
			//Reproduzco los padres entre s� para obtener la poblaci�n P'(t)
			nuevaPoblacion = reproducir(padresSeleccionados, cantidadSwappings, swap, porcentajeCorteMenor, porcentajeCorteMayor, tamanoPoblacion);
			
			//Inserto un Tabu Search para cada individuo de la nueva poblaci�n, con el objetivo de mejorar fitness de la nueva poblaci�n
			copiaNuevaPoblacion = (ArrayList<IndividuoAlgoritmoGenetico>) nuevaPoblacion.clone();
			nuevaPoblacion = realizarTabuSearchPoblacion(copiaNuevaPoblacion, swap, duracionTabuList, iteraciones, profundidadIntensificacion, intensificacion, diversificacion, iteracionesIntensificacion);
			
			//Eval�o la nueva poblaci�n
			nuevaPoblacion = evaluarCostosDeCadaIndividuoDeLaPoblacion(nuevaPoblacion,swap);//retorno la nueva poblaci�n con la evaluaci�n de los costos de cada individuo
			
			//guardo el costo de la iteraci�n actual
			copiaNuevaPoblacion = (ArrayList<IndividuoAlgoritmoGenetico>) nuevaPoblacion.clone();
			mejorCostoIteracionActual = obtenerMejorCostoPoblacion(copiaNuevaPoblacion);//Evaluaci�n de la poblaci�n P'(t)
			
			if(mejorCostoIteracionActual==-1) {//cuando la poblaci�n se reduce a cero
				break;
			}
			
			if(mejorCostoHistorico==0.0 || mejorCostoHistorico>mejorCostoIteracionActual) {
				mejorCostoHistorico=mejorCostoIteracionActual;								
			}
			
			if(mejorCostoHistoricoRestarts==0 || mejorCostoHistoricoRestarts>mejorCostoHistorico) {
				mejorCostoHistoricoRestarts=mejorCostoHistorico;
			}
			
			costos.add(mejorCostoHistorico);//guardo el mejor costo	
			/**if(mejorCostoHistorico==-1) {
				mejorCostoHistorico = 0;
			}*/
			System.out.println("Generaci�n "+contadorGeneraciones+" - Mejor costo: "+mejorCostoHistorico);
			
			//eval�o si reemplazo la poblaci�n actual por la nueva poblaci�n
			//poblacionActual = reemplazarPoblacionActualFitnessPromedio(copiaPoblacionActual, copiaNuevaPoblacion);
			copiaPoblacionActual = (ArrayList<IndividuoAlgoritmoGenetico>) poblacionActual.clone();
			copiaNuevaPoblacion = (ArrayList<IndividuoAlgoritmoGenetico>) nuevaPoblacion.clone();
			poblacionActual = reemplazarPoblacionActualMejorCosto(copiaPoblacionActual, copiaNuevaPoblacion);
			
			//Criterios de parada
			if(criterioParada==CRITERIO_PARADA_NUMERO_FIJO_GENERACIONES) {			
				if(!(contadorGeneraciones<valorDetencion)) {//Criterio numero de generaciones fijo
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
				if(mejorCostoIteracionAnterior<=mejorCostoIteracionActual) {
					contadorMismoCosto++;
				}else if(mejorCostoIteracionAnterior>mejorCostoIteracionActual){
					contadorMismoCosto=0;
				}
				
				if(!(contadorMismoCosto<maximoIteracionesAdaptativo)) {//Criterio maximoIteracionesAdaptativo generaciones sin encontrar un mejor costo
					if(numeroDeRestarts>-1) {
						if(numeroDeRestarts>0) {
							realizarRestartPoblacion(copiaPoblacionActual, solucionInicial, porcentajeAGuardarMejoresSolucionesEnMemoria, tamanoPoblacion, swap, incluirMemoriaPrevia);
							numeroDeRestarts--;
							mejorCostoHistorico = 0.0;
						}else {
							break;
						}					
					}else {
						break;
					}
				}
			}		
		}
		
		System.out.println("Mejor costo hist�rico encontrado: " + swap.evaluarCostoSolucion(mejorSolucionHistoricaRestarts));
		System.out.print("Mejor soluci�n hist�rica encontrada: ");
		swap.toStringSolucion(mejorSolucionHistoricaRestarts,1);
		System.out.println("N�mero de generaciones evaluadas: "+contadorGeneraciones);
		System.out.println("N�mero de restarts: "+(restarts-numeroDeRestarts));

		// tiempo de ejecuci�n
		long endTime = System.nanoTime();
		long totalTime = (endTime - startTime) / 1000000;
		//System.out.println("tiempo ejecuci�n: " + totalTime + " milisegundos");
				
		reset();
		
		return costos;//Fin
	}
}