package com.submeet.scheduler.avvisi.controller;

import com.submeet.client.entity.EntityPaper;
import com.submeet.client.entity.EntityUser;
import com.submeet.client.entity.EntityConference;
import com.submeet.dbmsboundary.DBMSBoundary;
import com.submeet.scheduler.avvisi.utility.EmailSender;

import java.util.ArrayList;
import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.stream.Collectors;


public class NotifyControl {
    // CHECK: correct
    public void checkRevisionDeadline() {
        System.out.println("Revision deadline reached");
        // Ottengo la mappa conferenceId -> reviewDeadline
        Map<Integer, Date> submissionDeadlines = DBMSBoundary.getReviewDeadlines();

        // Scorro ogni conferenza con la sua scadenza
        if (submissionDeadlines != null || !submissionDeadlines.isEmpty())
        {
            for (Map.Entry<Integer, Date> entry : submissionDeadlines.entrySet()) {
                int conferenceId   = entry.getKey();
                Date expirationDate = entry.getValue();

                // Recupero la lista di reviewer che non hanno completato la review entro expirationDate
                ArrayList<Integer> reviewers = DBMSBoundary.getReviewerWithPaperNotReviewed(conferenceId);

                // Se ci sono reviewer da sollecitare, scorro la lista
                if (reviewers != null && !reviewers.isEmpty()) {
                    for (Integer reviewerId : reviewers) {
                        String conferenceTitle = DBMSBoundary.getConference(conferenceId).getTitle();
                        String email = DBMSBoundary.getEmail(reviewerId);

                        EmailSender.sendEmail(email,
                                "AVViSO scadenza revisione",
                                "Salve, questo è un avviso riguardo la scadenza della revisione per la conferenza " + conferenceTitle + " prevista il " + expirationDate + ".");
                    }
                }
            }
        }
    }


    // CHECK: correct
    public void checkReviewerInviteDeadline() {
        System.out.println("Reviewer invitation deadline check started");

        // Ottengo la mappa conferenceId → invitationDeadline
        Map<Integer, Date> invitationDeadlines = DBMSBoundary.getInvitationDeadlines();

        if (invitationDeadlines != null && !invitationDeadlines.isEmpty()) {
            for (Map.Entry<Integer, Date> entry : invitationDeadlines.entrySet()) {
                int conferenceId = entry.getKey();
                Date expirationDate = entry.getValue();

                Integer chairId = DBMSBoundary.getConferenceChair(conferenceId);

                if (chairId != null) {
                    String email = DBMSBoundary.getEmail(chairId);
                    String conferenceTitle = DBMSBoundary.getConference(conferenceId).getTitle();

                    EmailSender.sendEmail(
                            email,
                            "AVVISO scadenza invito revisori",
                            "Salve, questo è un avviso riguardo la scadenza per l’invito ai revisori della conferenza \"" +
                                    conferenceTitle + "\", prevista per il " + expirationDate + "."
                    );
                }
            }
        }

        System.out.println("Reviewer invitation deadline check ended");
    }


    // CHECK: correct
    public void checkPaperSubmissionDeadline() {
        System.out.println("Paper submission deadline check started");

        // Ottengo la mappa conferenceId → submissionDeadline
        Map<Integer, Date> submissionDeadlines = DBMSBoundary.getSubmissionDeadlines();

        if (submissionDeadlines != null && !submissionDeadlines.isEmpty()) {
            for (Map.Entry<Integer, Date> entry : submissionDeadlines.entrySet()) {
                int conferenceId = entry.getKey();
                Date deadline = entry.getValue();

                // Ottengo gli autori che non hanno sottomesso paper
                ArrayList<Integer> authors = DBMSBoundary.getAuthorsWithNoSubmissions(conferenceId);

                if (authors != null && !authors.isEmpty()) {
                    String conferenceTitle = DBMSBoundary.getConference(conferenceId).getTitle();

                    for (Integer authorId : authors) {
                        String email = DBMSBoundary.getEmail(authorId);

                        EmailSender.sendEmail(
                                email,
                                "AVVISO scadenza sottomissione paper",
                                "Gentile autore, la scadenza per la sottomissione dei paper alla conferenza \"" +
                                        conferenceTitle + "\" scadrà in data " + deadline +
                                        ". Non risulta alcuna sottomissione associata al suo account."
                        );
                    }
                }
            }
        }

        System.out.println("Paper submission deadline check ended");
    }


    // CHECK: correct
    public void checkFinalVersionDeadline() {
        System.out.println("Final version deadline check started");

        // Ottengo la mappa conferenceId → finalVersionDeadline
        Map<Integer, Date> deadlines = DBMSBoundary.getFinalVersionDeadlines();

        if (deadlines != null && !deadlines.isEmpty()) {
            for (Map.Entry<Integer, Date> entry : deadlines.entrySet()) {
                int conferenceId = entry.getKey();
                Date deadline = entry.getValue();

                // Ottengo gli autori che hanno almeno un paper accettato (reviewed) senza versione finale
                ArrayList<Integer> authors = DBMSBoundary.getAuthorsWithAcceptedPapers(conferenceId);

                if (authors != null && !authors.isEmpty()) {
                    String conferenceTitle = DBMSBoundary.getConference(conferenceId).getTitle();

                    for (Integer authorId : authors) {
                        String email = DBMSBoundary.getEmail(authorId);

                        EmailSender.sendEmail(
                                email,
                                "Promemoria: invio versione finale per la conferenza",
                                "Gentile autore, le ricordiamo che la scadenza per inviare la versione finale del paper accettato " +
                                        "alla conferenza \"" + conferenceTitle + "\" è il " + deadline + ". " +
                                        "La invitiamo a completare l’upload entro tale data."
                        );
                    }
                }
            }
        }

        System.out.println("Final version deadline check ended");
    }


