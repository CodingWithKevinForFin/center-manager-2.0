# Security Warnings

We strongly recommend securing your 3forge instances, especially for production environments. For in-depth guides on securing your applications, please see the 'Authentication' and 'Encryption' sections of our [documentation](../architecture/index.md), otherwise to quickly secure your 3forge applications, see [this](../cheatsheets/security.md) guide.

Some options may require additional configuration or will throw errors. The most commonly encountered security warnings and how to resolve them are listed below.  

## Common Security Warnings

### AES Strength Too Low 
:  

    **Reason**: Occurs when using a bit depth lower than 256 for encrypting information.  
    **Resolve**: Set the AES strength for encryption to 256 by updating your `local.properties` with the following:  

    ```
    ami.aes.key.strength=256
    ```

    More applications of this encryption setting can be found [here](../encryption/properties_files.md/).

### AES-Strength Mismatch  
:  

    **Reason**: Occurs when there is a discrepancy between the expected and actual key strengths used in encryption or decryption operations. The AES key file **must** match the bit depth configured in `local.properties`.  
    **Resolve**: Update `local.properties` to match the bit depth of the AES key file. Alternatively, use an AES key file with matching bit depth. For example, for a 256 bit AES key:

    ```
    # Update to match with AES file
    ami.aes.key.strength=256
    ```

### Do Not Call Peek and Clear  
:  
    **Reason**: This warning is logged when `peekAndClear` is called on a password field. This method is meant to only be called for debugging, and should be avoided in production use cases.
    **Resolve**: Remove or comment out `peekAndClear` when not debugging.

### Non-Encrypted Passwords Stored in access.txt  
:  
    **Reason**: Occurs when non-encrypted passwords are stored in `access.txt`, such as the default `demo|demo123`.
    **Resolve**: Follow the user encryption [guide](https://doc.3forge.com/encryption/user_passwords/) to encrypt user passwords. These values are then stored in the password fields in the `access.txt` file.

### RSA Strength for SAML Configuration  
:  
    **Reason**: Either no identity provider certificate has been configured for the user, or the key strength of the SAML certificate is too low.  
    **Resolve**: If no identity provider certificate has been configured for the user, create one, then in `local.properties` set the RSA key strength appropriately:  

    ```
    # Set to a minimum of 2048 or larger
    saml.identity.provider.nocert.rsa.key.strength=3072
    ``` 

### HTTP/HTTPS Whitelist  
:   
    **Reason**: HTTP/HTTPS configurations have not been correctly configured.
    **Resolve**: Check the web [configuration guide](https://doc.3forge.com/configuration_guide/web/#http-connection) for `local.properties` configuration options enabling HTTP/HTTPS connection.

### Update CORS Policy  
:  
    **Reason**: All origins are allowed as part of the CORS policy -- your `local.properties` file likely has the following property: `ami.web.permitted.cors.origins=*`  
    **Resolve**: Modify the approved list of CORS origins from `*` to a delimited list of approved origins, such as:

    ```
    ami.web.permitted.cors.origins=http://myhost.com|http://thathost.com
    ```

### CORS Origin is Blocked 
:  
    **Reason**: The accessed resource does not have the request's origin in its CORS policy (e.g, accessing multiple AMI dashboards with different users within the same domain).  
    **Resolve**: Ensure the request's origin host is in the CORS Policy for the accessed resource in the `local.properties` of that center:  

    ```
    ami.web.permitted.cors.origins=http://myhost.com|http://thathost.com
    ```