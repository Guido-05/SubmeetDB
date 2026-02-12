package com.submeet.client.utility;

import com.submeet.client.common.ErrorPopupView;
import com.submeet.client.entity.EntityConference;

import java.util.Date;

public class DeadlineValidator {

    /**
     * Checks if the current date is before the specified deadline
     * @param deadline The deadline to check against
     * @param operationName The name of the operation for error message
     * @return true if the deadline has not passed, false otherwise
     */
    public static boolean isBeforeDeadline(Date deadline, String operationName) {
        if (deadline == null) {
            return true; // If no deadline is set, allow the operation
        }

        Date currentDate = new Date();
        if (currentDate.after(deadline)) {
            new ErrorPopupView("Non è possibile eseguire l'operazione: " + operationName + 
                             "\nLa scadenza è stata superata.");
            return false;
        }
        return true;
    }

    /**
     * Validates if paper submission is allowed based on submission deadline
     * @param conference The conference entity
     * @return true if submission is allowed, false otherwise
     */
    public static boolean canSubmitPaper(EntityConference conference) {
        return isBeforeDeadline(conference.getSubmissionDeadline(), 
                              "sottomissione paper");
    }

    /**
     * Validates if review submission is allowed based on review deadline
     * @param conference The conference entity
     * @return true if review submission is allowed, false otherwise
     */
    public static boolean canSubmitReview(EntityConference conference) {
        return isBeforeDeadline(conference.getReviewDeadline(), 
                              "sottomissione revisioni");
    }

    /**
     * Validates if final version submission is allowed based on final version deadline
     * @param conference The conference entity
     * @return true if final version submission is allowed, false otherwise
     */
    public static boolean canSubmitFinalVersion(EntityConference conference) {
        return isBeforeDeadline(conference.getFinalVersionDeadline(), 
                              "sottomissione versioni finali");
    }

    /**
     * Validates if paper assignment is allowed based on paper assignment deadline
     * @param conference The conference entity
     * @return true if paper assignment is allowed, false otherwise
     */
    public static boolean canAssignPaper(EntityConference conference) {
        return isBeforeDeadline(conference.getPaperAssignmentDeadline(), 
                              "assegnazione paper ai revisori");
    }

    /**
     * Validates if conference is still active based on schedule date
     * @param conference The conference entity
     * @return true if conference is still active, false otherwise
     */
    public static boolean isConferenceActive(EntityConference conference) {
        return isBeforeDeadline(conference.getScheduleDate(), 
                              "selezione conferenza");
    }

    /**
     * Validates if invitation sending is allowed based on invitation deadline
     * @param conference The conference entity
     * @return true if invitation sending is allowed, false otherwise
     */
    public static boolean canSendInvitation(EntityConference conference) {
        return isBeforeDeadline(conference.getInvitationDeadline(), 
                              "invio inviti ai revisori ed editori");
    }
}
