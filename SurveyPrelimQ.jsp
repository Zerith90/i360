<%@ page import="java.sql.*,
				java.util.*,
                 java.io.* "%>  

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<jsp:useBean id="logchk" class="CP_Classes.Login" scope="session"/>        
<jsp:useBean id="trans" class="CP_Classes.Translate" scope="session"/>
<jsp:useBean id="PrelimQController" class="CP_Classes.PrelimQuestionController" scope="session"/>
<jsp:useBean id="CE_Survey" class="CP_Classes.Create_Edit_Survey" scope="session"/>
<jsp:useBean id="PrelimQuestionBean" class="CP_Classes.PrelimQuestionBean" scope="session"/>
<%@ page import="CP_Classes.PrelimQuestion"%>
<%@ page import="CP_Classes.PrelimQuestionAns"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>AdditionalQuestions</title>
</head>
<SCRIPT LANGUAGE=JAVASCRIPT>

function Save(form){
		form.action="SurveyPrelimQ.jsp?save=1";
		form.method="post";
		form.submit();	
}

/*
function checkRatingScaleChanged(sel, ratingScaleID){
	//int value = parseInt(sel.options[sel.selectedIndex].value);
	/*if(isNan(value)){
		return false;
	}
	if(value == ratingScaleID){
		return false;
	}
	return true;
}*/


function Add(form){
		form.action="SurveyPrelimQ.jsp?add=1&save=2";
		form.method="post";
		form.submit();
}

function RatingScale(form) {
	myWindow=window.open('PrelimQ_AddRatingScale.jsp','windowRef','scrollbars=no, width=850, height=800');
	myWindow.moveTo(50,50);
    myWindow.location.href = 'PrelimQ_AddRatingScale.jsp';
}

function Delete(form, qid)
{
	if(confirm("<%=trans.tslt("Are you sure you want to delete this question")%>?"))
	{
		form.action="SurveyPrelimQ.jsp?delete="+qid;
		form.method="post";
		form.submit();
	}	
}

function ChangeRating(form,textbox,qid){
	if(textbox.value == ""){
		alert("Please enter valid input");
	}
	else if(confirm("<%=trans.tslt("Are you sure you want to change the rating scale for this question")%>?")){
		form.action="SurveyPrelimQ.jsp?changeRating="+qid+"&value="+textbox.value;
		form.method="post";
		form.submit();
	}
}


