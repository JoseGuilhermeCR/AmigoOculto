import java.io.*;
import java.util.Scanner;

//import sun.security.util.PropertyExpander.ExpandException;

public class CRUD {
    private IndDireto indDireto;
    private IndIndireto indIndireto;

    public CRUD() throws Exception {
        try {
            indDireto = new IndDireto(10,"/dados","/cestos");
            indIndireto = new IndIndireto(10,"/IndIndireto");
            RandomAccessFile arq = new RandomAccessFile("data.db", "rw");
            int n = arq.readInt();
            arq.close();
        } catch (EOFException e) {
            try {
                RandomAccessFile arq = new RandomAccessFile("data.db", "rw");
                arq.writeInt(0);
                arq.close();
            } catch (IOException error) {
                //e.printStackTrace();
            }
        } catch (IOException er) {
            //er.printStackTrace();
        }


        
    }

    public int create(String nome, String email, String senha) throws Exception {
        int cab = -1, ID = 0;
        try {
            RandomAccessFile arq = new RandomAccessFile("data.db", "rw");
            cab = arq.readInt();
            ID = cab + 1;
            arq.seek(0);
            arq.writeInt(ID);
            arq.seek(arq.length());
            long endereco = arq.length();
            indDireto.create(ID, endereco);
            indIndireto.create(email, ID);
            arq.writeBoolean(false);
            Usuario novo = new Usuario(ID, nome, email, senha);
            arq.write(novo.toByteArray());
            arq.close();
        } catch (IOException e) {
            //e.printStackTrace();
        }
        return ID;
    }

    public Usuario read(int ID) throws Exception {
        Usuario doidao = new Usuario();
        try {
            RandomAccessFile arq = new RandomAccessFile("data.db", "rw");
            int cab = arq.readInt();
            long endereco = indDireto.read(ID);
            arq.seek(endereco);
            arq.readBoolean();
            doidao.setID(arq.readInt());
            doidao.setNome(arq.readUTF());
            doidao.setEmail(arq.readUTF());
            doidao.setSenha(arq.readUTF());
            arq.close();
        } catch (IOException e) {
            //e.printStackTrace();
        }
        return doidao;
    }
    public Usuario read(String chaveSec) throws Exception{
        Usuario doidao = new Usuario();
        try{
        int ID = indIndireto.read(chaveSec);
        doidao = read(ID);
        }catch(Exception e){
            doidao.setEmail("");
        }
        return doidao;
    }

    public boolean update(Usuario novo) throws Exception {
        boolean success = false;

        RandomAccessFile arq = new RandomAccessFile("data.db","rw");
        long endereco = indDireto.read(novo.getID());
        arq.seek(endereco);
        long inicio = arq.getFilePointer();
        arq.readBoolean();
        arq.readInt();
        arq.readUTF();
        arq.readUTF();
        arq.readUTF();
        long fim = arq.getFilePointer(), TAM = fim-inicio;
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
            indIndireto.update(novo.getEmail(),novo.getID());
            success = true;
        }
        arq.close();
        return success;
    }
    public boolean delete(int ID) throws Exception{
        boolean success = false;
        long endereco = indDireto.read(ID);
        RandomAccessFile arq = new RandomAccessFile("data.db","rw");
        arq.seek(endereco);
        arq.writeBoolean(true);
        Usuario U = new Usuario();
        U.setID(arq.readInt());
        U.setNome(arq.readUTF());
        U.setEmail(arq.readUTF());
        U.setSenha(arq.readUTF());
        indIndireto.delete(U.getEmail());
        indDireto.delete(U.getID());
        arq.close();
        success = true;
        return success;
    }
    public static void main(String[] args){
        try{
        Scanner sc = new Scanner(System.in);
        CRUD crud = new CRUD();
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
                    int id = crud.create(nome,email,senha);
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
                    Usuario novo = new Usuario(id1,nome1,email1,senha1);
                    if(crud.update(novo)) System.out.println("Sucesso!");
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
		
        }catch(Exception e){
            //e.printStackTrace();
        }
	}
}