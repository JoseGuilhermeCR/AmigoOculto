/* José Guilherme de C. Rodrigues - 12/02/2020 */
/* Observações: reinterpret_cast<char *> vai resultar em valores binários
 * representandos na endianess nativa do sistema, nesse caso, little endian.
 * read_string, write_string podem ser funções em um header separado.
 * Também seria interessante esconder todas essas escritas para o stringstream
 * dentro de outro header. */
#include "usuario.hh"
#include <sstream>
#include <fstream>
#include <iostream>

Usuario::Usuario(unsigned id, const std::string &nome,
	       	const std::string &email, const std::string &senha)
	:
	m_id(id),
	m_nome(nome),
	m_email(email),
	m_senha(senha)
{
}

void Usuario::setID(unsigned id) {
	m_id = id;
}

void Usuario::setNome(const std::string &nome) {
	m_nome = nome;
}

void Usuario::setEmail(const std::string &email) {
	m_email = email;
}
void Usuario::setSenha(const std::string &senha) {
	m_senha = senha;
}

unsigned Usuario::getID() const {
	return m_id;
}

const std::string &Usuario::getNome() const {
	return m_nome;
}

const std::string &Usuario::getEmail() const {
	return m_email;
}

const std::string &Usuario::getSenha() const {
	return m_senha;
}

void Usuario::fromByteArray(const std::string &bytes) {
	std::stringstream byteStream(bytes);

	auto read_string = [&byteStream] (std::string &str) {
				unsigned short tmpLength = 0;
				byteStream.read(reinterpret_cast<char *>(&tmpLength),
						sizeof(tmpLength));

				char c;
				for (unsigned short i = 0; i < tmpLength; ++i) {
					byteStream.read(&c, sizeof(c));
					str += c;
				}
			};

	byteStream.read(reinterpret_cast<char *>(&m_id), sizeof(m_id));

	read_string(m_nome);
	read_string(m_email);
	read_string(m_senha);

}

std::string Usuario::toByteArray() const {
	std::stringstream byteStream;

	byteStream.write(reinterpret_cast<const char *>(&m_id), sizeof(m_id));

	auto write_string = [&byteStream] (const std::string &str) {
				unsigned short tmpLength = str.length();
				byteStream.write(reinterpret_cast<char *>(&tmpLength),
						 sizeof(tmpLength));
				byteStream.write(str.c_str(), tmpLength);
			};

	write_string(m_nome);
	write_string(m_email);
	write_string(m_senha);

	return byteStream.str();
}

std::ostream &operator<<(std::ostream &os, const Usuario &usuario) {
	os << usuario.m_id << "|" << usuario.m_nome << "|" << usuario.m_email;
	os << "|" << usuario.m_senha;
	return os;
}

int main() {
	/* SÓ PARA TESTES */
	std::fstream file("user.db", file.binary | file.trunc | file.out | file.in);

	Usuario user(1, "José Guilherme de C. Rodrigues", "joseguilhermebh@hotmail.com", "22");

	std::string array = user.toByteArray();
	file.write(array.c_str(), array.length());

	file.seekp(0);

	Usuario user1;

	std::string bytes;
	char c;
	for (unsigned short u = 0; u < 70; ++u) {
		file.read(&c, sizeof(c));
		bytes += c;
	}

	user1.fromByteArray(bytes);

	std::cout << user1 << std::endl;

	file.close();

	return 0;
}
