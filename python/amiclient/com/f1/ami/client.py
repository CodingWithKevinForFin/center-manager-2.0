"""
Python AmiClient class which provides an easy way of writing to both the console interface (AMISQL),
and also the real-time messaging API.

Use command line arguments '--help' to view currently configurable arguments
"""

#!/usr/bin/env python3


from enum import Enum
import socket
import logging
import time 

from com.f1.ami.constants import Constants
from com.f1.ami.receiver import Receiver
from com.f1.ami.writer import Writer
from com.f1.ami.callback_group import CallbackGroup
from com.f1.utils.options import Options
from com.f1.ami.AmiClientCommandDef import AmiClientCommandDef

class AmiClient:

    "Primary AmiClient class that provides easy write access to the AMI Realtime Server Interface"
        
    logging.basicConfig(level=logging.DEBUG)
    logger = logging.getLogger("AMIAmiClient")

    def __init__(self):
        self._rt_logged_in = False
        self._rt_socket = None
        self._logged_in = False
        self._socket = None
        self._receiver = None
        self._writer = None
        self._monitor_log = None
        self.log_level = logging.INFO
        self.initialized = False
        self.autoReconnectFrequencyMs = 1000
        self.seqnum = None

        self._rt_message = None
        self.sent_count = 0
        self.output_buffer = []
        self.acks_received = 0

        self.options = None
        self.port = None
        self.host = None
        self.loginId = None

        self.server_address = None
        self.rt_port = None
        self.rt_id = None
        self.timeout = None
        self.use_ipv4 = None
        
        self.disable_realtime = None
        
        self.c_id = None
        self.c_pw = None
        self.c_port = None
        self.monitor_log = None
        self.debug = None
        self.quiet = None
        self.login_attempts = None
        self.log_file = None

        self.listeners = []

    def start(self, host="localhost", port=3289, loginId : str="demo", options : int=0, timeout : int=10, use_ipv4=True):
        "Initialize adapter class"

        self.host = host
        self.port = port
        self.loginId = loginId
        self.options = options
        self.timeout = timeout
        self.use_ipv4 = use_ipv4
        
        if self._rt_logged_in or self._logged_in:
            AmiClient.logger.info("already logged in")
            return

        self._config_connection()
        
        #Configure log level
        log_level = self.log_level
        if self.debug:
            log_level = logging.DEBUG
        elif self.quiet:
            log_level = logging.WARNING
        self.log_level = log_level

        AmiClient.logger.setLevel(log_level)

        if self.log_file != "":
            fh = logging.FileHandler(self.log_file, 'w+')
            fh.setLevel(logging.DEBUG)
            formatter = logging.Formatter(
                '%(asctime)s - %(name)s - %(levelname)s - %(message)s'
            )
            fh.setFormatter(formatter)
            AmiClient.logger.addHandler(fh)

        self._monitor_log = self.monitor_log

        if not self._init_sockets():
            return
        
        for listener in self.listeners:
            listener.onConnect(self)

        self.attempt_login()
        self.register_callback("Relay AMI Message", AmiClient._output_received_messages)
        self.register_callback("Store AMI Message", self._store_received_messages)
        self.register_callback("Call onMessageReceived", self._call_onMessageReceived_on_listeners)
        self.register_callback("Call onCommand", self._call_onCommand_on_listeners)


        AmiClient.logger.info("AMI Client Adapter Successfully Initialized!")
        self.seqnum = 0

    def attempt_login(self):
        "Attempts the full sequence of creating a receiver and writer and logging in"
        #Create receiver and writer
        self._receiver = Receiver()
        self._receiver.init(
            logger = AmiClient.logger, rt_socket = self._rt_socket, 
            c_socket = self._socket, logfile=self._monitor_log 
        )

        self._writer = Writer()
        self._writer.init(
            logger = AmiClient.logger, rt_socket = self._rt_socket, socket = self._socket
        )

        if not self._login(
            rt_login = self.rt_id,
            c_login = self.c_id,
            c_pw = self.c_pw,
            attempts = self.login_attempts
        ):
            AmiClient.logger.error("Failed to login to AMI Server")
            return

        self.initialized = True
        # print(self.listeners)
        for listener in self.listeners:
            listener.onLoggedIn(self)

    def connect(self):
        "Try and reconnect, will also send login (L) instructions."
        if self.auto_reconnect:
            raise RuntimeError("Can not manually connect when autoReconnect is enabled")
        self.start(self.host, self.port, self.loginId, self.options)


    # setting logs to display DEBUG messages
    def setDebugMessages(self):
        self.log_level = logging.DEBUG
        self.logger.setLevel(self.log_level)

    # returns true if DEBUG messages are being logged
    def getDebugMessages(self):
        return self.log_level == logging.DEBUG

    # gets the output from the real time AMI command line
    def getOutputBuffer(self):
        self._receiver.receive()
        return self.output_buffer

    def getOptions(self):
        return self.options

    def setOptions(self, options : int):
        self.options = options

        self.quiet = Options.anyBits(options, Options.ENABLE_QUIET)

        self.auto_process_incoming = Options.anyBits(options, Options.ENABLE_AUTO_PROCESS_INCOMING)
        self.auto_reconnect = not Options.anyBits(options, Options.DISABLE_AUTO_RECONNECT)
        self.include_seq_num = Options.anyBits(options, Options.ENABLE_SEND_SEQNUM)
        self.include_timestamp = Options.anyBits(options, Options.ENABLE_SEND_TIMESTAMPS)
        self.log_reconnect_errors = Options.anyBits(options, Options.LOG_CONNECTION_RETRY_ERRORS)
        
        self.debug = Options.anyBits(options, Options.LOG_MESSAGES)

    def addListener(self, listener):
        self.listeners.append(listener)


    def remove_callback(self, callback_name: str):
        "Removes a callback for receiving messages"
        if self._receiver is not None:
            self._receiver.remove_callback(callback_name = callback_name)

    def register_callback(self, callback_name: str, callback, callbackGroup: CallbackGroup = CallbackGroup.ALL_LOGS):
        """Registers a callback for receiving messages
           callback is of type - Callable[[str], None]"""
        if self._receiver is not None:
            self._receiver.register_callback(callback_name = callback_name, callback = callback, callbackGroup = callbackGroup)

    def close(self):
        "Performs any cleaning up of the AmiClient"
        while self.sent_count > self.acks_received:
            self._receiver.receive()
        self.sendLogout()
        
        AmiClient.logger.debug("Cleaning up AmiClient")

        if self._writer is not None:
            self._writer.cleanup()
        if self._receiver is not None:
            self._receiver.cleanup()
        self._logged_in = False
        self._rt_logged_in = False
        self.initialized = False
        self.seqnum = 0

        for listener in self.listeners:
            listener.onDisconnect(self)

        AmiClient.logger.info("AmiClient cleanup complete!")


    def delete_obj(self, table_name: str, uid: str):
        "Sends a delete command to a given table with the given id"
        if not self._rt_logged_in:
            AmiClient.logger.warning(
                "Failed to delete object via real-time API (Not logged in)"
            )
            return

        if len(uid) == 0 or len(table_name) == 0:
            AmiClient.logger.debug("Failed to delete object via real-time API (Empty uid or table name)")
            return

        msg = f"{Constants.RT_KEY_DELETE}|"\
            f"{Constants.RT_KEY_ID}=\"{uid}\"|"\
            f"{Constants.RT_KEY_TABLE}=\"{table_name}\""

        #Send value over
        self._writer.rt_add_string(msg)

    def send_obj(self, table_name: str, values, uid = ""):
        """Sends an object to a given table,
           values is treated as a dictionary of string key and string value
           actual string values should be enclosed with double quotes around them"""
        if not self._rt_logged_in:
            AmiClient.logger.warning(
                "Failed to send object via real-time API (Not logged in)"
            )
            return

        if len(values) == 0:
            AmiClient.logger.debug(f"Not sending object to {table_name} because values are empty...")
            return

        msg = f"{Constants.RT_KEY_OBJECT}"

        #Add id
        if len(uid) != 0:
            msg += f"|{Constants.RT_KEY_ID}=\"{uid}\""

        #Add table name
        msg += f"|{Constants.RT_KEY_TABLE}=\"{table_name}\""

        #Add values
        for key, val in values.items():
            msg += f"|{key}={val}"

        #Send value over
        self._writer.rt_add_string(msg)

    def send_ami_script(self, ami_script : str):
        "Sends an AMI script to the database"
        if not self._logged_in:
            AmiClient.logger.warning(
                "Failed to send object via console (Not logged in)"
            )
            return
        AmiClient.logger.debug(ami_script)
        self._writer.c_add_string(ami_script)

    def send_rt_message(self, rt_message: str):
        "Sends a raw message via the real-time api"
        if not self._rt_logged_in:
            AmiClient.logger.warning(
                "Failed to send object via real-time API (Not logged in)"
            )
            return
        AmiClient.logger.debug(rt_message)
        self._writer.rt_add_string(rt_message)

    def checkSeqnumAndTimestamp(self):
        "Adds the next sequence number and timestamp to real-time message"
        if self.include_seq_num == True:
            self._rt_message += ("#")
            self.seqnum += 1
            self._rt_message += (str(self.seqnum))
        if self.include_timestamp == True:
            self._rt_message += ('@')
            self._rt_message += (str(int(time.time() * 1000)))
    
    @DeprecationWarning
    def startStatusMessage(self):
        "start a status (S) message"
        self._writer.rt_add_string(Constants.RT_STATUS)
        self.checkSeqnumAndTimestamp()

    def startObjectMessage(self, table_name : str, id = None):
        "start an object (O) message"
        self._rt_message = ""
        #Start object 
        self._rt_message += f"{Constants.RT_KEY_OBJECT}"

        #Adding sequence number and timestamp elements, if required
        self.checkSeqnumAndTimestamp()

        #Add id
        if id != None:
            self._rt_message += f"|{Constants.RT_KEY_ID}=\"{id}\""

        #Add table name
        self._rt_message += f"|{Constants.RT_KEY_TABLE}=\"{table_name}\""


    def startObjectMessageWithExpiresOn(self, table_name : str, expiresOn, id=None):
        "start an object message with expires on parameter"
        self.startObjectMessage(table_name, id)

        #Adding expiresOn as a Long
        self._rt_message += f"|{Constants.RT_EXPIRES_ON}={expiresOn}L"
        self.checkSeqnumAndTimestamp()


    def addMessageParamObject(self, key : str, value):
        "Convenience message for sending a boxed value"
        self._rt_message += f"|{key}={value}"

    def addMessageParams(self, params : dict):
        "Convenience message for quickly sending all the params from the dictionary where key is the param name and object is the value"
        #Add values
        for key, val in params.items():
            if isinstance(val, str): # if value is a string, wrap in ""
                self._rt_message += f"|{key}=\"{val}\""
            else:
                self._rt_message += f"|{key}={val}"

    def addMessageParamStringDoubleQuotes(self, key : str, val : str):
        "add a string (enclosed in double quotes) param to the current message being built"
        if val != None:
            self._rt_message += f"|{key}=\"{val}\""
    
    def addMessageParamString(self, key : str, val : str):
        "add a string (enclosed in single quotes) param to the current message being built"
        if val != None:
            self._rt_message += f"|{key}='{val}'"

    def addMessageParamInt(self, key : str, val : int):
        "add a non-None integer to the current message being built"
        if val != None:
            self._rt_message += f"|{key}={val}"
    
    def addMessageParamLong(self, key : str, val : int):
        "add a param to the current message being built"
        self._rt_message += f"|{key}={val}L"
    
    def addMessageParamDouble(self, key : str, val : float):
        "add a param to the current message being built"
        self._rt_message += f"|{key}={val}D"

    def addMessageParamFloat(self, key : str, val : float):
        "add a param to the current message being built"
        self._rt_message += f"|{key}={val}F"

    def addMessageParamBoolean(self, key : str, val : bool):
        "add a param to the current message being built"
        # translating booleans to respective string representations
        value = "false"
        if val:
            value = "true"
        self._rt_message += f"|{key}={value}"

    def addMessageParamJson(self, key : str, val):
        "add a param to the current message being built"
        self._rt_message += f"|{key}={val}J"

    def addMessageParamBinary(self, key : str, val : str):
        "add a param to the current message being built"
        # val needs to be a UUEncoded string
        self._rt_message += f"|{key}=\"{val}\"U"

    def addMessageParamEnum(self, key : str, val : str):
        "add a param to the current message being built"
        self._rt_message += f"|{key}='{val}'"

    def addRawText(self, text : str):
        "bypass this api and just send the raw chars on the string, you're responsible for properly escaping, quoting, etc"
        self._rt_message += text
    

    def sendMessage(self):
        "finalize and send the currently being built message"
        # print(self._rt_message)

        #Send value over
        self._writer.rt_add_string(self._rt_message)
        self.sent_count += 1
        for listener in self.listeners:
            listener.onMessageSent(self, self._rt_message)
        
        self._rt_message = ""

    def flush(self):
        "end pending message buffer to AMI, can be called at anytime"
        self._writer.flush_messages()
    
    def sendMessageAndFlush(self):
        "send the pending message to AMI and block until the message is fully read by AMI"
        self.sendMessage()
        self.flush()


    def resetMessage(self):
        "reset the pending message, following this you need to re-start the message"
        self._rt_message = ""


    def startCommandDefinition(self, id : str):
        "start a command definition (C) message"
        self._rt_message = Constants.RT_COMMAND
        self.checkSeqnumAndTimestamp()

        #Add id
        self.addMessageParamString(Constants.RT_KEY_ID, id)


    def sendCommandDefinition(self, commanddef : AmiClientCommandDef):
        "send a command (C) declaration"
        self.startCommandDefinition(commanddef.id)
        self._add_command_params_to_message(commanddef)
        self.sendMessageAndFlush()

    def sendCommandDefinitionWithAmiScript(self, commanddef : AmiClientCommandDef, script):
        "send a command (C) declaration with AMI script to execute"
        self.startCommandDefinition(commanddef.id)
        self._add_command_params_to_message(commanddef)
        self.addMessageParamString("X", script)
        self.sendMessageAndFlush()

    def _add_command_params_to_message(self, commanddef : AmiClientCommandDef):
        self.addMessageParamString("N", commanddef.name)
        if commanddef.argumentsjson is not None:
            self.addMessageParamString("A", commanddef.argumentsjson)
        self.addMessageParamString("W", commanddef.whereclause)
        self.addMessageParamString("H", commanddef.help)
        self.addMessageParamString("E", commanddef.enabledexpression)
        self.addMessageParamString("F", commanddef.fields)
        self.addMessageParamString("T", commanddef.filterclause)
        self.addMessageParamString("M", commanddef.selectmode)
        self.addMessageParamString("S", commanddef.style)
        self.addMessageParamString("C", commanddef.conditions)
        self.addMessageParamInt("L", commanddef.level)
        self.addMessageParamInt("P", commanddef.priority)

    def startResponseMessage(self, origRequestId : str, status=None, message=None):
        "start a response (R) message"
        self._rt_message = Constants.RT_RESPONSE

        #Add request id
        self.addMessageParamString(Constants.RT_KEY_ID, origRequestId)
        #Add status
        self.addMessageParamInt(Constants.RT_STATUS, status)
        #Add message
        self.addMessageParamString(Constants.RT_MESSAGE, message)
        self.checkSeqnumAndTimestamp()


    def startDeleteMessage(self, table_name : str, id : str):
        "start a delete (D) message"
        self._rt_message = Constants.RT_KEY_DELETE
        self.addMessageParamString(Constants.RT_KEY_ID, id)
        self.addMessageParamString(Constants.RT_KEY_TABLE, table_name)
        self.checkSeqnumAndTimestamp()

    def sendPause(self, delayMs : int):
        "send a pause (P) message"
        self._rt_message = Constants.RT_PAUSE
        self.addMessageParamInt("D", delayMs)
        self.sendMessageAndFlush()

    def sendLogout(self):
        self._writer.rt_add_string(Constants.RT_EXIT)
        self.flush()
        AmiClient.logger.debug("logging out...")

    def flushAndWaitForReplys(self, timeoutMs : int):
        "flush existing messages and wait for a response"
        self.flush()
        current_time = int(time.time() * 1000)
        end_time = current_time + timeoutMs
        if self.quiet:
            pass
        else:
            beforelength = len(self.output_buffer)
            while int(time.time() * 1000) <= end_time:
                self._receiver.receive()
            afterlength =  len(self.output_buffer)
            if afterlength <= beforelength: # if no new messages
                raise TimeoutError


    def getAutoReconnectFrequencyMs(self):
        return self.autoReconnectFrequencyMs

    def setAutoReconnectFrequencyMs(self, value : int):
        self.autoReconnectFrequencyMs = value


    def _init_sockets(self) -> bool:
        "Attempt to initialize the sockets according to the current configuration"
        #Real-time Socket initialization
        socket_type = socket.AF_INET if self.use_ipv4 else socket.AF_INET6

        if not self.disable_realtime:
            try:
                AmiClient.logger.debug("Attempting to create real-time socket...")
                self._rt_socket = socket.socket(socket_type, socket.SOCK_STREAM, 0)
                AmiClient.logger.debug("Successfully created real-time socket")
            except Exception as exception:
                AmiClient.logger.error(f"Could not create real-time socket, exception - {exception}")
                return False
            try:
                AmiClient.logger.debug("Attempting to connect to server...")
                self._rt_socket.settimeout(self.timeout)
                if self.use_ipv4:
                    self._rt_socket.connect((self.server_address, self.rt_port))
                else:
                    self._rt_socket.connect((self.server_address, self.rt_port,0,0))

                AmiClient.logger.debug(
                    f"Successfully connected to ami server [Info: {self._rt_socket.getpeername()}]"
                )
            except Exception as exception:
                AmiClient.logger.error(f"Could not connect to server, exception - {exception}")
                return False

        return True

    def _login(self, rt_login: str, c_login: str, c_pw: str, attempts: int) -> bool:
        "Attempt to login for both realtime access as well as console access"

        #Real-time Login
        if not self.disable_realtime:
            self.register_callback(
                callback_name = "RT Login callback", callback = self._rt_login_callback
            )

            quiet_option = "|O=\"QUIET\""
            if self.quiet:
                self._writer.rt_send_raw_msg(f"L|I=\"{rt_login}\"{quiet_option}")
                self._rt_logged_in = True # No way to know whether connection was successful
            else:
                for _ in range(attempts):
                    if self._rt_logged_in:
                        break

                    AmiClient.logger.debug(f"Attempting to login with id {rt_login}")

                    self._writer.rt_send_raw_msg(f"L|I=\"{rt_login}\"")
                
                    time.sleep(0.2)

                if not self._rt_logged_in:
                    AmiClient.logger.error("Failed to login with real-time port, terminating!")
                    self.close()
                    return False
                
                AmiClient.logger.info("Successfully logged in!")

            self.remove_callback(callback_name = "RT Login callback")

        return True

    def _close_socket(self):
        "Closes and cleans up the socket"
        if self._rt_socket is not None:
            AmiClient.logger.debug("Closing real-time socket!")
            self._rt_socket.close()
            self._rt_socket = None

    def _rt_login_callback(self, input_str: str):
        "Checks for a valid login status"
        result = Receiver.parse_msg(input_str)
        
        if result.message == Constants.RT_LOGIN_SUCCESS:
            self._rt_logged_in = True

    def _login_callback(self, input_str: str):
        "Checks for a valid login status"

        if (Constants.C_LOGIN_SUCCESS_HEAD in input_str):
            self._logged_in = True

    def _output_received_messages(input_str: str):
        "Prints all messages returned by the AMI Server"
        AmiClient.logger.info(f"{input_str}")

    def _store_received_messages(self, input_str: str):
        "Stores all messages returned by the AMI Server in a list"
        self.output_buffer.append(input_str)

    def _call_onMessageReceived_on_listeners(self, input_str: str):
        "Calls the onMessafeReceived function after each message is received"

        if input_str.startswith("M") or input_str.startswith("E"):
            parts = input_str.split("|")
            timestamp = None
            seqnum = None
            status = None
            message = None

            if len(parts[0]) > 1:
                at = parts[0].find("@")
                if at != -1:
                        timestamp = parts[0][at + 1:]

            for part in parts[1:]:
                sides = part.split("=")
                sides[1] = sides[1].replace("\"", "")
                if sides[0] == "S":
                    status = sides[1]
                elif sides[0] == "Q":
                    seqnum = sides[1]
                elif sides[0] == "M":
                    message = sides[1]
                else:
                    pass
            
            # if either timestamp or seqnum is not available
            if timestamp is None:
                timestamp = -1
            if seqnum is None:
                seqnum = -1
            if message == "ACK":
                self.acks_received += 1

            for listener in self.listeners:
                listener.onMessageReceived(self, int(timestamp), int(seqnum), status, message)

    def _call_onCommand_on_listeners(self, input_str: str):
        "Calls the onCommand function after an execute command is received"
        if input_str.startswith("E"):

        
            parts = input_str.split("|")
            res_id = None
            cmd = None
            username = None
            obj_type = None
            obj_id = None
            params = {}


            for part in parts[1:]:
                sides = part.split("=")
                sides[1] = sides[1].replace("\"", "")
                if sides[0] == "I":
                    res_id = sides[1]
                elif sides[0] == "C":
                    cmd = sides[1]
                elif sides[0] == "U":
                    username = sides[1]
                elif sides[0] == "O":
                    obj_id = sides[1]
                else:
                    params[sides[0]] = sides[1]


            for listener in self.listeners:
                listener.onCommand(self, res_id, cmd, username, obj_type, obj_id, params)

    def _config_connection(self):
        "Configures the connection to the centre for the API"
        self.server_address = self.host
        self.rt_port = self.port
        self.rt_id = self.loginId
        
        self.disable_realtime = False
        
        self.c_id = "demo"
        self.c_pw = "demo123"
        self.c_port = 3290
        self.monitor_log = ""
        self.login_attempts = 5
        self.log_file = Constants.LOG_FILE_PATH
        self.setOptions(self.options)