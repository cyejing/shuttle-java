package cn.cyejing.shuttle.common.encryption;

import lombok.Getter;

public abstract class CryptoBase implements Crypto {

	@Getter
	protected final String name;
	@Getter
	protected final String password;

	public CryptoBase(String name, String password) {
		this.name = name.toLowerCase();
		this.password = password;
	}

	@Override
	public abstract byte[] encrypt(byte[] data);

	@Override
	public abstract byte[] decrypt(byte[] data);
}
