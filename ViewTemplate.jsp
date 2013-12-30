<%@ page import = "java.sql.*,CP_Classes.vo.votblOrganization,java.util.*" %>

<%// Add pageEncoding and charset UTF-8 to enable input of symbols. i.e registered sign, Sebastian 21 July 2010 %>
<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html">
<title>View Template</title>
<style type="text/css">
<!--
.navy {
	font-family: Arial, Helvetica, sans-serif;
	font-size: 12px;
	color: #000066;
}
-->
</style>
</head>

<link REL="stylesheet" TYPE="text/css" href="Settings\Settings.css">
<%
//Added by Xuehai, 30 May 2011, Adding HTMLEditor:CKEditor.
%>
<script type="text/javascript" src="ckeditor/ckeditor.js"></script>
<jsp:useBean id="logchk" class="CP_Classes.Login" scope="session"/>     
<jsp:useBean id="Org" class="CP_Classes.Organization" scope="session"/>
<jsp:useBean id="ET" class="CP_Classes.EmailTemplate" scope="session"/>
<jsp:useBean id="trans" class="CP_Classes.Translate" scope="session"/>
<% 	// added to check whether organisation is a consulting company
// Mark Oei 09 Mar 2010 %>
<jsp:useBean id="CE_Survey" class="CP_Classes.Create_Edit_Survey" scope="session"/>

<script language="javascript">
//Edited by Xuehai, 30 May 2011, Remove 'void' to enable running on Chrome&Firefox
//void function confirmSave(form)
function confirmSave(form)
{
	//Added by Xuehai 31 May 2011. Adding CKEditor to support special words and symbols
	//The following code is to parse html codes of self-define tag to their real value, such as '&lt;Rater Name&gt;' to '<Rater Name>'
	var templateValue=CKEDITOR.instances.t_txtTemplate.getData();
	//Gwen Oh - 19/09/2011: Commented replacing '&lt;' to '<' and '&gt;' to '>' as the html safe codes should be saved
	//						in the db so that the tags will appear properly when the actual email is sent
	/*
	if(templateValue != ''){
		templateValue = templateValue.replace(/&lt;([A-Z]+.*?)&gt;/g,'<$1>');
	}
	*/
	/*The following line is commented off to remove the alert. 
	 - Liu Taichen ( 9 May 2012)
	*/
	//alert (templateValue);
	document.getElementById('txtTemplate').value=templateValue;
	//End of Adding
	
	//added length checking -Liu Taichen 9 May 2012
	if(form.txtTemplate.value.length > 8000){
		alert("Maximum characters exceeded. Maximum characters allowed: 8000");
	}
	
	else if(form.txtTemplate.value == "")
		alert("Please fill in the template.");
	
	
	else {
		if(confirm("Save this template?")) {
			form.action = "ViewTemplate.jsp?save=1";
			form.submit();
		}
	}
}
function loadDefault(form)
{
	if(confirm("Load default template?")) 
	{
		form.action = "ViewTemplate.jsp?subDefault=1";
		form.submit();
	}
}
/*
* Change(s) : Add methods sendTestEmail() and trim(). Added "Send Test Email" button and additional codes.
* Reason(s) : To add the function for user to send a test email template to their login account email
* Updated By: Sebastian
* Updated On: 21 July 2010
*/
function sendTestEmail(form)
{
	var subject = document.getElementsByName("txtSubject")[0].value;
	//Gwen Oh - 13/08/11: Get the template through CKEDITOR and put the template into the hidden text field, txtTemplate
	document.getElementById('txtTemplate').value = CKEDITOR.instances.t_txtTemplate.getData();
	
	var template = document.getElementsByName("txtTemplate")[0].value;
	subject = trim(subject);
	template = trim(template);
	
	//Prompt warning msg if the template email have not been inputed
	if ((subject == "null" || template == "null") || (subject == "" || template == ""))
	{
		alert("Please ensure that the email has a proper template");
	}
	else //proceed with sending a test mail
	{
		form.action = "ViewTemplate.jsp?btnSendTestEmail=1";
		form.submit();
	}
}

function trim(str)

{

    while (str.substring(0,1) == ' ') // check for white spaces from beginning

    {

        str = str.substring(1, str.length);

    }

    while (str.substring(str.length-1, str.length) == ' ') // check white space from end

    {

        str = str.substring(0,str.length-1);

    }

   

    return str;

}

function confirmSubmit(form)
{
	form.action = "ViewTemplate.jsp";
	form.submit();
}
function confirmSubmitOrg(form)
{
	//alert(form.ddOrg.value);
	form.action = "ViewTemplate.jsp?org="+form.ddOrg.value;
	form.submit();
}
<% 
/*******************************

Edited by Roger - 12 June 2008
Fix sorting . added length for textarea

**********************************/
%>
function checkTextAreaSize(txtAreaField, iMaxLimit) {
	if(txtAreaField.value.length > iMaxLimit) {
		alert("Maximum characters exceeded. Maximum characters allowed: " + iMaxLimit);
		txtAreaField.value = txtAreaField.value.substring(0,iMaxLimit);
	}
}

