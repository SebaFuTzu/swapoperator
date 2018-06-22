package swapOperator;

public class IndividuoAlgoritmoGenetico implements Comparable<IndividuoAlgoritmoGenetico>{
	private int[] solucion;
	private double costo;
	private double fitness;
	
	public IndividuoAlgoritmoGenetico(int[] solucion, double costo) {
		super();
		this.solucion = solucion;
		this.costo = costo;
		this.fitness = 0;
	}

	public double getFitness() {
		return fitness;
	}

	public void setFitness(double fitness) {
		this.fitness = fitness;
	}

	public int[] getSolucion() {
		return solucion;
	}

	public void setSolucion(int[] solucion) {
		this.solucion = solucion;
	}

	public double getCosto() {
		return costo;
	}

	public void setCosto(double costo) {
		this.costo = costo;
	}

	@Override
	public int compareTo(IndividuoAlgoritmoGenetico otroIndividuo) {
		if(this.getCosto()<otroIndividuo.getCosto())
	          return -1;
	    else if(otroIndividuo.getCosto()<this.getCosto())
	          return 1;
	    return 0;
	}	
}
