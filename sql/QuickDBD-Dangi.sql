-- Exported from QuickDBD: https://www.quickdatabasediagrams.com/
-- Link to schema: https://app.quickdatabasediagrams.com/#/d/sNCyj6
-- NOTE! If you have used non-SQL datatypes in your design, you will have to change these here.


CREATE TABLE `User` (
    `idx` int AUTO_INCREMENT NOT NULL ,
    `socialId` varchar(90)  NOT NULL ,
    -- K: kakao, N: naver
    `type` char(1)  NOT NULL ,
    `nickname` varchar(30)  NOT NULL ,
    `point` int  NOT NULL ,
    `tier` varchar(15)  NOT NULL ,
    `tierLevel` int  NOT NULL ,
    `badgeIdx` int  NOT NULL ,
    `isDark` boolean  NOT NULL DEFAULT true,
    `onAlarm` boolean  NOT NULL DEFAULT true,
    -- A:active, D:deleted, R:reported
    `status` char(1)  NOT NULL DEFAULT 'a',
    PRIMARY KEY (
        `idx`
    )
);

CREATE TABLE `BetHistory` (
    `idx` int AUTO_INCREMENT NOT NULL ,
    `userIdx` int  NOT NULL ,
    `bit` boolean  NOT NULL DEFAULT true,
    `eth` boolean  NOT NULL DEFAULT true,
    `xrp` boolean  NOT NULL DEFAULT true,
    `today` date  NOT NULL DEFAULT current_timestamp,
    `avgRate` double  NULL ,
    PRIMARY KEY (
        `idx`
    )
);

CREATE TABLE `Post` (
    `idx` int AUTO_INCREMENT NOT NULL ,
    `boardIdx` int  NOT NULL ,
    `userIdx` int  NULL ,
    `guestName` varchar(30)  NULL ,
    `guestPwd` varchar(200)  NULL ,
    `content` text  NOT NULL ,
    `upCnt` int  NOT NULL DEFAULT 0,
    `downCnt` int  NOT NULL DEFAULT 0,
    `viewCnt` int  NOT NULL DEFAULT 0,
    `reportCnt` int  NOT NULL DEFAULT 0,
    `createdAt` timestamp  NOT NULL DEFAULT current_timestamp,
    `updatedAt` timestamp  NOT NULL DEFAULT current_timestamp,
    -- A:active, D:deleted, R:reported
    `status` char(1)  NOT NULL DEFAULT 'a',
    PRIMARY KEY (
        `idx`
    )
);

CREATE TABLE `Board` (
    `idx` int AUTO_INCREMENT NOT NULL ,
    `name` varchar(30)  NOT NULL ,
    PRIMARY KEY (
        `idx`
    )
);

CREATE TABLE `Image` (
    `idx` int AUTO_INCREMENT NOT NULL ,
    `postIdx` int  NOT NULL ,
    `directory` varchar(200)  NOT NULL ,
    `title` varchar(200)  NOT NULL ,
    PRIMARY KEY (
        `idx`
    )
);

CREATE TABLE `Comment` (
    `idx` int AUTO_INCREMENT NOT NULL ,
    `postIdx` int  NOT NULL ,
    `userIdx` int  NULL ,
    `nickname` varchar(15)  NULL ,
    `password` varchar(200)  NULL ,
    `context` varchar(500)  NOT NULL ,
    `group` int  NOT NULL ,
    `level` int  NOT NULL DEFAULT 0,
    `reportCnt` int  NOT NULL DEFAULT 0,
    `createdAt` timestamp  NOT NULL DEFAULT current_timestamp,
    `updatedAt` timestamp  NOT NULL DEFAULT current_timestamp,
    -- A:active, D:deleted, R:reported
    `status` char(1)  NOT NULL DEFAULT 'a',
    PRIMARY KEY (
        `idx`
    )
);

CREATE TABLE `Badge` (
    `idx` int AUTO_INCREMENT NOT NULL ,
    `directory` varchar(200)  NOT NULL ,
    `tier` varchar(15)  NOT NULL ,
    `tierLevel` int  NOT NULL ,
    PRIMARY KEY (
        `idx`
    )
);

CREATE TABLE `Banner` (
    `idx` int AUTO_INCREMENT NOT NULL ,
    `imageURL` text  NOT NULL ,
    `createdAt` timestamp  NOT NULL DEFAULT current_timestamp,
    -- A:active, D:deleted, H:hidden
    `status` char(1)  NOT NULL DEFAULT 'a',
    PRIMARY KEY (
        `idx`
    )
);

CREATE TABLE `UserEmoji` (
    `idx` int AUTO_INCREMENT NOT NULL ,
    `userIdx` int  NOT NULL ,
    `emojiSetIdx` int  NOT NULL ,
    PRIMARY KEY (
        `idx`
    )
);

CREATE TABLE `EmojiSet` (
    `idx` int AUTO_INCREMENT NOT NULL ,
    `name` varchar(30)  NOT NULL ,
    PRIMARY KEY (
        `idx`
    )
);

CREATE TABLE `Emoji` (
    `idx` int AUTO_INCREMENT NOT NULL ,
    `emojiSetIdx` int  NOT NULL ,
    `directory` varchar(200)  NOT NULL ,
    PRIMARY KEY (
        `idx`
    )
);

ALTER TABLE `User` ADD CONSTRAINT `fk_User_badgeIdx` FOREIGN KEY(`badgeIdx`)
REFERENCES `Badge` (`idx`);

ALTER TABLE `BetHistory` ADD CONSTRAINT `fk_BetHistory_userIdx` FOREIGN KEY(`userIdx`)
REFERENCES `User` (`idx`);

ALTER TABLE `Post` ADD CONSTRAINT `fk_Post_boardIdx` FOREIGN KEY(`boardIdx`)
REFERENCES `Board` (`idx`);

ALTER TABLE `Post` ADD CONSTRAINT `fk_Post_userIdx` FOREIGN KEY(`userIdx`)
REFERENCES `User` (`idx`);

ALTER TABLE `Image` ADD CONSTRAINT `fk_Image_postIdx` FOREIGN KEY(`postIdx`)
REFERENCES `Post` (`idx`);

ALTER TABLE `Comment` ADD CONSTRAINT `fk_Comment_postIdx` FOREIGN KEY(`postIdx`)
REFERENCES `Post` (`idx`);

ALTER TABLE `Comment` ADD CONSTRAINT `fk_Comment_userIdx` FOREIGN KEY(`userIdx`)
REFERENCES `User` (`idx`);

ALTER TABLE `UserEmoji` ADD CONSTRAINT `fk_UserEmoji_userIdx` FOREIGN KEY(`userIdx`)
REFERENCES `User` (`idx`);

ALTER TABLE `UserEmoji` ADD CONSTRAINT `fk_UserEmoji_emojiSetIdx` FOREIGN KEY(`emojiSetIdx`)
REFERENCES `EmojiSet` (`idx`);

ALTER TABLE `Emoji` ADD CONSTRAINT `fk_Emoji_emojiSetIdx` FOREIGN KEY(`emojiSetIdx`)
REFERENCES `EmojiSet` (`idx`);

