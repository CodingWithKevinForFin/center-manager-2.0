# SSL

Here's some guidance on how to troubleshoot the following SSL-related issues, such as **No available authentication scheme**, **Unsupported or unrecognized SSL message**, or **SSL Protocol Error**.

In our experience these issues are not caused by 3forge AMI but are generic error messages indicating that there was a problem in how the certificate was generated.

Here is our recommended procedure for the generation:  

1. Download the root keystore for your environment (Should not matter if using jks or pem)

	```sh
	keytool -importkeystore -srckeystore cacerts.jks -destkeystore web.keystore
	```
	
	Or
	
	```sh
	keytool -import -file cacert.pem -keystore web.keystore
	```

1. Generate Certificate Signing Request (CSR) - Modifies keystore.

	Generate the certificate: 

	```sh
	keytool -genkeypair -keystore web.keystore -alias server -keyalg RSA -keysize 2048 -sigalg SHA256withRSA -dname "CN=...,OU=...,O=C=..."
	```
	Then generate a request through your Certificate Authority (CA): 

	```sh
	keytool -certreq -alias server -file server.csr -keystore web.keystore
	```
	
	!!!Note
		keypass and storepass should match `web.keystore.password`, source password is the cacerts password.



1. Import the certificate into the keystore  

	```sh
	keytool -import -v -trustcacerts -alias root -keystore web.keystore -file cert.cer -keypass [pass] -storepass [pass]
	```

1. In our experience the following command didn't work and was the cause of the above SSL-related errors  

	```sh
	keytool -importcert -keystore web.keystore -alias server -file cert.cer
	```

