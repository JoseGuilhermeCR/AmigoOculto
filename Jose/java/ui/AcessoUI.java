/* José Guilherme de Castro Rodrigues 2020 */

package ui;

import utils.Utils;
import entidades.Usuario;
import infraestrutura.CRUD;
import infraestrutura.Infraestrutura;

public class AcessoUI extends BaseUI {

	private CRUD<Usuario> crudUsuario;

	private MenuUI menuUI;

	public AcessoUI(Infraestrutura infraestrutura) {
		super(infraestrutura);
		
		crudUsuario = infraestrutura.getCrudUsuario();

		menuUI = new MenuUI(infraestrutura);
	}

	public Resultado telaAcesso() {
		Resultado resultado =  new Resultado();

		int opcao;
		do {
			Utils.limpaTela();

			Utils.mostrarMensagemResultado(resultado);
				
			System.out.print(
				"AMIGO OCULTO 1.0\n" +
				"================\n\n" +
				"ACESSO\n\n" +
				"1) Acesso ao sistema\n" +
				"2) Novo usuário (primeiro acesso)\n\n" +
				"0) Sair\n\n" + 
				"Opção: "
			);
			opcao = Utils.readInt();

			switch (opcao) {
				case 0:
					resultado.setSucesso("Até mais!");
					break;
				case 1:
					resultado = telaAcessoJaCadastrado();
					break;
				case 2:
					resultado = telaPrimeiroAcesso();
					break;
				default:
					resultado.setErro("Opção (" + opcao + ") inválida.");
			}
		} while (opcao != 0);

		return resultado;
	}

	private Resultado telaPrimeiroAcesso() {
		Resultado resultado = new Resultado();

		Utils.limpaTela();
		System.out.print(
			"NOVO USUÁRIO\n\n" +
			"Email: "
		);
		String email = Utils.scanner.nextLine();

		if (!email.isBlank() && email.length() != 2 && email.contains("@") && email.contains(".")) {
			// Pedimos o CRUD para recuperar o usuário com email. Esperamos que ainda não haja um.
			if (crudUsuario.read(email) == null) {
				System.out.print(
					"Nome: "
				);
				String nome = Utils.scanner.nextLine();

				System.out.print(
					"Senha: "
				);
				String senha = Utils.scanner.nextLine();

				if (!nome.isBlank() && !senha.isBlank()) {
					if (Utils.confirmar("Completar cadastro?")) {
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

		Utils.limpaTela();
		System.out.print(
			"ACESSO AO SISTEMA\n\n" +
			"Email: "
		);
		String email = Utils.scanner.nextLine();

		if (!email.isBlank() && email.length() != 2 && email.contains("@") && email.contains(".")) {
			Usuario usuario = crudUsuario.read(email);

			if (usuario != null) {
				System.out.print(
					"Senha: "
				);
				String senha = Utils.scanner.nextLine();

				if (usuario.validarSenha(senha)) {
					resultado = menuUI.telaMenuPrincipal(usuario);
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
}
