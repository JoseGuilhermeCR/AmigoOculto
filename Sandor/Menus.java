import java.io.*;
import java.util.Scanner;

class Menus {

  CRUD<Usuario> crudUser;

  public void telaMenu(Scanner in){

    inicio(false);
    int acesso = in.nextInt();
    while(acesso != 0){
      Usuario user;
      //CRUD<Usuario> crudUser;
      try{
        crudUser = new CRUD<>("data", Usuario.class.getConstructor());

        while(acesso < 0 || acesso > 2){
          inicio(true);
          acesso = in.nextInt();
        }
        in.nextLine(); //"fflush()"

        if(acesso == 1){
          login(in);
        } else if(acesso == 2){
          cadastro(in);
        }

      }catch (Exception e) {
        e.printStackTrace();
      }

      if(acesso != 0){
        inicio(false);
        acesso = in.nextInt();
      }

    }

  }

  public void cadastro(Scanner in){
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

  public void login(Scanner in){
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
            telaPrincipal(in);
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

  public void telaPrincipal(Scanner in){

    menuUsuario(false);
    int acesso = in.nextInt();
    while(acesso != 0){
      Usuario user;
      //CRUD<Usuario> crudUser;
      try{
        crudUser = new CRUD<>("data", Usuario.class.getConstructor());

        while(acesso < 0 || acesso > 3){
          menuUsuario(true);
          acesso = in.nextInt();
        }
        in.nextLine(); //"fflush()"

        if(acesso == 1){
          sugestao(in);
        } else if(acesso == 2){
          grupos(in);
        } else if(acesso == 3){
          convites(in);
        }

      }catch (Exception e) {
        e.printStackTrace();
      }

      if(acesso != 0){
        menuUsuario(false);
        acesso = in.nextInt();
      }

    }
  }

  public void sugestao(Scanner in){

  }

  public void grupos(Scanner in){

  }

  public void convites(Scanner in){

  }

  //----------------------------------------------------------------------------
  //Static methods that print the menus
  public static void inicio(boolean invalida){

    if(!invalida)
      System.out.print("AMIGO OCULTO\n================\n\n" + "ACESSO\n\n" +
                       "1 - acesso ao sistema\n2 - Novo usuario(primeiro acesso)\n" +
                       "0 - Sair\n\nopcao: ");
    else
      System.out.print("\n------------------------------------------------\n" +
                       "\n!!!opcao invalida!!!\n\n1 - acesso ao sistema\n" +
                       "2 - Novo usuario(primeiro acesso)\n0 - Sair\n\nopcao: ");
  }

  public static void menuUsuario(boolean invalida){

    if(!invalida)
      System.out.print("AMIGO OCULTO 1.0\n" + "============================\n\n" +
                      "INICIO:\n\n" + "1) Sugestao de presentes\n" +
                      "2) Grupos\n" + "3) Novos Convites\n\n" +
                      "0) Sair\n\n" + "Opcao: ");
    else
      System.out.print("Opcao Invalida!!!\n\n" + "1) Sugestao de presentes\n" +
                      "2) Grupos\n" + "3) Novos Convites\n\n" +
                      "0) Sair\n\n" + "Opcao: ");
  }

  public static void menuSugestao(){

  }
}
