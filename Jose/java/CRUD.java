/* José Guilherme de Castro Rodrigues - 19/02/2020 - 01/03/2020 */
import java.io.File;
import java.io.RandomAccessFile;
import java.io.IOException;

import java.util.Scanner;

/* TODO: Refatorar. */

public class CRUD {

	private final String DIRETORIO = "dados";

	private RandomAccessFile arquivo;
	private HashExtensivel indiceDireto;
	private ArvoreBMais_String_Int indiceIndireto;

	public CRUD(String nomeArquivo) throws Exception {
		File dir = new File(DIRETORIO);
		if (!dir.exists())
			dir.mkdir();

		arquivo = new RandomAccessFile(DIRETORIO + "/" + nomeArquivo + ".db", "rw");

		if (arquivo.length() < 4)
			arquivo.writeInt(0); // Cabeçalho do arquivo

		indiceDireto = new HashExtensivel(
				10,
			       	DIRETORIO + "/diretorio." + nomeArquivo + ".idx",
				DIRETORIO + "/cestos." + nomeArquivo + ".idx");

		indiceIndireto = new ArvoreBMais_String_Int(
				10,
				DIRETORIO + "/arvoreB." + nomeArquivo + ".idx");
	}

	/* Retorna o id do usuário adicionado, ou -1 em caso de erro */
	public int create(Usuario usuario) {
		int ultimoID;
		try {
			/* Lê último ID usado que está no cabeçalho do arquivo. */
			arquivo.seek(0);
			ultimoID = arquivo.readInt();

			/* Incrementa-se o ID em 1, e atualiza no cabeçalho. */
			ultimoID += 1;
			arquivo.seek(0);
			arquivo.writeInt(ultimoID);

			/* Coloca o ID no usuário recebido. */
			usuario.setID(ultimoID);

			/* Mover o ponteiro do arquivo para o fim do arquivo. */
			arquivo.seek(arquivo.length());
			/* Identificar o endereço em que o arquivo será escrito. */
			long endereco = arquivo.getFilePointer();

			/* Escrever o registro do usuário. */
			escreverRegistro(usuario);

			/* Incluir o par (ID, endereço) no índice direto (baseado em IDs). */
			// sucesso unused for now.
			boolean sucesso = indiceDireto.create(ultimoID, endereco);

			/* Incluir o par (chave secundária, ID) no índice indireto. */
			sucesso = indiceIndireto.create(usuario.chaveSecundaria(), ultimoID);
		} catch (IOException exception) {
			System.err.println("IOException occurred when trying to create: " + usuario);
			exception.printStackTrace();
			ultimoID = -1;
		} catch (Exception exception) {
			exception.printStackTrace();
			ultimoID = -1;
		}

		return ultimoID;
	}

	/* Retorna o usuário com determinado ID, ou null caso o usuário não exista. */
	public Usuario read(int id) {
		Usuario usuario = new Usuario();

		try {
			/* Buscar o endereço do registro do usuário no índice direto. */
			// Retorna -1 no caso em que não encontrar.
			long endereco = indiceDireto.read(id); 

			/* Mover o ponteiro do arquivo para o endereço recuperado. */
			// Caso endereço seja -1, IOException vai ocorrer.
			arquivo.seek(endereco);

			/* Ler lápide */
			byte lapide = arquivo.readByte();

			if (lapide == 0) {
				/* Ler tamanho */
				short tamanhoUsuario = arquivo.readShort();
				
				byte[] vetorUsuario = new byte[tamanhoUsuario];
				/* Ler vetor de bytes */
				arquivo.read(vetorUsuario);

				usuario.fromByteArray(vetorUsuario);
			} else {
				// Usuário foi deletado.
				// Retornamos null.
				usuario = null;
			}
		} catch (IOException exception) {
			System.err.println("Erro ao tentar ler usuário de id: " + id);
			exception.printStackTrace();
			usuario = null;
		} catch (Exception exception) {
			exception.printStackTrace();
			usuario = null;
		}

		return usuario;
	}

