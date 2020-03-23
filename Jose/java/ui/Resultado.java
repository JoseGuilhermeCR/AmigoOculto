/* José Guilherme de Castro Rodrigues 2020 */

package ui;

public class Resultado {
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