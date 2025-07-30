# Properties Files

Values in properties files can be encrypted. Let's say you have a property `myurl` in your local.properties which depends on a password and you would like to encode the password `Password123456`.

```
myurl=http://blah?pass=Password123456&user=steve
```

1. Under the scripts directory, use the `tools.sh` (`tools.bat` for Windows) to create a 256-bit strength key (feel free to adjust the strength).

	=== "Linux"
	
		```sh
		./tools.sh --aes_generate /opt/ami/secret.aes 256
		```
	
	=== "Windows"
	
		```sh
		./tools.bat --aes_generate /opt/ami/secret.aes 256
		```

1. Add this option to your start.sh so that AMI knows where the secret key for decoding encrypted properties is located

	=== "Linux"
	
		```
		-Dproperty.f1.properties.secret.key.files=/opt/ami/secret.aes
		```

	=== "Windows"
	
		```
		-Dproperty.f1.properties.secret.key.files=C:/ami/amione/secret.aes
		```

1. Using the same tools.sh, Encode the password and copy the output text into your clipboard

	=== "Linux"
	
		```
		./tools.sh --aes_encrypt /opt/ami/secret.aes Password123456
		```

	=== "Windows"
	
		```
		./tools.bat --aes_encrypt C:/ami/amione/secret.aes Password123456
		```
	
	Example encoded password:

	```
	fYjHz8Dr4o7XjZcOd1BhtKzV9U5MpZMpTyGlu-mpheL4qV-ZX-yUads
	```

1. Change your local.properties file to use the encrypted value instead, by enclosing with $*{CIPHER: ... }*

	```
	myurl=http://blah?pass=${CIPHER:fYjHz8Dr4o7XjZcOd1BhtKzV9U5MpZMpTyGlu-mpheL4qV-ZX-yUads}&user=steve
	```

1. Restart AMI

In the AmiOne.log you'll see that the value for the `myurl` property is substituted with \*\*\*\*\* for security purposes.

You can see the password listed in the user interface by going to Dashboard -\> Session Variables. You can also decrypt using `tool.sh` by running:

=== "Linux"

	```
	./tools.sh --aes_decrypt /opt/ami/secret.aes fYjHz8Dr4o7XjZcOd1BhtKzV9U5MpZMpTyGlu-mpheL4qV-ZX-yUads
	```

=== "Windows"

	```
	./tools.bat --aes_decrypt C:/ami/amione/secret.aes fYjHz8Dr4o7XjZcOd1BhtKzV9U5MpZMpTyGlu-mpheL4qV-ZX-yUads
	```

!!! note

    If you see the following error: `Error: Could not find or load main class` please modify the tools script to point to `com.f1.utils.encrypt.EncrypterTool` or `com.f1.encrypt.EncrypterTool`
