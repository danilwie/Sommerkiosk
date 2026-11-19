-- Fiktiver Bestand der heutigen Vorführsitzung, nie auf der Nutzdatenbank ausführen.
INSERT INTO kind(name,restbudget_cent) VALUES ('Alex',1400),('Alex',1500),('Jona',1400),('Mira',1375),('Sam',1450);
INSERT INTO artikel(name,preis_cent,vorhanden) VALUES ('Saft',100,TRUE),('Apfel',50,TRUE),('Keks',75,TRUE),('Wasser',25,TRUE),('Eis',125,FALSE);
INSERT INTO kauf(kind_id,artikel_id,gekauft_am,preis_cent) VALUES
(1,1,TIMESTAMP WITH TIME ZONE '2026-07-03 09:00:00+02',100),
(3,1,TIMESTAMP WITH TIME ZONE '2026-07-01 10:00:00+02',100),
(4,3,TIMESTAMP WITH TIME ZONE '2026-07-01 12:00:00+02',75),
(4,2,TIMESTAMP WITH TIME ZONE '2026-07-02 11:00:00+02',50),
(5,2,TIMESTAMP WITH TIME ZONE '2026-07-02 11:00:00+02',50);
