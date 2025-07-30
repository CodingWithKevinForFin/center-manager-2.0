####

This is the Wafra JDBC Client Project that is used to backup AMI to Wafra's Data Warehouse

####

Wafra usings Windows to run AMI so we need to make sure they update their scripts to point to the correct class path.
We also need to compile the project for them in Java 8.
To compile you need the out.jar and autocode.jar from their version of AMI placed in the lib directory
They will have a custom configuration so when updating we need make sure they retain their existing properties and existing start scripts.
Recommended they backup any existing project before updating in case we need to revert.

