CREATE TABLE users (
    "id" SERIAL PRIMARY KEY NOT NULL,
    "userID" varchar(255) UNIQUE NOT NULL,
    "bannedFromUse" bool DEFAULT false NOT NULL,
    "banReason" varchar(255),
    "trelloToken" varchar(255),
    "trelloID" varchar(255),
    "currentBoard" varchar(255),
    "discordToken" varchar(255),
    "discordRefresh" varchar(255),
    "locale" varchar(255),
    "prefixes" varchar(255)[] DEFAULT '{}' NOT NULL
)
