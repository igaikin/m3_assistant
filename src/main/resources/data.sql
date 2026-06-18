/*
TRUNCATE users CASCADE;
TRUNCATE qualificationClass CASCADE;
TRUNCATE roles CASCADE;
TRUNCATE professions CASCADE;
TRUNCATE trains CASCADE;
TRUNCATE file_alias CASCADE;


 */
INSERT INTO qualification_classes (id, qualification_class)
VALUES (1, '1'),
       (2, '2'),
       (3, '3'),
       (4, '-')
ON CONFLICT DO NOTHING;

INSERT INTO roles (id, role)
VALUES (1, 'ADMIN'),
       (2, 'MANAGER'),
       (3, 'CUSTOMER')
ON CONFLICT DO NOTHING;

INSERT INTO professions (id, profession)
VALUES (1, 'Машинист'),
       (2, 'Инженер'),
       (3, 'Инструктор')
ON CONFLICT DO NOTHING;

INSERT INTO trains (id, train)
VALUES (1, '10101-10901'),
       (2, '10102-10902'),
       (3, '10103-10903'),
       (4, '10104-10904'),
       (5, '10105-10905'),
       (6, '10106-10906'),
       (7, '70101-70901'),
       (8, '70102-70902'),
       (9, '70103-70903'),
       (10, '70104-70904'),
       (11, '70105-70905'),
       (12, '70106-70906'),
       (13, '70107-70907'),
       (99, '-')
ON CONFLICT DO NOTHING;

INSERT INTO users (avatar, first_name, surname, patronymic, personnel_number,
                   qualification_class_id, profession_id, role_id, train_id,
                   email, password)
SELECT *
FROM (VALUES ('uploads/avatars/e85948c5-a4f7-4579-aa2c-2b36cc7eedcb_av_cute-possum-wearing-clothes.jpg', 'Илья',
              'Гайкин', 'Викторович', '6177', 2, 1, 1, 1, 'admin',
              '$2a$12$eKFa90y6H.9J19CHHfZvj.2qSGZ02TZ6U8VdvfAvAh8vlKd6jqde6'),/*pass: 1111*/
             ('uploads/avatars/a5e981c5-64c5-49f9-9a16-673717b9a5b4_av_view-3d-cool-modern-bird.jpg', 'Виталий',
              'Тихонович', 'Владимирович', '0002', 1, 3, 2, 99, 'manager',
              '$2a$12$8n7j0hAX3VJu744f7ahhz.AFDX3TTrWYhZUZBkobdQvGyVeb0dYK6'),/*pass: 2222*/
             ('uploads/avatars/aafe816d-5468-42d1-9ba3-f88c44e295d9_av_3d-rendering-young-tiger-city.jpg', 'Павел',
              'Чистов', 'Александрович', '0003', 3, 1, 3, 4, 'user',
              '$2a$12$xRSnoAUg0HbJox9UFHg6EuWiHI.w4QjV.SEwDUvgUw.07UeFKT.mu')/*pass: 3333*/)
         AS DATA
WHERE NOT EXISTS (SELECT 1 FROM users);

