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

------------------------------------------------------------------------------------------------------------------------ Goga Mikic
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
           '35160000000000000002',
           'Goga Mikic',
           1000000.00,
           'RSD',
           true
       );

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
           'Goga Mikic',
           12,
           2030,
           true,
           '22222222-2222-2222-2222-222222222222'
       );

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
           'Goga Mikic',
           12,
           2030,
           false,
           '22222222-2222-2222-2222-222222222222'
       );

------------------------------------------------------------------------------------------------------------------------ Ranka Milovanovic
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
           '35160000000000000003',
           'Ranka Milovanovic',
           250.00,
           'RSD',
           true
       );

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
           'Ranka Milovanovic',
           12,
           2030,
           true,
           '33333333-3333-3333-3333-333333333333'
       );