-- 테스트 사용자 데이터 삽입
INSERT INTO users (email, password, name, phone_number, created_at, updated_at, is_active)
VALUES 
    ('test1@example.com', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', '테스트 사용자1', '010-1234-5678', NOW(), NOW(), true),
    ('test2@example.com', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', '테스트 사용자2', '010-8765-4321', NOW(), NOW(), true);

-- 테스트 잔액 데이터 삽입
INSERT INTO balances (user_id, amount, last_updated, created_at, updated_at)
SELECT id, 10000.00, NOW(), NOW(), NOW()
FROM users
WHERE email = 'test1@example.com';

INSERT INTO balances (user_id, amount, last_updated, created_at, updated_at)
SELECT id, 5000.00, NOW(), NOW(), NOW()
FROM users
WHERE email = 'test2@example.com';

-- 테스트 거래 데이터 삽입
INSERT INTO transactions (user_id, amount, type, status, description, timestamp, created_at, updated_at)
SELECT 
    id,
    1000.00,
    'PAYMENT',
    'COMPLETED',
    '테스트 결제',
    NOW(),
    NOW(),
    NOW()
FROM users
WHERE email = 'test1@example.com';

INSERT INTO transactions (user_id, amount, type, status, description, timestamp, created_at, updated_at)
SELECT 
    id,
    2000.00,
    'TOP_UP',
    'COMPLETED',
    '테스트 충전',
    NOW(),
    NOW(),
    NOW()
FROM users
WHERE email = 'test2@example.com';

-- 테스트 감사 로그 데이터 삽입
INSERT INTO audit_logs (user_id, action, type, status, details, timestamp, created_at, updated_at)
SELECT 
    id,
    'LOGIN',
    'USER_ACTION',
    'SUCCESS',
    '로그인 성공',
    NOW(),
    NOW(),
    NOW()
FROM users
WHERE email = 'test1@example.com';

INSERT INTO audit_logs (user_id, action, type, status, details, timestamp, created_at, updated_at)
SELECT 
    id,
    'PAYMENT',
    'TRANSACTION',
    'SUCCESS',
    '결제 성공',
    NOW(),
    NOW(),
    NOW()
FROM users
WHERE email = 'test2@example.com'; 