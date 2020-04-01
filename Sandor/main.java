import java.io.*;
import java.util.Scanner;

 class main{
  public static void main(String[] args)throws IOException {

    Scanner in = new Scanner(System.in);
    telaMenu();
    int acesso = in.nextInt();
    while(acesso != 0){
      Usuario user;
      CRUD crud;
      try{
        crud = new CRUD("data");

        while(acesso < 0 || acesso > 2){
          System.out.println("\n------------------------------------------------");
          System.out.print("\n!!!opcao invalida!!!\n\n1 - acesso ao sistema\n2 - Novo usuario(primeiro acesso)\n0 - Sair\n\nopcao: ");
          acesso = in.nextInt();
        }
        in.nextLine(); //"fflush()"

        if(acesso == 1){
          login(in, crud);
        } else if(acesso == 2){
          cadastro(in,crud);
        }

      }catch (Exception e) {
        e.printStackTrace();
      }

      if(acesso != 0){
        telaMenu();
        acesso = in.nextInt();
      }

    }
    //System.out.println(user.getNome());
  }

  public static void telaMenu(){
    System.out.print("AMIGO OCULTO\n================\n\n");
    System.out.print("ACESSO\n\n1 - acesso ao sistema\n2 - Novo usuario(primeiro acesso)\n0 - Sair\n\nopcao: ");
  }

  public static void cadastro(Scanner in, CRUD crud){
    try{
      Usuario user = new Usuario();
      boolean flag = false;
      String email = "";

      System.out.print("\n\nNOVO USUARIO\n-------------\n\nEMAIL: ");
      email = in.nextLine();
      while(!email.equals("") && flag != true){
        user = crud.read(email);

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
            crud.create(nome, email, senha);
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

  public static void login(Scanner in, CRUD crud){
    System.out.println("\n\nACESSO AO SISTEMA\n");
    System.out.print("Email: ");

    Usuario user = null;
    boolean flag = false;
    String email = in.nextLine();
    while (!flag) {
      user = crud.read(email);
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
    }


  }

  public static void telaPrincipal(){

  }
}//end class
