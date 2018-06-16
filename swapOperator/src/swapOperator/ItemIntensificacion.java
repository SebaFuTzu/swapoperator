package swapOperator;

public class ItemIntensificacion implements Comparable<ItemIntensificacion>{
	private int item;
	private int posicion;
	private int valor;
	
	public ItemIntensificacion(int item, int posicion, int valor) {
		this.item = item;
		this.posicion = posicion;
		this.valor = valor;
	}	
	
	public int getValor() {
		return valor;
	}

	public void setValor(int valor) {
		this.valor = valor;
	}

	public int getItem() {
		return item;
	}

	public void setItem(int item) {
		this.item = item;
	}
	
	public int getPosicion() {
		return posicion;
	}

	public void setPosicion(int posicion) {
		this.posicion = posicion;
	}
	
	@Override
    public int compareTo(ItemIntensificacion otroItem) {
        return getValor() - otroItem.getValor();
    }
}
