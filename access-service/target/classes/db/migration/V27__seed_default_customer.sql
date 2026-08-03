SET search_path TO sales;

INSERT INTO sales.customers (id, first_name, last_name, nit_ci, is_active)
VALUES (1, 'Consumidor', 'Final', '0', true);

-- Asegurar que la secuencia no colisione
SELECT setval('sales.customers_id_seq', 1, true);
