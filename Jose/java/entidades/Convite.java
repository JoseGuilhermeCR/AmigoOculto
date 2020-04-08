/* José Guilherme de Castro Rodrigues 2020 */

package entidades;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Convite extends Entidade {

	private	int idGrupo;

	private String emailUsuario;
	private long momentoConvite;
	private byte estado;

	public Convite() {
		super();

		this.emailUsuario = new String();
		this.estado = 0;	// Por padrão, pendente.
	}

	public Convite(int idGrupo, String emailUsuario) {
		this.idGrupo = idGrupo;
		this.emailUsuario = emailUsuario;
		this.estado = 0;	// Por padrão, pendente.
	}

	public void setIdGrupo(int idGrupo) {
		this.idGrupo = idGrupo;
	}

	public void setEmail(String email) {
		this.emailUsuario = emailUsuario;
	}

	public void setMomentoConvite(long momentoConvite) {
		this.momentoConvite = momentoConvite;
	}

	public void setEstado(byte estado) {
		this.estado = estado;
	}

	public void fromByteArray(byte[] array) throws IOException {
		ByteArrayInputStream byteStream = new ByteArrayInputStream(array);
		DataInputStream	byteStreamInput = new DataInputStream(byteStream);

		id = byteStreamInput.readInt();
		idGrupo = byteStreamInput.readInt();
		emailUsuario = byteStreamInput.readUTF();
		momentoConvite = byteStreamInput.readLong();
		estado = byteStreamInput.readByte();
	}

	public int getIdGrupo() {
		return idGrupo;
	}

	public String getEmailUsuario() {
		return emailUsuario;
	}

	public long getMomentoConvite() {
		return momentoConvite;
	}

	public byte getEstado() {
		return estado;
	}

	public byte[] toByteArray() throws IOException {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		DataOutputStream byteStreamOutput = new DataOutputStream(byteStream);

		byteStreamOutput.writeInt(id);
		byteStreamOutput.writeInt(idGrupo);
		byteStreamOutput.writeUTF(emailUsuario);
		byteStreamOutput.writeLong(momentoConvite);
		byteStreamOutput.writeByte(estado);

		return byteStream.toByteArray();
	}

	public String chaveSecundaria() {
		return idGrupo + "|" + emailUsuario;
	}

	public void prettyPrint() {
		String momento = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date(momentoConvite));

		String estadoStr = "pendente";
		if (estado == 1)
			estadoStr = "aceito";
		else if (estado == 2)
			estadoStr = "recusado";
		else if (estado == 3)
			estadoStr = "cancelado";

		System.out.println("\t" + emailUsuario + " (" + momento + ") - " + estadoStr);
	}
}
