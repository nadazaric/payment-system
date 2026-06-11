INSERT INTO merchant (
    bank_merchant_id,
    name
)
VALUES (
    'BANK_MERCHANT_001',
    'Rent a Car Web Shop'
)
ON CONFLICT (bank_merchant_id) DO NOTHING;