</SCRIPT>
<body>
<%
String username=(String)session.getAttribute("username");

  if (!logchk.isUsable(username)){
%> <font size="2">   
   <script>
	parent.location.href = "index.jsp";
	</script>
<%}		
	
	int SurveyID = CE_Survey.getSurvey_ID();
	System.out.println(SurveyID);
	Vector<PrelimQuestion> v = PrelimQController.getQuestions(SurveyID);
	if(request.getParameter("add") != null)
	{
		PrelimQuestionBean.setQuestionCount(PrelimQuestionBean.getQuestionCount()+1);
	}
	
	if(request.getParameter("entry") != null)
	{
		PrelimQuestionBean.setQuestionCount(v.size());
	}
	int QuestionCount = PrelimQuestionBean.getQuestionCount();
	if(request.getParameter("save") != null)
	{
		if(request.getParameter("header")!=null)
		{
			String header = PrelimQController.escapeInvalidChars(request.getParameter("header"));
			//save the header for the additional questions 
			if(!PrelimQController.getPrelimQnHeader(SurveyID).equals(""))
			{
				//header already exists update the header
				PrelimQController.updatePrelimQnHeader(SurveyID, header);
				
			}
			else
			{
				//header does not exist insert the new header
				PrelimQController.savePrelimQnHeader(SurveyID, header);
			
			}
		}
		
		
		//check and save each questions
		for(int i=0;i<QuestionCount+1;i++)
		{
			String qn = request.getParameter("qn"+i);
			if (qn!=null)
			{
				String qnid = request.getParameter("qid"+i);
				if (qnid!=null)
				{
					if(!qn.equals(""))
					{
						if(qnid.equals("0"))
						{
							if(request.getParameter("changeID"+i) != "")
								PrelimQController.saveQuestion(SurveyID, qn, Integer.parseInt(request.getParameter("changeID"+i)));
							else{
								PrelimQController.saveQuestion(SurveyID, qn, PrelimQController.getPrelimRatingID(Integer.parseInt(qnid)));
							}
						}
						else
						{
							if(request.getParameter("changeID"+i) != "")
								PrelimQController.updateQuestion(Integer.parseInt(qnid), SurveyID, qn, Integer.parseInt(request.getParameter("changeID"+i)));
							else{
								PrelimQController.updateQuestion(Integer.parseInt(qnid), SurveyID, qn, PrelimQController.getPrelimRatingID(Integer.parseInt(qnid)));
							}
						}
					}
				}
			}
		}
		
		if(request.getParameter("save").equals("1"))
		{
		%>
		<Script>
		alert("Saved successfully")
		location.href='SurveyPrelimQ.jsp?entry=1';
		</Script>
		<%
		}
		else
		{
			v = PrelimQController.getQuestions(SurveyID);
		}
		//end of save
	}
	

	
	if(request.getParameter("delete") != null)
	{
		int delqid = Integer.parseInt(request.getParameter("delete"));
		int confirm = 0;
		if(request.getParameter("confirm") != null){
			confirm = Integer.parseInt(request.getParameter("confirm"));
		}
		if(PrelimQController.checkHavingAns(delqid) && confirm != 1){
			%>
			<Script>
			if(confirm("<%=trans.tslt("Answers related to this question will be removed, are you sure")%>?"))
			{
				location.href='SurveyPrelimQ.jsp?confirm=1&delete='+<%=delqid%>;
			}
			</Script>
			<%
		}else{
			PrelimQController.deleteQuestion(delqid);
			%>
			<Script>
			alert("Deleted successfully")
			location.href='SurveyPrelimQ.jsp?entry=1';
			</Script>
			<%
		}
	
	}
	if(request.getParameter("changeRating") != null)
	{
		int qid = Integer.parseInt(request.getParameter("changeRating"));
		int scaleID = Integer.parseInt(request.getParameter("value"));
		if(PrelimQController.checkRatingScaleExist(scaleID)){
			PrelimQController.changeRatingScaleID(qid,scaleID);
			%>
			<Script>
			alert("Changed successfully")
			</Script>
			<%
		}
		else {
		%>
		<Script>
		alert("The entered scale ID does not exist!")
		</Script>
		<%			
		}
		
	
	}
	
	%>


<font size="2">
   
<table border="0" width="78%" cellspacing="0" cellpadding="0" bordercolor="#000000" style="border-width: 0px">
	<tr>
		<td width="114" style="border-style: none; border-width: medium">
		<p align="center">
		<font face="Arial" style="font-size: 10pt; font-weight: 700">
		<a href="SurveyDetail.jsp" style="cursor:pointer"><%= trans.tslt("Survey Detail") %></a></font></td>
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
		<font face="Arial" style="font-size: 10pt; font-weight: 700" size="2">
		<a href="SurveyCompetency.jsp" style="cursor:pointer"><%= trans.tslt("Competency") %></a></font></td>
		<td width="20" style="border-style: none; border-width: medium">
		<p align="center"><b>
		<font size="2">
		<img border="0" src="images/gray_arrow.gif" width="19" height="26"></font></b></td>
		<td width="113" style="border-style: none; border-width: medium">
		<p align="center"><b><font face="Arial" size="2">
		<a href="SurveyKeyBehaviour.jsp" style="cursor:pointer"><%= trans.tslt("Key Behaviour") %></a></font></b></td>
		<td width="24" style="border-style: none; border-width: medium">
		<p align="center"><b>
		<font size="2">
		<img border="0" src="images/gray_arrow.gif" width="19" height="26"></font></b></td>
		<td width="109" style="border-style: none; border-width: medium">
		<p align="center"><b><font face="Arial" size="2">
		<a href="SurveyDemos.jsp" style="cursor:pointer"><%= trans.tslt("Demographics") %></a></font></b></td>
		<td width="23" style="border-style: none; border-width: medium"><b>
		<font size="2">
		<img border="0" src="images/gray_arrow.gif" width="19" height="26"></font></b></td>
		<td width="135" style="border-style: none; border-width: medium">
		<p align="center"><b><font face="Arial" size="2">
		<a href="SurveyRating.jsp" style="cursor:pointer"><%= trans.tslt("Rating Task") %></a></font></b></td>
	</tr>
	<tr>
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
		<a href="AdvanceSettings.jsp" style="cursor:pointer"><%= trans.tslt("Advanced Settings") %></a></font></b>
		
		</td>
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
		<p align="center">&nbsp; <font size="2">
   
		<span style="font-weight: 700">
		<font face="Arial" style="font-size: 10pt; font-weight: 700" color="#CC0000"><%= trans.tslt("Preliminary Questions") %></font></span>
		</td>
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
		<a href="AdditionalQuestions.jsp?entry=1" style="cursor:pointer"><%= trans.tslt("Additional Questions") %></a></font></b></td>
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
		<td width="135" style="border-style: none; border-width: medium">&nbsp;
		</td>
	</tr>