	public Usuario read(String email) {
		Usuario usuario = new Usuario();

		try {
			/* Busca o ID no índice indireto, usando o valor retornado pelo método
			 * chaveSecundaria() */
			int id = indiceIndireto.read(email);

			/* Só invocamos o read que usa ID */
			usuario = read(id);
		} catch (IOException exception) {
			System.err.println("Erro ao tentar ler usuário de email: " + email);
			usuario = null;
		}

		return usuario;
	}

	/* Atualiza o usuário. Retorna true em caso de sucesso ou false em caso de falha. */
	public boolean update(Usuario usuario) {
		boolean sucesso = true;

		// TODO: Implementar DESAFIO 1 ?

		try {
			/* Buscar o endereço do registro do usuário no índice direto usando ID */
			long endereco = indiceDireto.read(usuario.getID());
			/* Mover o ponteiro no arquivo de usuários para o endereço recuperado */
			arquivo.seek(endereco);

			/* Ler o registro do usuário e checar se houve variação no tamanho do registro */
			// Lê a lápide.
			byte lapide = arquivo.readByte();
			// Lê tamanho do registro gravado.
			short tamanhoGravado = arquivo.readShort();
			// Lê o registro.
			byte[] registro = new byte[tamanhoGravado];
			arquivo.read(registro);
			Usuario usuarioGravado = new Usuario(registro);

			/* Retorna ponteiro para endereço recuperado */
			arquivo.seek(endereco);

			byte[] vetorUsuarioAtualizado = usuario.toByteArray();
			if (tamanhoGravado == vetorUsuarioAtualizado.length) {
				/* Usuário não mudou de tamanho após atualização. */
				/* Sobrescrever o registro atual. */
				escreverRegistro(usuario);
			} else {
				/* Usuário mudou de tamanho após atualização. */
				/* Marca campo como lápide. */
				arquivo.writeByte(1);

				/* Move ponteiro para fim do arquivo. */
				arquivo.seek(arquivo.length());

				/* Identificar o endereço onde registro será escrito. */
				endereco = arquivo.getFilePointer();

				/* Escrever novo registro. */
				escreverRegistro(usuario);

				/* Atualizar o par (ID, endereço) no índice direto. */
				indiceDireto.delete(usuario.getID());
				indiceDireto.create(usuario.getID(), endereco);
			}

			/* Caso chave secundária tenha mudado. */
			if (!usuario.chaveSecundaria().equals(usuarioGravado.chaveSecundaria())) {
				/* Atualizar o par (chaveSecundária, ID) no índice indireto. */

				// Essas funções retornam um boolean, mas como o resultado não bate com o que acontece
				// suponho que não sejam indicadores de sucesso/falha.

				// Deleta chave anterior.
				indiceIndireto.delete(usuarioGravado.chaveSecundaria());
				// Adiciona nova.
				indiceIndireto.create(usuario.chaveSecundaria(), usuario.getID());
			}

		} catch (IOException exception) {
			System.err.println("Erro ao tentar atualizar usuário de id: " + usuario.getID());
			exception.printStackTrace();
			sucesso = false;
		} catch (Exception exception) {
			exception.printStackTrace();
			sucesso = false;
		}

		return sucesso;
	}

	public boolean delete(int id) {
		boolean sucesso = true;

		// TODO: Implementar DESAFIO 2 ?

		try {
			/* Buscar endereço em índice direto. */
			long endereco = indiceDireto.read(id);

			/* Ir para endereço. */
			arquivo.seek(endereco);

			/* Ler registro para ler chave secundária. */
			byte lapide = arquivo.readByte();
			short tamanhoRegistro = arquivo.readShort();

			byte[] registro = new byte[tamanhoRegistro];
			arquivo.read(registro);

			Usuario usuario = new Usuario(registro);

			/* Retornar ponteiro para endereço. */
			arquivo.seek(endereco);
			/* Marcar campo lápide com 1 ( deletado ). */
			arquivo.writeByte(1);

			/* Deletar a entrada do índice indireto usando chaveSecundária. */
			indiceIndireto.delete(usuario.chaveSecundaria());
			/* Deletar a entrada do índice direto usando ID. */
			indiceDireto.delete(usuario.getID());
		} catch (IOException exception) {
			System.err.println("Erro ao tentar deletar usuário com id: " + id);
			exception.printStackTrace();
			sucesso = false;
		} catch (Exception exception) {
			exception.printStackTrace();
			sucesso = false;
		}

		return sucesso;
	}

