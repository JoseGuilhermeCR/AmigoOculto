package ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import entidades.Grupo;
import entidades.Participacao;
import entidades.Usuario;
import infraestrutura.ArvoreBMais_Int_Int;
import infraestrutura.CRUD;
import infraestrutura.Infraestrutura;
import utils.Utils;

public class GrupoUI extends BaseUI {

	private CRUD<Grupo> crudGrupo;
	private CRUD<Participacao> crudParticipacoes;
	private CRUD<Usuario> crudUsuarios;

	private ArvoreBMais_Int_Int arvoreUsuarioGrupo;
	private ArvoreBMais_Int_Int arvoreGrupoParticipacao;
	private ArvoreBMais_Int_Int arvoreUsuarioParticipacao;

	private ConviteUI conviteUI;
	private ParticipacaoUI participacaoUI;

	public GrupoUI(Infraestrutura infraestrutura) {
		super(infraestrutura);

		crudGrupo = infraestrutura.getCrudGrupo();
		crudParticipacoes = infraestrutura.getCrudParticipacoes();
		crudUsuarios = infraestrutura.getCrudUsuario();

		arvoreUsuarioGrupo = infraestrutura.getArvoreUsuarioGrupo();
		arvoreGrupoParticipacao = infraestrutura.getArvoreGrupoParticipacao();
		arvoreUsuarioParticipacao = infraestrutura.getArvoreUsuarioParticipacao();

		conviteUI = new ConviteUI(infraestrutura);
		participacaoUI = new ParticipacaoUI(infraestrutura);
	}

	public Resultado telaPrincipalGrupos(Usuario usuario) {
		Resultado resultado = new Resultado();

		int opcao;
		do {
			Utils.limpaTela();

			Utils.mostrarMensagemResultado(resultado);

			System.out.print(
				"AMIGO OCULTO 1.0\n" +
				"================\n\n" +
				"INÍCIO > MENU GRUPOS\n\n" +
				"1) Criação e gerenciamento de grupos\n" +
				"2) Participação nos grupos\n\n" +
				"0) Retornar ao menu anterior\n\n" +
				"Opção: "
			);
			opcao = Utils.readInt();

			switch (opcao) {
				case 0:
					resultado.setSucesso("MENU GRUPOS > INÍCIO");
					break;
				case 1:
					resultado = telaGerenciamentoGrupos(usuario);
					break;
				case 2:
					//resultado = participacaoUI.telaPrincipalParticipacao(usuario);
					break;
				default:
					resultado.setErro("Opção (" + opcao + ") inválida.");
			}
		} while (opcao != 0);

		return resultado;
	}

	private Resultado telaGerenciamentoGrupos(Usuario usuario) {
		Resultado resultado = new Resultado();

		int opcao;
		do {
			Utils.limpaTela();

			Utils.mostrarMensagemResultado(resultado);

			System.out.print(
				"AMIGO OCULTO 1.0\n" +
				"================\n\n" +
				"INÍCIO > MENU GRUPOS > GERENCIAMENTO DE GRUPOS\n\n" +
				"1) Grupos\n" +
				"2) Convites\n" +
				"3) Participantes\n" +
				"4) Sorteio\n\n" +
				"0) Retornar ao menu anterior\n\n" +
				"Opção: "
			);
			opcao = Utils.readInt();

			switch (opcao) {
				case 0:
					resultado.setSucesso("GERENCIAMENTO DE GRUPOS > MENU GRUPOS");
					break;
				case 1:
					resultado = telaGrupos(usuario);
					break;
				case 2:
					resultado = conviteUI.telaPrincipalConvites(usuario);
					break;
				case 3:
					resultado = telaParticipantesGrupo(usuario);
					break;
				default:
					resultado.setErro("Opção (" + opcao + ") inválida.");
			}
		} while (opcao != 0);

		return resultado;
	}

