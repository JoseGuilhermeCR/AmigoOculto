import java.io.*;
import java.util.Scanner;

public class Usuario {
	private int idUsuario;
	private String nome;
	private String email;
	private String senha;

	public Usuario() {
		this.idUsuario = 0;
		this.nome = "vazio";
		this.email = "vazio";
		this.senha = "vazio";
	}

	public Usuario(int id, String nome, String email, String senha) {
		this.idUsuario = id;
		this.nome = nome;
		this.email = email;
		this.senha = senha;
	}

	public void setID(int ID) {
		this.idUsuario = ID;
	}

	public int getID() {
		return this.idUsuario;
	}

	public void setNome(String NOME) {
		this.nome = NOME;
	}

	public String getNome() {
		return this.nome;
	}

	public void setEmail(String EMAIL) {
		this.email = EMAIL;
	}

	public String getEmail() {
		return this.email;
	}

	public void setSenha(String SENHA) {
		this.senha = SENHA;
	}

	public String getSenha() {
		return this.senha;
	}

	public String chaveSecundaria() {
		return this.email;
	}

	public byte[] toByteArray() throws IOException {
		ByteArrayOutputStream dados = new ByteArrayOutputStream();
		DataOutputStream saida = new DataOutputStream(dados);
		saida.writeInt(this.idUsuario);
		saida.writeUTF(this.nome);
		saida.writeUTF(this.email);
		saida.writeUTF(this.senha);

		return dados.toByteArray();
	}

	public void fromByteArray(byte[] bytes) throws IOException {
		ByteArrayInputStream dados = new ByteArrayInputStream(bytes);
		DataInputStream entrada = new DataInputStream(dados);
		this.idUsuario = entrada.readInt();
		this.nome = entrada.readUTF();
		this.email = entrada.readUTF();
		this.senha = entrada.readUTF();

	}

	public void print(){
		System.out.println
		("ID: "+this.getID()+"\n"+
		"Nome: "+this.getNome()+"\n"+
		"Email: "+this.getEmail()+"\n"+
		"Senha: "+this.getSenha()+"\n");
	}
}
