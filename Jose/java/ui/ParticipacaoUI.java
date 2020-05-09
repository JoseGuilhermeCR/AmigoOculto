package ui;

import java.io.IOException;
import java.util.ArrayList;

import entidades.Grupo;
import entidades.Participacao;
import entidades.Usuario;
import infraestrutura.ArvoreBMais_Int_Int;
import infraestrutura.CRUD;
import infraestrutura.Infraestrutura;
import utils.Utils;

public class ParticipacaoUI extends BaseUI {

	private CRUD<Grupo> crudGrupo;
	private CRUD<Participacao> crudParticipacoes;
	private ArvoreBMais_Int_Int arvoreUsuarioParticipacao;

	public ParticipacaoUI(Infraestrutura infraestrutura) {
		super(infraestrutura);

		crudGrupo = infraestrutura.getCrudGrupo();
		crudParticipacoes = infraestrutura.getCrudParticipacoes();
		arvoreUsuarioParticipacao = infraestrutura.getArvoreUsuarioParticipacao();
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
}
