# User Passwords

3forge recommends using the access.txt Authentication (AmiAuthenticatorFileBacked Authentication Plugin) only for demo purposes, for production environments we recommend implementing a AmiAuthenticator Plugin. By default, AMI does not encrypt passwords in the access.txt. This document details the steps for encrypting passwords in your access.txt.

1. Ensure your 3forge AMI Application is running. Your access.txt by default would be located in your data directory.

	![](../resources/legacy_mediawiki/Files_in_AMI_data_folder.png "Files_in_AMI_data_folder.png")

1. This document will contain a list of all users and their passwords as well as their permissions.

	![](../resources/legacy_mediawiki/Access_file.png "Access_file.png")

1. To start encrypting the passwords you will need to generate the encrypted string for each password. To do so you will need to telnet to your ami.db.console.port whose default value is 3290 and login to a user with DB permissions.

	![](../resources/legacy_mediawiki/Putty_Configuration.png "Putty_Configuration.png")

	![](../resources/legacy_mediawiki/PuTTY_Login.png "PuTTY_Login.png")

1. To encrypt each password, run the AMIScript method on the console strEncrypt("your-password"). This command will return your encrypted password.

	![](../resources/legacy_mediawiki/Encryption_Procedure.png "Encryption_Procedure.png")

1. For each password in the access.txt update the password with the encrypted value.

	![](../resources/legacy_mediawiki/Passwords_Updating.png "Passwords_Updating.png")

1. Add a new property in a local.properties or create one in your config directory. In the file add the following property: users.access.file.encrypt.mode=password

	![](../resources/legacy_mediawiki/Local.properties.png "Local.properties.png")

1. The final step is to restart your 3forge AMI Application

1st Note: Store the amikey.aes securely so that you have a recovery mechanism setup for this in case of data loss. This key by default is set by the property and configured with: ami.aes.key.file=persist/amikey.aes. If lost, users will not be able to login to their accounts.

2nd Note: To change the the access.txt file set the property: users.access.file=pathToYourAccess.txt

