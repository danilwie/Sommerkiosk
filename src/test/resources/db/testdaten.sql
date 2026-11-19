-- Nur nach leerem Schema, generierte IDs beginnen bei 1.
INSERT INTO kind(name,restbudget_cent) VALUES ('Mika',1400),('Kim',1350),('Mika',1475),('Noa',1450),('Lou',1500);
INSERT INTO artikel(name,preis_cent,vorhanden) VALUES ('Saft',100,TRUE),('Apfel',50,TRUE),('Riegel',150,TRUE),('Bonbon',25,TRUE),('Kakao',80,FALSE);
INSERT INTO kauf(kind_id,artikel_id,gekauft_am,preis_cent) VALUES
(1,1,TIMESTAMP WITH TIME ZONE '2026-07-01 11:00:00+02',100),
(2,1,TIMESTAMP WITH TIME ZONE '2026-07-01 12:00:00+02',100),
(2,2,TIMESTAMP WITH TIME ZONE '2026-07-01 12:05:00+02',50),
(3,4,TIMESTAMP WITH TIME ZONE '2026-07-02 10:00:00+02',25),
(4,2,TIMESTAMP WITH TIME ZONE '2026-07-02 10:10:00+02',50);
