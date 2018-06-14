package swapOperator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class TabuSearch {
	
	static HashMap<ItemTabu, Integer> listaTabu;
	static HashMap<ItemTabu, Integer> memoriaFrecuencias;
	
	public static ArrayList<CostosSA> TabuSearch(int[] solucionInicial, Swap swap, int duracionTabuList) {
		//definici�n de objetos y variables
		int cantidadSwappings = 2;
		
		ArrayList<CostosSA> costos = new ArrayList<CostosSA>();
		long startTime = System.nanoTime();// Contador de tiempo

		//int tamanoVecindad = swap.calcularTama�oVecindad(swap.getMatrizF(), cantidadSwappings);
		inicializarListaTabu(solucionInicial);
		inicializarMemoriaFrecuencias(solucionInicial);
		
		
		
		// tiempo de ejecuci�n
		long endTime = System.nanoTime();
		long totalTime = (endTime - startTime) / 1000000;
		System.out.println("tiempo ejecuci�n: " + totalTime + " milisegundos");

		return costos;
	}
	
	//inicializamos lista tabu
	public static void inicializarListaTabu(int[] solucionInicial){
		Arrays.sort(solucionInicial);//ordeno la soluci�n inicial de menor a mayor
		listaTabu = new HashMap<ItemTabu, Integer>();
		for(int i=0;i<solucionInicial.length;i++) {
			for(int j=i+1;j<solucionInicial.length;j++) {
				listaTabu.put(new ItemTabu(solucionInicial[i], solucionInicial[j]),0);			}
		}
	}
	
	//inicializamos la memoria de frecuencias (memoria de largo plazo)
	public static void inicializarMemoriaFrecuencias(int[] solucionInicial){
		Arrays.sort(solucionInicial);//ordeno la soluci�n inicial de menor a mayor
		memoriaFrecuencias = new HashMap<ItemTabu, Integer>();
		for(int i=0;i<solucionInicial.length;i++) {
			for(int j=i+1;j<solucionInicial.length;j++) {
				memoriaFrecuencias.put(new ItemTabu(solucionInicial[i], solucionInicial[j]),0);			}
		}
	}
	
	//funci�n criterio de aspiraci�n tab�
	public static boolean evaluarCriterioAspiracion(double costoSolucionActual, double costoSolucionTabu) {
		return (costoSolucionTabu-costoSolucionActual)>0;
	}
}
