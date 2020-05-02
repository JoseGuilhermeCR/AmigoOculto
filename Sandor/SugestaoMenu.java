import java.io.*;
import java.util.Scanner;

class SugestaoMenu{

  public static void menuSugestao(boolean invalida){

    if(!invalida)
      System.out.print("\n\nAMIGO OCULTO 1.0\n" + "============================\n\n" +
                      "INICIO > SUGESTOES\n\n" + "1) Listar\n" +
                      "2) Incluir\n" + "3) Alterar\n" + "4) Excluir\n\n" +
                      "0) Sair\n\n" + "Opcao: ");
    else
      System.out.print("\n\nOpcao Invalida!!!\n\n" + "INICIO > SUGESTOES\n\n" +
                       "1) Listar\n" + "2) Incluir\n" +
                       "3) Alterar\n" + "4) Excluir\n\n" +
                       "0) Sair\n\n" + "Opcao: ");
  }

  public static void listagem(Scanner in, Usuario user, Menus obj) throws Exception{
    int[] suggestions = obj.relacaoSugestao.read(user.getID());

    for (int i = 0; i < suggestions.length; ++i) {
      Sugestao sugestao = obj.crudSugestao.read(suggestions[i]);
      sugestao.print();
      System.out.println();
    }

  }

  public static void inclusao(Scanner in, Usuario user, CRUD<Sugestao> crudSugestao) throws Exception{
    System.out.print("Qual o nome do produto?: ");
    String produto = in.nextLine();

    if(!produto.equals("")){
      System.out.print("Qual o nome da loja?: ");
      String loja = in.nextLine();
      System.out.print("Qual o preco?: ");
      float valor = in.nextFloat();
      //in.nextLine();
      System.out.print("Alguma observacao?: ");
      String observacoes = in.nextLine();
      System.out.print("Tem certeza que quer incluir?(y/n): ");
      String resp = in.nextLine();
      if(resp.equals("y")){
        crudSugestao.create(new Sugestao(0, user.getID(), produto, loja, observacoes, valor));
        System.out.print("Sugestao incluida \\*-*/");
      }
    }

  }

  public static void alteracao(Scanner in, Usuario user){

  }

  public static void exclusao(Scanner in, Usuario user){

  }

}
