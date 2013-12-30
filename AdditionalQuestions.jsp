<%@ page import="java.sql.*,
				java.util.*,
                 java.io.* "%>  

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<jsp:useBean id="logchk" class="CP_Classes.Login" scope="session"/>        
<jsp:useBean id="trans" class="CP_Classes.Translate" scope="session"/>
<jsp:useBean id="AddQController" class="CP_Classes.AdditionalQuestionController" scope="session"/>
<jsp:useBean id="CE_Survey" class="CP_Classes.Create_Edit_Survey" scope="session"/>
<jsp:useBean id="QuestionBean" class="CP_Classes.AdditionalQuestionBean" scope="session"/>
<%@ page import="CP_Classes.AdditionalQuestion"%>
<%@ page import="CP_Classes.AdditionalQuestionAns"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>AdditionalQuestions</title>
</head>
<SCRIPT LANGUAGE=JAVASCRIPT>
function Save(form)
{

		form.action="AdditionalQuestions.jsp?save=1";
		form.method="post";
		form.submit();
	
}

function Add(form)
{

		form.action="AdditionalQuestions.jsp?add=1&save=2";
		form.method="post";
		form.submit();
}

function Delete(form, qid)
{
	if(confirm("<%=trans.tslt("Are you sure you want to delete this question")%>?"))
	{
		form.action="AdditionalQuestions.jsp?delete="+qid;
		form.method="post";
		form.submit();
	}
	
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
<% 	}
  
	int SurveyID = CE_Survey.getSurvey_ID();
	System.out.println(SurveyID);
	Vector<AdditionalQuestion> v = AddQController.getQuestions(SurveyID);
	if(request.getParameter("add") != null)
	{
		QuestionBean.setQuestionCount(QuestionBean.getQuestionCount()+1);
	}
	
	if(request.getParameter("entry") != null)
	{
  		QuestionBean.setQuestionCount(v.size());
	}
	int QuestionCount = QuestionBean.getQuestionCount();
	if(request.getParameter("save") != null)
	{
		if(request.getParameter("header")!=null)
		{
			String header = AddQController.escapeInvalidChars(request.getParameter("header"));
			//save the header for the additional questions 
			if(!AddQController.getAnswerHeader(SurveyID).equals(""))
			{
				//header already exists update the header
				AddQController.updateAnswerHeader(SurveyID, header);
				
			}
			else
			{
				//header does not exist insert the new header
				AddQController.saveAnswerHeader(SurveyID, header);
			
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
							AddQController.saveQuestion(SurveyID, qn);
						}
						else
						{
							AddQController.updateQuestion(Integer.parseInt(qnid), SurveyID, qn);
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
		location.href='AdditionalQuestions.jsp?entry=1';
		</Script>
		<%
		}
		else
		{
			v = AddQController.getQuestions(SurveyID);
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
		if(AddQController.checkHavingAns(delqid) && confirm != 1){
			%>
			<Script>
			if(confirm("<%=trans.tslt("Answers related to this question will be removed, are you sure")%>?"))
			{
				location.href='AdditionalQuestions.jsp?confirm=1&delete='+<%=delqid%>;
			}
			</Script>
			<%
		}else{
			AddQController.deleteQuestion(delqid);
			%>
			<Script>
			alert("Deleted successfully")
			location.href='AdditionalQuestions.jsp?entry=1';
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
		<p align="center">&nbsp; <font size="2">
   
		<span style="font-weight: 700">
		<font face="Arial" style="font-size: 10pt; font-weight: 700" color="#CC0000"><%= trans.tslt("Additional Questions") %></font></span>
		</td>
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
<form name="AdditionalQuestions" action="AdditionalQuestions.jsp" method="post">
<table border=1 bordercolor="#3399FF">
<tr>
<td bgcolor="#000080">
<font color="white"  face="Verdana" style="font-weight: 700" size="2">
<b><%=trans.tslt("Additional Questions Header")%></b></font>
</td>
</tr>

<tr><td>
<textarea rows=5 cols=75 name="header" id="header">
<% String h = AddQController.getAnswerHeader(SurveyID); 
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
<b><%=trans.tslt("Additional Questions")%></b></font>
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
			out.print(" value="+v.get(x).getAddQnId());
	}
	else
	{
		out.print(" value=0");
	}
	out.println(" />");
	out.print("<input type=\"text\" name=qn"+x+" id=qn"+x+" size=100 ");
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
		out.println("<INPUT type=button value=Delete onclick=\"Delete(this.form, "+v.get(x).getAddQnId()+")\"/>");
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
	out.println("<input type=\"text\" name=qn0 id=qn0 size=100 />");
	out.println("</td>");
	out.println("</tr>");
}
	%>
</table>
<br/>
<INPUT type="button" value="Add" onclick="Add(this.form)"/> 
<INPUT type="button" value="Save" onclick="Save(this.form)"/> 

		</font>
</form>
</body>
</html>