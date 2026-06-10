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
import java.util.logging.Level;
import java.util.logging.Logger;

public class ValenbiciAPI {

    private static final Logger LOG = Logger.getLogger(ValenbiciAPI.class.getName());
    private static final String API_URL = Config.get("api.valenbici.url");

    public static void main(String[] args) {
        if (API_URL == null || API_URL.isEmpty()) {
            LOG.severe("La URL de la API no esta especificada en config.properties");
            return;
        }

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(API_URL);
            LOG.info("Consultando API: " + API_URL);

            HttpResponse response = httpClient.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode != 200) {
                LOG.severe("Error HTTP: " + statusCode);
                return;
            }

            HttpEntity entity = response.getEntity();
            if (entity == null) {
                LOG.warning("Respuesta vacia de la API");
                return;
            }

            String result = EntityUtils.toString(entity);
            JSONObject jsonObject = new JSONObject(result);
            JSONArray features = jsonObject.getJSONArray("features");

            System.out.println("=== ESTACIONES VALENBISI ===");
            System.out.println("Total: " + features.length() + " estaciones\n");

            int totalBicis = 0;
            int totalLibres = 0;

            for (int i = 0; i < features.length(); i++) {
                JSONObject feature = features.getJSONObject(i);
                JSONObject attrs = feature.getJSONObject("attributes");
                JSONObject geometry = feature.getJSONObject("geometry");

                int number = attrs.getInt("number");
                String address = attrs.optString("address", "Sin direccion");
                int available = attrs.getInt("available");
                int free = attrs.getInt("free");
                int total = attrs.getInt("total");
                double x = geometry.getDouble("x");
                double y = geometry.getDouble("y");

                String coords = ConversionGeoLongLat.conversion(x, y);

                totalBicis += available;
                totalLibres += free;

                System.out.printf("  #%d | %s%n", number, address);
                System.out.printf("       Bicis: %d | Libres: %d | Total: %d%n", available, free, total);
                System.out.printf("       Coordenadas: %s%n", coords);
                System.out.println("       " + getStatusText(available, total));
                System.out.println("  " + "-".repeat(50));
            }

            System.out.printf("%nRESUMEN: %d estaciones, %d bicis disponibles, %d anclajes libres%n",
                features.length(), totalBicis, totalLibres);

        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Error de conexion con la API", e);
        } catch (org.json.JSONException e) {
            LOG.log(Level.SEVERE, "Error al procesar JSON", e);
        }
    }

    private static String getStatusText(int available, int total) {
        if (total == 0) return "Sin datos";
        double ratio = (double) available / total;
        if (ratio > 0.7) return "Bien surtido";
        if (ratio > 0.3) return "Disponibilidad moderada";
        if (ratio > 0) return "Quedan pocas bicis";
        return "Sin bicicletas disponibles";
    }
}
