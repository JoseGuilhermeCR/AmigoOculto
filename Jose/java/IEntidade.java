import java.io.IOException;

public interface IEntidade {
	public void setID(int id);
	public int getID();

	public String chaveSecundaria();

	public void fromByteArray(byte[] bytes) throws IOException;
	public byte[] toByteArray() throws IOException;
}
