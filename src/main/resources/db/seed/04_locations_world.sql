-- ==========================================
-- SEED: 04 - UBICACIONES - RESTO DEL MUNDO
-- IDs: 17–31
-- ==========================================

-- América del Sur (no Argentina)
INSERT INTO location (id, address, latitude, longitude) VALUES
(17, 'Machu Picchu, Cusco, Perú',                      -13.1631,  -72.5450),
(18, 'Cristo Redentor, Río de Janeiro, Brasil',         -22.9519,  -43.2105);

-- América del Norte
INSERT INTO location (id, address, latitude, longitude) VALUES
(19, 'Times Square, Nueva York, Estados Unidos',         40.7589,  -73.9851),
(20, 'Chichen Itzá, Yucatán, México',                    20.6843,  -88.5678),
(21, 'Teotihuacán, Estado de México, México',            19.6925,  -98.8438);

-- Europa
INSERT INTO location (id, address, latitude, longitude) VALUES
(22, 'Coliseo Romano, Roma, Italia',                     41.8902,   12.4922),
(23, 'Torre Eiffel, París, Francia',                     48.8584,    2.2945),
(24, 'Stonehenge, Wiltshire, Inglaterra',                51.1789,   -1.8262),
(25, 'Acrópolis, Atenas, Grecia',                        37.9715,   23.7267),
(26, 'Sagrada Familia, Barcelona, España',               41.4036,    2.1744);

-- Asia
INSERT INTO location (id, address, latitude, longitude) VALUES
(27, 'Gran Muralla China, Beijing, China',               40.4319,  116.5704),
(28, 'Taj Mahal, Agra, India',                           27.1751,   78.0421),
(29, 'Templo Dorado, Kioto, Japón',                      35.0394,  135.7292);

-- África
INSERT INTO location (id, address, latitude, longitude) VALUES
(30, 'Pirámides de Giza, El Cairo, Egipto',              29.9792,   31.1342);

-- Oceanía
INSERT INTO location (id, address, latitude, longitude) VALUES
(31, 'Ópera de Sídney, Australia',                      -33.8568,  151.2153);


-- ── América del Sur (no Argentina) adicional ──────────────────────────────
INSERT INTO location (id, address, latitude, longitude) VALUES (72, 'Gran Cañón del Colca, Arequipa, Perú', -15.6330, -71.8960);
INSERT INTO location (id, address, latitude, longitude) VALUES (73, 'Lago Titicaca, Bolivia/Perú', -15.9254, -69.3354);
INSERT INTO location (id, address, latitude, longitude) VALUES (74, 'Salar de Uyuni, Bolivia', -20.1338, -67.4891);
INSERT INTO location (id, address, latitude, longitude) VALUES (75, 'Torres del Paine, Patagonia, Chile', -50.9423, -73.4068);
INSERT INTO location (id, address, latitude, longitude) VALUES (76, 'Desierto de Atacama, Chile', -23.8634, -69.0451);
INSERT INTO location (id, address, latitude, longitude) VALUES (77, 'Isla de Pascua (Rapa Nui), Chile', -27.1127, -109.3497);
INSERT INTO location (id, address, latitude, longitude) VALUES (78, 'Líneas de Nazca, Perú', -14.7390, -75.1300);
INSERT INTO location (id, address, latitude, longitude) VALUES (79, 'Ciudad de Cusco, Perú', -13.5320, -71.9675);
INSERT INTO location (id, address, latitude, longitude) VALUES (80, 'Galápagos, Ecuador', -0.9537, -90.9656);
INSERT INTO location (id, address, latitude, longitude) VALUES (81, 'Centro Histórico de Quito, Ecuador', -0.2200, -78.5123);
INSERT INTO location (id, address, latitude, longitude) VALUES (82, 'Pantanal, Brasil', -17.0000, -57.0000);
INSERT INTO location (id, address, latitude, longitude) VALUES (83, 'Salvador de Bahía, Brasil', -12.9714, -38.5014);
INSERT INTO location (id, address, latitude, longitude) VALUES (84, 'Colonia del Sacramento, Uruguay', -34.4714, -57.8430);

