/* José Guilherme de Castro Rodrigues - 19/02/2020 - 05/03/2020 */
import java.lang.reflect.Constructor;
import java.io.File;
import java.io.FileWriter;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.time.LocalDateTime;
//import java.util.Scanner;

/* O CRUD usa um índice direto de IDs para endereços e um indireto de emais para IDs.
 * O cabeçalho do arquivo consiste de 4 bytes que guardam o último ID usado por um registro seguido
 * de 8 bytes que guardam o próximo endereço vazio disponível no arquivo. 
 * Um registro pode ter dois formatos. O primeiro formato é usado quando o registro é válido e consiste
 * das seguintes propriedades: 1 byte indicando lápide marcada como 0, 2 bytes que indicam o tamanho
 * do registro e por fim n bytes de registro. 
 * O segundo formato é usado nos registros que foram deletados e consiste de 1 byte indicando lápide
 * marcada como 1, 2 bytes que indicam o tamanho original do registro e por fim 8 bytes indicando o endereço do próximo
 * registro vazio na lista de registros vazios.
 * Observe que no segundo caso, todos os registros têm (tamanho original - 8) bytes de lixo. 
 * A lista encadeada é formada por um conjunto de registros vazios. Cada vez que um registro é deletado,
 * ele é encadeado na lista de forma que a lista fique ordenada de forma crescente. Toda vez que um registro
 * é criado, checa-se a existência de um registro vazio na lista para que haja reaproveitamento de espaço.
 * Caso um registro vazio seja encontrado, checa-se se a porcentagem do espaço gasto pelo novo registro é maior
 * que 80% do tamanho do registro vazio, se sim, ele é desencadeado e usado, se não, o novo registro será escrito
 * no final do arquivo e a lista encadeada não sofre alteração. */

public class CRUD<T extends Entidade> {

	private final String DIRETORIO = "dados";

	private Constructor<T> tConstructor;

	private RandomAccessFile arquivo;
	private FileWriter arquivoLog;
	private HashExtensivel indiceDireto;
	private ArvoreBMais_String_Int indiceIndireto;

	public CRUD(String nomeArquivo, Constructor<T> tConstructor) throws Exception {
		this.tConstructor = tConstructor;

		File dir = new File(DIRETORIO);
		if (!dir.exists())
			dir.mkdir();

		arquivo = new RandomAccessFile(DIRETORIO + "/" + nomeArquivo + ".db", "rw");
		arquivoLog = new FileWriter(DIRETORIO + "/" + nomeArquivo + ".log", true);

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

	// Retorna o id do usuário adicionado, ou -1 em caso de erro.
	public int create(T entidade) {
		int ultimoID;

		try {
			// Lê último ID usado que está no cabeçalho do arquivo.
			ultimoID = fetchUltimoIDUsado();

			// Incrementa-se o ID em 1, e atualiza no cabeçalho.
			setUltimoIDUsado(++ultimoID);

			// Coloca o ID na entidade recebida.
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
			if (escreverNoFinal) {
				arquivo.writeShort(tamanhoEntidade);
			} else {
				// Também é necessário desencadear esse registro da lista antes de utilizá-lo.
				desencadearRegistroVazio(endereco);
				// Pula a lápide e o tamanho desse registro.
				arquivo.seek(endereco + 3);
			}

			arquivo.write(bytesEntidade);

			// Incluir o par (ID, endereço) no índice direto (baseado em IDs).
			indiceDireto.create(ultimoID, endereco);
			// Incluir o par (chave secundária, ID) no índice indireto.
			indiceIndireto.create(entidade.chaveSecundaria(), ultimoID);
		} catch (Exception exception) {
			reportarExcecao("Erro ao inserir entidade!", exception);
			ultimoID = -1;
		}

		return ultimoID;
	}

	// Retorna a entidade com determinado ID, ou null caso o usuário não exista.
	public T read(int id) {
		T entidade = null;

		try {
			entidade = tConstructor.newInstance();

			// Buscar o endereço do registro da entidade no índice direto.
			// Retorna -1 no caso em que não encontrar.
			long endereco = indiceDireto.read(id); 

			// Mover o ponteiro do arquivo para o endereço recuperado.
			// Caso endereço seja -1, IOException vai ocorrer.
			arquivo.seek(endereco);

			// Ler lápide
			byte lapide = arquivo.readByte();

			if (lapide == 0) {
				// Ler tamanho
				short tamanhoEntidade = arquivo.readShort();
				
				byte[] vetorEntidade = new byte[tamanhoEntidade];
				// Ler vetor de bytes
				arquivo.read(vetorEntidade);

				entidade.fromByteArray(vetorEntidade);
			} else {
				// Usuário foi deletado.
				// Retornamos null.
				entidade = null;
			}
		} catch (Exception exception) {
			reportarExcecao("Erro ao tentar ler entidade!", exception);
			entidade = null;
		}

		return entidade;
	}

	public T read(String email) {
		T entidade = null;

		try {
			// Busca o ID no índice indireto, usando o valor retornado pelo método
			// chaveSecundaria()
			int id = indiceIndireto.read(email);

			// Só invocamos o read que usa ID
			entidade = read(id);
		} catch (IOException exception) {
			reportarExcecao("Erro ao tentar ler entidade!", exception);
			entidade = null;
		}

		return entidade;
	}

	// Atualiza a entidade. Retorna true em caso de sucesso ou false em caso de falha.
	public boolean update(T entidade) {
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

				// Coloca registro na lista de registros vazios.
				encadearRegistroVazio(endereco, tamanhoGravado);

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
		} catch (Exception exception) {
			reportarExcecao("Erro ao tentar atualizar entidade de id: " + entidade.getID(), exception);
			sucesso = false;
		}

		return sucesso;
	}

