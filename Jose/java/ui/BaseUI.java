package ui;

import infraestrutura.Infraestrutura;

public abstract class BaseUI {

	protected Infraestrutura infraestrutura;

	public BaseUI(Infraestrutura infraestrutura) {
		this.infraestrutura = infraestrutura;
	}
}