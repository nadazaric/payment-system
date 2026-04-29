INSERT INTO APP_USER(name, username, email, password, role)
VALUES ('Nada Zaric', 'nada', 'nada@email.com', '$2a$12$7hc0JeJJx4ynUkfw1N528.pjD1/pIxXUEuf2mZhyANcJl6Td7vN3C', 'ADMIN');

INSERT INTO APP_USER(name, username, email, password, role)
VALUES ('Gordana Mikic', 'goga', 'goga@email.com', '$2a$10$XOdmu89Fp5WtTbY/trC3H.NnucImTNZB7UPBmCxJRsuizmeRv2YMu', 'CUSTOMER');

INSERT INTO INSURANCE_PACKAGE(name, description, price_per_day)
VALUES ('Basic Insurance', 'Basic insurance included in the rental price.', 0.00);

INSERT INTO INSURANCE_PACKAGE(name, description, price_per_day)
VALUES ('Full Insurance', 'Additional protection in case of vehicle damage.', 10.00);

INSERT INTO INSURANCE_PACKAGE(name, description, price_per_day)
VALUES ('Premium Insurance', 'Full protection package with roadside assistance included.', 20.00);

INSERT INTO ADDITIONAL_SERVICE(name, description, price_per_day)
VALUES ('GPS Navigation', 'GPS navigation device for easier travel.', 5.00);

INSERT INTO ADDITIONAL_SERVICE(name, description, price_per_day)
VALUES ('Pet Friendly', 'Allows the customer to travel with a pet during the rental period.', 6.00);

INSERT INTO ADDITIONAL_SERVICE(name, description, price_per_day)
VALUES ('Child Seat', 'Child seat for safe travel with children.', 7.00);

INSERT INTO ADDITIONAL_SERVICE(name, description, price_per_day)
VALUES ('Winter Equipment', 'Winter equipment package suitable for driving in cold weather conditions.', 8.00);

INSERT INTO VEHICLE(name, description, type, price_per_day, image_path)
VALUES ('Volkswagen Golf 8', 'Comfortable compact car suitable for city and longer trips.', 'COMPACT', 45.00, '/images/vehicles/1.png');

INSERT INTO VEHICLE(name, description, type, price_per_day, image_path)
VALUES ('Toyota Yaris', 'Small and economical car, ideal for city driving and short rentals.', 'ECONOMY', 35.00, '/images/vehicles/2.png');

INSERT INTO VEHICLE(name, description, type, price_per_day, image_path)
VALUES ('BMW X3', 'Spacious SUV with premium comfort, suitable for longer trips and family travel.', 'SUV', 85.00, '/images/vehicles/3.png');

INSERT INTO VEHICLE(name, description, type, price_per_day, image_path)
VALUES ('Mercedes-Benz Vito', 'Large van with plenty of space, ideal for group travel or extra luggage.', 'VAN', 95.00, '/images/vehicles/4.png');
