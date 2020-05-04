package ui;

import java.io.IOException;
import java.util.ArrayList;

import entidades.Grupo;
import entidades.Usuario;
import infraestrutura.ArvoreBMais_Int_Int;
import infraestrutura.CRUD;
import infraestrutura.Infraestrutura;
import utils.Utils;

public class ParticipacaoUI extends BaseUI {
	
	public ParticipacaoUI(Infraestrutura infraestrutura) {
		super(infraestrutura);
	}

	/*public Resultado telaPrincipalParticipacao(Usuario usuario, Grupo grupo) {
		Resultado resultado = new Resultado();

		int opcao;
		do {
			Utils.limpaTela();

			Utils.mostrarMensagemResultado(resultado);

			System.out.print(
				"AMIGO OCULTO 1.0\n" +
				"================\n\n" +
				"INÍCIO > GRUPOS > PARTICIPAÇÃO EM GRUPO\n\n" +
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
				//	resultado = telaParticipacaoGrupos(usuario);
					break;
				default:
					resultado.setErro("Opção (" + opcao + ") inválida.");
			}
		} while (opcao != 0);

		return resultado;
	}*/
}
