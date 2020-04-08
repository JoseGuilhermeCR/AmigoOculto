package ui;

import java.util.ArrayList;
import java.io.IOException;

import entidades.Grupo;
import entidades.Usuario;
import entidades.Convite;
import infraestrutura.ArvoreBMais_Int_Int;
import infraestrutura.CRUD;
import infraestrutura.Infraestrutura;
import utils.Utils;

public class ConviteUI extends BaseUI {

	private CRUD<Grupo> crudGrupo;
	private CRUD<Convite> crudConvite;

	private ArvoreBMais_Int_Int arvoreUsuarioGrupo;
	private ArvoreBMais_Int_Int arvoreGrupoConvite;

	public ConviteUI(Infraestrutura infraestrutura) {
		super(infraestrutura);

		crudGrupo = infraestrutura.getCrudGrupo();
		crudConvite = infraestrutura.getCrudConvite();

		arvoreUsuarioGrupo = infraestrutura.getArvoreUsuarioGrupo();
		arvoreGrupoConvite = infraestrutura.getArvoreGrupoConvite();
	}

	public Resultado telaPrincipalConvites(Usuario usuario) {
		Resultado resultado = new Resultado();

		int opcao;
		do {
			Utils.limpaTela();

			Utils.mostrarMensagemResultado(resultado);

			System.out.print(
					"AMIGO OCULTO 1.0\n" +
					"================\n\n" +
					"INÍCIO > GRUPOS > GERENCIAMENTO DE GRUPOS > CONVITES\n\n" +
					"1) Listagem dos convites\n" +
					"2) Emissão de convites\n" +
					"3) Cancelamento de convites\n\n" +
					"0) Retornar ao menu anterior\n\n" +
					"Opção: "
			);
			opcao = Utils.readInt();

			switch (opcao) {
				case 0:
					resultado.setSucesso("CONVITES > GERENCIAMENTO DE GRUPOS");
					break;
				case 1:
					resultado = telaListarConvites(usuario, false);
					break;
				case 2:
					resultado = telaEmitirConvites(usuario);
					break;
				case 3:
					break;
				default:
					resultado.setErro("Opção (" + opcao + ") inválida.");
			}
		} while (opcao != 0);

		return resultado;
	}

	private Resultado telaListarConvites(Usuario usuario, boolean antesSorteio) {
		// Mostramos grupos para usuário escolher de qual grupo quer ver os convites.
		Resultado resultado = escolherGrupoDeLista(usuario, antesSorteio);

		if (resultado.valido()) {
			Grupo grupoEscolhido = (Grupo) resultado.getObjeto();
			
			resultado = infraestrutura.listarRelacao1N(grupoEscolhido, crudConvite, arvoreGrupoConvite);

			ArrayList<Convite> convites = (ArrayList<Convite>) resultado.getObjeto();

			if (resultado.valido() && convites != null) {
				Utils.limpaTela();
				System.out.println("CONVITES DO GRUPO \"" + grupoEscolhido.getNome() + "\"\n");

				int contador = -1;
				for (Convite convite : convites) {
					if (convite != null) {
						System.out.print(contador + ".");
						convite.prettyPrint();
						System.out.println();
					}
					contador++;
				}

				System.out.println("Pressione qualquer tecla para continuar...");
				Utils.scanner.nextLine();

				resultado.setObjeto(grupoEscolhido);
			} else {
				resultado.setErro("Não há convites nesse grupo.");
			}
		}

		return resultado;
	}

	private Resultado telaEmitirConvites(Usuario usuario) {
		Resultado resultado = telaListarConvites(usuario, true);
		
		if (resultado.valido()) {
			Grupo grupoEscolhido = (Grupo) resultado.getObjeto();

			Utils.limpaTela();
			System.out.println("EMITINDO CONVITES PARA \"" + grupoEscolhido.getNome() + "\"\n\n");

			System.out.print("Email do usuário (Vazio para cancelar): ");
			String email = Utils.scanner.nextLine();
			while (!email.isBlank()) {
				String chaveSecundaria = grupoEscolhido.getID() + "|" + usuario.getEmail();

				Convite convite = crudConvite.read(chaveSecundaria);
				if (convite != null) {
					// Se pendente ou aceito.
					if (convite.getEstado() == 0 || convite.getEstado() == 1) {
						resultado.setSucesso("Convite já emitido para esse email.");
					} else {
						// Recusado ou cancelado.
						if (Utils.confirmar("Quer reemitir esse convite?")) {
							convite.setEstado((byte)0);	// Volta estado para pendente.
							crudConvite.update(convite);
							// TODO: Necessário colocar convite na lista invertida novamente?
						} else {
							resultado.setSucesso("Convite não reemitido.");
						}
					}
				} else {
					// Convite não existe, emitir!
					int idInserido = crudConvite.create(new Convite(grupoEscolhido.getID(), usuario.getEmail()));
					if (idInserido != -1) {
						// TODO: Inserir par email e id do novo convite na lista invertida de convites pendentes.
						
						// Inserir na árvore de relação.
						try {
							arvoreGrupoConvite.create(grupoEscolhido.getID(), idInserido);
							resultado.setSucesso("Convite emitido com sucesso.");
						} catch (IOException exception) {
							resultado.setErro("Ocorreu um erro durante a inclusão do relacionamento.");
						}
					} else {
						resultado.setErro("Erro ao criar convite.");
					}
				}

				Utils.limpaTela();
				Utils.mostrarMensagemResultado(resultado);
				System.out.println("EMITINDO CONVITES PARA \"" + grupoEscolhido.getNome() + "\"\n\n");
	
				System.out.print("Email do usuário (Vazio para cancelar): ");
				email = Utils.scanner.nextLine();
			}
		} else {
			resultado.setErro("Não há grupos em que seja possível emitir convites.");
		}

		return resultado;
	}

	private Resultado escolherGrupoDeLista(Usuario usuario, boolean antesSorteio) {
		Resultado resultado = infraestrutura.listarRelacao1N(usuario, crudGrupo, arvoreUsuarioGrupo);
		ArrayList<Grupo> grupos = GrupoUI.filtrarGruposAtivos((ArrayList<Grupo>) resultado.getObjeto());
		
		if (antesSorteio)
			grupos = GrupoUI.filtrarGruposNaoSorteados(grupos);

		if (resultado.valido() && grupos != null && GrupoUI.contemGrupoAtivo(grupos)) {
			Utils.limpaTela();
			System.out.println("ESCOLHA O GRUPO:\n\n");

			int contador = 1;
			for (Grupo grupo : grupos) {
				// Caso o CRUD não ache o grupo com esse ID, será retornado null.
				if (grupo != null) {
					System.out.print(contador + ".");
					grupo.prettyPrint();
					System.out.println();
				}
				contador++;
			}

			System.out.print("Grupo: ");

			try {
				resultado.setObjeto(grupos.get(Utils.readInt() - 1));
			} catch (IndexOutOfBoundsException exception) {
				resultado.setErro("Você não selecionou um grupo válido.");
			}
		} else {
			resultado.setErro("Você não tem nenhum grupo.");
		}

		return resultado;
	}
}
