import java.io.*;
import java.util.Scanner;

 class main{
  public static void main(String[] args)throws IOException {

    Scanner in = new Scanner(System.in);
    Menus menus = new Menus();
    Menus.telaMenu();
    Sugestao i = new Sugestao();
    int acesso = in.nextInt();
    while(acesso != 0){
      Usuario user;
      //CRUD<Usuario> crudUser;
      try{
        menus.crudUser = new CRUD<>("data", Usuario.class.getConstructor());

        while(acesso < 0 || acesso > 2){
          System.out.println("\n------------------------------------------------");
          System.out.print("\n!!!opcao invalida!!!\n\n1 - acesso ao sistema\n2 - Novo usuario(primeiro acesso)\n0 - Sair\n\nopcao: ");
          acesso = in.nextInt();
        }
        in.nextLine(); //"fflush()"

        if(acesso == 1){
          Menus.login(in, menus.crudUser);
        } else if(acesso == 2){
          Menus.cadastro(in, menus.crudUser);
        }

      }catch (Exception e) {
        e.printStackTrace();
      }

      if(acesso != 0){
        Menus.telaMenu();
        acesso = in.nextInt();
      }

    }
    //System.out.println(user.getNome());
  }

}//end class
