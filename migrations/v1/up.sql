CREATE TABLE "Users" (
    "id" SERIAL PRIMARY KEY NOT NULL,
    "userID" text UNIQUE NOT NULL,
    "bannedFromUse" bool DEFAULT false NOT NULL,
    "banReason" text,
    "trelloToken" text,
    "trelloID" text,
    "currentBoard" text,
    "discordToken" text,
    "discordRefresh" text,
    "locale" text,
    "prefixes" text[] DEFAULT '{}' NOT NULL
)
