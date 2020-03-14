import java.io.IOException;
import java.util.Scanner;

class Resultado {
	private String _mensagem;
	private boolean _valido;

	public Resultado() {
		_mensagem = "";
		_valido = true;
	}

	public void setSucesso(String mensagem) {
		_mensagem = mensagem;
		_valido = true;
	}

	public void setErro(String mensagem) {
		_mensagem = mensagem;
		_valido = false;
	}

	public boolean valido() {
		return _valido;
	}

	public String mensagem() {
		return _mensagem;
	}

	public void limparMensagem() {
		_mensagem = "";
	}
}

// TODO: Por enquanto, acentos apresentam problema no Windows.
// Quando compilar, é preciso usar -encoding utf-8.
// Quando executar, os caracteres apresentam problema mesmo com chcp 65001 no terminal.

public class AmigoOculto {

	private CRUD<Usuario> crudUsuario;
	private CRUD<Sugestao> crudSugestao;

	// Tentar abstrair e tirar essa classe daqui. Na teoria só o programa principal ficaria aqui...
	private ArvoreBMais_Int_Int arvoreUsuarioSugestao;

	private Scanner scanner;

	public AmigoOculto() throws Exception {
		scanner = new Scanner(System.in);

		crudUsuario = new CRUD<>("user", Usuario.class.getConstructor());
		crudSugestao = new CRUD<>("sugestao", Sugestao.class.getConstructor());

		arvoreUsuarioSugestao = new ArvoreBMais_Int_Int(10, "dados/arvoreB.usuarioSugestao.idx");
	}

	public Resultado run() {
		return telaAcesso();
	}

	private Resultado telaAcesso() {
		Resultado resultado =  new Resultado();

		int opcao;
		do {
			limpaTela();

			mostrarMensagemResultado(resultado);
				
			System.out.print(
				"AMIGO OCULTO 1.0\n" +
				"================\n\n" +
				"ACESSO\n\n" +
				"1) Acesso ao sistema\n" +
				"2) Novo usuário (primeiro acesso)\n\n" +
				"Opção: "
			);
			opcao = scanner.nextInt();
			scanner.nextLine();

			switch (opcao) {
				case 1:
					resultado = telaAcessoJaCadastrado();
					break;
				case 2:
					resultado = telaPrimeiroAcesso();
					break;
			}
		} while (opcao > 0 && opcao <= 2);

		return resultado;
	}

	private Resultado telaPrimeiroAcesso() {
		Resultado resultado = new Resultado();

		limpaTela();
		System.out.print(
			"NOVO USUÁRIO\n\n" +
			"Email: "
		);
		String email = scanner.nextLine();

		if (!email.isBlank() && email.length() != 2 && email.contains("@") && email.contains(".")) {
			// Pedimos o CRUD para recuperar o usuário com email. Esperamos que ainda não haja um.
			if (crudUsuario.read(email) == null) {
				System.out.print(
					"Nome: "
				);
				String nome = scanner.nextLine();

				System.out.print(
					"Senha: "
				);
				String senha = scanner.nextLine();

				if (!nome.isBlank() && !senha.isBlank()) {
					if (confirmar("Completar cadastro?")) {
						int idInserido = crudUsuario.create(new Usuario(nome, email, senha));

						if (idInserido != -1) {
							resultado.setSucesso("Cadastro realizado com sucesso.");
						} else {
							resultado.setErro("Ocorreu um erro durante o cadastro.");
						}
					} else {
						resultado.setSucesso("Cadastro cancelado.");
					}
				} else {
					resultado.setErro("O nome ou senha digitados não são válidos.");
				}
			} else {
				resultado.setErro("Um usuário já está cadastro com esse email (" + email + ").");
			}
		} else {
			resultado.setErro("Email (" + email + ") não é um email válido.");
		}

		return resultado;
	}

	private Resultado telaAcessoJaCadastrado() {
		Resultado resultado = new Resultado();

		limpaTela();
		System.out.print(
			"ACESSO AO SISTEMA\n\n" +
			"Email: "
		);
		String email = scanner.nextLine();

		if (!email.isBlank() && email.length() != 2 && email.contains("@") && email.contains(".")) {
			Usuario usuario = crudUsuario.read(email);

			if (usuario != null) {
				System.out.print(
					"Senha: "
				);
				String senha = scanner.nextLine();

				if (usuario.validarSenha(senha)) {
					resultado = telaMenuPrincipal(usuario);
				} else {
					resultado.setErro("Senha incorreta.");	
				}
			} else {
				resultado.setErro("Nenhum usuário com email (" + email + ") foi encontrado.");
			}
		} else {
			resultado.setErro("Email (" + email + ") não é um email válido.");
		}

		return resultado;
	}

