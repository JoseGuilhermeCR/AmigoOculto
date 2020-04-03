import java.io.IOException;

public interface Entidade {

  public void setID(int id);
  public int getID();
  public byte[] toByteArray() throws IOException;
  public void fromByteArray(byte[] bytes) throws IOException;
  public String chaveSecundaria();

}
