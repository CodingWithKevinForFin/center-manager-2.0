# Tables

This section details how to create encrypted AMI tables, including system tables. Adding decrypters on existing encrypted tables is also supported.

## Creating a Encrypted User Table

To create an encrypted user table, add `#!amiscript persist_encrypter="default"` to the `#!amiscript CREATE TABLE` statement containing `#!amiscript PersistEngine`. This will encrypt the data stored for the persistent user table. By default the encrypter should be "default". Example:

```amiscript
CREATE PUBLIC TABLE MyTable(c1 Integer, c2 Short, c3 Long, c4 Double, c5 Character, c6 Boolean, c7 String) USE PersistEngine="FAST" persist_encrypter="default" RefreshPeriodMs="100"
```

-   `persist_encrypter`(required): name of encrypter

## Adding Decrypters

To add decrypters, add the following option to the `start.sh` file (found in `amione/scripts`):

```
-Dproperty.f1.properties.decrypters=DECRYPTER_NAME=package.ClassName
```

-   `DECRYPTER_NAME`: name of decrypter

-   `ClassName`: name of decrypter Java class

## Encrypting System Tables

To encrypt system tables, add either of the following lines to the local.properties file (found in `amione/config`):

```
ami.db.persist.encrypter.system.tables=default
```

OR

```
ami.db.persist.encrypter.system.table._DATASOURCE=[encryptername]
```

-   `encryptername`: name of encrypter used

**NOTE**: The old unencrypted `.dat` persist files will not be deleted after encryption, and will be appended with `.deleteme`

