public class CRUD {
  public final String diretório = "dados";

  public RandomAccessFile arquivo;
  public HashExtensível índiceDireto;
  public ArvoreBMais_String_Int índiceIndireto;

  public CRUD(String nomeArquivo) throws Exception {

    File d = new File(this.diretório);
    if(!d.exists()) d.mkdir();

    arquivo = new RandomAccessFile(this.diretório+"/"+nomeArquivo+".db", "rw");

    if(arquivo.length()<4) arquivo.writeInt(0);  // cabeçalho do arquivo

    índiceDireto = new HashExtensível( 10, this.diretório+"/diretorio."+nomeArquivo+".idx", this.diretório+"/cestos."+nomeArquivo+".idx");
    índiceIndireto = new ArvoreBMais_String_Int( 10, this.diretório+"/arvoreB."+nomeArquivo+".idx");

  }//CRUD

  int create(String Nome, String Email, String Senha) throws IOException{



    return id;
  }

}
