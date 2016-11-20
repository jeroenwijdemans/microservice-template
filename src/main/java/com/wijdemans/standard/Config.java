package com.wijdemans.standard;

import org.apache.commons.lang3.StringUtils;
import org.glassfish.hk2.api.Immediate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.PostConstruct;
import javax.ws.rs.ext.Provider;
import java.io.*;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

@Provider
@Immediate
public class Config {

    private static final Properties props = new Properties();
    private static final Logger logger = LoggerFactory.getLogger(Config.class);
    private static final String ENV_PROPS_LOCATION = "PROPERTIES_LOCATION";
    private static final String APP_YAML = "app.yaml";

    @PostConstruct
    public void init() {
        Yaml yaml = new Yaml();
        Map<String, Object> map = yaml.loadAs(getFileInputStream(APP_YAML), Map.class);
        assignProperties(props, map, null);
        logger.debug("Read [{}] keys", props.keySet().size());
    }

    public static String getPropertiesLocation() {
        if (System.getenv(ENV_PROPS_LOCATION) == null) {
            throw new RuntimeException(ENV_PROPS_LOCATION + " has not been set as environment variable. Please set.");
        }
        return System.getenv(ENV_PROPS_LOCATION);
    }

    public static InputStream getFileInputStream(String file) {
        String loc = getPropertiesLocation() + "/" + file;
        logger.debug("Reading properties from [{}]", loc);
        try {
            File f = Paths.get(loc).toFile();
            return new FileInputStream(f);
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("Cannot find [" + loc + "]. Is the correct volume mounted? ");
        }
    }

    public static Properties getProperties(String file) {
        Properties properties = new Properties();
        try {
            properties.load(getFileInputStream(file));
        } catch (IOException e1) {
            throw new IllegalStateException("Cannot load properties in props object. Are they in java props format.");
        }
        return properties;
    }

    public static String get(String key) {
        if (props.containsKey(key)) {
            return props.getProperty(key);
        }
        throw new IllegalStateException(String.format("Cannot find key [%s] in [%s]", key, Arrays.toString(props.keySet().toArray())));
    }

    public static int getInt(String key) {
        return Integer.valueOf(get(key));
    }

    private void assignProperties(Properties props, Map<String, Object> map, String path) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            if (StringUtils.isNotEmpty(path))
                key = path + "." + key;
            Object val = entry.getValue();
            if (val instanceof String) {
                props.put(key.trim(), val);
            } else if (val instanceof Number) {
                props.put(key.trim(), "" + val);
            } else if (val instanceof Map) {
                assignProperties(props, (Map<String, Object>) val, key);
            }
        }
    }
}
