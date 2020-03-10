/* José Guilherme de Castro Rodrigues - 10/03/2020 */
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public class Sugestao extends Entidade {
	
	private int idUsuario;

	private float valor;

	private String produto;
	private String loja;
	private String observacoes;

	public Sugestao() {
		super();

		this.idUsuario = 0;

		this.valor = 0.f;

		this.produto = new String();
		this.loja = new String();
		this.observacoes = new String();
	}

	public void setIDUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}

	public void setValor(float valor) {
		this.valor = valor;
	}

	public void setProduto(String produto) {
		this.produto = produto;
	}

	public void setLoja(String loja) {
		this.loja = loja;
	}

	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}

	public void fromByteArray(byte[] array) throws IOException {
		ByteArrayInputStream byteStream = new ByteArrayInputStream(array);
		DataInputStream byteStreamInput = new DataInputStream(byteStream);

		id = byteStreamInput.readInt();
		idUsuario = byteStreamInput.readInt();
		valor = byteStreamInput.readFloat();
		produto = byteStreamInput.readUTF();
		loja = byteStreamInput.readUTF();
		observacoes = byteStreamInput.readUTF();
	}

	public int getIDUsuario() {
		return idUsuario;
	}

	public float getValor() {
		return valor;
	}

	public String getProduto() {
		return produto;
	}

	public String getLoja() {
		return loja;
	}

	public String getObservacoes() {
		return observacoes;
	}

	public byte[] toByteArray() throws IOException {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		DataOutputStream byteStreamOutput = new DataOutputStream(byteStream);

		byteStreamOutput.writeInt(id);
		byteStreamOutput.writeInt(idUsuario);
		byteStreamOutput.writeFloat(valor);
		byteStreamOutput.writeUTF(produto);
		byteStreamOutput.writeUTF(loja);
		byteStreamOutput.writeUTF(observacoes);

		return byteStream.toByteArray();
	}

	public String toString() {
		return id + "|" + idUsuario + "|" + valor + "|" +
			produto + "|" + loja + "|" + observacoes;
	}

	public String chaveSecundaria() {
		return idUsuario + "|" + produto;
	}
}
