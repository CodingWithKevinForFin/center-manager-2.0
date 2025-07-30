# Python

## Setup

The python library files are available upon request - please contact support@3forge.com to receive the latest version of the library.  
This library requires the [`chardet`](https://pypi.org/project/chardet/) library, and it can be installed via pip: `pip install chardet`.

### Overview

The python library provides an interface identical to Java and can be easily configured.

### Example - Python Code

``` python
from com.f1.ami.client import AmiClient
from com.f1.ami.listener import AmiClientListenerInterface
from com.f1.utils.options import Options

class TestClient(AmiClientListenerInterface):
    "A script demonstrating how this client can be used to connect to AMI"
    def __init__(self) -> None:
        super().__init__()
        
        ad = AmiClient()
        options = Options.ENABLE_SEND_SEQNUM + Options.ENABLE_SEND_TIMESTAMPS
        ad.addListener(self)

        # ad.setDebugMessages()

        # Connecting to the AMI instance, replace with appropriate connection parameters
        ad.start("127.0.0.1", 3289, "demo", options)
        
        # Sending a row of data to the table 'Sample'
        ad.startObjectMessage("Sample")
        ad.addMessageParamString("Symbol", "GBP")
        ad.addMessageParamFloat("Quantity", 11.0)
        ad.sendMessageAndFlush()
        
        # print(ad.getOutputBuffer())
        
        ad.close()

    def onMessageSent(self, client: AmiClient, message: str):
        print("msg:" + message)
    
    def onConnect(self, client: AmiClient):
        print("Connected to server!")

    def onDisconnect(self, client: AmiClient):
        print("Disconnected from server!")

    def onLoggedIn(self, client: AmiClient):
        print("Logged in to AMI!")

    def onMessageReceived(self, client: AmiClient, now, seqnum, status, message: str):
        print(f"Message received from server '{message}' at {now} with sequence number {seqnum} and status {status}")
    
test = TestClient()
```

## Commands

### Register Command

Commands can be created and registered to AMI via the AmiClientCommandDef class.

See the [AMI Realtime Message Command Definition `C`](../reference/ami_realtime_messages.md#command-definition-c) for more details.

#### Example - Python Code

``` python
#Creates a new command
#Constructor: __init__(self, id, name, argumentsjson=None, whereclause="True", help=None, enabledexpression="True", fields=None, filterclause=None, selectmode=None, style=None, conditions=None, level=None, priority=None):
commandDef = AmiClientCommandDef("COMMAND_ID", "Command Name",filterclause="panel.title==\"PanelName\"", fields="id")
#Sends a command (C) declaration via the AMI Client
client.sendCommandDefinition(commandDef)
```

### Processing Command Callbacks

Command callbacks are processed using the AmiClientListenerInterface onCommand() method.

See the [AMI Realtime Message Response to Execute Command `R`](../reference/ami_realtime_messages.md#response-to-execute-command-r) for more details.

#### Example - Python Code

``` python
def onCommand(self, client : AmiClient, requestId : str, cmd : str, userName : str, objectType : str, objectId : str, params : dict):
        status = 1
        message = "Okay"
        #Starts a response (R) message
        client.startResponseMessage(requestId, status, message)
        #Get additional params defined by AmiClientCommandDef.setFields
        id = params.get("id")
        client.addMessageParamLong("id", id)
        client.sendMessageAndFlush()
}
```

