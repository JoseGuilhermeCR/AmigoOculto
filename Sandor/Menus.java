import java.io.*;
import java.util.Scanner;

class Menus {

  CRUD<Usuario> crudUser; 

  public static void telaMenu(){
    System.out.print("AMIGO OCULTO\n================\n\n");
    System.out.print("ACESSO\n\n1 - acesso ao sistema\n2 - Novo usuario(primeiro acesso)\n0 - Sair\n\nopcao: ");
  }

  public static void cadastro(Scanner in, CRUD<Usuario> crudUser){
    try{
      Usuario user = new Usuario();
      boolean flag = false;
      String email = "";

      System.out.print("\n\nNOVO USUARIO\n-------------\n\nEMAIL: ");
      email = in.nextLine();
      while(!email.equals("") && flag != true){
        user = crudUser.read(email);

        if(user != null){
          System.out.println("Esse usuario ja esta cadastrado!!!\n\n");
          System.out.print("EMAIL: ");
          email = in.nextLine();  //new user read
        } else {
          System.out.print("nome: ");
          String nome = in.nextLine();
          System.out.print("senha: ");
          String senha = in.nextLine();
          System.out.println("Incluir " + nome + " - " + email + "?(y/n)");
          String resp = in.nextLine();
          if (resp.equals("y")) {
            crudUser.create(new Usuario(0, nome, email, senha));
            System.out.println("\n" + nome + " foi incluido :)\n\n");
            flag = true;
          } else
            flag = true;

        }//else if

      }//while
          //System.out.println(user.getEmail());
    }catch (Exception e){
      e.printStackTrace();
    }

  }

  public static void login(Scanner in, CRUD<Usuario> crudUser){
    try{
      System.out.println("\n\nACESSO AO SISTEMA\n");
      System.out.print("Email: ");

      Usuario user = null;
      boolean flag = false;
      String email = in.nextLine();
      while (!flag) {
        user = crudUser.read(email);
        if(user == null){
          System.out.println("\nEmail nao encontrado!!!");
          System.out.print("Email: ");
          email = in.nextLine();
        } else {
          System.out.print("\nSenha: ");
          String senha = in.nextLine();
          if(senha.equals(user.getSenha())){
            telaPrincipal();
            flag = true;
          } else {
            System.out.println("\nSenha incorreta!!!");
            System.out.print("Email: ");
            email = in.nextLine();
          }

        }
      }//while
    } catch (Exception e) {
      e.printStackTrace();
    }


  }

  public static void telaPrincipal(){
    System.out.println("entrou");
  }


}
