/* José Guilherme de Castro Rodrigues 2020 */

package infraestrutura;

import entidades.*;

public class Infraestrutura {

	private CRUD<Usuario> crudUsuario;
	private CRUD<Sugestao> crudSugestao;
	private CRUD<Grupo> crudGrupo;

	private ArvoreBMais_Int_Int arvoreUsuarioSugestao;
	private ArvoreBMais_Int_Int arvoreUsuarioGrupo;

	public Infraestrutura() throws Exception {
		crudUsuario = new CRUD<>("user", Usuario.class.getConstructor());
		crudSugestao = new CRUD<>("sugestao", Sugestao.class.getConstructor());
		crudGrupo = new CRUD<>("grupo", Grupo.class.getConstructor());

		arvoreUsuarioSugestao = new ArvoreBMais_Int_Int(10, "dados/arvoreB.usuarioSugestao.idx");
		arvoreUsuarioGrupo = new ArvoreBMais_Int_Int(10, "dados/arvoreB.usuarioGrupo.idx");
	}

	public CRUD<Usuario> getCrudUsuario() {
		return crudUsuario;
	}

	public CRUD<Sugestao> getCrudSugestao() {
		return crudSugestao;
	}

	public CRUD<Grupo> getCrudGrupo() {
		return crudGrupo;
	}

	public ArvoreBMais_Int_Int getArvoreUsuarioSugestao() {
		return arvoreUsuarioSugestao;
	}

	public ArvoreBMais_Int_Int getArvoreUsuarioGrupo() {
		return arvoreUsuarioGrupo;
	}
}