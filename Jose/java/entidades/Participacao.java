/* José Guilherme de Castro Rodrigues 2020 */

package entidades;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Participacao extends Entidade {
	
	private int idUsuario;	// ID do usuário que participará do grupo.
	private int idGrupo;	// ID do grupo no qual o usuário participará.
	private int idAmigo;	// ID do usuário que será presenteado pelo usuário da participação.

	public Participacao() {
		super();
	}

	public Participacao(int idUsuario, int idGrupo, int idAmigo) {
		super();
		
		this.idUsuario = idUsuario;
		this.idGrupo = idGrupo;
		this.idAmigo = idAmigo;
	}

	public void setIDUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}

	public int getIDUsuario() {
		return idUsuario;
	}

	public void setIDGrupo(int idGrupo) {
		this.idGrupo = idGrupo;
	}

	public int getIDGrupo() {
		return idGrupo;
	}

	public void setIDAmigo(int idAmigo) {
		this.idAmigo = idAmigo;
	}

	public int getIDAmigo() {
		return idAmigo;
	}

	public void fromByteArray(byte[] array) throws IOException {
		ByteArrayInputStream byteStream = new ByteArrayInputStream(array);
		DataInputStream byteStreamInput = new DataInputStream(byteStream);

		id = byteStreamInput.readInt();
		idUsuario = byteStreamInput.readInt();
		idGrupo = byteStreamInput.readInt();
		idAmigo = byteStreamInput.readInt();
	}

	public byte[] toByteArray() throws IOException {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		DataOutputStream byteStreamOutput = new DataOutputStream(byteStream);

		byteStreamOutput.writeInt(id);
		byteStreamOutput.writeInt(idUsuario);
		byteStreamOutput.writeInt(idGrupo);
		byteStreamOutput.writeInt(idAmigo);
		
		return byteStream.toByteArray();
	}

	public String chaveSecundaria() {
		return idUsuario + "|" + idGrupo;	
	}
}
