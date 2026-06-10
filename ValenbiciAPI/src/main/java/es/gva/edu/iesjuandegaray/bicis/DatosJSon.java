package es.gva.edu.iesjuandegaray.bicis;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatosJSon {

    private static final Logger LOG = Logger.getLogger(DatosJSon.class.getName());
    private static final String API_URL = Config.get("api.geoportal.url");

    private final List<EstacionBici> estaciones = new ArrayList<>();
    private String datos = "";

    public DatosJSon(int numEst) {
    }

    public void mostrarDatos(int numEst) {
        estaciones.clear();
        datos = "";

        if (API_URL == null || API_URL.isEmpty()) {
            datos = "La URL de la API no esta especificada.";
            LOG.severe(datos);
            return;
        }

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(API_URL);
            HttpResponse response = httpClient.execute(request);

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                datos = "Error HTTP: " + statusCode;
                LOG.severe(datos);
                return;
            }

            HttpEntity entity = response.getEntity();
            if (entity == null) {
                datos = "Respuesta vacia de la API";
                LOG.warning(datos);
                return;
            }

            String result = EntityUtils.toString(entity);

            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray features = jsonObject.getJSONArray("features");

                int limit = Math.min(features.length(), numEst);
                StringBuilder sb = new StringBuilder();

                for (int i = 0; i < limit; i++) {
                    JSONObject feature = features.getJSONObject(i);
                    JSONObject attributes = feature.getJSONObject("attributes");
                    JSONObject geometry = feature.getJSONObject("geometry");

                    int number = attributes.getInt("number");
                    String nombre = attributes.optString("address", "Sin direccion");
                    int bicis = attributes.getInt("available");
                    int anclajes = attributes.getInt("free");
                    double x = geometry.getDouble("x");
                    double y = geometry.getDouble("y");

                    String coords = ConversionGeoLongLat.conversion(x, y);
                    String[] partes = coords.split(",");
                    double lat = Double.parseDouble(partes[0].trim());
                    double lon = Double.parseDouble(partes[1].trim());

                    EstacionBici estacion = new EstacionBici(number, nombre, bicis, anclajes, lat, lon);
                    estaciones.add(estacion);

                    sb.append(estacion.formatear()).append("\n");
                }

                datos = sb.toString();

            } catch (org.json.JSONException e) {
                datos = "Error al procesar JSON: " + e.getMessage();
                LOG.log(Level.SEVERE, datos, e);
            }

        } catch (IOException e) {
            datos = "Error de conexion: " + e.getMessage();
            LOG.log(Level.SEVERE, datos, e);
        }
    }

    public String getDatos() { return datos; }
    public List<EstacionBici> getEstaciones() { return estaciones; }
}
