/* José Guilherme de Castro Rodrigues - 10/03/2020 */
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Scanner;

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

	public Sugestao(String produto, float valor, String loja, String observacoes) {
		super();

		this.produto = produto;
		this.valor  = valor;
		this.loja = loja;
		this.observacoes = observacoes;
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

	public void prettyPrint() {
		System.out.println("\t" + produto);
		
		if (!loja.isBlank())
			System.out.println("\t" + loja);

		if (!Float.isNaN(valor))
			System.out.println("\tR$ " + valor);

		if (!observacoes.isBlank())
			System.out.println("\t" + observacoes);
	}

	public boolean equals(Sugestao outra) {
		return (
			produto.equals(outra.getProduto()) &&
			valor == outra.getValor() &&
			loja.equals(outra.getLoja()) &&
			observacoes.equals(outra.getObservacoes())
		);
	}

	// Lê uma nova sugestão que funcionará para alteração.
	public Sugestao lerNovaSugestao(Scanner scanner) {
		// Lê as alterações dos campos.
		System.out.println("O campo deixado em branco não será alterado.");
		System.out.print(
			"Novo nome do produto: "
		);
		String novoNome = scanner.nextLine();

		System.out.print(
			"Nova loja: "
		);
		String novaLoja = scanner.nextLine();

		System.out.print(
			"Novo valor: "
		);
		String novoValorStr = scanner.nextLine();

		float novoValor;
		try {
			novoValor = Float.parseFloat(novoValorStr);
		} catch (Exception e) {
			novoValor = Float.NaN;
		}

		System.out.print(
			"Novas observações: "
		);
		String novasObservacoes = scanner.nextLine();

		Sugestao novaSugestao = new Sugestao(
								(novoNome.isBlank()) ? produto : novoNome,
								(Float.isNaN(novoValor)) ? valor : novoValor,
								(novaLoja.isBlank()) ? loja : novaLoja,
								(novasObservacoes.isBlank()) ? observacoes : novasObservacoes
		);
		novaSugestao.setID(id);
		novaSugestao.setIDUsuario(idUsuario);

		return novaSugestao;
	}
}