	public boolean delete(int id) {
		boolean sucesso = true;

		try {
			// Buscar endereço em índice direto.
			long endereco = indiceDireto.read(id);

			// Ir para endereço (pulando a lápide que gasta 1 byte)
			arquivo.seek(endereco);
			// Já marca lápide de uma vez.
			arquivo.writeByte(1);

			// Ler registro para ler chave secundária.
			short tamanhoRegistro = arquivo.readShort();
			byte[] registro = new byte[tamanhoRegistro];
			arquivo.read(registro);

			T entidade = tConstructor.newInstance();
			entidade.fromByteArray(registro);

			// Coloca registro na lista de registros vazios.
			encadearRegistroVazio(endereco, tamanhoRegistro);

			// Deletar a entrada do índice indireto usando chaveSecundária.
			indiceIndireto.delete(entidade.chaveSecundaria());
			// Deletar a entrada do índice direto usando ID.
			indiceDireto.delete(id);
		} catch (Exception exception) {
			reportarExcecao("Erro ao tentar deletar entidade de id: " + id, exception);
			sucesso = false;
		}

		return sucesso;
	}

	// Adiciona o registro vazio na lista encadeada de registros vazios.
	private void encadearRegistroVazio(long enderecoRegistro, short tamanhoRegistro) throws IOException {
		long registro = fetchPrimeiroRegistroVazio();
		// Se o cabeçalho não aponta pra nenhum registro vazio, a lista está vazia.
		if (registro == -1) {
			System.out.println("Cabeçalho aponta para registro -1.");
			setPrimeiroRegistroVazio(enderecoRegistro);
			// Pula a lápide e o tamanho do registro.
			arquivo.seek(enderecoRegistro + 3);
			// Escreve o endereço do pŕoximo registro vazio.
			arquivo.writeLong(-1);
		} else {
			System.out.println("Cabeçalho aponta para: " + String.format("0x%08X", registro));
			// Vai para o primeiro registro e pega seu tamanho.
			arquivo.seek(registro + 1); // Pula lápide.
			// Lê o tamanho dele.
			short tamanhoApontado = arquivo.readShort();

			// O nosso registro é menor do que o primeiro registro.
			if (tamanhoRegistro < tamanhoApontado) {
				System.out.println("Nosso registro é menor que o primeiro registro.");
				// O cabeçalho aponta para o registro sendo adicionado.
				setPrimeiroRegistroVazio(enderecoRegistro);
				// O nosso registro aponta para o, já não mais, primeiro registro.
				arquivo.seek(enderecoRegistro + 3); // Pula lápide e tamanho.
				arquivo.writeLong(registro);
			} else { // É maior do que o primeiro registro.
				System.out.println("Nosso registro NÃO é menor que o primeiro registro.");
				// registroAnterior é o primeiro registro.
				long registroAnterior = registro;
				// registro é o próximo registro. 
				registro = arquivo.readLong();

				// Enquanto não chegarmos ao fim da lista.
				while (tamanhoRegistro > tamanhoApontado && registro != -1) {
					arquivo.seek(registro + 1); // Pula lápide
					tamanhoApontado = arquivo.readShort();

					registroAnterior = registro;
					registro = arquivo.readLong();
				}

				// Saindo do loop, registro anterior é o registro que apontará para o registro sendo encadeado.
				System.out.println("Registro anterior ( " + String.format("0x%08X", registroAnterior) + ") apontará para: " + String.format("0x%08X", enderecoRegistro));
				arquivo.seek(registroAnterior + 3); // Pula lápide e o tamanho do registro.
				arquivo.writeLong(enderecoRegistro);

				// O nosso registro, deve apontar para o endereço que está em registro.
				System.out.println("Nosso registro ( " + String.format("0x%08X", enderecoRegistro) + ") apontará para: " + String.format("0x%08X", registro));
				arquivo.seek(enderecoRegistro + 3); // Pula lápide e o tamanho do registro.
				arquivo.writeLong(registro);
			}
		}
	}

