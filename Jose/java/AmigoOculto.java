/* José Guilherme de Castro Rodrigues 2020 */

import infraestrutura.Infraestrutura;
import ui.Resultado;
import ui.AcessoUI;

// TODO: Por enquanto, acentos apresentam problema no Windows.
// Quando compilar, é preciso usar -encoding utf-8.
// Quando executar, os caracteres apresentam problema mesmo com chcp 65001 no terminal.
// chcp 1252 resolve o problema, mas não é o ideal.

public class AmigoOculto {
	private Infraestrutura infraestrutura;

	private AcessoUI acessoUI;

	public AmigoOculto() throws Exception {
		infraestrutura = new Infraestrutura();

		acessoUI = new AcessoUI(infraestrutura);
	}

	public Resultado run() {
		return acessoUI.telaAcesso();
	}

	public static void main(String[] args) {
		try {
			new AmigoOculto().run();
		} catch (Exception exception) {
			System.err.println("Um erro ocorreu durante a inicialização do programa.");
		}
	}
}