	private Resultado telaGrupos(Usuario usuario) {
		Resultado resultado = new Resultado();

		int opcao;
		do {
			Utils.limpaTela();

			Utils.mostrarMensagemResultado(resultado);

			System.out.print(
				"AMIGO OCULTO 1.0\n" +
				"================\n\n" +
				"INÍCIO > MENU GRUPOS > GERENCIAMENTO DE GRUPOS > GRUPOS\n\n" +
				"1) Listar\n" +
				"2) Incluir\n" +
				"3) Alterar\n" +
				"4) Desativar\n\n" +
				"0) Retornar ao menu anterior\n\n" +
				"Opção: "
			);
			opcao = Utils.readInt();

			switch (opcao) {
				case 0:
					resultado.setSucesso("GRUPOS > GERENCIAMENTO DE GRUPOS");
					break;
				case 1:
					resultado = telaListarGrupos(usuario);
					break;
				case 2:
					resultado = telaIncluirGrupo(usuario);
					break;
				case 3:
					resultado = telaAlterarGrupos(usuario);
					break;
				case 4:
					resultado = telaDesativarGrupos(usuario);
					break;
				default:
					resultado.setErro("Opção (" + opcao + ") inválida.");
			}
		} while (opcao != 0);

		return resultado;
	}

	private Resultado telaListarGrupos(Usuario usuario) {
		Resultado resultado = new Resultado();

		resultado = infraestrutura.listarRelacao1N(usuario, crudGrupo, arvoreUsuarioGrupo);
		ArrayList<Grupo> grupos = GrupoUI.filtrarGruposAtivos((ArrayList<Grupo>) resultado.getObjeto());

		if (resultado.valido() && grupos != null && grupos.size() != 0) {
			Utils.limpaTela();
			System.out.println("MEUS GRUPOS\n");

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

			System.out.println("Pressione enter para continuar...");
			Utils.scanner.nextLine();

			resultado.setObjeto(grupos);
		} else {
			resultado.setErro("Você não tem nenhum grupo.");
		}

		return resultado;
	}

	private Resultado telaIncluirGrupo(Usuario usuario) {
		Resultado resultado = new Resultado();

		Utils.limpaTela();
		System.out.print(
			"INCLUIR GRUPO\n\n" +
			"Nome do grupo: "
		);
		String nomeGrupo = Utils.scanner.nextLine();

		if (!nomeGrupo.isBlank()) {
			System.out.print(
				"Data do sorteio (dd/MM/yyyy HH:mm): "
			);
			Date dataSorteio = Utils.readData("dd/MM/yyyy HH:mm");
			
			// A data deve ser válida e maior que a atual.
			if (dataSorteio != null && dataSorteio.compareTo(new Date()) > 0) {
				float valorMedio = Utils.readFloatOpcional("Valor médio dos presentes (opcional): ");

				System.out.print(
					"Data do encontro (dd/MM/yyyy HH:mm): "
				);
				Date dataEncontro = Utils.readData("dd/MM/yyyy HH:mm");

				// A data deve ser válida e maior que a do sorteio.
				if (dataEncontro != null && dataEncontro.compareTo(dataSorteio) > 0) {
					System.out.print( 
						"Local do encontro (opcional): "
					);
					String localEncontro = Utils.scanner.nextLine();

					System.out.print( 
						"Observações (opcional): "
					);
					String observacoes = Utils.scanner.nextLine();

					if (Utils.confirmar("Completar inclusão?")) {
						int idInserido = crudGrupo.create(new Grupo(usuario.getID(), nomeGrupo, valorMedio,
												dataSorteio.getTime(), dataEncontro.getTime(),
												localEncontro, observacoes));
						if (idInserido != -1) {
							try {
								arvoreUsuarioGrupo.create(usuario.getID(), idInserido);
								resultado.setSucesso("Inclusão realizada com sucesso.");
							} catch (IOException exception) {
								resultado.setErro("Ocorreu um erro durante a inclusão do relacionamento.");
							}
						} else {
							resultado.setErro("Ocorreu um erro durante a inclusão.");
						}
					} else {
						resultado.setSucesso("Inclusão cancelada.");	
					}
				} else {
					resultado.setErro("Data do encontro inválida.");
				}
			} else {
				resultado.setErro("Data do sorteio inválida.");
			}
		} else {
			resultado.setErro("O nome do grupo não pode estar vazio.");
		}

		return resultado;
	}

