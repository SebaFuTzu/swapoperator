package swapOperator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.stream.Stream;

public class Main {
	public static final String SEPARATOR=";";
	public static final String QUOTE="\"";

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			//String Fayudantia1 = "F-ayudantia1.txt";
			//String Dayudantia1 = "D-ayudantia1.txt";
			int cantidadSwappings = 2;
			String Fayudantia1 = "F64.txt";
			String Dayudantia1 = "D64.txt";
			String path = System.getProperty("user.dir")+"\\datos\\";

			DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
			DateFormat dfLog = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			if(args[0].equalsIgnoreCase("-path")) {  // si parte con -path asume que realizaras una prueba unitaria
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
				swap.toStringSolcuionInicial(solucionInicial);


				if(args[5].equals("ejercicio1")) {
					System.out.println("######## Ejercicio 1 ########");
					int tamañoVecindad = (int)swap.calcularTamañoVecindad(f, cantidadSwappings);
					swap.ejercicio1(solucionInicial, cantidadSwappings, tamañoVecindad, 50);
				}else if(args[5].equals("ejercicio2")) {
					System.out.println("######## Ejercicio 2 ########");
					int tamañoVecindad = (int)swap.calcularTamañoVecindad(f, cantidadSwappings);
					ArrayList<Double> costos = swap.ejercicio2(solucionInicial, cantidadSwappings, tamañoVecindad, 30);

					//plotting
					final XYPlot demo = new XYPlot("Gráfico optimización con operador swap "+args[5], "Costo", costos);

					//demo.pack();
					//RefineryUtilities.centerFrameOnScreen(demo);
					//demo.setVisible(true);
				}else if(args[5].equals("ejercicio3")) {
					System.out.println("######## Ejercicio 3 ########");
					int tamañoVecindad = (int)swap.calcularTamañoVecindad(f, cantidadSwappings);
					ArrayList<Double> costos = swap.ejercicio2(solucionInicial, cantidadSwappings, tamañoVecindad, 100);

					//plotting
					final XYPlot demo = new XYPlot("Gráfico optimización con operador swap "+args[5], "Costo", costos);
					//demo.pack();
					//RefineryUtilities.centerFrameOnScreen(demo);
					//demo.setVisible(true);
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


					ArrayList<Costos> costos = SimulatedAnnealing.simulatedAnnealing(solucionInicial, temperaturaMinima, temperaturaMaxima, cantidadSwappings, 
							funcionEnfriamiento, probabilidadAceptar, swap, decrecimiento, ponderadorVecindad);

					//plotting
					final XYPlot demo = new XYPlot("Gráfico optimización Simulated Annealing", "Costo sin memoria", "Costo con memoria", costos);
					//demo.pack();
					//RefineryUtilities.centerFrameOnScreen(demo);
					//demo.setVisible(true);
				}else if(args[5].equals("TABU")) {
					System.out.println("######## Tabu search ########");
					int duracionTabuList = 22;
					int iteraciones = 20;
					int profundidadIntensificacion = 18;
					boolean intensificacion = true;
					boolean diversificacion = true;

					if ( args.length > 6)
						duracionTabuList = Integer.parseInt(args[6]);

					if ( args.length > 7)
						iteraciones = Integer.parseInt(args[7]);

					if ( args.length > 8)
						profundidadIntensificacion = Integer.parseInt(args[8]);

					if ( args.length > 9)
						intensificacion = Boolean.parseBoolean(args[9]);

					if ( args.length > 7)
						diversificacion = Boolean.parseBoolean(args[10]);

					ArrayList<Double> costos = TabuSearch.TabuSearch(solucionInicial, swap, duracionTabuList, iteraciones, profundidadIntensificacion, intensificacion, diversificacion);

					//plotting
					final XYPlot demo = new XYPlot("Gráfico optimización Tabu Search", "Costo", costos, 6156, "costo"); //Yo
					//demo.pack();
					//RefineryUtilities.centerFrameOnScreen(demo);
					//demo.setVisible(true);
				}
			}else if(args[0].equalsIgnoreCase("-dataset")){ //si parto con -dataset carga el dataste para hacer varias pruebas programadas
				BufferedReader br = null;
				try {
					//dejo un espacio para cargar el dataset
					//br =new BufferedReader(new FileReader("dataset-SimulatedAnnealing.csv"));
					//String Fayudantia1 = path+"F"+datos;
					//String Dayudantia1 = path+"D"+datos;
					br =new BufferedReader(new FileReader(args[1]));
					//separo la linea por el separador
					String line = br.readLine();

					String [] fields = line.split(SEPARATOR);
					//remuevo basura del codigo
					fields = removeTrailingQuotes(fields);
					//line = br.readLine(); //activar si usamos cabecera ya q salta la primera linea
					//escribir archivo csv

					//creo la matriz afuera ya que si uso la misma matriz solo necesito actualizar solucion inicial y no todo
					Stream<String> matrizF;
					Stream<String> matrizD;
					path="";
					Fayudantia1="";
					Dayudantia1="";
					Swap swap;
					int[] solucionInicial;
					int[][] f= {{0}};
					int[][] d= {{0}};
					int cantidadExperimentos = 1;
					int mejorMaximoConocido = 6155;
					String fileName = df.format(new java.util.Date())  ; //+ ;
					FileWriter fileWriter = new FileWriter(args[2] + fileName  +  ".log" );
					//FileWriter fichero = null;
					//PrintWriter pw = null;
					//fichero = new FileWriter(args[2]  +  ".log");
					//pw = new PrintWriter(fichero);
					XYPlotSpeed plot = new XYPlotSpeed();
					int cantLines = 0;
					while (null!=line) {  //voy leyendo linea a linea
						//cargo los datos, si son diferentes actualizo todo
						cantLines++;
						if(path!=fields[1] || Fayudantia1!=fields[2] || Dayudantia1!=fields[3])
						{
							path = fields[1];
							Fayudantia1 = fields[2];
							Dayudantia1 = fields[3];
							cantidadSwappings = Integer.parseInt(fields[4]);

							matrizF = Files.lines(Paths.get(path+Fayudantia1));
							matrizD = Files.lines(Paths.get(path+Dayudantia1));

							f = convertirString(matrizF);
							d = convertirString(matrizD);		

						}
						else
						{	
							//Se quitan estas lineas porque cada experimento hace sus propios SWAP y genera las soluciones
							//ya tengo cargado los datos, falta crear nueva solucion inicial para proxima prueba
							//swap = new Swap(f, d);
							//solucionInicial = swap.generarSolcuionInicial(swap.getMatrizD());
							//swap.toStringSolcuionInicial(solucionInicial);
						}
						if(fields[5].equals("SA")) {
							//System.out.println("######## Simulated Annealing ########");

							double temperaturaMinima = 0;
							double temperaturaMaxima = 350;
							double probabilidadAceptar = 0.99;
							int funcionEnfriamiento = SimulatedAnnealing.FUNCION_ENFRIAMIENTO_GEOMETRICO;
							double decrecimiento = SimulatedAnnealing.RAZON_DECRECIMIENTO_ARITMETICO;
							double ponderadorVecindad = 1;

							//#¿NOMBRE?;./datos/;F64.txt;D64.txt;2;SA;1;1000;0.95;1;0.3;2;100;116
							temperaturaMinima = Double.parseDouble(fields[6]);

							temperaturaMaxima = Double.parseDouble(fields[7]);

							probabilidadAceptar = Double.parseDouble(fields[8]);

							funcionEnfriamiento = Integer.parseInt(fields[9]);

							decrecimiento = Double.parseDouble(fields[10]);

							ponderadorVecindad = Double.parseDouble(fields[11]);


							//plotting
							//final XYPlot demo = new XYPlot("Gráfico optimización Simulated Annealing", "Costo sin memoria", "Costo con memoria", costos);


							cantidadExperimentos = Integer.parseInt(fields[12]);
							mejorMaximoConocido = Integer.parseInt(fields[13]);
							//String fileName = df.format(new java.util.Date())  ; //+ ;

							//fileWriter = new FileWriter(args[2] + fileName  +  ".log" );
							System.out.println( dfLog.format(new java.util.Date()) + " Linea de experimento [" + cantLines + "]");

							for (int i = 0; i < cantidadExperimentos; i++) { 

								System.out.println(dfLog.format(new java.util.Date()) + " Experimento [" + cantidadExperimentos + "]");

								swap = new Swap(f, d);
								solucionInicial = swap.generarSolcuionInicial(swap.getMatrizD());

								swap.toStringSolcuionInicial(solucionInicial);

								ArrayList<Costos> costos = SimulatedAnnealing.simulatedAnnealing(solucionInicial, temperaturaMinima, temperaturaMaxima, cantidadSwappings, 
										funcionEnfriamiento, probabilidadAceptar, swap, decrecimiento, ponderadorVecindad);


								//plotting
								//final XYPlot demo = new XYPlot("Gráfico optimización Simulated Annealing", "Costo sin memoria", "Costo con memoria", costos, mejorMaximoConocido, fileName + "_" + String.format("%04d", i+1)); //Yo
								
								plot.Plot("Gráfico optimización Simulated Annealing", "Costo sin memoria", "Costo con memoria", costos, mejorMaximoConocido, fileName + "_" + String.format("%04d", i+1)); 
								
								//demo.pack();
								//System.out.println("Mejor solucion"+minimo(costos));
								//escribo solucion en el log
								//fileWriter.append(String.valueOf(minimo(costos)));
								//fileWriter.append(QUOTE);
								//RefineryUtilities.centerFrameOnScreen(demo);
								//demo.setVisible(true);}
							}
						}else if(fields[5].equals("TABU")) {
							//System.out.println("######## Tabu search ########");
							int duracionTabuList = Integer.parseInt(fields[6]);
							int iteraciones = Integer.parseInt(fields[7]);
							int profundidadIntensificacion = Integer.parseInt(fields[8]);
							boolean intensificacion = Boolean.parseBoolean(fields[9]);
							boolean diversificacion = Boolean.parseBoolean(fields[10]);

							cantidadExperimentos = Integer.parseInt(fields[11]);
							mejorMaximoConocido = Integer.parseInt(fields[12]);
							//String fileName = df.format(new java.util.Date())  ; //+ ;

							System.out.println(dfLog.format(new java.util.Date()) + " Linea de experimento [" + cantLines + "]");

							for (int i = 0; i < cantidadExperimentos; i++) { 

								System.out.println(dfLog.format(new java.util.Date()) + " Experimento [" + cantidadExperimentos + "]");

								swap = new Swap(f, d);
								solucionInicial = swap.generarSolcuionInicial(swap.getMatrizD());

								swap.toStringSolcuionInicial(solucionInicial);

								ArrayList<Double> costos = TabuSearch.TabuSearch(solucionInicial, swap, duracionTabuList, iteraciones, profundidadIntensificacion, intensificacion, diversificacion);

								fileName = df.format(new java.util.Date())  ; //+ ;
								//plotting
								//final XYPlot demo = new XYPlot("Gráfico optimización Tabu Search", "Costo", costos, mejorMaximoConocido, fileName + "_" + String.format("%04d", i+1)); //Yo
								plot.Plot("Gráfico optimización Tabu Search", "Costo", costos, mejorMaximoConocido, fileName + "_" + String.format("%04d", i+1));
								
								
								
								System.out.println("Mejor solucion: "+minimo(costos));

								fileWriter.write(fileName);
								fileWriter.write(SEPARATOR);
								fileWriter.write(String.valueOf(minimo(costos)));
								fileWriter.write("\n");
								
							}
						}

						//Leo la siguiente linea
						line = br.readLine();
						if (line!= null) { 
							fields = line.split(SEPARATOR);
							//remuevo basura del codigo
							fields = removeTrailingQuotes(fields);
						}

					}
					fileWriter.flush();
					fileWriter.close();
					//pw.close();

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (null!=br) {
						br.close();
					}

				}
			}else if(args[0].equalsIgnoreCase("-help"))	{
				System.out.println("Ejemplo de sintaxis:");
				System.out.println("java -jar ayudantia1metaheuristicas.jar [carga desde cmd] [path] [txt matriz F] [txt matriz distancias] [cantidad de swappings] [nombre ejercicio]");
				System.out.println("java -jar ayudantia1metaheuristicas.jar -path C: F64.txt D64.txt 2 ejercicio1");
			}		
		} catch (IOException e) {
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


	private static String[] removeTrailingQuotes(String[] fields) {
		String result[] = new String[fields.length];
		for (int i=0;i<result.length;i++){
			result[i] = fields[i].replaceAll("^"+QUOTE, "").replaceAll(QUOTE+"$", "");
		}
		return result;
	}

	static double minimo(ArrayList<Double> costos)
	{
		double min=costos.get(0);
		for(int i=1;i<costos.size();i++) {
			if(min>costos.get(i))
			{
				min=costos.get(i);
			}
		}
		return min;
	}


}
