INSERT INTO APP_USER(name, username, email, password, role)
VALUES ('Nada Zaric', 'nada', 'nada@email.com', '$2a$12$7hc0JeJJx4ynUkfw1N528.pjD1/pIxXUEuf2mZhyANcJl6Td7vN3C', 'ADMIN');

INSERT INTO APP_USER(name, username, email, password, role)
VALUES ('Gordana Mikic', 'goga', 'goga@email.com', '$2a$10$XOdmu89Fp5WtTbY/trC3H.NnucImTNZB7UPBmCxJRsuizmeRv2YMu', 'CUSTOMER');

INSERT INTO APP_USER(name, username, email, password, role)
VALUES ('Ranka Milovanovic', 'ranka', 'ranka@email.com', '$2a$10$1nNhoiALYeW0/MyVtmY4wu/uZLLIc2iE2ByItCXQnsft/vZUqaiHe', 'CUSTOMER');

INSERT INTO INSURANCE_PACKAGE(name, description, price_per_day)
VALUES ('Basic Insurance', 'Basic insurance included in the rental price.', 0.00);

INSERT INTO INSURANCE_PACKAGE(name, description, price_per_day)
VALUES ('Full Insurance', 'Additional protection in case of vehicle damage.', 1000.00);

INSERT INTO INSURANCE_PACKAGE(name, description, price_per_day)
VALUES ('Premium Insurance', 'Full protection package with roadside assistance included.', 2000.00);

INSERT INTO ADDITIONAL_SERVICE(name, description, price_per_day)
VALUES ('GPS Navigation', 'GPS navigation device for easier travel.', 500.00);

INSERT INTO ADDITIONAL_SERVICE(name, description, price_per_day)
VALUES ('Pet Friendly', 'Allows the customer to travel with a pet during the rental period.', 600.00);

INSERT INTO ADDITIONAL_SERVICE(name, description, price_per_day)
VALUES ('Child Seat', 'Child seat for safe travel with children.', 700.00);

INSERT INTO ADDITIONAL_SERVICE(name, description, price_per_day)
VALUES ('Winter Equipment', 'Winter equipment package suitable for driving in cold weather conditions.', 800.00);

INSERT INTO VEHICLE(name, description, type, price_per_day, image_path)
VALUES ('Volkswagen Golf 8', 'It is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. The point of using Lorem Ipsum is that it has a more-or-less normal distribution of letters, as opposed to using ''Content here, content here'', making it look like readable English. Many desktop publishing packages and web page editors now use Lorem Ipsum as their default model text, and a search for ''lorem ipsum'' will uncover many web sites still in their infancy. Various versions have evolved over the years, sometimes by accident, sometimes on purpose (injected humour and the like).', 'COMPACT', 2500.00, '/images/vehicles/1.png');

INSERT INTO VEHICLE(name, description, type, price_per_day, image_path)
VALUES ('Toyota Yaris', 'Small and economical car, ideal for city driving and short rentals.', 'ECONOMY', 3000.00, '/images/vehicles/2.png');

INSERT INTO VEHICLE(name, description, type, price_per_day, image_path)
VALUES ('BMW X3', 'Spacious SUV with premium comfort, suitable for longer trips and family travel.', 'SUV', 5000.00, '/images/vehicles/3.jpg');

INSERT INTO VEHICLE(name, description, type, price_per_day, image_path)
VALUES ('Mercedes-Benz Vito', 'Large van with plenty of space, ideal for group travel or extra luggage.', 'VAN', 6000.00, '/images/vehicles/4.jpg');