	private Resultado telaAlterarGrupos(Usuario usuario) {
		Resultado resultado = new Resultado();

		Utils.limpaTela();

		// Primeiro, lista os grupos do usuário.
		resultado = telaListarGrupos(usuario);
		ArrayList<Grupo> grupos = (ArrayList<Grupo>) resultado.getObjeto();

		if (grupos != null && grupos.size() != 0) {
			System.out.print(
				"Quais grupos você quer alterar? (0 para sair ou [1, 2, ...]): "
			);
			String indicesGruposAAlterar[] = Utils.scanner.nextLine().replace(" ", "").split(",");

			for (String str : indicesGruposAAlterar) {
				int indiceGrupo = Integer.parseInt(str) - 1;

				// Se o grupo for válido (estiver na lista apresentada anteriormente).
				if (indiceGrupo >= 0 && indiceGrupo < grupos.size()) {
					Grupo grupo = grupos.get(indiceGrupo);

					if (grupo != null) {
						Utils.limpaTela();
						Utils.mostrarMensagemResultado(resultado);

						System.out.println("ALTERANDO GRUPO " + str + "\n");
						grupo.fullPrettyPrint();
						System.out.println();

						// Lê as alterações dos campos.
						Grupo novoGrupo = lerNovoGrupo(grupo);

						if (novoGrupo != null && !novoGrupo.equals(grupo)) {
							if (Utils.confirmar("Confirmar alteração?")) {
								crudGrupo.update(novoGrupo);
								resultado.setSucesso("Grupo " + novoGrupo.getNome() + " alterado com sucesso!");
							}
						} else {
							resultado.setErro("Algum erro ocorreu na alteração do grupo (" + str + ").");
						}
					}
				}
			}
		}

		return resultado;
	}

	private Resultado telaDesativarGrupos(Usuario usuario) {
		Resultado resultado = new Resultado();

		Utils.limpaTela();

		resultado = telaListarGrupos(usuario);
		// Observe que nesse ponto, os grupos já foram filtrados dentro de telaListarGrupos que colocou a lista no resultado.
		ArrayList<Grupo> grupos = (ArrayList<Grupo>) resultado.getObjeto();

		if (grupos != null && grupos.size() != 0) {
			System.out.print(
					"Quais grupos você quer desativar? (0 para sair ou [1, 2, ...]): "
			);
			String indicesGruposADeletar[] = Utils.scanner.nextLine().replace(" ", "").split(",");
			
			for (String str : indicesGruposADeletar) {
				int indiceGrupo = Integer.parseInt(str) - 1;

				// Se o grupo for válido (estiver na lista apresentada anteriormente).
				if (indiceGrupo >= 0 && indiceGrupo < grupos.size()) {
					Grupo grupo = grupos.get(indiceGrupo);

					if (grupo != null) {
						Utils.limpaTela();
						Utils.mostrarMensagemResultado(resultado);

						System.out.println("DESATIVANDO GRUPO " + str + "\n");
						grupo.prettyPrint();
						System.out.println();

						if (Utils.confirmar("Confirmar?")) {
							grupo.setAtivo(false);
							crudGrupo.update(grupo);
							resultado.setSucesso("Grupo " + grupo.getNome() + " desativado com sucesso!");
						}
					}
				}
			}
		}

		return resultado;
	}

	private Resultado telaParticipantesGrupo(Usuario usuario) {
		Resultado resultado = new Resultado();

		int opcao;
		do {
			Utils.limpaTela();

			Utils.mostrarMensagemResultado(resultado);

			System.out.print(
				"AMIGO OCULTO 1.0\n" +
				"================\n\n" +
				"INÍCIO > MENU GRUPOS > GERENCIAMENTO DE GRUPOS > PARTICIPANTES\n\n" +
				"1) Listagem\n" +
				"2) Remoção\n" +
				"0) Retornar ao menu anterior\n\n" +
				"Opção: "
			);
			opcao = Utils.readInt();

			switch (opcao) {
				case 0:
					resultado.setSucesso("PARTICIPANTES > GERENCIAMENTO DE GRUPOS");
					break;
				case 1:
					resultado = telaListarParticipantesGrupo(usuario);
					break;
				case 2:
					resultado = telaRemoverParticipantesGrupo(usuario);
					break;
				default:
					resultado.setErro("Opção (" + opcao + ") inválida.");
			}
		} while (opcao != 0);

		return resultado;
	}

