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
           '845000000040484987',
           'Rent a Car Web Shop',
           0.00,
           'RSD',
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
           1000000.00,
           'RSD',
           true
       );

------------------------------ Security code: 348, 4111 1111 1111 1111
INSERT INTO payment_card (
    id,
    pan_hash,
    masked_pan,
    card_holder_name,
    expiration_month,
    expiration_year,
    active,
    bank_account_id
)
VALUES (
           '44444444-4444-4444-4444-444444444444',
           'tLESqsvqFWPrbbXkyVxYbMBuKfHVuTMdnNCwoc6y1pM',
           '4111 **** **** 1111',
           'Nada Zaric',
           12,
           2030,
           true,
           '22222222-2222-2222-2222-222222222222'
       );

------------------------------ Security code: 905, 4242 4242 4242 4242
INSERT INTO payment_card (
    id,
    pan_hash,
    masked_pan,
    card_holder_name,
    expiration_month,
    expiration_year,
    active,
    bank_account_id
)
VALUES (
           '55555555-5555-5555-5555-555555555555',
           'eAyaGdWVGUHNDI8ovRWAnDq1CkCnwYqdI9Fbh1IP9tg',
           '4242 **** **** 4242',
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
           'RSD',
           true
       );

------------------------------ Security code: 292, 5555555555554444
INSERT INTO payment_card (
    id,
    pan_hash,
    masked_pan,
    card_holder_name,
    expiration_month,
    expiration_year,
    active,
    bank_account_id
)
VALUES (
           '66666666-6666-6666-6666-666666666666',
           'FNzSdm5fkbimpwLapEr9RG4GjhqvD-GGQ5XlWYoPhL4',
           '5555 **** **** 4444',
           'Marko Markovic',
           12,
           2030,
           true,
           '33333333-3333-3333-3333-333333333333'
       );

------------------------------------------------------------------------------------------------------------------------ Test QR Payment
INSERT INTO payment (
    id,
    bank_merchant_id,
    amount,
    currency,
    stan,
    psp_timestamp,
    payment_method,
    success_url,
    fail_url,
    error_url,
    plugin_callback_url,
    status,
    created_at,
    expires_at,
    payment_attempt_used,
    qr_payment_reference
)
VALUES (
           '77777777-7777-7777-7777-777777777777',
           'BANK_MERCHANT_001',
           100.00,
           'RSD',
           'QRTEST001',
           CURRENT_TIMESTAMP,
           'QR',
           'http://localhost:3000/payment/success',
           'http://localhost:3000/payment/failed',
           'http://localhost:3000/payment/error',
           'http://localhost:8084/api/plugin/bank/callback',
           'CREATED',
           CURRENT_TIMESTAMP,
           CURRENT_TIMESTAMP + INTERVAL '15 minutes',
           false,
           '2606132130000001'
       );