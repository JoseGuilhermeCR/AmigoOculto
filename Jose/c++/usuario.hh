/* José Guilherme de C. Rodrigues - 12/02/2020 */
#ifndef __USUARIO_HH__
#define __USUARIO_HH__

#include <ostream>

class Usuario {
	unsigned m_id;
	std::string m_nome;
	std::string m_email;
	std::string m_senha;
public:
	Usuario() = default;
	explicit Usuario(unsigned id, const std::string &nome = "", const std::string &email = "", const std::string &senha = "");

	void setID(unsigned id);
	void setNome(const std::string &nome);
	void setEmail(const std::string &email);
	void setSenha(const std::string &senha);

	unsigned getID() const;
	const std::string &getNome() const;
	const std::string &getEmail() const;
	const std::string &getSenha() const;

	void fromByteArray(const std::string &bytes);
	std::string toByteArray() const;

	friend std::ostream &operator<<(std::ostream &os, const Usuario &usuario);
};

#endif
