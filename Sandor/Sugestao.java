import java.io.*;

class Sugestao implements Entidade{

  private int idSugestao, idUsuario;
  private String produto, loja, observacoes;
  private float valor;

  public Sugestao(){
    idSugestao = idUsuario = 0;
    produto = loja = observacoes = "";
    valor = 0;
  }

  public Sugestao(int idSugestao, int idUsuario, String produto, String loja, String observacoes, float valor){
    this.idSugestao = idSugestao;
    this.idUsuario = idUsuario;
    this.produto = produto;
    this.loja = loja;
    this.observacoes = observacoes;
    this.valor = valor;
  }

  //----------------------------------------------------------------------------
  //Settes
  public void setID(int idSugestao){
    this.idSugestao = idSugestao;
  }

  public void setIdUsuario(int idUsuario){
    this.idUsuario = idUsuario;
  }

  public void setProduto(String produto){
    this.produto = produto;
  }

  public void setLoja(String loja){
    this.loja = loja;
  }

  public void setObservacoes(String observacoes){
    this.observacoes = observacoes;
  }

  public void setValor(float valor){
    this.valor = valor;
  }

  //----------------------------------------------------------------------------
  //Getters
  public int getID(){
    return idSugestao;
  }

  public int getIdUsuario(){
    return idUsuario;
  }

  public String getProduto(){
    return produto;
  }

  public String getLoja(){
    return loja;
  }

  public String getObservacoes(){
    return observacoes;
  }

  public float getValor(){
    return valor;
  }
  //----------------------------------------------------------------------------

  public String chaveSecundaria(){
    return this.idUsuario + "|" + this.produto;
  }

  public byte[] toByteArray(){
    ByteArrayOutputStream dados = new ByteArrayOutputStream();
    DataOutputStream saida = new DataOutputStream(dados);
    try{

      saida.writeInt(idSugestao);
      saida.writeInt(idUsuario);
      saida.writeUTF(produto);
      saida.writeUTF(loja);
      saida.writeUTF(observacoes);
      saida.writeFloat(valor);


    } catch (Exception e) {
      e.printStackTrace();
    }
    return dados.toByteArray();
  }

  public void fromByteArray(byte[] bytes){
    ByteArrayInputStream dados = new ByteArrayInputStream(bytes);
    DataInputStream entrada = new DataInputStream(dados);

    try{
      idSugestao = entrada.readInt();
      idUsuario = entrada.readInt();
      produto = entrada.readUTF();
      loja = entrada.readUTF();
      observacoes = entrada.readUTF();
      valor = entrada.readFloat();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void print(){
    System.out.println(produto+"\n\t"+loja+"\n\tR$"+valor+"\n\t"+observacoes);

  }

}//end class
