/* José Guilherme de Castro Rodrigues 2020 */

package ui;

import java.io.IOException;

import utils.Utils;
import entidades.Sugestao;
import entidades.Usuario;
import infraestrutura.ArvoreBMais_Int_Int;
import infraestrutura.CRUD;
import infraestrutura.Infraestrutura;

public class SugestaoUI {

	private CRUD<Sugestao> crudSugestao;

	private ArvoreBMais_Int_Int arvoreUsuarioSugestao;

	public SugestaoUI(Infraestrutura infraestrutura) {
		crudSugestao = infraestrutura.getCrudSugestao();
		arvoreUsuarioSugestao = infraestrutura.getArvoreUsuarioSugestao();
	}

	public Resultado telaSugestoes(Usuario usuario) {
		Resultado resultado = new Resultado();

		int opcao;
		do {
			Utils.limpaTela();

			Utils.mostrarMensagemResultado(resultado);

			System.out.print(
				"AMIGO OCULTO 1.0\n" +
				"================\n\n" +
				"INÍCIO > SUGESTÕES\n\n" +
				"1) Listar\n" +
				"2) Incluir\n" +
				"3) Alterar\n" +
				"4) Excluir\n\n" +
				"0) Retornar ao menu anterior\n\n" +
				"Opção: "
			);
			opcao = Utils.readInt();

			switch (opcao) {
				case 0:
					resultado.setSucesso("SUGESTÕES > INÍCIO");
					break;
				case 1:
					resultado = telaListarSugestoes(usuario);
					break;
				case 2:
					resultado = telaIncluirSugestao(usuario);
					break;
				case 3:
					resultado = telaAlterarSugestoes(usuario);
					break;
				case 4:
					resultado = telaExcluirSugestao(usuario);
					break;
				default:
					resultado.setErro("Opção (" + opcao + ") inválida.");
			}
		} while (opcao != 0);

		return resultado;
	}

	private Resultado telaListarSugestoes(Usuario usuario) {
		Resultado resultado = new Resultado();

		Sugestao sugestoes[] = listarSugestoes(usuario, resultado);

		if (sugestoes != null && sugestoes.length != 0) {
			Utils.limpaTela();
			System.out.println("MINHAS SUGESTÕES\n");

			int contador = 1;
			for (Sugestao sugestao : sugestoes) {
				// Caso o CRUD não ache a sugestão com esse ID, será retornado null.
				if (sugestao != null) {
					System.out.print(contador + ".");
					sugestao.prettyPrint();
					System.out.println();
				}
				contador++;	
			}

			System.out.println("Pressione qualquer tecla para continuar...");
			Utils.scanner.nextLine();
		} else {
			resultado.setErro("Você não tem nenhuma sugestão.");
		}

		return resultado;
	}

	private Resultado telaIncluirSugestao(Usuario usuario) {
		Resultado resultado = new Resultado();

		Utils.limpaTela();
		System.out.print(
			"INCLUIR SUGESTÃO\n\n" +
			"Nome do produto: "
		);
		String nomeProduto = Utils.scanner.nextLine();

		if (!nomeProduto.isBlank()) {
			System.out.print(
				"Loja (opcional): "
			);
			String loja = Utils.scanner.nextLine();

			// Pega-se o valor como se fosse uma String para que seja possível omitir.
			System.out.print(
				"Valor (opcional): "
			);
			String valorStr = Utils.scanner.nextLine();

			float valor;
			try {
				valor = Float.parseFloat(valorStr);
			} catch (Exception e) {
				valor = Float.NaN;
			}

			System.out.print(
				"Observações (opcional): "
			);
			String observacoes = Utils.scanner.nextLine();

			if (Utils.confirmar("Completar inclusão?")) {
				int idInserido = crudSugestao.create(new Sugestao(nomeProduto, valor, loja, observacoes));

				if (idInserido != -1) {

					try {
						arvoreUsuarioSugestao.create(usuario.getID(), idInserido);
					} catch (IOException exception) {
						resultado.setErro("Ocorreu um erro durante a inclusão.");
					}

					resultado.setSucesso("Inclusão realizada com sucesso.");
				} else {
					resultado.setErro("Ocorreu um erro durante a inclusão.");
				}
			} else {
				resultado.setSucesso("Inclusão cancelada.");	
			}
		} else {
			resultado.setErro("O nome do produto não pode estar vazio.");
		}

		return resultado;
	}

