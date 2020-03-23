/* José Guilherme de Castro Rodrigues 2020 */

package utils;

import java.util.Scanner;
import ui.Resultado;

public class Utils {

	public static Scanner scanner = new Scanner(System.in);

	public static int readInt() {
		int i = scanner.nextInt();
		scanner.nextLine(); // limpa buffer

		return i;
	}
	
	public static boolean confirmar(String mensagem) {
		System.out.print(mensagem + " (sim/não): ");
		String confirmacao = scanner.nextLine();

		return (confirmacao.toLowerCase().equals("sim"));
	}

	public static void mostrarMensagemResultado(Resultado resultado) {
		if (!resultado.valido()) {
			System.out.println("ERRO: " + resultado.mensagem() + "\n");
		} else {
			System.out.println(resultado.mensagem() + "\n");
		}
		resultado.limparMensagem();
	}

	public static void limpaTela() {
		try {
			// Equivalente em Linux usando BASH
			// new ProcessBuilder("bash", "-c", "clear").inheritIO().start().waitFor();
			// Windows
			new ProcessBuilder("cmd", "/C", "cls").inheritIO().start().waitFor();
		} catch (Exception exception) {
			// Talvez escrever no arquivo de log?
			exception.printStackTrace();
		}
	}

	// Funções de ajuda para possíveis necessidades das entidades.
	public static int boolToInt(boolean b) {
		return (b) ? 1 : 0;
	}

	public static boolean intToBool(int i) {
		return (i == 0) ? false : true;
	}
}