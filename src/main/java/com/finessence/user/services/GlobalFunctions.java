package com.finessence.user.services;

import com.finessence.user.entities.Accounttype;
import com.finessence.user.entities.ApprovalsDone;
import com.finessence.user.entities.GroupApprovalLevel;
import com.finessence.user.entities.GroupApprovalLevelsConfig;
import com.finessence.user.entities.Groupmember;
import com.finessence.user.entities.Invgroup;
import com.finessence.user.entities.LoanDisbursements;
import com.finessence.user.entities.Loanapplication;
import com.finessence.user.entities.Loandetails;
import com.finessence.user.entities.Loanguarantor;
import com.finessence.user.entities.Loantype;
import com.finessence.user.entities.Memberaccount;
import com.finessence.user.entities.Permission;
import com.finessence.user.entities.Role;
import com.finessence.user.entities.Transactiondetails;
import com.finessence.user.entities.Users;
import com.finessence.user.model.Token;
import com.finessence.user.repository.CrudService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.xml.bind.DatatypeConverter;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author patrick
 */
@Service("functions")
public class GlobalFunctions {

    private final Logger LOG = Logger.getLogger(GlobalFunctions.class);

    @Autowired
    CrudService crudService;

    //function to decode Jwt
    public Token parseJWT(String jwt) {
        //This line will throw an exception if it is not a signed JWS (as expected)
        Claims claims = Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary("KEY123456"))
                .parseClaimsJws(jwt).getBody();

        Token token = new Token();
        token.setGroupID(claims.get("groupid", String.class));
        token.setUserid(claims.get("userid", String.class));
        token.setIsadminagent(claims.get("isadmingroup", String.class));

