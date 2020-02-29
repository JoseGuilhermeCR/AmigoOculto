/* José Guilherme de C. Rodrigues - 13/02/2020 */
#include "bytestream.hh"

bytestream::bytestream()
{
}

bytestream::bytestream(const std::string &bytes)
	:
	m_stream(bytes)
{
}

/* This function will write a std::string into the bytestream.
 * This means that this function can write a std::string, intended for utf-8 uses,
 * or an array of bytes, inside the str, into the stream. */
void bytestream::write_string(const std::string &str) {
	/* May be changed later on, for now, std::string will be able to have
	 * up to 65535 bytes.*/
	unsigned short length = str.length();
	// Write the length of str.
	write<unsigned short>(length);
	// Now write the string.
	m_stream.write(str.c_str(), length);
}

std::string bytestream::read_string() {
	unsigned short length = read<unsigned short>();

	std::string str;
	for (unsigned short u = 0; u < length; ++u)
		str += read<char>();

	return str;
}

std::string bytestream::get_bytes() const {
	return m_stream.str();
}
