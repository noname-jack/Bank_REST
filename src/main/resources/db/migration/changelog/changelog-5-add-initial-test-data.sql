-- password: 123456
INSERT INTO users (id, username, password_hash, role)
VALUES (1, 'admin', '$2a$10$uZhK9cNy.AgQ0Ch.6JH2S.XS57F8YW/fzGfVF2DwdwPQYcSoh8UCu', 'ADMIN');


-- password: 112233
-- password: 223344
INSERT INTO users (id, username, password_hash, role)
VALUES (2, 'user1', '$2a$10$mgfWUInMr9Ocnj9VQbfDV.G8d5tnreZAWFHLwVHT1L6GAWU8wBn4a', 'USER'),
       (3, 'user2', '$2a$10$J2wCrNstR.DICbNWx4b8ZeFEiRaS62OEiESOOvnzFubz2aOhqv9q2', 'USER');

-- 1234567890121234
-- 1234567890125678
-- 1234567890129012
INSERT INTO cards (id, user_id, card_number, expiration_date, status, balance)
VALUES (1, 1, '5CJXeVoxfNdF9pM3LIQ4LjDzpqv5d3U8LJytpSNQQ9SzrmHwR7mHpuES258=', '2028-12-31', 'ACTIVE', 1000.00),
       (2, 1, 'bMZyxGHfNUA0+Sx8qVMDLfJ55LVApkACRXpG9HsB86hhkXHAtHWqp5jdRtQ=', '2028-12-31', 'ACTIVE', 500.00),
       (3, 1, 'KqfZnv0mTOa+oUoflqd6rEHY9KltDqHFACQ1Z3o3n2irXFgePFLxqYjAbro=', '2028-12-31', 'BLOCKED', 200.00);

-- 2234567890123456
-- 2234567890127890
-- 2234567890122345
INSERT INTO cards (id, user_id, card_number, expiration_date, status, balance)
VALUES (4, 2, 'BPpyGifsBDK12XEwCO51hcgsw9gnhNzlHD+vl7pGAFoeEJTC5gR/MQUZ8vQ=', '2028-12-31', 'ACTIVE', 800.00),
       (5, 2, 'yJle0bgSlkI1UytEAYmoD5q825kzUwP/qXtoe1O/UB+eLDoEvS6QT2miInA=', '2028-12-31', 'ACTIVE', 300.00),
       (6, 2, '1SKLa4Hn1m/EPEmr+ygKOSp9qfKVl5VmUtIn59MWftjsXhmIyh3wiy+U5WU=', '2028-12-31', 'BLOCKED', 100.00);

-- 3234567890123456
-- 3234567890123457
-- 3234567890123458
INSERT INTO cards (id, user_id, card_number, expiration_date, status, balance)
VALUES (7, 3, 'tQq50rP9lK4IBJHg6Lffte/35gVfa22GlPpUQ8q/8vxfNdiUFP08I4oeu7g=', '2028-12-31', 'ACTIVE', 1200.00),
       (8, 3, 'rfFzFiAk5GHdjqFlC+Wb1AvSZDeI5qs7TINPxsGtm/Fql7fEadDxd6oxfs4=', '2028-12-31', 'ACTIVE', 400.00),
       (9, 3, '2aUrppJHdRKd/Z2aiWYF7RmfdWhfYAYX+rBARGi1x0fheJMUSFDxkh9jWj4=', '2028-12-31', 'BLOCKED', 50.00);
