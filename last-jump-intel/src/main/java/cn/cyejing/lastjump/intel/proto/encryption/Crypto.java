package cn.cyejing.lastjump.intel.proto.encryption;

import java.io.ByteArrayOutputStream;

public interface Crypto {
	
	void encrypt(byte[] data, ByteArrayOutputStream stream);

	void encrypt(byte[] data, int length, ByteArrayOutputStream stream);

	void decrypt(byte[] data, ByteArrayOutputStream stream);

	void decrypt(byte[] data, int length, ByteArrayOutputStream stream);

}
