package es.gva.edu.iesjuandegaray.bicis;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EstacionBiciTest {

    @Test
    void testCreacionEstacion() {
        EstacionBici e = new EstacionBici(1, "Calle Mayor 10", 5, 10, 39.47, -0.37);
        assertEquals(1, e.getNumber());
        assertEquals("Calle Mayor 10", e.getAddress());
        assertEquals(5, e.getAvailable());
        assertEquals(10, e.getFree());
        assertEquals(15, e.getTotal());
        assertEquals(39.47, e.getLat(), 0.001);
        assertEquals(-0.37, e.getLon(), 0.001);
    }

    @Test
    void testDireccionNula() {
        EstacionBici e = new EstacionBici(1, null, 3, 7, 39.47, -0.37);
        assertEquals("Sin direccion", e.getAddress());
    }

    @Test
    void testFormatear() {
        EstacionBici e = new EstacionBici(5, "Plaza Ayuntamiento", 8, 12, 39.47, -0.37);
        String formato = e.formatear();
        assertTrue(formato.contains("#5"));
        assertTrue(formato.contains("Plaza Ayuntamiento"));
        assertTrue(formato.contains("8"));
        assertTrue(formato.contains("12"));
        assertTrue(formato.contains("20"));
    }

    @Test
    void testToValuesString() {
        EstacionBici e = new EstacionBici(1, "Calle Mayor", 3, 7, 39.47, -0.37);
        String valores = e.toValuesString();
        assertEquals("1|Calle Mayor|3|7|39.47|-0.37", valores);
    }
}
