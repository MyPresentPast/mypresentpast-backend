-- ==========================================
-- SEED: 03 - UBICACIONES - ARGENTINA (PROVINCIAS)
-- IDs: 8–16
-- Para agregar nuevas provincias, agregar al final (próximo ID: 17)
-- ==========================================

INSERT INTO location (id, address, latitude, longitude) VALUES
( 8, 'Cataratas del Iguazú, Misiones, Argentina',      -25.6953,  -54.4367),
( 9, 'Glaciar Perito Moreno, Santa Cruz, Argentina',    -50.4684,  -73.0306),
(10, 'Cerro Aconcagua, Mendoza, Argentina',             -32.6532,  -70.0109),
(11, 'Quebrada de Humahuaca, Jujuy, Argentina',         -23.2044,  -65.3486),
(12, 'Península Valdés, Chubut, Argentina',             -42.5051,  -64.0178),
(13, 'Cueva de las Manos, Santa Cruz, Argentina',       -47.1500,  -70.6667),
(14, 'Salta Capital, Salta, Argentina',                 -24.7821,  -65.4232),
(15, 'Bariloche, Río Negro, Argentina',                 -41.1335,  -71.3103),
(16, 'Ushuaia, Tierra del Fuego, Argentina',            -54.8019,  -68.3030);

-- ── Provincia de Buenos Aires (no capital) ─────────────────────────────────
INSERT INTO location (id, address, latitude, longitude) VALUES
(32, 'Mar del Plata, Buenos Aires, Argentina',                   -38.0023, -57.5575),
(33, 'Tandil, Buenos Aires, Argentina',                          -37.3317, -59.1332),
(34, 'Luján, Buenos Aires, Argentina',                           -34.5658, -59.1028),
(35, 'La Plata, Buenos Aires, Argentina',                        -34.9214, -57.9545),
(68, 'Sierra de la Ventana, Buenos Aires, Argentina',            -38.1333, -62.0000),
(69, 'Miramar, Buenos Aires, Argentina',                         -38.2722, -57.8375);

-- ── Córdoba ────────────────────────────────────────────────────────────────
INSERT INTO location (id, address, latitude, longitude) VALUES
(36, 'Córdoba Capital, Córdoba, Argentina',                      -31.4201, -64.1888),
(37, 'Villa Carlos Paz, Córdoba, Argentina',                     -31.4226, -64.4965),
(38, 'La Cumbrecita, Córdoba, Argentina',                        -31.9903, -64.9838),
(39, 'Jesús María, Córdoba, Argentina',                          -30.9833, -64.0986);

-- ── Tucumán ────────────────────────────────────────────────────────────────
INSERT INTO location (id, address, latitude, longitude) VALUES
(40, 'San Miguel de Tucumán, Tucumán, Argentina',                -26.8167, -65.2167),
(41, 'Tafí del Valle, Tucumán, Argentina',                       -26.8497, -65.6888);

-- ── Entre Ríos ─────────────────────────────────────────────────────────────
INSERT INTO location (id, address, latitude, longitude) VALUES
(42, 'Gualeguaychú, Entre Ríos, Argentina',                      -33.0158, -59.0311),
(43, 'Parque Nacional El Palmar, Entre Ríos, Argentina',         -31.8583, -58.2833),
(70, 'Colón, Entre Ríos, Argentina',                             -32.2233, -58.1467),
(71, 'Paraná, Entre Ríos, Argentina',                            -31.7333, -60.5333);

-- ── Corrientes ─────────────────────────────────────────────────────────────
INSERT INTO location (id, address, latitude, longitude) VALUES
(44, 'Esteros del Iberá, Corrientes, Argentina',                 -28.5000, -57.3000),
(45, 'Corrientes Capital, Corrientes, Argentina',                -27.4691, -58.8306);

-- ── Santa Fe ───────────────────────────────────────────────────────────────
INSERT INTO location (id, address, latitude, longitude) VALUES
(46, 'Rosario, Santa Fe, Argentina',                             -32.9587, -60.6932),
(47, 'Santa Fe Capital, Santa Fe, Argentina',                    -31.6333, -60.7000);

