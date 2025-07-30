# LDAP

The LDAP plugin is available upon request. Please reach out via support@3forge.com

-   `ami.auth.plugin.class`: auth class name, should be set to com.f1.ami.amildap.AmiAuthenticatorLDAP
-   `ldap.address`: address of ldap server. Ex: ldap.example.com
-   `ldap.port`: ldap port, default is 389
-   `ldap.bindDN`: admin bind DN. Ex: cn=admin,dc=example,dc=com
-	`ldap.baseDN`: base DN. Ex: ou=group,dc=example,dc=com
-   `ldap.bindPW`: admin password
-   `ldap.attributes`: Optional. Comma delimited attributes to search for in ldap (i.e. cn,givenName,sn,mail)
-   `ldap.userAttributeName`: attribute to identify user object (i.e. sAMAccountName)
-	`ldap.maxConnections`: Optional. Maximum number of connections (defaults to 10)
-	`ldap.initialConnections`: Optional. Initial number of connections (defaults to 5)
-	`ldap.useSSL`: Optional. Specifies that ssl should be used for all connections (defaults to false)
-	`ldap.useStartTLS`: Optional. Specifies that the StartTLS protocol should be used, requires useSSL to be true
-	`ldap.sslTrustStorePath`: Optional. Path to the truststore cert (for ssl)
-	`ldap.sslTrustStorePin`: Optional. Pin for the truststore cert (for ssl)
-	`ldap.sslTrustStoreFormat`: Optional. Format of the truststore cert (valid inputs are: all,pem. Defaults to JVM defaults otherwise)
-	`ldap.sslValidateHostname`: Optional. Boolean indicating if the hostname should be validated. (Defaults to true)