package swapOperator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class TabuSearch {

	static HashMap<ItemTabu, Integer> listaTabu;
	static HashMap<ItemTabu, Double> memoriaFrecuencias;
	static HashMap<ItemTabu, Double> listaCandidatos;
	static int[] solucionActual;
	static int[] valoresSwapped;
	static int cantidadSwappings;
	static int tamanoVecindad;
	static double costoSolucionActual;
	static double costoSolucionInicial;
	static ArrayList<ItemTabu> prioridadEvaluacion;
	static ItemTabu mejorSolucion;
	static double penalizacion;
	static Costos costo;
	static double mejorCostoHistorico;

	public static ArrayList<Double> TabuSearch(int[] solucionInicial, Swap swap, int duracionTabuList, int iteraciones) {
		// definición de objetos y variables
		cantidadSwappings = 2;
		valoresSwapped = new int[cantidadSwappings];
		mejorCostoHistorico = 0;

		ArrayList<Double> costos = new ArrayList<Double>();
		long startTime = System.nanoTime();// Contador de tiempo

		//tamanoVecindad = swap.calcularTamañoVecindad(swap.getMatrizF(), cantidadSwappings);
		inicializarListaTabu(solucionInicial);
		inicializarMemoriaFrecuencias(solucionInicial);
		inicializarListaCandidatos(solucionInicial);

		costoSolucionInicial = swap.evaluarCostoSolucion(solucionInicial);
		costos.add(costoSolucionInicial);
		
		mejorCostoHistorico=costoSolucionInicial;

		while(iteraciones>0) {
			// Genero la vecindad y los valores de las soluciones candidatas a óptimo
			prioridadEvaluacion = new ArrayList<>(); 
			for (Map.Entry<ItemTabu, Integer> entry : listaTabu.entrySet()) {
				valoresSwapped[0] = entry.getKey().getItem1();
				valoresSwapped[1] = entry.getKey().getItem2();
				solucionActual = swap.swapping(solucionInicial, valoresSwapped);
				costoSolucionActual = swap.evaluarCostoSolucion(solucionActual);
				//entry.setValue(costoSolucionInicial - costoSolucionActual);// guardo la mayor diferencia como mejor costo
				//entry.getKey().setOrden(costoSolucionInicial - costoSolucionActual);
				penalizacion = memoriaFrecuencias.get(new ItemTabu(entry.getKey().getItem1(), entry.getKey().getItem2(),0));
				prioridadEvaluacion.add(new ItemTabu(valoresSwapped[0], valoresSwapped[1], costoSolucionInicial - costoSolucionActual - penalizacion));			
			}
			prioridadEvaluacion.sort(Comparator.comparingDouble(ItemTabu::getCosto));
			
			//calculo la mejor solución para esta iteración
			mejorSolucion = null;
			for (int z=prioridadEvaluacion.size()-1;z>=0;z--) {			
				// guardo el mejor item hasta el momento
				if (mejorSolucion == null || (prioridadEvaluacion.get(z).getCosto()-mejorSolucion.getCosto()) < 0) {
					// verifico que no esté en la lista tabú
					if (listaTabu.get(new ItemTabu(prioridadEvaluacion.get(z).getItem1(), prioridadEvaluacion.get(z).getItem2(),0)) <= 0) {// no es tabú
						mejorSolucion = prioridadEvaluacion.get(z);
						listaTabu.put(new ItemTabu(prioridadEvaluacion.get(z).getItem1(), prioridadEvaluacion.get(z).getItem2(),0), listaTabu.get(new ItemTabu(prioridadEvaluacion.get(z).getItem1(), prioridadEvaluacion.get(z).getItem2(),0))+duracionTabuList);
						memoriaFrecuencias.put(new ItemTabu(prioridadEvaluacion.get(z).getItem1(), prioridadEvaluacion.get(z).getItem2(),0), memoriaFrecuencias.get(new ItemTabu(prioridadEvaluacion.get(z).getItem1(), prioridadEvaluacion.get(z).getItem2(),0))+1);
						break;
					} else {// es tabú
						if(mejorSolucion==null) {//sólo si no hay una solución ya guardada antes
							valoresSwapped[0] = prioridadEvaluacion.get(z).getItem1();
							valoresSwapped[1] = prioridadEvaluacion.get(z).getItem2();
							solucionActual = swap.swapping(solucionInicial, valoresSwapped);
							costoSolucionActual = swap.evaluarCostoSolucion(solucionActual);
							if (evaluarCriterioAspiracion(mejorCostoHistorico, costoSolucionActual)) {// comparo la solución tabú con la mejor histórica
								mejorSolucion = prioridadEvaluacion.get(z);
								listaTabu.put(new ItemTabu(prioridadEvaluacion.get(z).getItem1(), prioridadEvaluacion.get(z).getItem2(),0), listaTabu.get(new ItemTabu(prioridadEvaluacion.get(z).getItem1(), prioridadEvaluacion.get(z).getItem2(),0))+duracionTabuList);
								memoriaFrecuencias.put(new ItemTabu(prioridadEvaluacion.get(z).getItem1(), prioridadEvaluacion.get(z).getItem2(),0), memoriaFrecuencias.get(new ItemTabu(prioridadEvaluacion.get(z).getItem1(), prioridadEvaluacion.get(z).getItem2(),0))+1);
								break;
							}
						}
					}
				}
			}
			if(mejorSolucion==null)
				break;
			valoresSwapped[0] = mejorSolucion.getItem1();
			valoresSwapped[1] = mejorSolucion.getItem2();
			solucionInicial = swap.swapping(solucionInicial, valoresSwapped);
			costoSolucionInicial = swap.evaluarCostoSolucion(solucionInicial);
			costos.add(costoSolucionInicial);
			
			if(mejorCostoHistorico<costoSolucionInicial) {
				mejorCostoHistorico=costoSolucionInicial;
			}
			
			iteraciones--;
		}
		
		System.out.println("Mejor costo histórico encontrado: " + swap.evaluarCostoSolucion(solucionInicial));
		System.out.print("Mejor solución histórica encontrada: ");
		swap.toStringSolucion(solucionInicial,1);

		// tiempo de ejecución
		long endTime = System.nanoTime();
		long totalTime = (endTime - startTime) / 1000000;
		System.out.println("tiempo ejecución: " + totalTime + " milisegundos");

		return costos;
	}

	// inicializamos lista tabu
	public static void inicializarListaTabu(int[] solucionInicial) {
		Arrays.sort(solucionInicial);// ordeno la solución inicial de menor a mayor
		listaTabu = new HashMap<ItemTabu, Integer>();
		for (int i = 0; i < solucionInicial.length; i++) {
			for (int j = i + 1; j < solucionInicial.length; j++) {
				listaTabu.put(new ItemTabu(solucionInicial[i], solucionInicial[j],0), 0);
			}
		}
	}

	// inicializamos la memoria de frecuencias (memoria de largo plazo)
	public static void inicializarMemoriaFrecuencias(int[] solucionInicial) {
		Arrays.sort(solucionInicial);// ordeno la solución inicial de menor a mayor
		memoriaFrecuencias = new HashMap<ItemTabu, Double>();
		for (int i = 0; i < solucionInicial.length; i++) {
			for (int j = i + 1; j < solucionInicial.length; j++) {
				memoriaFrecuencias.put(new ItemTabu(solucionInicial[i], solucionInicial[j],0), 0.0);
			}
		}
	}

	// inicializamos la lista de candidatos
	public static void inicializarListaCandidatos(int[] solucionInicial) {
		Arrays.sort(solucionInicial);// ordeno la solución inicial de menor a mayor
		listaCandidatos = new HashMap<ItemTabu, Double>();
		for (int i = 0; i < solucionInicial.length; i++) {
			for (int j = i + 1; j < solucionInicial.length; j++) {
				listaCandidatos.put(new ItemTabu(solucionInicial[i], solucionInicial[j],0), 0.0);
			}
		}
	}

	// función criterio de aspiración tabú
	public static boolean evaluarCriterioAspiracion(double costoMejorSolucionHistorica, double costoSolucionTabu) {
		return (costoMejorSolucionHistorica - costoSolucionTabu) > 0;// si la solución tabu es mejor que la solucion
																		// inicial,
																		// retorno true
	}
}
