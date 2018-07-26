/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.finessence.user.model;

import java.util.List;

/**
 *
 * @author patrick
 */
public class ApprovalPostRequest {

    private List<Long> loanIds;
    private String approvalComments;
    private String approvalStatus;
    private List<Integer> disbursementIds;

    public List<Long> getLoanIds() {
        return loanIds;
    }

    public void setLoanIds(List<Long> loanIds) {
        this.loanIds = loanIds;
    }

    public String getApprovalComments() {
        return approvalComments;
    }

    public void setApprovalComments(String approvalComments) {
        this.approvalComments = approvalComments;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public List<Integer> getDisbursementIds() {
        return disbursementIds;
    }

    public void setDisbursementIds(List<Integer> disbursementIds) {
        this.disbursementIds = disbursementIds;
    }

}
