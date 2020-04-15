package ui;

import java.util.ArrayList;
import java.io.IOException;

import entidades.Grupo;
import entidades.Usuario;
import entidades.Convite;
import utils.Utils;
import infraestrutura.*;

public class ConviteUI extends BaseUI {

	private CRUD<Grupo> crudGrupo;
	private CRUD<Convite> crudConvite;

	private ArvoreBMais_Int_Int arvoreUsuarioGrupo;
	private ArvoreBMais_Int_Int arvoreGrupoConvite;
	private ArvoreBMais_ChaveComposta_String_Int listaConvitesPendentes;

	public ConviteUI(Infraestrutura infraestrutura) {
		super(infraestrutura);

		crudGrupo = infraestrutura.getCrudGrupo();
		crudConvite = infraestrutura.getCrudConvite();

		arvoreUsuarioGrupo = infraestrutura.getArvoreUsuarioGrupo();
		arvoreGrupoConvite = infraestrutura.getArvoreGrupoConvite();

		listaConvitesPendentes = infraestrutura.getListaInvertidaConvitesPendentes();
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
					resultado = telaCancelarConvites(usuario);
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

					int contador = 1;
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
					String chaveSecundaria = grupoEscolhido.getID() + "|" + email;

					Convite convite = crudConvite.read(chaveSecundaria);
					if (convite != null) {
						// Se pendente ou aceito.
						if (convite.getEstado() == 0 || convite.getEstado() == 1) {
							resultado.setSucesso("Convite já emitido para esse email.");
						} else {
							// Recusado ou cancelado.
							if (Utils.confirmar("O convite foi " + ((convite.getEstado() == 2) ? "recusado" : "cancelado" ) + ". Quer reemitir esse convite?")) {
								convite.setEstado((byte)0);	// Volta estado para pendente.
								crudConvite.update(convite);
								try {
									listaConvitesPendentes.create(email, convite.getID());
									resultado.setSucesso("Convite emitido novamente.");
								} catch (IOException exception) {
									resultado.setErro("Ocorreu um erro durante a inclusão.");
								}
							} else {
								resultado.setSucesso("Convite não reemitido.");
							}
						}
					} else {
						// Convite não existe, emitir!
						int idInserido = crudConvite.create(new Convite(grupoEscolhido.getID(), email));
						if (idInserido != -1) {
							try {
								// Inserir na lista invertida de convites pendentes.
								listaConvitesPendentes.create(email, idInserido);
								// Inserir na árvore de relação.
								arvoreGrupoConvite.create(grupoEscolhido.getID(), idInserido);
								resultado.setSucesso("Convite para " + email + " emitido com sucesso.");
							} catch (IOException exception) {
								resultado.setErro("Ocorreu um erro durante a inclusão.");
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
				resultado.setErro("Não foi possível continuar o processo de emissão de convites.");
			}

			return resultado;
		}

		private Resultado telaCancelarConvites(Usuario usuario) {
			Resultado resultado = telaListarConvites(usuario, true);

			if (resultado.valido()) {
				// Grupo que foi escolhido está no resultado.
				Grupo grupoEscolhido = (Grupo) resultado.getObjeto();

				// Pega todos os convites relacionados a esse grupo.
				resultado = infraestrutura.listarRelacao1N(grupoEscolhido, crudConvite, arvoreGrupoConvite);
				ArrayList<Convite> convites = (ArrayList<Convite>) resultado.getObjeto();

				if (resultado.valido() && convites != null) {
					System.out.print(
							"Quais convites você quer cancelar? (0 para sair ou [1, 2, ...]): "
					);
					String indicesConvitesACancelar[] = Utils.scanner.nextLine().replace(" ", "").split(",");

					for (String str : indicesConvitesACancelar) {
						int indiceConvite = Integer.parseInt(str) - 1;

						// Se o convit for válido (estiver na lista apresentada anteriormente).
						if (indiceConvite >= 0 && indiceConvite < convites.size()) {
							Convite convite = convites.get(indiceConvite);
							if (convite != null) {
								Utils.limpaTela();
								Utils.mostrarMensagemResultado(resultado);

								System.out.print("CANCELANDO CONVITE " + str + "\n");
								convite.prettyPrint();
								System.out.println();

								if (Utils.confirmar("Confirmar?")) {
									convite.setEstado((byte)3);	// Cancelado.
									crudConvite.update(convite);

									String email = convite.getEmailUsuario();
									try {
										// Remover da lista invertida de convites pendentes.
										listaConvitesPendentes.delete(email, convite.getID());
										resultado.setSucesso("Convite para " + email + " cancelado com sucesso.");
									} catch (IOException exception) {
										resultado.setErro("Ocorreu um erro durante o cancelamento do convite para " + email);
									}
								}
							}
						}
					}
				}
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
