/* José Guilherme de Castro Rodrigues 2020 */

package ui;

import java.io.IOException;

import utils.Utils;
import entidades.Usuario;
import infraestrutura.Infraestrutura;
import infraestrutura.ArvoreBMais_ChaveComposta_String_Int;

public class MenuUI extends BaseUI {

	private SugestaoUI sugestaoUI;
	private GrupoUI grupoUI;
	private NovosConvitesUI novosConvitesUI;

	public MenuUI(Infraestrutura infraestrutura) {
		super(infraestrutura);

		sugestaoUI = new SugestaoUI(infraestrutura);
		grupoUI = new GrupoUI(infraestrutura);
		novosConvitesUI = new NovosConvitesUI(infraestrutura);
	}

	public Resultado telaMenuPrincipal(Usuario usuario) {
		Resultado resultado = new Resultado();

		resultado.setSucesso("Bem-vindo " + usuario.getNome() + "!");

		int opcao = 0;
		do {
			Utils.limpaTela();
			Utils.mostrarMensagemResultado(resultado);

			// Pega o número de convites pendentes desse usuário.
			int convites;
			try {
				convites = infraestrutura.getListaInvertidaConvitesPendentes()
							.read(usuario.getEmail()).length;
			} catch (IOException exception) {
				convites = 0;
			}

			System.out.print(
				"AMIGO OCULTO 1.0\n" +
				"================\n\n" +
				"INÍCIO\n\n" +
				"1) Sugestões de presentes\n" +
				"2) Grupos\n" +
				"3) Novos convites: " + convites + "\n\n" +
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
					resultado = grupoUI.telaPrincipalGrupos(usuario);
					break;
				case 3:
					if (convites != 0)
						resultado = novosConvitesUI.telaNovosConvites(usuario);
					else
						resultado.setErro("Você não tem convites pendentes.");
					break;
				default:
					resultado.setErro("Opção (" + opcao + ") inválida.");
			}
		} while (opcao != 0);

		return resultado;
	}
}
