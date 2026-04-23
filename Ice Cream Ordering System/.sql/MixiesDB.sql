CREATE DATABASE IF NOT EXISTS MixiesDB;
USE MixiesDB;

CREATE TABLE Employee (
    employeeID INT NOT NULL AUTO_INCREMENT,
    employeeName VARCHAR(100) NOT NULL,
    employeeRole VARCHAR(20) NOT NULL,
    PRIMARY KEY (employeeID)
);

CREATE TABLE IceCreamFlavor (
    flavorID INT NOT NULL AUTO_INCREMENT,
    flavorName VARCHAR(100) NOT NULL,
    seasonality VARCHAR(50),
    stockLevel INT NOT NULL,    remakeThreshold INT NOT NULL,
    allergens VARCHAR(255),
    availabilityStatus VARCHAR(30) NOT NULL,
    PRIMARY KEY (flavorID),
    UNIQUE (flavorName)
);

CREATE TABLE Topping (
    toppingID INT NOT NULL AUTO_INCREMENT,
    toppingName VARCHAR(100) NOT NULL,
    PRIMARY KEY (toppingID),
    UNIQUE (toppingName)
);

CREATE TABLE Orders (
    orderID INT NOT NULL AUTO_INCREMENT,
    employeeID INT NOT NULL,
    orderDate DATETIME NOT NULL,
    tip DECIMAL(6,2) DEFAULT 0.00,
    total DECIMAL(8,2) NOT NULL,
    orderStatus VARCHAR(20) NOT NULL DEFAULT 'Open',
    PRIMARY KEY (orderID),
    CONSTRAINT fk_orders_employee
        FOREIGN KEY (employeeID) REFERENCES Employee(employeeID)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);

CREATE TABLE OrderItem (
    orderItemID INT NOT NULL AUTO_INCREMENT,
    orderID INT NOT NULL,
    flavorID INT NOT NULL,
    quantity INT NOT NULL,
    itemCost DECIMAL(8,2) NOT NULL,
    refundStatus ENUM('Not Refunded', 'Refunded') NOT NULL DEFAULT 'Not Refunded',
    PRIMARY KEY (orderItemID),
    CONSTRAINT fk_orderitem_order
        FOREIGN KEY (orderID) REFERENCES Orders(orderID)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_orderitem_flavor
        FOREIGN KEY (flavorID) REFERENCES IceCreamFlavor(flavorID)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);

CREATE TABLE OrderItemTopping (
    orderItemID INT NOT NULL,
    toppingID INT NOT NULL,
    toppingQuantity INT NOT NULL DEFAULT 1,
    PRIMARY KEY (orderItemID, toppingID),
    CONSTRAINT fk_orderitemtopping_orderitem
        FOREIGN KEY (orderItemID) REFERENCES OrderItem(orderItemID)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_orderitemtopping_topping
        FOREIGN KEY (toppingID) REFERENCES Topping(toppingID)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);

SHOW TABLES;

INSERT INTO employee (employeeID, employeeName, employeeRole)
VALUES (1, 'Ava', 'Manager');

INSERT INTO icecreamflavor (flavorName, seasonality, stockLevel, remakeThreshold, allergens, availabilityStatus)
VALUES 
('Vanilla', 'All Year', 10, 3, 'Milk', 'Available'),
('Chocolate', 'All Year', 8, 3, 'Milk', 'Available');

INSERT INTO topping (toppingName)
VALUES 
('Sprinkles'),
('Oreos'),
('Caramel');
