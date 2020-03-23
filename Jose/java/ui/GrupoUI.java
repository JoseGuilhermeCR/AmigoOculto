package ui;

import java.io.IOException;
import java.util.Date;

import entidades.Grupo;
import entidades.Usuario;
import infraestrutura.ArvoreBMais_Int_Int;
import infraestrutura.CRUD;
import infraestrutura.Infraestrutura;
import utils.Utils;

public class GrupoUI {

	private CRUD<Grupo> crudGrupo;

	private ArvoreBMais_Int_Int arvoreUsuarioGrupo;

	public GrupoUI(Infraestrutura infraestrutura) {
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

		resultado = listarGrupos(usuario);
		Grupo grupos[] = (Grupo[]) resultado.getObjeto();
		
		if (resultado.valido() && grupos != null && grupos.length != 0) {
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

			if (dataSorteio != null && dataSorteio.compareTo(new Date()) > 0) {
				float valorMedio = Utils.readFloatOpcional("Valor médio dos presentes (opcional): ");

				System.out.print(
					"Data do encontro (dd/MM/yyyy HH:mm): "
				);
				Date dataEncontro = Utils.readData("dd/MM/yyyy HH:mm");

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
							} catch (IOException exception) {
								resultado.setErro("Ocorreu um erro durante a inclusão.");
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

	// TODO: Talvez transformar em uma função genérica. (Duplicado de SugestaoUI.java)
	private Resultado listarGrupos(Usuario usuario) {
		Resultado resultado = new Resultado();

		try {
			// Lê todas sugestões desse usuário.
			int idsGrupos[] = arvoreUsuarioGrupo.read(usuario.getID());
			Grupo sugestoes[] = new Grupo[idsGrupos.length];

			// As lê e coloca no vetor de sugestões.
			int contador = 0;
			for (int id : idsGrupos) {
				sugestoes[contador++] = crudGrupo.read(id);
			}

			resultado.setObjeto(sugestoes);
		} catch (Exception exception) {
			resultado.setErro("Ocorreu um erro ao tentar ler as suas sugestões.");
		}

		return resultado;
	}
}