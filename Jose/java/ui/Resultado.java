/* José Guilherme de Castro Rodrigues 2020 */

package ui;

public class Resultado {
	private String _mensagem;
	private boolean _valido;

	// Usado em casos que é necessário voltar alguma coisa junto com o resultado.
	private Object _objeto;

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
		_objeto = null;
	}

	public void setObjeto(Object objeto) {
		_objeto = objeto;
	}

	public Object getObjeto() {
		// Dessa forma, o objeto não ficará mais no resultado depois de ser pego por alguém.
		Object tmp = _objeto;

		_objeto = null;

		return tmp;
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