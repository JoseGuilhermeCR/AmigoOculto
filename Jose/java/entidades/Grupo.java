/* José Guilherme de Castro Rodrigues 2020 */

package entidades;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import utils.Utils;

public class Grupo extends Entidade {

	private int idUsuario;

	private String nome;
	private float valor;

	private long momentoSorteio;
	private long momentoEncontro;
	private String localEncontro;

	private String observacoes;
	private boolean sorteado;
	private boolean ativo;

	public Grupo() {
		super();

		idUsuario = 0;

		nome = new String();
		localEncontro = new String();
		observacoes = new String();

		valor = 0.0f;
		momentoEncontro = 0;
		momentoSorteio = 0;

		sorteado = false;
		ativo = true;
	}

	public Grupo(int idUsuario, String nome, float valor, long momentoSorteio, long momentoEncontro,
		String localEncontro, String observacoes)
	{
		super();

		this.idUsuario = idUsuario;
		this.nome = nome;
		this.valor = valor;
		this.momentoSorteio = momentoSorteio;
		this.momentoEncontro = momentoEncontro;
		this.localEncontro = localEncontro;
		this.observacoes = observacoes;

		sorteado = false;
		ativo = true;
	}

	public void fromByteArray(byte[] array) throws IOException {
		ByteArrayInputStream byteStream = new ByteArrayInputStream(array);
		DataInputStream byteStreamInput = new DataInputStream(byteStream);

		id = byteStreamInput.readInt();
		idUsuario = byteStreamInput.readInt();
		nome = byteStreamInput.readUTF();
		valor = byteStreamInput.readFloat();
		momentoSorteio = byteStreamInput.readLong();
		momentoEncontro = byteStreamInput.readLong();
		localEncontro = byteStreamInput.readUTF();
		observacoes = byteStreamInput.readUTF();

		byte mask = byteStreamInput.readByte();
		// Separa a máscara em seus valores reais.
		sorteado = Utils.intToBool(mask & 0x02);
		ativo = Utils.intToBool(mask & 0x01);
	}

	public byte[] toByteArray() throws IOException {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		DataOutputStream byteStreamOutput = new DataOutputStream(byteStream);

		byteStreamOutput.writeInt(id);
		byteStreamOutput.writeInt(idUsuario);
		byteStreamOutput.writeUTF(nome);
		byteStreamOutput.writeFloat(valor);
		byteStreamOutput.writeLong(momentoSorteio);
		byteStreamOutput.writeLong(momentoEncontro);
		byteStreamOutput.writeUTF(localEncontro);
		byteStreamOutput.writeUTF(observacoes);

		/* Para não gastar dois bytes para dois booleans, 
		* empacotaremos os booleans em um byte.
		* Esse byte tem formato 0000 00 (sorteado) (ativo). */
		byteStreamOutput.writeByte(((0x00 | Utils.boolToInt(sorteado)) << 1) | Utils.boolToInt(ativo));

		return byteStream.toByteArray();
	}

	public String chaveSecundaria() {
		return idUsuario + "|" + nome;
	}

	public void prettyPrint() {
		String stringEncontro = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date(momentoEncontro));
		System.out.println("\t" + nome + " " + stringEncontro + " " + localEncontro);
	}

	public int getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public float getValor() {
		return valor;
	}

	public void setValor(float valor) {
		this.valor = valor;
	}

	public long getMomentoSorteio() {
		return momentoSorteio;
	}

	public void setMomentoSorteio(long momentoSorteio) {
		this.momentoSorteio = momentoSorteio;
	}

	public long getMomentoEncontro() {
		return momentoEncontro;
	}

	public void setMomentoEncontro(long momentoEncontro) {
		this.momentoEncontro = momentoEncontro;
	}

	public String getLocalEncontro() {
		return localEncontro;
	}

	public void setLocalEncontro(String localEncontro) {
		this.localEncontro = localEncontro;
	}

	public String getObservacoes() {
		return observacoes;
	}

	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}

	public boolean isSorteado() {
		return sorteado;
	}

	public void setSorteado(boolean sorteado) {
		this.sorteado = sorteado;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
}