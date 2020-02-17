class usuario{
  private int idUsuario;
  private String Nome;
  private String Email;
  private String Senha;

  //Cronstructors
  public usuario(){
    idUsuário = 0;
    Nome = "";
    Email = "";
    Senha = "";
  }

  public usuario(int i, String n, String e, String s){
    idUsuário = i;
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
  //getters---------------------------------------------------------------------
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

}
