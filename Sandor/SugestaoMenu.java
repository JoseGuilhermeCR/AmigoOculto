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

  public static void inclusao(Scanner in, Usuario user, CRUD<Sugestao> crudSugestao, ArvoreBMais_Int_Int relacaoSugestao) throws Exception{
    System.out.print("Qual o nome do produto?: ");
    String produto = in.nextLine();

    if(!produto.equals("")){
      System.out.print("Qual o nome da loja?: ");
      String loja = in.nextLine();
      System.out.print("Qual o preco?: ");
      float valor = in.nextFloat();
      in.nextLine();  //fflush()
      System.out.print("Alguma observacao?: ");
      String observacoes = in.nextLine();
      System.out.print("Tem certeza que quer incluir?(y/n): ");
      String resp = in.nextLine();
      if(resp.equals("y")){
        int idSugestao = crudSugestao.create(new Sugestao(0, user.getID(), produto, loja, observacoes, valor));
        System.out.print("Sugestao incluida \\*-*/");
        relacaoSugestao.create(user.getID(), idSugestao);
      }
    }

  }

  public static void alteracao(Scanner in, Usuario user, ArvoreBMais_Int_Int relacaoSugestao, Menus obj) throws Exception{
    int[] sugestoes = relacaoSugestao.read(user.getID());

    for (int i = 0; i < sugestoes.length; ++i) {
      Sugestao sugestao = obj.crudSugestao.read(sugestoes[i]);
      sugestao.print();
      System.out.println();
    }

    System.out.print("Qual sugestao voce deseja alterar? : ");
    int num = in.nextInt();
    in.nextLine();  //fflush()

    if(num >= 0){
      Sugestao sugestao = obj.crudSugestao.read(sugestoes[num]);
      sugestao.print();
      System.out.println();

      System.out.print("Qual o nome do produto?: ");
      String produto = in.nextLine();
      if(!produto.equals(""))
        sugestao.setProduto(produto);

      System.out.print("Qual o nome da loja?: ");
      String loja = in.nextLine();
      if(!loja.equals(""))
        sugestao.setLoja(loja);

      System.out.print("Qual o preco?: ");
      String valor = in.nextLine();
      if(!produto.equals("")){
        try{
          float v = Float.parseFloat(valor);
          sugestao.setValor(v);
        }catch (Exception e) {
          System.out.println("Erro no valor");
        }
      }

      System.out.print("Alguma observacao?: ");
      String observacoes = in.nextLine();
      if(!produto.equals(""))
        sugestao.setObservacoes(observacoes);

      obj.crudSugestao.update(sugestao);

    }

  }

  public static void exclusao(Scanner in, Usuario user, Menus obj) throws Exception {
    int[] sugestoes = obj.relacaoSugestao.read(user.getID());
    for (int i = 0; i < sugestoes.length; ++i) {
      Sugestao sugestao = obj.crudSugestao.read(sugestoes[i]);
      sugestao.print();
      System.out.println();
    }

    System.out.print("Qual sugestao voce deseja excluir? : ");
    int num = in.nextInt();
    in.nextLine();  //fflush()

    if(num >= 0){
      Sugestao sugestao = obj.crudSugestao.read(sugestoes[num]);
      sugestao.print();
      System.out.println();

      System.out.print("Tem certeza que quer excluir essa sugestao? (y/n): ");
      String resp = in.nextLine();

      if(resp.equals("y")){
        obj.crudSugestao.delete(sugestao.getID());
        obj.relacaoSugestao.delete(user.getID(), sugestao.getID());
        System.out.println("Sugestao excluida\n");
      }
    }

  }

}
