/* José Guilherme de C. Rodrigues - 13/02/2020 */
/* Here, an array of bytes is considered a std::string.
 * This means that whenever you get the bytes, you are actually getting an std::string object.
 * If you want to write them to a file, you must .c_str() to get the array itself and use .length()
 * of the std::string object.
 * The endianess of numbers written to a bytestream will depend on the native endianess of the
 * system.
 * Remember, here, a std::string is an array of bytes! */
#ifndef __bytestream_hh__
#define __bytestream_hh__

#include <iostream>
#include <sstream>

class bytestream {
	std::stringstream m_stream;
public:
	bytestream();
	explicit bytestream(const std::string &bytes);

	template<typename T> void write(const T &t);
	void write_string(const std::string &str);

	template<typename T> T read();
	std::string read_string();

	std::string get_bytes() const;
};

template<typename T> void bytestream::write(const T &t) {
	static_assert(std::is_arithmetic<T>::value, "T is not writeable.");

	m_stream.write(reinterpret_cast<const char *>(&t), sizeof(t));
}

template<typename T> T bytestream::read() {
	static_assert(std::is_arithmetic<T>::value, "T is not readable.");

	T t;
	m_stream.read(reinterpret_cast<char *>(&t), sizeof(t));

	return t;
}

#endif