    public void checkPaperAssignmentDeadline() {
        System.out.println("Paper assignation deadline reached");

        Map<Integer, Date> deadlines = DBMSBoundary.paperAssignmentDeadlines();

        if (deadlines != null && !deadlines.isEmpty()) {
            Date currentDate = new Date(System.currentTimeMillis());

            for (Map.Entry<Integer, Date> entry : deadlines.entrySet()) {
                int conferenceId = entry.getKey();
                Date expirationDate = entry.getValue();

                // Check if deadline has passed
                if (!currentDate.before(expirationDate)) {
                    List<EntityPaper> unassignedPapers = DBMSBoundary.getUnassignedPaper(conferenceId);

                    if (unassignedPapers != null && !unassignedPapers.isEmpty()) {
                        // Automatic assignment using keyword method
                        assignPapersAutomatically(conferenceId, unassignedPapers);

                        // Send notification to chair about automatic assignment
                        Integer chairId = DBMSBoundary.getConferenceChair(conferenceId);
                        if (chairId != null) {
                            String email = DBMSBoundary.getEmail(chairId);
                            String conferenceTitle = DBMSBoundary.getConference(conferenceId).getTitle();
                            System.out.println("Paper assignation deadline reached, sending notification to chair");

                            /*
                            EmailSender.sendEmail(
                                    email,
                                    "AVVISO assegnazione automatica paper",
                                    "Salve, la scadenza per l'assegnazione dei paper della conferenza \"" +
                                            conferenceTitle + "\" è stata superata il " + expirationDate + 
                                            ". I paper sono stati assegnati automaticamente utilizzando il metodo di assegnazione per parola chiave."
                            );
                             */
                        }
                    }
                } else {
                    // Deadline not yet passed, send reminder
                    List<EntityPaper> unassignedPapers = DBMSBoundary.getUnassignedPaper(conferenceId);

                    if (unassignedPapers != null && !unassignedPapers.isEmpty()) {
                        Integer chairId = DBMSBoundary.getConferenceChair(conferenceId);

                        if (chairId != null) {
                            String email = DBMSBoundary.getEmail(chairId);
                            String conferenceTitle = DBMSBoundary.getConference(conferenceId).getTitle();
                            System.out.println("Paper assignation deadline not yet reached, sending reminder to chair");

                            /*
                            EmailSender.sendEmail(
                                    email,
                                    "AVVISO scadenza assegnazione paper",
                                    "Salve, questo è un avviso riguardo la scadenza per l'assegnazione dei paper della conferenza \"" +
                                            conferenceTitle + "\", prevista per il " + expirationDate + "."
                            );
                             */
                        }
                    }
                }
            }
        }
    }

    /**
     * Automatically assigns papers using keyword assignment method
     * @param conferenceId The conference ID
     * @param unassignedPapers List of unassigned papers
     */
    private void assignPapersAutomatically(int conferenceId, List<EntityPaper> unassignedPapers) {
        System.out.println("Automatic paper assignment started");
        EntityConference conference = DBMSBoundary.getConference(conferenceId);
        ArrayList<EntityUser> reviewers = DBMSBoundary.getConferencePCMembers(conferenceId);

        if (reviewers == null || reviewers.isEmpty()) {
            return;
        }

        int maxReviewersPerPaper = Math.min(conference.getReviewerForPaper(), reviewers.size());

        for (EntityPaper paper : unassignedPapers) {
            int paperId = paper.getPaperId();
            List<String> paperSpecs = DBMSBoundary.getPaperSpecializations(paperId);

            Map<Integer, Integer> reviewerScores = new HashMap<>();

            for (EntityUser reviewer : reviewers) {
                int reviewerId = reviewer.getUserId();

                if (DBMSBoundary.getReviewerConflict(reviewerId, paperId)) {
                    continue; // Skip reviewer if conflict
                }

                int score = 0;

                // KEYWORD ASSIGNMENT LOGIC
                String paperTitle = paper.getTitle().toLowerCase();
                for (String s : paperSpecs) {
                    if (paperTitle.contains(s)) {
                        score++;
                    }
                }

                // Subtract the number of reviews from the score to balance workload
                score -= DBMSBoundary.getReviewListCount(reviewerId, conferenceId);
                System.out.println("Score for paper " + paperId + " by reviewer " + reviewerId + ": " + score);

                // Add reviewer to scoring map
                reviewerScores.put(reviewerId, score);
            }

            // Sort by descending score and take top N reviewers
            LinkedList<Integer> topReviewers = reviewerScores.entrySet().stream()
                    .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                    .limit(maxReviewersPerPaper)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toCollection(LinkedList::new));

            if (!topReviewers.isEmpty()) {
                DBMSBoundary.insertPaperReviewers(paperId, topReviewers);
            }
            System.out.println("Paper " + paperId + "assigned");
        }
    }
}
