public abstract class Entidade implements IEntidade {

	protected int id;

	public Entidade() {
		this.id = 0;
	}

	public void setID(int id) {
		this.id = id;
	}

	public int getID() {
		return id;
	}
}