	private Resultado telaListarParticipantesGrupo(Usuario usuario) {
		Resultado resultado = new Resultado();

		resultado = telaListarGrupos(usuario);
		if (resultado.valido()) {
			ArrayList<Grupo> grupos = (ArrayList<Grupo>) resultado.getObjeto();

			System.out.print("Quer ver os participantes de qual grupo? (0 para voltar): ");
			int opcao = Utils.readInt();

			if (opcao != 0) {
				int indice = opcao - 1;
				if (indice >= 0 && indice < grupos.size()) {
					Grupo grupo = grupos.get(indice);

					// Apresenta informações mais detalhadas do grupo.
					Utils.limpaTela();
					System.out.print("INFORMAÇÕES DO GRUPO\n\n");
					grupo.fullPrettyPrint();
					if (grupo.isSorteado()) {
						System.out.print("\nObserve que o sorteio desse grupo já aconteceu!\n\n");
					} else {
						System.out.print("\nObserve que o sorteio desse grupo ainda não aconteceu!\n\n");
					}

					// Busca os usuários que participam desse grupo.
					resultado = infraestrutura.listarRelacao1N(grupo, crudParticipacoes, arvoreGrupoParticipacao);

					if (resultado.valido()) {
						ArrayList<Participacao> participacoes = (ArrayList<Participacao>) resultado.getObjeto();

						System.out.println("Participantes deste grupo:");
						// Agora usar os ids dos usuários nas participações para recuperar os usuários que estão no grupo
						// e mostrar o nome desses usuários na tela.
						for (Participacao participacao : participacoes) {
							if (participacao != null) {
								Usuario u = crudUsuarios.read(participacao.getIDUsuario());
								if (u != null) {
									System.out.println(u.getNome() + " - " + u.getEmail());
								}	
							}
						}

						System.out.print("\nPressione enter para continuar...");
						Utils.scanner.nextLine();
					} else {
						resultado.setErro("Não foi possível recuperar os participantes deste grupo.");
					}

				} else {
					resultado.setErro("Grupo escolhido não existe.");
				}
			} else {
				resultado.setSucesso("Listagem cancelada");
			}
		}

		return resultado;
	}

	private Resultado telaRemoverParticipantesGrupo(Usuario usuario) {
		Resultado resultado = new Resultado();

		resultado = telaListarGrupos(usuario);
		if (resultado.valido()) {
			ArrayList<Grupo> grupos = (ArrayList<Grupo>) resultado.getObjeto();

			System.out.print("Quer remover participantes de qual grupo? (0 para voltar): ");
			int opcao = Utils.readInt();

			if (opcao != 0) {
				int indice = opcao - 1;
				if (indice >= 0 && indice < grupos.size()) {
					Grupo grupo = grupos.get(indice);

					// Apresenta informações mais detalhadas do grupo.
					Utils.limpaTela();
					System.out.print("INFORMAÇÕES DO GRUPO\n\n");
					grupo.fullPrettyPrint();
					if (grupo.isSorteado()) {
						System.out.print("\nObserve que o sorteio desse grupo já aconteceu!\n\n");
					} else {
						System.out.print("\nObserve que o sorteio desse grupo ainda não aconteceu!\n\n");
					}

					// Busca os usuários que participam desse grupo.
					resultado = infraestrutura.listarRelacao1N(grupo, crudParticipacoes, arvoreGrupoParticipacao);

					if (resultado.valido()) {
						ArrayList<Participacao> participacoes = (ArrayList<Participacao>) resultado.getObjeto();
						HashMap<Integer, Integer> presenteadosPor = new HashMap<>();

						System.out.println("Participantes deste grupo:");
						// Agora usar os ids dos usuários nas participações para recuperar os usuários que estão no grupo
						int contador = 1;
						for (Participacao participacao : participacoes) {
							if (participacao != null) {
								//Mostrar o nome desses usuários na tela.
								Usuario u = crudUsuarios.read(participacao.getIDUsuario());
								if (u != null) {
									System.out.println(contador + "\t" + u.getNome() + " - " + u.getEmail());
								}

								if (grupo.isSorteado()) {
									presenteadosPor.put(participacao.getIDAmigo(), participacao.getID());
								}
							}

							++contador;
						}
						
						System.out.print("\nQual participante você quer remover? (0 para voltar): ");
						opcao = Utils.readInt();

						if (opcao != 0) {
							indice = opcao - 1;
							if (indice >= 0 && indice < participacoes.size()) {
								Participacao participacaoRemovida = participacoes.get(indice);
								Usuario usuarioRemovido = crudUsuarios.read(participacaoRemovida.getIDUsuario());
								
								// É necessário transferir o presente que seria presenteado a esse para outro usuário.
								if (grupo.isSorteado()) {
									int idParticipacaoPresenteariaRemovido = presenteadosPor.get(usuarioRemovido.getID());
									// A participação que presentearia o removido vai presentear quem o removido presentearia.
									Participacao p = crudParticipacoes.read(idParticipacaoPresenteariaRemovido);
									p.setIDAmigo(participacaoRemovida.getIDAmigo());
									crudParticipacoes.update(p);
								}

								crudParticipacoes.delete(participacaoRemovida.getID());
								
								try {
									arvoreGrupoParticipacao.delete(grupo.getID(), participacaoRemovida.getID());
									arvoreUsuarioParticipacao.delete(usuarioRemovido.getID(), participacaoRemovida.getID());
								} catch (IOException exception) {
									resultado.setErro("Um erro ocorreu na remoção do usuário.");
								}
							} else {
								resultado.setErro("Usuário escolhido não existe.");
							}
						} else {
							resultado.setSucesso("Remoção cancelada");
						}
					} else {
						resultado.setErro("Não foi possível recuperar os participantes deste grupo.");
					}
				} else {
					resultado.setErro("Grupo escolhido não existe.");
				}
			} else {
				resultado.setSucesso("Remoção cancelada");
			}
		}

		return resultado;
	}

