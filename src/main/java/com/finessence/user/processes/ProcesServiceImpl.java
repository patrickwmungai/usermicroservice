package com.finessence.user.processes;

import com.finessence.user.entities.Memberaccount;
import com.finessence.user.repository.CrudService;
import com.finessence.user.services.GlobalFunctions;
import com.finessence.user.services.RestTemplateServices;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author patrick
 */
@Service("processService")
@Transactional
public class ProcesServiceImpl implements ProcesService {

    Logger LOGGER = Logger.getLogger(ProcesServiceImpl.class.getName());

    @Autowired
    Environment environment;

    private static ThreadPoolExecutor exec_service = new ThreadPoolExecutor(10, 10, 5000, TimeUnit.SECONDS, new LinkedBlockingQueue());

    private static ThreadPoolExecutor pool = new ThreadPoolExecutor(20, 20, 5000, TimeUnit.SECONDS, new LinkedBlockingQueue());

    @Autowired
    Environment env;

    @Autowired
    GlobalFunctions globalFunctions;

    @Autowired
    CrudService crudService;

    @Autowired
    RestTemplateServices resttemplateService;

//    @Scheduled(initialDelay = 2000, fixedRate = 2000)
    @Override
    public void processCrude() {

        LOGGER.info("processing Crude items");
    }
    

//     a) Create a loan Account e.g 0107000000012354 01070 being the account type the rest is 7 digit member code the rest is group code.
//      b) Populate the Loan details table with values discussed on Call.
//      c) Create Loan Account set balance be 0
//      d) Debit Loan account (Yes to negative) with Applied Amount and credit the wallet account with similar amount (Applied Amount)
//      e) Debit Current Account with the loan application charges as follows;
//           1) Credit Loan Application Fee to the loan application account which should be an account of GL Code 01A and type 01090KES, Debit member's
//               wallet account
//           2) Credit Insurance Fee to the Insurance Fees Account(Account Type 01011KES and GLCode 01L), Debit Customer Account

   
    
}
//
//During Application Validate Aganist loan Type and Gurantors
//aPPROVERS
//APPROVAL LEVELS
//lOAS aPPLICATION FORM PROCESS
//loand type,
//Serach a member via code.
//key details of a loan
//Send to Approval 
//Loand undergoes approval process
//If rejected send email/sms kabugi to provide the notification classes
//
//
//LOAND DISBUSERMENT PROCESS
//DRAWIND DOWN PROCESS (Creating loan account from loan application)
//Select Approved loan
//Create loan account Member account Table 01070 account type member code (Member code) account name (Meber name +loan type name) 
//account type(01070KES) currency (KES) GLCODE(ALL loans are assets i.e 01A) DRAWNDOWNSTATUS=Y during creation,amount=amount_requested+interest
//Credit The account created as Above
//Source of loan is the internal revenue account
//Debit the revenue account i.e *(Find account by 01R2kes i.e find by account type and groupid to ensure it belongs to that particular account)
//
//
//TRANSACTIONDETAILS records the transactions
//NB THE REFERENCE SHOULD BE THE SAME and you should genetate it
//Debit account Shares i.e Accountid is the Revenueaccount 01R2KES descripition dradown,MemberCode=member code of revenue account, STATUS=A,TRANSNAME=LOAN_DRADOWN ,TRANSACTOR=USER_ID,TRANSTYPE=D
//Credit the loan account Shares i.e Accountid is the newly created account,Amount is the amount,MemberCode=member code of the group member, descripition dradown, STATUS=A,TRANSNAME=LOAN_DRADOWN ,TRANSACTOR=USER_ID,TRANSTYPE=C
//
//DISBURSEMENT PROCESS
//list of drawndown accounts (find by group id, account type and drawdownstatus)
//Approval process
//When approved
//LOAN DETAILS(created once but updated during repayment) ADDEDBY=last approver,APPLICATIONDATE=application date,APPLICATIONID=FK OF loan application,DISBURSEMENTAMOUNT=APPROVED_AMOUNT-(ALL CHARGES ON LOAN TYPE),DISBURSEMENTDATE=disdate,INSTALLMENTAMOUNT=amount on loan account table/repayment period in loan application,INTERESTTOTAL=intrest of the loan,MEMBERCODE=patrick acconts,NEXTPAYMENTAMOUNT=amount to paynex month
//,PRINCIPLETOTAL=loan_amount_requested
//
//Debit the loan account with disbursement_amount on loan_details table
//Credit the member_current_account with the disbursement_amount
//CHARGES AMOUNT To be credit to the credit account to charges account
//
//
//
//Group Member 
//NB Members has 3 accounts shares,savings and current/wallet account
//nb Member account has firts 5 digit account type next 6 is member code next 4 is the group code.
//
//
//

//
//We expect the Loans Module to perform as follows.
//
//A) Loan Application
//
//1) User Accesses 'New Loan Application' Menu
//
//2) System Displays input field to search member using ;
//    a) Member-code, Member Number, Id number, Telephone /Mobile
//
//3) System displays the loan capture form which should have a drop down for LoanTypes
//
//4) User Enters Applied Amount. And Other Loan Parameters like
//       a) Repayment Period
//       b) Purpose for loan
//       c) Guarantors - (This is an entity review it) The system should validate with minimum guarantors set on the LoanType selected
//       d) if the loan type requires security, you need to capture security details ie security value, serial number and scanned copy of the security document
//
//5) User submits by clicking 'Submit Application' button. -Sends Email to users in approval list (We can do this later)'
//
//6) Loan goes for a referral/approval - You can do approvals later once i finish User Management
//
//7) Once a loan is rejected or approved - Send Notification to the member and users (We can do this later)
//
//8) Once a loan is approved, see logic below for disbursement
//
//B) LOAN DISBURSEMENT
//
//1) User selects menu 'Loan Disbursement'
//
//2) System presents input to search for loan application using ; Member Code display relevant error if member has no approved loan application
//
//3) If Application Found, Display loan details to user including member details
//
//4) User clicks on 'Disburse Loan' button.
//
//5) The System sends the disbursement to referral (We can do this later)
//
//6) Once approved, the system should
//    a) Create a Loan Account of type 01070KES
//    b) Debit the Loan Account Above with the loan application amount (Yes, it will be negative)
//    c) Credit the Member's Wallet/Current Account with the loan application amount
//    d) Debit the member's wallet account with loan application fee amount
//    e) Debit the member's wallet account with Insurance Fee Account
//    f) Credit Internal Loan Application Fees Account
//    g) Credit Internal Loan Insurance Account
//
//
//C) Loan Repayment - Wait until Chris is through with the transaction capturing menu