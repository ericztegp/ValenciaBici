package es.gva.edu.iesjuandegaray.bicis;

import org.locationtech.proj4j.CRSFactory;
import org.locationtech.proj4j.CoordinateReferenceSystem;
import org.locationtech.proj4j.CoordinateTransform;
import org.locationtech.proj4j.CoordinateTransformFactory;
import org.locationtech.proj4j.ProjCoordinate;

import java.util.logging.Logger;

public class ConversionGeoLongLat {

    private static final Logger LOG = Logger.getLogger(ConversionGeoLongLat.class.getName());
    private static final CRSFactory crsFactory = new CRSFactory();
    private static final CoordinateTransform transform = crearTransformacion();
    private static final ProjCoordinate utmCoord = new ProjCoordinate();
    private static final ProjCoordinate latLon = new ProjCoordinate();

    private ConversionGeoLongLat() {}

    private static CoordinateTransform crearTransformacion() {
        CoordinateReferenceSystem utm = crsFactory.createFromParameters(
            "ETRS89_UTM30",
            "+proj=utm +zone=30 +datum=WGS84 +units=m +no_defs"
        );
        CoordinateReferenceSystem wgs84 = crsFactory.createFromParameters(
            "WGS84",
            "+proj=longlat +datum=WGS84 +no_defs"
        );
        CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
        return ctFactory.createTransform(utm, wgs84);
    }

    public static String conversion(double xGeom, double yGeom) {
        if (Double.isNaN(xGeom) || Double.isNaN(yGeom)) {
            LOG.warning("Coordenadas invalidas: (" + xGeom + ", " + yGeom + ")");
            return "0.0, 0.0";
        }

        synchronized (transform) {
            utmCoord.x = xGeom;
            utmCoord.y = yGeom;
            transform.transform(utmCoord, latLon);
        }

        return latLon.y + ", " + latLon.x;
    }
}