	// Remove o registro vazio apontado pelo enderecoRegistro da lista encadeada de registros vazios.
	private void desencadearRegistroVazio(long enderecoRegistro) throws IOException {
		long registroAnterior = 4; // Registro anterior é na verdade o cabeçalho.
		long registro = fetchPrimeiroRegistroVazio(); // Endereço do primeiro registro vazio da lista.

		// Anda até o registro que será desencadeado para poder ter acesso ao registro anterior.
		while (registro != enderecoRegistro) {
			arquivo.seek(registro + 3); // Pula lápide e tamanho.
			
			registroAnterior = registro;
			registro = arquivo.readLong();
		}
		System.out.println("Registro que será desencadeado está sendo apontando por " + String.format("0x%08X", registroAnterior));

		// Vai no registro que será desencadeado e pega o endereço apontado por ele.
		arquivo.seek(enderecoRegistro + 3);
		long proximoRegistro = arquivo.readLong();
		System.out.println("Registro que será desencadeado está apontando para " + String.format("0x%08X", proximoRegistro));

		// Caso o registro anterior não continue sendo o cabeçalho, temos que ir para ele e pular a lápide + tamanho.
		// Se continuar sendo o cabeçalho, só fazemos um seek para a posição correta  (não aplicamos o offset).
		arquivo.seek(registroAnterior + ((registroAnterior != 4) ? 3 : 0));
		
		arquivo.writeLong(proximoRegistro);		
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

	// "Setta" o último ID usado no cabeçalho do arquivo.
	// O ponteiro do arquivo não sai do lugar onde estava quando a função é chamada.
	private void setUltimoIDUsado(int id) throws IOException {
		long endereco = arquivo.getFilePointer();

		arquivo.seek(0);
		arquivo.writeInt(id);

		arquivo.seek(endereco);
	}

	// Busca o endereço do primeiro registro vazio no cabeçalho do arquivo.
	// O ponteiro do arquivo não sai do lugar onde estava quando a função é chamada.
	private long fetchPrimeiroRegistroVazio() throws IOException {
		long endereco = arquivo.getFilePointer();

		arquivo.seek(4); // Offset para chegar nesse item do cabeçalho.
		long primeiroRegistroVazio = arquivo.readLong();

		arquivo.seek(endereco);

		return primeiroRegistroVazio;
	}

	// "Setta" o endereço do primeiro registro vazio no cabeçalho do arquivo.
	// O ponteiro do arquivo não sai do lugar onde estava quando a função é chamada.
	private void setPrimeiroRegistroVazio(long enderecoRegistro) throws IOException {
		long endereco = arquivo.getFilePointer();

		arquivo.seek(4); // Offset para chegar nesse item do cabeçalho.
		arquivo.writeLong(enderecoRegistro);

		arquivo.seek(endereco);
	}

	// Escreve a exceção ocorrida em um arquivo de log.
	private void reportarExcecao(String mensagem, Exception exception) {
		try {
			arquivoLog.write(
				"Exceção: " + mensagem + " Data: " +  LocalDateTime.now().toString() + "\n" +
				exception.toString() + "\n"
			);
			
			StackTraceElement[] elements = exception.getStackTrace();

			arquivoLog.write("======StackTrace======\n");
			for (StackTraceElement element : elements) {
				arquivoLog.write(element.toString() + "\n");
			}

			arquivoLog.write("\n\n");

			arquivoLog.flush();
		} catch(IOException e) { }
	}

	// Main feita só para testes.
	/*public static void main(String[] args) {
		try {

			CRUD crud = new CRUD("user");

			Scanner scanner = new Scanner(System.in);
			
			System.out.println("1 - Criar");
			System.out.println("2 - Ler ID");
			System.out.println("3 - Ler Chave Secundaria");
			System.out.println("4 - Atualizar");
			System.out.println("5 - Deletar");
			System.out.println("6 - Criar usuário de tamanho específico");
			System.out.println("7 - Sair");
			int opcao = scanner.nextInt();
			scanner.nextLine();

			while (opcao != 7) {
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
					case 6:
					{
						final int tamanhoMinimo = 10;

						System.out.println("Digite o tamanho do usuário: ");
						int tamanho = scanner.nextInt();
						scanner.nextLine();

						int tamanhoPreciso = tamanho - tamanhoMinimo;

						String email = new String();
						for (int i = 0; i < tamanhoPreciso; ++i)
							email += "a";

						
						Usuario usuario = new Usuario(0, "", email, "");

						int insertedID = crud.create(usuario);
						System.out.println("Usuário: " + usuario + " criado com ID: " + insertedID);

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
				System.out.println("6 - Criar usuário de tamanho específico");
				System.out.println("7 - Sair");
				opcao = scanner.nextInt();
				scanner.nextLine();
			}

			scanner.close();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}*/
}
