package swapOperator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class SimulatedAnnealing {
	public static final int FUNCION_ENFRIAMIENTO_ARITMETICO = 0;
	public static final int FUNCION_ENFRIAMIENTO_GEOMETRICO = 1;
	public static final int FUNCION_ENFRIAMIENTO_LOGARITMICO = 2;
	
	public static final double RAZON_DECRECIMIENTO_ARITMETICO = 10;
	public static final double PORCENTAJE_RAZON_DECRECIMIENTO_GEOMETRICO = 0.01;
	public static final double CONSTANTE_DECRECIMIENTO_LOGARITMICO = 10;


	public static ArrayList<CostosSA> simulatedAnnealing(int[] solucionInicial, double temperaturaMinima,
			double temperaturaMaxima, int cantidadSwappings, int funcionEnfriamiento, double probabilidadAceptar,
			Swap swap, double decrecimiento, double ponderadorVecindad) {

		int[] memoriaSolucionInicial = Arrays.copyOf(solucionInicial, solucionInicial.length);
		
		ArrayList<CostosSA> costos = new ArrayList<CostosSA>();
		long startTime = System.nanoTime();// Contador de tiempo

		int tamanoVecindad = swap.calcularTama�oVecindad(swap.getMatrizF());
		
		double temperaturaActual = temperaturaMaxima;
		// ciclo temperatura
		while (temperaturaActual > temperaturaMinima) {

			// ciclo generaci�n de vecinos dentro de una misma temperatura actual
			int i = 0;
			while (i < (tamanoVecindad * ponderadorVecindad)) {// itera hasta que se alcance el numero de iteraciones de tama�o de la vecindad
				int[] nuevoVecinoAleatorio = swap.swapping(solucionInicial, cantidadSwappings);
				double costoNuevaSolucion = swap.evaluarCostoSolucion(nuevoVecinoAleatorio);
				double costoActualSolucion = swap.evaluarCostoSolucion(solucionInicial);
				double deltaEnergia = costoNuevaSolucion - costoActualSolucion;
				int[] copiaNuevoVecinoAleatorio = Arrays.copyOf(nuevoVecinoAleatorio, nuevoVecinoAleatorio.length);
				int[] copiaSolucionActual = Arrays.copyOf(solucionInicial, solucionInicial.length);
				CostosSA costo = new CostosSA();
				if (deltaEnergia <= 0) {
					solucionInicial = Arrays.copyOf(nuevoVecinoAleatorio, nuevoVecinoAleatorio.length);
					costo.setCostoActualSolucion(costoNuevaSolucion);
					if(costos.size()>0) {
						if(costos.get(costos.size()-1).getCostoMejorSolucion()<costoNuevaSolucion) {
							costo.setCostoMejorSolucion(costos.get(costos.size()-1).getCostoMejorSolucion());
							int [] mejorResultadoHistorico = Arrays.copyOf(costos.get(costos.size()-1).getMejorSolucionHistorica(), costos.get(costos.size()-1).getMejorSolucionHistorica().length);
							costo.setMejorSolucionHistorica(mejorResultadoHistorico);
						}else {
							costo.setCostoMejorSolucion(costoNuevaSolucion);
							costo.setMejorSolucionHistorica(copiaNuevoVecinoAleatorio);
						}
					}else {
						costo.setCostoMejorSolucion(costoNuevaSolucion);
						costo.setMejorSolucionHistorica(copiaNuevoVecinoAleatorio);
					}
					costos.add(costo);
				} else {
					Random rnd = new Random();
					probabilidadAceptar = rnd.nextDouble();
					if (funcionProbabilidadBoltzmann(deltaEnergia, temperaturaActual, probabilidadAceptar)) {
						solucionInicial = Arrays.copyOf(nuevoVecinoAleatorio, nuevoVecinoAleatorio.length);
						costo.setCostoActualSolucion(costoNuevaSolucion);
						if(costos.size()>0) {
							if(costos.get(costos.size()-1).getCostoMejorSolucion()<costoActualSolucion) {
								costo.setCostoMejorSolucion(costos.get(costos.size()-1).getCostoMejorSolucion());
								int [] mejorResultadoHistorico = Arrays.copyOf(costos.get(costos.size()-1).getMejorSolucionHistorica(), costos.get(costos.size()-1).getMejorSolucionHistorica().length);
								costo.setMejorSolucionHistorica(mejorResultadoHistorico);
							}else {
								costo.setCostoMejorSolucion(costoActualSolucion);
								costo.setMejorSolucionHistorica(copiaSolucionActual);
							}
						}else {
							costo.setCostoMejorSolucion(costoActualSolucion);
							costo.setMejorSolucionHistorica(copiaSolucionActual);
						}
						costos.add(costo);
					}
				}
				
				i++;
			}

			// Disminuyo temperatura actual
			switch (funcionEnfriamiento) {
			case FUNCION_ENFRIAMIENTO_ARITMETICO:
				temperaturaActual = funcionEnfriamientoAritmetico(temperaturaActual, decrecimiento);
				break;
			case FUNCION_ENFRIAMIENTO_GEOMETRICO:
				temperaturaActual = funcionEnfriamientoGeometrico(temperaturaActual,
						decrecimiento);
				break;
			case FUNCION_ENFRIAMIENTO_LOGARITMICO:
				temperaturaActual = funcionEnfriamientoLogaritmico(temperaturaActual,
						decrecimiento);
				break;
			default:
				break;
			}
		}

		// imprimo mejor resultado �ptimo encontrado
		System.out.println("Mejor costo encontrado al t�rmino de la iteraci�n: " + swap.evaluarCostoSolucion(solucionInicial));
		System.out.print("Mejor soluci�n encontrada al t�rmino de la iteraci�n: ");
		swap.toStringSolucion(solucionInicial,0);
		System.out.println("Mejor costo hist�rico encontrado: " + swap.evaluarCostoSolucion(costos.get(costos.size()-1).getMejorSolucionHistorica()));
		System.out.print("Mejor soluci�n hist�rica encontrada: ");
		swap.toStringSolucion(costos.get(costos.size()-1).getMejorSolucionHistorica(),1);

		// tiempo de ejecuci�n
		long endTime = System.nanoTime();
		long totalTime = (endTime - startTime) / 1000000;
		System.out.println("tiempo ejecuci�n: " + totalTime + " milisegundos");

		return costos;
	}

	public static double funcionEnfriamientoAritmetico(double temperatura, double razonDecrecimiento) {
		return temperatura - razonDecrecimiento;
	}

	public static double funcionEnfriamientoGeometrico(double temperatura, double porcentajeRazonDecrecimiento) {
		return temperatura * (porcentajeRazonDecrecimiento / 100);
	}

	public static double funcionEnfriamientoLogaritmico(double temperatura, double constante) {
		return constante / Math.log(temperatura);
	}

	public static boolean funcionProbabilidadBoltzmann(double deltaEnergia, double temperatura, double probabilidadAceptar) {
		return (probabilidadAceptar <= (Math.exp(-deltaEnergia / temperatura))) ? true : false;
	}
}