</table>
<form name="SurveyPrelimQ" action="SurveyPrelimQ.jsp" method="post">
<table border=1 bordercolor="#3399FF">
<tr>
<td bgcolor="#000080">
<font color="white"  face="Verdana" style="font-weight: 700" size="2">
<b><%=trans.tslt("Preliminary Questions Header")%></b></font>
</td>
</tr>

<tr><td>
<textarea rows=5 cols=75 name="header" id="header">
<% String h = PrelimQController.getPrelimQnHeader(SurveyID); 
		if(!h.equals(""))
		{
			out.print(h);
		}
%>
</textarea>
</td></tr>

</table>


<br/>
<br/>

<table border=1 bordercolor="#3399FF">

<tr>
<td bgcolor="#000080">
<font color="white"  face="Verdana" style="font-weight: 700" size="2">
<b><%=trans.tslt("Preliminary Questions")%></b></font>
</td>
</tr>
<% 

System.out.println("question count "+QuestionCount);

if(QuestionCount>0)
{
for(int x=0; x<QuestionCount;x++)
	{
	out.println("<tr>");
	out.println("<td>");
	out.print("<input type=\"hidden\" name=qid"+x+"");
	if(x<v.size()) //check if the answer is saved already, retrieve the question from db
	{
			out.print(" value="+v.get(x).getPrelimQnId());
	}
	else
	{
		out.print(" value=0");
	}
	out.println(" />");
	out.print("<input type=\"text\" name=qn"+x+" id=qn"+x+" size=50 ");
	if(x<v.size())
	{
		if(v.get(x)!=null)
		{
			out.print("value=\""+v.get(x).getQuestion()+"\"");
		}
	}
	out.println(" />");
	if(x<v.size())
	{
		int ratingScaleId = PrelimQController.getPrelimRatingID(v.get(x).getPrelimQnId());
		out.println("Rating Scale ID: ");
		out.print("<select name=changeID"+x+" id=changeID"+x+">");
		Vector scales = PrelimQController.getAllRatingScale();
		for(Object scale : scales){
			String selected = "";
			if(Integer.parseInt(scale.toString()) == ratingScaleId){
				selected = " selected";
			}
			out.print("<option value=\""+ scale.toString()+"\""+selected+">"+scale.toString()+"</option>");
		}
		out.println("</select>");
		//out.println("<INPUT type=button value=\"Change Rating\" onclick=\"ChangeRating(this.form,this.form.changeID"+x+","+v.get(x).getPrelimQnId()+")\"/>");
		out.println("<INPUT type=button value=Delete onclick=\"Delete(this.form, "+v.get(x).getPrelimQnId()+")\"/>");
	}else{
		
		//String defaultOption = "<option value=></option>";
		out.println("Rating Scale ID: ");
		out.print("<select name=changeID"+x+" id=changeID"+x+">");
		//out.print(defaultOption);
		Vector scales = PrelimQController.getAllRatingScale();
		for(Object scale : scales){
			String selected = "";
			out.print("<option value=\""+ scale.toString()+"\""+selected+">"+scale.toString()+"</option>");
		}
		out.println("</select>");
		//out.println("<INPUT disabled type=button value=\"Change Rating\"/>");
		out.println("<INPUT disabled type=button value=Delete />");
	}

	
	out.println("</td>");
	out.println("</tr>");
	}
}
else
{
	out.println("<tr>");
	out.println("<td>");
	out.println("<input type=\"hidden\" name=qid0 value=0 />");	
	out.println("<input type=\"text\" name=qn0 id=qn0 size=100 disabled/>");
	out.println("</td>");
	out.println("</tr>");
}
	%>
</table>
<br/>
<INPUT type="button" value="Add" onclick="Add(this.form)"/> 
<INPUT type="button" value="Save" onclick="Save(this.form)"/>
<INPUT type="button" value="Manage Rating Scale" onclick="RatingScale(this.form)"/> 

		</font>
</form>
</body>
</html>