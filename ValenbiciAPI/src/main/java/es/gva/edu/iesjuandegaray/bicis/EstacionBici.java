package es.gva.edu.iesjuandegaray.bicis;

public class EstacionBici {

    private final int number;
    private final String address;
    private final int available;
    private final int free;
    private final double lat;
    private final double lon;

    public EstacionBici(int number, String address, int available, int free, double lat, double lon) {
        this.number = number;
        this.address = address != null ? address : "Sin direccion";
        this.available = available;
        this.free = free;
        this.lat = lat;
        this.lon = lon;
    }

    public int getNumber() { return number; }
    public String getAddress() { return address; }
    public int getAvailable() { return available; }
    public int getFree() { return free; }
    public int getTotal() { return available + free; }
    public double getLat() { return lat; }
    public double getLon() { return lon; }

    public String formatear() {
        return String.format("Estacion #%d - %s | Bicis: %d | Libres: %d | Total: %d | Lat: %.6f | Lon: %.6f",
            number, address, available, free, getTotal(), lat, lon);
    }

    public String toValuesString() {
        return number + "|" + address + "|" + available + "|" + free + "|" + lat + "|" + lon;
    }

    @Override
    public String toString() {
        return formatear();
    }
}
