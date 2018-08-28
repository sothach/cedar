INSERT INTO party (id, username, password, locale)
VALUES ('50395480-0805-11e3-8ffd-0800200c9a66', 'test', '29335b135381e4200971dafed2d8302107a1d881', 'en');
INSERT INTO party (id, username, password, locale)
VALUES ('67173b50-097b-11e3-8ffd-0800200c9a66', 'roy', 'd1676df4adf3aab778c04e7ddb81a87ae4342384', 'de');

INSERT INTO task (id, description, create_time, owner_id, state)
VALUES ('cb9e0df0-072e-11e3-8ffd-0800200c9a66',
        'Test Task Description #1',
        1003885078000,
        '50395480-0805-11e3-8ffd-0800200c9a66',
        'UNCHECKED');
INSERT INTO task (id, description, create_time, owner_id, state)
VALUES ('cb9e0df0-072e-11e3-8ffd-0800200c9a67',
        'Test Task Description #2',
        1003885078000,
        '50395480-0805-11e3-8ffd-0800200c9a66',
        'CHECKED');
INSERT INTO task (id, description, create_time, owner_id, state)
VALUES ('cb9e0df0-072e-11e3-8ffd-0800200c9a68',
        'Test Task Description #3',
        1003885078000,
        '50395480-0805-11e3-8ffd-0800200c9a66',
        'DELETED');
INSERT INTO task (id, description, create_time, owner_id, state)
VALUES ('cb9e0df0-072e-11e3-8ffd-0800200c9a69',
        'Test Task Description #4',
        1003885078000,
        '50395480-0805-11e3-8ffd-0800200c9a66',
        'UNCHECKED');

