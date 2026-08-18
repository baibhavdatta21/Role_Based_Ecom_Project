User Microservice
---------------------------------------------------------------------------------------------------------------------------------------------
CREATE TABLE user_role (
    role_id INT AUTO_INCREMENT PRIMARY KEY,
    role    VARCHAR(50) NOT NULL
);
INSERT INTO user_role(role) VALUES ('ADMIN');
INSERT INTO user_role(role) VALUES ('SELLER');
INSERT INTO user_role(role) VALUES ('CUSTOMER');

CREATE TABLE address (
    id INT AUTO_INCREMENT PRIMARY KEY,
    street 	VARCHAR(400),
	city   	VARCHAR(100),
	state  	VARCHAR(100),
	country VARCHAR(100),
	Zipcode VARCHAR(100),
);
INSERT INTO address(street, city, state, country, zipcode)
VALUES ('123 Main St', 'New York', 'NY', 'USA', '10001');

INSERT INTO address (street, city, state, country, zipcode)
VALUES ('456 Park Ave', 'New York', 'NY', 'USA', '10022');

INSERT INTO address (street, city, state, country, zipcode)
VALUES ('789 Broadway', 'New York', 'NY', 'USA', '10003');

CREATE TABLE users (
    id 			INT AUTO_INCREMENT PRIMARY KEY,
    first_name 	VARCHAR(100),
	last_name      	VARCHAR(100),
	email  	VARCHAR(100) UNIQUE,
	password 	VARCHAR(255),
	phone 		VARCHAR(100),
	address_id  INT UNIQUE,
	created_at    	DATETIME,
	updated_at    	DATETIME,
	CONSTRAINT fk_user_address FOREIGN KEY (address_id) REFERENCES address(id)
);
INSERT INTO users(first_name, last_name, email, phone, password, address_id)
VALUES ('John2', 'Doe2', 'john@gmail.com', '335128571', '12', 1);

INSERT INTO users (first_name, last_name, email, phone, password, address_id)
VALUES ('Jane', 'Smith', 'jane@gmail.com', '9876543210', '12', 2);

INSERT INTO users (first_name, last_name, email, phone, password, address_id)
VALUES ('Robert', 'Brown', 'robert@gmail.com', '8765432109', '12', 3);

CREATE TABLE user_role_inner (
    user_id INT,
    role_id INT
);
INSERT INTO user_role_inner(user_id, role_id)
VALUES (1, 1);
INSERT INTO user_role_inner(user_id, role_id)
VALUES (2, 2);
INSERT INTO user_role_inner(user_id, role_id)
VALUES (3,3);
-------------------------------------------------------------------------------------------------------------------------------------------------
Product Microservice
Create TABLE product(
	id 				INT AUTO_INCREMENT PRIMARY KEY,
	name			VARCHAR(100),
	description 	VARCHAR(200),
	price 			 DECIMAL(10, 2),
	stock_quantity  INT,
	category		VARCHAR(100),
	image_url		VARCHAR(100),
	active 		TINYINT(1),
	created_at    	DATETIME,
	updated_at    	DATETIME
)

INSERT INTO product (name, description, price, stock_quantity, category, image_url, active, created_at, updated_at)
VALUES
('Running Shoes', 'Lightweight running shoes with cushioned sole', 59.99, 100, 'Footwear', 'https://example.com/running-shoes.jpg', 1, NOW(), NOW()),
('Cotton T-Shirt', 'Comfortable everyday cotton t-shirt', 19.99, 150, 'Clothing', 'https://example.com/tshirt.jpg', 1, NOW(), NOW());
------------------------------------------------------------------------------------------------------------------------------------------
Cart-Order-Microservice
Create TABLE cart_item(
	id 				INT AUTO_INCREMENT PRIMARY KEY,
	user_id			VARCHAR(100),
	product_id 		VARCHAR(100),
	quantity  		INT,
	price 			price DECIMAL(10, 2),
	created_at    	DATETIME,
	updated_at    	DATETIME
);
CREATE TABLE user_role (
    role_id INT AUTO_INCREMENT PRIMARY KEY,
    role    VARCHAR(50) NOT NULL
);
CREATE TABLE orders(
	id 				INT AUTO_INCREMENT PRIMARY KEY,
	user_id			VARCHAR(100),
	status			VARCHAR(100),
	total_amount	 DECIMAL(10, 2),
	created_at    	DATETIME,
	updated_at    	DATETIME
);
CREATE TABLE order_item (
    id           INT AUTO_INCREMENT PRIMARY KEY,
	product_id 		VARCHAR(100),
    quantity     INT,
    price        DECIMAL(10, 2),
	order_id     INT NOT NULL,
    CONSTRAINT fk_orderitem_order FOREIGN KEY (order_id) REFERENCES orders(id)
);