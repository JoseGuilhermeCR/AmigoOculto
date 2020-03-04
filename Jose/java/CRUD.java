/* José Guilherme de Castro Rodrigues - 19/02/2020 - 04/03/2020 */
import java.io.File;
import java.io.RandomAccessFile;
import java.io.IOException;

import java.util.Scanner;

// lápide,tamanho,registro -> registros válidos.
// lápide,tamanho,próximo_registro_vazio (-1 ou endereço) -> registros deletados.

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

		// Cabeçalho do arquivo.
		if (arquivo.length() < 12) {
			arquivo.writeInt(0); // Último ID usado.
			arquivo.writeLong(-1); // Primeiro espaço vazio (também o menor).
		}

		indiceDireto = new HashExtensivel(
				10,
			       	DIRETORIO + "/diretorio." + nomeArquivo + ".idx",
				DIRETORIO + "/cestos." + nomeArquivo + ".idx");

		indiceIndireto = new ArvoreBMais_String_Int(
				10,
				DIRETORIO + "/arvoreB." + nomeArquivo + ".idx");
	}

	// TODO: Registros excluídos precisam ter um long como endereço para o próximo espaço vazio!

	// Retorna o id do usuário adicionado, ou -1 em caso de erro.
	public int create(Usuario entidade) {
		int ultimoID;

		try {
			// Lê último ID usado que está no cabeçalho do arquivo.
			ultimoID = fetchUltimoIDUsado();

			// Incrementa-se o ID em 1, e atualiza no cabeçalho.
			setUltimoIDUsado(++ultimoID);

			// Coloca o ID no usuário recebido.
			entidade.setID(ultimoID);

			byte[] bytesEntidade = entidade.toByteArray();
			short tamanhoEntidade = (short) bytesEntidade.length;

			// Variáveis de controle para busca em lista encadeada.
			long registroVazioAnterior = -1;
			long registroVazio = fetchPrimeiroRegistroVazio();
			short tamanhoVazio = 0;

			// Existe um endereço vazio, vamos procurá-lo e checar se esse registro cabe nele.
			while (tamanhoVazio < tamanhoEntidade && registroVazio != -1) {
				// Vai para o endereço pulando a lápide.
				arquivo.seek(registroVazio + 1);
				
				tamanhoVazio = arquivo.readShort();

				registroVazioAnterior = registroVazio;
				// Lê o próximo registro vazio.
				registroVazio = arquivo.readLong();
			}

			// Atenção, divisão de um float por 0.0f resultará em Inf. Não é preciso se preocupar com o 0.
			float porcentagem = (float)tamanhoEntidade / (float)tamanhoVazio;

			// Espaço vazio encontrado (mas a porcentagem gasta não será interessante)
			// ou espaço vazio não encontrado. Nesses casos, inserimos no final.
			boolean escreverNoFinal = porcentagem < 0.8 || porcentagem > 1.0;

			long endereco = (escreverNoFinal) ? arquivo.length() : registroVazioAnterior;
			
			arquivo.seek(endereco);

			// Escreve registro.
			arquivo.writeByte(0);

			// Se é no final, temos que escrever o tamanho, se não, deixamos o tamanho do registro original.
			if (escreverNoFinal)
				arquivo.writeShort(bytesEntidade.length);
			else
				arquivo.seek(arquivo.getFilePointer() + 2);

			arquivo.write(bytesEntidade);

			// Incluir o par (ID, endereço) no índice direto (baseado em IDs).
			indiceDireto.create(ultimoID, endereco);
			// Incluir o par (chave secundária, ID) no índice indireto.
			indiceIndireto.create(entidade.chaveSecundaria(), ultimoID);
		} catch (IOException exception) {
			System.err.println("IOException occurred when trying to create: " + entidade);
			exception.printStackTrace();
			ultimoID = -1;
		} catch (Exception exception) {
			exception.printStackTrace();
			ultimoID = -1;
		}

		return ultimoID;
	}

	// Retorna o usuário com determinado ID, ou null caso o usuário não exista.
	public Usuario read(int id) {
		Usuario usuario = new Usuario();

		try {
			// Buscar o endereço do registro do usuário no índice direto.
			// Retorna -1 no caso em que não encontrar.
			long endereco = indiceDireto.read(id); 

			// Mover o ponteiro do arquivo para o endereço recuperado.
			// Caso endereço seja -1, IOException vai ocorrer.
			arquivo.seek(endereco);

			// Ler lápide
			byte lapide = arquivo.readByte();

			if (lapide == 0) {
				// Ler tamanho
				short tamanhoUsuario = arquivo.readShort();
				
				byte[] vetorUsuario = new byte[tamanhoUsuario];
				// Ler vetor de bytes
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
			// Busca o ID no índice indireto, usando o valor retornado pelo método
			// chaveSecundaria()
			int id = indiceIndireto.read(email);

			// Só invocamos o read que usa ID
			usuario = read(id);
		} catch (IOException exception) {
			System.err.println("Erro ao tentar ler usuário de email: " + email);
			usuario = null;
		}

		return usuario;
	}

	// Atualiza o usuário. Retorna true em caso de sucesso ou false em caso de falha.
	public boolean update(Usuario entidade) {
		boolean sucesso = true;

		try {
			// Buscar o endereço do registro da entidade no índice direto usando ID 
			long endereco = indiceDireto.read(entidade.getID());
			// Mover o ponteiro no arquivo de entidades para o endereço recuperado (depois da lápide do registro) 
			arquivo.seek(endereco + 1);

			// Ler o registro da entidade e checar se houve variação no tamanho do registro 
			short tamanhoGravado = arquivo.readShort();

			byte[] bytesEntidadeAtualizada = entidade.toByteArray();
			short novoTamanho = (short) bytesEntidadeAtualizada.length;

			// Calculando o percentual de espaço usado pelo novo tamanho 
			float percentual = (float)novoTamanho / tamanhoGravado;

			// Novo tamanho gasta menos que 80% do espaço ou é maior que o espaço original.
			// Escrevemos no fim do arquivo.
			if (percentual < 0.80 || percentual > 1.0) {
				// Retorna ponteiro para endereço recuperado 
				arquivo.seek(endereco);
				// Marca campo lápide. 
				arquivo.writeByte(1);
				// Move ponteiro para fim do arquivo. 
				arquivo.seek(arquivo.length());
				// Identifica o novo endereço. 
				endereco = arquivo.getFilePointer();
				// Escreve novo campo
				arquivo.writeByte(0);
				arquivo.writeShort(bytesEntidadeAtualizada.length);
				arquivo.write(bytesEntidadeAtualizada);
				// Atualiza índice direto. (Mudança de endereço).
				indiceDireto.update(entidade.getID(), endereco);
			} else {
				// Reaproveita espaço.
				arquivo.seek(endereco + 3); // (1 byte para lápide e 2 para tamanho que não será alterado).
				arquivo.write(bytesEntidadeAtualizada);
			}

			// Atualiza índice indireto para caso de mudança de chave secundária.
			indiceIndireto.create(entidade.chaveSecundaria(), entidade.getID());
		} catch (IOException exception) {
			System.err.println("Erro ao tentar atualizar usuário de id: " + entidade.getID());
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
			// Buscar endereço em índice direto.
			long endereco = indiceDireto.read(id);

			// Ir para endereço (pulando a lápide que gasta 1 byte)
			arquivo.seek(endereco + 1);

			// Ler registro para ler chave secundária.
			short tamanhoRegistro = arquivo.readShort();

			byte[] registro = new byte[tamanhoRegistro];
			arquivo.read(registro);

			Usuario usuario = new Usuario(registro);

			// Retornar ponteiro para endereço.
			arquivo.seek(endereco);
			// Marcar campo lápide com 1 ( deletado ).
			arquivo.writeByte(1);

			// Deletar a entrada do índice indireto usando chaveSecundária.
			indiceIndireto.delete(usuario.chaveSecundaria());
			// Deletar a entrada do índice direto usando ID.
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

	// Busca o último ID usado no cabeçalho do arquivo.
	// O ponteiro do arquivo não sai do lugar onde estava quando a função é chamada.
	private int fetchUltimoIDUsado() throws IOException {
		long endereco = arquivo.getFilePointer();

		arquivo.seek(0);
		int ultimoID = arquivo.readInt();

		arquivo.seek(endereco);

		return ultimoID;
	}

	private void setUltimoIDUsado(int id) throws IOException {
		long endereco = arquivo.getFilePointer();

		arquivo.seek(0);
		arquivo.writeInt(id);

		arquivo.seek(endereco);
	}

	private long fetchPrimeiroRegistroVazio() throws IOException {
		long endereco = arquivo.getFilePointer();

		arquivo.seek(4); // Offset para chegar nesse item do cabeçalho.
		long primeiroRegistroVazio = arquivo.readLong();

		arquivo.seek(endereco);

		return primeiroRegistroVazio;
	}

	// Main feita só para testes.
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

			scanner.close();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
}
