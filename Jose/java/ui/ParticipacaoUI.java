package ui;

import java.util.ArrayList;

import entidades.Grupo;
import entidades.Participacao;
import entidades.Usuario;
import entidades.Sugestao;
import infraestrutura.ArvoreBMais_Int_Int;
import infraestrutura.CRUD;
import infraestrutura.Infraestrutura;
import utils.Utils;

public class ParticipacaoUI extends BaseUI {

	private CRUD<Usuario> crudUsuario;
	private CRUD<Grupo> crudGrupo;
	private CRUD<Participacao> crudParticipacoes;
	private CRUD<Sugestao> crudSugestao;

	private ArvoreBMais_Int_Int arvoreUsuarioParticipacao;
	private ArvoreBMais_Int_Int arvoreGrupoParticipacao;
	private ArvoreBMais_Int_Int arvoreUsuarioSugestao;

	public ParticipacaoUI(Infraestrutura infraestrutura) {
		super(infraestrutura);

		crudUsuario = infraestrutura.getCrudUsuario();
		crudGrupo = infraestrutura.getCrudGrupo();
		crudParticipacoes = infraestrutura.getCrudParticipacoes();
		crudSugestao = infraestrutura.getCrudSugestao();

		arvoreUsuarioParticipacao = infraestrutura.getArvoreUsuarioParticipacao();
		arvoreGrupoParticipacao = infraestrutura.getArvoreGrupoParticipacao();
		arvoreUsuarioSugestao = infraestrutura.getArvoreUsuarioSugestao();
	}

	public Resultado telaPrincipalParticipacao(Usuario usuario) {
		Resultado resultado = telaEscolhaGrupos(usuario);

		if (resultado.valido()) {
			Grupo grupoEscolhido = (Grupo) resultado.getObjeto();

			int opcao;
			do {
				Utils.limpaTela();

				Utils.mostrarMensagemResultado(resultado);

				System.out.print(
					"AMIGO OCULTO 1.0\n" +
					"================\n\n" +
					"INÍCIO > GRUPOS > PARTICIPAÇÃO EM GRUPO\n\n"
				);

				grupoEscolhido.fullPrettyPrint();
				System.out.println();

				System.out.print(
					"1) Visualizar participantes\n" +
					"2) Visualizar amigo sorteado\n" +
					"3) Ler/enviar mensagens ao grupo\n\n" +
					"0) Retornar ao menu anterior\n\n" +
					"Opção: "
				);
				opcao = Utils.readInt();

				switch (opcao) {
					case 0:
						resultado.setSucesso("PARTICIPAÇÃO EM GRUPO > GRUPOS");
						break;
					case 1:
						resultado = telaVisualizarParticipantes(usuario, grupoEscolhido);
						break;
					case 2:
						resultado = telaVisualizarAmigoSorteado(usuario, grupoEscolhido);
						break;
					case 3:
						//resultado = telaLerEnviarMensagens(usuario, grupoEscolhido);
					default:
						resultado.setErro("Opção (" + opcao + ") inválida.");
				}
			} while (opcao != 0);
		}

		return resultado;
	}

	private Resultado telaEscolhaGrupos(Usuario usuario) {
		Resultado resultado = new Resultado();

		resultado = infraestrutura.listarRelacao1N(usuario, crudParticipacoes, arvoreUsuarioParticipacao);
		ArrayList<Participacao> participacoes = (ArrayList<Participacao>) resultado.getObjeto();

		if (resultado.valido() && participacoes != null && participacoes.size() != 0) {
			ArrayList<Grupo> grupos = new ArrayList<Grupo>();
			for (Participacao participacao : participacoes) {
				grupos.add(crudGrupo.read(participacao.getIDGrupo()));
			}

			if (grupos.size() != 0) {
				Utils.limpaTela();
				System.out.println("ESCOLHA UM DOS GRUPOS EM QUE VOCÊ PARTICIPA:\n");

				int contador = 1;
				for (Grupo grupo : grupos) {
					// Caso o CRUD não ache o grupo com esse ID, será retornado null.
					if (grupo != null) {
						System.out.println(contador + ".\t" + grupo.getNome());
					}
					contador++;
				}

				System.out.println();
				System.out.print("Grupo (0 para voltar): ");

				int indice = Utils.readInt() - 1;
				try {
					resultado.setObjeto(grupos.get(indice));
				} catch (Exception exception) {
					if (indice == -1) {
						resultado.setErro("Escolha cancelada.");
					} else {
						resultado.setErro("Um erro ocorreu durante a escolha do grupo.");
					}
				}
			}
		} else {
			resultado.setErro("Você não está nenhum grupo.");
		}

		return resultado;
	}

	private Resultado telaVisualizarParticipantes(Usuario usuario, Grupo grupo) {
		Resultado resultado = new Resultado();

		resultado = infraestrutura.listarRelacao1N(grupo, crudParticipacoes, arvoreGrupoParticipacao);
		ArrayList<Participacao> participacoes = (ArrayList<Participacao>) resultado.getObjeto();

		if (resultado.valido() && participacoes != null && participacoes.size() != 0) {
			Utils.limpaTela();
			System.out.println("PARTICIPANTES DE " + grupo.getNome() + "\n");

			for (Participacao participacao : participacoes) {
				Usuario u = crudUsuario.read(participacao.getIDUsuario());
				if (u != null) {
					System.out.println(u.getNome() + " - " + u.getEmail() + ((usuario.getID() == u.getID()) ? " (Você)" : ""));
				}
			}

			System.out.print("Pressione enter para continuar...");
			Utils.scanner.nextLine();
		}

		return resultado;
	}

	private Resultado telaVisualizarAmigoSorteado(Usuario usuario, Grupo grupo) {
		Resultado resultado = new Resultado();

		if (grupo.isSorteado()) {
			Participacao participacao = crudParticipacoes.read(usuario.getID() + "|" + grupo.getID());

			Usuario amigo = crudUsuario.read(participacao.getIDAmigo());

			Utils.limpaTela();
			System.out.println("AMIGO SORTEADO:\n\n");
			System.out.println(amigo.getNome());
			System.out.println("\nSugestões de presente do amigo:");

			resultado = infraestrutura.listarRelacao1N(amigo, crudSugestao, arvoreUsuarioSugestao);
			if (resultado.valido()) {
				ArrayList<Sugestao> sugestoes = (ArrayList<Sugestao>) resultado.getObjeto();

				if (sugestoes.size() != 0) {
					for (Sugestao sugestao : sugestoes) {
						sugestao.prettyPrint();
						System.out.println();
					}
				} else {
					System.out.println(amigo.getNome() + " não cadastrou nenhuma sugestão de presente.\n");
				}
			} else {
				System.out.println("Ocorreu um erro ao buscar as sugestões de " + amigo.getNome());
			}

			System.out.print("Pressione enter para continuar...");
			Utils.scanner.nextLine();
		} else {
			resultado.setErro("O sorteio deste grupo ainda não aconteceu.");
		}

		return resultado;
	}
}
