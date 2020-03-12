import java.io.*;
import java.util.Scanner;

public class Sistema{
    public static void main(String[]args){
        try{
        System.out.println("     Sistema 1.0\n"+
            "=====================\n");
        Scanner sc = new Scanner(System.in);
        CRUD crud = new CRUD();
        int option = 0; 

        System.out.println("2- Novo Usuario\n"+
                           "1- Acesso ao sistema\n"+
                           "0- Sair\n");
            
        option = sc.nextInt();
        String str = new String();
        String str1 = new String();
        String str2 = new String();
        while(option != 0){
            switch(option){
                case 2:
                    System.out.print("Inserir email: ");
                    sc.nextLine();
                    str = sc.nextLine();
                    if(str.equals("")){
                        System.out.println("Nenhum input, retornando...");
                    }else if(crud.read(str).getID() == 0){
                        System.out.println("Insira nome: ");
                        str1 = sc.nextLine();
                        System.out.println("Insira sua senha: ");
                        str2 = sc.nextLine();
                        crud.create(str1,str,str2);
                        System.out.println("Sucesso, bem-vindo, "+str1+"!");
                    } else if(crud.read(str).getID() > 0){
                        System.out.println("Email ja cadastrado!");
                    }
                break;
                case 1:
                    System.out.print("Inserir email: ");
                    sc.nextLine();
                    str = sc.nextLine();
                    Usuario U = crud.read(str);
                    if(str.equals("")){
                        System.out.println("Nenhum input, retornando...");
                    }else if(crud.read(str).getID() == 0){
                        System.out.println("Erro! Email nao cadastrado");
                    } else if(crud.read(str).getID() > 0){
                        System.out.println("Insira senha: ");
                        str1 = sc.nextLine();
                        if(str1.equals(U.getSenha())){
                            System.out.println("Tela principal TBA");
                        } else {
                            System.out.println("Erro! Senha incorreta");
                        }
                    }
                break;
            }
            System.out.println("2- Novo Usuario\n"+
                           "1- Acesso ao sistema\n"+
                           "0- Sair");
            option = sc.nextInt();
        }
        sc.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}