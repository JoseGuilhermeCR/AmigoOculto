import java.io.IOException;

public interface Entidade{
    public void setID(int ID);
    public int getID();
    public void fromByteArray(byte[] bytes) throws IOException;
    public byte[] toByteArray() throws IOException;
    public long fromByteArray(String fileName,long endereco) throws IOException;
    public String chaveSecundaria();
}