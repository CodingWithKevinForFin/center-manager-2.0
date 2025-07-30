from com.f1.ami.client import AmiClient

class AmiClientListenerInterface:

    def __init__(self) -> None:
        pass

    def onMessageReceived(self, client : AmiClient, now, seqnum, status, message : str):
        pass

    def onMessageSent(self, client : AmiClient, message : str):
        pass

    def onConnect(self, client : AmiClient):
        pass

    def onDisconnect(self, client : AmiClient):
        pass

    def onCommand(self, client : AmiClient, requestId : str, cmd : str, userName : str, objectType : str, objectId : str, params : dict):
        pass

    def onLoggedIn(self, client : AmiClient):
        pass