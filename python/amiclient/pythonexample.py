from com.f1.ami.client import AmiClient
from com.f1.ami.listener import AmiClientListenerInterface
from com.f1.utils.options import Options
from com.f1.ami.AmiClientCommandDef import AmiClientCommandDef

class TestClient(AmiClientListenerInterface):
    "A script demonstrating how this client can be used to connect to AMI"
    def __init__(self) -> None:
        super().__init__()
        
        ad = AmiClient()
        options = Options.ENABLE_SEND_SEQNUM + Options.ENABLE_SEND_TIMESTAMPS + Options.ENABLE_AUTO_PROCESS_INCOMING
        ad.addListener(self)

        # Connecting to the AMI instance, replace with appropriate connection parameters
        ad.start("localhost", 3289, "demo", options)
        
        command = AmiClientCommandDef("alert", "Alert", fields="row")
        ad.sendCommandDefinition(command)
        ad.flush()
        # while True:
        #     name= "mir"
        #     num = 10
        #     num += 1
        
    def onMessageSent(self, client: AmiClient, message: str):
        print("msg:" + message)
    
    def onConnect(self, client: AmiClient):
        print("Connected to server!")

    def onDisconnect(self, client: AmiClient):
        print("Disconnected from server!")

    def onLoggedIn(self, client: AmiClient):
        print("Logged in to AMI!")

    def onCommand(self, client, res_id, cmd, username, obj_type, obj_id, params):
        print("Command invoked")

    def onMessageReceived(self, client: AmiClient, now, seqnum, status, message: str):
        print(f"Message received from server '{message}' at {now} with sequence number {seqnum} and status {status}")

print("before")    
test = TestClient()
print("after")

