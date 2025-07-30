import os
import com.javtech.appia.ef.session.DefaultSessionPlugin
import com.javtech.appia.ef.session.InboundPreValidationPlugin
import com.javtech.appia.ef.session.OutboundPreValidationPlugin
import com.javtech.javatoolkit.fix.FixConstants as Fix

# This Jython plugin provides functionality equivalent to the
# sample plugin class FixTag4822 implemented in Java.
#
class FixTag4822Jy(
        com.javtech.appia.ef.session.InboundPreValidationPlugin,
        com.javtech.appia.ef.session.OutboundPostValidationPlugin,
		com.javtech.appia.ef.session.DefaultSessionPlugin
    ) :

    def __init__(self, plugin_name, filter, plugin_args):
#        self.dsp = com.javtech.appia.ef.session.DefaultSessionPlugin(
#                                                        plugin_name, filter)
        com.javtech.appia.ef.session.DefaultSessionPlugin.__init__(self, plugin_name, filter)
        self.plugin_args = plugin_args
        self.savedir = plugin_args.get("instruments-dir")
        self.idvalues = {}
        if not self.savedir :
            self.savedir = "instruments-cache"
        if not os.path.exists(self.savedir):
            os.mkdir(self.savedir)


    def processOutboundMessagePostValidation(self, mo, ccx):
        idSource = mo.messageData[Fix.SecurityIDSource]
        if idSource:
            securityId = mo.messageData[Fix.SecurityID]
            symbol     = mo.messageData[Fix.Symbol]
            values = self.idvalues.get(symbol)
            if not values :
                self.idvalues[symbol] = [idSource, securityId]
                f = open(self.savedir + os.sep + symbol + ".txt", "w")
                f.write(idSource + "\n")
                f.write(securityId + "\n")
                f.close()

    def processInboundMessagePreValidation(self, mo, ccx):
        symbol = mo.messageData[Fix.Symbol]
        idSource = mo.messageData[Fix.SecurityIDSource]
        if (not idSource) and symbol :
            values = self.idvalues.get(symbol)
            if not values :
                f = open(self.savedir + os.sep + symbol + ".txt")
                l = f.readlines()
                f.close()
                values = [l[0].strip(), l[1].strip()]
                self.idvalues[symbol] = values
            mo.messageData[Fix.SecurityIDSource] = values[0]
            mo.messageData[Fix.SecurityID]       = values[1]


    #
    # Just remember to change the name of the class in the newInstance()
    # method -- otherwise the following is all boilerplate code
    #
    def newInstance(self):
        rval = FixTag4822Jy(self.getName(),
                                  self.getFilter(), self.plugin_args)
    #    rval.dsp = self.dsp.newInstance()
        return rval

    #def isInterested(self, plugin_point):
    #    return self.dsp.isInterested(plugin_point)

    #def getName(self):
    #    return self.dsp.getName()

    #def getFilter(self):
    #    return self.dsp.getName()

    #def getPluginPoint(self):
    #    return self.dsp.getPluginPoint()

