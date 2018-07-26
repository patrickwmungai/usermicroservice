<?php

/*
  First get total number of rows in data table.
  If you have a WHERE clause in your query, make sure you mirror it here.
 */
extract($_POST);
if (isset($_POST[submit])) {
    $searchFilter = '';
    $filterArray = array();
    if ($keywords != '') {
        $searchFilter = "AND (`budget`.`title` like '%$keywords%' or `treatment_model`.`title` like '%$keywords%'
									or `msisdn` like '%$keywords%' or credit like '%$keywords%' or firstname like '%$keywords%' or middlename like '%$keywords%' or lastname like '%$keywords%') ";
        $filterArray['KEYWORDS_F'] = $keywords;
    }
    if ($province != '') {
        $searchFilter .= " AND province = '$province' ";
        $filterArray['PROVINCE_F'] = $province;
    }
    if ($budget != '') {
        $searchFilter .= " AND budget = '$budget' ";
        $filterArray['BUDGET_F'] = $budget;
    }
    if ($county != '') {
        $searchFilter .= " AND county = '$county' ";
        $filterArray['COUNTY_F'] = $county;
    }
    if ($month != '') {
        $searchFilter .= " AND (apiorder.month_of_claim='$month' or apiorder.month='$month') ";
        $filterArray['MONTH_F'] = $month;
    }
    if ($year != '') {
        $searchFilter .= " AND apiorder.year like '%$year%' ";
        $filterArray['YEAR_F'] = $year;
    }
    if ($datefrom != '') {
        $searchFilter .= " AND requesttime >= '$_POST[datefrom] 00:00:00' ";
        $filterArray['DATEFROM_F'] = $_POST[datefrom];
    }
    if ($dateto != '') {
        $searchFilter .= " AND requesttime <= '$_POST[dateto] 23:59:59' ";
        $filterArray['DATETO_F'] = $_POST[dateto];
    }
    /*
      Store filterArray and SearchFilter in Session for as long as
      user is on this page. They are unset at resolve.php when
      the user navigates away from this page
     */
    $_SESSION[filterArray] = $filterArray;
    $_SESSION[searchFilter] = $searchFilter;
}
#Insert Query Here
$org_filters = str_replace("ORG_ID", "apiorder.ORG_ID", $_SESSION[ORG_FILTER]);
$query = "SELECT `apiorder`.`ID` AS `primarykey`,IFNULL(`month`,`month_of_claim`)Month,year as 'Year',  `budget`.`title` AS `Budget Line`,(CASE WHEN (SELECT title FROM province WHERE province.id = `apiorder`.`province`) IS NULL THEN 'N/A' ELSE (SELECT title FROM province WHERE province.id = `apiorder`.`province`) END)  AS `Region`,(CASE WHEN (SELECT title FROM county WHERE county.id = `apiorder`.`county`) IS NULL THEN 'N/A' ELSE (SELECT title FROM county WHERE county.id = `apiorder`.`county`) END)  AS `County`,facility, `treatment_model`.`title` AS `Request Type`, `msisdn` AS `Phone No.`,  CONCAT(`firstname`,' ',`middlename`,' ',`lastname`)  AS `Beneficiary`,initiator_username AS 'Initator', CONVERT_TZ(apiorder.requesttime,'+00:00','+03:00') AS `Request Time`,
		  `patient_registration_number` As 'Patient Number', `date_treatment_started` AS 'Date Treatement Started', `dot_nurse_name` AS 'Dot name', `dot_nurse_phoneno` as 'DOT Phone',
		`recipient2credit` AS `DOT Amount`,driver_amount as 'Driver AMT', `credit` AS `Amount` FROM `apiorder` LEFT JOIN `ordertype` ON (`apiorder`.`ordertype` = `ordertype`.`ID`) 
                    LEFT JOIN `budget` ON (`apiorder`.`budget` = `budget`.`ID`) 
                    LEFT JOIN `treatment_model` ON (`apiorder`.`treatment_model` = `treatment_model`.`ID`) 
                    WHERE `approval_level`= 2
                    AND `approval_status` = 'Pending' 
                    AND `request_src` = 'api' 
                    " . $_SESSION[searchFilter] . " $org_filters 
                    AND apiorder.inTrash = 'No' AND (case when " . $_SESSION[region] . "=0 then '1=1' else county = '" . $_SESSION[region] . "' end) AND apiorder.intrash = 'No'
                    ORDER BY `apiorder`.`ID` desc";

$_SESSION['sqlxls'] = $query;
$_SESSION['FileName'] = 'PTLCApproval';

$total_pages = mysql_num_rows(mysql_db_query($database, $query, $dblink));
$target_page = $_SERVER['PHP_SELF']; //. $_SERVER['REQUEST_URI'];

if (!strstr($target_page, "page=")) {
    $_SESSION['targetpage'] = $_SERVER['PHP_SELF']; // . $_SERVER['REQUEST_URI'];
}
$target_page = $_SESSION['targetpage'];
/* Setup vars for query. */
#	How many adjacent pages should be shown on each side?
$adjacents = 3;
$limit = $_SESSION[page_size];         //how many items to show per page
$page = $_GET['page'];
if ($page) {
    $start = ($page - 1) * $limit;    //first item to display on this page
} else {
    $start = 0;        //if no page var is given, set start to 0
    $page = 1;
}
/* Get data. */
$query .= " LIMIT $start, $limit";
$result = mysql_db_query($database, $query, $dblink);
/*
 * 	getPaginationString($page[page num], $totalitems[row count], $limit[rows/page], $adjacents[number of adjacent columns], $targetpage[current page])
 *
 */
$pagination = getPaginationString($page, $total_pages, $limit, $adjacents, $target_page);


/* Set action bars */
$actionsBar = (hasPermission("approvaltwo", "3") ? '<div id="approval" style="display:none"><form name="approvalTop" method="post">
		<input type="text" name="Notes" class="grid5of10 gridActionButtons required" placeholder="Enter comment here">
		<input type="button" name="approve" value="Approve" class="gridActionButtons gridApproveButton">
		<input type="button" name="reject" value="Reject" class="gridActionButtons gridRejectButton">
                <input type="hidden" name="batch_approval_level" value="2"/>
</form></div>' : '');


$res = generateGrid("Payment Requests", "view", $result, false, true, true, true, false, $pagination, $actionsBar, array("search", "province", "county", "budget", "batch", "Month", "Year", "date"));
//echo $query;
//echo "Testing here ".$_SESSION['BATCHSESSION'];
echo $res;
?>
