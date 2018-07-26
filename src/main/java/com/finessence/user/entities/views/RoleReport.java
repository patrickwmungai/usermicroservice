/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.finessence.user.entities.views;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author patrick
 */
@Entity
@Table(name = "ROLE_REPORT")
@NamedQueries({
    @NamedQuery(name = "RoleReport.findAll", query = "SELECT r FROM RoleReport r")
    , @NamedQuery(name = "RoleReport.findById", query = "SELECT r FROM RoleReport r WHERE r.id = :id")
    , @NamedQuery(name = "RoleReport.findByName", query = "SELECT r FROM RoleReport r WHERE r.name = :name")
    , @NamedQuery(name = "RoleReport.findByGroupId", query = "SELECT r FROM RoleReport r WHERE r.groupId = :groupId")
    , @NamedQuery(name = "RoleReport.findByTimeCreated", query = "SELECT r FROM RoleReport r WHERE r.timeCreated = :timeCreated")
    , @NamedQuery(name = "RoleReport.findByTimeUpdated", query = "SELECT r FROM RoleReport r WHERE r.timeUpdated = :timeUpdated")
    , @NamedQuery(name = "RoleReport.findByCreatedBy", query = "SELECT r FROM RoleReport r WHERE r.createdBy = :createdBy")
    , @NamedQuery(name = "RoleReport.findByUpdatedBy", query = "SELECT r FROM RoleReport r WHERE r.updatedBy = :updatedBy")
    , @NamedQuery(name = "RoleReport.findByStatus", query = "SELECT r FROM RoleReport r WHERE r.status = :status")
    , @NamedQuery(name = "RoleReport.findByApprovalStatus", query = "SELECT r FROM RoleReport r WHERE r.approvalStatus = :approvalStatus")
    , @NamedQuery(name = "RoleReport.findByMaxApprovals", query = "SELECT r FROM RoleReport r WHERE r.maxApprovals = :maxApprovals")
    , @NamedQuery(name = "RoleReport.findByCurrentApprovalLevel", query = "SELECT r FROM RoleReport r WHERE r.currentApprovalLevel = :currentApprovalLevel")
    , @NamedQuery(name = "RoleReport.findByIntrash", query = "SELECT r FROM RoleReport r WHERE r.intrash = :intrash")
    , @NamedQuery(name = "RoleReport.findByGroupName", query = "SELECT r FROM RoleReport r WHERE r.groupName = :groupName")
    , @NamedQuery(name = "RoleReport.findByGroupCode", query = "SELECT r FROM RoleReport r WHERE r.groupCode = :groupCode")
    , @NamedQuery(name = "RoleReport.findByCreatedByName", query = "SELECT r FROM RoleReport r WHERE r.createdByName = :createdByName")
    , @NamedQuery(name = "RoleReport.findByUpdatedByName", query = "SELECT r FROM RoleReport r WHERE r.updatedByName = :updatedByName")})
public class RoleReport implements Serializable {

    private static final long serialVersionUID = 1L;
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID")
    @Id
    private int id;
    @Size(max = 70)
    @Column(name = "NAME")
    private String name;
    @Size(max = 255)
    @Column(name = "GROUP_ID")
    private String groupId;
    @Column(name = "TIME_CREATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timeCreated;
    @Column(name = "TIME_UPDATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timeUpdated;
    @Column(name = "CREATED_BY")
    private Integer createdBy;
    @Column(name = "UPDATED_BY")
    private Integer updatedBy;
    @Column(name = "STATUS")
    private Integer status;
    @Column(name = "APPROVAL_STATUS")
    private Integer approvalStatus;
    @Column(name = "MAX_APPROVALS")
    private Integer maxApprovals;
    @Column(name = "CURRENT_APPROVAL_LEVEL")
    private Integer currentApprovalLevel;
    @Size(max = 5)
    @Column(name = "INTRASH")
    private String intrash;
    @Size(max = 255)
    @Column(name = "GROUP_NAME")
    private String groupName;
    @Size(max = 255)
    @Column(name = "GROUP_CODE")
    private String groupCode;
    @Size(max = 50)
    @Column(name = "CREATED_BY_NAME")
    private String createdByName;
    @Size(max = 50)
    @Column(name = "UPDATED_BY_NAME")
    private String updatedByName;

    public RoleReport() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    public Date getTimeUpdated() {
        return timeUpdated;
    }

    public void setTimeUpdated(Date timeUpdated) {
        this.timeUpdated = timeUpdated;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public Integer getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Integer updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(Integer approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public Integer getMaxApprovals() {
        return maxApprovals;
    }

    public void setMaxApprovals(Integer maxApprovals) {
        this.maxApprovals = maxApprovals;
    }

    public Integer getCurrentApprovalLevel() {
        return currentApprovalLevel;
    }

    public void setCurrentApprovalLevel(Integer currentApprovalLevel) {
        this.currentApprovalLevel = currentApprovalLevel;
    }

    public String getIntrash() {
        return intrash;
    }

    public void setIntrash(String intrash) {
        this.intrash = intrash;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public String getUpdatedByName() {
        return updatedByName;
    }

    public void setUpdatedByName(String updatedByName) {
        this.updatedByName = updatedByName;
    }
    
}
