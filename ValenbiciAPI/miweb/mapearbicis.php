<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mapa Valenbisi</title>
    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
    <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
    <link rel="stylesheet" href="style.css">
</head>
<body>
    <header>
        <?php
        $lang = isset($_GET['lang']) ? $_GET['lang'] : 'es';

        $langData = [
            'es' => [
                'title' => 'Mapa de Estaciones Valenbisi',
                'subtitle' => 'Ubicacion y disponibilidad',
                'lang_en' => 'English',
                'lang_es' => 'Castellano',
                'switchLang' => 'EN',
                'back' => 'Volver al listado',
                'legendTitle' => 'Bicis disponibles',
                'gt10' => "\u{2265} 10",
                'lt5' => '< 5',
                'wellStocked' => 'Bien surtido',
                'moderate' => 'Disponibilidad moderada',
                'few' => 'Quedan pocas',
                'none' => 'Sin bicis',
                'available' => 'Disponibles',
                'freeSlots' => 'Anclajes libres',
                'total' => 'Total',
                'loadError' => 'Error al cargar los datos del mapa',
            ],
            'en' => [
                'title' => 'Valenbisi Stations Map',
                'subtitle' => 'Location and availability',
                'lang_en' => 'English',
                'lang_es' => 'Castellano',
                'switchLang' => 'ES',
                'back' => 'Back to list',
                'legendTitle' => 'Bikes available',
                'gt10' => "\u{2265} 10",
                'lt5' => '< 5',
                'wellStocked' => 'Well stocked',
                'moderate' => 'Moderate availability',
                'few' => 'Few bikes left',
                'none' => 'No bikes',
                'available' => 'Available',
                'freeSlots' => 'Free slots',
                'total' => 'Total',
                'loadError' => 'Error loading map data',
            ],
        ];

        $t = $langData[$lang];
        $otherLang = $lang === 'es' ? 'en' : 'es';
        ?>
        <h1><?= $t['title'] ?></h1>
        <p class="subtitle"><?= $t['subtitle'] ?></p>
        <div class="lang-switcher">
            <a href="?lang=<?= $otherLang ?>" class="btn-lang"><?= $t['switchLang'] ?></a>
        </div>
    </header>

    <main>
        <div id="map"></div>
        <div class="actions">
            <a href="index.php?lang=<?= $lang ?>" class="btn-primary">&larr; <?= $t['back'] ?></a>
        </div>
    </main>

    <script>
        var lang = <?= json_encode($t) ?>;

        var map = L.map('map').setView([39.47, -0.37], 13);

        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>',
            maxZoom: 19
        }).addTo(map);

        function crearIcono(color) {
            return L.icon({
                iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-' + color + '.png',
                shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/images/marker-shadow.png',
                iconSize: [25, 41],
                iconAnchor: [12, 41],
                popupAnchor: [1, -34],
                shadowSize: [41, 41]
            });
        }

        function getColor(available) {
            if (available >= 10) return 'green';
            if (available >= 5) return 'orange';
            return 'red';
        }

        function getStatusText(available) {
            if (available >= 10) return lang.wellStocked;
            if (available >= 5) return lang.moderate;
            if (available > 0) return lang.few;
            return lang.none;
        }

        var markers = [];

        var legend = L.control({ position: 'bottomright' });
        legend.onAdd = function() {
            var div = L.DomUtil.create('div', 'map-legend');
            div.innerHTML = '<strong>' + lang.legendTitle + '</strong><br>' +
                '<span class="legend-dot" style="background:#4caf50;"></span> ' + lang.gt10 + '<br>' +
                '<span class="legend-dot" style="background:#ff9800;"></span> 5-9<br>' +
                '<span class="legend-dot" style="background:#f44336;"></span> ' + lang.lt5;
            return div;
        };
        legend.addTo(map);

        fetch('data.json')
            .then(function(response) {
                if (!response.ok) throw new Error('Error al cargar datos');
                return response.json();
            })
            .then(function(data) {
                var stations = Object.values(data);

                stations.forEach(function(station) {
                    var color = getColor(station.available);
                    if (station.latitude && station.longitude) {
                        var marker = L.marker([station.latitude, station.longitude], {
                            icon: crearIcono(color)
                        });
                        marker.bindPopup(
                            '<div class="popup-content">' +
                            '<strong>' + station.address + '</strong><br><br>' +
                            lang.available + ': <strong>' + station.available + '</strong><br>' +
                            lang.freeSlots + ': ' + station.free + '<br>' +
                            lang.total + ': ' + station.total + '<br><br>' +
                            '<span class="status-badge status-' + color + '">' + getStatusText(station.available) + '</span>' +
                            '</div>'
                        );
                        markers.push(marker);
                        marker.addTo(map);
                    }
                });

                var bounds = [];
                stations.forEach(function(s) {
                    if (s.latitude && s.longitude) {
                        bounds.push([s.latitude, s.longitude]);
                    }
                });
                if (bounds.length > 0) {
                    map.fitBounds(bounds, { padding: [50, 50] });
                }
            })
            .catch(function(error) {
                console.error('Error:', error);
                document.getElementById('map').innerHTML =
                    '<p class="message error">' + lang.loadError + '</p>';
            });
    </script>
</body>
</html>