        return token;

    }

    public boolean checkifAnyOfGurantorsIsTheGuyApplyingLoan(List<Loanguarantor> gurantors, String currentMemberCode) {
        boolean isApplicant = false;
        for (Loanguarantor loanguarantor : gurantors) {
            if (loanguarantor.getMembercode().equals(currentMemberCode)) {
                isApplicant = true;
                break;
            }
        }
        return isApplicant;
    }

    public void logApprovals(Integer approvalLevel, String groupId, String approvalComments, String approvalStatus, BigInteger approvedAmount, BigInteger originalAmount, Long recordId, String recordType, Integer approverId) {
        GroupApprovalLevel groupApprovalLevel = getGroupApprovalLevel(groupId, approvalLevel);

        ApprovalsDone approvalsDone = new ApprovalsDone();
        approvalsDone.setId(0);
        approvalsDone.setApprovalLevel(approvalLevel);
        approvalsDone.setGroupId(groupId);
        approvalsDone.setApprovalLevelName(groupApprovalLevel.getName());
        approvalsDone.setApprovalNotes(approvalComments);
        approvalsDone.setApprovalStatus(approvalStatus);
        approvalsDone.setApprovalTime(new Date());
        approvalsDone.setApprovedAmount(new BigDecimal(approvedAmount.toString()));
        approvalsDone.setOriginalAmount(new BigDecimal(originalAmount.toString()));
        approvalsDone.setRecordId(Integer.parseInt(recordId.toString()));
        approvalsDone.setType(recordType);
        approvalsDone.setApproverId(approverId);

        crudService.save(approvalsDone);
    }

    public void createDisbursement(Loanapplication loanapplication, Token token) {
        LoanDisbursements loanDisbursements = new LoanDisbursements();
        loanDisbursements.setId(0);
        loanDisbursements.setLoanApplicationId(Integer.parseInt(loanapplication.getId().toString()));
        loanDisbursements.setCreatedBy(Integer.parseInt(token.getUserid()));
        loanDisbursements.setDateCreated(new Date());
        loanDisbursements.setGroupId(loanapplication.getGroupid());
        loanDisbursements.setAmount(Long.parseLong(loanapplication.getApprovedamount().toString()));

        boolean disbursementHasNoApprovalLevels = false;
        //Get approval levels if any then set
        GroupApprovalLevelsConfig approvalLevelsConfig = getGroupsApprovalLevelsConfigByType("LOAN_DISBURSEMENT", loanapplication.getGroupid());
        if (approvalLevelsConfig != null) {
            String[] levels = approvalLevelsConfig.getTypeApprovals().split(",");
            if (levels.length > 0) {
                loanDisbursements.setCurrentApprovalLevel(Integer.parseInt(levels[0]));
                loanDisbursements.setApprovalStatus("Pending");
            } else {
                disbursementHasNoApprovalLevels = true;
            }
        } else {
            disbursementHasNoApprovalLevels = true;
        }
        if (disbursementHasNoApprovalLevels) {
            //Automatically approve disbursement
            loanDisbursements.setCurrentApprovalLevel(0);
            loanDisbursements.setApprovalStatus("Approved");
            loanDisbursements.setApprovedBy(token.getUserid());
            loanDisbursements.setDateApproved(new Date());
            crudService.save(loanDisbursements);

            processApprovedDisbursement(loanDisbursements, loanapplication, token);

        } else {
            crudService.save(loanDisbursements);
        }

    }

    public void processApprovedDisbursement(LoanDisbursements loanDisbursements, Loanapplication loanapplication, Token token) {
        //create loan details
        Memberaccount loanAccount = createLoanAccount(loanapplication);
        //create loan details account
        createLoanDetails(loanapplication, loanDisbursements, token);

        //Call accounts function for accounting entries
        //Debit loan account
        debitAccount(loanAccount, loanapplication.getAppliedamount(), "Loan Disbursment Credit");
        //credit current 
        Accounttype accountTypeCurrent = getAccountByName("CURRENT");
        Memberaccount memberCurrentAccount = getMemberaccountByAccountTypeAndAccountGroupId(accountTypeCurrent.getTypecode() + "KES", loanapplication.getGroupid());
        creditAccount(memberCurrentAccount, loanapplication.getAppliedamount(), "Credit customer loan account from loan account " + loanAccount.getAccountid());
        //Get charges
        //Get accounts
        //Get loan type to check the fees to be charged
        Loantype loantype = getLoantype(loanapplication.getLoantypeid().toString());
        if (loantype != null) {
            Double applicationfee = loantype.getApplicationfee() == null ? 0.0 : loantype.getApplicationfee();
            Double insurancefee = loantype.getInsurancefee() == null ? 0.0 : loantype.getInsurancefee();
            if (applicationfee > 0.0) {
                //get application  fee account
                Accounttype accounttype = getAccountByName("APPLICATION_FEE");
                Memberaccount applicationFeeAccount = getMemberaccountByAccountTypeAndAccountGroupId(accounttype.getTypecode() + "KES", loanapplication.getGroupid());

                //Debit wallet Account applicationFeeAccount
                debitAccount(memberCurrentAccount, new BigInteger(applicationfee.toString().replace(".0", "")), "Loan Application fee payment to account " + applicationFeeAccount.getAccountid());
                //credit applicationFeeAccount
                creditAccount(applicationFeeAccount, new BigInteger(applicationfee.toString().replace(".0", "")), "Loan Application fee from member account " + memberCurrentAccount.getAccountid());

            }

            if (insurancefee > 0.0) {
                //get Insurance  fee account
                Accounttype accounttype = getAccountByName("INSURANCE_FEE");
                Memberaccount insuranceFeeAccount = getMemberaccountByAccountTypeAndAccountGroupId(accounttype.getTypecode() + "KES", loanapplication.getGroupid());

                //Debit wallet Account applicationFeeAccount
                debitAccount(memberCurrentAccount, new BigInteger(insurancefee.toString().replace(".0", "")), "Loan Insurance fee payment to account " + insuranceFeeAccount.getAccountid());
                //credit applicationFeeAccount
                creditAccount(insuranceFeeAccount, new BigInteger(insurancefee.toString().replace(".0", "")), "Loan Insurance fee from member account " + memberCurrentAccount.getAccountid());

            }
        } else {
            LOG.info("loan type is null");
        }

    }

    public Memberaccount createLoanAccount(Loanapplication loanapplication) {
        Loantype loantype = getLoantype(loanapplication.getLoantypeid().toString());
        Groupmember groupmember = getGroupmember(loanapplication.getMembercode());
        Invgroup invgroup = getGroupById(loanapplication.getGroupid());
        Memberaccount loanaccount = new Memberaccount();
        loanaccount.setId(0);
        loanaccount.setAccountbalance(BigInteger.ZERO);
        loanaccount.setAccountname(groupmember.getMembername() + " " + loantype.getTypename());
        loanaccount.setAvailablebalance(BigInteger.ZERO);
        loanaccount.setBlockedbalance(BigInteger.ZERO);
        loanaccount.setCurrency("KES");
        loanaccount.setDescription("Loan Account for member");
        loanaccount.setDormantstatus("0");
        loanaccount.setGroupid(loanapplication.getGroupid());
        loanaccount.setMembercode(groupmember.getMembercode());
        loanaccount.setDateopened(new Date());
        Accounttype accounttype = getAccountByName("LOAN");
        String accountid = accounttype.getTypecode() + groupmember.getMembercode() + invgroup.getGroupcode() + loanapplication.getId();
        loanaccount.setAccountid(accountid);
        loanaccount.setAccounttype(accounttype.getTypecode() + "KES");
        loanaccount.setGlcode("01A");

        crudService.save(loanaccount);
        Memberaccount memberaccount = getMemberaccountByAccountNo(accountid);
        loanaccount.setId(memberaccount.getId());
        //update loan with the created loan account
        loanapplication.setLoanAccountNo(accountid);
        crudService.saveOrUpdate(loanapplication);

        return loanaccount;
    }

    public void creditAccount(Memberaccount memberaccount, BigInteger amountToCredit, String description) {
        memberaccount.setAccountbalance(memberaccount.getAccountbalance().add(amountToCredit));
        crudService.saveOrUpdate(memberaccount);

        //Accounting entris
        //Credit  leg to new Investment account
        int randomNo = new Random().nextInt(10000);
        String transactionReference = randomNo + new SimpleDateFormat("ddMMhhmmss").format(new Date());

        Transactiondetails accountCR = new Transactiondetails();
        accountCR.setTransactionid(Long.parseLong("0"));
        accountCR.setTransname(description);
        accountCR.setMembercode(memberaccount.getMembercode());
        accountCR.setTransactor(memberaccount.getMembercode());
        accountCR.setAccountid(memberaccount.getAccountid());
        accountCR.setAmount(amountToCredit);
        accountCR.setGroupid(memberaccount.getGroupid());
        accountCR.setNarration(memberaccount.getAccountname());
        accountCR.setReference(transactionReference);
        accountCR.setTranstype("C");
        accountCR.setRunningBalance(memberaccount.getAccountbalance());

        crudService.save(accountCR);
    }

    public void debitAccount(Memberaccount memberaccount, BigInteger amountToDebit, String description) {
        memberaccount.setAccountbalance(memberaccount.getAccountbalance().subtract(amountToDebit));
        crudService.saveOrUpdate(memberaccount);

        //Accounting entris
        //Credit  leg to new Investment account
        int randomNo = new Random().nextInt(10000);
        String transactionReference = randomNo + new SimpleDateFormat("ddMMhhmmss").format(new Date());

        Transactiondetails accountDR = new Transactiondetails();
        accountDR.setTransactionid(Long.parseLong("0"));
        accountDR.setTransname(description);
        accountDR.setMembercode(memberaccount.getMembercode());
        accountDR.setTransactor(memberaccount.getMembercode());
        accountDR.setAccountid(memberaccount.getAccountid());
        accountDR.setAmount(amountToDebit);
        accountDR.setGroupid(memberaccount.getGroupid());
        accountDR.setNarration(memberaccount.getAccountname());
        accountDR.setReference(transactionReference);
        accountDR.setTranstype("D");
        accountDR.setRunningBalance(memberaccount.getAccountbalance());

        crudService.save(accountDR);
    }
