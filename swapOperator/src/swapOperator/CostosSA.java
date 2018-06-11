package swapOperator;

public class CostosSA {
	private double costoMejorSolucion = 0;
	private double costoActualSolucion = 0;
	
	public CostosSA() {
		super();
		this.costoMejorSolucion = 0;
		this.costoActualSolucion = 0;
	}

	public double getCostoMejorSolucion() {
		return costoMejorSolucion;
	}

	public void setCostoMejorSolucion(double costoMejorSolucion) {
		this.costoMejorSolucion = costoMejorSolucion;
	}

	public double getCostoActualSolucion() {
		return costoActualSolucion;
	}

	public void setCostoActualSolucion(double costoActualSolucion) {
		this.costoActualSolucion = costoActualSolucion;
	}

}
