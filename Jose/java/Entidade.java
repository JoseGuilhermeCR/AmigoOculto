public interface Entidade {
	public void setID(int id);
	public int getID();

	public String chaveSecundaria();

	public void fromByteArray(byte[] bytes);
	public byte[] toByteArray();
}
