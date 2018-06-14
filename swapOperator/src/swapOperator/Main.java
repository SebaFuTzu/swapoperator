package swapOperator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

import org.jfree.ui.RefineryUtilities;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			//String Fayudantia1 = "F-ayudantia1.txt";
			//String Dayudantia1 = "D-ayudantia1.txt";
			int cantidadSwappings = 2;
			String Fayudantia1 = "F64.txt";
			String Dayudantia1 = "D64.txt";
			String path = "C:/Universidad/USACH/2018-1/Optimización en Ingeniería/Ayudantía/Metaheurísticas/Ayudantía 1/Datos/";
			if(args[0].equalsIgnoreCase("-path")) {
				path = args[1];
				Fayudantia1 = args[2];
				Dayudantia1 = args[3];
				cantidadSwappings = Integer.parseInt(args[4]);
				
				Stream<String> matrizF = Files.lines(Paths.get(path+Fayudantia1));
				Stream<String> matrizD = Files.lines(Paths.get(path+Dayudantia1));
				//filasD.forEach(System.out::println);
				int[][] f = convertirString(matrizF);
				int[][] d = convertirString(matrizD);
				/*for(int[] i: m){
					for(int j: i){
						System.out.println(j);
					}
				}*/					
				
				Swap swap = new Swap(f, d);
				int[] solucionInicial = swap.generarSolcuionInicial(swap.getMatrizD());
				//int[] solucionInicial = new int[] {3,2,1,4};
				swap.toStringSolcuionInicial(solucionInicial);
				
				//Genero una nueva solución swapped
				//int[] solucionSwapped = swap.swapping(solucionInicial, 4);
				//swap.toStringSolucion(solucionSwapped, 2);
				
				//swap.toStringSolucionSwapped(solucionInicial, cantidadSwappings);
				
				
				//System.out.println("Costo solución inicial: "+swap.evaluarCostoSolucion(solucionInicial));
				
				//int[] listaOrdenadaF = swap.calcularListaOrdenadaSumatoriaMatriz(swap.getMatrizF(), true);
				//swap.toStringLista(listaOrdenadaF, "ascendente F");
				
				//int[] listaOrdenadaD = swap.calcularListaOrdenadaSumatoriaMatriz(swap.getMatrizD(), false);
				//swap.toStringLista(listaOrdenadaD, "descendente D");
				
				//int[] listaOrdenadaFUnidimensional = swap.calcularListaOrdenadaMatriz(swap.getMatrizF(), true);
				//swap.toStringLista(listaOrdenadaFUnidimensional, "ascendente F unidimensional");
				
				//int[] listaOrdenadaDUnidimensional = swap.calcularListaOrdenadaMatriz(swap.getMatrizD(), false);
				//swap.toStringLista(listaOrdenadaDUnidimensional, "descendente D unidimensional");
				
				//System.out.println("----------------------------------------");
				
				//listaOrdenadaFUnidimensional = swap.eliminarCerosLista(listaOrdenadaFUnidimensional);
				//listaOrdenadaDUnidimensional = swap.eliminarCerosLista(listaOrdenadaDUnidimensional);
				
				//System.out.println("Costo solución ordenada: "+swap.evaluarCostoSolucionListasOrdenadas(listaOrdenadaFUnidimensional, listaOrdenadaDUnidimensional));
				
				int tamañoVecindad = swap.calcularTamañoVecindad(f, cantidadSwappings);
				
				//System.out.println("Tamaño total vecindad: "+tamañoVecindad);
				
				if(args[5].equals("ejercicio1")) {
					System.out.println("######## Ejercicio 1 ########");
					swap.ejercicio1(solucionInicial, cantidadSwappings, tamañoVecindad, 50);
				}else if(args[5].equals("ejercicio2")) {
					System.out.println("######## Ejercicio 2 ########");
					ArrayList<Double> costos = swap.ejercicio2(solucionInicial, cantidadSwappings, tamañoVecindad, 30);
					
					//plotting
					final XYPlot demo = new XYPlot("Gráfico optimización con operador swap "+args[5], "Costo", costos);
				    demo.pack();
				    RefineryUtilities.centerFrameOnScreen(demo);
				    demo.setVisible(true);
				}else if(args[5].equals("ejercicio3")) {
					System.out.println("######## Ejercicio 3 ########");
					ArrayList<Double> costos = swap.ejercicio2(solucionInicial, cantidadSwappings, tamañoVecindad, 100);
					
					//plotting
					final XYPlot demo = new XYPlot("Gráfico optimización con operador swap "+args[5], "Costo", costos);
				    demo.pack();
				    RefineryUtilities.centerFrameOnScreen(demo);
				    demo.setVisible(true);
				}else if(args[5].equals("SA")) {
					System.out.println("######## Simulated Annealing ########");

					double temperaturaMinima = 0;
					double temperaturaMaxima = 350;
					double probabilidadAceptar = 0.99;
					int funcionEnfriamiento = SimulatedAnnealing.FUNCION_ENFRIAMIENTO_GEOMETRICO;
					double decrecimiento = SimulatedAnnealing.RAZON_DECRECIMIENTO_ARITMETICO;
					double ponderadorVecindad = 1;
					
					
					if ( args.length > 6)
						temperaturaMinima = Double.parseDouble(args[6]);

					if ( args.length > 7)
						temperaturaMaxima = Double.parseDouble(args[7]);
					
					if ( args.length > 8)
						probabilidadAceptar = Double.parseDouble(args[8]);
					
					if ( args.length > 9 )
						funcionEnfriamiento = Integer.parseInt(args[9]);
					
					if ( args.length > 10 ) 
						decrecimiento = Double.parseDouble(args[10]);
					else { 
						switch (funcionEnfriamiento) {
						case SimulatedAnnealing.FUNCION_ENFRIAMIENTO_ARITMETICO:
							decrecimiento = SimulatedAnnealing.RAZON_DECRECIMIENTO_ARITMETICO;
							break;
						case SimulatedAnnealing.FUNCION_ENFRIAMIENTO_GEOMETRICO:
							decrecimiento = SimulatedAnnealing.PORCENTAJE_RAZON_DECRECIMIENTO_GEOMETRICO;
							break;
						case SimulatedAnnealing.FUNCION_ENFRIAMIENTO_LOGARITMICO:
							decrecimiento = SimulatedAnnealing.CONSTANTE_DECRECIMIENTO_LOGARITMICO;
							break;
						}
					}
					
					if ( args.length > 11 ) 
						ponderadorVecindad = Double.parseDouble(args[11]);
					
					
					ArrayList<CostosSA> costos = SimulatedAnnealing.simulatedAnnealing(solucionInicial, temperaturaMinima, temperaturaMaxima, cantidadSwappings, 
							funcionEnfriamiento, probabilidadAceptar, swap, decrecimiento, ponderadorVecindad);
					
					//plotting
					final XYPlot demo = new XYPlot("Gráfico optimización Simulated Annealing", "Costo sin memoria", "Costo con memoria", costos);
				    demo.pack();
				    RefineryUtilities.centerFrameOnScreen(demo);
				    demo.setVisible(true);
				}
			}else if(args[0].equalsIgnoreCase("-help"))	{
				System.out.println("Ejemplo de sintaxis:");
				System.out.println("java -jar ayudantia1metaheuristicas.jar [carga desde cmd] [path] [txt matriz F] [txt matriz distancias] [cantidad de swappings] [nombre ejercicio]");
				System.out.println("java -jar ayudantia1metaheuristicas.jar -path C: F64.txt D64.txt 2 ejercicio1");
			}		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public static int[][] convertirString(Stream<String> filas){
		String[] fila = filas.toArray(String[]::new);
		int[][] matriz = new int[fila.length][fila.length];
		for(int i=0;i<fila.length;i++){
			String[] f = fila[i].split(" ");
			for(int j=0;j<f.length;j++){
				matriz[i][j]=Integer.parseInt(f[j]);
			}
		}
		return matriz;
	}
}
