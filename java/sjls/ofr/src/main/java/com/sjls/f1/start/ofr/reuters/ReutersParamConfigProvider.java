package com.sjls.f1.start.ofr.reuters;

import java.util.Set;

import org.apache.log4j.Logger;

import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.reuters.rfa.config.ConfigProvider;

public class ReutersParamConfigProvider implements ConfigProvider {

        private final Logger m_logger = Logger.getLogger(ReutersParamConfigProvider.class);

        private PropertyController properties;

        public ReutersParamConfigProvider(PropertyController properties) {
                this.properties = properties;
        }

        @Override
        public String variable(String namespace, String key) {
                return variable(namespace, key, null);
        }

        @Override
        public String variable(String namespace, String key, String dflt) {
                String fullKey = toProperty(namespace) + '.' + key;
                String r = properties.getOptional(fullKey, String.class);
                if (r != null)
                        return r;
                if(m_logger.isDebugEnabled()) m_logger.debug("Requested Property for " + fullKey + " not found");
                return dflt;

        }

        @Override
        public String[] childrenNames(String namespace) {
                Set<String> keys = properties.getSubPropertyController(toProperty(namespace)).getKeys();
                return keys.toArray(new String[keys.size()]);
        }

        static private String toProperty(String namespace) {
                return SH.replaceAll(namespace, '/', '.');
        }

}
