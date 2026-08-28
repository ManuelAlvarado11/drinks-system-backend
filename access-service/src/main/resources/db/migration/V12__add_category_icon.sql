-- ============================================================================
-- V12__add_category_icon.sql
-- Adds an `icon` column to inventory.categories so each category can define its
-- own Material Design icon name (e.g. sports_bar, wine_bar, restaurant).
-- Used by the POS and menu views to render a per-category icon.
-- ============================================================================

SET search_path TO inventory;

-- =============================================================================
-- 1. Nueva columna: icon (nombre del icono de Material Design)
--    NULL = el frontend usa una heurística por nombre como fallback.
-- =============================================================================
ALTER TABLE inventory.categories
    ADD COLUMN icon VARCHAR(50);

-- =============================================================================
-- 2. Semilla de iconos por defecto para categorías existentes según su nombre.
--    Coincidencia case-insensitive por palabras clave. Es best-effort; el
--    usuario puede ajustar el icono desde el CRUD de categorías.
-- =============================================================================
UPDATE inventory.categories
SET icon = 'sports_bar'
WHERE icon IS NULL AND (
    lower(name) LIKE '%cerveza%' OR lower(name) LIKE '%beer%' OR
    lower(name) LIKE '%pilsener%' OR lower(name) LIKE '%lager%'
);

UPDATE inventory.categories
SET icon = 'wine_bar'
WHERE icon IS NULL AND (lower(name) LIKE '%vino%' OR lower(name) LIKE '%wine%');

UPDATE inventory.categories
SET icon = 'local_bar'
WHERE icon IS NULL AND (
    lower(name) LIKE '%trago%' OR lower(name) LIKE '%coctel%' OR lower(name) LIKE '%cóctel%' OR
    lower(name) LIKE '%cocktail%' OR lower(name) LIKE '%licor%' OR lower(name) LIKE '%whisky%' OR
    lower(name) LIKE '%ron%' OR lower(name) LIKE '%vodka%'
);

UPDATE inventory.categories
SET icon = 'restaurant'
WHERE icon IS NULL AND (
    lower(name) LIKE '%comida%' OR lower(name) LIKE '%food%' OR lower(name) LIKE '%pupusa%' OR
    lower(name) LIKE '%plato%' OR lower(name) LIKE '%snack%' OR lower(name) LIKE '%boquita%'
);

UPDATE inventory.categories
SET icon = 'local_drink'
WHERE icon IS NULL AND (
    lower(name) LIKE '%refresco%' OR lower(name) LIKE '%gaseosa%' OR lower(name) LIKE '%soda%' OR
    lower(name) LIKE '%agua%' OR lower(name) LIKE '%jugo%' OR lower(name) LIKE '%bebida%'
);

UPDATE inventory.categories
SET icon = 'local_cafe'
WHERE icon IS NULL AND (
    lower(name) LIKE '%cafe%' OR lower(name) LIKE '%café%' OR lower(name) LIKE '%coffee%' OR
    lower(name) LIKE '%te%' OR lower(name) LIKE '%té%'
);

-- Cualquier categoría restante sin coincidencia queda con un icono genérico.
UPDATE inventory.categories
SET icon = 'category'
WHERE icon IS NULL;