</script>

<body class="pagecolor">
<%
int FKOrg =0;
/*
 * Change : add the organization combobox checking
 * Reason : To fix problem when edit email template
 * Add by : Johanes
 * Add on : 9/11/2009
 */	
 /*
 * Change : add Coaching Booking notification email and reminder email
 * Add by : Dai Yong
 * Add on : 03/07/2013
 */	
if(request.getParameter("org") != null)
{ 
	FKOrg = new Integer(request.getParameter("org")).intValue();
 	logchk.setOrg(FKOrg);
}else{
	 FKOrg = logchk.getOrg();
}
	int FKComp = logchk.getCompany();
	int template = 0;
	String [] strType = {"Nomination Notification Email", "Nomination Reminder Email", "Participants Notification Email", "Participants Reminder Email", "Target Individual Report Email","","Coaching Notification Email","Coaching Reminder Email"};
	String [] sTemplate = new String[8];
	String strTemplate = "";
	String strSubject = "";
	int FKJob = 0;
	
	//Clear selected templates
	sTemplate[0] = "";
	sTemplate[1] = "";
	sTemplate[2] = "";
	sTemplate[3] = "";
	sTemplate[4] = "";
	sTemplate[5] = "";
	sTemplate[6] = "";
	sTemplate[7] = "";
	strTemplate = ET.getEmailTemplate(FKOrg, template); //Get default email template: template = 0
	
	/*
 * Change : add subject textfield
 * Reason : there's need to change the subject
 * Add by : Johanes
 * Add on : 9/11/2009
 */	
	strSubject = ET.getEmailSubject(FKOrg, template); 

	
	if(request.getParameter("ddTemplate") != null) {
		template = Integer.parseInt(request.getParameter("ddTemplate"));
		sTemplate[template] = "selected";
		
		strTemplate = ET.getEmailTemplate(FKOrg, template);
		/*
 * Change : add subject textfield
 * Reason : there's need to change the subject
 * Add by : Johanes
 * Add on : 9/11/2009
 */	
		strSubject = ET.getEmailSubject(FKOrg, template); 
	}


	
	if(request.getParameter("save") != null) {
		//template = Integer.parseInt(request.getParameter("hidType"));
		strTemplate = request.getParameter("txtTemplate");
		strSubject = request.getParameter("txtSubject");
		//the maximum number of character is changed to 8000 in accordance with the documentation
		// - Liu Taichen ( 9 May 2012)
		if (strTemplate.length() >= 8000) {
%>
<script>
	alert("Maximum characters exceeded. Maximum characters allowed: 8000");
</script>
<%		
		}	else  {	
%>
<script>
	alert("Saved successfully");
</script>
<%		
		}
		
		try {
			ET.editTemplate(FKOrg, template, strTemplate,strSubject);
			
			strTemplate = ET.getEmailTemplate(FKOrg, template);
			/*
 * Change : add subject textfield
 * Reason : there's need to change the subject
 * Add by : Johanes
 * Add on : 9/11/2009
 */	
			strSubject = ET.getEmailSubject(FKOrg, template); 
		}catch(SQLException SE) { System.out.println(SE.getMessage());}
	}else if(request.getParameter("subDefault") != null) {

		/* Get default template */
		//System.out.println("template:"+template);
		strTemplate = ET.getDefaultTemplate(template);
	}
	//add codes to carry out the functionality of sending an email template when user clicks on the "Send Test Email" button, Sebastian 21 July 2010
	else if (request.getParameter("btnSendTestEmail") != null)
	{
		int pkUser = logchk.getPKUser(); //get the current login user id
		int pkOrg = Integer.parseInt(request.getParameter("ddOrg"));
		strSubject = request.getParameter("txtSubject");
		strTemplate = request.getParameter("txtTemplate");
		
		//use function here to send test email here
		boolean result = ET.SendTestEmail(pkUser, pkOrg, strSubject, strTemplate);
		
		//email has been sent successfully
		if (result)
		{
			%>
				<script>
					alert("Test email sent successfully")
				</script>
			<%
		}
		//failed to send email due to sender and recipient error, see server log  
		else
		{
			%>
				<script>
					alert("Failed to send Test Email")
				</script>
			<%
		}
	}
