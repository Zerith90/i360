<%@ page import="java.sql.*,
				java.util.*,
                 java.io.* "%>  

<jsp:useBean id="logchk" class="CP_Classes.Login" scope="session"/>                 
<jsp:useBean id="CE_Survey" class="CP_Classes.Create_Edit_Survey" scope="session"/>
<jsp:useBean id="trans" class="CP_Classes.Translate" scope="session"/>
<jsp:useBean id="SVR" class="CP_Classes.SurveyRating" scope="session"/>
<jsp:useBean id="JP" class="CP_Classes.JobPosition" scope="page"/>
<jsp:useBean id="TimeFrame" class="CP_Classes.TimeFrame" scope="session"/>
<jsp:useBean id="RT" class="CP_Classes.RatingTask" scope="page"/>

<%@ page import="CP_Classes.vo.votblTimeFrame"%>
<%@ page import="CP_Classes.vo.votblJobPosition"%>
<%@ page import="CP_Classes.vo.votblSurvRatingTask"%>
<%@ page import="CP_Classes.vo.votblSurvey"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html">
<%@ page pageEncoding="UTF-8"%>
<%// by lydia Date 05/09/2008 Fix jsp file to support Thai language %>
</head>
<SCRIPT LANGUAGE=JAVASCRIPT>

var x = parseInt(window.screen.width) / 2 - 500;  // the number 250 is the exact half of the width of the pop-up and so should be changed according to the size of the pop-up
var y = parseInt(window.screen.height) / 2 - 225;  // the number 125 is the exact half of the height of the pop-up and so should be changed according to the size of the pop-up

function goBack() { window.location = "SurveyDemos.jsp"; }


function goEnd(form) 	
{	
	/*
	 * Change(s) : Added validation code before inserting minimum pass score into database
	 * Reason(s) : To make sure the minimum pass score textbox is not blank and is within the range before insert its value into database 
	 * Updated By: Kian Hwee
	 * Updated On: 02 March 2010
	 */

	//Transfer to javascript function to validate
	//Mark Oei 16 April 2010
	//var minPassScoreTextField = document.getElementById('txtMinPassScore');
	//var currentSelectedRatingScale = document.getElementById('selRatScale');
	//var bToValidateZero = document.getElementById('toValidateZero');
	
	/*if(isNaN(minPassScoreTextField.value)||(bToValidateZero.value=="true" && parseFloat(minPassScoreTextField.value)<1 )||parseFloat(minPassScoreTextField.value)>parseFloat(currentSelectedRatingScale.value))
		{
			alert("Please enter number from 1 to "+currentSelectedRatingScale.value+" at the Minimum Pass Score Field");
		}
		else if (minPassScoreTextField.value ==null || minPassScoreTextField.value =="")
		{
			alert("Minimum Pass Score Field cannot be blank ");
		}
		else*/
	
	var checkCPGapFlag = true;
	checkNumberFlag = checkNumbers();
	if (checkNumberFlag) {
		checkCPGapFlag = validate();
		if (checkCPGapFlag) {
			form.action="SurveyRating.jsp?end=1";
			form.method="post";
			form.submit();
		}
	}
}


function goSave(form, field, field2) 
{ 	
	/*
	 * Change(s) : Added validation code before inserting minimum pass score into database
	 * Reason(s) : To make sure the minimum pass score textbox is not blank and is within the range before insert its value into database 
	 * Updated By: Kian Hwee
	 * Updated On: 02 March 2010
	 */
	//Transfer to javascript function to validate
	//Mark Oei 16 April 2010
	//var currentSelectedRatingScale = document.getElementById('selRatScale');
	//var bToValidateZero = document.getElementById('toValidateZero');

		/*if(isNaN(minPassScoreTextField.value)||(bToValidateZero.value=="true" && parseFloat(minPassScoreTextField.value)<1 )||parseFloat(minPassScoreTextField.value)>parseFloat(currentSelectedRatingScale.value))
		{
			alert("Please enter number from 1 to "+currentSelectedRatingScale.value+" at the Minimum Pass Score Field");
		}
		else if (minPassScoreTextField.value ==null || minPassScoreTextField.value =="")
		{
			alert("Minimum Pass Score Field cannot be blank ");
		}
		else
		{*/
	var checkCPGapFlag = true;
	checkNumberFlag = checkNumbers();
	
	if (checkNumberFlag) {
		if (checkCPGapFlag){
			 //\\Changed by Ha 29/05/08
			if(check(field) && check(field2))
			{
				form.action="SurveyRating.jsp?save=1&end=1";
				form.method="post";
				form.submit();
			}
			else
			{
				alert("<%=trans.tslt("Please complete the Future Job and Time Frame")%>");
			}
		}
	}
		/*}*/
}


function purpose(form,field)
{
	form.action="SurveyRating.jsp?purpose="+field.value;
	form.method="post";
	form.submit();
}
function windowOpen() 
{
	var myWindow=window.open('Survey_RatingList.jsp','windowRef','scrollbars=no, width=950, height=380');
	myWindow.moveTo(x,y);
    myWindow.location.href = 'Survey_RatingList.jsp';
}

function check(field)
{
	var flag = false;
	
	if(field != null)
	{
		for (i = 0; i < field.length; i++) 
		{
			if(field[i].checked)
				flag = true;
			
			if(field[i].selected && flag == false && field[i].value != "")
			{
				flag = true;	
			}
		}
		if(field != null)
		{	
			if(field.checked)
				flag = true;		
		}
	}
	return flag;	
}
 
//Edited by Xuehai 02 Jun 2011. Remove 'void'. Enable to run on different browers like Chrome&Firefox.
//void function remove(form, field)
function remove(form, field)
{
	if(check(field))
	{
		if(confirm("<%=trans.tslt("Delete Assigned Rating Task")%>?"))
		{
			form.action="SurveyRating.jsp?remove=1";
			form.method="post";
			form.submit();
		}
		
	}
	else
	{
		alert("<%=trans.tslt("No record selected")%>");
	}

} 

