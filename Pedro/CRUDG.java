import java.io.*;
import java.lang.reflect.Constructor;
import java.util.Scanner;

//import sun.security.util.PropertyExpander.ExpandException;

public class CRUDG <T extends Entidade> {
    Constructor<T> construtor;
    private IndDireto indDireto;
    private IndIndireto indIndireto;
    private String fileName;
    private RandomAccessFile arq;

    public CRUDG(String nomeArq,Constructor<T> construtor) throws Exception {
        try {
            this.construtor = construtor;
            this.fileName = nomeArq+".db";
            indDireto = new IndDireto(10,"/dados","/"+fileName+"INDD"+".db");
            indIndireto = new IndIndireto(10,fileName+"INDID"+".db");
            arq = new RandomAccessFile(fileName, "rw");
            if(arq.length() < 4){
                arq.writeInt(0);
            }
            //arq.seek(0);
            //int n = arq.readInt();
        } catch (IOException er) {
            er.printStackTrace();
        }


        
    }

    public int create(T Entidade) throws Exception {
        int cab = -1, ID = 0;
        try {
            arq.seek(0);
            cab = arq.readInt();
            ID = cab + 1;
            Entidade.setID(ID);
            arq.seek(0);
            arq.writeInt(ID);
            arq.seek(arq.length());
            long endereco = arq.length();
            indDireto.create(ID, endereco);
            indIndireto.create( Entidade.chaveSecundaria() , ID);
            arq.writeBoolean(false);
            arq.write(Entidade.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ID;
    }

    public T read(int ID) throws Exception {
        T doidao = this.construtor.newInstance();
        try {
            //RandomAccessFile arq = new RandomAccessFile(fileName, "rw");
            arq.seek(0);
            int cab = arq.readInt();
            long endereco = indDireto.read(ID);
            arq.seek(endereco);
            arq.readBoolean();
            doidao.fromByteArray(fileName,arq.getFilePointer());
        } catch (IOException e) {
            //e.printStackTrace();
        }
        return doidao;
    }
    public T read(String chaveSec) throws Exception{
        T doidao = this.construtor.newInstance();
        try{
            int ID = indIndireto.read(chaveSec);
            doidao = read(ID);
        }catch(Exception e){
            e.printStackTrace();
        }
        return doidao;
    }

    public boolean update(T novo) throws Exception {

        boolean success = false;
        long endereco = indDireto.read(novo.getID());
        arq.seek(endereco);
        //arq.readBoolean();
        long TAM = this.read(novo.getID()).toByteArray().length+1;
        if(TAM == (novo.toByteArray().length+1)){
            arq.seek(endereco);
            arq.writeBoolean(false);
            arq.write(novo.toByteArray());
            success = true;
        } else{
            arq.seek(endereco);
            arq.writeBoolean(true);
            arq.seek(arq.length());
            endereco = arq.length();
            arq.writeBoolean(false);
            arq.write(novo.toByteArray());
            indDireto.update(novo.getID(), endereco);
            indIndireto.update(novo.chaveSecundaria(),novo.getID());
            success = true;
        }
        return success;
    }
    public boolean delete(int ID) throws Exception{
        T Ent = this.construtor.newInstance();
        boolean success = false;
        long endereco = indDireto.read(ID);
        arq.seek(endereco);
        arq.writeBoolean(true);
        Ent.fromByteArray(this.fileName, endereco);
        indIndireto.delete(Ent.chaveSecundaria());
        indDireto.delete(Ent.getID());
        success = true;
        return success;
    }

    public void close() throws IOException {
        this.arq.close();
    }
    public static void main(String[] args){
        
        try{
        Scanner sc = new Scanner(System.in);
        CRUDG<Usuario> crud = new CRUDG<Usuario>("usuarios",Usuario.class.getConstructor());
        System.out.println("Bem-vindo ao CRUD, o que deseja fazer?\n"+
                            "1- Criar\n"+
                            "2- Ler\n"+
                            "3- Atualizar\n"+
                            "4- Deletar\n"+
                            "0- Sair");
        int resp = sc.nextInt();
        sc.nextLine();
        while(resp != 0){
            switch(resp){
                case 1:
                    System.out.println("Insira nome: ");
                    String nome = sc.nextLine();
                    System.out.println("Insira email: ");
                    String email = sc.nextLine();
                    System.out.println("Insira senha: ");
                    String senha = sc.nextLine();
                    Usuario novo = new Usuario(0,nome,email,senha);
                    int id = crud.create(novo);
                    System.out.println("Criado,id: "+id);
                break;
                
                case 2:
                    System.out.println("Ler por ID ou por Email?\n"+
                                        "1- ID\n"+
                                        "2- Email\n"+
                                        "0- Voltar\n");
                    int respR = sc.nextInt();
                    sc.nextLine();
                    if(respR == 1){
                        System.out.println("Inserir ID: ");
                        respR = sc.nextInt();
                        sc.nextLine();
                        Usuario lido = crud.read(respR);
                        lido.print();
                    } else if(respR == 2){
                        System.out.println("Inserir Email: ");
                        String respS = sc.nextLine();
                        Usuario lido = crud.read(respS);
                        lido.print();
                    }
                break;

                case 3:
                    System.out.println("Qual o ID do usuario a ser atualizado?");
                    int id1 = sc.nextInt();
                    sc.nextLine();
                    System.out.println("Insira novo nome: ");
                    String nome1 = sc.nextLine();
                    System.out.println("Insira novo email: ");
                    String email1 = sc.nextLine();
                    System.out.println("Insira nova senha: ");
                    String senha1 = sc.nextLine();
                    Usuario novoU = new Usuario(id1,nome1,email1,senha1);
                    if(crud.update(novoU)) System.out.println("Sucesso!");
                    else System.out.println("Erro ao atualizar");
                break;
                 
                case 4:
                    System.out.println("Qual o ID do usuario a ser deletado?");
                    int id2 = sc.nextInt();
                    sc.nextLine();
                    if(crud.delete(id2)) System.out.println("Sucesso!");
                    else System.out.println("Erro ao deletar");
                break;
            }
            System.out.println("O que deseja fazer?\n"+
                            "1- Criar\n"+
                            "2- Ler\n"+
                            "3- Atualizar\n"+
                            "4- Deletar\n"+
                            "0- Sair");
            resp = sc.nextInt();
            sc.nextLine();
        }

		sc.close();
		crud.close();
        }catch(Exception e){
            //e.printStackTrace();
        }
        
	}
}