%>
<form name="Template" method="post" action="ViewTemplate.jsp">
<table width="630" class="fontstyle">
  <tr>
    <td class="navy" colspan=3>
    <b><font face="Arial" size="2" color="#000080">Email Templates</font></b>
	</td>
  </tr>
  <tr>
    <td colspan=3>
    <ul>
    <li>Simply edit text and Press Save.</li>
	<li>Please DO NOT CHANGE anything within the tag "< >" as they are field names from the database. You may move them to other locations 
	or delete the field if it is not required.</li>
	</ul>
	</td>
  </tr>

  <tr>
  <td width="100"><%= trans.tslt("Organisation") %></td>
  <td width="1">:</td>
  <td>

	<%
	/*
 * Change : add organization combobox
 * Reason : to fix problem when adding template to database
 * Add by : Johanes
 * Add on : 9/11/2009
 */	
	// Added to check whether organisation is also a consulting company
	// if yes, will display a dropdown list of organisation managed by this company
	// else, it will display the current organisation only
	// Mark Oei 09 Mar 2010
	String [] UserDetail = new String[14];
	UserDetail = CE_Survey.getUserDetail(logchk.getPKUser());
	boolean isConsulting = true;
	isConsulting = Org.isConsulting(UserDetail[10]); // check whether organisation is a consulting company 
	if (isConsulting){ %>
	  	<select size="1" name="ddOrg" onchange="confirmSubmitOrg(this.form)">
	<%
		Vector vOrg = logchk.getOrgList(logchk.getCompany());

		for(int i=0; i<vOrg.size(); i++)
		{
			votblOrganization vo = (votblOrganization)vOrg.elementAt(i);
			int PKOrg = vo.getPKOrganization();
			String OrgName = vo.getOrganizationName();

			if(logchk.getOrg() == PKOrg)
			{ %>
				<option value=<%=PKOrg%> selected><%=OrgName%></option>
			<% } else { %>
				<option value=<%=PKOrg%>><%=OrgName%></option>
			<%	}	
		} 
	} else { %>
		<select size="1" name="ddOrg" onchange="confirmSubmitOrg(this.form)">
		<option value=<%=logchk.getSelfOrg()%>><%=UserDetail[10]%></option>
	<% } // End of isConsulting %>
	</select></td>
</tr>
  <tr>
  <td><%= trans.tslt("Email Type")%></td>
  <td>:</td>
    <td>
    <select size="1" name="ddTemplate" onchange="confirmSubmit(this.form)">
	<option value="0" <%=sTemplate[0]%>>Nomination Notification Email</option>
	<option value="1" <%=sTemplate[1]%>>Nomination Reminder Email</option>
	<option value="2" <%=sTemplate[2]%>>Participants Notification Email</option>
	<option value="3" <%=sTemplate[3]%>>Participants Reminder Email</option>
	<option value="4" <%=sTemplate[4]%>>Target Individual Report Email</option>
	<option value="6" <%=sTemplate[6]%>>Coaching Notification Email</option>
	<option value="7" <%=sTemplate[7]%>>Coaching Reminder Email</option>
	</select></td>
  </tr>
  <tr>
  <td><%= trans.tslt("Subject")%></td>
  <td>:</td>
  <td><input class="fontstyle" name="txtSubject" size="83" value="<%=strSubject%>"></td>
</tr>
  <tr>
  <td></td>
  <td></td>
    <td colspan="3"><input type="hidden" name="txtTemplate" id="txtTemplate" />
    	<%
    	//Edited by Xuehai, 31 May 2011. Adding CKEditor.
    	//Encoding special symbol to htmlcode, to show correctly in CKEditor. Such as '<Rater Name>' to '&amp;lt;Rater Name&amp;gt;'
	    if(strTemplate != null){
	    	//Gwen Oh: 19/09/2011 - Added if/else stmt
	    	if(request.getParameter("subDefault") != null)
	    		//Default template has <> tags
	    		strTemplate=strTemplate.replaceAll("<([A-Z]+.*?)>", "&amp;lt;$1&amp;gt;");
	    	else
	    		//Tags from database are '&lt;' and '&gt;' to encode the non-html tags
				strTemplate=strTemplate.replaceAll("&lt;([A-Za-z]+.*?)&gt;", "&amp;lt;$1&amp;gt;");
				//Code below to cater to templates that have been previously saved with <> non-html tags in the database
				strTemplate=strTemplate.replaceAll("<([A-Z]+.*?)>", "&amp;lt;$1&amp;gt;");
		}
    	%>
    	<textarea name="t_txtTemplate" id="t_txtTemplate" style='width:535px;height:300px' <%//onkeyup="checkTextAreaSize(this,4000)"%> ><%=strTemplate%></textarea>
    </td>
  </tr>
</table>
<%
//Added by Xuehai, 30 May 2011, Adding HTMLEditor:CKEditor.
%>
<script type="text/javascript">
   CKEDITOR.replace('t_txtTemplate', {skin:"v2", width:535,height:300});
</script>
<p></p>
<table width="630" class="fontstyle">
  <tr>
  	<%//add button "Send Test Email" for the send email template functionality, Sebastian 21 July 2010%>
    <td width="346" align="left"><input type="button" name="subDefault" value="Default Template" onClick="loadDefault(this.form)">&nbsp;<input type="button" name="btnSendTestEmail" value="Send Test Email" onClick="sendTestEmail(this.form)"></td>
    <td width="191" align="right">&nbsp;</td>
    <td width="77" align="right"><input type="button" name="btnSave" value="Save" onClick="confirmSave(this.form)"></td>
  </tr>
</table>
</form>
</body>

</html>