function editing(form,field)
{
	var data;
	
	for (i = 0; i < field.length; i++) 
	{
		if(field[i].checked)
			data = field[i].value;
	}
	if(field != null)
	{	
		if(field.checked)
		{
			data = field.value;
		}
	}

	form.action="SurveyRating.jsp?edit="+data;
	form.method="post";
	form.submit();

}
function changeRatSca(form, field)
{	
	if(field != null)
	{
		if(confirm("<%=trans.tslt("Changing of scale range will remove the current assignment of rating task to the survey")%>"))
		{
			form.action="SurveyRating.jsp?changeRat=1";
			form.method="post";
			form.submit();
		}else {
			form.action="SurveyRating.jsp";
			form.method="post";
			form.submit();
			return false;
		}	
	}
	else
	{
			form.action="SurveyRating.jsp?changeRat=2";
			form.method="post";
			form.submit();
	}
	
}

function Comp()
{
	var xjob = document.SurveyRating.selJobFuture
	var xtime = document.SurveyRating.selTimeFrame
	
	if(confirm("Save the changes?"))
	{
		if(check(xjob) && check(xtime))
		{
			document.SurveyRating.action="SurveyRating.jsp?proceed=1";
			document.SurveyRating.method="post";
			document.SurveyRating.submit();
		}
		else
		{
			alert("<%=trans.tslt("Please complete the Future Job and Time Frame")%>");
	
		}
	}
	
}

function Detail()
{
	var xjob = document.SurveyRating.selJobFuture
	var xtime = document.SurveyRating.selTimeFrame
	
	if(confirm("<%=trans.tslt("Save the changes")%>?"))
	{
		if(check(xjob) && check(xtime))
		{
			document.SurveyRating.action="SurveyRating.jsp?detail=1";
			document.SurveyRating.method="post";
			document.SurveyRating.submit();
		}
		else
		{
			alert("<%=trans.tslt("Please complete the Future Job and Time Frame")%>");
	
		}
	}
	
}

function Behav()
{
	var xjob = document.SurveyRating.selJobFuture
	var xtime = document.SurveyRating.selTimeFrame

	if(confirm("<%=trans.tslt("Save the changes")%>?"))
	{
		if(check(xjob) && check(xtime))
		{
			document.SurveyRating.action="SurveyRating.jsp?behav=1";
			document.SurveyRating.method="post";
			document.SurveyRating.submit();
		}
		else
		{
			alert("<%=trans.tslt("Please complete the Future Job and Time Frame")%>");
	
		}
	}
}
function Demos()
{
	var xjob = document.SurveyRating.selJobFuture
	var xtime = document.SurveyRating.selTimeFrame

	if(confirm("<%=trans.tslt("Save the changes")%>?"))
	{
		if(check(xjob) && check(xtime))
		{
			document.SurveyRating.action="SurveyRating.jsp?demos=1";
			document.SurveyRating.method="post";
			document.SurveyRating.submit();
		}
		else
		{
			alert("<%=trans.tslt("Please complete the Future Job and Time Frame")%>");
	
		}
	}
}

function advance()
{
	var xjob = document.SurveyRating.selJobFuture
	var xtime = document.SurveyRating.selTimeFrame

	if(confirm("<%=trans.tslt("Save the changes")%>?"))
	{
		if(check(xjob) && check(xtime))
		{
			document.SurveyRating.action="SurveyRating.jsp?adv=1";
			document.SurveyRating.method="post";
			document.SurveyRating.submit();
		}
		else
		{
			alert("<%=trans.tslt("Please complete the Future Job and Time Frame")%>");
	
		}
	}
}

/*
 * Change(s) : Added checkRange method.
 * Reason(s) : To check the value in minimum pass score textbox is within the range
 * Updated By: Kian Hwee
 * Updated On: 02 March 2010
 */
function checkRange(upperRange)
{
	//Rename minPassScoreTextField and use upperLimitTextField and lowerLimitTextField
	//Decrease upperRange by 0.1 as upperLimit for comparison should be lower than upperRange scale
	//Mark Oei 16 April 2010
	//var minPassScoreTextField = document.getElementById('txtMinPassScore');
	var upperLimitTextField = document.getElementById('txtUpperLimit');
	var lowerLimitTextField = document.getElementById('txtLowerLimit');
	
	upperRange = upperRange - 0.1; 
	if ((isNaN(lowerLimitTextField.value)||parseFloat(lowerLimitTextField.value) > upperRange)||(isNaN(upperLimitTextField.value)||parseFloat(upperLimitTextField.value) > upperRange))
	{
		alert("Please enter number between 1 to "+upperRange);
	}
}
/*
 * Change(s) : Added a new function 
 * Reason(s) : To make sure input are numeric before the input are validated 
 * Updated By: Mark Oei
 * Updated On: 16 April 2010
 */
function checkNumbers()
{	
	var insertLimitsTextField = document.getElementById('insertLimits');
	var currentSelectedRatingScale = document.getElementById('selRatScale');
	var lowerLimitTextField = document.getElementById('txtLowerLimit');
	var upperLimitTextField = document.getElementById('txtUpperLimit');
	
	var isNumberFlag = true;
	
	if (insertLimitsTextField.value == "Gap"){
		if (isNaN(lowerLimitTextField.value) || isNaN(upperLimitTextField.value)) {
			alert ("Please enter a number between +/- " + (currentSelectedRatingScale.value-0.1));
			isNumberFlag = false;
		}
	} else if (insertLimitsTextField.value == "CP"){
		if (isNaN(lowerLimitTextField.value) || isNaN(upperLimitTextField.value)) {
			alert ("Please enter a number from 1 to " + (currentSelectedRatingScale.value-0.1));
			isNumberFlag = false;
		} 
	}
	return isNumberFlag;
}
	/*
	 * Change(s) : Added a new function
	 * Reason(s) : To check for valid MinGap, MaxGap anf GapValue 
	 * Updated By: Mark Oei
	 * Updated On: 16 April 2010
	 */
