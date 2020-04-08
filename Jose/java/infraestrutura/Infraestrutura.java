/* José Guilherme de Castro Rodrigues 2020 */

package infraestrutura;

import java.lang.reflect.Array;
import java.util.ArrayList;

import entidades.*;
import ui.Resultado;

public class Infraestrutura {

	private CRUD<Usuario> crudUsuario;
	private CRUD<Sugestao> crudSugestao;
	private CRUD<Grupo> crudGrupo;
	private CRUD<Convite> crudConvite;

	private ArvoreBMais_Int_Int arvoreUsuarioSugestao;
	private ArvoreBMais_Int_Int arvoreUsuarioGrupo;
	private ArvoreBMais_Int_Int arvoreGrupoConvite;

	public Infraestrutura() throws Exception {
		crudUsuario = new CRUD<>("user", Usuario.class.getConstructor());
		crudSugestao = new CRUD<>("sugestao", Sugestao.class.getConstructor());
		crudGrupo = new CRUD<>("grupo", Grupo.class.getConstructor());
		crudConvite = new CRUD<>("convite", Convite.class.getConstructor());

		arvoreUsuarioSugestao = new ArvoreBMais_Int_Int(10, "dados/arvoreB.usuarioSugestao.idx");
		arvoreUsuarioGrupo = new ArvoreBMais_Int_Int(10, "dados/arvoreB.usuarioGrupo.idx");
		arvoreGrupoConvite = new ArvoreBMais_Int_Int(10, "dados/arvoreB.grupoConvite.idx");
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

	public CRUD<Convite> getCrudConvite() {
		return crudConvite;
	}

	public ArvoreBMais_Int_Int getArvoreUsuarioSugestao() {
		return arvoreUsuarioSugestao;
	}

	public ArvoreBMais_Int_Int getArvoreUsuarioGrupo() {
		return arvoreUsuarioGrupo;
	}

	public ArvoreBMais_Int_Int getArvoreGrupoConvite() {
		return arvoreGrupoConvite;
	}

	public <K extends Entidade, V extends Entidade> Resultado listarRelacao1N(K entidade, CRUD<V> crud, ArvoreBMais_Int_Int arvoreRelacao) {
		Resultado resultado = new Resultado();

		try {
			// Lê todas entidades V relacionadas a entidade K.
			int ids[] = arvoreRelacao.read(entidade.getID());
			
			ArrayList<V> vs = new ArrayList<>(ids.length);

			for (int id : ids) {
				vs.add(crud.read(id));
			}

			resultado.setObjeto(vs);
		} catch (Exception exception) {
			resultado.setErro("Ocorreu um erro ao tentar ler as relações.");
		}
		
		return resultado;
	}
}
