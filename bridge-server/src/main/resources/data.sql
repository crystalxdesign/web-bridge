DROP TABLE IF EXISTS seats;

CREATE TABLE seats (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  playername VARCHAR(50) NOT NULL,
  playerid VARCHAR(36) NOT NULL,
  tableid VARCHAR(36) NOT NULL,
  position INT
);

INSERT INTO seats (playername, playerid, tableid, position) VALUES ('Rupert','d82e150f-1529-4182-a9b0-befb0c55784a','59c73dfc-d715-4566-a092-047322ea0c6d',0);
INSERT INTO seats (playername, playerid, tableid, position) VALUES ('Michael','26bbbb06-fb3a-45a6-bd84-1343fc72c77a','59c73dfc-d715-4566-a092-047322ea0c6d',1);

INSERT INTO seats (playername, playerid, tableid, position) VALUES ('Graham','2d02fbf8-c7c2-4d3d-a358-69f975c8e06a','77357cf9-df41-4e24-b765-ed8b69c3a906',0);
INSERT INTO seats (playername, playerid, tableid, position) VALUES ('Benjamin','c54169ca-9de9-4441-b01b-9ff58644b475','77357cf9-df41-4e24-b765-ed8b69c3a906',2);
INSERT INTO seats (playername, playerid, tableid, position) VALUES ('Charles','9b953196-105f-4404-8a8c-5ba01dd4d7f7','77357cf9-df41-4e24-b765-ed8b69c3a906',3);

INSERT INTO seats (playername, playerid, tableid, position) VALUES ('Roger','feb87509-f819-4ca9-ab2e-d5fa8706ea6f','c6c5238a-1032-496c-8c03-7300db85adfe',0);
INSERT INTO seats (playername, playerid, tableid, position) VALUES ('Eustace','b4bf1bb8-b48c-48e2-bbaa-5d2147c140ff','c6c5238a-1032-496c-8c03-7300db85adfe',2);
