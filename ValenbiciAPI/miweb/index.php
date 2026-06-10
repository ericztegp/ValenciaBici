<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Valenbisi</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
    <header>
        <?php
        $lang = isset($_GET['lang']) ? $_GET['lang'] : 'es';

        $text = [
            'es' => [
                'title' => 'Valenbisi',
                'subtitle' => 'Disponibilidad de estaciones en Valencia',
                'address' => 'Direccion',
                'num' => 'Num',
                'status' => 'Estado',
                'available' => 'Disponibles',
                'free' => 'Libres',
                'total' => 'Total',
                'updated' => 'Actualizado',
                'location' => 'Ubicacion',
                'open' => 'Si',
                'closed' => 'No',
                'noData' => 'Sin datos',
                'wellStocked' => 'Bien surtido',
                'moderate' => 'Moderado',
                'few' => 'Pocas bicis',
                'empty' => 'Vacio',
                'map' => 'Ver mapa interactivo',
                'connectionError' => 'Error de conexion',
                'httpError' => 'Error HTTP',
                'jsonError' => 'Error al decodificar JSON',
                'noStations' => 'No se encontraron estaciones',
                'lang_en' => 'English',
                'lang_es' => 'Castellano',
                'switchLang' => 'EN',
                'summary' => 'Resumen y Glosario',
            ],
            'en' => [
                'title' => 'Valenbisi',
                'subtitle' => 'Station Availability in Valencia',
                'address' => 'Address',
                'num' => 'Num',
                'status' => 'Status',
                'available' => 'Available',
                'free' => 'Free',
                'total' => 'Total',
                'updated' => 'Updated',
                'location' => 'Location',
                'open' => 'Yes',
                'closed' => 'No',
                'noData' => 'No data',
                'wellStocked' => 'Well stocked',
                'moderate' => 'Moderate',
                'few' => 'Few bikes',
                'empty' => 'Empty',
                'map' => 'View interactive map',
                'connectionError' => 'Connection error',
                'httpError' => 'HTTP error',
                'jsonError' => 'Error decoding JSON',
                'noStations' => 'No stations found',
                'lang_en' => 'English',
                'lang_es' => 'Castellano',
                'switchLang' => 'ES',
                'summary' => 'Summary and Glossary',
            ],
        ];

        $t = $text[$lang];
        $otherLang = $lang === 'es' ? 'en' : 'es';
        ?>
        <h1><?= $t['title'] ?></h1>
        <p class="subtitle"><?= $t['subtitle'] ?></p>
        <div class="lang-switcher">
            <a href="?lang=<?= $otherLang ?>" class="btn-lang"><?= $t['switchLang'] ?></a>
        </div>
    </header>

    <main>
        <?php
        $baseUrl = "https://geoportal.valencia.es/server/rest/services/OPENDATA/Trafico/MapServer/228/query?where=1=1&outFields=*&returnGeometry=true&outSR=4326&f=json";

        function epsg25830ToWgs84($easting, $northing) {
            $a = 6378137.0;
            $f = 1 / 298.257222101;
            $k0 = 0.9996;
            $zone = 30;
            $falseEasting = 500000.0;
            $falseNorthing = 0.0;
            $e = sqrt($f * (2 - $f));
            $e1sq = ($e * $e) / (1 - $e * $e);
            $x = $easting - $falseEasting;
            $y = $northing - $falseNorthing;
            $m = $y / $k0;
            $mu = $m / ($a * (1 - pow($e, 2) / 4 - 3 * pow($e, 4) / 64 - 5 * pow($e, 6) / 256));
            $e1 = (1 - sqrt(1 - $e * $e)) / (1 + sqrt(1 - $e * $e));
            $j1 = 3 * $e1 / 2 - 27 * pow($e1, 3) / 32;
            $j2 = 21 * pow($e1, 2) / 16 - 55 * pow($e1, 4) / 32;
            $j3 = 151 * pow($e1, 3) / 96;
            $j4 = 1097 * pow($e1, 4) / 512;
            $fp = $mu + $j1 * sin(2 * $mu) + $j2 * sin(4 * $mu) + $j3 * sin(6 * $mu) + $j4 * sin(8 * $mu);
            $sinFp = sin($fp);
            $cosFp = cos($fp);
            $tanFp = tan($fp);
            $c1 = $e1sq * $cosFp * $cosFp;
            $t1 = $tanFp * $tanFp;
            $r1 = $a * (1 - $e * $e) / pow(1 - ($e * $e * $sinFp * $sinFp), 1.5);
            $n1 = $a / sqrt(1 - ($e * $e * $sinFp * $sinFp));
            $d = $x / ($n1 * $k0);
            $latRad = $fp - ($n1 * $tanFp / $r1) * (
                pow($d, 2) / 2
                - (5 + 3 * $t1 + 10 * $c1 - 4 * $c1 * $c1 - 9 * $e1sq) * pow($d, 4) / 24
                + (61 + 90 * $t1 + 298 * $c1 + 45 * $t1 * $t1 - 252 * $e1sq - 3 * $c1 * $c1) * pow($d, 6) / 720
            );
            $lonOrigin = deg2rad(($zone - 1) * 6 - 180 + 3);
            $lonRad = $lonOrigin + (
                $d
                - (1 + 2 * $t1 + $c1) * pow($d, 3) / 6
                + (5 - 2 * $c1 + 28 * $t1 - 3 * $c1 * $c1 + 8 * $e1sq + 24 * $t1 * $t1) * pow($d, 5) / 120
            ) / $cosFp;
            return array('latitude' => rad2deg($latRad), 'longitude' => rad2deg($lonRad));
        }

        function normalizarCoordenadas($geometry) {
            $x = isset($geometry['x']) ? (float)$geometry['x'] : 0.0;
            $y = isset($geometry['y']) ? (float)$geometry['y'] : 0.0;
            if ($x >= -180 && $x <= 180 && $y >= -90 && $y <= 90) {
                return array('latitude' => $y, 'longitude' => $x);
            }
            $conv = epsg25830ToWgs84($x, $y);
            return array('latitude' => $conv['latitude'], 'longitude' => $conv['longitude']);
        }

        function getStatusInfo($available, $total, $t) {
            if ($total == 0) return array('text' => $t['noData'], 'class' => 'status-unknown');
            $ratio = $available / $total;
            if ($ratio > 0.7) return array('text' => $t['wellStocked'], 'class' => 'status-good');
            if ($ratio > 0.3) return array('text' => $t['moderate'], 'class' => 'status-moderate');
            if ($ratio > 0) return array('text' => $t['few'], 'class' => 'status-low');
            return array('text' => $t['empty'], 'class' => 'status-empty');
        }

        $allStations = array();
        $curl = curl_init();
        curl_setopt_array($curl, array(
            CURLOPT_URL => $baseUrl,
            CURLOPT_RETURNTRANSFER => true,
            CURLOPT_HTTPHEADER => array('Accept: application/json'),
            CURLOPT_SSL_VERIFYPEER => false,
            CURLOPT_TIMEOUT => 30,
        ));
        $response = curl_exec($curl);

        if ($response === false) {
            echo "<div class='message error'>" . $t['connectionError'] . ": " . curl_error($curl) . "</div>";
            curl_close($curl);
            die("</main></body></html>");
        }
        $httpCode = curl_getinfo($curl, CURLINFO_HTTP_CODE);
        curl_close($curl);

        if ($httpCode !== 200) {
            echo "<div class='message error'>" . $t['httpError'] . ": $httpCode</div>";
            die("</main></body></html>");
        }

        $data = json_decode($response, true);
        if ($data === null) {
            echo "<div class='message error'>" . $t['jsonError'] . "</div>";
            die("</main></body></html>");
        }

        if (isset($data['features']) && is_array($data['features'])) {
            foreach ($data['features'] as $station) {
                $geometry = normalizarCoordenadas($station['geometry']);
                $number = $station['attributes']['number'];
                $allStations[$number] = array(
                    'address' => $station['attributes']['address'],
                    'open' => ($station['attributes']['open'] === 'T'),
                    'available' => (int)$station['attributes']['available'],
                    'free' => (int)$station['attributes']['free'],
                    'total' => (int)$station['attributes']['total'],
                    'updated_at' => $station['attributes']['updated_at'],
                    'latitude' => round($geometry['latitude'], 7),
                    'longitude' => round($geometry['longitude'], 7),
                );
            }
        }

        if (!empty($allStations)) {
            $jsonPath = __DIR__ . '/data.json';
            file_put_contents($jsonPath, json_encode($allStations, JSON_PRETTY_PRINT));

            echo "<div class='table-wrapper'>";
            echo "<table>";
            echo "<thead><tr><th>" . $t['address'] . "</th><th>" . $t['num'] . "</th><th>" . $t['status'] . "</th><th>" . $t['available'] . "</th><th>" . $t['free'] . "</th><th>" . $t['total'] . "</th><th>" . $t['updated'] . "</th><th>" . $t['location'] . "</th></tr></thead>";
            echo "<tbody>";
            foreach ($allStations as $number => $station) {
                $status = getStatusInfo($station['available'], $station['total'], $t);
                $openBadge = $station['open'] ? "<span class='badge-open'>" . $t['open'] . "</span>" : "<span class='badge-closed'>" . $t['closed'] . "</span>";
                echo "<tr>";
                echo "<td class='td-address'>" . htmlspecialchars($station['address']) . "</td>";
                echo "<td>$number</td>";
                echo "<td>$openBadge</td>";
                echo "<td class='num-" . $status['class'] . "'>" . $station['available'] . "</td>";
                echo "<td>" . $station['free'] . "</td>";
                echo "<td>" . $station['total'] . "</td>";
                echo "<td>" . htmlspecialchars($station['updated_at']) . "</td>";
                printf("<td>%.4f, %.4f</td>", $station['latitude'], $station['longitude']);
                echo "</tr>";
            }
            echo "</tbody>";
            echo "</table>";
            echo "</div>";

            echo "<div class='actions'>";
            echo "<a href='mapearbicis.php?lang=$lang' class='btn-primary'>" . $t['map'] . "</a>";

            echo "</div>";
        } else {
            echo "<div class='message warning'>" . $t['noStations'] . "</div>";
        }
        ?>
    </main>

</body>
</html>
