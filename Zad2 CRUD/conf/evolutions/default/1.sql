# --- !Ups

CREATE TABLE "category" (
 "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
 "name" VARCHAR NOT NULL
);

CREATE TABLE "product" (
 "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
 "name" VARCHAR NOT NULL,
 "description" TEXT NOT NULL,
 "category" INT NOT NULL,
 "price" DECIMAL(10, 2) NOT NULL,
 "quantity" INTEGER NOT NULL,
 "rating" INTEGER NOT NULL,
 "photo" INTEGER NOT NULL,
 FOREIGN KEY(photo) REFERENCES photo(id),
 FOREIGN KEY(rating) REFERENCES rating(id),
 FOREIGN KEY(category) references category(id)
);

CREATE TABLE "customer" (
 "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
 "forename" VARCHAR NOT NULL,
 "name" VARCHAR NOT NULL,
 "email" VARCHAR NOT NULL
)

CREATE TABLE "comment" {
 "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
 "content" TEXT NOT NULL,
 "product_id" INTEGER NOT NULL,
 "rating" INTEGER NOT NULL,
 FOREIGN KEY(rating) REFERENCES rating(id),
 FOREIGN KEY(product_id) REFERENCES product(id)
}

CREATE TABLE "order" {
 "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
 "customer" INTEGER NOT NULL,
 "product" INTEGER NOT NULL,
 "shipping" INTEGER NOT NULL,
 "quantity" INTEGER NOT NULL,
 FOREIGN KEY(customer) REFERENCES customer(id),
 FOREIGN KEY(product) REFERENCES product(id),
 FOREIGN KEY(shipping) REFERENCES shipping(id)
}

CREATE TABLE "orderItem" {
 "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
 "order_id" INTEGER NOT NULL,
 "payment" INTEGER NOT NULL,
 "dispute" VARCHAR NOT NULL,
 "status" VARCHAR NOT NULL,
 FOREIGN KEY(order_id) REFERENCES order(id),
 FOREIGN KEY(payment) REFERENCES payment(id)
}

CREATE TABLE "payment" {
 "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
 "total_price" DECIMAL(10,2) NOT NULL,
 "date" DATE NOT NULL,
 "is_done" INTEGER NOT NULL,
 CHECK ("is_done" == 0 OR "is_done" == 1)
}

CREATE TABLE "shipping" {
 "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
 "street" VARCHAR NOT NULL,
 "house_number" VARCHAR NOT NULL,
 "city" VARCHAR NOT NULL,
 "zip_code" VARCHAR NOT NULL
}

CREATE TABLE "rating" {
 "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
 "customer_id" INTEGER NOT NULL,
 "value" INT NOT NULL,
 FOREIGN KEY(customer_id) REFERENCES customer(id)
}

CREATE TABLE "photo" {
 "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
 "photo" BLOB NOT NULL,
}

# --- !Downs

DROP TABLE "category"
DROP TABLE "product"
DROP TABLE "photo"
DROP TABLE "rating"
DROP TABLE "shipping"
DROP TABLE "comment"
DROP TABLE "customer"
DROP TABLE "order"
DROP TABLE "orderItem"
DROP TABLE "payment"