function validate()
{
	var insertLimitsTextField = document.getElementById('insertLimits');
	
	var checkingFlag = true;
	var pattern = new RegExp(/^(\d|-)?(\d)\.?\d*$/);

	var iValid =0;
            x = document.SurveyRating
        	MinGap = x.txtLowerLimit.value
        	MaxGap = x.txtUpperLimit.value
        	GapValue = (MaxGap - MinGap) + 0.01
            Scale = x.txtScaleRange.value
    
        var valMin = pattern.exec(MinGap);
        var valMax = pattern.exec(MaxGap);

    if (insertLimitsTextField.value == "Gap"){
    	if ((MinGap == "") || (valMin == null))
    	{
    		alert("<%=trans.tslt("Please enter Min Gap")%>");
    		checkingFlag = false;
    	} else if ((MaxGap == "")||(valMax == null))
    	{
    		alert("<%=trans.tslt("Please enter Max Gap")%>");
    		checkingFlag = false;
    	} else if(Math.abs(MinGap)>Scale-0.1) 
    	{
    		alert("Please enter a valid Min Gap. It should be between +/- "+(Scale-0.1));
    		checkingFlag = false;
    	} else if(Math.abs(MaxGap)> Scale-0.1)
    	{
    		alert("Please enter a valid Max Gap. It should be less than "+(Scale-0.1));
    		checkingFlag = false;
    	} else if(GapValue < 0)
    	{
    		alert("<%=trans.tslt("Min Gap must be lower than Max Gap value")%>");
    		checkingFlag = false;
    	} else if(GapValue <= 0.2)
    	{
    		alert("<%=trans.tslt("Gap difference must be at least 0.2")%>");
    		checkingFlag = false;
    	}		
    }
    else {
    	if((MinGap == "")||(valMin == null))
    	{
    		alert("<%=trans.tslt("Please enter Lower Limit")%>");
    		checkingFlag = false;
    	} else if((MaxGap == "")||(valMax == null))
    	{
    		alert("<%=trans.tslt("Please enter Upper Limit")%>");
    		checkingFlag = false;
    	} else if(MinGap>(Scale-0.1)) 
    	{
    		alert("Please enter a valid lower limit. It should be greater than 1 and less than "+(Scale-0.1));
    		checkingFlag = false;
    	} else if(MaxGap> (Scale-0.1))
    	{
    		alert("Please enter a valid upper limit. It should be less than "+(Scale-0.1));
    		checkingFlag = false;
    	} else if (MinGap<1 || MaxGap <1)
    	{
    		alert("<%=trans.tslt("Limits must be greater than or equal to 1")%>");
    		checkingFlag = false;    		
    	} else if(GapValue < 0)
    	{
    		alert("<%=trans.tslt("Upper Limit must be higher than Lower Limit")%>");
    		checkingFlag = false;
    	} else if(GapValue <= 0.2)
    	{
    		alert("<%=trans.tslt("Difference between upper limit and lower limit must be at least 0.2")%>");
    		checkingFlag = false;
    	}		
    }
	
 	return checkingFlag;
}
</SCRIPT>

<body>
<%


String username=(String)session.getAttribute("username");
  if (!logchk.isUsable(username)) 
  {%> <font size="2">
   
	<script>
	parent.location.href = "index.jsp";
	</script>
<%  } 



	

  
if(request.getParameter("changeRat") != null)
{
	int changerat = new Integer(request.getParameter("changeRat")).intValue();
	int ratsca = new Integer(request.getParameter("selRatScale")).intValue();
	CE_Survey.setRatScale(ratsca);
	
	if(changerat == 1)
	{
		boolean bIsDeleted = SVR.deleteSurveyRating(CE_Survey.getSurvey_ID());
		
		if(bIsDeleted) {
%>
			<script>
				alert("Deleted successfully");
			</script>

<%			
		}
	}
}
//Changed by Ha 11/06/08
else
{
	CE_Survey.setRatScale(0);
}



if(request.getParameter("remove") != null)
{
	//Added to get scale range
	//Mark Oei 16 April 2010
	int intRange = Integer.parseInt(request.getParameter("selRatScale"));
	int SurveyID = CE_Survey.getSurvey_ID();
	boolean canDel = false;
	int count = 0;
	String [] RatID = request.getParameterValues("chkDel");
	
	
	// Changed by Ha 28/05/08 to pop out the message when action is done successfully
	if(RatID != null)
	{ 
		for(int i=0; i<RatID.length; i++)
		{
			canDel = CE_Survey.delRating(SurveyID,RatID[i]);
			if (canDel)
				count++;
		}
		if (count==RatID.length )
      	{
			//Added to insert default values into database
			//Mark Oei 16 April 2010
			Vector vtRatingTaskInThisPage = SVR.getSurveyRatingTask(CE_Survey.getSurvey_ID());
			String rating = SVR.getRatingTaskName(CE_Survey.getSurvey_ID(),1);
			int lLimit = 0;
			int uLimit = 0;
			//System.out.println("Number of rating tasks " + vtRatingTaskInThisPage.size() + " Rating Name " +rating);
			if((vtRatingTaskInThisPage.size()==1) && (rating.equals("Current Proficiency"))){
				lLimit = (intRange/2) - 1;
				uLimit = (intRange/2) + 1; 
			} else {
				lLimit = -1;
				uLimit = 1;
			}
			boolean isSuccessful = CE_Survey.setMinPassScore(SurveyID, lLimit, uLimit);
			if(isSuccessful)
			{
				System.out.println("Default Limits "+lLimit + ", " + uLimit +" has been inserted successfully");
			}
			session.setAttribute("isSubmitted", 0);
%>
			<script>
                                // Changed by DeZ, 26/06/08, update survey status to Not Commissioned whenever changes are made to survey
				alert("Deleted successfully, survey status has been changed to Non Commissioned, to re-open survey please go to the Survey Detail page");
			</script>
<% 			
        }
	}
}

if(request.getParameter("edit") != null)
{	
	int branch = new Integer(request.getParameter("edit")).intValue();
	CE_Survey.set_SurvRating(branch);
%>
<script>
	var myWindow=window.open('SurveyRating _Edit.jsp','windowRef','scrollbars=no, width=600, height=200');
	myWindow.moveTo(x,y);
    myWindow.location.href = 'SurveyRating _Edit.jsp';
    
</script>
<%
}

