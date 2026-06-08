INSERT INTO APP_USER(name, username, email, password, role)
VALUES ('Nada Zaric', 'nada', 'nada@email.com', '$2a$12$7hc0JeJJx4ynUkfw1N528.pjD1/pIxXUEuf2mZhyANcJl6Td7vN3C', 'ADMIN');

INSERT INTO APP_USER(name, username, email, password, role)
VALUES ('Gordana Mikic', 'goga', 'goga@email.com', '$2a$10$XOdmu89Fp5WtTbY/trC3H.NnucImTNZB7UPBmCxJRsuizmeRv2YMu', 'CUSTOMER');

INSERT INTO APP_USER(name, username, email, password, role)
VALUES ('Ranka Milovanovic', 'ranka', 'ranka@email.com', '$2a$10$1nNhoiALYeW0/MyVtmY4wu/uZLLIc2iE2ByItCXQnsft/vZUqaiHe', 'CUSTOMER');

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
VALUES ('Volkswagen Golf 8', 'It is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. The point of using Lorem Ipsum is that it has a more-or-less normal distribution of letters, as opposed to using ''Content here, content here'', making it look like readable English. Many desktop publishing packages and web page editors now use Lorem Ipsum as their default model text, and a search for ''lorem ipsum'' will uncover many web sites still in their infancy. Various versions have evolved over the years, sometimes by accident, sometimes on purpose (injected humour and the like).', 'COMPACT', 45.00, '/images/vehicles/1.png');

INSERT INTO VEHICLE(name, description, type, price_per_day, image_path)
VALUES ('Toyota Yaris', 'Small and economical car, ideal for city driving and short rentals.', 'ECONOMY', 35.00, '/images/vehicles/2.png');

INSERT INTO VEHICLE(name, description, type, price_per_day, image_path)
VALUES ('BMW X3', 'Spacious SUV with premium comfort, suitable for longer trips and family travel.', 'SUV', 85.00, '/images/vehicles/3.jpg');

INSERT INTO VEHICLE(name, description, type, price_per_day, image_path)
VALUES ('Mercedes-Benz Vito', 'Large van with plenty of space, ideal for group travel or extra luggage.', 'VAN', 95.00, '/images/vehicles/4.jpg');

INSERT INTO RESERVATION(vehicle_id, user_id, insurance_package_id, start_date, end_date, total_price, payment_status)
VALUES (1, 3, 1, '2026-05-01', '2026-05-05', 180.00, 'SUCCESS');

INSERT INTO RESERVATION(vehicle_id, user_id, insurance_package_id, start_date, end_date, total_price, payment_status)
VALUES (2, 2, 1, '2026-05-10', '2026-05-14', 140.00, 'SUCCESS');

INSERT INTO RESERVATION(vehicle_id, user_id, insurance_package_id, start_date, end_date, total_price, payment_status)
VALUES (3, 3, 1, '2026-06-01', '2026-06-05', 372.00, 'SUCCESS');

INSERT INTO RESERVATION(vehicle_id, user_id, insurance_package_id, start_date, end_date, total_price, payment_status)
VALUES (4, 2, 1, '2026-06-10', '2026-06-13', 303.00, 'CREATED');

INSERT INTO RESERVATION(vehicle_id, user_id, insurance_package_id, start_date, end_date, total_price, payment_status)
VALUES (1, 2, 1, '2026-05-11', '2026-05-15', 180.00, 'CREATED');

INSERT INTO RESERVATION(vehicle_id, user_id, insurance_package_id, start_date, end_date, total_price, payment_status)
VALUES (2, 2, 1, '2026-05-02', '2026-05-06', 140.00, 'FAILED');

INSERT INTO RESERVATION(vehicle_id, user_id, insurance_package_id, start_date, end_date, total_price, payment_status)
VALUES (3, 2, 1, '2026-06-20', '2026-06-24', 340.00, 'FAILED');

INSERT INTO RESERVATION(vehicle_id, user_id, insurance_package_id, start_date, end_date, total_price, payment_status)
VALUES (4, 2, 2, '2026-04-20', '2026-04-25', 525.00, 'SUCCESS');

INSERT INTO RESERVATION(vehicle_id, user_id, insurance_package_id, start_date, end_date, total_price, payment_status)
VALUES (1, 2, 3, '2026-07-01', '2026-07-04', 216.00, 'SUCCESS');

INSERT INTO RESERVATION_ADDITIONAL_SERVICES(reservation_id, additional_service_id)
VALUES (3, 4);

INSERT INTO RESERVATION_ADDITIONAL_SERVICES(reservation_id, additional_service_id)
VALUES (4, 2);

INSERT INTO RESERVATION_ADDITIONAL_SERVICES(reservation_id, additional_service_id)
VALUES (9, 1);

INSERT INTO RESERVATION_ADDITIONAL_SERVICES(reservation_id, additional_service_id)
VALUES (9, 3);
