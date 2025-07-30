# Security

This page shows the most basic actions required to make a 3forge environment more secure. If you are encountering specific security warnings, check [here](../troubleshooting/security.md) to see if you have encountered some common issues. Otherwise, please contact at us at support@3forge.com for additional help.

## Login credentials

You can find the default credentials to log in to AMI in the `amione/data/access.txt` file. Until other forms of authentication can be set up, we recommend changing to something more secure using the following format:

```
# USERNAME|PASSWORD|Key1=Value1|Key2=Value2|.... 
demo|demo123|ISADMIN=true|ISDEV=true|
test|test123|
```

For production environments, please follow [these instructions](../encryption/user_passwords.md) to encrypt your user login information, or use a different authentication system.


## Set Up HTTPS connection

By default, the AMI frontend is accessible through a HTTP connection. This is insecure as it is not encrypted. The following steps will explain how to set up a secure HTTPS connection:

1. Ensure that your certificate truststore (for example `cacerts`) is up to date with the appropriate server certificates. See Oracle's guide on using [keytool](https://docs.oracle.com/cd/E19798-01/821-1841/6nmq2cpjv/index.html) to generate certificates and adding them to your truststore. 

2. Generate SSL keys for 3forge using [these instructions](../architecture/advanced_setup.md/#instructions-for-ssltls)


3. Set the following properties:

	```
	https.port=3333
	https.keystore.password=YOUR_PASSWORD
	https.keystore.file=PATH_TO_YOUR_web.keystore_FILE
	```

## Remove Shell, SSH, and SFTP adapters

The Shell, SSH, and SFTP adapters are powerful tools that give developers great flexibility; but in the wrong hands they can provide privileged access to the server hosting AMI. If these adapters are not being used, we recommend to remove them by setting the following property:

```
ami.datasource.plugins=com.f1.ami.plugins.mysql.AmiMysqlDatasourcePlugin,\
                com.f1.ami.center.ds.AmiKxDatasourcePlugin,\
                com.f1.ami.center.ds.AmiFlatFileDatasourcePlugin,\
                com.f1.ami.center.ds.AmiAmiDbDatasourcePlugin,\
                com.f1.ami.center.ds.AmiGenericJdbcDatasourcePlugin,\
                com.f1.ami.plugins.postgresql.AmiPostgresqlDatasourcePlugin,\
                com.f1.ami.plugins.oracle.AmiOracleDatasourcePlugin,\
                com.f1.ami.plugins.excel.AmiExcelDatasourcePlugin,\
                com.f1.ami.plugins.db2.AmiDb2DatasourcePlugin,\
                com.f1.ami.center.ds.AmiQuandlDatasourcePlugin,\
                com.f1.ami.center.ds.AmiFredDatasourcePlugin,\
                com.f1.ami.center.ds.AmiOneTickDatasourcePlugin,\
                com.f1.ami.plugins.restapi.AmiRestAPIDatasourcePlugin,\
                com.f1.ami.plugins.sqlite.AmiSqLiteDatasourcePlugin
```

!!!note

	The default list of available datasource plugins can be found in `amione/config/default.properties`