	private Resultado telaAlterarSugestoes(Usuario usuario) {
		Resultado resultado = new Resultado();

		Utils.limpaTela();

		// Primeiro, lista as sugestões do usuário.
		resultado = telaListarSugestoes(usuario);
		Sugestao sugestoes[] = listarSugestoes(usuario, resultado);

		if (sugestoes != null && sugestoes.length != 0) {
			System.out.print(
				"Quais sugestões você quer alterar? (0 para sair ou [1, 2, ...]): "
			);
			String indicesSugestoesAAlterar[] = Utils.scanner.nextLine().replace(" ", "").split(",");

			for (String str : indicesSugestoesAAlterar) {
				int indiceSugestao = Integer.parseInt(str) - 1;

				// Se a sugestão for válida (estiver na lista apresentada anteriormente).
				if (indiceSugestao >= 0 && indiceSugestao < sugestoes.length) {
					Sugestao sugestao = sugestoes[indiceSugestao];

					if (sugestao != null) {
						Utils.limpaTela();

						System.out.println("ALTERANDO SUGESTÃO " + str + "\n");
						sugestao.prettyPrint();
						System.out.println();

						// Lê as alterações dos campos.
						Sugestao novaSugestao = lerNovaSugestao(sugestao);

						// Se houver alterações.
						if (!novaSugestao.equals(sugestao)) {
							if (Utils.confirmar("Confirmar alteração?")) {
								crudSugestao.update(novaSugestao);
							}
						}
					}
				}
			}
		}

		return resultado;
	}

	private Resultado telaExcluirSugestao(Usuario usuario) {
		Resultado resultado = new Resultado();

		Utils.limpaTela();

		resultado = telaListarSugestoes(usuario);
		Sugestao sugestoes[] = listarSugestoes(usuario, resultado);

		if (sugestoes != null && sugestoes.length != 0) {
			System.out.print(
					"Quais sugestões você quer deletar? (0 para sair ou [1, 2, ...]): "
			);
			String indicesSugestoesADeletar[] = Utils.scanner.nextLine().replace(" ", "").split(",");
			
			for (String str : indicesSugestoesADeletar) {
				int indiceSugestao = Integer.parseInt(str) - 1;

				// Se a sugestão for válida (estiver na lista apresentada anteriormente).
				if (indiceSugestao >= 0 && indiceSugestao < sugestoes.length) {
					Sugestao sugestao = sugestoes[indiceSugestao];

					if (sugestao != null) {
						Utils.limpaTela();

						System.out.println("EXCLUINDO SUGESTÃO " + str + "\n");
						sugestao.prettyPrint();
						System.out.println();

						if (Utils.confirmar("Confirmar exclusão?")) {
							crudSugestao.delete(sugestao.getID());

							try { 
								arvoreUsuarioSugestao.delete(usuario.getID(), sugestao.getID());
							} catch (Exception e) {
								resultado.setErro("Ocorreu um erro durante a exclusão.");
							}
						}
					}
				}
			}
		}

		return resultado;
	}

	// Lê uma nova sugestão que funcionará para alteração.
	public Sugestao lerNovaSugestao(Sugestao sugestao) {
		// Lê as alterações dos campos.
		System.out.println("O campo deixado em branco não será alterado.");
		System.out.print(
			"Novo nome do produto: "
		);
		String novoNome = Utils.scanner.nextLine();

		System.out.print(
			"Nova loja: "
		);
		String novaLoja = Utils.scanner.nextLine();

		System.out.print(
			"Novo valor: "
		);
		String novoValorStr = Utils.scanner.nextLine();

		float novoValor;
		try {
			novoValor = Float.parseFloat(novoValorStr);
		} catch (Exception e) {
			novoValor = Float.NaN;
		}

		System.out.print(
			"Novas observações: "
		);
		String novasObservacoes = Utils.scanner.nextLine();

		Sugestao novaSugestao = new Sugestao(
			(novoNome.isBlank()) ? sugestao.getProduto() : novoNome,
			(Float.isNaN(novoValor)) ? sugestao.getValor() : novoValor,
			(novaLoja.isBlank()) ? sugestao.getLoja() : novaLoja,
			(novasObservacoes.isBlank()) ? sugestao.getObservacoes() : novasObservacoes
		);
		novaSugestao.setID(sugestao.getID());
		novaSugestao.setIDUsuario(sugestao.getIDUsuario());

		return novaSugestao;
	}

	private Sugestao[] listarSugestoes(Usuario usuario, Resultado resultado) {
		Sugestao sugestoes[];

		try {
			// Lê todas sugestões desse usuário.
			int idsSugestoes[] = arvoreUsuarioSugestao.read(usuario.getID());
			sugestoes = new Sugestao[idsSugestoes.length];

			// As lê e coloca no vetor de sugestões.
			int contador = 0;
			for (int id : idsSugestoes) {
				sugestoes[contador++] = crudSugestao.read(id);
			}
		} catch (Exception exception) {
			resultado.setErro("Ocorreu um erro ao tentar ler as suas sugestões.");
			sugestoes = null;
		}

		return sugestoes;
	}
}