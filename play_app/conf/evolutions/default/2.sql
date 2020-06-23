# --- !Ups

INSERT INTO "category"("name") VALUES("Sport");
INSERT INTO "category"("name") VALUES("Turystyka");
INSERT INTO "category"("name") VALUES("Siłownia");
INSERT INTO "category"("name") VALUES("Militaria");
INSERT INTO "category"("name") VALUES("Wędkarstwo");
INSERT INTO "category"("name") VALUES("Rowery");
INSERT INTO "photo"("photo", "link") VALUES("Rower", "https://tinyurl.com/y8r2bm7j");
INSERT INTO "photo"("photo", "link") VALUES("Wędka", "https://tinyurl.com/y8zkp8tk");
INSERT INTO "photo"("photo", "link") VALUES("ASG", "https://tinyurl.com/ycj7kyzj");
INSERT INTO "photo"("photo", "link") VALUES("Sztanga", "https://tinyurl.com/y83gsgxp");
INSERT INTO "photo"("photo", "link") VALUES("Namiot", "https://tinyurl.com/y7opjeng");
INSERT INTO "photo"("photo", "link") VALUES("Piłka", "https://tinyurl.com/y9awsn8z");

# --- !Downs

DELETE FROM "category" WHERE name="Sport"";
DELETE FROM "category" WHERE name="Turystyka"";
DELETE FROM "category" WHERE name="Siłownia"";
DELETE FROM "category" WHERE name="Militaria"";
DELETE FROM "category" WHERE name="Wędkarstwo"";
DELETE FROM "category" WHERE name="Rowery"";
DELETE FROM "photo" WHERE photo="Rower"";
DELETE FROM "photo" WHERE photo="Wędka"";
DELETE FROM "photo" WHERE photo="ASG"";
DELETE FROM "photo" WHERE photo="Sztanga"";
DELETE FROM "photo" WHERE photo="Namiot"";
DELETE FROM "photo" WHERE photo="Piłka"";