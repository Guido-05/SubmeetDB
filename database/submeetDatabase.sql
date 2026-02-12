DROP DATABASE IF EXISTS submeetdb;

CREATE DATABASE IF NOT EXISTS submeetdb;
USE submeetdb;

-- Disabilito temporaneamente i controlli per evitare errori di ordine
SET foreign_key_checks = 0;

-- Entity: Users
CREATE TABLE EntityUser (
                            userId       INT PRIMARY KEY AUTO_INCREMENT,
                            name         VARCHAR(50) NOT NULL,
                            surname      VARCHAR(50) NOT NULL,
                            email        VARCHAR(100) UNIQUE NOT NULL,
                            password     VARCHAR(255) NOT NULL,
                            nationality  VARCHAR(50) NOT NULL
);

-- Entity: Conferences
CREATE TABLE EntityConference (
                                  conferenceId            INT PRIMARY KEY AUTO_INCREMENT,
                                  title                   VARCHAR(255) NOT NULL,
                                  reviewerForPaper        INT NOT NULL,
                                  scheduleDate            DATE NOT NULL,
                                  submissionDeadline      DATE NOT NULL,
                                  reviewDeadline          DATE NOT NULL,
                                  finalVersionDeadline    DATE NOT NULL,
                                  invitationDeadline      DATE NOT NULL,
                                  paperAssignmentDeadline DATE NOT NULL,
                                  templateFile            LONGBLOB
);

-- Entity: Papers
CREATE TABLE EntityPaper (
    paperId         INT PRIMARY KEY AUTO_INCREMENT,
    conferenceId    INT NOT NULL,
    authorId        INT NOT NULL,
    title           VARCHAR(255) NOT NULL,
    description     TEXT NOT NULL,
    submitionDate   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    isFinalVersion  TINYINT(1) DEFAULT 0,
    accepted        INT DEFAULT 0 CHECK (accepted BETWEEN -1 AND 1),
    fileData        LONGBLOB NOT NULL,
    FOREIGN KEY (conferenceId) REFERENCES EntityConference(conferenceId) ON DELETE CASCADE,
    FOREIGN KEY (authorId) REFERENCES EntityUser(userId) ON DELETE CASCADE
);

-- Entity: Reviews
CREATE TABLE EntityReview (
                              revisionId       INT PRIMARY KEY AUTO_INCREMENT,
                              reviewerId       INT NOT NULL,
                              paperId          INT NOT NULL,
                              isSubReview      TINYINT(1) DEFAULT 0,
                              state            VARCHAR(50) NOT NULL CHECK (state IN ('new', 'done', 'on_going', 'delegated')),
                              reviewComment    TEXT NOT NULL,
                              rating           INT CHECK (rating BETWEEN 0 AND 5),
                              privateComment   TEXT,
                              delegator  INT NULL,
                              FOREIGN KEY (reviewerId) REFERENCES EntityUser(userId) ON DELETE CASCADE,
                              FOREIGN KEY (paperId) REFERENCES EntityPaper(paperId) ON DELETE CASCADE
);

-- Entity: Notifications
CREATE TABLE EntityNotification (
                                    notifId       INT PRIMARY KEY AUTO_INCREMENT,
                                    userId        INT NOT NULL,
                                    type          VARCHAR(50) NOT NULL CHECK (type IN ('invitation PC', 'invitation EDITOR', 'invitation SUBREVIEWER', 'reminder', 'system')),
                                    accepted      INT DEFAULT 0 CHECK (accepted BETWEEN -1 AND 1),  -- 1:accepted 0:pending -1:rejected
                                    visualized    TINYINT(1) DEFAULT 0,
                                    conferenceId  INT,
                                    paperId       INT,
                                    sender        INT NULL,
                                    text          TEXT NOT NULL,
                                    date         DATETIME DEFAULT CURRENT_TIMESTAMP,
                                    FOREIGN KEY (userId) REFERENCES EntityUser(userId) ON DELETE CASCADE,
                                    FOREIGN KEY (conferenceId) REFERENCES EntityConference(conferenceId),
                                    FOREIGN KEY (paperId) REFERENCES EntityPaper(paperId)
);

