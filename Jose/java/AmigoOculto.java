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

public class AmigoOculto {

	private CRUD<Usuario> crudUsuario;
	private Scanner scanner;

	public AmigoOculto() throws Exception {
		scanner = new Scanner(System.in);

		crudUsuario = new CRUD<>("user", Usuario.class.getConstructor());
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

				if (!resultado.valido()) {
					System.out.println("ERRO: " + resultado.mensagem() + "\n");
					resultado.limparMensagem();
				} else {
					System.out.println(resultado.mensagem() + "\n");
					resultado.limparMensagem();
				}
				
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
					resultado.setSucesso("AindaNãoImplementada");
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

	private void limpaTela() throws IOException, InterruptedException {
		new ProcessBuilder("bash", "-c", "clear").inheritIO().start().waitFor();
	}

	public static void main(String[] args) {
		try {
			new AmigoOculto().run();
		} catch (Exception exception) {
			System.err.println("Um erro ocorreu durante a inicialização do programa.");
		}
	}
}
