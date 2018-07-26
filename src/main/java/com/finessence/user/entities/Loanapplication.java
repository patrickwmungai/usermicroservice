/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.finessence.user.entities;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author patrick
 */
@Entity
@Table(name = "LOANAPPLICATION")
@NamedQueries({
    @NamedQuery(name = "Loanapplication.findAll", query = "SELECT l FROM Loanapplication l")
    , @NamedQuery(name = "Loanapplication.findById", query = "SELECT l FROM Loanapplication l WHERE l.id = :id")
    , @NamedQuery(name = "Loanapplication.findByApplicationdate", query = "SELECT l FROM Loanapplication l WHERE l.applicationdate = :applicationdate")
    , @NamedQuery(name = "Loanapplication.findByApplicationstatus", query = "SELECT l FROM Loanapplication l WHERE l.applicationstatus = :applicationstatus")
    , @NamedQuery(name = "Loanapplication.findByAppliedamount", query = "SELECT l FROM Loanapplication l WHERE l.appliedamount = :appliedamount")
    , @NamedQuery(name = "Loanapplication.findByApprovedamount", query = "SELECT l FROM Loanapplication l WHERE l.approvedamount = :approvedamount")
    , @NamedQuery(name = "Loanapplication.findByApprovedby", query = "SELECT l FROM Loanapplication l WHERE l.approvedby = :approvedby")
    , @NamedQuery(name = "Loanapplication.findByGroupid", query = "SELECT l FROM Loanapplication l WHERE l.groupid = :groupid")
    , @NamedQuery(name = "Loanapplication.findByLoantypeid", query = "SELECT l FROM Loanapplication l WHERE l.loantypeid = :loantypeid")
    , @NamedQuery(name = "Loanapplication.findByMembercode", query = "SELECT l FROM Loanapplication l WHERE l.membercode = :membercode")
    , @NamedQuery(name = "Loanapplication.findBySelfapplication", query = "SELECT l FROM Loanapplication l WHERE l.selfapplication = :selfapplication")
    , @NamedQuery(name = "Loanapplication.findByUserId", query = "SELECT l FROM Loanapplication l WHERE l.userId = :userId")})
public class Loanapplication implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID")
    private Long id;
    @Column(name = "APPLICATIONDATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date applicationdate;
    @Size(max = 255)
    @Column(name = "APPLICATIONSTATUS")
    private String applicationstatus;
    @Column(name = "APPLIEDAMOUNT")
    private BigInteger appliedamount;
    @Column(name = "APPROVEDAMOUNT")
    private BigInteger approvedamount;
    @Column(name = "APPROVEDBY")
    private BigInteger approvedby;
    @Size(max = 255)
    @Column(name = "GROUPID")
    private String groupid;
    @Column(name = "LOANTYPEID")
    private BigInteger loantypeid;
    @Size(max = 255)
    @Column(name = "MEMBERCODE")
    private String membercode;
    @Size(max = 255)
    @Column(name = "SELFAPPLICATION")
    private String selfapplication;
    @Column(name = "USER_ID")
    private Integer userId;
    @Column(name = "UPDATED_BY")
    private Integer updatedBy;
    @Column(name = "REPAYMENT_PERIOD")
    private Integer repaymentPeriod;
    @Column(name = "CURRENT_APPROVAL_LEVEL")
    private Integer currentApprovalLevel;
    @Column(name = "APPROVAL_STATUS")
    private String approvalStatus;
    @Column(name = "LOAN_ACCOUNT_NO")
    private String loanAccountNo;
    @Column(name = "INTEREST_RATE")
    private Double interestRate;
//    @JoinTable(name = "LOANAPPLICATION_LOANGUARANTOR", joinColumns = {
//        @JoinColumn(name = "Loanapplication_ID", referencedColumnName = "ID")}, inverseJoinColumns = {
//        @JoinColumn(name = "proposedGuarantors_ID", referencedColumnName = "ID")})
//    @ManyToMany
    @Transient
    private List<Loanguarantor> loanguarantors;

    public Loanapplication() {
    }

    public Double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(Double interestRate) {
        this.interestRate = interestRate;
    }

    public String getLoanAccountNo() {
        return loanAccountNo;
    }

    public void setLoanAccountNo(String loanAccountNo) {
        this.loanAccountNo = loanAccountNo;
    }

    public Integer getCurrentApprovalLevel() {
        return currentApprovalLevel;
    }

    public void setCurrentApprovalLevel(Integer currentApprovalLevel) {
        this.currentApprovalLevel = currentApprovalLevel;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public Integer getRepaymentPeriod() {
        return repaymentPeriod;
    }

    public void setRepaymentPeriod(Integer repaymentPeriod) {
        this.repaymentPeriod = repaymentPeriod;
    }

    public Loanapplication(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Integer getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Integer updatedBy) {
        this.updatedBy = updatedBy;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getApplicationdate() {
        return applicationdate;
    }

    public void setApplicationdate(Date applicationdate) {
        this.applicationdate = applicationdate;
    }

    public String getApplicationstatus() {
        return applicationstatus;
    }

    public void setApplicationstatus(String applicationstatus) {
        this.applicationstatus = applicationstatus;
    }

    public BigInteger getAppliedamount() {
        return appliedamount;
    }

    public void setAppliedamount(BigInteger appliedamount) {
        this.appliedamount = appliedamount;
    }

    public BigInteger getApprovedamount() {
        return approvedamount;
    }

    public void setApprovedamount(BigInteger approvedamount) {
        this.approvedamount = approvedamount;
    }

    public BigInteger getApprovedby() {
        return approvedby;
    }

    public void setApprovedby(BigInteger approvedby) {
        this.approvedby = approvedby;
    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public BigInteger getLoantypeid() {
        return loantypeid;
    }

    public void setLoantypeid(BigInteger loantypeid) {
        this.loantypeid = loantypeid;
    }

    public String getMembercode() {
        return membercode;
    }

    public void setMembercode(String membercode) {
        this.membercode = membercode;
    }

    public String getSelfapplication() {
        return selfapplication;
    }

    public void setSelfapplication(String selfapplication) {
        this.selfapplication = selfapplication;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public List<Loanguarantor> getLoanguarantors() {
        return loanguarantors;
    }

    public void setLoanguarantors(List<Loanguarantor> loanguarantors) {
        this.loanguarantors = loanguarantors;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Loanapplication)) {
            return false;
        }
        Loanapplication other = (Loanapplication) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.finessence.user.entities.Loanapplication[ id=" + id + " ]";
    }

}
