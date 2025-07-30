
class Options:

    # Options:

    ENABLE_QUIET = 4

    
    ENABLE_AUTO_PROCESS_INCOMING = 2
    """
	By default, this client will automatically read inbound messages 
    and process them in a seperate thread, if disabled you must manually call {@link #pumpIncomingEvent()}
	"""
   
    DISABLE_AUTO_RECONNECT = 8
    """
	By default, this client will keep trying to reconnect to the ami server, see {@link #setAutoReconnectFrequencyMs(long)}
	"""
    
    ENABLE_SEND_TIMESTAMPS = 32
    """
	 Should this client send timestamps, useful for enabling delayed message detection from ami client
    """
   
    ENABLE_SEND_SEQNUM = 64
    """
	Should this client send sequence numbers, useful for linking a particular client message to the message in ami server
	"""
    
    LOG_CONNECTION_RETRY_ERRORS = 128
    """
	Should this client log errors each time a connection retry fails, if not set then just on the first connection failure
	"""
    LOG_MESSAGES = 256

    def anyBits(options : int, check : int):
        "Checks if options contains the check bit"
        return (options & check) > 0