# --- !Ups

CREATE TABLE IF NOT EXISTS "category" (
 "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
 "name" VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS "payment" (
 "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
 "total_price" DECIMAL(10,2) NOT NULL,
 "date" DATE NOT NULL,
 "is_done" INTEGER NOT NULL,
 CHECK ("is_done" == 0 OR "is_done" == 1)
);


CREATE TABLE IF NOT EXISTS "customer" (
 "id" INTEGER NOT NULL PRIMARY KEY,
 "forename" VARCHAR NOT NULL,
 "name" VARCHAR NOT NULL,
 "email" VARCHAR NOT NULL,
 "role" VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS "password" (
 "loginInfoId" VARCHAR NOT NULL PRIMARY KEY,
 "hasher" VARCHAR NOT NULL,
 "hash" VARCHAR NOT NULL,
 "salt" VARCHAR
);

CREATE TABLE IF NOT EXISTS "login_info" (
 "id" VARCHAR NOT NULL PRIMARY KEY,
 "providerId" VARCHAR NOT NULL,
 "providerKey" VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS "user_login_info" (
 "userId" INTEGER NOT NULL,
 "loginInfoId" VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS "OAuth2Info" (
  "id" VARCHAR NOT NULL PRIMARY KEY,
  "accessToken" VARCHAR NOT NULL,
  "tokenType" VARCHAR,
  "expiresIn" INTEGER,
  "refreshToken" VARCHAR,
  "loginInfoId" VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS "shipping" (
 "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
 "street" VARCHAR NOT NULL,
 "house_number" VARCHAR NOT NULL,
 "city" VARCHAR NOT NULL,
 "zip_code" VARCHAR NOT NULL
);


CREATE TABLE IF NOT EXISTS "product" (
 "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
 "name" VARCHAR NOT NULL,
 "description" TEXT NOT NULL,
 "category" INT NOT NULL,
 "price" DECIMAL(10, 2) NOT NULL,
 "quantity" INTEGER NOT NULL,
 "photo" INTEGER NOT NULL,
 FOREIGN KEY("category") references category("id")
 FOREIGN KEY("photo") references photo("id")
);

CREATE TABLE IF NOT EXISTS "photo" (
 "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
 "photo" VARCHAR NOT NULL,
 "link" VARCHAR NOT NULL
);


CREATE TABLE IF NOT EXISTS "rating" (
 "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
 "customer_id" INTEGER NOT NULL,
 "value" INT NOT NULL,
 "product" INTEGER NOT NULL,
 FOREIGN KEY ("product") REFERENCES product(id),
 FOREIGN KEY("customer_id") REFERENCES customer(id)
);


CREATE TABLE IF NOT EXISTS "comment" (
 "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
 "content" TEXT NOT NULL,
 "product_id" INTEGER NOT NULL,
 FOREIGN KEY("product_id") REFERENCES product(id)
);

CREATE TABLE IF NOT EXISTS "order" (
 "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
 "customer" INTEGER NOT NULL,
 "product" INTEGER NOT NULL,
 "shipping" INTEGER NOT NULL,
 "quantity" INTEGER NOT NULL,
 FOREIGN KEY(customer) REFERENCES customer(id),
 FOREIGN KEY(product) REFERENCES product(id),
 FOREIGN KEY(shipping) REFERENCES shipping(id)
);

CREATE TABLE IF NOT EXISTS  "orderItem" (
 "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
 "order_id" INTEGER NOT NULL,
 "payment" INTEGER NOT NULL,
 "dispute" VARCHAR NOT NULL,
 "status" VARCHAR NOT NULL,
 FOREIGN KEY("order_id") REFERENCES [order](id),
 FOREIGN KEY("payment") REFERENCES payment(id)
);


# --- !Downs

DROP TABLE "category";
DROP TABLE "payment";
DROP TABLE "customer";
DROP TABLE "login_info";
DROP TABLE "user_login_info";
DROP TABLE "OAuth2Info";
DROP TABLE "password";
DROP TABLE "shipping";
DROP TABLE "product";
DROP TABLE "photo";
DROP TABLE "rating";
DROP TABLE "comment";
DROP TABLE "order";
DROP TABLE "orderItem";
