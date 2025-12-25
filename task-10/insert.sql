TRUNCATE TABLE product CASCADE;
TRUNCATE TABLE pc CASCADE;
TRUNCATE TABLE laptop CASCADE;
TRUNCATE TABLE printer CASCADE;

-- Попросил ии нагенерить реальные данные, чтобы было интереснее)

INSERT INTO product (maker, model, type)
VALUES ('Dell', 'Inspiron 3000', 'PC'),
       ('Dell', 'OptiPlex 7070', 'PC'),
       ('Dell', 'Latitude 3520', 'Laptop'),
       ('Dell', 'XPS 13', 'Laptop'),
       ('Dell', 'S2421HN', 'PC'),
       ('Dell', 'LaserJet 1100', 'Printer'),
       ('HP', 'EliteDesk 800', 'PC'),
       ('HP', 'ProDesk 600', 'PC'),
       ('HP', 'LaserJet Pro', 'Printer'),
       ('HP', 'OfficeJet Pro', 'Printer'),
       ('Lenovo', 'ThinkCentre', 'PC'),
       ('Lenovo', 'ThinkStation', 'PC'),
       ('Lenovo', 'ThinkPad X1', 'Laptop'),
       ('Lenovo', 'Yoga 7i', 'Laptop'),
       ('Lenovo', 'Legion 5', 'Laptop'),
       ('Asus', 'ZenBook 14', 'Laptop'),
       ('Asus', 'ROG Zephyrus', 'Laptop'),
       ('Acer', 'Aspire TC', 'PC'),
       ('Acer', 'Veriton X', 'PC'),
       ('Acer', 'Predator Orion', 'PC'),
       ('Canon', 'PIXMA TR', 'Printer'),
       ('Canon', 'imageCLASS', 'Printer'),
       ('Epson', 'EcoTank', 'Printer'),
       ('Microsoft', 'Surface Studio', 'PC'),
       ('Microsoft', 'Surface Pro', 'Laptop'),
       ('Samsung', 'Galaxy Book', 'Laptop'),
       ('Apple', 'Mac Pro', 'PC'),
       ('Apple', 'MacBook Pro', 'Laptop'),
       ('Intel', 'NUC 11', 'PC'),
       ('Razer', 'Blade 15', 'Laptop'),
       ('Brother', 'HL-L2350', 'Printer');

INSERT INTO pc (code, model, speed, ram, hd, cd, price)
VALUES (1, 'Inspiron 3000', 3200, 16, 500, '12x', 450.00),
       (2, 'Aspire TC', 2800, 8, 320, '24x', 480.00),
       (3, 'ThinkCentre', 3500, 4, 250, '12x', 550.00),
       (4, 'EliteDesk 800', 3000, 8, 320, '24x', 580.00),
       (5, 'Mac Pro', 450, 16, 500, '8x', 800.00),
       (6, 'ThinkStation', 500, 32, 1000, '16x', 1200.00),
       (7, 'OptiPlex 7070', 600, 64, 2000, '24x', 1500.00),
       (8, 'Veriton X', 3200, 16, 500, '12x', 900.00),
       (9, 'Surface Studio', 3400, 32, 500, '12x', 1100.00),
       (10, 'Predator Orion', 3200, 16, 500, '12x', 950.00),
       (11, 'NUC 11', 3200, 16, 750, '12x', 1050.00),
       (12, 'S2421HN', 800, 2, 160, '4x', 300.00),
       (13, 'ProDesk 600', 1000, 2, 200, '8x', 350.00),
       (14, 'Aspire TC', 3600, 8, 500, '8x', 600.00),
       (15, 'Veriton X', 3800, 16, 1000, '12x', 850.00),
       (16, 'Predator Orion', 4000, 32, 2000, '16x', 1200.00);

INSERT INTO laptop (code, model, speed, ram, hd, price, screen)
VALUES (1, 'XPS 13', 3200, 16, 512, 1200.00, 13.4),
       (2, 'MacBook Pro', 3400, 32, 1000, 1500.00, 16.0),
       (3, 'Latitude 3520', 2800, 8, 100, 800.00, 15.6),
       (4, 'ThinkPad X1', 3000, 16, 1000, 1300.00, 14.0),
       (5, 'Yoga 7i', 2000, 8, 256, 600.00, 14.0),
       (6, 'ZenBook 14', 2200, 8, 256, 650.00, 14.0),
       (7, 'ThinkPad X1', 3200, 16, 512, 1100.00, 13.3),
       (8, 'Legion 5', 3500, 32, 1000, 1800.00, 17.3),
       (9, 'Surface Pro', 3600, 64, 2000, 2200.00, 13.5),
       (10, 'ZenBook 14', 3300, 16, 512, 1000.00, 14.0),
       (11, 'ROG Zephyrus', 4200, 32, 1000, 2000.00, 15.6),
       (12, 'Galaxy Book', 3100, 16, 512, 1100.00, 15.6),
       (13, 'Blade 15', 4500, 32, 1000, 2500.00, 15.6);

INSERT INTO printer (code, model, color, type, price)
VALUES (1, 'LaserJet Pro', 'y', 'Laser', 200.00),
       (2, 'PIXMA TR', 'y', 'Jet', 150.00),
       (3, 'OfficeJet Pro', 'y', 'Jet', 180.00),
       (4, 'imageCLASS', 'n', 'Laser', 100.00),
       (5, 'EcoTank', 'y', 'Laser', 500.00),
       (6, 'HL-L2350', 'y', 'Jet', 500.00),
       (7, 'LaserJet 1100', 'y', 'Laser', 120.00),
       (8, 'PIXMA TR', 'y', 'Jet', 120.00),
       (9, 'LaserJet Pro', 'y', 'Laser', 250.00),
       (10, 'OfficeJet Pro', 'n', 'Jet', 200.00);