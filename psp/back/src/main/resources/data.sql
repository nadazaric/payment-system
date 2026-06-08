INSERT INTO super_admin (
    username,
    password_hash,
    name,
    active
)
VALUES (
           'superadmin',
           '$2y$10$r6nKsSEwhdNgT741zO1njufAy8JCTd1txS70JWvqMFecmEysf9yMK',
           'PSP Super Admin',
           true
       );

INSERT INTO merchant (
    merchant_id,
    merchant_name,
    merchant_password_hash,
    currency,
    success_url,
    fail_url,
    error_url,
    active,
    created_at,
    updated_at
)
VALUES (
           'MER-TEST0001',
           'Vehicle Rental Agency',
           '$2y$10$VVUV..VIlKxpt6Gf5kQI4ezeohwiA7x6.WMufSFqJkUDyKiKtHYuO',
           'EUR',
           'http://localhost:3000/payment/success',
           'http://localhost:3000/payment/failed',
           'http://localhost:3000/payment/error',
           false,
           CURRENT_TIMESTAMP,
           CURRENT_TIMESTAMP
       );

INSERT INTO merchant_admin (
    id,
    merchant_id,
    username,
    password_hash,
    name,
    active
)
VALUES (
           'admin-test-001',
           'MER-TEST0001',
           'admin',
           '$2y$10$r6nKsSEwhdNgT741zO1njufAy8JCTd1txS70JWvqMFecmEysf9yMK',
           'Vehicle Rental Admin',
           true
       );

INSERT INTO merchant_seller_account (
    id,
    merchant_id,
    seller_reference,
    display_name,
    active
)
VALUES (
           'seller-test-001',
           'MER-TEST0001',
           'MAIN_SELLER',
           'Main seller',
           false
       );

INSERT INTO payment_plugin (
    code,
    display_name,
    base_url,
    active_by_admin,
    active,
    encrypted_plugin_secret
)
VALUES (
           'MOCK_PLUGIN',
           'Mock Payment Plugin',
           NULL,
           true,
           false,
           'LluElvujwjREXdPz1Z0ClqafYEEdUbzE8R1MqTRB0mbxix6R611K5wx/OGLh/vi5RuPImKjKjwYTgMtTx8FIbegjh38Aew=='
       );