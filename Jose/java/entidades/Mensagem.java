/* José Guilherme de Castro Rodrigues 2020 */

package entidades;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;

public class Mensagem extends Entidade {
	
    private int idGrupo;	// ID do grupo do qual a mensagem pertence.
    private int idUsuario;  // Autor da mensagem.
    
    private String titulo;
    private String conteudo;

    private long momentoEnvio;

	public Mensagem() {
        super();

        titulo = new String();
        conteudo = new String();
	}

	public Mensagem(int idGrupo, int idUsuario, String titulo, String conteudo) {
        super();

        this.idGrupo = idGrupo;
        this.idUsuario = idUsuario;
        this.titulo = titulo;
        this.conteudo = conteudo;
        
        momentoEnvio = new Date().getTime();
	}

	public void fromByteArray(byte[] array) throws IOException {
		ByteArrayInputStream byteStream = new ByteArrayInputStream(array);
		DataInputStream byteStreamInput = new DataInputStream(byteStream);

		id = byteStreamInput.readInt();
		
		idGrupo = byteStreamInput.readInt();
        idUsuario = byteStreamInput.readInt();
        titulo = byteStreamInput.readUTF();
        conteudo = byteStreamInput.readUTF();
        momentoEnvio = byteStreamInput.readLong();
	}

	public byte[] toByteArray() throws IOException {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		DataOutputStream byteStreamOutput = new DataOutputStream(byteStream);

        byteStreamOutput.writeInt(id);
        byteStreamOutput.writeInt(idGrupo);
		byteStreamOutput.writeInt(idUsuario);
        byteStreamOutput.writeUTF(titulo);
        byteStreamOutput.writeUTF(conteudo);
        byteStreamOutput.writeLong(momentoEnvio);
		
		return byteStream.toByteArray();
    }

	public String chaveSecundaria() {
        // Não será utilizado.
		return new String();
	}

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public int getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(int idGrupo) {
        this.idGrupo = idGrupo;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public long getMomentoEnvio() {
        return momentoEnvio;
    }

    public void setMomentoEnvio(long momentoEnvio) {
        this.momentoEnvio = momentoEnvio;
    }
}