	/* Escreve um registro (lápide, tamanho, entidade) considerando que o ponteiro
	 * do arquivo esteja no local certo. */
	private void escreverRegistro(Usuario usuario) throws IOException {
		arquivo.writeByte(0); // Lápide (0, não marcada).

		// May throw IOException.
		byte[] vetorUsuario = usuario.toByteArray();

		arquivo.writeShort(vetorUsuario.length); // Tamanho do vetor de bytes.
		arquivo.write(vetorUsuario); // Vetor de bytes.
	}

	/* Main feita só para testes. */
	public static void main(String[] args) {
		try {

			CRUD crud = new CRUD("user");

			Scanner scanner = new Scanner(System.in);
			
			System.out.println("1 - Criar");
			System.out.println("2 - Ler ID");
			System.out.println("3 - Ler Chave Secundaria");
			System.out.println("4 - Atualizar");
			System.out.println("5 - Deletar");
			System.out.println("6 - Sair");
			int opcao = scanner.nextInt();
			scanner.nextLine();

			while (opcao != 6) {
				switch (opcao) {
					case 1:
					{
						System.out.println("Digite um usuário: (nome/email/senha)");
						String[] uStr = scanner.nextLine().split("/");
						Usuario usuario = new Usuario(0, uStr[0], uStr[1], uStr[2]);

						int insertedID = crud.create(usuario);
						System.out.println("Usuário: " + usuario + " criado com ID: " + insertedID);
						break;
					}
					case 2:
					{
						System.out.println("Digite o ID: ");
						int id = scanner.nextInt();
						scanner.nextLine();

						Usuario usuario = crud.read(id);

						if (usuario != null) {
							System.out.println("Li usuário: " + usuario);
						} else {
							System.out.println("Nenhum usuário com ID: " + id);
						}

						break;
					}
					case 3:
					{
						System.out.println("Digite o email: ");
						String email = scanner.nextLine();

						Usuario usuario = crud.read(email);

						if (usuario != null) {
							System.out.println("Li usuário: " + usuario);
						} else {
							System.out.println("Nenhum usuário com email: " + email);
						}

						break;
					}
					case 4:
					{
						System.out.println("Digite o ID do usuário que você quer atualizar: ");
						int id = scanner.nextInt();
						scanner.nextLine();

						System.out.println("Agora digite os novos campos do usuário: (nome/email/senha)");
						String[] uStr = scanner.nextLine().split("/");
						Usuario usuario = new Usuario(id, uStr[0], uStr[1], uStr[2]);

						if (crud.update(usuario))
							System.out.println("Usuário atualizado com sucesso.");
						else 
							System.out.println("Algum erro aconteceu e não foi possível atualizar o usuário.");

						break;
					}
					case 5:
					{
						System.out.println("Digite o ID do usuário que você quer deletar: ");
						int id = scanner.nextInt();
						scanner.nextLine();

						if (crud.delete(id))
							System.out.println("Usuário deletado com sucesso.");
						else 
							System.out.println("Algum erro aconteceu e não foi possível deletar o usuário.");

						break;
					}
					default:
						System.out.println("Opção não válida.");
						break;
				}

				System.out.println("1 - Criar");
				System.out.println("2 - Ler ID");
				System.out.println("3 - Ler Chave Secundaria");
				System.out.println("4 - Atualizar");
				System.out.println("5 - Deletar");
				System.out.println("6 - Sair");
				opcao = scanner.nextInt();
				scanner.nextLine();
			}

		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
}
