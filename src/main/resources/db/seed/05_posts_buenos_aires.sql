-- ==========================================
-- SEED: 05 - POSTS - BUENOS AIRES
-- IDs: 1–7  |  location_ids: 1–7
-- ==========================================

INSERT INTO post (id, title, content, date, posted_at, is_by_ia, category, status, author_id, location_id) VALUES
(1,
 'Fundación de Buenos Aires',
 'El 11 de junio de 1580, Juan de Garay refundó Buenos Aires con el nombre de "Ciudad de la Santísima Trinidad y Puerto de Santa María del Buen Ayre". Esta segunda fundación fue exitosa.',
 '1580-06-11', '2024-01-25', false, 'STORY', 'ACTIVE', 3, 1),

(2,
 'Construcción del Obelisco',
 'El 23 de mayo de 1936 se inauguró el Obelisco de Buenos Aires para conmemorar el cuarto centenario de la primera fundación de la ciudad. Con 67 metros de altura, se convirtió en el símbolo de la ciudad.',
 '1936-05-23', '2024-01-26', false, 'INFORMATION', 'ACTIVE', 1, 2),

(3,
 'Puerto Madero: De Puerto a Barrio',
 'En 1989 comenzó la renovación de Puerto Madero, transformando el antiguo puerto en el barrio más moderno de Buenos Aires. Este proyecto urbano se convirtió en modelo para otras ciudades del mundo.',
 '1989-01-01', '2024-01-27', false, 'STORY', 'ACTIVE', 2, 3),

(4,
 'El Tango en La Boca',
 'A principios del siglo XX, el barrio de La Boca se convirtió en la cuna del tango. En sus conventillos y cafés nacieron los primeros tangos que conquistarían el mundo.',
 '1900-01-01', '2024-01-28', false, 'STORY', 'ACTIVE', 4, 4),

(5,
 'El Cementerio de la Recoleta',
 'Inaugurado en 1822, el Cementerio de la Recoleta se convirtió en el más prestigioso de Buenos Aires. Aquí descansan presidentes, escritores y personalidades como Eva Perón.',
 '1822-11-17', '2024-01-29', false, 'INFORMATION', 'ACTIVE', 3, 5),

(6,
 'Los Bosques de Palermo',
 'En 1875 se inauguraron los Bosques de Palermo, el pulmón verde de Buenos Aires. Estos jardines fueron diseñados por el paisajista francés Charles Thays y se convirtieron en el paseo favorito de los porteños.',
 '1875-11-11', '2024-01-30', false, 'INFORMATION', 'ACTIVE', 1, 6),

(7,
 'El Mercado de San Telmo',
 'Desde 1897, el Mercado de San Telmo ha sido el corazón comercial del barrio más antiguo de Buenos Aires. Sus puestos de antigüedades y su arquitectura de hierro lo convierten en un ícono porteño.',
 '1897-01-01', '2024-01-31', false, 'STORY', 'ACTIVE', 2, 7);
