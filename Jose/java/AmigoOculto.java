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

	private Scanner scanner;

	public AmigoOculto() throws Exception {
		scanner = new Scanner(System.in);

		crudUsuario = new CRUD<>("user", Usuario.class.getConstructor());
		crudSugestao = new CRUD<>("sugestao", Sugestao.class.getConstructor());
	}

	public Resultado run() {
		return telaAcesso();
	}

	private Resultado telaAcesso() {
		Resultado resultado =  new Resultado();

		try {
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
		} catch (Exception exception) {
			resultado.setErro(exception.getMessage());
		}

		return resultado;
	}

	private Resultado telaPrimeiroAcesso() throws IOException, InterruptedException {
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
					System.out.print(
						"Completar cadastro? (sim/não): "
					);
					String completarCadastro = scanner.nextLine();

					if (completarCadastro.toLowerCase().equals("sim")) {
						int idInserido = crudUsuario.create(new Usuario(0, nome, email, senha));

						if (idInserido != -1) {
							resultado.setSucesso("Cadastro realizado com sucesso.");
						} else {
							resultado.setErro("Ocorreu um erro durante o cadastro");
						}
					} else {
						resultado.setErro("Cadastro cancelado.");
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

	private Resultado telaAcessoJaCadastrado() throws IOException, InterruptedException {
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

				if (usuario.getSenha().equals(senha)) {
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

	private Resultado telaMenuPrincipal(Usuario usuario) throws IOException, InterruptedException {
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

	private Resultado telaSugestoes(Usuario usuario) throws IOException, InterruptedException {
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

	private void mostrarMensagemResultado(Resultado resultado) {
		if (!resultado.valido()) {
			System.out.println("ERRO: " + resultado.mensagem() + "\n");
		} else {
			System.out.println(resultado.mensagem() + "\n");
		}
		resultado.limparMensagem();
	}

	private void limpaTela() throws IOException, InterruptedException {
		// Equivalente em Linux usando BASH
		// new ProcessBuilder("bash", "-c", "clear").inheritIO().start().waitFor();
		// Windows
		new ProcessBuilder("cmd", "/C", "cls").inheritIO().start().waitFor();
	}

	public static void main(String[] args) {
		try {
			new AmigoOculto().run();
		} catch (Exception exception) {
			System.err.println("Um erro ocorreu durante a inicialização do programa.");
		}
	}
}
