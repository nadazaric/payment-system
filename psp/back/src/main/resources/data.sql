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
           '$2y$10$GTRDoOGd27/Md.qaGZ/SReN5oSIVBtTVuIJDYspHwM5AeC9O3LsyK',
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
    manifest_path,
    health_check_path,
    active
)
VALUES
    (
        'BANK_PLUGIN',
        'Bank Payment Plugin',
        'http://localhost:8082',
        '/api/plugin/manifest',
        '/api/plugin/health',
        true
    ),
    (
        'MOCK_PLUGIN',
        'Mock Payment Plugin',
        'http://localhost:8085',
        '/api/plugin/manifest',
        '/api/plugin/health',
        true
    );

INSERT INTO payment_method (
    code,
    display_name,
    active,
    plugin_code,
    config_schema_json
)
VALUES
    (
        'CARD',
        'Payment card',
        true,
        'BANK_PLUGIN',
        '[{"fieldKey":"bankMerchantId","label":"Bank merchant ID","fieldType":"TEXT","required":true,"sensitive":false,"placeholder":"Enter bank merchant ID"},{"fieldKey":"bankMerchantPassword","label":"Bank merchant password","fieldType":"PASSWORD","required":true,"sensitive":true,"placeholder":"Enter bank merchant password"}]'
    ),
    (
        'QR_CODE',
        'QR code',
        true,
        'BANK_PLUGIN',
        '[{"fieldKey":"recipientAccountNumber","label":"Recipient account number","fieldType":"TEXT","required":true,"sensitive":false,"placeholder":"Enter recipient account number"},{"fieldKey":"recipientName","label":"Recipient name","fieldType":"TEXT","required":true,"sensitive":false,"placeholder":"Enter recipient name"}]'
    ),
    (
        'MOCK_PAY',
        'Mock payment',
        true,
        'MOCK_PLUGIN',
        '[{"fieldKey":"mockApiKey","label":"Mock API key","fieldType":"PASSWORD","required":true,"sensitive":true,"placeholder":"Enter mock API key"}]'
    );