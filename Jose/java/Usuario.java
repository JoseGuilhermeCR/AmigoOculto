/* José Guilherme de Castro Rodrigues - 12/02/2020 */
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Usuario {

	private int id;

	private String nome;
	private String email;
	private String senha;

	public Usuario() {
		this.id = 0;
		this.nome = new String();
		this.email = new String();
		this.senha = new String();
	}

	public Usuario(int id, String nome, String email, String senha) {
		this.id = id;
		this.nome = nome;
		this.email = email;
		this.senha = senha;
	}

	public void setID(int id) {
		this.id = id;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public void fromByteArray(byte[] array) throws IOException {
		ByteArrayInputStream byteStream = new ByteArrayInputStream(array);
		DataInputStream byteStreamInput = new DataInputStream(byteStream);

		id = byteStreamInput.readInt();
		nome = byteStreamInput.readUTF();
		email = byteStreamInput.readUTF();
		senha = byteStreamInput.readUTF();
	}

	public int getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}

	public String getEmail() {
		return email;
	}

	public String getSenha() {
		return senha;
	}

	public byte[] toByteArray() throws IOException {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		DataOutputStream byteStreamOutput = new DataOutputStream(byteStream);

		byteStreamOutput.writeInt(id);
		byteStreamOutput.writeUTF(nome);
		byteStreamOutput.writeUTF(email);
		byteStreamOutput.writeUTF(senha);

		return byteStream.toByteArray();
	}

	public String toString() {
		return id + "|" + nome + "|" + email + "|" + senha;
	}
}
