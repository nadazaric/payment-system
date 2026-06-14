INSERT INTO bank_plugin_seller_configuration (
    id,
    merchant_id,
    seller_reference,
    payment_method_code,
    bank_merchant_id
)
VALUES (
           '11111111-1111-1111-1111-111111111111',
           'MER-TEST0001',
           'MAIN_SELLER',
           'CARD',
           'BANK_MERCHANT_001'
       );

INSERT INTO bank_plugin_seller_configuration (
    id,
    merchant_id,
    seller_reference,
    payment_method_code,
    bank_merchant_id
)
VALUES (
           '22222222-2222-2222-2222-222222222222',
           'MER-TEST0001',
           'MAIN_SELLER',
           'QR',
           'BANK_MERCHANT_001'
       );