-- ── América del Norte adicional ───────────────────────────────────────────
INSERT INTO location (id, address, latitude, longitude) VALUES (85, 'Cartagena de Indias, Colombia', 10.3910, -75.4794);
INSERT INTO location (id, address, latitude, longitude) VALUES (86, 'Gran Cañón, Arizona, EEUU', 36.0544, -112.1401);
INSERT INTO location (id, address, latitude, longitude) VALUES (87, 'Cataratas del Niágara, Canadá/EEUU', 43.0962, -79.0377);
INSERT INTO location (id, address, latitude, longitude) VALUES (88, 'Yellowstone, Wyoming, EEUU', 44.4280, -110.5885);
INSERT INTO location (id, address, latitude, longitude) VALUES (89, 'Centro Histórico de Ciudad de México (Tenochtitlan)', 19.4326, -99.1332);
INSERT INTO location (id, address, latitude, longitude) VALUES (90, 'Barranca del Cobre, Chihuahua, México', 27.5481, -107.6959);
INSERT INTO location (id, address, latitude, longitude) VALUES (91, 'Tulum, Quintana Roo, México', 20.2108, -87.4655);

-- ── Europa adicional ───────────────────────────────────────────────────────
INSERT INTO location (id, address, latitude, longitude) VALUES (92, 'Palenque, Chiapas, México', 17.4838, -91.9828);
INSERT INTO location (id, address, latitude, longitude) VALUES (93, 'Ciudad de Quebec, Canadá', 46.8139, -71.2080);
INSERT INTO location (id, address, latitude, longitude) VALUES (94, 'Parque Nacional Banff, Canadá', 51.4968, -115.9281);
INSERT INTO location (id, address, latitude, longitude) VALUES (95, 'Nueva Orleans, Luisiana, EEUU', 29.9511, -90.0715);
INSERT INTO location (id, address, latitude, longitude) VALUES (96, 'Alhambra, Granada, España', 37.1761, -3.5881);
INSERT INTO location (id, address, latitude, longitude) VALUES (97, 'Canales de Venecia, Italia', 45.4408, 12.3155);
INSERT INTO location (id, address, latitude, longitude) VALUES (98, 'Palacio de Versalles, Francia', 48.8049, 2.1204);
INSERT INTO location (id, address, latitude, longitude) VALUES (99, 'Catedral de Colonia, Alemania', 50.9413, 6.9583);
INSERT INTO location (id, address, latitude, longitude) VALUES (100, 'Castillo de Praga, República Checa', 50.0906, 14.4020);
INSERT INTO location (id, address, latitude, longitude) VALUES (101, 'Big Ben y Parlamento, Londres, Inglaterra', 51.5007, -0.1246);
INSERT INTO location (id, address, latitude, longitude) VALUES (102, 'Dubrovnik, Croacia', 42.6507, 18.0944);
INSERT INTO location (id, address, latitude, longitude) VALUES (103, 'Meteora, Grecia', 39.7217, 21.6306);
INSERT INTO location (id, address, latitude, longitude) VALUES (104, 'Pompeya, Italia', 40.7461, 14.4966);
INSERT INTO location (id, address, latitude, longitude) VALUES (105, 'Hallstatt, Austria', 47.5622, 13.6493);
INSERT INTO location (id, address, latitude, longitude) VALUES (106, 'Cataratas del Rhin, Suiza', 47.6773, 8.6151);
INSERT INTO location (id, address, latitude, longitude) VALUES (107, 'Ópera de Viena, Austria', 48.2030, 16.3695);
INSERT INTO location (id, address, latitude, longitude) VALUES (108, 'Loch Ness, Escocia', 57.3229, -4.4244);
INSERT INTO location (id, address, latitude, longitude) VALUES (109, 'Parque Nacional de Plitvice, Croacia', 44.8655, 15.5820);
INSERT INTO location (id, address, latitude, longitude) VALUES (110, 'Monte Vesubio, Nápoles, Italia', 40.8217, 14.4260);
INSERT INTO location (id, address, latitude, longitude) VALUES (111, 'Acrópolis de Lindos, Rodas, Grecia', 36.0913, 28.0900);
INSERT INTO location (id, address, latitude, longitude) VALUES (112, 'Stari Most, Mostar, Bosnia', 43.3376, 17.8152);
INSERT INTO location (id, address, latitude, longitude) VALUES (113, 'Auschwitz-Birkenau, Polonia', 50.0275, 19.2025);
INSERT INTO location (id, address, latitude, longitude) VALUES (114, 'Ciudad Vieja de Tallin, Estonia', 59.4370, 24.7536);
INSERT INTO location (id, address, latitude, longitude) VALUES (115, 'Sintra, Portugal', 38.7978, -9.3880);
INSERT INTO location (id, address, latitude, longitude) VALUES (116, 'Fiordo de Geiranger, Noruega', 62.1003, 7.2059);
INSERT INTO location (id, address, latitude, longitude) VALUES (117, 'Matterhorn, Suiza', 45.9763, 7.6586);
INSERT INTO location (id, address, latitude, longitude) VALUES (118, 'Torre de Pisa, Italia', 43.7230, 10.3966);
INSERT INTO location (id, address, latitude, longitude) VALUES (119, 'Ávila Amurallada, España', 40.6564, -4.6882);
INSERT INTO location (id, address, latitude, longitude) VALUES (120, 'Castillo de Neuschwanstein, Alemania', 47.5576, 10.7498);
INSERT INTO location (id, address, latitude, longitude) VALUES (121, 'Van Gogh Museum, Amsterdam, Países Bajos', 52.3584, 4.8811);
INSERT INTO location (id, address, latitude, longitude) VALUES (122, 'Santuario de Fátima, Portugal', 39.6250, -8.6708);
INSERT INTO location (id, address, latitude, longitude) VALUES (123, 'Capilla Sixtina, Ciudad del Vaticano', 41.9029, 12.4534);
INSERT INTO location (id, address, latitude, longitude) VALUES (124, 'Castillo de Edimburgo, Escocia', 55.9486, -3.1999);
INSERT INTO location (id, address, latitude, longitude) VALUES (125, 'Isla de Skye, Escocia', 57.2736, -6.2169);
INSERT INTO location (id, address, latitude, longitude) VALUES (126, 'Angkor Wat, Camboya', 13.4125, 103.8670);
INSERT INTO location (id, address, latitude, longitude) VALUES (127, 'Bahía de Halong, Vietnam', 20.9101, 107.1839);
INSERT INTO location (id, address, latitude, longitude) VALUES (128, 'Bagan, Myanmar', 21.1717, 94.8585);
INSERT INTO location (id, address, latitude, longitude) VALUES (129, 'Borobudur, Java, Indonesia', -7.6079, 110.2038);
INSERT INTO location (id, address, latitude, longitude) VALUES (130, 'Monte Fuji, Japón', 35.3606, 138.7274);
INSERT INTO location (id, address, latitude, longitude) VALUES (131, 'Ejército de Terracota, Xi''an, China', 34.3847, 109.2733);
INSERT INTO location (id, address, latitude, longitude) VALUES (132, 'Río Li, Guilin, China', 25.2744, 110.2998);
INSERT INTO location (id, address, latitude, longitude) VALUES (133, 'Capadocia, Turquía', 38.6430, 34.8289);
INSERT INTO location (id, address, latitude, longitude) VALUES (134, 'Hagia Sophia, Estambul, Turquía', 41.0086, 28.9802);
INSERT INTO location (id, address, latitude, longitude) VALUES (135, 'Pamukkale, Turquía', 37.9145, 29.1267);
INSERT INTO location (id, address, latitude, longitude) VALUES (136, 'Petra, Jordania', 30.3285, 35.4444);
INSERT INTO location (id, address, latitude, longitude) VALUES (137, 'Varanasi, India', 25.3176, 82.9739);
INSERT INTO location (id, address, latitude, longitude) VALUES (138, 'Hampi, Karnataka, India', 15.3350, 76.4600);
INSERT INTO location (id, address, latitude, longitude) VALUES (139, 'Templo Tanah Lot, Bali, Indonesia', -8.6215, 115.0865);
INSERT INTO location (id, address, latitude, longitude) VALUES (140, 'Sigiriya, Sri Lanka', 7.9572, 80.7600);
INSERT INTO location (id, address, latitude, longitude) VALUES (141, 'Templos de Ayutthaya, Tailandia', 14.3532, 100.5630);
INSERT INTO location (id, address, latitude, longitude) VALUES (142, 'Hoi An, Vietnam', 15.8801, 108.3380);
INSERT INTO location (id, address, latitude, longitude) VALUES (143, 'Luang Prabang, Laos', 19.8947, 102.1354);
INSERT INTO location (id, address, latitude, longitude) VALUES (144, 'Palacio Potala, Lhasa, Tíbet', 29.6575, 91.1172);
INSERT INTO location (id, address, latitude, longitude) VALUES (145, 'Zhangjiajie, Hunan, China', 29.1170, 110.4790);
INSERT INTO location (id, address, latitude, longitude) VALUES (146, 'Palacio Gyeongbokgung, Seúl, Corea del Sur', 37.5796, 126.9770);
INSERT INTO location (id, address, latitude, longitude) VALUES (147, 'Monte Bromo, Java, Indonesia', -7.9425, 112.9530);
INSERT INTO location (id, address, latitude, longitude) VALUES (148, 'Barrio Al Fahidi, Dubái, Emiratos Árabes', 25.2631, 55.2972);
INSERT INTO location (id, address, latitude, longitude) VALUES (149, 'Palmira, Siria', 34.5498, 38.2688);
INSERT INTO location (id, address, latitude, longitude) VALUES (150, 'Masada, Israel', 31.3156, 35.3538);
INSERT INTO location (id, address, latitude, longitude) VALUES (151, 'Baalbek, Líbano', 34.0046, 36.2111);
INSERT INTO location (id, address, latitude, longitude) VALUES (152, 'Persépolis, Irán', 29.9350, 52.8900);
INSERT INTO location (id, address, latitude, longitude) VALUES (153, 'Ciudad Vieja de Jerusalén, Israel', 31.7781, 35.2359);
INSERT INTO location (id, address, latitude, longitude) VALUES (154, 'Prambanan, Java, Indonesia', -7.7520, 110.4915);
INSERT INTO location (id, address, latitude, longitude) VALUES (155, 'Templo Todai-ji, Nara, Japón', 34.6851, 135.8048);
INSERT INTO location (id, address, latitude, longitude) VALUES (156, 'Kilimanjaro, Tanzania', -3.0674, 37.3556);
INSERT INTO location (id, address, latitude, longitude) VALUES (157, 'Serengeti, Tanzania', -2.3333, 34.8333);
INSERT INTO location (id, address, latitude, longitude) VALUES (158, 'Cataratas Victoria, Zambia/Zimbabue', -17.9243, 25.8572);
INSERT INTO location (id, address, latitude, longitude) VALUES (159, 'Medina de Fez, Marruecos', 34.0531, -4.9998);
INSERT INTO location (id, address, latitude, longitude) VALUES (160, 'Erg Chebbi, Sahara, Marruecos', 31.1500, -3.9667);
INSERT INTO location (id, address, latitude, longitude) VALUES (161, 'Cabo de Buena Esperanza, Sudáfrica', -34.3569, 18.4734);
INSERT INTO location (id, address, latitude, longitude) VALUES (162, 'Lalibela, Etiopía', 12.0321, 39.0474);
INSERT INTO location (id, address, latitude, longitude) VALUES (163, 'Plaza Jemaa el-Fna, Marrakech, Marruecos', 31.6295, -7.9811);
INSERT INTO location (id, address, latitude, longitude) VALUES (164, 'Ruinas de Cartago, Túnez', 36.8528, 10.3233);
INSERT INTO location (id, address, latitude, longitude) VALUES (165, 'Valle de los Reyes, Luxor, Egipto', 25.7402, 32.6014);
INSERT INTO location (id, address, latitude, longitude) VALUES (166, 'Gran Barrera de Coral, Queensland, Australia', -18.2871, 147.6992);
INSERT INTO location (id, address, latitude, longitude) VALUES (167, 'Uluru, Territorio del Norte, Australia', -25.3444, 131.0369);
INSERT INTO location (id, address, latitude, longitude) VALUES (168, 'Milford Sound, Nueva Zelanda', -44.6723, 167.9201);
INSERT INTO location (id, address, latitude, longitude) VALUES (169, 'Hobbiton, Waikato, Nueva Zelanda', -37.8724, 175.6821);
INSERT INTO location (id, address, latitude, longitude) VALUES (170, 'Bora Bora, Polinesia Francesa', -16.5004, -151.7415);
INSERT INTO location (id, address, latitude, longitude) VALUES (171, 'Tongariro, Nueva Zelanda', -39.1987, 175.6378);

SELECT setval(pg_get_serial_sequence('location', 'id'), (SELECT MAX(id) FROM location));

-- ── Sitios de mitos del mundo ─────────────────────────────────────────────
INSERT INTO location (id, address, latitude, longitude) VALUES
(176, 'Océano Atlántico, frente al Estrecho de Gibraltar', 35.9996,  -5.6000),
(177, 'Gran Sabana, Venezuela',                            5.5000,  -61.5000);

SELECT setval(pg_get_serial_sequence('location', 'id'), (SELECT MAX(id) FROM location));
