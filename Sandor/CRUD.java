import java.io.*;

public class CRUD {
  public final String diretorio = "dados";

  public RandomAccessFile arquivo;
  public HashExtensivel indiceDireto;
  public ArvoreBMais_String_Int indiceIndireto;

  public CRUD(String nomeArquivo) throws Exception {

    File d = new File(this.diretorio);
    if(!d.exists()) d.mkdir();

    arquivo = new RandomAccessFile(this.diretorio+"/"+nomeArquivo+".db", "rw");

    if(arquivo.length() < 4) arquivo.writeInt(0);  // cabeçalho do arquivo

    indiceDireto = new HashExtensivel( 10, this.diretorio+"/diretorio."+nomeArquivo+".idx", this.diretorio+"/cestos."+nomeArquivo+".idx");
    indiceIndireto = new ArvoreBMais_String_Int( 10, this.diretorio+"/arvoreB."+nomeArquivo+".idx");

  }//CRUD

  public int create(String Nome, String Email, String Senha) throws Exception{

    arquivo.seek(0);
    int id = arquivo.readInt();   //reads and updates the id for the new user
    System.out.println(id);
    id++;

    arquivo.seek(0);        //updates the file id header
    arquivo.writeInt(id);

    Usuario user = new Usuario(id,Nome,Email,Senha);  //creates the user to put into the file
    byte[] i = user.toByteArray();
    arquivo.seek(arquivo.length());
    long offSet = arquivo.getFilePointer();
    boolean lapide = true;
    arquivo.writeBoolean(lapide);
    arquivo.writeShort(i.length);
    arquivo.write(i);

    indiceDireto.create(user.getID(),offSet);
    indiceIndireto.create(user.getEmail(),user.getID());

    return id;
  }

  public Usuario read(int id) throws Exception{
    Usuario user = new Usuario();

    long address = indiceDireto.read(id);  //acha o endereço

    arquivo.seek(address);    //vai para o endereco

    short size = 0;
    if(arquivo.readBoolean()){
      size = arquivo.readShort();
      byte[] data = new byte[size];
      arquivo.read(data);

      user.fromByteArray(data);
    }

    System.out.println(user.getNome());

    return user;
  }

  public Usuario read(String chave) throws Exception{
    return read(indiceIndireto.read(chave));
  }

}
