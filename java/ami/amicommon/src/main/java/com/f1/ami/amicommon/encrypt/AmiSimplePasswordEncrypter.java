package com.f1.ami.amicommon.encrypt;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiCommonProperties;
import com.f1.container.ContainerTools;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;

public class AmiSimplePasswordEncrypter extends AmiAbstractPasswordEncrypterPlugin {
	public static Logger log = LH.get();
	private static final Charset DEFAULT_CHAR = StandardCharsets.UTF_16LE;
	private Charset charset;

	public AmiSimplePasswordEncrypter() {
		charset = DEFAULT_CHAR;
	}

	@Override
	public void init(ContainerTools tools, PropertyController props) {
		super.init(tools, props);

		String charsetName = props.getOptional(AmiCommonProperties.PROPERTY_AMI_PASSWORD_ENCRYPTER_CHARSET, "");
		if (SH.is(charsetName)) {
			this.charset = Charset.forName(charsetName);
			if (this.getCharset() == null) {
				String msg = "Simple Password encrypter failed to initialize charset of name: " + charsetName;
				LH.warning(log, msg);
				throw new RuntimeException(msg);
			}
		} else {
			LH.info(log, "Simple Password Encrypter is using default charset ", charset.name());
		}

	}

	@Override
	public String getPluginId() {
		return "SimplePasswordEncrypterPlugin";
	}
	@Override
	public String decryptString(String encrypted) {
		if (encrypted == null)
			return null;
		return new String(decrypt(encrypted), this.getCharset());
	}

	@Override
	public byte[] decrypt(String encrypted) {
		if (encrypted == null)
			return null;
		return decryptBytes(encrypted.getBytes(this.getCharset()));

	}

	@Override
	public String encryptString(String unencrypted) {
		if (unencrypted == null)
			return null;
		return encrypt(unencrypted.getBytes(this.getCharset()));
	}

	@Override
	public String encrypt(byte[] unencrypted) {
		if (unencrypted == null)
			return null;
		byte[] data = encryptBytes(unencrypted);
		return new String(data, this.getCharset());
	}

	private byte[] decryptBytes(byte[] bytes) {
		byte[] data = bytes.clone();
		for (int i = 0; i < data.length; i++)
			data[i] ^= 0xffff;
		return data;
	}
	private byte[] encryptBytes(byte[] bytes) {
		byte[] data = bytes.clone();
		for (int i = 0; i < data.length; i++)
			data[i] ^= 0xffff;
		return data;
	}

	public Charset getCharset() {
		return charset;
	}
	public static void main(String[] args) {
		AmiSimplePasswordEncrypter enc = new AmiSimplePasswordEncrypter();
		String s = "asdfghjk ASDFGHJKL 1234567890 !@#$%^&*() -= _+ ;' :\"";

		String es = enc.encryptString(s);
		String ds = enc.decryptString(es);
		System.out.println(ds);
		OH.assertEq(s, ds);
	}

}
