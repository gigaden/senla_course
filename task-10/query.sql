-- 1
SELECT model,
       speed,
       hd
FROM pc
WHERE price < 500::money;

-- 2
SELECT p.maker
FROM product p
         JOIN printer pr ON pr.model = p.model;

-- 3
SELECT model,
       ram,
       screen
FROM laptop
WHERE price > 1000::money;

-- 4
SELECT *
FROM printer
WHERE color = 'y';

-- 5
SELECT model,
       speed,
       hd
FROM pc
WHERE cd IN ('12x', '24x')
  AND price < 600::money;

-- 6
SELECT p.maker,
       l.speed
FROM product p
         JOIN laptop l ON l.model = p.model
WHERE l.hd >= 100;

-- 7 У меня нет производителя B, поэтому взял Dell
SELECT p.model,
       t.price
FROM product p
         JOIN (SELECT model,
                      price
               FROM laptop
               UNION
               SELECT model,
                      price
               FROM pc
               UNION
               SELECT model,
                      price
               FROM printer) t ON p.model = t.model
WHERE p.maker = 'Dell';

-- 8
SELECT DISTINCT maker
FROM product
WHERE type = 'PC'
  AND maker NOT IN (SELECT maker
                    FROM product
                    WHERE type = 'Laptop');

-- 9
SELECT DISTINCT pr.maker
FROM product pr
         JOIN pc p ON pr.model = p.model
WHERE p.speed >= 450;

-- 10
SELECT model,
       price
FROM printer
WHERE price = (SELECT MAX(PRICE)
               FROM printer);

-- 11
SELECT AVG(speed)
FROM pc;

-- 12
SELECT AVG(speed)
FROM laptop
WHERE price > 1000::money;

-- 13 у меня производителя A нет, поэтому взял Dell
SELECT AVG(p.speed)
FROM pc p
         JOIN product pr ON pr.model = p.model
WHERE pr.maker = 'Dell';

-- 14
SELECT speed,
       AVG(price::numeric)::money
FROM pc
GROUP BY speed;

-- 15
SELECT hd
FROM pc
GROUP BY hd
HAVING count(*) >= 2;

-- 16
SELECT DISTINCT p2.model AS biggest_model,
                p1.model AS smallest_model,
                p1.speed,
                p1.ram
FROM pc p1
         JOIN pc p2 ON p1.speed = p2.speed AND p1.ram = p2.ram AND p1.model < p2.model;

-- 17
SELECT (SELECT type FROM product WHERE model = l.model) AS type,
       l.model,
       l.speed
FROM laptop l
WHERE speed < (SELECT MIN(speed) FROM pc);

-- 18
SELECT p.maker, pr.price
FROM product p
         JOIN printer pr ON pr.model = p.model
WHERE pr.color = 'y'
  AND pr.price = (SELECT MIN(price) FROM printer WHERE color = 'y');

-- 19
SELECT p.maker,
       ROUND(AVG(l.screen)::numeric, 2) AS screen_size
FROM product p
         JOIN laptop l ON p.model = l.model
GROUP BY p.maker;

-- 20
SELECT pr.maker, COUNT(*) as model_count
FROM product pr
         JOIN pc p ON pr.model = p.model
GROUP BY pr.maker
HAVING COUNT(*) >= 3;

-- 21
SELECT pr.maker,
       MAX(p.price) AS max_price
FROM product pr
         JOIN pc p ON pr.model = p.model
GROUP BY pr.maker
ORDER BY max_price DESC;

-- 22
SELECT speed, AVG(price::numeric)::money AS avg_price
FROM pc
WHERE speed > 600
GROUP BY speed
ORDER BY speed;

-- 23
SELECT maker
FROM product
WHERE model IN (SELECT model FROM pc WHERE speed >= 750)
INTERSECT
SELECT maker
FROM product
WHERE model IN (SELECT model FROM laptop WHERE speed >= 750);

-- 24
WITH all_prices AS (SELECT model, price
                    FROM pc
                    UNION ALL
                    SELECT model, price
                    FROM laptop
                    UNION ALL
                    SELECT model, price
                    FROM printer),
     max_price AS (SELECT MAX(price) as max_price
                   FROM all_prices)
SELECT model, price
FROM all_prices
WHERE price = (SELECT max_price FROM max_price);

-- 25
WITH pc_specs AS (SELECT p.maker, pc.ram, pc.speed
                  FROM product p
                           JOIN pc ON p.model = pc.model
                  WHERE pc.ram = (SELECT MIN(ram) FROM pc)),
     fastest_pc AS (SELECT maker
                    FROM pc_specs
                    WHERE speed = (SELECT MAX(speed) FROM pc_specs))
SELECT DISTINCT p.maker
FROM product p
WHERE p.type = 'Printer'
  AND p.maker IN (SELECT maker FROM fastest_pc);