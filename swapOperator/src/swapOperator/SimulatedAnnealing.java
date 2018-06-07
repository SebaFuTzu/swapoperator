package swapOperator;

import java.util.ArrayList;

public class SimulatedAnnealing {
	public static final int FUNCION_ENFRIAMIENTO_ARITMETICO = 0;
	public static final int FUNCION_ENFRIAMIENTO_GEOMETRICO = 1;
	public static final int FUNCION_ENFRIAMIENTO_LOGARITMICO = 2;

	public ArrayList<CostosSA> simulatedAnnealing(int[] solucionInicial, double temperaturaMinima, double temperaturaMaxima,
			int cantidadSwappings, int funcionEnfriamiento, double probabilidadAceptar, Swap swap) {
		
		ArrayList<CostosSA> costos = new ArrayList<CostosSA>();
		long startTime = System.nanoTime();//Contador de tiempo

		double temperaturaActual = temperaturaMaxima;
		// ciclo temperatura
		while (temperaturaActual < temperaturaMinima) {

			// ciclo generación de vecinos dentro de una misma temperatura actual
			int i = 0;
			int tamañoVecindad = swap.calcularTamañoVecindad(swap.getMatrizF());
			while (i < tamañoVecindad) {// itera hasta que se alcance el numero de iteraciones de tamaño de la vecindad
				int[] nuevoVecinoAleatorio = swap.swapping(solucionInicial, cantidadSwappings);
				double costoNuevarSolucion = swap.evaluarCostoSolucion(nuevoVecinoAleatorio);
				double costoAnteriorSolucion = swap.evaluarCostoSolucion(solucionInicial);
				double deltaEnergia = costoNuevarSolucion - costoAnteriorSolucion;
				CostosSA costo = new CostosSA();
				if (deltaEnergia <= 0) {
					solucionInicial = nuevoVecinoAleatorio;
					costo.setCostoMejorSolucion(costoNuevarSolucion);
					costo.setCostoAnteriorSolucion(costoNuevarSolucion);
				} else {
					if(funcionProbabilidadBoltzmann(deltaEnergia, temperaturaActual, probabilidadAceptar)) {
						solucionInicial = nuevoVecinoAleatorio;
						costo.setCostoMejorSolucion(costoNuevarSolucion);
						costo.setCostoAnteriorSolucion(costoAnteriorSolucion);
					}
				}
			}

			// Disminuyo temperatura actual
			switch (funcionEnfriamiento) {
			case FUNCION_ENFRIAMIENTO_ARITMETICO:
				temperaturaActual = funcionEnfriamientoAritmetico(temperaturaActual, 2);
				break;
			case FUNCION_ENFRIAMIENTO_GEOMETRICO:
				temperaturaActual = funcionEnfriamientoGeometrico(temperaturaActual, 5);
				break;
			case FUNCION_ENFRIAMIENTO_LOGARITMICO:
				temperaturaActual = funcionEnfriamientoLogaritmico(temperaturaActual, 10);
				break;
			default:
				break;
			}
		}

		// imprimo mejor resultado óptimo encontrado

		// tiempo de ejecución
		long endTime = System.nanoTime();
		long totalTime = (endTime - startTime) / 1000000;
		System.out.println("tiempo ejecución: " + totalTime + " milisegundos");
		
		return costos;
	}

	public double funcionEnfriamientoAritmetico(double temperatura, double razonDecrecimiento) {
		return temperatura - razonDecrecimiento;
	}

	public double funcionEnfriamientoGeometrico(double temperatura, double porcentajeRazonDecrecimiento) {
		return temperatura * (porcentajeRazonDecrecimiento / 100);
	}

	public double funcionEnfriamientoLogaritmico(double temperatura, double constante) {
		return constante / Math.log(temperatura);
	}

	public boolean funcionProbabilidadBoltzmann(double deltaEnergia, double temperatura, double probabilidadAceptar) {
		return (probabilidadAceptar <= (Math.exp(deltaEnergia / temperatura))) ? true : false;
	}
}
