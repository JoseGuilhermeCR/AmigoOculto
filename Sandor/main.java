import java.io.*;
import java.util.Scanner;

 class main{
  public static void main(String[] args)throws IOException {
    telaInicial();
    Scanner in = new Scanner(System.in);
    Usuario user;
    try{
      CRUD crud = new CRUD("data");
    }catch (Exception e) {
      e.printStackTrace();
    }

    int acesso = in.nextInt();
    while(acesso < 1 || acesso > 2){
      System.out.println("\n------------------------------------------------");
      System.out.println("\n!!!opcao invalida!!!\n\n1- acesso ao sistema\n2- Novo usuario(primeiro acesso)\n\nopcao: ");
      acesso = in.nextInt();
    }
    in.nextLine();

    if(acesso == 1){

    } else {
      cadastro(in,crud);
    }
    //System.out.println(user.getNome());
  }

  public static void telaInicial(){
    System.out.print("AMIGO OCULTO\n================\n\nACESSO\n\n1- acesso ao sistema\n2- Novo usuario(primeiro acesso)\n\nopcao: ");
  }

  public static void cadastro(Scanner in, CRUD crud){
    System.out.println("\n\nNOVO USUARIO\n-------------\n\nEMAIL: ");
    String email = in.nextLine();
    if(!email.equals("")){
      crud.read();
    }
  }

}//end class
