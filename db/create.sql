/*
 * Schema used to set up Main Database
 */

DROP TABLE IF EXISTS Users;

CREATE TABLE Users(
  id VARCHAR(100) NOT NULL,
  username VARCHR(100),
  PRIMARY KEY(id)
);