	private Grupo lerNovoGrupo(Grupo grupo) {
		Grupo novoGrupo = null;

		// Lê as alterações dos campos.
		System.out.println("O campo deixado em branco não será alterado. Datas obrigatórias.");
		System.out.print(
			"Novo nome do grupo: "
		);
		String novoNome = Utils.scanner.nextLine();

		System.out.print(
			"Nova data do sorteio (dd/MM/yyyy HH:mm): "
		);
		Date novaDataSorteio = Utils.readData("dd/MM/yyyy HH:mm");
		
		if (novaDataSorteio != null && novaDataSorteio.compareTo(new Date()) > 0) {
			float novoValorMedio = Utils.readFloatOpcional("Novo valor médio dos presentes: ");

			System.out.print(
				"Nova data do encontro (dd/MM/yyyy HH:mm): "
			);
			Date novaDataEncontro = Utils.readData("dd/MM/yyyy HH:mm");

			if (novaDataEncontro != null && novaDataEncontro.compareTo(novaDataSorteio) > 0) {
				System.out.print( 
					"Novo local do encontro: "
				);
				String novoLocalEncontro = Utils.scanner.nextLine();

				System.out.print( 
					"Novas observações: "
				);
				String novasObservacoes = Utils.scanner.nextLine();

				novoGrupo = new Grupo(
					grupo.getIdUsuario(),
					novoNome.isBlank() ? grupo.getNome() : novoNome,
					Float.isNaN(novoValorMedio) ? grupo.getValor() : novoValorMedio,
					novaDataSorteio.getTime(),
					novaDataEncontro.getTime(),
					novoLocalEncontro.isBlank() ? grupo.getLocalEncontro() : novoLocalEncontro,
					novasObservacoes.isBlank() ? grupo.getObservacoes() : novasObservacoes
				);
				novoGrupo.setID(grupo.getID());
			}
		}

		return novoGrupo;
	}
	
	public static boolean contemGrupoAtivo(ArrayList<Grupo> grupos) {
		boolean resp = false;

		int i = 0;
		while (i < grupos.size() && !grupos.get(i).isAtivo())
			++i;

		if (i < grupos.size())
			resp = true;

		return resp;
	}

	public static ArrayList<Grupo> filtrarGruposAtivos(ArrayList<Grupo> grupos) {
		ArrayList<Grupo> filtrados = null;

		if (grupos != null) {
			filtrados = new ArrayList<>();

			for (Grupo grupo : grupos) {
				if (grupo != null && grupo.isAtivo())
					filtrados.add(grupo);
			}
		}

		return filtrados;
	}

	public static ArrayList<Grupo> filtrarGruposNaoSorteados(ArrayList<Grupo> grupos) {
		ArrayList<Grupo> filtrados = null;

		if (grupos != null) {
			long agora = new Date().getTime();

			filtrados = new ArrayList<>();

			for (Grupo grupo : grupos) {
				if (grupo != null && agora < grupo.getMomentoSorteio())
					filtrados.add(grupo);
			}
		}

		return filtrados;
	}
}

