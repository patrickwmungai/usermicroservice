/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.finessence.user.model;

import java.util.List;
import org.springframework.stereotype.Service;

/**
 *
 * @author patrick
 */
@Service("responseCodes")
public class ResponseCodes {

    public ApiResponse SUCCESS = new ApiResponse("00", "Successful");
    public ApiResponse LOAN_REQUIRES_GURANTORS = new ApiResponse("01", "Kindly provide loan <COUNT> gurantors");
    public ApiResponse CREATION_FAILED = new ApiResponse("02", "Failed to complete the request");
    public ApiResponse EXCEPTION_OCCURRED = new ApiResponse("03", "Failed to complete the request due to Exception");
    public ApiResponse NO_RECORDS_FOUND = new ApiResponse("04", "No records Found");    
    public ApiResponse LOAN_AMOUNT_EXEEDS_FACTOR_LIMIT = new ApiResponse("05", "Applied amount <AMOUNT> should not exeed <FACTOR> times your savings balance <SAVINGS>. That is <FACTORSAVINGS>.");
    public ApiResponse LOAN_TYPE_NOT_FOUND = new ApiResponse("06", "Loan type not found");
    public ApiResponse MEMBER_SAVINGS_ACCOUNT_NOT_FOUND = new ApiResponse("07", "Member Savings account not found");
    public ApiResponse APPLICANT_NOT_ALLOWED_TO_GURANTEE_HIS_LOAN = new ApiResponse("08", "One of your gurantors is self.You are not allowed to gurantee your self.");
    public ApiResponse LOAN_AMOUNT_EXCEEDS_MAX_AMOUNT = new ApiResponse("09", "Applied Amount <AMOUNT> exeeds limit amount <LIMIT>.");
    public ApiResponse LOAN_PERIOD_EXCEEDS_MAX_REPAY_PERIOD = new ApiResponse("10", "Repay period <PERIOD> exceeds max repayemnt period <LIMIT>.");
    public ApiResponse DUPLICATE_LOAN = new ApiResponse("11", "You already have an existing loan pending approval.");
    public ApiResponse LOAN_ALREADY_UNDEGONE_APPROVAL_PROCESS = new ApiResponse("12", "Loan Already undergone approval process.");
    public ApiResponse DISUBURSEMENT_ALREADY_UNDEGONE_APPROVAL_PROCESS = new ApiResponse("13", "Disbursement Already undergone approval process.");
    public ApiResponse LOAN_DETAILS_NOT_FOUND = new ApiResponse("14", "Loan Details for the loan repayment not found..");
    public ApiResponse CURRENT_ACCOUNT_NOT_FOUND = new ApiResponse("15", "Current account not found..");
    public ApiResponse INSUFFICIENT_BALANCE_FOR_LOAN_REPAYMENT = new ApiResponse("16", "Amount <RAMOUNT> is insufficient to repay your installement of <IAMOUNT>.Top up your current account then try again.");
    public ApiResponse INVALID_CREDENTIALS = new ApiResponse("17", "Invalid Credentials");
    public ApiResponse TOKEN_NOT_GENERATED = new ApiResponse("18", "No token generated");

    public ApiResponse getLOAN_AMOUNT_EXCEEDS_MAX_AMOUNT() {
        return LOAN_AMOUNT_EXCEEDS_MAX_AMOUNT;
    }

    public void setLOAN_AMOUNT_EXCEEDS_MAX_AMOUNT(ApiResponse LOAN_AMOUNT_EXCEEDS_MAX_AMOUNT) {
        this.LOAN_AMOUNT_EXCEEDS_MAX_AMOUNT = LOAN_AMOUNT_EXCEEDS_MAX_AMOUNT;
    }

    public ApiResponse getLOAN_PERIOD_EXCEEDS_MAX_REPAY_PERIOD() {
        return LOAN_PERIOD_EXCEEDS_MAX_REPAY_PERIOD;
    }

