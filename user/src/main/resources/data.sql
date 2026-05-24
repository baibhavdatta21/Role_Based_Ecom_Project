INSERT INTO user_role(role) VALUES ('ADMIN');

INSERT INTO address(street, city, state, country, zipcode)
VALUES ('123 Main St', 'New York', 'NY', 'USA', '10001');
INSERT INTO users(first_name, last_name, email, phone, password, address_id)
VALUES ('John2', 'Doe2', 'john@gmail.com', '335128571', '12', 1);

INSERT INTO user_role_inner(user_id, role_id)
VALUES (1, 1);
