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
           'RSD',
           'https://localhost:3001/payment/success',
           'https://localhost:3001/payment/failed',
           'https://localhost:3001/payment/error',
           true,
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
           true
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
           'BANK_PLUGIN',
           'Bank Payment Plugin',
           'https://localhost:5103/',
           true,
           true,
           'JG/CXiRyNO1sSYmt9EJsxSGIQK6T96VTGeVBYqd6l3LIJTdl23ptfiY/lgJ7DXspsme64QWwmwvIZikpTmuTacyXJfGDyA=='
       );

INSERT INTO payment_method (
    code,
    display_name,
    active,
    plugin_code,
    config_schema_json
)
VALUES (
           'CARD',
           'Payment card',
           true,
           'BANK_PLUGIN',
           '[{"fieldName":"bankMerchantId","fieldType":"TEXT"}]'
       );

INSERT INTO payment_method (
    code,
    display_name,
    active,
    plugin_code,
    config_schema_json
)
VALUES (
           'QR',
           'QR payment',
           true,
           'BANK_PLUGIN',
           '[{"fieldName":"bankMerchantId","fieldType":"TEXT"}]'
       );

INSERT INTO merchant_seller_payment_method (
    id,
    seller_account_id,
    payment_method_code,
    configured
)
VALUES (
           'seller-method-card-001',
           'seller-test-001',
           'CARD',
           true
       );

INSERT INTO merchant_seller_payment_method (
    id,
    seller_account_id,
    payment_method_code,
    configured
)
VALUES (
           'seller-method-qr-001',
           'seller-test-001',
           'QR',
           true
       );