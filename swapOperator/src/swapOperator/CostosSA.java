package swapOperator;

public class CostosSA {
	private double costoMejorSolucion = 0;
	private double costoAnteriorSolucion = 0;
	
	public CostosSA() {
		super();
		this.costoMejorSolucion = 0;
		this.costoAnteriorSolucion = 0;
	}

	public double getCostoMejorSolucion() {
		return costoMejorSolucion;
	}

	public void setCostoMejorSolucion(double costoMejorSolucion) {
		this.costoMejorSolucion = costoMejorSolucion;
	}

	public double getCostoAnteriorSolucion() {
		return costoAnteriorSolucion;
	}

	public void setCostoAnteriorSolucion(double costoAnteriorSolucion) {
		this.costoAnteriorSolucion = costoAnteriorSolucion;
	}

}
