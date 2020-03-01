import java.io.*;

public class CRUD {
  public final String diretorio = "dados";

  public RandomAccessFile arquivo;
  //public HashExtensível índiceDireto;
  //public ArvoreBMais_String_Int índiceIndireto;

  public CRUD(String nomeArquivo) throws Exception {

    File d = new File(this.diretorio);
    if(!d.exists()) d.mkdir();

    arquivo = new RandomAccessFile(this.diretorio+"/"+nomeArquivo+".db", "rw");

    if(arquivo.length() < 4) arquivo.writeInt(0);  // cabeçalho do arquivo

    //índiceDireto = new HashExtensível( 10, this.diretorio+"/diretorio."+nomeArquivo+".idx", this.diretorio+"/cestos."+nomeArquivo+".idx");
    //índiceIndireto = new ArvoreBMais_String_Int( 10, this.diretorio+"/arvoreB."+nomeArquivo+".idx");

  }//CRUD

  public int create(String Nome, String Email, String Senha) throws IOException{

    arquivo.seek(0);
    int id = arquivo.readInt();
    System.out.println(id);
    id++;

    arquivo.seek(0);
    arquivo.writeInt(id);

    Usuario user = new Usuario(id,Nome,Email,Senha);
    byte[] i = user.toByteArray();
    arquivo.seek(arquivo.length());
    char lapide = ' ';
    arquivo.write(lapide);
    arquivo.write(i.length);
    arquivo.write(i);

    /* Not complete, there still is a need for the indexing */

    return id;
  }

  

}
