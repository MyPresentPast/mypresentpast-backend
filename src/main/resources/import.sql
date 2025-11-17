-- ===================================
-- MyPresentPast - Datos de Precarga
-- Eventos Históricos del Mundo + Sistema de Verificaciones
-- ===================================

-- 1. USUARIOS (SIN DUPLICADOS)
-- 🔐 CONTRASEÑA PARA TODOS: TestPassword123
INSERT INTO user_account (profile_username, email, password, role, avatar, name, last_name, email_verified) VALUES ('mferradans', 'mateo@test.com', '$2a$10$wPpCaSsVWMND5AEtYqLKfuKdgZb2JyHGSQA/poP/9jbYosU59ShBW', 'ADMIN', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&h=400&fit=crop&crop=face', 'Mateo', 'Ferradans', true);
INSERT INTO user_account (profile_username, email, password, role, avatar, name, last_name, email_verified) VALUES ('Museo Nacional Argentino','museo@test.com', '$2a$10$wPpCaSsVWMND5AEtYqLKfuKdgZb2JyHGSQA/poP/9jbYosU59ShBW', 'INSTITUTION', 'https://images.unsplash.com/photo-1651419366419-d587fdf057fb?w=400&h=400&fit=crop', 'Museo Nacional Argentino', null, true);
INSERT INTO user_account (profile_username, email, password, role, avatar, name, last_name, email_verified) VALUES ('Historia Argentina', 'historia@test.com','$2a$10$wPpCaSsVWMND5AEtYqLKfuKdgZb2JyHGSQA/poP/9jbYosU59ShBW', 'NORMAL', 'https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=400&h=400&fit=crop&crop=face', 'Historia', 'Argentina', true);
INSERT INTO user_account (profile_username, email, password, role, avatar, name, last_name, email_verified) VALUES ('Universidad Nacional', 'universidad@test.com', '$2a$10$wPpCaSsVWMND5AEtYqLKfuKdgZb2JyHGSQA/poP/9jbYosU59ShBW', 'INSTITUTION', 'https://images.unsplash.com/photo-1607237138185-eedd9c632b0b?w=400&h=400&fit=crop', 'Universidad Nacional de Córdoba', null, true);
INSERT INTO user_account (profile_username, email, password, role, avatar, name, last_name, email_verified) VALUES ('usuario_normal', 'normal@test.com', '$2a$10$wPpCaSsVWMND5AEtYqLKfuKdgZb2JyHGSQA/poP/9jbYosU59ShBW', 'NORMAL', 'https://images.unsplash.com/photo-1539571696285-e7d0710b8999?w=400&h=400&fit=crop&crop=face', 'Juan', 'Pérez', true);

-- 2. UBICACIONES DISTRIBUIDAS POR TODO EL MUNDO
-- América del Sur
INSERT INTO location (address, latitude, longitude) VALUES ('Machu Picchu, Cusco, Perú', -13.1631, -72.5450);
INSERT INTO location (address, latitude, longitude) VALUES ('Cristo Redentor, Río de Janeiro, Brasil', -22.9519, -43.2105);
-- América del Norte
INSERT INTO location (address, latitude, longitude) VALUES ('Times Square, Nueva York, Estados Unidos', 40.7589, -73.9851);
INSERT INTO location (address, latitude, longitude) VALUES ('Chichen Itzá, Yucatán, México', 20.6843, -88.5678);
INSERT INTO location (address, latitude, longitude) VALUES ('Teotihuacán, Estado de México, México', 19.6925, -98.8438);
-- Buenos Aires, Argentina (para efecto cluster)
INSERT INTO location (address, latitude, longitude) VALUES ('Plaza de Mayo, Buenos Aires, Argentina', -34.6082, -58.3716);
INSERT INTO location (address, latitude, longitude) VALUES ('Obelisco, Buenos Aires, Argentina', -34.6037, -58.3816);
INSERT INTO location (address, latitude, longitude) VALUES ('Puerto Madero, Buenos Aires, Argentina', -34.6118, -58.3623);
INSERT INTO location (address, latitude, longitude) VALUES ('La Boca, Buenos Aires, Argentina', -34.6345, -58.3635);
INSERT INTO location (address, latitude, longitude) VALUES ('Recoleta, Buenos Aires, Argentina', -34.5875, -58.3974);
INSERT INTO location (address, latitude, longitude) VALUES ('Palermo, Buenos Aires, Argentina', -34.5755, -58.4338);
INSERT INTO location (address, latitude, longitude) VALUES ('San Telmo, Buenos Aires, Argentina', -34.6214, -58.3731);
-- Argentina - Otras provincias
INSERT INTO location (address, latitude, longitude) VALUES ('Cataratas del Iguazú, Misiones, Argentina', -25.6953, -54.4367);
INSERT INTO location (address, latitude, longitude) VALUES ('Glaciar Perito Moreno, Santa Cruz, Argentina', -50.4684, -73.0306);
INSERT INTO location (address, latitude, longitude) VALUES ('Cerro Aconcagua, Mendoza, Argentina', -32.6532, -70.0109);
INSERT INTO location (address, latitude, longitude) VALUES ('Quebrada de Humahuaca, Jujuy, Argentina', -23.2044, -65.3486);
INSERT INTO location (address, latitude, longitude) VALUES ('Península Valdés, Chubut, Argentina', -42.5051, -64.0178);
INSERT INTO location (address, latitude, longitude) VALUES ('Cueva de las Manos, Santa Cruz, Argentina', -47.1500, -70.6667);
INSERT INTO location (address, latitude, longitude) VALUES ('Salta Capital, Salta, Argentina', -24.7821, -65.4232);
INSERT INTO location (address, latitude, longitude) VALUES ('Bariloche, Río Negro, Argentina', -41.1335, -71.3103);
INSERT INTO location (address, latitude, longitude) VALUES ('Ushuaia, Tierra del Fuego, Argentina', -54.8019, -68.3030);
-- Europa
INSERT INTO location (address, latitude, longitude) VALUES ('Coliseo Romano, Roma, Italia', 41.8902, 12.4922);
INSERT INTO location (address, latitude, longitude) VALUES ('Torre Eiffel, París, Francia', 48.8584, 2.2945);
INSERT INTO location (address, latitude, longitude) VALUES ('Stonehenge, Wiltshire, Inglaterra', 51.1789, -1.8262);
INSERT INTO location (address, latitude, longitude) VALUES ('Acrópolis, Atenas, Grecia', 37.9715, 23.7267);
INSERT INTO location (address, latitude, longitude) VALUES ('Sagrada Familia, Barcelona, España', 41.4036, 2.1744);
-- Asia
INSERT INTO location (address, latitude, longitude) VALUES ('Gran Muralla China, Beijing, China', 40.4319, 116.5704);
INSERT INTO location (address, latitude, longitude) VALUES ('Taj Mahal, Agra, India', 27.1751, 78.0421);
INSERT INTO location (address, latitude, longitude) VALUES ('Templo Dorado, Kioto, Japón', 35.0394, 135.7292);
-- África
INSERT INTO location (address, latitude, longitude) VALUES ('Pirámides de Giza, El Cairo, Egipto', 29.9792, 31.1342);
-- Oceanía
INSERT INTO location (address, latitude, longitude) VALUES ('Ópera de Sídney, Australia', -33.8568, 151.2153);

-- 3. POSTS HISTÓRICOS (SIN COLUMNA is_verified)
-- América del Sur
INSERT INTO post (title, content, date, posted_at, is_by_ia, category, status, author_id, location_id) VALUES ('El Descubrimiento de Machu Picchu', 'El 24 de julio de 1911, el explorador estadounidense Hiram Bingham III redescubrió Machu Picchu para el mundo occidental. Esta ciudadela inca, construida en el siglo XV, había permanecido oculta en las montañas peruanas durante más de 400 años.', '1911-07-24', '2024-01-15', false, 'STORY', 'ACTIVE', 2, 1);
INSERT INTO post (title, content, date, posted_at, is_by_ia, category, status, author_id, location_id) VALUES ('La Construcción del Cristo Redentor', 'Entre 1922 y 1931 se construyó el Cristo Redentor en Río de Janeiro, Brasil. Esta estatua Art Déco de 30 metros de altura se erigió en la cima del Corcovado como símbolo de paz y cristiandad.', '1931-10-12', '2024-01-20', false, 'INFORMATION', 'ACTIVE', 1, 2);

-- América del Norte
INSERT INTO post (title, content, date, posted_at, is_by_ia, category, status, author_id, location_id) VALUES ('El Primer Ball Drop de Times Square', 'El 31 de diciembre de 1907 se realizó por primera vez el famoso "Ball Drop" en Times Square, Nueva York. Esta tradición comenzó cuando se prohibieron los fuegos artificiales en Manhattan.', '1907-12-31', '2024-02-01', false, 'STORY', 'ACTIVE', 1, 3);
INSERT INTO post (title, content, date, posted_at, is_by_ia, category, status, author_id, location_id) VALUES ('El Equinoccio en Chichen Itzá', 'Cada equinoccio de primavera y otoño, la pirámide de Kukulcán en Chichen Itzá crea un espectáculo de luces y sombras único. Las sombras forman la imagen de una serpiente que desciende por la escalinata.', '1200-03-21', '2024-02-05', false, 'INFORMATION', 'ACTIVE', 4, 4);
INSERT INTO post (title, content, date, posted_at, is_by_ia, category, status, author_id, location_id) VALUES ('La Ciudad de los Dioses: Teotihuacán', 'Entre los años 100 y 650 d.C., Teotihuacán fue una de las ciudades más grandes del mundo antiguo, con una población estimada de 200,000 habitantes. Sus monumentales pirámides siguen siendo un misterio.', '0450-01-01', '2024-02-10', false, 'INFORMATION', 'ACTIVE', 4, 5);

-- Buenos Aires, Argentina
INSERT INTO post (title, content, date, posted_at, is_by_ia, category, status, author_id, location_id) VALUES ('Fundación de Buenos Aires', 'El 11 de junio de 1580, Juan de Garay refundó Buenos Aires con el nombre de "Ciudad de la Santísima Trinidad y Puerto de Santa María del Buen Ayre". Esta segunda fundación fue exitosa.', '1580-06-11', '2024-01-25', false, 'STORY', 'ACTIVE', 3, 6);
INSERT INTO post (title, content, date, posted_at, is_by_ia, category, status, author_id, location_id) VALUES ('Construcción del Obelisco', 'El 23 de mayo de 1936 se inauguró el Obelisco de Buenos Aires para conmemorar el cuarto centenario de la primera fundación de la ciudad. Con 67 metros de altura, se convirtió en el símbolo de la ciudad.', '1936-05-23', '2024-01-26', false, 'INFORMATION', 'ACTIVE', 1, 7);
INSERT INTO post (title, content, date, posted_at, is_by_ia, category, status, author_id, location_id) VALUES ('Puerto Madero: De Puerto a Barrio', 'En 1989 comenzó la renovación de Puerto Madero, transformando el antiguo puerto en el barrio más moderno de Buenos Aires. Este proyecto urbano se convirtió en modelo para otras ciudades del mundo.', '1989-01-01', '2024-01-27', false, 'STORY', 'ACTIVE', 2, 8);
INSERT INTO post (title, content, date, posted_at, is_by_ia, category, status, author_id, location_id) VALUES ('El Tango en La Boca', 'A principios del siglo XX, el barrio de La Boca se convirtió en la cuna del tango. En sus conventillos y cafés nacieron los primeros tangos que conquistarían el mundo.', '1900-01-01', '2024-01-28', false, 'STORY', 'ACTIVE', 4, 9);
INSERT INTO post (title, content, date, posted_at, is_by_ia, category, status, author_id, location_id) VALUES ('El Cementerio de la Recoleta', 'Inaugurado en 1822, el Cementerio de la Recoleta se convirtió en el más prestigioso de Buenos Aires. Aquí descansan presidentes, escritores y personalidades como Eva Perón.', '1822-11-17', '2024-01-29', false, 'INFORMATION', 'ACTIVE', 3, 10);
INSERT INTO post (title, content, date, posted_at, is_by_ia, category, status, author_id, location_id) VALUES ('Los Bosques de Palermo', 'En 1875 se inauguraron los Bosques de Palermo, el pulmón verde de Buenos Aires. Estos jardines fueron diseñados por el paisajista francés Charles Thays y se convirtieron en el paseo favorito de los porteños.', '1875-11-11', '2024-01-30', false, 'INFORMATION', 'ACTIVE', 1, 11);
INSERT INTO post (title, content, date, posted_at, is_by_ia, category, status, author_id, location_id) VALUES ('El Mercado de San Telmo', 'Desde 1897, el Mercado de San Telmo ha sido el corazón comercial del barrio más antiguo de Buenos Aires. Sus puestos de antigüedades y su arquitectura de hierro lo convierten en un ícono porteño.', '1897-01-01', '2024-01-31', false, 'STORY', 'ACTIVE', 2, 12);

-- Argentina - Otras provincias
INSERT INTO post (title, content, date, posted_at, is_by_ia, category, status, author_id, location_id) VALUES ('Las Cataratas del Iguazú', 'Descubiertas por Álvar Núñez Cabeza de Vaca en 1541, las Cataratas del Iguazú son una de las maravillas naturales más impresionantes del mundo. Con 275 saltos de agua, forman un espectáculo único en la frontera argentino-brasileña.', '1541-01-01', '2024-03-01', false, 'INFORMATION', 'ACTIVE', 2, 13);
INSERT INTO post (title, content, date, posted_at, is_by_ia, category, status, author_id, location_id) VALUES ('El Glaciar Perito Moreno', 'Nombrado en honor al explorador Francisco Moreno, este glaciar de 30 km de longitud es uno de los pocos que aún avanza. Su frente de 5 km de ancho y 60 metros de altura ofrece un espectáculo único cuando se desprenden bloques de hielo.', '1899-01-01', '2024-03-05', false, 'STORY', 'ACTIVE', 3, 14);
INSERT INTO post (title, content, date, posted_at, is_by_ia, category, status, author_id, location_id) VALUES ('La Conquista del Aconcagua', 'El 14 de enero de 1897, el suizo Matthias Zurbriggen alcanzó por primera vez la cumbre del Aconcagua, la montaña más alta de América con 6,962 metros. Esta hazaña marcó el inicio del montañismo en Argentina.', '1897-01-14', '2024-03-10', false, 'STORY', 'ACTIVE', 1, 15);
INSERT INTO post (title, content, date, posted_at, is_by_ia, category, status, author_id, location_id) VALUES ('La Quebrada de Humahuaca', 'Declarada Patrimonio de la Humanidad en 2003, la Quebrada de Humahuaca ha sido durante 10,000 años una ruta de intercambio cultural. Sus pueblos conservan tradiciones ancestrales y una arquitectura única.', '1500-01-01', '2024-03-15', false, 'INFORMATION', 'ACTIVE', 4, 16);
INSERT INTO post (title, content, date, posted_at, is_by_ia, category, status, author_id, location_id) VALUES ('Península Valdés y las Ballenas', 'Desde 1970, Península Valdés se convirtió en el santuario de ballenas más importante del mundo. Cada año, entre junio y diciembre, las ballenas francas australes llegan para reproducirse en estas aguas protegidas.', '1970-06-01', '2024-03-20', false, 'INFORMATION', 'ACTIVE', 2, 17);
INSERT INTO post (title, content, date, posted_at, is_by_ia, category, status, author_id, location_id) VALUES ('La Cueva de las Manos', 'Con más de 9,000 años de antigüedad, la Cueva de las Manos contiene las expresiones artísticas más antiguas de Sudamérica. Sus pinturas rupestres muestran manos en negativo y escenas de caza de guanacos.', '7000-01-01', '2024-03-25', false, 'STORY', 'ACTIVE', 4, 18);
INSERT INTO post (title, content, date, posted_at, is_by_ia, category, status, author_id, location_id) VALUES ('Fundación de Salta', 'El 16 de abril de 1582, Hernando de Lerma fundó la ciudad de Salta en el Valle de Lerma. Su arquitectura colonial y su ubicación estratégica la convirtieron en un importante centro comercial del noroeste argentino.', '1582-04-16', '2024-03-30', false, 'STORY', 'ACTIVE', 3, 19);
INSERT INTO post (title, content, date, posted_at, is_by_ia, category, status, author_id, location_id) VALUES ('Bariloche y los Pioneros Alemanes', 'A partir de 1895, inmigrantes alemanes se establecieron en la región de Bariloche, introduciendo la arquitectura alpina y la tradición chocolatera. Carlos Wiederhold fundó el primer hotel y estableció las bases del turismo patagónico.', '1895-01-01', '2024-04-01', false, 'STORY', 'ACTIVE', 1, 20);
INSERT INTO post (title, content, date, posted_at, is_by_ia, category, status, author_id, location_id) VALUES ('Ushuaia: El Fin del Mundo', 'Fundada en 1884 por Augusto Lasserre, Ushuaia se convirtió en la ciudad más austral del mundo. Su presidio, construido en 1902, albergó a los presos más peligrosos de Argentina hasta 1947.', '1884-10-12', '2024-04-05', false, 'INFORMATION', 'ACTIVE', 2, 21);

-- Europa
INSERT INTO post (title, content, date, posted_at, is_by_ia, category, status, author_id, location_id) VALUES ('Inauguración del Coliseo Romano', 'En el año 80 d.C., el emperador Tito inauguró el Anfiteatro Flavio, conocido como el Coliseo. Durante 100 días consecutivos se celebraron juegos que incluyeron combates de gladiadores y cacerías.', '0080-01-01', '2024-02-15', false, 'STORY', 'ACTIVE', 2, 22);
INSERT INTO post (title, content, date, posted_at, is_by_ia, category, status, author_id, location_id) VALUES ('Inauguración de la Torre Eiffel', 'El 31 de marzo de 1889 se inauguró la Torre Eiffel en París, construida por Gustave Eiffel para la Exposición Universal. Con 300 metros de altura, fue la estructura más alta del mundo hasta 1930.', '1889-03-31', '2024-02-20', false, 'STORY', 'ACTIVE', 1, 23);
INSERT INTO post (title, content, date, posted_at, is_by_ia, category, status, author_id, location_id) VALUES ('El Misterio de Stonehenge', 'Construido entre 3100 y 1600 a.C., Stonehenge sigue siendo uno de los monumentos prehistóricos más enigmáticos del mundo. Este círculo de piedras megalíticas fue utilizado durante más de 1500 años.', '2500-06-21', '2024-02-25', false, 'INFORMATION', 'ACTIVE', 4, 24);
INSERT INTO post (title, content, date, posted_at, is_by_ia, category, status, author_id, location_id) VALUES ('La Edad de Oro de Atenas', 'Durante el siglo V a.C., bajo el liderazgo de Pericles, Atenas vivió su Edad de Oro. Se construyó el Partenón en la Acrópolis y florecieron la filosofía y la democracia.', '0440-01-01', '2024-03-01', false, 'STORY', 'ACTIVE', 2, 25);
INSERT INTO post (title, content, date, posted_at, is_by_ia, category, status, author_id, location_id) VALUES ('Gaudí y la Sagrada Familia', 'En 1882 comenzó la construcción de la Sagrada Familia en Barcelona bajo la dirección de Antoni Gaudí. Este arquitecto dedicó 43 años de su vida al proyecto, desarrollando un estilo único.', '1882-03-19', '2024-03-05', false, 'INFORMATION', 'ACTIVE', 3, 26);

-- Asia
INSERT INTO post (title, content, date, posted_at, is_by_ia, category, status, author_id, location_id) VALUES ('La Gran Muralla: Maravilla Milenaria', 'La construcción de la Gran Muralla China comenzó en el siglo VII a.C. y continuó durante más de 2000 años. Con más de 21,000 kilómetros de longitud, es la estructura militar más larga jamás construida.', '0220-01-01', '2024-03-15', false, 'INFORMATION', 'ACTIVE', 2, 27);
INSERT INTO post (title, content, date, posted_at, is_by_ia, category, status, author_id, location_id) VALUES ('El Taj Mahal: Monumento al Amor Eterno', 'Entre 1632 y 1653, el emperador mogol Shah Jahan mandó construir el Taj Mahal como mausoleo para su esposa Mumtaz Mahal. Esta obra maestra empleó a más de 20,000 artesanos.', '1653-01-01', '2024-03-20', false, 'STORY', 'ACTIVE', 3, 28);
INSERT INTO post (title, content, date, posted_at, is_by_ia, category, status, author_id, location_id) VALUES ('El Pabellón Dorado de Kioto', 'El Kinkaku-ji o Pabellón Dorado fue construido en 1397 como villa de retiro para el shogun Ashikaga Yoshimitsu. Este edificio de tres pisos, cubierto en pan de oro, es uno de los templos más fotografiados de Japón.', '1397-01-01', '2024-03-25', false, 'INFORMATION', 'ACTIVE', 4, 29);

-- África
INSERT INTO post (title, content, date, posted_at, is_by_ia, category, status, author_id, location_id) VALUES ('Las Pirámides de Giza: Última Maravilla Antigua', 'Construidas durante la IV dinastía del Antiguo Egipto (2580-2510 a.C.), las pirámides de Giza son la única maravilla del mundo antiguo que permanece intacta.', '2560-01-01', '2024-04-15', false, 'INFORMATION', 'ACTIVE', 2, 30);

-- Oceanía
INSERT INTO post (title, content, date, posted_at, is_by_ia, category, status, author_id, location_id) VALUES ('La Ópera de Sídney: Icono Arquitectónico', 'Inaugurada el 20 de octubre de 1973 por la reina Isabel II, la Ópera de Sídney se convirtió instantáneamente en el símbolo de Australia. Su construcción tomó 14 años y costó 15 veces más de lo presupuestado.', '1973-10-20', '2024-05-01', false, 'STORY', 'ACTIVE', 2, 31);

-- 4. MEDIA PARA LOS POSTS (IDs 1-31)
INSERT INTO media (type, url, post_id) VALUES ('IMAGE', 'https://images.unsplash.com/photo-1587595431973-160d0d94add1?w=800', 1);
INSERT INTO media (type, url, post_id) VALUES ('IMAGE', 'https://images.unsplash.com/photo-1544551763-46a013bb70d5?w=800', 2);
INSERT INTO media (type, url, post_id) VALUES ('IMAGE', 'https://images.unsplash.com/photo-1485738422979-f5c462d49f74?w=800', 3);
INSERT INTO media (type, url, post_id) VALUES ('IMAGE', 'https://images.unsplash.com/photo-1518638150340-f706e86654de?w=800', 4);
INSERT INTO media (type, url, post_id) VALUES ('IMAGE', 'https://images.unsplash.com/photo-1539650116574-75c0c6d73f6e?w=800', 5);
INSERT INTO media (type, url, post_id) VALUES ('IMAGE', 'https://images.unsplash.com/photo-1589909202802-8f4aadce1849?w=800', 6);
INSERT INTO media (type, url, post_id) VALUES ('IMAGE', 'https://res.cloudinary.com/dttgwvnoe/image/upload/v1763390381/contrucci%C3%B3n_obelisco_i7ntzy.jpg', 7);
INSERT INTO media (type, url, post_id) VALUES ('IMAGE', 'https://res.cloudinary.com/dttgwvnoe/image/upload/v1763390381/Construcci%C3%B3n_obelisco_2_glkvu1.jpg', 7);
INSERT INTO media (type, url, post_id) VALUES ('IMAGE', 'https://res.cloudinary.com/dttgwvnoe/image/upload/v1763391054/puerto_madero_1_apu7yy.jpg', 8);
INSERT INTO media (type, url, post_id) VALUES ('IMAGE', 'https://res.cloudinary.com/dttgwvnoe/image/upload/v1763391077/puerto_madero_fal8pw.jpg', 8);
INSERT INTO media (type, url, post_id) VALUES ('IMAGE', 'https://res.cloudinary.com/dttgwvnoe/image/upload/v1763390771/tango_de_la_boca_zgdsz8.jpg', 9);
INSERT INTO media (type, url, post_id) VALUES ('IMAGE', 'https://res.cloudinary.com/dttgwvnoe/image/upload/v1763390830/tango_de_la_boca_2_m6uqgp.jpg', 9);
INSERT INTO media (type, url, post_id) VALUES ('IMAGE', 'https://res.cloudinary.com/dttgwvnoe/image/upload/v1763391358/cementerio_de_la_recoleta_pkzojo.jpg', 10);
INSERT INTO media (type, url, post_id) VALUES ('IMAGE', 'https://res.cloudinary.com/dttgwvnoe/image/upload/v1763391358/cementerio_de_la_recoleta_2_peohdy.jpg', 10);
INSERT INTO media (type, url, post_id) VALUES ('IMAGE', 'https://res.cloudinary.com/dttgwvnoe/image/upload/v1763390883/bosques_palermo_2_rivkiq.jpg', 11);
INSERT INTO media (type, url, post_id) VALUES ('IMAGE', 'https://res.cloudinary.com/dttgwvnoe/image/upload/v1763390872/bosques_palermo_1_o3ehwk.jpg', 11);
INSERT INTO media (type, url, post_id) VALUES ('IMAGE', 'https://res.cloudinary.com/dttgwvnoe/image/upload/v1763391278/mercado_san_telmo_b5dne3.jpg', 12);
INSERT INTO media (type, url, post_id) VALUES ('IMAGE', 'https://res.cloudinary.com/dttgwvnoe/image/upload/v1763391279/mercado_san_telmo_2_cybpuk.webp', 12);
INSERT INTO media (type, url, post_id) VALUES ('IMAGE', 'https://res.cloudinary.com/dttgwvnoe/image/upload/v1763390207/cataratas_del_iguazu_1_xljkgr.jpg', 13);
INSERT INTO media (type, url, post_id) VALUES ('IMAGE', 'https://res.cloudinary.com/dttgwvnoe/image/upload/v1763390657/glaciar_perito_moreno_2_n6h1jh.avif', 14);
INSERT INTO media (type, url, post_id) VALUES ('IMAGE', 'https://res.cloudinary.com/dttgwvnoe/image/upload/v1763390657/glaciar_perito_moreno_1_mtxjrj.jpg', 14);
INSERT INTO media (type, url, post_id) VALUES ('IMAGE', 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=800', 15);
INSERT INTO media (type, url, post_id) VALUES ('IMAGE', 'https://images.unsplash.com/photo-1544735716-392fe2489ffa?w=800', 16);
INSERT INTO media (type, url, post_id) VALUES ('IMAGE', 'https://images.unsplash.com/photo-1580060839134-75a5edca2e99?w=800', 17);
INSERT INTO media (type, url, post_id) VALUES ('IMAGE', 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=800', 18);
INSERT INTO media (type, url, post_id) VALUES ('IMAGE', 'https://images.unsplash.com/photo-1544966503-7cc5ac882d5f?w=800', 19);
INSERT INTO media (type, url, post_id) VALUES ('IMAGE', 'https://images.unsplash.com/photo-1578662996442-48f60103fc96?w=800', 20);
INSERT INTO media (type, url, post_id) VALUES ('IMAGE', 'https://images.unsplash.com/photo-1551524164-6cf2ac531fb4?w=800', 21);
INSERT INTO media (type, url, post_id) VALUES ('IMAGE', 'https://images.unsplash.com/photo-1552832230-c0197dd311b5?w=800', 22);
INSERT INTO media (type, url, post_id) VALUES ('IMAGE', 'https://images.unsplash.com/photo-1511739001486-6bfe10ce785f?w=800', 23);
INSERT INTO media (type, url, post_id) VALUES ('IMAGE', 'https://images.unsplash.com/photo-1599833975787-5c143f373c30?w=800', 24);
INSERT INTO media (type, url, post_id) VALUES ('IMAGE', 'https://images.unsplash.com/photo-1555993539-1732b0258235?w=800', 25);
INSERT INTO media (type, url, post_id) VALUES ('IMAGE', 'https://images.unsplash.com/photo-1558642452-9d2a7deb7f62?w=800', 26);
INSERT INTO media (type, url, post_id) VALUES ('IMAGE', 'https://images.unsplash.com/photo-1508804185872-d7badad00f7d?w=800', 27);
INSERT INTO media (type, url, post_id) VALUES ('IMAGE', 'https://images.unsplash.com/photo-1564507592333-c60657eea523?w=800', 28);
INSERT INTO media (type, url, post_id) VALUES ('IMAGE', 'https://images.unsplash.com/photo-1478436127897-769e1b3f0f36?w=800', 29);
INSERT INTO media (type, url, post_id) VALUES ('IMAGE', 'https://images.unsplash.com/photo-1539650116574-75c0c6d73f6e?w=800', 30);
INSERT INTO media (type, url, post_id) VALUES ('IMAGE', 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=800', 31);

-- 5. LIKES PARA HACER MÁS REALISTA LA EXPERIENCIA
-- Posts de Buenos Aires con más likes (para efecto cluster)
INSERT INTO post_like (user_id, post_id, created_at) VALUES (1, 7, '2024-01-26 10:00:00');
INSERT INTO post_like (user_id, post_id, created_at) VALUES (2, 7, '2024-01-26 11:00:00');
INSERT INTO post_like (user_id, post_id, created_at) VALUES (3, 7, '2024-01-26 12:00:00');
INSERT INTO post_like (user_id, post_id, created_at) VALUES (4, 7, '2024-01-26 13:00:00');

INSERT INTO post_like (user_id, post_id, created_at) VALUES (1, 9, '2024-01-28 10:00:00');
INSERT INTO post_like (user_id, post_id, created_at) VALUES (2, 9, '2024-01-28 11:00:00');
INSERT INTO post_like (user_id, post_id, created_at) VALUES (3, 9, '2024-01-28 12:00:00');

INSERT INTO post_like (user_id, post_id, created_at) VALUES (1, 8, '2024-01-27 10:00:00');
INSERT INTO post_like (user_id, post_id, created_at) VALUES (2, 8, '2024-01-27 11:00:00');

INSERT INTO post_like (user_id, post_id, created_at) VALUES (1, 6, '2024-01-25 10:00:00');
INSERT INTO post_like (user_id, post_id, created_at) VALUES (4, 6, '2024-01-25 13:00:00');

INSERT INTO post_like (user_id, post_id, created_at) VALUES (2, 11, '2024-01-30 11:00:00');
INSERT INTO post_like (user_id, post_id, created_at) VALUES (3, 11, '2024-01-30 12:00:00');

INSERT INTO post_like (user_id, post_id, created_at) VALUES (1, 10, '2024-01-29 10:00:00');

INSERT INTO post_like (user_id, post_id, created_at) VALUES (4, 12, '2024-01-31 13:00:00');

-- Posts de Argentina con likes variados
INSERT INTO post_like (user_id, post_id, created_at) VALUES (1, 13, '2024-03-01 10:00:00');
INSERT INTO post_like (user_id, post_id, created_at) VALUES (2, 13, '2024-03-01 11:00:00');
INSERT INTO post_like (user_id, post_id, created_at) VALUES (3, 13, '2024-03-01 12:00:00');
INSERT INTO post_like (user_id, post_id, created_at) VALUES (4, 13, '2024-03-01 13:00:00');

INSERT INTO post_like (user_id, post_id, created_at) VALUES (1, 14, '2024-03-05 10:00:00');
INSERT INTO post_like (user_id, post_id, created_at) VALUES (2, 14, '2024-03-05 11:00:00');
INSERT INTO post_like (user_id, post_id, created_at) VALUES (3, 14, '2024-03-05 12:00:00');

-- 6. RELACIONES DE SEGUIMIENTO
INSERT INTO follow (follower_id, followee_id) VALUES (1, 2);
INSERT INTO follow (follower_id, followee_id) VALUES (1, 3);
INSERT INTO follow (follower_id, followee_id) VALUES (1, 4);
INSERT INTO follow (follower_id, followee_id) VALUES (2, 3);
INSERT INTO follow (follower_id, followee_id) VALUES (2, 4);
INSERT INTO follow (follower_id, followee_id) VALUES (3, 2);
INSERT INTO follow (follower_id, followee_id) VALUES (3, 4);
INSERT INTO follow (follower_id, followee_id) VALUES (4, 1);
INSERT INTO follow (follower_id, followee_id) VALUES (4, 2);

-- 7. COLECCIONES DE EJEMPLO
INSERT INTO collection (name, description, author_id, created_at, updated_at) VALUES ('Maravillas del Mundo Antiguo', 'Las construcciones más impresionantes de las civilizaciones antiguas', 2, '2024-01-01 10:00:00', '2024-01-01 10:00:00');
INSERT INTO collection (name, description, author_id, created_at, updated_at) VALUES ('Arquitectura Europea', 'Los monumentos más emblemáticos de Europa', 1, '2024-01-15 10:00:00', '2024-01-15 10:00:00');
INSERT INTO collection (name, description, author_id, created_at, updated_at) VALUES ('Buenos Aires Histórica', 'Los momentos más importantes en la historia de la capital argentina', 3, '2024-02-15 10:00:00', '2024-02-15 10:00:00');

-- 8. POSTS EN COLECCIONES (SOLO CON IDS QUE EXISTEN)
INSERT INTO collection_post (collection_id, post_id, added_at) VALUES (1, 22, '2024-01-02 10:00:00');
INSERT INTO collection_post (collection_id, post_id, added_at) VALUES (1, 25, '2024-01-02 11:00:00');
INSERT INTO collection_post (collection_id, post_id, added_at) VALUES (1, 30, '2024-01-02 12:00:00');
INSERT INTO collection_post (collection_id, post_id, added_at) VALUES (2, 23, '2024-01-16 10:00:00');
INSERT INTO collection_post (collection_id, post_id, added_at) VALUES (2, 26, '2024-01-16 11:00:00');
INSERT INTO collection_post (collection_id, post_id, added_at) VALUES (2, 7, '2024-01-16 12:00:00');
INSERT INTO collection_post (collection_id, post_id, added_at) VALUES (3, 6, '2024-02-16 10:00:00');
INSERT INTO collection_post (collection_id, post_id, added_at) VALUES (3, 7, '2024-02-16 11:00:00');
INSERT INTO collection_post (collection_id, post_id, added_at) VALUES (3, 8, '2024-02-16 12:00:00');
INSERT INTO collection_post (collection_id, post_id, added_at) VALUES (3, 9, '2024-02-16 13:00:00');
INSERT INTO collection_post (collection_id, post_id, added_at) VALUES (3, 10, '2024-02-16 14:00:00');
INSERT INTO collection_post (collection_id, post_id, added_at) VALUES (3, 11, '2024-02-16 15:00:00');
INSERT INTO collection_post (collection_id, post_id, added_at) VALUES (3, 12, '2024-02-16 16:00:00');

-- 9. VERIFICACIONES DE POSTS (SISTEMA DE VERIFICACIÓN)
-- IMPORTANTE: Los posts creados por instituciones (usuarios 2 y 4) están auto-verificados por lógica del backend
-- Solo incluimos verificaciones EXTERNAS de posts creados por usuarios NORMALES (usuarios 1 y 3)

-- La institución 'Museo Nacional Argentino' (ID=2) verifica posts de usuarios normales
INSERT INTO post_verification (post_id, verified_by_id, verified_at, is_active) VALUES (6, 2, '2024-02-01 10:00:00', true);   -- Post de usuario 3 verificado por institución 2
INSERT INTO post_verification (post_id, verified_by_id, verified_at, is_active) VALUES (15, 2, '2024-03-11 10:00:00', true); -- Post de usuario 1 verificado por institución 2

-- La institución 'Universidad Nacional' (ID=4) verifica posts de usuarios normales
INSERT INTO post_verification (post_id, verified_by_id, verified_at, is_active) VALUES (7, 4, '2024-02-16 10:00:00', true);  -- Post de usuario 1 verificado por institución 4