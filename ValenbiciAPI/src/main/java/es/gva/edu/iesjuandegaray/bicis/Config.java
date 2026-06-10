package es.gva.edu.iesjuandegaray.bicis;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

public class Config {

    private static final Logger LOG = Logger.getLogger(Config.class.getName());
    private static final Properties props = new Properties();

    static {
        try (InputStream is = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (is != null) {
                props.load(is);
            }
        } catch (IOException e) {
            LOG.warning("No se pudo cargar config.properties del classpath");
        }

        try (InputStream is = new FileInputStream("config.properties")) {
            props.load(is);
        } catch (IOException e) {
            // si no existe el archivo, usamos valores por defecto
        }

        if (!props.containsKey("db.url"))
            props.setProperty("db.url", "jdbc:mysql://localhost:3307/valenbicibd");
        if (!props.containsKey("db.user"))
            props.setProperty("db.user", "valenbici_user");
        if (!props.containsKey("db.password"))
            props.setProperty("db.password", "Valenbici2024!");
        if (!props.containsKey("db.driver"))
            props.setProperty("db.driver", "com.mysql.cj.jdbc.Driver");
        if (!props.containsKey("api.valenbici.url"))
            props.setProperty("api.valenbici.url",
                "https://geoportal.valencia.es/server/rest/services/OPENDATA/Trafico/MapServer/228/query?where=1%3D1&outFields=*&returnGeometry=true&f=json");
        if (!props.containsKey("api.geoportal.url"))
            props.setProperty("api.geoportal.url",
                "https://geoportal.valencia.es/server/rest/services/OPENDATA/Trafico/MapServer/228/query?where=1%3D1&outFields=*&returnGeometry=true&f=json");
    }

    public static String get(String key) {
        return props.getProperty(key);
    }

    private Config() {}
}
