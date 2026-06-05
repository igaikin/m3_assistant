/*
TRUNCATE qualificationClass CASCADE;
TRUNCATE roles CASCADE;
TRUNCATE professions CASCADE;
TRUNCATE trains CASCADE;
TRUNCATE users CASCADE;
 */
INSERT INTO qualificationClass (classes)
VALUES ('1'),
       ('2'),
       ('3'),
       ('-');

INSERT INTO roles (role)
VALUES ('CUSTOMER'),
       ('MANAGER'),
       ('ADMIN');

INSERT INTO professions (profession)
VALUES ('DRIVER'),
       ('INSTRUCTOR '),
       ('ENGINEER');

INSERT INTO trains (train)
VALUES ('10101-10901'),
       ('10102-10902'),
       ('10103-10903'),
       ('10104-10904'),
       ('10105-10905'),
       ('10106-10906'),
       ('70101-70901'),
       ('70102-70902'),
       ('70103-70903'),
       ('70104-70904'),
       ('70105-70905'),
       ('70106-70906'),
       ('70107-70907');

INSERT INTO users (avatar, first_name, surname, patronymic, personnel_number, qualificationClass_id, profession_id,
                   role_id, train_id,
                   email, password)
VALUES ('images/noAvatar.png', 'Admin', 'Test', 'Test', 0001,
        (SELECT id FROM qualificationClass WHERE classes = '1'),
        (SELECT id FROM professions WHERE profession = 'DRIVER'),
        (SELECT id FROM roles WHERE role = 'ADMIN'),
        (SELECT id FROM trains WHERE train = '10101-10901'),
        'admin', 'd033e22ae348aeb5660fc2140aec35850c4da997'),
       ('images/noAvatar.png', 'Manager', 'Test', 'Test', 0002,
        (SELECT id FROM qualificationClass WHERE classes = '2'),
        (SELECT id FROM professions WHERE profession = 'INSTRUCTOR'),
        (SELECT id FROM roles WHERE role = 'MANAGER'),
        (SELECT id FROM trains WHERE train = '10102-10902'),
        'manager', '1a8565a9dc72048ba03b4156be3e569f22771f23'),
       ('images/noAvatar.png', 'User', 'Test', 'Test', 0003,
        (SELECT id FROM qualificationClass WHERE classes = '3'),
        (SELECT id FROM professions WHERE profession = 'ENGINEER'),
        (SELECT id FROM roles WHERE role = 'CUSTOMER'),
        (SELECT id FROM trains WHERE train = '10103-10903'),
        'user', '12dea96fec20593566ab75692c9949596833adc9');

