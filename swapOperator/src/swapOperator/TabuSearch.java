package swapOperator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class TabuSearch {

	static HashMap<ItemTabu, Integer> listaTabu;
	static HashMap<ItemTabu, Double> memoriaFrecuencias;
	static HashMap<ItemTabu, Double> listaCandidatos;
	static HashMap<Integer, int[]> memoriaMedianoPlazo;
	static HashMap<ItemTabu, Double> listaItemsIntensificacion;
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
	static int[] copiaSolucionInicial;
	static HashMap<Integer, ItemIntensificacion> mejoresVariablesIntensificacion;
	static ArrayList<ItemIntensificacion> mejoresValoresItemsIntensificacion;
	static int valorMaximo;
	static int posicionMaximo;
	static int itemMaximo;

	public static ArrayList<Double> TabuSearch(int[] solucionInicial, Swap swap, int duracionTabuList, int iteraciones, int profundidadIntensificacion) {
		// definición de objetos y variables
		cantidadSwappings = 2;
		valoresSwapped = new int[cantidadSwappings];
		mejorCostoHistorico = 0;

		ArrayList<Double> costos = new ArrayList<Double>();
		long startTime = System.nanoTime();// Contador de tiempo

		//tamanoVecindad = swap.calcularTamañoVecindad(swap.getMatrizF(), cantidadSwappings);
		
		copiaSolucionInicial = Arrays.copyOf(solucionInicial, solucionInicial.length);
		
		inicializarListaTabu(copiaSolucionInicial);
		inicializarMemoriaFrecuencias(copiaSolucionInicial);
		inicializarListaCandidatos(copiaSolucionInicial);
		inicializarMemoriaMedianoPlazo(copiaSolucionInicial);

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
				
				//para la etapa de intensificación, debo anotar en la memoria
				//de mediano plazo, las veces en que quedan los items en una posición
				//a causa del swap
				for(int x=0;x<solucionActual.length;x++) {
					if(entry.getKey().getItem1()==solucionActual[x]) {
						guardarMemoriaMedianoPlazo(entry.getKey().getItem1(), x);
					}
					if(entry.getKey().getItem2()==solucionActual[x]) {
						guardarMemoriaMedianoPlazo(entry.getKey().getItem2(), x);
					}
				}
				
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
			
			//etapa de intensificación
			realizarIntensificacion(solucionInicial, profundidadIntensificacion, swap, costos, iteraciones, duracionTabuList);
			
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
	
	public static void inicializarMemoriaMedianoPlazo(int[] solucionInicial) {
		Arrays.sort(solucionInicial);// ordeno la solución inicial de menor a mayor
		memoriaMedianoPlazo = new HashMap<Integer, int[]>();
		for(int i=0;i<solucionInicial.length;i++) {
			memoriaMedianoPlazo.put(solucionInicial[i], new int[solucionInicial.length]);
		}
	}

	// función criterio de aspiración tabú
	public static boolean evaluarCriterioAspiracion(double costoMejorSolucionHistorica, double costoSolucionTabu) {
		return (costoMejorSolucionHistorica - costoSolucionTabu) > 0;// si la solución tabu es mejor que la solucion
																		// inicial,
																		// retorno true
	}	
	
	//método que guarda en la memoria de mediano plazo
	public static void guardarMemoriaMedianoPlazo(int item, int posicion) {
		memoriaMedianoPlazo.get(item)[posicion] += 1;
	}
	
	//intensificación
	public static void realizarIntensificacion(int[] solucionInicial, int profundidadIntensificacion, Swap swap, ArrayList<Double> costos, int iteraciones, int duracionTabuList) {
		mejoresVariablesIntensificacion = new HashMap<>();
		mejoresValoresItemsIntensificacion = new ArrayList<>();
		for (Entry<Integer, int[]> entry : memoriaMedianoPlazo.entrySet()) {
			valorMaximo = 0;
			posicionMaximo = 0;
			itemMaximo = 0;
			for(int i=0;i<entry.getValue().length;i++) {
				if(entry.getValue()[i]>valorMaximo) {
					valorMaximo = entry.getValue()[i];
					posicionMaximo = i;
					itemMaximo = entry.getKey();
				}
			}
			mejoresValoresItemsIntensificacion.add(new ItemIntensificacion(itemMaximo, posicionMaximo, valorMaximo));
		}		
		Collections.sort(mejoresValoresItemsIntensificacion);
		//int i=0;
		for(int x=mejoresValoresItemsIntensificacion.size()-1;x>(mejoresValoresItemsIntensificacion.size()-1-profundidadIntensificacion);x--) {
			mejoresVariablesIntensificacion.put(mejoresValoresItemsIntensificacion.get(x).getItem(), mejoresValoresItemsIntensificacion.get(x));
			//i++;
		}
		
		//inicializar mini hash con los campos que se pueden modificar
		inicializarListaIntensificacion(solucionInicial, mejoresVariablesIntensificacion);
		
		//realización de intensificación en sí
		buscarOptimoIntensificacion(solucionInicial, swap, costos, iteraciones, duracionTabuList);
	}
	
	// inicializamos lista de intensificación
	public static void inicializarListaIntensificacion(int[] solucionInicial, HashMap<Integer, ItemIntensificacion> mejoresVariablesIntensificacion) {
		Arrays.sort(solucionInicial);// ordeno la solución inicial de menor a mayor
		listaItemsIntensificacion = new HashMap<ItemTabu, Double>();//vamos a guardar solo aquellos items dentro de una solución que podemos modificar
		for (int i = 0; i < solucionInicial.length; i++) {
			//saltamos aquellas posiciones e items que están fijos por la intensificacion
			if(mejoresVariablesIntensificacion.containsKey(solucionInicial[i])) {
				continue;
			}
			
			for (int j = i + 1; j < solucionInicial.length; j++) {
				//saltamos aquellas posiciones e items que están fijos por la intensificacion
				if(mejoresVariablesIntensificacion.containsKey(solucionInicial[j])) {
					continue;
				}
			
				//guardo en el hash el item que se podrá modificar en la instensificación
				listaItemsIntensificacion.put(new ItemTabu(solucionInicial[i], solucionInicial[j],0.0), 0.0);
			}
		}
	}
	
	public static void buscarOptimoIntensificacion(int[] solucionInicial, Swap swap, ArrayList<Double> costos, int iteraciones, int duracionTabuList) {
		costoSolucionInicial = swap.evaluarCostoSolucion(solucionInicial);
		//costos.add(costoSolucionInicial);
		
		mejorCostoHistorico=costoSolucionInicial;

		while(iteraciones>0) {
			// Genero la vecindad y los valores de las soluciones candidatas a óptimo
			prioridadEvaluacion = new ArrayList<>(); 
			for (Map.Entry<ItemTabu, Double> entry : listaItemsIntensificacion.entrySet()) {
				valoresSwapped[0] = entry.getKey().getItem1();
				valoresSwapped[1] = entry.getKey().getItem2();
				solucionActual = swap.swapping(solucionInicial, valoresSwapped);
				costoSolucionActual = swap.evaluarCostoSolucion(solucionActual);
				
				//para la etapa de intensificación, debo anotar en la memoria
				//de mediano plazo, las veces en que quedan los items en una posición
				//a causa del swap
				for(int x=0;x<solucionActual.length;x++) {
					if(entry.getKey().getItem1()==solucionActual[x]) {
						guardarMemoriaMedianoPlazo(entry.getKey().getItem1(), x);
					}
					if(entry.getKey().getItem2()==solucionActual[x]) {
						guardarMemoriaMedianoPlazo(entry.getKey().getItem2(), x);
					}
				}
				
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
		}
	}
}
