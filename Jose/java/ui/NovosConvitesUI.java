package ui;

import java.util.ArrayList;
import java.io.IOException;

import utils.Utils;
import infraestrutura.*;
import entidades.Usuario;
import entidades.Grupo;
import entidades.Convite;
import entidades.Participacao;

public class NovosConvitesUI extends BaseUI {
	
	private CRUD<Usuario> crudUsuarios;
	private CRUD<Grupo> crudGrupos;
	private CRUD<Convite> crudConvites;
	private CRUD<Participacao> crudParticipacoes;

	private ArvoreBMais_ChaveComposta_String_Int listaConvitesPendentes;
	private ArvoreBMais_Int_Int arvoreGrupoParticipacao;
	private ArvoreBMais_Int_Int arvoreUsuarioParticipacao;

	public NovosConvitesUI(Infraestrutura infraestrutura) {
		super(infraestrutura);

		crudUsuarios = infraestrutura.getCrudUsuario();
		crudGrupos = infraestrutura.getCrudGrupo();
		crudConvites = infraestrutura.getCrudConvite();
		crudParticipacoes = infraestrutura.getCrudParticipacoes();

		listaConvitesPendentes = infraestrutura.getListaInvertidaConvitesPendentes();
		arvoreGrupoParticipacao = infraestrutura.getArvoreGrupoParticipacao();
		arvoreUsuarioParticipacao = infraestrutura.getArvoreUsuarioParticipacao();
	}

	public Resultado telaNovosConvites(Usuario usuario) {
		Resultado resultado = new Resultado();

		Utils.limpaTela();
		System.out.print(
				"VOCÊ FOI CONVIDADO PARA PARTICIPAR DOS GRUPOS ABAIXO.\n" +
				"ESCOLHA QUAIS CONVITES DESEJA ACEITAR OU RECUSAR:\n\n"
		);

		// Lê todos os convites pendentes do usuário.
		int idConvites[];
		try {
			idConvites = listaConvitesPendentes.read(usuario.getEmail());
		} catch (IOException exception) {
			idConvites = null;
		}

		if (idConvites != null) {
			ArrayList<Convite> convites = new ArrayList<>();
			ArrayList<String> mensagens = new ArrayList<String>();

			// Para cada id de convite.
			int contador = 1;
			for (int id : idConvites) {
				Convite convite = crudConvites.read(id);
				Grupo grupo = crudGrupos.read(convite.getIdGrupo());
				Usuario adminGrupo = crudUsuarios.read(grupo.getIdUsuario());

				if (convite.getEstado() == 0) {
					String mensagem = contador + ".\t" + grupo.getNome() + "\n" +
							  "\tConvidado em: " + convite.getMomentoConviteFormatado() + "\n" +
							  "\tpor " + adminGrupo.getNome() + "\n";

					// Escreve convite na tela.
					System.out.println(mensagem);

					++contador;
					convites.add(convite);
					mensagens.add(mensagem);
				} else {
					// Como esse convite veio parar aqui? Tira da lista.
					try {
						listaConvitesPendentes.delete(usuario.getEmail(), convite.getID());
					} catch (IOException exception) {
						resultado.setErro("Erro ao retirar convite não pendente da lista.");
					}
				}

			}

			System.out.print(
					"Quais convites quer aceitar ou recusar? (0 para sair ou [1, 2, ...]): "
			);
			String indices[] = Utils.scanner.nextLine().replace(" ", "").split(",");

			for (String str : indices) {
				int indice = Integer.parseInt(str) - 1;

				if (indice >= 0 && indice < convites.size()) {
					Convite convite = convites.get(indice);

					Utils.limpaTela();
					Utils.mostrarMensagemResultado(resultado);

					System.out.println("CONVITE PENDENTE " + str);
					System.out.println(mensagens.get(indice));

					String opcao;
					do{
						System.out.print("Aceitar, recusar ou não fazer nada? (A/R/N): ");
						opcao = Utils.scanner.nextLine().toLowerCase();
					} while (!(opcao.contains("a") || opcao.contains("r") || opcao.contains("n"))); 

					if (!opcao.contains("n")) {
						if (opcao.contains("a")) {
							convite.setEstado((byte)1);

							// Inclui entidade participacao que conecta usuário com grupo e a pessoa que será presenteada pelo usuário.
							int idParticipacao = crudParticipacoes.create(new Participacao(usuario.getID(), convite.getIdGrupo(), 0));

							if (idParticipacao != -1) {
								// Também inclui nas árvores de relacionamento usuarioParticipacao e grupoParticipacao.
								try {
										arvoreGrupoParticipacao.create(convite.getIdGrupo(), idParticipacao);
										arvoreUsuarioParticipacao.create(usuario.getID(), idParticipacao);
								} catch (IOException exception) {
									resultado.setErro("Erro ao aceitar convite.");
								}
							}

						} else if (opcao.contains("r")) {
							convite.setEstado((byte)2);
						}

						if (resultado.valido()) {
							crudConvites.update(convite);
						
							try {
								listaConvitesPendentes.delete(usuario.getEmail(), convite.getID());
								resultado.setSucesso("Alteração concluída.");
							} catch (IOException exception) {
								resultado.setErro("Erro ao retirar convite pendente.");
							}
						}
					}
				} else {
					resultado.setErro("Um ou mais convites especificados não eram válidos.");
				}
			}	
		} else {
			resultado.setErro("Erro ao ler seus convites pendentes!");
		}

		return resultado;
	}
}
