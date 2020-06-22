# --- !Ups

INSERT INTO "category"("name") VALUES("Sport");
INSERT INTO "category"("name") VALUES("Turystyka");
INSERT INTO "category"("name") VALUES("Siłownia");
INSERT INTO "category"("name") VALUES("Militaria");
INSERT INTO "category"("name") VALUES("Wędkarstwo");
INSERT INTO "category"("name") VALUES("Rowery");
INSERT INTO "photo"("photo", "link") VALUES("Rower", "https://tinyurl.com/y8r2bm7j");

# --- !Downs

DELETE FROM "category" WHERE name="Sport"";
DELETE FROM "category" WHERE name="Turystyka"";
DELETE FROM "category" WHERE name="Siłownia"";
DELETE FROM "category" WHERE name="Militaria"";
DELETE FROM "category" WHERE name="Wędkarstwo"";
DELETE FROM "category" WHERE name="Rowery"";
DELETE FROM "photo" WHERE photo="Rower"";