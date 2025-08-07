-- ===================================
-- MyPresentPast - Datos de Precarga
-- Eventos Históricos de Argentina
-- ===================================
-- Este archivo se ejecuta automáticamente por Hibernate
-- después de crear las tablas (import.sql)

-- 1. USUARIOS
INSERT INTO user_account (profile_username, email, password, role, avatar) VALUES ('mferradans', 'mateo@mail.com', '$2a$10$NROW7R3GTc7oPD3O2KuXROOc/uEoah1xABCsxJjafciPvMoWzQU5y', 'ADMIN', 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=400');
INSERT INTO user_account (profile_username, email, password, role, avatar) VALUES ('Museo Nacional Argentino','museo@mail.com', '$2a$10$NROW7R3GTc7oPD3O2KuXROOc/uEoah1xABCsxJjafciPvMoWzQU5y', 'INSTITUTION', 'https://images.unsplash.com/photo-1566127992631-137a642a90f4?w=400');
INSERT INTO user_account (profile_username, email, password, role, avatar) VALUES ('Historia Argentina', 'historia@mail.com','$2a$10$NROW7R3GTc7oPD3O2KuXROOc/uEoah1xABCsxJjafciPvMoWzQU5y', 'NORMAL', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400');

-- 2. UBICACIONES
INSERT INTO location (address, latitude, longitude) VALUES ('Villa María, Córdoba, Argentina', -32.4075, -63.2406);
INSERT INTO location (address, latitude, longitude) VALUES ('Plaza de Mayo, Buenos Aires, Argentina', -34.6082, -58.3716);
INSERT INTO location (address, latitude, longitude) VALUES ('Teatro Colón, Buenos Aires, Argentina', -34.6010, -58.3835);
INSERT INTO location (address, latitude, longitude) VALUES ('Mendoza, Argentina', -32.8895, -68.8458);
INSERT INTO location (address, latitude, longitude) VALUES ('Villa General Belgrano, Córdoba, Argentina', -31.9785, -64.5431);
INSERT INTO location (address, latitude, longitude) VALUES ('Ushuaia, Tierra del Fuego, Argentina', -54.8019, -68.3030);
INSERT INTO location (address, latitude, longitude) VALUES ('Bariloche, Río Negro, Argentina', -41.1335, -71.3103);
INSERT INTO location (address, latitude, longitude) VALUES ('Rosario, Santa Fe, Argentina', -32.9442, -60.6505);
INSERT INTO location (address, latitude, longitude) VALUES ('Córdoba Capital, Argentina', -31.4201, -64.1888);
INSERT INTO location (address, latitude, longitude) VALUES ('La Plata, Buenos Aires, Argentina', -34.9205, -57.9536);

-- 3. POSTS SOBRE EVENTOS HISTÓRICOS ARGENTINOS
-- Evento icónico: La nevada en Villa María
INSERT INTO post (title, content, date, posted_at, is_by_ia, is_verified, category, status, author_id, location_id) VALUES ('La Histórica Nevada de Villa María - 9 de Julio de 2006', 'Un evento extraordinario que marcó la historia de Villa María: el 9 de julio de 2006, día patrio, la ciudad se despertó cubierta de nieve. Fue la primera nevada en más de 80 años, coincidiendo con las celebraciones del Día de la Independencia. Los habitantes salieron a las calles a fotografiar este fenómeno único, convirtiendo la fecha patria en un día aún más memorable. La temperatura descendió a -8°C, algo inusual para esta región de Córdoba.', '2006-07-09', '2024-01-15', false, true, 'STORY', 'ACTIVE', 1, 1);

-- Eventos de Buenos Aires
INSERT INTO post (title, content, date, posted_at, is_by_ia, is_verified, category, status, author_id, location_id) VALUES ('Fundación de Buenos Aires por Pedro de Mendoza', 'El 2 de febrero de 1536, el adelantado Pedro de Mendoza fundó por primera vez Buenos Aires con el nombre de "Puerto de Nuestra Señora Santa María del Buen Ayre". Esta primera fundación fracasó debido a los conflictos con los pueblos originarios y el hambre. La ciudad sería refundada 44 años después por Juan de Garay en 1580, estableciendo definitivamente lo que hoy conocemos como Buenos Aires.', '1536-02-02', '2024-01-10', false, true, 'STORY', 'ACTIVE', 2, 2);

INSERT INTO post (title, content, date, posted_at, is_by_ia, is_verified, category, status, author_id, location_id) VALUES ('Inauguración del Teatro Colón', 'El 25 de mayo de 1908 se inauguró oficialmente el Teatro Colón de Buenos Aires con la ópera "Aida" de Giuseppe Verdi. Considerado uno de los teatros de ópera más importantes del mundo, su construcción llevó 20 años. El edificio combina estilos arquitectónicos neoclásico, italiano y francés, y su acústica es reconocida mundialmente como una de las mejores del planeta.', '1908-05-25', '2024-01-20', false, true, 'INFORMATION', 'ACTIVE', 2, 3);

-- Eventos del interior
INSERT INTO post (title, content, date, posted_at, is_by_ia, is_verified, category, status, author_id, location_id) VALUES ('El Terremoto de Mendoza de 1861', 'El 20 de marzo de 1861, un devastador terremoto de magnitud 7.2 destruyó completamente la ciudad de Mendoza. El sismo ocurrió a las 23:30 y causó la muerte de aproximadamente 6.000 personas, prácticamente toda la población de la ciudad. Este evento marcó un antes y un después en la historia mendocina, ya que la ciudad fue completamente reconstruida con nuevos criterios antisísmicos y un diseño urbano diferente.', '1861-03-20', '2024-02-05', false, true, 'STORY', 'ACTIVE', 3, 4);

INSERT INTO post (title, content, date, posted_at, is_by_ia, is_verified, category, status, author_id, location_id) VALUES ('La Fundación de Villa General Belgrano por los Alemanes', 'Tras el hundimiento del acorazado Admiral Graf Spee en 1939, muchos marinos alemanes fueron internados en Argentina. Algunos de ellos se establecieron en lo que hoy es Villa General Belgrano, en las sierras de Córdoba, fundando esta pintoresca localidad de arquitectura bávara. La ciudad se convirtió en el corazón de la comunidad alemana en Argentina y es famosa por su Oktoberfest, la fiesta de la cerveza más importante del país.', '1940-05-15', '2024-02-10', false, false, 'STORY', 'ACTIVE', 1, 5);

-- Eventos de la Patagonia
INSERT INTO post (title, content, date, posted_at, is_by_ia, is_verified, category, status, author_id, location_id) VALUES ('El Perito Moreno y la Exploración de la Patagonia', 'Francisco Pascasio Moreno, conocido como "Perito Moreno", realizó entre 1876 y 1890 múltiples expediciones a la Patagonia, siendo uno de los primeros exploradores científicos de la región. Sus estudios fueron fundamentales para la demarcación de límites con Chile y para el conocimiento geográfico y antropológico de la región. El famoso glaciar que lleva su nombre fue bautizado en su honor por sus contribuciones a la exploración patagónica.', '1876-11-20', '2024-02-15', false, true, 'INFORMATION', 'ACTIVE', 2, 6);

INSERT INTO post (title, content, date, posted_at, is_by_ia, is_verified, category, status, author_id, location_id) VALUES ('La Fundación de San Carlos de Bariloche', 'En 1902 se fundó oficialmente San Carlos de Bariloche, aunque la región ya había sido explorada desde finales del siglo XIX. La llegada del ferrocarril en 1934 transformó esta pequeña localidad en el destino turístico más importante de la Patagonia argentina. La ciudad se desarrolló inspirada en la arquitectura alpina suiza, convirtiéndose en la "Suiza Argentina".', '1902-05-03', '2024-02-20', false, false, 'INFORMATION', 'ACTIVE', 3, 7);

-- Eventos de Santa Fe
INSERT INTO post (title, content, date, posted_at, is_by_ia, is_verified, category, status, author_id, location_id) VALUES ('La Revolución de los Constituyentes en Rosario', 'El 11 de septiembre de 1893 estalló en Rosario la "Revolución de los Constituyentes", un levantamiento cívico-militar contra el gobierno de Luis Sáenz Peña. La revolución tuvo apoyo popular y fue liderada por la Unión Cívica Radical. Rosario se convirtió en el epicentro de la resistencia política, marcando un hito en la lucha por los derechos democráticos en Argentina.', '1893-09-11', '2024-02-25', false, true, 'STORY', 'ACTIVE', 1, 8);

-- Eventos de Córdoba
INSERT INTO post (title, content, date, posted_at, is_by_ia, is_verified, category, status, author_id, location_id) VALUES ('La Reforma Universitaria de 1918', 'El 15 de junio de 1918 comenzó en Córdoba la histórica Reforma Universitaria, un movimiento estudiantil que revolucionó la educación superior en Argentina y América Latina. Los estudiantes de la Universidad Nacional de Córdoba se rebelaron contra el sistema educativo tradicional, exigiendo cogobierno, libertad de cátedra y acceso democrático a la universidad. Este movimiento se extendió por toda Latinoamérica.', '1918-06-15', '2024-03-01', false, true, 'STORY', 'ACTIVE', 2, 9);

-- Eventos de La Plata
INSERT INTO post (title, content, date, posted_at, is_by_ia, is_verified, category, status, author_id, location_id) VALUES ('Fundación de La Plata - La Ciudad Planificada', 'El 19 de noviembre de 1882 se fundó La Plata como nueva capital de la provincia de Buenos Aires. Fue la primera ciudad planificada de Argentina, diseñada por el arquitecto francés Pierre Benoit con un trazado geométrico perfecto, plazas cada seis cuadras y diagonales que se cruzan en el centro. La ciudad se construyó en tiempo récord y fue considerada una maravilla urbanística de la época.', '1882-11-19', '2024-03-05', false, true, 'INFORMATION', 'ACTIVE', 3, 10);

-- Leyendas y mitos argentinos
INSERT INTO post (title, content, date, posted_at, is_by_ia, is_verified, category, status, author_id, location_id) VALUES ('La Leyenda del Calafate Patagónico', 'Cuenta la leyenda tehuelche que una joven india llamada Calafate se enamoró de un cacique de una tribu enemiga. Cuando su padre se opuso al amor, ella huyó hacia el sur, donde murió de pena. Los dioses, compadecidos, la transformaron en el arbusto de calafate, con frutos dulces de color azul violáceo. Se dice que quien come el fruto del calafate, siempre regresa a la Patagonia.', '1200-01-01', '2024-03-10', false, false, 'MYTH', 'ACTIVE', 1, 6);

-- 4. MEDIA PARA LOS POSTS
INSERT INTO media (type, url, post_id) VALUES ('IMAGE', 'https://images.unsplash.com/photo-1547036967-23d11aacaee0?w=800', 1);
INSERT INTO media (type, url, post_id) VALUES ('IMAGE', 'https://images.unsplash.com/photo-1578662996442-48f60103fc96?w=800', 1);
INSERT INTO media (type, url, post_id) VALUES ('IMAGE', 'https://images.unsplash.com/photo-1589909202802-8f4aadce1849?w=800', 2);
INSERT INTO media (type, url, post_id) VALUES ('IMAGE', 'https://images.unsplash.com/photo-1567538096630-e0c55bd6374c?w=800', 3);
INSERT INTO media (type, url, post_id) VALUES ('IMAGE', 'https://images.unsplash.com/photo-1590736969955-71cc94901144?w=800', 4);
INSERT INTO media (type, url, post_id) VALUES ('IMAGE', 'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=800', 5);
INSERT INTO media (type, url, post_id) VALUES ('IMAGE', 'https://images.unsplash.com/photo-1570993620540-c3986fb7f0bf?w=800', 6);
INSERT INTO media (type, url, post_id) VALUES ('IMAGE', 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=800', 7);
INSERT INTO media (type, url, post_id) VALUES ('IMAGE', 'https://images.unsplash.com/photo-1572276596237-5db2c3e16c5d?w=800', 8);
INSERT INTO media (type, url, post_id) VALUES ('IMAGE', 'https://images.unsplash.com/photo-1541745537411-b8046dc6d66c?w=800', 9);
INSERT INTO media (type, url, post_id) VALUES ('IMAGE', 'https://images.unsplash.com/photo-1581833971358-2c8b550f87b3?w=800', 10);
INSERT INTO media (type, url, post_id) VALUES ('IMAGE', 'https://images.unsplash.com/photo-1520637836862-4d197d17c16a?w=800', 11);

-- 5. RELACIONES DE SEGUIMIENTO (FOLLOW) - Para probar el sistema
-- Usuario 1 (mferradans) sigue al Usuario 2 (Museo Nacional)
INSERT INTO follow (follower_id, followee_id) VALUES (1, 2);
-- Usuario 1 (mferradans) sigue al Usuario 3 (Historia Argentina)  
INSERT INTO follow (follower_id, followee_id) VALUES (1, 3);
-- Usuario 2 (Museo Nacional) sigue al Usuario 3 (Historia Argentina)
INSERT INTO follow (follower_id, followee_id) VALUES (2, 3);