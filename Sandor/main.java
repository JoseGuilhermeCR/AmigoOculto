import java.io.*;

 class main{
  public static void main(String[] args)throws IOException {
    Usuario user;
    try{
      CRUD c = new CRUD("data");
      c.create("ar","goleiro","carro");
      user = c.read("goleiro");

      //System.out.println(user.getNome());
      user.printUser();

    }catch (Exception e) {
      e.printStackTrace();
    }

  }
}