	private Resultado telaMenuPrincipal(Usuario usuario) {
		Resultado resultado = new Resultado();

		// Talvez não seja uma boa ideia fazer esse tipo de uso para o resultado...
		// Não foi pensado para se usar dessa maneira.
		resultado.setSucesso("Bem-vindo " + usuario.getNome() + "!");

		int opcao;
		do {
			limpaTela();

			mostrarMensagemResultado(resultado);

			System.out.print(
				"AMIGO OCULTO 1.0\n" +
				"================\n\n" +
				"INÍCIO\n\n" +
				"1) Sugestões de presentes\n" +
				"2) Grupos\n" +
				"3) Novos convites: 0\n\n" +
				"0) Sair\n\n" +
				"Opcão: "
			);
			opcao = scanner.nextInt();
			scanner.nextLine();

			switch (opcao) {
				case 0:
					resultado.setSucesso("Até mais " + usuario.getNome() + "!");
					break;
				case 1:
					resultado = telaSugestoes(usuario);
					break;
				case 2:
					break;
				case 3:
					break;
				default:
					resultado.setErro("Opção (" + opcao + ") inválida.");
			}
		} while (opcao != 0);

		return resultado;
	}

	private Resultado telaSugestoes(Usuario usuario) {
		Resultado resultado = new Resultado();

		int opcao;
		do {
			limpaTela();

			mostrarMensagemResultado(resultado);

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
			opcao = scanner.nextInt();
			scanner.nextLine();

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
			limpaTela();
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
			scanner.nextLine();
		} else {
			resultado.setErro("Você não tem nenhuma sugestão.");
		}

		return resultado;
	}

	private Resultado telaIncluirSugestao(Usuario usuario) {
		Resultado resultado = new Resultado();

		limpaTela();
		System.out.print(
			"INCLUIR SUGESTÃO\n\n" +
			"Nome do produto: "
		);
		String nomeProduto = scanner.nextLine();

		if (!nomeProduto.isBlank()) {
			System.out.print(
				"Loja (opcional): "
			);
			String loja = scanner.nextLine();

			// Pega-se o valor como se fosse uma String para que seja possível omitir.
			System.out.print(
				"Valor (opcional): "
			);
			String valorStr = scanner.nextLine();

			float valor;
			try {
				valor = Float.parseFloat(valorStr);
			} catch (Exception e) {
				valor = Float.NaN;
			}

			System.out.print(
				"Observações (opcional): "
			);
			String observacoes = scanner.nextLine();

			if (confirmar("Completar inclusão?")) {
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

		limpaTela();

		// Primeiro, lista as sugestões do usuário.
		resultado = telaListarSugestoes(usuario);
		Sugestao sugestoes[] = listarSugestoes(usuario, resultado);

		if (sugestoes != null && sugestoes.length != 0) {
			System.out.print(
				"Quais sugestões você quer alterar? (0 para sair ou [1, 2, ...]): "
			);
			String indicesSugestoesAAlterar[] = scanner.nextLine().replace(" ", "").split(",");

			for (String str : indicesSugestoesAAlterar) {
				int indiceSugestao = Integer.parseInt(str) - 1;

				// Se a sugestão for válida (estiver na lista apresentada anteriormente).
				if (indiceSugestao >= 0 && indiceSugestao < sugestoes.length) {
					Sugestao sugestao = sugestoes[indiceSugestao];

					if (sugestao != null) {
						limpaTela();

						System.out.println("ALTERANDO SUGESTÃO " + str + "\n");
						sugestao.prettyPrint();
						System.out.println();

						// Lê as alterações dos campos.
						Sugestao novaSugestao = sugestao.lerNovaSugestao(scanner);

						// Se houver alterações.
						if (!novaSugestao.equals(sugestao)) {
							if (confirmar("Confirmar alteração?")) {
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

		limpaTela();

		resultado = telaListarSugestoes(usuario);
		Sugestao sugestoes[] = listarSugestoes(usuario, resultado);

		if (sugestoes != null) {
			System.out.print(
					"Quais sugestões você quer deletar? (0 para sair ou [1, 2, ...]): "
			);
			String indicesSugestoesADeletar[] = scanner.nextLine().replace(" ", "").split(",");
			
			for (String str : indicesSugestoesADeletar) {
				int indiceSugestao = Integer.parseInt(str) - 1;

				// Se a sugestão for válida (estiver na lista apresentada anteriormente).
				if (indiceSugestao >= 0 && indiceSugestao < sugestoes.length) {
					Sugestao sugestao = sugestoes[indiceSugestao];

					if (sugestao != null) {
						limpaTela();

						System.out.println("EXCLUINDO SUGESTÃO " + str + "\n");
						sugestao.prettyPrint();
						System.out.println();

						if (confirmar("Confirmar exclusão?")) {
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

	private boolean confirmar(String mensagem) {
		System.out.print(mensagem + " (sim/não): ");
		String confirmacao = scanner.nextLine();

		return (confirmacao.toLowerCase().equals("sim"));
	}

	private void mostrarMensagemResultado(Resultado resultado) {
		if (!resultado.valido()) {
			System.out.println("ERRO: " + resultado.mensagem() + "\n");
		} else {
			System.out.println(resultado.mensagem() + "\n");
		}
		resultado.limparMensagem();
	}

	private void limpaTela() {
		try {
			// Equivalente em Linux usando BASH
			// new ProcessBuilder("bash", "-c", "clear").inheritIO().start().waitFor();
			// Windows
			new ProcessBuilder("cmd", "/C", "cls").inheritIO().start().waitFor();
		} catch (Exception exception) {
			// Talvez escrever no arquivo de log?
			exception.printStackTrace();
		}
	}

	public static void main(String[] args) {
		try {
			new AmigoOculto().run();
		} catch (Exception exception) {
			System.err.println("Um erro ocorreu durante a inicialização do programa.");
		}
	}
}
