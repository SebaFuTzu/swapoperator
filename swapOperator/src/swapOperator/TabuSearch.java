package swapOperator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class TabuSearch {

	static HashMap<ItemTabu, Double> listaTabu;
	static HashMap<ItemTabu, Double> memoriaFrecuencias;
	static HashMap<ItemTabu, Double> listaCandidatos;
	static int[] solucionActual;
	static int[] valoresSwapped;
	static double costoSolucionActual;

	public static ArrayList<CostosSA> TabuSearch(int[] solucionInicial, Swap swap, int duracionTabuList) {
		// definición de objetos y variables
		int cantidadSwappings = 2;
		valoresSwapped = new int[cantidadSwappings];

		ArrayList<CostosSA> costos = new ArrayList<CostosSA>();
		long startTime = System.nanoTime();// Contador de tiempo

		int tamanoVecindad = swap.calcularTamañoVecindad(swap.getMatrizF(), cantidadSwappings);
		inicializarListaTabu(solucionInicial);
		inicializarMemoriaFrecuencias(solucionInicial);
		inicializarListaCandidatos(solucionInicial);

		double costoSolucionInicial = swap.evaluarCostoSolucion(solucionInicial);

		// Genero la vecindad y los valores de las soluciones candidatas a óptimo
		ArrayList<ItemTabu> prioridadEvaluacion = new ArrayList<>(); 
		for (Map.Entry<ItemTabu, Double> entry : listaTabu.entrySet()) {
			valoresSwapped[0] = entry.getKey().getItem1();
			valoresSwapped[1] = entry.getKey().getItem2();
			solucionActual = swap.swapping(solucionInicial, valoresSwapped);
			costoSolucionActual = swap.evaluarCostoSolucion(solucionActual);
			entry.setValue(costoSolucionInicial - costoSolucionActual);// guardo la mayor diferencia como mejor costo
			entry.getKey().setOrden(costoSolucionInicial - costoSolucionActual);
			prioridadEvaluacion.add(entry.getKey());			
		}
		prioridadEvaluacion.sort(Comparator.comparingDouble(ItemTabu::getOrden));
		
		//calculo la mejor solución para esta iteración
		ItemTabu mejorSolucion = null;
		for (int z=prioridadEvaluacion.size()-1;z>=0;z--) {			
			// guardo el mejor item hasta el momento
			if (mejorSolucion == null || (prioridadEvaluacion.get(z).getOrden()-mejorSolucion.getOrden()) < 0) {
				// verifico que no esté en la lista tabú
				if (listaTabu.get(prioridadEvaluacion.get(z)) <= 0) {// no es tabú
					mejorSolucion = prioridadEvaluacion.get(z);
					break;
				} else {// es tabú
					if(mejorSolucion==null) {
						valoresSwapped[0] = prioridadEvaluacion.get(z).getItem1();
						valoresSwapped[1] = prioridadEvaluacion.get(z).getItem2();
						solucionActual = swap.swapping(solucionInicial, valoresSwapped);
						costoSolucionActual = swap.evaluarCostoSolucion(solucionActual);
						if (evaluarCriterioAspiracion(costoSolucionInicial, costoSolucionActual)) {// comparo la solución tabú con la mejor histórica
							mejorSolucion = prioridadEvaluacion.get(z);
							break;
						}
					}
				}
			}
		}

		// tiempo de ejecución
		long endTime = System.nanoTime();
		long totalTime = (endTime - startTime) / 1000000;
		System.out.println("tiempo ejecución: " + totalTime + " milisegundos");

		return costos;
	}

	// inicializamos lista tabu
	public static void inicializarListaTabu(int[] solucionInicial) {
		Arrays.sort(solucionInicial);// ordeno la solución inicial de menor a mayor
		listaTabu = new HashMap<ItemTabu, Double>();
		for (int i = 0; i < solucionInicial.length; i++) {
			for (int j = i + 1; j < solucionInicial.length; j++) {
				listaTabu.put(new ItemTabu(solucionInicial[i], solucionInicial[j]), 0.0);
			}
		}
	}

	// inicializamos la memoria de frecuencias (memoria de largo plazo)
	public static void inicializarMemoriaFrecuencias(int[] solucionInicial) {
		Arrays.sort(solucionInicial);// ordeno la solución inicial de menor a mayor
		memoriaFrecuencias = new HashMap<ItemTabu, Double>();
		for (int i = 0; i < solucionInicial.length; i++) {
			for (int j = i + 1; j < solucionInicial.length; j++) {
				memoriaFrecuencias.put(new ItemTabu(solucionInicial[i], solucionInicial[j]), 0.0);
			}
		}
	}

	// inicializamos la lista de candidatos
	public static void inicializarListaCandidatos(int[] solucionInicial) {
		Arrays.sort(solucionInicial);// ordeno la solución inicial de menor a mayor
		listaCandidatos = new HashMap<ItemTabu, Double>();
		for (int i = 0; i < solucionInicial.length; i++) {
			for (int j = i + 1; j < solucionInicial.length; j++) {
				listaCandidatos.put(new ItemTabu(solucionInicial[i], solucionInicial[j]), 0.0);
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
