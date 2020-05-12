/* José Guilherme de Castro Rodrigues 2020 */

package utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import ui.Resultado;

public class Utils {

	public static String clear = null;
	public static Scanner scanner = new Scanner(System.in);

	/* Lê a data com o formato indicado. Caso seja vazio, usa o formato padrão dd/MM/yyyy. 
	 * Caso linha esteja em branco, retorna 01/01/1970 00:00:00.
	 * Caso aconteça algum erro na conversão, retorna null. */
	public static Date readData(String format) {
		String data = scanner.nextLine();

		Date parsedData;
		if (!data.isBlank()) {
			if (format.isBlank()) {
				format = "dd/MM/yyyy";
			}

			SimpleDateFormat formatter = new SimpleDateFormat(format);

			try {
				parsedData = (Date) formatter.parse(data);
			} catch (ParseException pe) {
				parsedData = null;
			}	
		} else {
			parsedData = new Date(0L);
		}

		return parsedData;
	}

	public static int readInt() {
		int i = scanner.nextInt();
		scanner.nextLine(); // limpa buffer
		return i;
	}

	/* Retorna o valor ou NaN caso não tenha sido digitado o float opcional. */
	public static float readFloatOpcional(String mensagem) {
		// Pega-se o valor como se fosse uma String para que seja possível omitir.
		System.out.print(mensagem);
		String valorStr = Utils.scanner.nextLine();

		float valor;
		try {
			valor = Float.parseFloat(valorStr);
		} catch (Exception e) {
			valor = Float.NaN;
		}

		return valor;
	}
	
	public static boolean confirmar(String mensagem) {
		System.out.print(mensagem + " (sim/não): ");
		String confirmacao = scanner.nextLine();

		return (confirmacao.toLowerCase().contains("sim"));
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
			if (clear == null) {
				if (System.getProperty("os.name").contains("Windows")) {
					clear = new String("cls");
				} else {
					// Should work in Linux and MacOS.
					clear = new String("clear");
				}
			}
			new ProcessBuilder(clear).inheritIO().start().waitFor();
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

