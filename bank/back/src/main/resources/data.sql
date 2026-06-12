------------------------------------------------------------------------------------------------------------------------ Web Shop
INSERT INTO bank_account (
    id,
    account_number,
    holder_name,
    balance,
    currency,
    active
)
VALUES (
           '11111111-1111-1111-1111-111111111111',
           'RS35160000000000000001',
           'Rent a Car Web Shop',
           0.00,
           'EUR',
           true
       );

INSERT INTO merchant (
    bank_merchant_id,
    name,
    bank_account_id
)
VALUES (
           'BANK_MERCHANT_001',
           'Rent a Car Web Shop',
           '11111111-1111-1111-1111-111111111111'
       );

------------------------------------------------------------------------------------------------------------------------ Nada Zaric
INSERT INTO bank_account (
    id,
    account_number,
    holder_name,
    balance,
    currency,
    active
)
VALUES (
           '22222222-2222-2222-2222-222222222222',
           'RS35160000000000000002',
           'Nada Zaric',
           10000.00,
           'EUR',
           true
       );


INSERT INTO payment_card (
    id,
    pan,
    card_holder_name,
    expiration_month,
    expiration_year,
    active,
    bank_account_id
)
VALUES (
           '44444444-4444-4444-4444-444444444444',
           '4111111111111111',
           'Nada Zaric',
           12,
           2030,
           true,
           '22222222-2222-2222-2222-222222222222'
       );

INSERT INTO payment_card (
    id,
    pan,
    card_holder_name,
    expiration_month,
    expiration_year,
    active,
    bank_account_id
)
VALUES (
           '55555555-5555-5555-5555-555555555555',
           '4242424242424242',
           'Nada Zaric',
           12,
           2030,
           false,
           '22222222-2222-2222-2222-222222222222'
       );

------------------------------------------------------------------------------------------------------------------------ Marko Markovic
INSERT INTO bank_account (
    id,
    account_number,
    holder_name,
    balance,
    currency,
    active
)
VALUES (
           '33333333-3333-3333-3333-333333333333',
           'RS35160000000000000003',
           'Marko Markovic',
           250.00,
           'EUR',
           true
       );

INSERT INTO payment_card (
    id,
    pan,
    card_holder_name,
    expiration_month,
    expiration_year,
    active,
    bank_account_id
)
VALUES (
           '66666666-6666-6666-6666-666666666666',
           '5555555555554444',
           'Marko Markovic',
           12,
           2030,
           true,
           '33333333-3333-3333-3333-333333333333'
       );

INSERT INTO payment (
    id,
    bank_merchant_id,
    stan,
    psp_timestamp,
    payment_method,
    amount,
    currency,
    success_url,
    fail_url,
    error_url,
    plugin_callback_url,
    status,
    created_at,
    expires_at,
    payment_attempt_used
)
VALUES (
           '77777777-7777-7777-7777-777777777777',
           'BANK_MERCHANT_001',
           '123456',
           CURRENT_TIMESTAMP,
           'CARD',
           100.00,
           'EUR',
           'http://localhost:3001/payment/success',
           'http://localhost:3001/payment/failed',
           'http://localhost:3001/payment/error',
           'http://localhost:8086/api/plugin/payments/bank-callback',
           'CREATED',
           CURRENT_TIMESTAMP,
           CURRENT_TIMESTAMP + INTERVAL '30 minutes',
           false
       )
    ON CONFLICT (id) DO UPDATE
                            SET status = 'CREATED',
                            payment_attempt_used = false,
                            expires_at = CURRENT_TIMESTAMP + INTERVAL '30 minutes';