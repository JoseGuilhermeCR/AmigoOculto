package ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import entidades.Grupo;
import entidades.Usuario;
import infraestrutura.ArvoreBMais_Int_Int;
import infraestrutura.CRUD;
import infraestrutura.Infraestrutura;
import utils.Utils;

public class GrupoUI extends BaseUI {

	private CRUD<Grupo> crudGrupo;

	private ArvoreBMais_Int_Int arvoreUsuarioGrupo;

	public GrupoUI(Infraestrutura infraestrutura) {
		super(infraestrutura);

		crudGrupo = infraestrutura.getCrudGrupo();
		arvoreUsuarioGrupo = infraestrutura.getArvoreUsuarioGrupo();
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
					resultado = telaParticipacaoGrupos(usuario);
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
					//resultado = telaParticipacaoGrupos(usuario);
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

	private Resultado telaParticipacaoGrupos(Usuario usuario) {
		Resultado resultado = new Resultado();

		

		return resultado;
	}

	private Resultado telaListarGrupos(Usuario usuario) {
		Resultado resultado = new Resultado();

		resultado = infraestrutura.listarRelacao1N(usuario, crudGrupo, arvoreUsuarioGrupo);
		ArrayList<Grupo> grupos = filtrarGruposAtivos((ArrayList<Grupo>) resultado.getObjeto());

		if (resultado.valido() && grupos != null && contemGrupoAtivo(grupos)) {
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

			System.out.println("Pressione qualquer tecla para continuar...");
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

						System.out.println("DESATIVANDO GRUPO " + str + "\n");
						grupo.prettyPrint();
						System.out.println();

						if (Utils.confirmar("Confirmar?")) {
							grupo.setAtivo(false);
							crudGrupo.update(grupo);
						}
					}
				}
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
	
	private boolean contemGrupoAtivo(ArrayList<Grupo> grupos) {
		boolean resp = false;

		int i = 0;
		while (i < grupos.size() && !grupos.get(i).isAtivo())
			++i;

		if (i < grupos.size())
			resp = true;

		return resp;
	}

	private ArrayList<Grupo> filtrarGruposAtivos(ArrayList<Grupo> grupos) {
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
}