    public void setLOAN_PERIOD_EXCEEDS_MAX_REPAY_PERIOD(ApiResponse LOAN_PERIOD_EXCEEDS_MAX_REPAY_PERIOD) {
        this.LOAN_PERIOD_EXCEEDS_MAX_REPAY_PERIOD = LOAN_PERIOD_EXCEEDS_MAX_REPAY_PERIOD;
    }

    public ApiResponse getDUPLICATE_LOAN() {
        return DUPLICATE_LOAN;
    }

    public void setDUPLICATE_LOAN(ApiResponse DUPLICATE_LOAN) {
        this.DUPLICATE_LOAN = DUPLICATE_LOAN;
    }

    
    public ApiResponse getAPPLICANT_NOT_ALLOWED_TO_GURANTEE_HIS_LOAN() {
        return APPLICANT_NOT_ALLOWED_TO_GURANTEE_HIS_LOAN;
    }

    public void setAPPLICANT_NOT_ALLOWED_TO_GURANTEE_HIS_LOAN(ApiResponse APPLICANT_NOT_ALLOWED_TO_GURANTEE_HIS_LOAN) {
        this.APPLICANT_NOT_ALLOWED_TO_GURANTEE_HIS_LOAN = APPLICANT_NOT_ALLOWED_TO_GURANTEE_HIS_LOAN;
    }

    public ApiResponse getMEMBER_SAVINGS_ACCOUNT_NOT_FOUND() {
        return MEMBER_SAVINGS_ACCOUNT_NOT_FOUND;
    }

    public void setMEMBER_SAVINGS_ACCOUNT_NOT_FOUND(ApiResponse MEMBER_SAVINGS_ACCOUNT_NOT_FOUND) {
        this.MEMBER_SAVINGS_ACCOUNT_NOT_FOUND = MEMBER_SAVINGS_ACCOUNT_NOT_FOUND;
    }

    public ApiResponse getLOAN_TYPE_NOT_FOUND() {
        return LOAN_TYPE_NOT_FOUND;
    }

    public void setLOAN_TYPE_NOT_FOUND(ApiResponse LOAN_TYPE_NOT_FOUND) {
        this.LOAN_TYPE_NOT_FOUND = LOAN_TYPE_NOT_FOUND;
    }
    
    public ApiResponse getLOAN_AMOUNT_EXEEDS_FACTOR_LIMIT() {
        return LOAN_AMOUNT_EXEEDS_FACTOR_LIMIT;
    }

    public void setLOAN_AMOUNT_EXEEDS_FACTOR_LIMIT(ApiResponse LOAN_AMOUNT_EXEEDS_FACTOR_LIMIT) {
        this.LOAN_AMOUNT_EXEEDS_FACTOR_LIMIT = LOAN_AMOUNT_EXEEDS_FACTOR_LIMIT;
    }
    
    public ApiResponse getSUCCESS() {
        return SUCCESS;
    }

    public void setSUCCESS(ApiResponse SUCCESS) {
        this.SUCCESS = SUCCESS;
    }

    public ApiResponse getLOAN_REQUIRES_GURANTORS() {
        return LOAN_REQUIRES_GURANTORS;
    }

    public void setLOAN_REQUIRES_GURANTORS(ApiResponse LOAN_REQUIRES_GURANTORS) {
        this.LOAN_REQUIRES_GURANTORS = LOAN_REQUIRES_GURANTORS;
    }

    public ApiResponse getCREATION_FAILED() {
        return CREATION_FAILED;
    }

    public void setCREATION_FAILED(ApiResponse CREATION_FAILED) {
        this.CREATION_FAILED = CREATION_FAILED;
    }

    public ApiResponse getEXCEPTION_OCCURRED() {
        return EXCEPTION_OCCURRED;
    }

    public void setEXCEPTION_OCCURRED(ApiResponse EXCEPTION_OCCURRED) {
        this.EXCEPTION_OCCURRED = EXCEPTION_OCCURRED;
    }

    public ApiResponse getNO_RECORDS_FOUND() {
        return NO_RECORDS_FOUND;
    }

    public void setNO_RECORDS_FOUND(ApiResponse NO_RECORDS_FOUND) {
        this.NO_RECORDS_FOUND = NO_RECORDS_FOUND;
    }

}
