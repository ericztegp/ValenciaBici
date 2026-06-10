CREATE DATABASE IF NOT EXISTS valenbicibd;

USE valenbicibd;

CREATE TABLE IF NOT EXISTS historico (
    id INT AUTO_INCREMENT PRIMARY KEY,
    estacion_id INT NOT NULL,
    direccion VARCHAR(255),
    bicis_disponibles INT NOT NULL,
    anclajes_libres INT NOT NULL,
    estado_operativo BOOLEAN NOT NULL DEFAULT true,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ubicacion POINT NOT NULL SRID 4326,
    SPATIAL INDEX idx_ubicacion (ubicacion),
    INDEX idx_estacion_id (estacion_id),
    INDEX idx_fecha_registro (fecha_registro)
);