if(request.getParameter("end") != null)
{
	/*
	 * Change(s) : Added validation code before inserting minimum pass score into database
	 * Reason(s) : To make sure the minimum pass score textbox is not blank
	 * before insert its value into database 
	 * Updated By: Kian Hwee
	 * Updated On: 02 March 2010
	 */
	int SurveyID = CE_Survey.getSurvey_ID();
	//Added to check whether inserting to CP range or Gap range
	//Mark Oei 16 April 2010
	if(request.getParameter("insertLimits")!=null)
	{
		if ((request.getParameter("txtLowerLimit")!=null)&&(request.getParameter("txtUpperLimit")!=null))
		{
			//Add one more attribute to setMinPassScore method - upperLimit and check that value entered in numeric
			//Change minPassScore to lowerLimit and add upperLimit
			//Mark Oei 16 April 2010
			try {
			double lowerLimit = Double.valueOf(request.getParameter("txtLowerLimit")).doubleValue();
			double upperLimit = Double.valueOf(request.getParameter("txtUpperLimit")).doubleValue();
			boolean isSuccessful = CE_Survey.setMinPassScore(SurveyID, lowerLimit, upperLimit);
			if(isSuccessful)
			{	
				if (request.getParameter("insertLimits").equals("CP"))
					System.out.println("CP Limits "+lowerLimit + ", " + upperLimit +" has been inserted successfully");
				else if (request.getParameter("insertLimits").equals("Gap"))
					System.out.println("Gap Limits "+lowerLimit + ", " + upperLimit +" has been inserted successfully");
			}
			} catch (Exception ex){
				System.out.println("Input error " + ex.getMessage());
			}
		}
	}
	String del = request.getParameter("hid_rat");

		if(del == null)
		{
		%>
			<script>
			if(confirm("<%=trans.tslt("Finish Setup without Rating Task")%>?"))
			{
				window.location = "SurveyList_Create.jsp";
			}
			else
			{
				window.location = "SurveyRating.jsp";
			}
			</script>
<%		}
		else
		{%>
		<script>
			window.location = "SurveyList_Create.jsp";
		</script>
	<%	}	

		
}

if(request.getParameter("adv") != null || request.getParameter("save") != null  || request.getParameter("proceed") != null || request.getParameter("behav") != null || request.getParameter("demos") != null || request.getParameter("detail") != null)
{
	int JobFutureID = new Integer(request.getParameter("selJobFuture")).intValue();
	int TimeFrameID = new Integer(request.getParameter("selTimeFrame")).intValue();
		
	CE_Survey.updateFuture(CE_Survey.getSurvey_ID(),JobFutureID,TimeFrameID,logchk.getPKUser());
	
	if(request.getParameter("proceed") != null)
	{%>
	    <script>
	    //location.href('SurveyCompetency.jsp');
	    location.href='SurveyCompetency.jsp';
	    </script>
<%	}
	else if(request.getParameter("save") != null)
	{	
%>
		<script>
			window.location = "SurveyRating.jsp";
		</script>
<%	}
	else if(request.getParameter("behav") != null)
	{
	%>
		<script>
    //location.href('SurveyKeyBehaviour.jsp');
    location.href='SurveyKeyBehaviour.jsp';
    </script>
			
	<%
	}
	else if(request.getParameter("demos") != null)
	{
	%>
		<script>
    //location.href('SurveyDemos.jsp');
    location.href='SurveyDemos.jsp';
    </script>
			
	<%
   	}
   	else if(request.getParameter("adv") != null)
	{
	%>
		<script>
    //location.href('AdvanceSettings.jsp');
    location.href='AdvanceSettings.jsp';
    </script>
			
	<%
   	}
   	else if(request.getParameter("detail") != null)
   	{	%>
   		<script>
    //location.href('SurveyDetail.jsp');
    location.href='SurveyDetail.jsp';
    </script>
<%	}


}