-- ── Mendoza ────────────────────────────────────────────────────────────────
INSERT INTO location (id, address, latitude, longitude) VALUES
(48, 'Mendoza Capital, Mendoza, Argentina',                      -32.8908, -68.8272),
(49, 'Valle de Uco, Mendoza, Argentina',                         -33.7000, -69.1000);

-- ── Neuquén ────────────────────────────────────────────────────────────────
INSERT INTO location (id, address, latitude, longitude) VALUES
(50, 'Neuquén Capital, Neuquén, Argentina',                      -38.9516, -68.0591),
(51, 'Volcán Lanín, Neuquén, Argentina',                         -39.6338, -71.4976),
(52, 'Villa La Angostura, Neuquén, Argentina',                   -40.7614, -71.6442);

-- ── San Juan ───────────────────────────────────────────────────────────────
INSERT INTO location (id, address, latitude, longitude) VALUES
(53, 'Parque Ischigualasto, San Juan, Argentina',                -30.1667, -67.9000),
(54, 'Santuario Difunta Correa, San Juan, Argentina',            -31.3667, -67.9167);

-- ── La Rioja ───────────────────────────────────────────────────────────────
INSERT INTO location (id, address, latitude, longitude) VALUES
(55, 'Parque Nacional Talampaya, La Rioja, Argentina',           -29.6667, -67.9333),
(56, 'Chilecito, La Rioja, Argentina',                           -29.1667, -67.5000);

-- ── Catamarca ──────────────────────────────────────────────────────────────
INSERT INTO location (id, address, latitude, longitude) VALUES
(57, 'Antofagasta de la Sierra, Catamarca, Argentina',           -26.0667, -67.4000);

-- ── Misiones ───────────────────────────────────────────────────────────────
INSERT INTO location (id, address, latitude, longitude) VALUES
(58, 'San Ignacio Miní, Misiones, Argentina',                    -27.2558, -55.5373);

-- ── Chaco, Formosa, Santiago del Estero ───────────────────────────────────
INSERT INTO location (id, address, latitude, longitude) VALUES
(59, 'Resistencia, Chaco, Argentina',                            -27.4606, -58.9875),
(60, 'Bañado La Estrella, Formosa, Argentina',                   -24.5000, -61.9000),
(61, 'Termas de Río Hondo, Santiago del Estero, Argentina',      -27.4990, -64.8433);

-- ── Jujuy, Río Negro, La Pampa, San Luis ──────────────────────────────────
INSERT INTO location (id, address, latitude, longitude) VALUES
(62, 'Purmamarca, Jujuy, Argentina',                             -23.7428, -65.4988),
(63, 'El Bolsón, Río Negro, Argentina',                          -41.9583, -71.5294),
(64, 'Parque Provincial Luro, La Pampa, Argentina',              -37.7917, -64.2333),
(65, 'Merlo, San Luis, Argentina',                               -32.3500, -65.0167);

-- ── Chubut, Santa Cruz (adicionales) ──────────────────────────────────────
INSERT INTO location (id, address, latitude, longitude) VALUES
(66, 'Esquel, Chubut, Argentina',                                -42.9075, -71.3139),
(67, 'Lago del Desierto, Santa Cruz, Argentina',                 -49.0800, -72.8500);

-- ── Islas Malvinas ────────────────────────────────────────────────────────
INSERT INTO location (id, address, latitude, longitude) VALUES
(172, 'Puerto Argentino (Puerto Stanley), Islas Malvinas, Argentina', -51.6938, -57.8567),
(173, 'Cementerio de Darwin, Islas Malvinas, Argentina',              -51.5767, -58.9500);

-- ── Sitios de mitos argentinos ────────────────────────────────────────────
INSERT INTO location (id, address, latitude, longitude) VALUES
(174, 'Mercedes, Corrientes, Argentina',         -29.1833, -58.0667),
(175, 'Lago Nahuel Huapi, Río Negro, Argentina', -41.0500, -71.5000);
