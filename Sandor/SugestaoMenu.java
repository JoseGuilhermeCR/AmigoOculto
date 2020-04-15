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

  public static void listagem(Scanner in, Usuario user, Menus obj){
    int[] suggestions = obj.relacaoSugestao.read(user.getID());

    for (int i = 0; i < suggestions.length; ++i) {
      Sugestao sugestao = obj.crudSugestao.read(suggestions[i]);
      sugestao.print();
      System.out.println();
    }
    
  }

  public static void inclusao(Scanner in, Usuario user){

  }

  public static void alteracao(Scanner in, Usuario user){

  }

  public static void exclusao(Scanner in, Usuario user){

  }

}
