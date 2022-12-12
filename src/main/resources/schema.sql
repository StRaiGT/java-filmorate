DROP TABLE IF EXISTS MPA CASCADE;
DROP TABLE IF EXISTS FILMS CASCADE;
DROP TABLE IF EXISTS GENRES CASCADE;
DROP TABLE IF EXISTS FILMS_GENRES CASCADE;
DROP TABLE IF EXISTS USERS CASCADE;
DROP TABLE IF EXISTS LIKES CASCADE;
DROP TABLE IF EXISTS FRIENDSHIP CASCADE;

CREATE TABLE IF NOT EXISTS MPA (
    MPA_ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    NAME VARCHAR(50) NOT NULL UNIQUE,
    DESCRIPTION VARCHAR(200)
);

CREATE TABLE IF NOT EXISTS FILMS (
  FILM_ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  NAME VARCHAR(50) NOT NULL UNIQUE,
  DESCRIPTION VARCHAR(200),
  RELEASE_DATE DATE,
  DURATION INTEGER,
  MPA_ID INTEGER REFERENCES MPA (MPA_ID)
);

CREATE TABLE IF NOT EXISTS GENRES (
    GENRE_ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    NAME VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS FILMS_GENRES (
    FILM_ID INTEGER REFERENCES FILMS (FILM_ID) ON DELETE CASCADE,
    GENRE_ID INTEGER REFERENCES GENRES (GENRE_ID),
    PRIMARY KEY (FILM_ID,GENRE_ID)
);

CREATE TABLE IF NOT EXISTS USERS (
    USER_ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    EMAIL VARCHAR(50) NOT NULL,
    LOGIN VARCHAR(50) NOT NULL UNIQUE,
    NAME VARCHAR(50),
    BIRTHDAY DATE
);

CREATE TABLE IF NOT EXISTS LIKES (
    FILM_ID INTEGER REFERENCES FILMS (FILM_ID) ON DELETE CASCADE,
    USER_ID INTEGER REFERENCES USERS (USER_ID) ON DELETE CASCADE,
    PRIMARY KEY (FILM_ID, USER_ID)
);

CREATE TABLE IF NOT EXISTS FRIENDSHIP (
    USER_ID INTEGER REFERENCES USERS (USER_ID) ON DELETE CASCADE,
    FRIEND_ID INTEGER REFERENCES USERS (USER_ID) ON DELETE CASCADE,
    PRIMARY KEY (USER_ID, FRIEND_ID)
);