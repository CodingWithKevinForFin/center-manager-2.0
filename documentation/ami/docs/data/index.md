# How is data brought into AMI?

There are four common ways data can be transferred in and out of AMI:

1. **Datasource Adapters**, used to connect AMI to external database, AMI sends queries and receives responses. These are configured through the GUI.

	- Use Case: Pulling data from and pushing data to an Oracle database

1. **Feed Handlers**, used to connect AMI to external data feeds, AMI receives data sent over the feed. These are configured through the `local.properties` files.

	- Use Case: Receiving data from a KDB stream

1. **Clients**, custom clients for sending data into AMI, AMI receives data sent by the client and can send commands to the client. Clients are written in Java, Python or .NET

	- Use Case: Receiving data from another application and sending instructions

1. **JDBC Connection**, a standard database connection port that other applications can use to send data into AMI. Configured through the `local.properties` files

	- Use Case: Communicating with another Java application

!!! Note

	The documentation only provides guides for some of the more complex Datasource Adapters and Feed Handlers, for an exhaustive list see the [Supported Software](../supported_software.md) list.

Additionally, data that has been sent into AMI before from a Feed Handler or Client can be replayed using the [Replay Plugin](./replay.md) and an `AmiMessages.log` file.