-- Tabella delle specializzazioni (entità centrale)
CREATE TABLE EntitySpecialization (
                                      specializationId INT PRIMARY KEY AUTO_INCREMENT,
                                      name             VARCHAR(100) NOT NULL
);

-- Aree di interesse scientifico della conferenza
CREATE TABLE ConferenceSpecialization (
                                          conferenceId       INT NOT NULL,
                                          specializationId   INT NOT NULL,
                                          PRIMARY KEY (conferenceId, specializationId),
                                          FOREIGN KEY (conferenceId) REFERENCES EntityConference(conferenceId) ON DELETE CASCADE,
                                          FOREIGN KEY (specializationId) REFERENCES EntitySpecialization(specializationId) ON DELETE CASCADE
);

-- Aree di interesse del paper
CREATE TABLE PaperSpecialization (
                                     paperId            INT NOT NULL,
                                     specializationId   INT NOT NULL,
                                     PRIMARY KEY (paperId, specializationId),
                                     FOREIGN KEY (paperId) REFERENCES EntityPaper(paperId) ON DELETE CASCADE,
                                     FOREIGN KEY (specializationId) REFERENCES EntitySpecialization(specializationId) ON DELETE CASCADE
);

-- Aree di competenza degli utenti
CREATE TABLE UserSpecialization (
                                    userId             INT NOT NULL,
                                    specializationId   INT NOT NULL,
                                    PRIMARY KEY (userId, specializationId),
                                    FOREIGN KEY (userId) REFERENCES EntityUser(userId) ON DELETE CASCADE,
                                    FOREIGN KEY (specializationId) REFERENCES EntitySpecialization(specializationId) ON DELETE CASCADE
);


CREATE TABLE EntityLanguage (
                              languageId   INT PRIMARY KEY AUTO_INCREMENT,
                              language     VARCHAR(50) NOT NULL
);

-- Lingue supportate dalla conferenza
CREATE TABLE ConferenceLanguage (
                                    conferenceId INT NOT NULL,
                                    languageId   INT NOT NULL,
                                    PRIMARY KEY (conferenceId, languageId),
                                    FOREIGN KEY (languageId) REFERENCES EntityLanguage(languageId) ON DELETE CASCADE,
                                    FOREIGN KEY (conferenceId) REFERENCES EntityConference(conferenceId) ON DELETE CASCADE
);

-- User roles in conferences
CREATE TABLE UserConferenceRole (
                                    userId         INT NOT NULL,
                                    conferenceId   INT NOT NULL,
                                    role           VARCHAR(50) NOT NULL CHECK (role IN ('Author', 'Reviewer', 'Chair', 'Editor', 'SubReviewer')),
                                    PRIMARY KEY (userId, conferenceId, role),
                                    FOREIGN KEY (userId) REFERENCES EntityUser(userId) ON DELETE CASCADE,
                                    FOREIGN KEY (conferenceId) REFERENCES EntityConference(conferenceId) ON DELETE CASCADE
);

CREATE TABLE EntityPreference (
                                    preferenceId INT AUTO_INCREMENT,
                                    userId INT NOT NULL,
                                    paperId INT NOT NULL,
                                    isConflict TINYINT(1) DEFAULT 0,
                                    preference INT NOT NULL DEFAULT 0 CHECK (preference BETWEEN 0 AND 5),
                                    PRIMARY KEY (preferenceId),
                                    FOREIGN KEY (userId) REFERENCES EntityUser(userId) ON DELETE CASCADE,
                                    FOREIGN KEY (paperId) REFERENCES EntityPaper(paperId) ON DELETE CASCADE
);

-- Riattivo i controlli sulle chiavi esterne
SET foreign_key_checks = 1;
