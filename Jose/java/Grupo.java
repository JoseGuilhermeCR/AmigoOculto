import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

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
        sorteado = intToBool(mask & 0x02);
        ativo = intToBool(mask & 0x01);
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

        // Para não gastar dois bytes para dois booleans,
        // irei escrever um byte que contém os dois booleans
        // como bits.
        int sorteado = (this.sorteado) ? 1 : 0;
        int ativo = (this.ativo) ? 1 : 0;

        // Esse byte tem formato 0000 00 (sorteado) (ativo)
        byteStreamOutput.writeByte(((0x00 | sorteado) << 1) | ativo);

		return byteStream.toByteArray();
    }

    public String chaveSecundaria() {
        return idUsuario + "|" + nome;
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