import java.io.IOException;

public abstract class Entidade {

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

	abstract public String chaveSecundaria();

	abstract public void fromByteArray(byte[] bytes) throws IOException;
	abstract public byte[] toByteArray() throws IOException;
}
