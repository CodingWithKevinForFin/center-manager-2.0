# Custom Encryption

## Overview

This plugin is used to avoid storing plain text passwords inside properties files. Use the Decrypter interface to implement a customized methodology for decrypting/retrieving data. After creating a class that implements the `#!java com.f1.utils.encrypt.Decrypter` interface, add the full class name to the `-Df1.properties.decrypters` property. As a result, where ever the `${CIPHER:xxx}` syntax within `.properties` files is encountered the `xxx` will be passed to the `decryptString(...)` method. If the decryptString is unable to process the `xxx`, then simply return null or throw an exception.

Note: Instead of using the Decrypter plugin, you can instead use the tools.sh in conjunction with the `-Df1.properties.secret.key.files` to encrypt tokens manually.

``` java
package com.f1.ami.relay;

package com.f1.utils.encrypt;
public interface Decrypter {
  String decryptString(String encrypted);
  byte[]decrypt(String encrypted);
}
```

## Example

``` java
package com.example;
import com.f1.utils.encrypt.Decrypter;

//Add to your java vm arguments: -Df1.properties.decrypters=com.example.MyDecrypter
public MyDecrypter implements Decrypter {

  //Normally this would be more sophisticated, like reaching out to a secure vault, etc.
  public String decryptString(String encrypted){
    if("secretPass".equals(encrypted))
      return "password123";
    return null;
  }
  public  byte[] decrypt(String encrypted){
    String s=decryptString(encrypted);
    return s==null ? null : s.getBytes();
  }
}

// Now, if you put this inside your properties, some.password will be set to password123:
//   some.password=${CIPHER:secretPass}
```