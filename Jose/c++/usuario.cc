/* José Guilherme de C. Rodrigues - 12/02/2020 */
#include "usuario.hh"
#include "bytestream.hh"
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
	bytestream stream(bytes);

	m_id = stream.read<decltype(m_id)>();
	m_nome = stream.read_string();
	m_email = stream.read_string();
	m_senha = stream.read_string();
}

std::string Usuario::toByteArray() const {
	bytestream stream;

	stream.write<decltype(m_id)>(m_id); // unsigned int.
	stream.write_string(m_nome);
	stream.write_string(m_email);
	stream.write_string(m_senha);

	return stream.get_bytes();
}

std::ostream &operator<<(std::ostream &os, const Usuario &usuario) {
	os << usuario.m_id << "|" << usuario.m_nome << "|" << usuario.m_email;
	os << "|" << usuario.m_senha;
	return os;
}

int main() {
	/* Some tests */
	std::fstream file("user.db", file.binary | file.trunc | file.out | file.in);

	Usuario user(1, "José Guilherme de C. Rodrigues", "joseguilhermebh@hotmail.com", "22");

	// Write test.
	// Gets user byte array.
	std::string array = user.toByteArray();

	bytestream stream;
	// Writes the byte array into the stream preceding it by it's size.
	stream.write_string(array);
	// Write the user into the file.
	file.write(stream.get_bytes().c_str(), stream.get_bytes().length());

	// Rewind...
	file.seekp(0);

	// Let's do a reading test.
	// Reading won't be a pain once we have a file manager, but... for now...
	unsigned short userLength;
	file.read(reinterpret_cast<char *>(&userLength), sizeof(userLength));

	std::string userBytes;
	char c;
	for (unsigned short u = 0; u < userLength; ++u) {
		file.read(&c, sizeof(c));
		userBytes += c;
	}

	Usuario user1;
	user1.fromByteArray(userBytes);

	std::cout << user1 << std::endl;

	file.close();

	return 0;
}
