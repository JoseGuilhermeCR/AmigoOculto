import java.io.*;

 class main{
  public static void main(String[] args)throws IOException {
    try{
      CRUD c = new CRUD("data");
      c.create("ar","bola","carro");
    }catch (Exception e) {
      e.printStackTrace();
    }

  }
}
