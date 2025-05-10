-- 테스트 관리자 계정 생성 (비밀번호: admin123)
INSERT INTO users (email, password, name, phone_number, role, created_at, updated_at)
VALUES ('admin@example.com', '$2a$10$rDkPvvAFV6GgJjXpYWxqUOQZxXxXxXxXxXxXxXxXxXxXxXxXxXx', 'Admin User', '010-1234-5678', 'ADMIN', NOW(), NOW());

-- 테스트 일반 사용자 계정 생성 (비밀번호: user123)
INSERT INTO users (email, password, name, phone_number, role, created_at, updated_at)
VALUES ('user@example.com', '$2a$10$rDkPvvAFV6GgJjXpYWxqUOQZxXxXxXxXxXxXxXxXxXxXxXxXxXx', 'Test User', '010-8765-4321', 'USER', NOW(), NOW());

-- 테스트 사용자 잔액 생성
INSERT INTO balances (user_id, amount, created_at, updated_at)
SELECT id, 10000.00, NOW(), NOW()
FROM users
WHERE email = 'user@example.com';

-- 테스트 거래 내역 생성
INSERT INTO transactions (user_id, amount, type, status, description, created_at, updated_at)
SELECT id, 5000.00, 'DEPOSIT', 'COMPLETED', 'Initial deposit', NOW(), NOW()
FROM users
WHERE email = 'user@example.com';

-- 테스트 감사 로그 생성
INSERT INTO audit_logs (user_id, action, details, ip_address, user_agent, created_at)
SELECT id, 'USER_CREATED', 'Test user account created', '127.0.0.1', 'Mozilla/5.0', NOW()
FROM users
WHERE email = 'user@example.com'; 