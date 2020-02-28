/* José Guilherme de Castro Rodrigues - 19/02/2020 */
import java.io.File;
import java.io.RandomAccessFile;

public class CRUD {

	private final String DIRETORIO = "dados";

	private RandomAccessFile arquivo;
	private HashExtensivel indiceDireto;
	private ArvoreBMais_String_Int indiceDireto;

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

	public int create(Usuario usuario) {
		return 0;
	}

	public Usuario read(int id) {
		return null;
	}

	public boolean update(Usuario usuario) {
		return false;
	}

	public boolean delete(int id) {
		return false;
	}
}