%>
<form name="SurveyRating" action="SurveyRating.jsp" method="post">
<table border="0" width="80%" cellspacing="0" cellpadding="0" bordercolor="#000000" style="border-width: 0px">
	<tr>
		
		<td width="114" style="border-style: none; border-width: medium">
		<p align="center">
		<font face="Arial" style="font-size: 10pt; font-weight: 700">
		<a href="SurveyDetail.jsp" style="cursor:pointer"><%=trans.tslt("Survey Detail")%></a></font></td>
		<td width="28" style="border-style: none; border-width: medium"><b>
		<font size="2">
		<img border="0" src="images/gray_arrow.gif" width="19" height="26"></font></b></td>
		
		<td width="101" style="border-style: none; border-width: medium">
		<p align="center">
		<font face="Arial" style="font-size: 10pt; font-weight: 700" color=blue><u>
		<a href="SurveyCluster.jsp" style="cursor:pointer"><%=trans.tslt("Cluster")%></a></u></font></td>
		
		<td width="28" style="border-style: none; border-width: medium"><b>
		<font size="2">
		<img border="0" src="images/gray_arrow.gif" width="19" height="26"></font></b></td>
		<td width="101" style="border-style: none; border-width: medium">
		<p align="center">
		<font face="Arial" style="font-size: 10pt; font-weight: 700">
		<a href="SurveyCompetency.jsp" style="cursor:pointer"><%=trans.tslt("Competency")%></a></font></td>
		<td width="20" style="border-style: none; border-width: medium">
		<p align="center"><b>
		<font size="2">
		<img border="0" src="images/gray_arrow.gif" width="19" height="26"></font></b></td>
		<td width="113" style="border-style: none; border-width: medium">
		<p align="center"><b><font face="Arial" size="2">
		<a href="SurveyKeyBehaviour.jsp" style="cursor:pointer"><%=trans.tslt("Key Behaviour")%></a></font></b></td>
		<td width="24" style="border-style: none; border-width: medium">
		<p align="center"><b>
		<font size="2">
		<img border="0" src="images/gray_arrow.gif" width="19" height="26"></font></b></td>
		<td width="109" style="border-style: none; border-width: medium">
		<p align="center"><b><font face="Arial" size="2">
		<a href="SurveyDemos.jsp" style="cursor:pointer"><%=trans.tslt("Demographics")%></a></font></b></td>
		<td width="23" style="border-style: none; border-width: medium"><b>
		<img border="0" src="images/gray_arrow.gif" width="19" height="26"></b></td>
		<td width="130" style="border-style: none; border-width: medium">
		<p align="center"><b><font face="Arial" size="2" color="#CC0000"><%=trans.tslt("Rating Task")%></font></b></td>
	</tr>
	<tr>
		<td width="114" style="border-style: none; border-width: medium">&nbsp;
		</td>
		<td width="28" style="border-style: none; border-width: medium">&nbsp;</td>
		<td width="101" style="border-style: none; border-width: medium">&nbsp;
		</td>
		<td width="28" style="border-style: none; border-width: medium">&nbsp;</td>
		<td width="101" style="border-style: none; border-width: medium">&nbsp;
		</td>
		<td width="20" style="border-style: none; border-width: medium">&nbsp;
		</td>
		<td width="113" style="border-style: none; border-width: medium">&nbsp;
		</td>
		<td width="24" style="border-style: none; border-width: medium">&nbsp;
		</td>
		<td width="109" style="border-style: none; border-width: medium">&nbsp;
		</td>
		<td width="23" style="border-style: none; border-width: medium">&nbsp;</td>
		<td width="130" style="border-style: none; border-width: medium">
		<p align="center">&nbsp;
		<font size="2">
   
		<span style="font-weight: 700">
		<font face="Arial" style="font-size: 10pt; font-weight: 700" color=blue><u>
		<a href ="AdvanceSettings.jsp" style="cursor:pointer"><%=trans.tslt("Advanced Settings")%></a></u></font></span></td>
	</tr>
	<tr height="30">
		<td width="114" style="border-style: none; border-width: medium">&nbsp;</td>
		<td width="28" style="border-style: none; border-width: medium">&nbsp;</td>
		<td width="101" style="border-style: none; border-width: medium">&nbsp;
		</td>
		<td width="20" style="border-style: none; border-width: medium">&nbsp;
		</td>
		<td width="101" style="border-style: none; border-width: medium">&nbsp;
		</td>
		<td width="20" style="border-style: none; border-width: medium">&nbsp;
		</td>
		<td width="113" style="border-style: none; border-width: medium">&nbsp;
		</td>
		<td width="24" style="border-style: none; border-width: medium">&nbsp;
		</td>
		<td width="109" style="border-style: none; border-width: medium">&nbsp;
		</td>
		<td width="23" style="border-style: none; border-width: medium">&nbsp;</td>
		<td width="135" style="border-style: none; border-width: medium">
		<p align="center"><b><font face="Arial" size="2">
		<a href="SurveyPrelimQ.jsp?entry=1" style="cursor:pointer"><%= trans.tslt("Preliminary Questions") %></a></font></b></td>
	</tr>
	<tr height="30">
		<td width="114" style="border-style: none; border-width: medium">&nbsp;</td>
		<td width="28" style="border-style: none; border-width: medium">&nbsp;</td>
		<td width="101" style="border-style: none; border-width: medium">&nbsp;
		</td>
		<td width="28" style="border-style: none; border-width: medium">&nbsp;</td>
		<td width="101" style="border-style: none; border-width: medium">&nbsp;
		</td>
		<td width="20" style="border-style: none; border-width: medium">&nbsp;
		</td>
		<td width="113" style="border-style: none; border-width: medium">&nbsp;
		</td>
		<td width="24" style="border-style: none; border-width: medium">&nbsp;
		</td>
		<td width="109" style="border-style: none; border-width: medium">&nbsp;
		</td>
		<td width="23" style="border-style: none; border-width: medium">&nbsp;</td>
		<td width="135" style="border-style: none; border-width: medium">
		<p align="center"><b><font face="Arial" size="2">
		<a href="AdditionalQuestions.jsp?entry=1" style="cursor:pointer"><%= trans.tslt("Additional Questions") %></a></font></b></td>
	</tr>
		
	<tr>
		<td width="791" style="border-style: none; border-width: medium" colspan="9">
		<ul>
			<li><font face="Arial" size="2"><%=trans.tslt("To Edit Rating Task name, click on the relevant radio button under Edit")%>.</font></li>
			<li><font face="Arial" size="2"><%=trans.tslt("To Remove Rating Task(s), tick on the checkbox(es) under Remove")%>.</font></li>
		</ul>
		<table border="0" width="100%" cellspacing="0" cellpadding="0">
			<tr>
				<td> <font size="2">
   
				<b><font face="Arial" size="2"><%=trans.tslt("Select Scale Range")%>:</font></b><td width="75%">
<%				
	if(CE_Survey.Allow_SurvChange(CE_Survey.getSurvey_ID()))
	{ 	%>				
		<select size="1" name="selRatScale" onchange="return changeRatSca(this.form,this.form.chkDel)">	
<%	}
	else		
	{	%>
		<select size="1" name="selRatScale" onchange="return changeRatSca(this.form,this.form.chkDel)" disabled>	
<%	}
	//Comment off by Ha 10/06/08 use scaleRange instead	is scale range = 0	
	if(CE_Survey.getRatScale() == 0)				
		CE_Survey.setRatScale(SVR.getSurveyScaleRange(CE_Survey.getSurvey_ID()));
	
%>				
				
			<% for(int i = 3; i< 11; i++)
			{
				if(CE_Survey.getRatScale() == i)
				{%>
					<option value=<%=i%> selected><%=i%></option>
		<%		}
				else
				{%>
					<option value=<%=i%>><%=i%></option>	
		<%		}							
			}%>				
			
			
			
			</select></td></td>
			</tr>
			
<%
if(request.getParameter("setting") != null)
{
	int ratsca = new Integer(request.getParameter("selRatScale")).intValue();
	CE_Survey.setRatScale(ratsca);
	%>
	<Script>
		//location.href('RatingSetup.jsp');
		location.href='RatingSetup.jsp';
	</Script>
	<%
}	
%>			
			<tr>
				<td><td width="75%">&nbsp;</td>
			</tr>
		</table>
		</td>
	</tr>
</table>
<% 	// 	Remove height:192px to reduce the white space between the tables 
	//	Mark Oei 16 April 2010  %>
