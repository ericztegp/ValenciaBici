<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Project Summary & Glossary</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
    <header>
        <?php
        $lang = isset($_GET['lang']) ? $_GET['lang'] : 'es';

        $text = [
            'es' => [
                'title' => 'Resumen y Glosario',
                'subtitle' => 'ValenbiciJDG Project',
                'lang_en' => 'English',
                'lang_es' => 'Castellano',
                'switchLang' => 'EN',
                'summary' => 'Resumen del Proyecto',
                'glossary' => 'Glosario',
                'back' => 'Volver al listado',
                'englishSummary' => 'ValenbiciJDG is a Java-based web application that provides real-time information about Valenbisi bicycle stations in Valencia, Spain. The project integrates with the Valencia City Council geospatial API to retrieve station data, including location, bicycle availability, free anchor points, and operational status. It features a bilingual (Spanish/English) web interface with two main pages: a list view displaying all stations in a table format, and an interactive map that shows station markers color-coded by availability. The backend handles coordinate conversion, stores data in JSON format, and connects to a MySQL database for persistent storage. The project aims to promote sustainable urban mobility by making real-time bike-sharing data accessible and easy to use for citizens and visitors in Valencia.

The application is designed with a clean and intuitive user interface, allowing users to quickly find the nearest station and check whether bicycles or free anchor points are available in real time. Each station displays updated information about its current operational status, helping users plan their routes more efficiently. The system retrieves and processes geospatial data automatically, ensuring that the information shown is always accurate and up to date.',
                'glossaryTerms' => [
                    ['Station', 'Estación', 'A fixed location where Valenbisi bicycles are parked and available for rent.'],
                    ['Availability', 'Disponibilidad', 'The number of bicycles currently available for use at a station.'],
                    ['Free anchor points', 'Anclajes libres', 'Empty docking slots where bicycles can be returned.'],
                    ['API', 'API', 'Application Programming Interface; allows the application to retrieve data from external services.'],
                    ['Leaflet.js', 'Leaflet.js', 'An open-source JavaScript library used to display interactive maps.'],
                    ['EPSG:25830', 'EPSG:25830', 'A coordinate reference system used by Spanish geographic data.'],
                    ['JSON', 'JSON', 'JavaScript Object Notation; a lightweight data format used to store and exchange information.'],
                    ['Real-time', 'Tiempo real', 'Data that is updated immediately as conditions change.'],
                    ['Marker', 'Marcador', 'An icon on the map representing a station location.'],
                    ['Popup', 'Popup', 'A small window that appears when clicking a marker, showing station details.'],
                    ['Sustainable mobility', 'Movilidad sostenible', 'Transportation methods that reduce environmental impact.'],
                    ['Bike-sharing', 'Bicicleta compartida', 'A service where bicycles are available for shared use at various stations.'],
                ],
            ],
            'en' => [
                'title' => 'Summary and Glossary',
                'subtitle' => 'ValenbiciJDG Project',
                'lang_en' => 'English',
                'lang_es' => 'Castellano',
                'switchLang' => 'ES',
                'summary' => 'Project Summary',
                'glossary' => 'Glossary',
                'back' => 'Back to list',
                'englishSummary' => 'ValenbiciJDG is a Java-based web application that provides real-time information about Valenbisi bicycle stations in Valencia, Spain. The project integrates with the Valencia City Council geospatial API to retrieve station data, including location, bicycle availability, free anchor points, and operational status. It features a bilingual (Spanish/English) web interface with two main pages: a list view displaying all stations in a table format, and an interactive map that shows station markers color-coded by availability. The backend handles coordinate conversion, stores data in JSON format, and connects to a MySQL database for persistent storage. The project aims to promote sustainable urban mobility by making real-time bike-sharing data accessible and easy to use for citizens and visitors in Valencia.

The application is designed with a clean and intuitive user interface, allowing users to quickly find the nearest station and check whether bicycles or free anchor points are available in real time. Each station displays updated information about its current operational status, helping users plan their routes more efficiently. The system retrieves and processes geospatial data automatically, ensuring that the information shown is always accurate and up to date.',
                'glossaryTerms' => [
                    ['Station', 'Estación', 'A fixed location where Valenbisi bicycles are parked and available for rent.'],
                    ['Availability', 'Disponibilidad', 'The number of bicycles currently available for use at a station.'],
                    ['Free anchor points', 'Anclajes libres', 'Empty docking slots where bicycles can be returned.'],
                    ['API', 'API', 'Application Programming Interface; allows the application to retrieve data from external services.'],
                    ['Leaflet.js', 'Leaflet.js', 'An open-source JavaScript library used to display interactive maps.'],
                    ['EPSG:25830', 'EPSG:25830', 'A coordinate reference system used by Spanish geographic data.'],
                    ['JSON', 'JSON', 'JavaScript Object Notation; a lightweight data format used to store and exchange information.'],
                    ['Real-time', 'Tiempo real', 'Data that is updated immediately as conditions change.'],
                    ['Marker', 'Marcador', 'An icon on the map representing a station location.'],
                    ['Popup', 'Popup', 'A small window that appears when clicking a marker, showing station details.'],
                    ['Sustainable mobility', 'Movilidad sostenible', 'Transportation methods that reduce environmental impact.'],
                    ['Bike-sharing', 'Bicicleta compartida', 'A service where bicycles are available for shared use at various stations.'],
                ],
            ],
        ];

        $t = $text[$lang];
        $otherLang = $lang === 'es' ? 'en' : 'es';
        ?>

        <h1><?= $t['title'] ?></h1>
        <p class="subtitle"><?= $t['subtitle'] ?></p>

        <div class="lang-switcher">
            <a href="?lang=<?= $otherLang ?>" class="btn-lang">
                <?= $t['switchLang'] ?>
            </a>
        </div>
    </header>

    <main>
        <div class="english-section">
            <h2><?= $t['summary'] ?></h2>
            <p><?= nl2br($t['englishSummary']) ?></p>

            <h2><?= $t['glossary'] ?></h2>

            <table class="glossary">
                <thead>
                    <tr>
                        <th>English</th>
                        <th>Español</th>
                        <th>Definition</th>
                    </tr>
                </thead>
                <tbody>
                    <?php foreach ($t['glossaryTerms'] as $term): ?>
                    <tr>
                        <td><?= $term[0] ?></td>
                        <td><?= $term[1] ?></td>
                        <td><?= $term[2] ?></td>
                    </tr>
                    <?php endforeach; ?>
                </tbody>
            </table>
        </div>

        <div class="actions">
            <a href="index.php?lang=<?= $lang ?>" class="btn-primary">
                &larr; <?= $t['back'] ?>
            </a>
        </div>
    </main>
</body>
</html>