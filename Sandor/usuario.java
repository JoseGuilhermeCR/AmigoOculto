import java.io.*;

class Usuario implements Entidade{

  private int idUsuario;
  private String Nome;
  private String Email;
  private String Senha;

  //Cronstructors
  public Usuario(){
    idUsuario = 0;
    Nome = "";
    Email = "";
    Senha = "";
  }

  public Usuario(int i, String n, String e, String s){
    idUsuario = i;
    Nome = n;
    Email = e;
    Senha = s;
  }

  //Setters---------------------------------------------------------------------
  public void setID(int id){
    idUsuario = id;
  }
  public void setNome(String n){
    Nome = n;
  }
  public void setEmail(String e){
    Email = e;
  }
  public void setSenha(String s){
    Senha = s;
  }
  //Getters---------------------------------------------------------------------
  public int getID(){
    return idUsuario;
  }
  public String getNome(){
    return Nome;
  }
  public String getEmail(){
    return Email;
  }
  public String getSenha(){
    return Senha;
  }
  //----------------------------------------------------------------------------

  public String chaveSecundaria(){
    return Email;
  }

  public byte[] toByteArray() throws IOException {
    ByteArrayOutputStream dados = new ByteArrayOutputStream();
    DataOutputStream saida = new DataOutputStream(dados);
    saida.writeInt(this.idUsuario);
    // Escrever os demais atributos do objeto usando métodos como writeInt(), writeUTF(), writeFloat(), ...
    saida.writeUTF(Nome);
    saida.writeUTF(Email);
    saida.writeUTF(Senha);

    return dados.toByteArray();
  }

  public void fromByteArray(byte[] bytes) throws IOException {
    ByteArrayInputStream dados = new ByteArrayInputStream(bytes);
    DataInputStream entrada = new DataInputStream(dados);

    idUsuario = entrada.readInt();
    Nome = entrada.readUTF();
    Email = entrada.readUTF();
    Senha = entrada.readUTF();
  }

  public void printUser(){
    System.out.println("ID: " + idUsuario);
    System.out.println("Nome: " + Nome);
    System.out.println("Email: " + Email);
    System.out.println("Senha: " + Senha);
  }

}