<div style='width:617px; z-index:1; overflow:auto'>  
		<table border="1" width="610" bordercolor="#3399FF">
		<tr>
			<td colspan="5" bgcolor="#000080">
			<p align="center"><span style="font-weight: 700">
			<font face="Arial" color="#FFFFFF" size="2">
			<%=trans.tslt("Rating Task")%></font></span></td>
		</tr>
		<tr>
			<%
			if(CE_Survey.Allow_SurvChange(CE_Survey.getSurvey_ID()))
			{ 	%>	
				<td bgcolor="#000080" width="3%" align="center">
				<span style="font-weight: 700">
				<font face="Arial" size="2" color="#FFFFFF"><%=trans.tslt("Remove")%> </font>
				</span></td>
				<td bgcolor="#000080" width="5%" align="center">
				<span style="font-weight: 700">
				<font face="Arial" color="#FFFFFF" size="2">
				<%=trans.tslt("Edit")%></font></span></td>
			<%	}	%>
				<td bgcolor="#000080" width="27%" align="center">
				<span style="font-weight: 700">
				<font face="Arial" color="#FFFFFF" size="2">
				<%=trans.tslt("Rating Task Code")%></font></span></td>
				<td bgcolor="#000080" width="27%" align="center">
				<span style="font-weight: 700">
				<font face="Arial" color="#FFFFFF" size="2">
				<%=trans.tslt("Rating Task Name")%></font></span></td>
				<td bgcolor="#000080" width="56%" align="center">
				<span style="font-weight: 700">
				<font face="Arial" color="#FFFFFF" size="2">
				<%=trans.tslt("Scale Description")%></font></span></td>
		</tr>
<%	
	int JobFutureID=0;
	int TimeFrameID=0;
	int orgID = 0;
	
	Vector v = SVR.getSurveyRatingTask(CE_Survey.getSurvey_ID());
	
	for(int i=0; i<v.size(); i++)
	{
		votblSurvRatingTask vo = (votblSurvRatingTask)v.elementAt(i);
		int RatID = vo.getFKRatingTaskID(); //This is RatingTaskID in table tblRatingTask
		String RatName = vo.getSurvRatingTask(); //This is RatingTask in table tblRatingTask
		String ScaleDesc = vo.getScaleDescription();
		String RTCode = RT.getRTCode(RatID);
		
%>		
		  <tr onMouseOver = "this.bgColor = '#99ccff'"
			  onMouseOut = "this.bgColor = '#FFFFFF'">
				
		<%
			if(CE_Survey.Allow_SurvChange(CE_Survey.getSurvey_ID()))
			{ 	%>	
				<td bgcolor="#FFFFCC" width="3%" align="left">			
				<p align="center"><font face="Arial"><input type="checkbox" name="chkDel" value=<%=RatID%>></font></td>

				<td bgcolor="#FFFFCC" width="5%" align="left">
				<p align="center">
				<font face="Arial">
				<input type="radio" value=<%=RatID%> name="radEdit" onClick="editing(this.form,this.form.radEdit)"></font>
		<%	}	%>
				</td>
				<td bgcolor="#FFFFCC" width="27%" align="left">
				<font face="Arial" size="2"><%=RTCode%></FONT></td>
				
				<td bgcolor="#FFFFCC" width="27%" align="left">
				<font face="Arial" size="2"><%=RatName%></FONT></td>
				
				<td bgcolor="#FFFFCC" width="56%" align="left">
				<font face="Arial" size="2"><%=ScaleDesc%></FONT></td>
				<input type="hidden" value="<%=RatID%>" name="hid_rat">
		</tr>
<%
		} // End of for loop %>
		
		</table>
</div>

