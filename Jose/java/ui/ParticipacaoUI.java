package ui;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import entidades.Grupo;
import entidades.Participacao;
import entidades.Usuario;
import entidades.Sugestao;
import entidades.Mensagem;
import infraestrutura.ArvoreBMais_Int_Int;
import infraestrutura.CRUD;
import infraestrutura.Infraestrutura;
import utils.Utils;

public class ParticipacaoUI extends BaseUI {

	private CRUD<Usuario> crudUsuario;
	private CRUD<Grupo> crudGrupo;
	private CRUD<Participacao> crudParticipacoes;
	private CRUD<Sugestao> crudSugestao;
	private CRUD<Mensagem> crudMensagem;

	private ArvoreBMais_Int_Int arvoreUsuarioParticipacao;
	private ArvoreBMais_Int_Int arvoreGrupoParticipacao;
	private ArvoreBMais_Int_Int arvoreUsuarioSugestao;
	private ArvoreBMais_Int_Int arvoreGrupoMensagem;

	public ParticipacaoUI(Infraestrutura infraestrutura) {
		super(infraestrutura);

		crudUsuario = infraestrutura.getCrudUsuario();
		crudGrupo = infraestrutura.getCrudGrupo();
		crudParticipacoes = infraestrutura.getCrudParticipacoes();
		crudSugestao = infraestrutura.getCrudSugestao();
		crudMensagem = infraestrutura.getCrudMensagem();

		arvoreUsuarioParticipacao = infraestrutura.getArvoreUsuarioParticipacao();
		arvoreGrupoParticipacao = infraestrutura.getArvoreGrupoParticipacao();
		arvoreUsuarioSugestao = infraestrutura.getArvoreUsuarioSugestao();
		arvoreGrupoMensagem = infraestrutura.getArvoreGrupoMensagem();
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
						resultado = telaLerEnviarMensagens(usuario, grupoEscolhido);
						break;
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

	private Resultado telaLerEnviarMensagens(Usuario usuario, Grupo grupo) {
		Resultado resultado = new Resultado();

		int opcao;
		do {
			Utils.limpaTela();

			Utils.mostrarMensagemResultado(resultado);

			System.out.print(
				"AMIGO OCULTO 1.0\n" +
				"================\n\n" +
				"INÍCIO > GRUPOS > PARTICIPAÇÃO EM GRUPO > LEITURA E ENVIO DE MENSAGENS\n\n" +
				"1) Enviar mensagem\n" +
				"2) Ler mensagens\n\n" +
				"0) Retornar ao menu anterior\n\n" +
				"Opção: "
			);
			opcao = Utils.readInt();

			switch (opcao) {
				case 0:
					resultado.setSucesso("LEITURA E ENVIO DE MENSAGENS > PARTICIPAÇÃO EM GRUPO");
					break;
				case 1:
					resultado = telaEnviarMensagem(usuario, grupo);
					break;
				case 2:
					resultado = telaLerMensagens(usuario, grupo);
					break;
				default:
					resultado.setErro("Opção (" + opcao + ") inválida.");
			}
		} while (opcao != 0);

		return resultado;
	}

	private Resultado telaEnviarMensagem(Usuario usuario, Grupo grupo) {
		Resultado resultado = new Resultado();

		Utils.limpaTela();
		System.out.println("ENVIAR MENSAGEM\n");

		System.out.print("Título: ");
		String titulo = Utils.scanner.nextLine();
		if (!titulo.isBlank()) {
			System.out.print("Conteúdo (enter em linha vazia para terminar):\n");
			String conteudo = new String();

			String linha = Utils.scanner.nextLine();
			while (!linha.isBlank()) {
				conteudo += linha + "\n";
				linha = Utils.scanner.nextLine();
			}
			System.out.println();

			if (!conteudo.isBlank()) {
				if (Utils.confirmar("Confirmar envio da mensagem?")) {
					int idInserido = crudMensagem.create(new Mensagem(grupo.getID(), usuario.getID(), titulo, conteudo));
					if (idInserido != -1) {
						try {
							arvoreGrupoMensagem.create(grupo.getID(), idInserido);
							resultado.setSucesso("Mensagem enviada com sucesso.");
						} catch(IOException exception) {
							resultado.setErro("Mensagem enviada, mas com erros.");
						}
					} else {
						resultado.setErro("Aconteceu um erro no envio da mensagem.");
					}
				} else {
					resultado.setSucesso("Envio cancelado.");
				}
			} else {
				resultado.setErro("Conteúdo não pode estar vazio.");
			}
		} else {
			resultado.setErro("Título não pode estar vazio.");
		}

		return resultado;
	}

	private Resultado telaLerMensagens(Usuario usuario, Grupo grupo) {
		Resultado resultado = new Resultado();

		resultado = infraestrutura.listarRelacao1N(grupo, crudMensagem, arvoreGrupoMensagem);
		if (resultado.valido()) {
			ArrayList<Mensagem> mensagens = (ArrayList<Mensagem>) resultado.getObjeto();
			HashMap<Integer, Usuario> remetentes = new HashMap<Integer, Usuario>();

			// Como elas estão ordenadas no arquivo da mais antiga para mais nova, só invertemos a lista.
			Collections.reverse(mensagens);

			SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");

			int numPaginas = (int)Math.ceil((double)mensagens.size() / 5);
			int i = 0;
			// Cada iteração do while é uma página com até 5 mensagens.
			while (i < mensagens.size()) {
				int pagina = (int)Math.ceil((double)(i + 1) / 5);

				Utils.limpaTela();
				Utils.mostrarMensagemResultado(resultado);
				System.out.println("PÁGINA " + pagina + " DE " + numPaginas + "\n");

				// Para cada mensagem.
				for (int msg = 0; msg < 5 && i + msg < mensagens.size(); ++msg) {
					Mensagem mensagem = mensagens.get(i + msg);

					// Coloca usuário no "cache" de remetentes caso ele já não esteja nele.
					int idUsuario = mensagem.getIdUsuario();
					if (!remetentes.containsKey(idUsuario)) {
						remetentes.put(idUsuario, crudUsuario.read(idUsuario));
					}

					Usuario remetente = remetentes.get(idUsuario);
					System.out.println(
						"Remetente: " + remetente.getNome() + " - " + remetente.getEmail() +
						((remetente.getID() == usuario.getID()) ? " (Você)" : "") + 
						" Enviado em: " + dateFormatter.format(new Date(mensagem.getMomentoEnvio())) + "\n"
					);
					System.out.println("\t" + mensagem.getTitulo());
					System.out.println(mensagem.getConteudo());
					System.out.println("=======================================================================================");
				}

				System.out.print("Avançar página, voltar página ou sair? (a/v/s): ");
				String resposta = Utils.scanner.nextLine().toLowerCase();
				if (resposta.contains("a")) {
					if (pagina != numPaginas) {
						i += 5;
						resultado.setSucesso("");
					} else {
						resultado.setErro("Essa é a última página.");
					}
				} else if (resposta.contains("v")) {
					if (pagina != 1) {
						i -= 5;
						resultado.setSucesso("");
					} else {
						resultado.setErro("Essa é a primeira página!");
					}
				} else if (resposta.contains("s")) {
					// Causa o fim do while.
					i = mensagens.size();
					resultado.setSucesso("LER MENSAGENS > LEITURA E ENVIO DE MENSAGENS");
				} else {
					resultado.setErro("Opção selecionada não é válida.");
				}
			}
		}

		return resultado;
	}
}