//100000 applied amount
//Conpute loan intrest 10 % 100000 =10000
//charges 5000
    //principal 100000
    //interest amount 10000
//total amount applied amount + intrest=balance 1100000+10000=110000    
    //during disbursment
    //Disbursment is application amount 100000
    //INSTALLMENTAMOUNT loan bal 110k /period PMT formular Google to calculate
    //INTERESTTOTAL 10000
    //NEXTPAYMENTDATE 30 days from disburment date
    //disbursement amount appliend amount

    public void createLoanDetails(Loanapplication loanapplication, LoanDisbursements loanDisbursements, Token token) {
        Loandetails loandetails = new Loandetails();
        loandetails.setId(Long.parseLong("0"));
        loandetails.setAddedby(token.getUserid());
        loandetails.setApplicationdate(loanapplication.getApplicationdate());
        loandetails.setApplicationid(new BigInteger(loanapplication.getId().toString()));
        loandetails.setGroupid(loanapplication.getGroupid());
        loandetails.setDisbursementamount(loanapplication.getAppliedamount());
        loandetails.setDisbursementdate(loanDisbursements.getDateApproved());
        loandetails.setGroupid(loanapplication.getGroupid());
        Double interestRate = loanapplication.getInterestRate() / 100;
        Double interestAmountTotal = Double.parseDouble(loanapplication.getAppliedamount().toString()) * interestRate;
        BigInteger totalAmount = loanapplication.getAppliedamount().add(new BigInteger(interestAmountTotal.toString().replace(".0", "")));
        BigInteger installementAmount = totalAmount.divide(new BigInteger(loanapplication.getRepaymentPeriod().toString()));
        loandetails.setInstallmentamount(installementAmount);
        loandetails.setInteresttotal(new BigInteger(interestAmountTotal.toString().replace(".0", "")));
        loandetails.setLastpaymentamount(BigInteger.ZERO);
        loandetails.setLoanaccountid(loanapplication.getLoanAccountNo());
        loandetails.setLoanbalance(totalAmount);
        loandetails.setMembercode(loanapplication.getMembercode());
        loandetails.setNextpaymentamount(installementAmount);
        Calendar c = new GregorianCalendar();
        c.add(Calendar.DATE, 30);
        Date dPlus30Days = c.getTime();
        loandetails.setNextpaymentdate(dPlus30Days);
        loandetails.setPrincipletotal(totalAmount);
        loandetails.setTotalinterestpaid(BigInteger.ZERO);
        loandetails.setTotalpriciplepaid(BigInteger.ZERO);
        crudService.save(loandetails);
    }

    public Loantype getLoantype(String loanTypeId) {
        //get approva level name
        String q = "select r from Loantype r where id = :id ";

        Map<String, Object> params = new HashMap<>();
        params.put("id", Long.parseLong(loanTypeId));
        List<Loantype> entity = crudService.fetchWithHibernateQuery(q, params);

        return entity == null || entity.isEmpty() ? null : entity.get(0);
    }

    public Loanapplication getLoanApplicationById(String loanApplicationId) {
        //get approva level name
        String q = "select r from Loanapplication r where id = :id ";

        Map<String, Object> params = new HashMap<>();
        params.put("id", Long.parseLong(loanApplicationId));
        List<Loanapplication> entity = crudService.fetchWithHibernateQuery(q, params);

        return entity == null || entity.isEmpty() ? null : entity.get(0);
    }

    public Loandetails getLoanDetailsByLoanApplicationById(BigInteger loanApplicationId) {
        //get approva level name
        String q = "select r from Loandetails r where applicationid = :applicationid ";

        Map<String, Object> params = new HashMap<>();
        params.put("applicationid", loanApplicationId);
        List<Loandetails> entity = crudService.fetchWithHibernateQuery(q, params);

        return entity == null || entity.isEmpty() ? null : entity.get(0);
    }

    public Loanapplication getLoanApplicationById(Long loanApplicationId) {
        //get approva level name
        String q = "select r from Loanapplication r where id = :id ";

        Map<String, Object> params = new HashMap<>();
        params.put("id", loanApplicationId);
        List<Loanapplication> entity = crudService.fetchWithHibernateQuery(q, params);

        return entity == null || entity.isEmpty() ? null : entity.get(0);
    }

    public Memberaccount getMemberaccountByAccountNo(String accountId) {
        //get approva level name
        String q = "select r from Memberaccount r where accountid = :accountid ";

        Map<String, Object> params = new HashMap<>();
        params.put("accountid", accountId);
        List<Memberaccount> entity = crudService.fetchWithHibernateQuery(q, params);

        return entity == null || entity.isEmpty() ? null : entity.get(0);
    }

    public Memberaccount getMemberaccountByAccountTypeAndAccountGroupId(String accountType, String groupId) {
        //get approva level name
        String q = "select r from Memberaccount r where accounttype = :accounttype and groupid=:groupid";

        Map<String, Object> params = new HashMap<>();
        params.put("accounttype", accountType);
        params.put("groupid", groupId);
        List<Memberaccount> entity = crudService.fetchWithHibernateQuery(q, params);

        return entity == null || entity.isEmpty() ? null : entity.get(0);
    }

    public Memberaccount getMemberaccountByAccountId(String accountId) {
        //get approva level name
        String q = "select r from Memberaccount r where accountid = :accountid ";

        Map<String, Object> params = new HashMap<>();
        params.put("accountid", accountId);
        List<Memberaccount> entity = crudService.fetchWithHibernateQuery(q, params);

        return entity == null || entity.isEmpty() ? null : entity.get(0);
    }

    public Memberaccount getMemberaccountByAccountTypeAndGroupIdAndMemberCode(String accountType, String groupId, String memberCode) {
        //get approva level name
        String q = "select r from Memberaccount r where accounttype = :accounttype and groupid=:groupid and membercode=:membercode";

        Map<String, Object> params = new HashMap<>();
        params.put("accounttype", accountType);
        params.put("groupid", groupId);
        params.put("membercode", memberCode);
        List<Memberaccount> entity = crudService.fetchWithHibernateQuery(q, params);

        return entity == null || entity.isEmpty() ? null : entity.get(0);
    }

    public Groupmember getGroupmember(String memberNo) {

        String q = "select r from Groupmember r where membercode = :membercode ";

        Map<String, Object> params = new HashMap<>();
        params.put("membercode", memberNo);
        List<Groupmember> entity = crudService.fetchWithHibernateQuery(q, params);

        return entity == null || entity.isEmpty() ? null : entity.get(0);
    }

    public Accounttype getAccountByName(String typename) {
        String q = "select r from Accounttype r where typename = :typename ";

        Map<String, Object> params = new HashMap<>();
        params.put("typename", typename);
        List<Accounttype> entity = crudService.fetchWithHibernateQuery(q, params);

        return entity == null || entity.isEmpty() ? null : entity.get(0);
    }

    public Invgroup getGroupById(String groupid) {
        String q = "select r from Invgroup r where groupid = :groupid ";

        Map<String, Object> params = new HashMap<>();
        params.put("groupid", groupid);
        List<Invgroup> entity = crudService.fetchWithHibernateQuery(q, params);

        return entity == null || entity.isEmpty() ? null : entity.get(0);
    }

    public GroupApprovalLevelsConfig getGroupsApprovalLevelsConfigByType(String type, String groupId) {
        String q = "select r from GroupApprovalLevelsConfig r where groupId = :groupId and type = :type";

        Map<String, Object> params = new HashMap<>();
        params.put("groupId", groupId);
        params.put("type", type);
        List<GroupApprovalLevelsConfig> entity = crudService.fetchWithHibernateQuery(q, params);

        return entity == null || entity.isEmpty() ? null : entity.get(0);
    }

    public Loanapplication getLoanByGroupIdMemberNoAndStatus(BigInteger loantypeid, String groupId, String membercode, String applicationstatus) {
        //get approva level name
        Loanapplication loanapplication = null;
        String q = "select r from Loanapplication r where loantypeid=:loantypeid and groupId = :groupId and membercode = :membercode and applicationstatus=:applicationstatus";

        Map<String, Object> params = new HashMap<>();
        params.put("loantypeid", loantypeid);
        params.put("groupId", groupId);
        params.put("membercode", membercode);
        params.put("applicationstatus", "Active");
        List<Loanapplication> entity = crudService.fetchWithHibernateQuery(q, params);

        if (!entity.isEmpty()) {
            loanapplication = entity.get(0);
        }
        return loanapplication;
    }

    public GroupApprovalLevel getGroupApprovalLevel(String groupId, Integer position) {
        //get approva level name
        String q = "select r from GroupApprovalLevel r where groupId = :groupId and position = :position";

        Map<String, Object> params = new HashMap<>();
        params.put("groupId", groupId);
        params.put("position", position);
        List<GroupApprovalLevel> entity = crudService.fetchWithHibernateQuery(q, params);

        return entity == null || entity.isEmpty() ? null : entity.get(0);
    }

    public Users findByUserName(String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Users findUserByuserNameAndPassordAndGroupCode(String username, String password, String groupCode) {
        String q = "select r from Users r where userName = :username and password = :password and groupId in (select p.groupid from Invgroup p where groupcode=:groupcode)";

        Map<String, Object> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        params.put("groupcode", groupCode);
        List<Users> entity = crudService.fetchWithHibernateQuery(q, params);

        return entity == null || entity.isEmpty() ? null : entity.get(0);
    }

    public List<Role> findRolesByUserId(Integer userId) {

        String q = "select r from Role r where id in (select p.roleId from RoleMap p where userId=:userId)";

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        List<Role> entity = crudService.fetchWithHibernateQuery(q, params);

        return entity;
    }
    public List<Role> findRolesByGroupId(String groupId) {

        String q = "select r from Role r where groupId=:groupId ";

        Map<String, Object> params = new HashMap<>();
        params.put("groupId", groupId);
        List<Role> entity = crudService.fetchWithHibernateQuery(q, params);

        return entity;
    }

    public int postionOfItemInArray(String[] array, int needle) {
        int positon = 0;
        String needleString = String.valueOf(needle);
        for (int i = 0; i < array.length; i++) {
            if (needleString.equals(array[i])) {
                positon = i;
                break;
            }
        }

        return positon;
    }

    public void deleteRolesByUserId(Integer userId) {
        String q = "delete from RoleMap c where c.userId =:userId";

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        crudService.executeHibernateQuery(q, params);
    }

    public List<Permission> findPermissionByUserId(Integer userId) {

        String q = "select r from Permission r where id in (select pm.permissionId from PermissionMap pm where pm.roleId in "
                + "(select rm.roleId from RoleMap rm where rm.userId=:userId)) order by module";

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        List<Permission> entity = crudService.fetchWithHibernateQuery(q, params);

        return entity;
    }

    public List<Permission> findAllMenuPermissions(Integer userId) {
        String q = "select r from Permission r where isMenu='1' and id in (select pm.permissionId from PermissionMap pm where roleId in "
                + "(select rm.roleId from RoleMap rm where userId=:userId)) ";

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        List<Permission> entity = crudService.fetchWithHibernateQuery(q, params);

        return entity;
    }
    
    public List<Permission> findAllPermissions() {
        String q = "select r from Permission r ";

        List<Permission> entity = crudService.fetchWithHibernateQuery(q, Collections.EMPTY_MAP);

        return entity;
    }

    public List<Permission> findMenuChildPermissions(String subModule, Integer userId) {

        String q = "select r from Permission r where isMenu='0' and subModule=:subModule and id in (select pm.permissionId from PermissionMap pm where roleId in "
                + "(select rm.roleId from RoleMap rm where userId=:userId)) ";

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("subModule", subModule);
        List<Permission> entity = crudService.fetchWithHibernateQuery(q, params);

        return entity;
    }

    public Role findRoleByNameAndGroupIdAndIntrash(String name, String groupId, String intrash) {
        String q = "select r from Role r where name =:name and groupId=:groupId and intrash=:intrash";

        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("groupId", groupId);
        params.put("intrash", intrash);
        List<Role> entity = crudService.fetchWithHibernateQuery(q, params);

        return entity == null || entity.isEmpty() ? null : entity.get(0);
    }

    public List<Permission> findPermissionByRoleId(Integer roleId) {
     
         String q = "select r from Permission r where id in ( select pm.permissionId from PermissionMap pm where roleId =:roleId ) ";

        Map<String, Object> params = new HashMap<>();
        params.put("roleId", roleId);
        List<Permission> entity = crudService.fetchWithHibernateQuery(q, params);

        return entity;
    }

    public void deleteByRoleId(Integer roleId) {
       
        String q = "delete from PermissionMap c where c.roleId =:roleId";

        Map<String, Object> params = new HashMap<>();
        params.put("roleId", roleId);
        crudService.executeHibernateQuery(q, params);
    }

}
