/* José Guilherme de Castro Rodrigues 2020 */

package ui;

import utils.Utils;
import entidades.Usuario;
import infraestrutura.Infraestrutura;

public class MenuUI {

	private SugestaoUI sugestaoUI;

	public MenuUI(Infraestrutura infraestrutura) {
		sugestaoUI = new SugestaoUI(infraestrutura);
	}

	public Resultado telaMenuPrincipal(Usuario usuario) {
		Resultado resultado = new Resultado();

		// Talvez não seja uma boa ideia fazer esse tipo de uso para o resultado...
		// Não foi pensado para se usar dessa maneira.
		resultado.setSucesso("Bem-vindo " + usuario.getNome() + "!");

		int opcao;
		do {
			Utils.limpaTela();

			Utils.mostrarMensagemResultado(resultado);

			System.out.print(
				"AMIGO OCULTO 1.0\n" +
				"================\n\n" +
				"INÍCIO\n\n" +
				"1) Sugestões de presentes\n" +
				"2) Grupos\n" +
				"3) Novos convites: 0\n\n" +
				"0) Sair\n\n" +
				"Opcão: "
			);
			opcao = Utils.readInt();

			switch (opcao) {
				case 0:
					resultado.setSucesso("Até mais " + usuario.getNome() + "!");
					break;
				case 1:
					resultado = sugestaoUI.telaSugestoes(usuario);
					break;
				case 2:
					break;
				case 3:
					break;
				default:
					resultado.setErro("Opção (" + opcao + ") inválida.");
			}
		} while (opcao != 0);

		return resultado;
	}
}