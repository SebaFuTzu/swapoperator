package swapOperator;

public class ItemTabu {
	private int item1=0;
	private int item2=0;
	private double costo = 0;
	
	public ItemTabu(int item1, int item2, double orden) {
		this.item1=item1;
		this.item2=item2;
		this.costo=orden;
	}

	public double getCosto() {
		return costo;
	}

	public void setCosto(double costo) {
		this.costo = costo;
	}

	public int getItem1() {
		return item1;
	}

	public void setItem1(int item1) {
		this.item1 = item1;
	}

	public int getItem2() {
		return item2;
	}

	public void setItem2(int item2) {
		this.item2 = item2;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + item1;
		result = prime * result + item2;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ItemTabu other = (ItemTabu) obj;
		if (item1 != other.item1)
			return false;
		if (item2 != other.item2)
			return false;
		return true;
	}	
}