<% 
	/*
	 * Change(s) : Added code to check whether the only Rating Task is a Current Proficiency(CP).
	 * Reason(s) : Display the option to input minimum pass score when the only Rating Task is a Current Proficiency(CP).
	 * Updated By: Kian Hwee
	 * Updated On: 02 March 2010
	 */
	votblSurvey voGap = CE_Survey.getSurveyDetail(CE_Survey.getSurvey_ID());
	//Add to get current values for display, 
	//Mark Oei 16 April 2010
	float MinGap = voGap.getMIN_Gap();	
	float MaxGap = voGap.getMAX_Gap();
	String title = "";
	String CPGap = "";
	
	int intScaleRange= CE_Survey.getRatScale();
	%>
	<input type="hidden" name="txtScaleRange" size="2" value ="<%=intScaleRange%>">
	<%
	Vector vtRatingTaskInThisPage = SVR.getSurveyRatingTask(CE_Survey.getSurvey_ID());
	String ratingName = SVR.getRatingTaskName(CE_Survey.getSurvey_ID(),1);
	
	//if only one Rating Task exists
	session.setAttribute("taskCount", 0);
	if (vtRatingTaskInThisPage.size()!=0){
		session.setAttribute("taskCount", vtRatingTaskInThisPage.size());
		
		//System.out.println("TaskCount " + session.getAttribute("taskCount") + " " +session.getAttribute("ratingName"));
		if((vtRatingTaskInThisPage.size()==1) && ratingName.equals("Current Proficiency"))
		{
			title = "Current Proficiency Range Definition";
			CPGap = "CP";
			//Commented off as this will be handled by the CP Range Definition Table
			//Mark Oei 16 April 2010
			//votblSurvRatingTask onlyRT = (votblSurvRatingTask)vtRatingTaskInThisPage.elementAt(0);
			//String currentMinPassScore = Double.toString(CE_Survey.getMinPassScore(CE_Survey.getSurvey_ID()));
			//if only Rating Task is a Current Proficency (CP)
			//if(onlyRT.getFKRatingTaskID()==1)
			//{
				//if(currentMinPassScore.substring(0,1).equals("0") )
				//{
					//currentMinPassScore="";
				//}
	%>
		<% // Add a table for Current Proficiency Range Definition, Mark Oei, 16 April 2010 %>
		<input value="CP" type="hidden" name="insertLimits">
		
   		<font face="Arial" size="2"> 
   		<% 	//<!-- Commented off this will handled by CP Range Definition Table
   			//Mark Oei 16 April 2010 
   			//Minimum Pass Score<sup>*</sup>: <input size="2" value="currentMinPassScore" maxlength="5" type="text" id ="txtMinPassScore" name="txtMinPassScore" onblur="checkRange(intScaleRange)">
		//<br>
		//<br>
   		//* Competencies with CP(All) < Minimum Pass Score will be considered as development areas.</FONT>%>
   		<%	//Used to enable validation since CP is only Rating Task
   		 	//<input value="true" type="hidden" id ="toValidateZero" name="toValidateZero">
   			//<br>
   		
	   	// *********line added to make easier to veiw the table between CP and Gap ************* 
   		
   		// }//Commented off as this is handled by CP Range Definition
    		//Mark Oei 16 April 2010  
   			// else { 
   			//System.out.println("Only Rating Task not a CP, Minimum Pass Score changed to 0.");
			//<input value="0.0" type="hidden" id ="txtMinPassScore" name="txtMinPassScore">
			//Used to disable validation since CP is not only Rating Task 
			//<input value="false" type="hidden" id ="toValidateZero" name="toValidateZero">%>
			<%	
		} else {
		//Added to display the Gap Definition Table
		//Mark Oei 16 April 2010
			title = "Gap Definition";
			CPGap = "Gap";
		%>
			<input value="Gap" type="hidden" name="insertLimits">
		<% } // End of checking Number of rating task = 1 and rating name = current proficiency
		%>
		<table border="1" width="610px" bordercolor="#3399FF">
		<tr>
			<font size="2">
				<td style="border-left-style: solid; border-left-width: 1px; border-right-style: solid; border-right-width: 1px; border-bottom-style: none; border-bottom-width: medium; " bordercolor="#3399FF" colspan="4" bgcolor="#FFFFCC">
				<font size="2">
				<p align="center">
				<b>
				<font face="Arial" size="2"><%=trans.tslt(title)%></font></b>&nbsp;</td>
		</tr>
		<tr>
			<td width="4%" style="border-left-style: solid; border-left-width: 1px; border-right-style: none; border-right-width: medium; border-bottom-style: none; border-bottom-width: medium; border-top-style:none; border-top-width:medium" rowspan="3" bordercolor="#3399FF" bgcolor="#FFFFCC">&nbsp;
			</td>
			<td style="border-style:none; border-width:medium; " width="216" bgcolor="#CCCCFF" bordercolor="#3399FF">
			<font face="Arial" size="2"><%=trans.tslt("Meet Expectation is when")%>&nbsp; </font></td>
			<td style="border-style:none; border-width:medium; " bgcolor="#CCCCFF" bordercolor="#3399FF">
			<font size="2">
			<% if(CE_Survey.getSurveyStatus() != 2)
				{
					if(CE_Survey.Allow_SurvChange(CE_Survey.getSurvey_ID()))
					{  %>               
						<font face="Arial" style="font-size: 11pt" size="2">
							<input type="text" name="txtLowerLimit" size="2" value ="<%=MinGap%>" onblur="checkRange(<%=intScaleRange%>)"></font>
						<font face="Arial" size="2">&nbsp; &lt; &nbsp; <%=trans.tslt(CPGap)%>&nbsp; &lt;=&nbsp;  </font>
						<font face="Arial" style="font-size: 11pt" size="2">   
							<input type="text" name="txtUpperLimit" size="2" value ="<%=MaxGap%>" onblur="checkRange(<%=intScaleRange%>)"></font>
						</font>
				<%	}  else { %>             
						<font face="Arial" style="font-size: 11pt" size="2">
						<input type="text" name="txtLowerLimit" size="2" value ="<%=MinGap%>" disabled></font>
						<font face="Arial" size="2">&nbsp; &lt; &nbsp; <%=trans.tslt(CPGap)%>&nbsp; &lt;=&nbsp;  </font>
						<font face="Arial" style="font-size: 11pt" size="2">   
						<input type="text" name="txtUpperLimit" size="2" value ="<%=MaxGap%>" disabled></font>
				<%	}	 // End of Allow_SurvChange if statement
				} else { %>
					<font face="Arial" style="font-size: 11pt" size="2">
					<input type="text" name="txtLowerLimit" size="2" value ="<%=MinGap%>" disabled></font>
					<font face="Arial" size="2">&nbsp; &lt; &nbsp; <%=trans.tslt(CPGap)%>&nbsp; &lt;=&nbsp;  </font>
					<font face="Arial" style="font-size: 11pt" size="2">   
					<input type="text" name="txtUpperLimit" size="2" value ="<%=MaxGap%>" disabled></font>
				<% } // End of getSurveyStatus if statement
		%>	</td> <% // End of Meeting Expectations column %>
			<td style="border-left-style:none; border-left-width:medium; border-right-style:solid; border-right-width:1px; border-bottom-style:none; border-bottom-width:medium; border-top-style:none; border-top-width:medium" bordercolor="#3399FF" rowspan="3" bgcolor="#FFFFCC">&nbsp;
			</td>
		</tr>
		<tr>
			<td style="border-style: none; border-width: medium" width="216" bgcolor="#FFCCCC" bordercolor="#3399FF">
			<font face="Arial" size="2"><%=trans.tslt("Strength is when")%> </font></td>
			<td style="border-style:none; border-width:medium; " bgcolor="#FFCCCC" bordercolor="#3399FF">
			<font size="2">
				<font face="Arial" size="2"><%=trans.tslt(CPGap)%>&nbsp; &gt;&nbsp; <%=MaxGap%> &nbsp;</font></td>
			</tr>
			<tr>
				<td style="border-style: none; border-width: medium" width="216" bgcolor="#CCFFCC" bordercolor="#3399FF">
				<font face="Arial" size="2">
				<%=trans.tslt("Developmental Area is when")%> </font></td>
				<td style="border-style:none; border-width:medium; " bgcolor="#CCFFCC" bordercolor="#3399FF">
				<font size="2">
				<font face="Arial" size="2"><%=trans.tslt(CPGap)%>&nbsp; &lt;=&nbsp; <%=MinGap%> &nbsp;</font></td>
			</tr>
			<tr>
				<td style="border-left-style: solid; border-left-width: 1px; border-right-style: solid; border-right-width: 1px; border-bottom-style: solid; border-bottom-width: 1px; border-top-style:none; border-top-width:medium" bordercolor="#3399FF" colspan="4" bgcolor="#FFFFCC">&nbsp;
				</td>
			</tr>
		</table> <% //End of CP Range / Gap Defintion table %>
		<%
		} // End of number of rating task = 0 if statement
	//}// else {
		// Commented off as this will be handled by CP Range Definition
		// Mark Oei 16 April 2010
		// boolean bIsMinPassScoreToZero=CE_Survey.setMinPassScore(CE_Survey.getSurvey_ID(),0,0);
		// if(bIsMinPassScoreToZero)
		//{ System.out.println("Zero or more than one Rating Task, Minimum Pass Score changed to lower limit."); 
		//	<input value="0.0" type="hidden" id ="txtMinPassScore" name="txtMinPassScore">
			//Used to disable validation since CP is not only Rating Task 
			//<input value="false" type="hidden" id ="toValidateZero" name="toValidateZero">
		 //} %>
		
