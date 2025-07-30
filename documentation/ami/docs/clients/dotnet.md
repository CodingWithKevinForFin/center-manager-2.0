# .NET

## Setup

The .NET project files are available upon request - please contact support@3forge.com to receive the latest version of the library.

### Overview

Overview The AMI Client Listener is used to process messages and commands sent and received by the AMI Client.  
The AMI Client connects to the AMI Realtime Backend API. Below is a simple example that sends a few messages and a command via the AMI Client and processes the command callback.  
Note that the AMI Client is **NOT** thread-safe.

### Configuration

The hostname is the host where either AmiRelay is running.  
The port is configured via the property **"ami.port"** which is by default set to 3289.

### Importing the Adapter in a Project

1. Extract the files from the zip file provided: You can find an **ExampleClient** that makes use of the adapter in the top level directory. All AmiClient code is stored in **com.f1.ami.client**.  
	Note that **your project** folder needs to be separate from the extracted folder.  
1. Add a reference to the extracted project to **your project**: From within your project directory, run the command:  
	`$> dotnet add reference `*`<Your_Path_to_`**`AmiAdapter.csproj`**`_file>`*  
	E.g.`$> dotnet add reference ..\.NET_AMI_Adapter\AmiAdapter.csproj`  
	See [dotnet add reference command - .NET CLI \| Microsoft Learn](https://learn.microsoft.com/en-us/dotnet/core/tools/dotnet-add-reference)
1. Import the namespace to use AmiClient - put this line in the file that accesses AmiClient: **using com.f1.ami.client;**
1. Run `dotnet build` and `dotnet run`.  

Alternatively, this adapter can be added as a project to an existing .NET Solution using Visual Studio. See [dotnet sln command - .NET CLI](https://learn.microsoft.com/en-us/dotnet/core/tools/dotnet-sln#add) and [Learn about Solution Explorer - Visual Studio (Windows) \| Microsoft Learn.](https://learn.microsoft.com/en-us/visualstudio/ide/use-solution-explorer?view=vs-2022#the-add-menu)

1. Extract files from the zip file provided.  
1. Add the extracted directory as a project to the existing solution (using dotnet sln or Visual Studio).  
1. In **Solution Explorer**, find the project that the adapter needs to be added to and right click. In the context menu, select **Add** -\> **Project Reference...**  
1. Select **AmiAdapter** from the listed projects. Click OK.  
1. The **AmiClient** class can be accessed under the **com.f1.ami.client** namespace.

### .NET Interface

**com.f1.ami.client.AmiClient** - Main client class  
**com.f1.ami.client.IAmiClientListener** - Listener interface that defines callbacks  

### Example - C# Code

``` csharp
using com.f1.ami.client;
class ExampleClient : IAmiClientListener {
    public static void Main(String[] args) {
        ExampleClient testClient = new ExampleClient();
        // can pass in your own logger, a default console logger will be used otherwise
        AmiClient client = new AmiClient();
        // adding a listener
        client.AddListener(testClient);
        client.Start("localhost", 3289, "demo", AmiClient.LOG_MESSAGES | AmiClient.ENABLE_AUTO_PROCESS_INCOMING);
        // Optionally, the debug property can be used to see logged messages
        // client.debug = true;
        int MESSAGE_COUNT = 100;
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            // making a message
            client.StartObjectMessage("testTable", "test_id" + i);
            client.AddMessageParamString("test", "hello world" + i);

            // adds message to queue
            client.SendMessage();
        }
        // sending all message
        client.Flush();
        // optionally, can use SendMessageAndFlush() if just one message
        // cleaning up
        client.Close();
    }
    public void onCommand(AmiClient client, string requestId, string cmd, string userName, 
                    string objectType, string objectId, Dictionary<string, string> paramDict) {
        Console.WriteLine("onCommand callback");
    }
    public void onConnect(AmiClient client) { 
        Console.WriteLine("onConnect callback"); 
    }
    public void onDisconnect(AmiClient client) {
        Console.WriteLine("onDisconnect callback");
    }
    public void onLoggedIn(AmiClient client) {
        Console.WriteLine("onLoggedIn callback");
    }
    public void onMessageReceived(AmiClient client, long now, long seqnum, int status, string message) {
        Console.WriteLine("onMesssageReceived callback");
    }
    public void onMessageSent(AmiClient client, string message) {
        Console.WriteLine("onMessageSent callback");
    }
}
```

### Common Methods

Here are some commonly used methods that will be helpful in sending messages. For more detailed docs, please see the **documentation** folder in the zip file provided. The **AmiClient.html** file lists all public methods that can be used to construct and send messages.

``` csharp
void Start (String host, int port, String loginId, int options)
//Method Start: Start the client and connect to AMI with the provided arguments.
//Note: The Start(...) creates an unencrypted connection. 
bool Connect () 
//Method Connect: Try and reconnect, will also send login (L) instructions.
AmiClient StartObjectMessage (String type, String id) 
//Method StartObjectMessage: start an object message (O| ...)
AmiClient AddMessageParamDouble (String key, Double? value)
//Method AddMessageParamDouble: add a param to the current message being built. If value is null, skip field
AmiClient AddMessageParams (Dictionary< String, Object > paramMap) 
//Method AddMessageParams: Convenience message for quickly sending all the params from the map where key is the param name and object is the value.
AmiClient AddMessageParamObject (String key, Object value) 
//Method AddMessageParams: Convenience message for quickly sending all the params from the map where key is the param name and object is the value.
bool SendMessage ()
//Method SendMessage: finalize and add the currently being built message to the send queue.
bool SendMessageAndFlush ()
//Method SendMessageAndFlush: send the pending message to AMI and block until the message is fully read by AMI.
void ResetMessage ()
//Method ResetMessage: reset the pending message, following this you need to re-start the message.
void Flush ()
//Method Flush: sends all messages waiting in the send queue, populated by com.f1.ami.client.AmiClient::SendMessage()
void FlushAndWaitForReplys (int timeoutMs)
//Method FlushAndWaitForReplys: flush existing messages and wait for a response.
void AddListener (IAmiClientListener listener)
//Method AddListener: add a listener for receiving callbacks on important events about this connection.
```

Note: sending Commands and Response messages have been deprecated.
