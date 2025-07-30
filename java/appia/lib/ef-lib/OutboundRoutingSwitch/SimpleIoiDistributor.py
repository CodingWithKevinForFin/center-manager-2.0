# $Id: SimpleIoiDistributor.py,v 1.1 2004/03/11 17:51:56 akamdar Exp $
# Author: Ajay Kamdar

# This plugin takes an IOI sent to FIX session and sends the same IOI
# out over on other FIX sessions as well.
#
# The SimpleIoiDistributor itself makes no attempt to change the the IOI
# contents based upon the FIX session it fans out the message to, but it
# is certainly feasible to add that kind of logic if necessary.
#

import com.javtech.appia.ef.session.DefaultSessionPlugin
import com.javtech.appia.ef.session.OutboundRoutingSwitchPlugin
import com.javtech.appia.ef.router.RoutingDestinationRegistry as Destinations
import com.javtech.javatoolkit.fix.FixConstants as Fix

class SimpleIoiDistributor (
        com.javtech.appia.ef.session.OutboundRoutingSwitchPlugin,
		com.javtech.appia.ef.session.DefaultSessionPlugin
    ) :

    # Constructor
    def __init__(self, plugin_name, filter, plugin_args):
#        self.dsp = com.javtech.appia.ef.session.DefaultSessionPlugin(
#                                                        plugin_name, filter)
#        self.dsp = com.javtech.appia.ef.session.DefaultSessionPlugin.newInstance(self)
        com.javtech.appia.ef.session.DefaultSessionPlugin.__init__(self, plugin_name, filter)
        self.plugin_args = plugin_args
        distribution_list = plugin_args.get("distribution-list")
        if not distribution_list :
	    raise Exception, \
	    	"ERROR: distribution-list is required"
        else:
            self.destinations = []
            for item in distribution_list.items() :
                destination = item[0]
                self.destinations.append(destination)

            #print "INFO: IOIs will be distributed to FIX sessions ", \
            #       self.destinations


    # Appia EF calls this method as the first step before sending out a message
    # on a FIX session. Returning OutboundRoutingSwitchPlugin.TRY_NEXT_ROUTE
    # will instruct Appia to continue its normal sending of message, and
    # returning OutboundRoutingSwitchPlugin.MESSAGE_ROUTED will tell Appia
    # that the plugin has taken care of sending the message out and it
    # should not try to send it out on its own.
    #
    # In this example, the method does the following:
    #   - Tells Appia to continue usual processing if message is not an IOI
    #   - Sends an IOI individually to each configured destination FIX session
    #   - Clones the IOI object to make sure modifications made to the object.
    #     while sending  it over one FIX session do not interfere with the
    #     processing of the IOI on another FIX session.
    #   - Checks to make sure the destination if available before sending the
    #     IOI.
    #   - NOTE: if the desired behavior is to have Appia automatically send
    #     the IOI to a FIX session that is currently not available, then
    #     modify the dest.send() call to provide a storeAndForward parameter.
    #   
    def processOutboundMessageRouting(self, mo, ccx):
	msgType = mo.messageType.code
        # Ignore everything other than IOIs
        if msgType != "6" :
            return self.TRY_NEXT_ROUTE

        for target in self.destinations :
            if target == ccx.pluginPoint.sessionName :
                continue

            dest = Destinations.getDestination(target)
            if dest and dest.isAvailable() :
                # clone the message so that the message processing on one FIX
                # session does not interfere with the message processing on
                # another FIX session
                msg = mo.clone()
                dest.send(msg)
                print "INFO: Sent IOI to ", target

        self_dest = Destinations.getDestination(ccx.pluginPoint.sessionName)
        if self_dest.isAvailable() :
            return self.TRY_NEXT_ROUTE
        else :
            return self.MESSAGE_ROUTED

    #
    # If you are copying this code to create your own plugin, just remember
    # to change the name of the class in the newInstance() method -- otherwise
    # the following is all boilerplate code
    #
    def newInstance(self):
        rval = SimpleIoiDistributor(self.getName(),
                                  self.getFilter(), self.plugin_args)
        #rval.dsp = com.javtech.appia.ef.session.DefaultSessionPlugin.newInstance()
        return rval

    #def isInterested(self, plugin_point):
    #    return self.dsp.isInterested(plugin_point)

    #def getName(self):
    #    return self.dsp.getName()

    #def getFilter(self):
    #    return self.dsp.getName()