<table border="0" width="610" cellspacing="0" cellpadding="0">

	<tr>
		<td colspan="5"> <font size="2">
   
	<table border="0" width="610">
		<tr>
		<%
			boolean isFuture = false;		
			boolean bExist = SVR.existSurveyRating(CE_Survey.getSurvey_ID());
			
			if(bExist)
			{		
				isFuture = true;
	%>
			<td height="27">
				<font face="Arial" style="font-size: 10pt"><%=trans.tslt("Future Job")%>:</font></td>
			<td width="42%" height="27"><font face="Arial">
				<span style="font-size: 11pt">
				<%	if(CE_Survey.Allow_SurvChange(CE_Survey.getSurvey_ID()))
					{	%>				
						<select size="1" name="selJobFuture">
					<%	}
					else
					{
						%>
						<select size="1" name="selJobFuture" disabled>
				<%	}	%>			
					
					<option selected>&nbsp</option>
<%				
				votblSurvey voSurvey = CE_Survey.getSurveyDetail(CE_Survey.getSurvey_ID());
		
				if(voSurvey != null)
				{
					JobFutureID = voSurvey.getJobFutureID();	
					TimeFrameID = voSurvey.getTimeFrameID();
					orgID = voSurvey.getFKOrganization();
				}	
	
	
	
				Vector vJobPos = JP.getAllJobPositions(CE_Survey.get_survOrg());
	
				for(int i=0; i<vJobPos.size(); i++)
				{
					votblJobPosition voJob = (votblJobPosition)vJobPos.elementAt(i);
					int JobPosition_ID2 = voJob.getJobPositionID();
					String JobPosition_Desc2 = voJob.getJobPosition();
	
					if(JobFutureID == JobPosition_ID2)
					{
				%>		<option value=<%=JobPosition_ID2%> selected><%=JobPosition_Desc2%></option>
				<%	}
					else	{
				%>		<option value=<%=JobPosition_ID2%>><%=JobPosition_Desc2%></option>
				<%	}	
				}	// End of for loop
	%>
				</select></span></font></td>
				<td height="27">
				<font face="Arial" style="font-size: 10pt" size="2"><%=trans.tslt("Time Frame")%>:</font></td>
				<td width="20%" height="27"><font face="Arial">
				<span style="font-size: 11pt">
<%	if(CE_Survey.Allow_SurvChange(CE_Survey.getSurvey_ID()))
	{	%>				
			<select size="1" name="selTimeFrame">
<%	}
	else
	{
%>
			<select size="1" name="selTimeFrame" disabled>
<%	}	%>					
	
	<option selected>&nbsp;</option>
<%
	 Vector vTime = TimeFrame.getAllTimeFrames(logchk.getOrg());
	 
	 for(int i=0; i<vTime.size(); i++) {
	  
		votblTimeFrame vo = (votblTimeFrame)vTime.elementAt(i);
		
		int db_TimeFrameID = vo.getTimeFrameID();
		String TimeFrame_Desc = vo.getTimeFrame();
		
		if(TimeFrameID == db_TimeFrameID)
		{
%>			<option value=<%=db_TimeFrameID%> selected><%=TimeFrame_Desc%></option>
<%		}
		else
		{
%>			<option value=<%=db_TimeFrameID%>><%=TimeFrame_Desc%></option>
<%		}	
		

	}
}	
else
{
	%>
	<td>&nbsp</td>
<%	}	%>				
				</select></span></font></td>
		</tr>
		<tr>
		<td height="27">&nbsp;
				</td>
				<td width="42%" height="27">&nbsp;</td>
				<td width="103" height="27">&nbsp;
				</td>
				<td width="20%" height="27">&nbsp;</td>
		</tr>
</table>
		
	</tr>
	<tr>
		<td width="20%">
		<input type="button" value="<%=trans.tslt("Back")%>" name="btnBack" style="float: left" onclick="goBack()"></td>
		<%
		if(CE_Survey.getSurveyStatus() != 2)
		{

			if(CE_Survey.Allow_SurvChange(CE_Survey.getSurvey_ID()))
			{ 	%>
				<td width="26%">
				<input type="button" value="<%=trans.tslt("Add Rating Task")%>" name="btnAdd" style="float: right" onclick="windowOpen()"></td>
				<td width="24%">
				<input type="button" value="<%=trans.tslt("Remove")%>" name="btnDel" style="float: right" onclick="remove(this.form, this.form.chkDel)"></td>
				<%	
			}	
		}
		
		if(CE_Survey.Allow_SurvChange(CE_Survey.getSurvey_ID()) && isFuture == true)
		{ 
			if(JobFutureID != 0)
			{			
%>
				<td width="30%">
				<input type="button" value="<%=trans.tslt("Save")%> &amp; <%=trans.tslt("Finish")%>" name="btnEnd" style="float: right" onclick="goSave(this.form, this.form.selJobFuture, this.form.selTimeFrame)"></td>
<%
			}	
			else
			{	
%>			
				<td width="30%">
				<input type="button" value="<%=trans.tslt("Save")%> &amp; <%=trans.tslt("Finish")%>" name="btnEnd" style="float: right" onclick="goSave(this.form, this.form.selJobFuture, this.form.selTimeFrame, this.form.chkDel)"></td>
<%			}	
		}
		else
		{				
			%>
				<td width="30%">
				<input type="button" value="<%=trans.tslt("Save")%> &amp; <%=trans.tslt("Finish")%>" name="btnEnd" style="float: right" onclick="goEnd(this.form)"></td>
<%		}	%>
	</tr>
</table>
</form>

<p></p>
<%@ include file="Footer.jsp"%>
	</body>
	</html>
  