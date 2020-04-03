import java.io.*;
import java.lang.reflect.Constructor;

public class CRUD<T extends Entidade> {
  public final String diretorio = "dados";

  public RandomAccessFile arquivo;
  public HashExtensivel indiceDireto;
  public ArvoreBMais_String_Int indiceIndireto;
  public Constructor<T> ConstructorT;

  public CRUD(String nomeArquivo, Constructor<T> ConstructorT) throws Exception {

    this.ConstructorT = ConstructorT;
    File d = new File(this.diretorio);
    if(!d.exists()) d.mkdir();

    arquivo = new RandomAccessFile(this.diretorio+"/"+nomeArquivo+".db", "rw");

    if(arquivo.length() < 4) arquivo.writeInt(0);  // cabeçalho do arquivo

    indiceDireto = new HashExtensivel( 10, this.diretorio+"/diretorio."+nomeArquivo+".idx", this.diretorio+"/cestos."+nomeArquivo+".idx");
    indiceIndireto = new ArvoreBMais_String_Int( 10, this.diretorio+"/arvoreB."+nomeArquivo+".idx");

  }//CRUD

  public int create(T entity) throws Exception{

    arquivo.seek(0);
    int id = arquivo.readInt();   //reads and updates the id for the new entity
    //System.out.println(id);
    id++;
    entity.setID(id);

    arquivo.seek(0);        //updates the file id header
    arquivo.writeInt(id);

    byte[] i = entity.toByteArray();        // creates a byte array with the entity info.
    arquivo.seek(arquivo.length());     // goes to the end of the file
    long offSet = arquivo.getFilePointer();
    boolean lapide = true;
    arquivo.writeBoolean(lapide);    // writes that it is a valid registry
    arquivo.writeShort(i.length);    // writes the size of the entity
    arquivo.write(i);                // writes the entity it self

    indiceDireto.create(entity.getID(),offSet);
    indiceIndireto.create(entity.chaveSecundaria(),entity.getID());

    return id;
  }

  public T read(int id) throws Exception{
    T entity = ConstructorT.newInstance();

    long address = indiceDireto.read(id);  //acha o endereço

    if(address > 0){
      arquivo.seek(address);    //vai para o endereco

      short size = 0;
      if(arquivo.readBoolean()){         //checks if the registry is not marked for deletion
        size = arquivo.readShort();      //
        byte[] data = new byte[size];
        arquivo.read(data);

        entity.fromByteArray(data);
      }

      //System.out.println(entity.getNome());
    } else {
      entity = null;
    }
    return entity;
  }

  public T read(String chave) throws Exception{
    return read(indiceIndireto.read(chave));
  }
  //----------------------------------------------------------------------------

  public void update(T updatedEntity) throws Exception{

    long address = indiceDireto.read(updatedEntity.getID());

    arquivo.seek(address+1);
    short entitySize = arquivo.readShort();

    byte[] updatedByteArray = updatedEntity.toByteArray();
    short updatedSize = (short)updatedByteArray.length;

    //System.out.println(entitySize +" | "+ updatedSize);

    if(entitySize == updatedSize){
      arquivo.seek(address+3);
      arquivo.write(updatedByteArray);

    } else {
      arquivo.seek(address);
      arquivo.writeBoolean(false);
      arquivo.seek(arquivo.length());
      long offSet = arquivo.getFilePointer();
      boolean lapide = true;
      arquivo.writeBoolean(lapide);
      arquivo.writeShort(updatedByteArray.length);
      arquivo.write(updatedByteArray);
      indiceDireto.update(updatedEntity.getID(), offSet);

    }

    indiceIndireto.update(updatedEntity.chaveSecundaria(), updatedEntity.getID());

  }

  public void delete(int id) throws Exception{
    long address = indiceDireto.read(id);
    arquivo.seek(address+1);

    T entity = ConstructorT.newInstance();
    short size = 0;
    size = arquivo.readShort();
    byte[] data = new byte[size];
    arquivo.read(data);
    entity.fromByteArray(data);

    String chave = entity.chaveSecundaria();

    arquivo.seek(address);
    arquivo.writeBoolean(false);

    indiceDireto.delete(id);
    indiceIndireto.delete(chave);

  }

}






//
