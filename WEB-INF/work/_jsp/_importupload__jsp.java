/*
 * JSP generated by Resin Professional 4.0.36 (built Fri, 26 Apr 2013 03:33:09 PDT)
 */

package _jsp;
import javax.servlet.*;
import javax.servlet.jsp.*;
import javax.servlet.http.*;
import java.sql.*;
import java.io.*;
import javazoom.upload.*;
import java.util.*;
import java.util.regex.*;

public class _importupload__jsp extends com.caucho.jsp.JavaPage
{
  private static final java.util.HashMap<String,java.lang.reflect.Method> _jsp_functionMap = new java.util.HashMap<String,java.lang.reflect.Method>();
  private boolean _caucho_isDead;
  private boolean _caucho_isNotModified;
  private com.caucho.jsp.PageManager _jsp_pageManager;
  
  public void
  _jspService(javax.servlet.http.HttpServletRequest request,
              javax.servlet.http.HttpServletResponse response)
    throws java.io.IOException, javax.servlet.ServletException
  {
    javax.servlet.http.HttpSession session = request.getSession(true);
    com.caucho.server.webapp.WebApp _jsp_application = _caucho_getApplication();
    com.caucho.jsp.PageContextImpl pageContext = _jsp_pageManager.allocatePageContext(this, _jsp_application, request, response, null, session, 8192, true, false);

    TagState _jsp_state = null;

    try {
      _jspService(request, response, pageContext, _jsp_application, session, _jsp_state);
    } catch (java.lang.Throwable _jsp_e) {
      pageContext.handlePageException(_jsp_e);
    } finally {
      _jsp_pageManager.freePageContext(pageContext);
    }
  }
  
  private void
  _jspService(javax.servlet.http.HttpServletRequest request,
              javax.servlet.http.HttpServletResponse response,
              com.caucho.jsp.PageContextImpl pageContext,
              javax.servlet.ServletContext application,
              javax.servlet.http.HttpSession session,
              TagState _jsp_state)
    throws Throwable
  {
    javax.servlet.jsp.JspWriter out = pageContext.getOut();
    final javax.el.ELContext _jsp_env = pageContext.getELContext();
    javax.servlet.ServletConfig config = getServletConfig();
    javax.servlet.Servlet page = this;
    javax.servlet.jsp.tagext.JspTag _jsp_parent_tag = null;
    com.caucho.jsp.PageContextImpl _jsp_parentContext = pageContext;
    response.setContentType("text/html");
    response.setCharacterEncoding("utf-8");

    out.write(_jsp_string0, 0, _jsp_string0.length);
    CP_Classes.Import Import;
    synchronized (pageContext.getSession()) {
      Import = (CP_Classes.Import) pageContext.getSession().getAttribute("Import");
      if (Import == null) {
        Import = new CP_Classes.Import();
        pageContext.getSession().setAttribute("Import", Import);
      }
    }
    out.write(_jsp_string1, 0, _jsp_string1.length);
    CP_Classes.Login logchk;
    synchronized (pageContext.getSession()) {
      logchk = (CP_Classes.Login) pageContext.getSession().getAttribute("logchk");
      if (logchk == null) {
        logchk = new CP_Classes.Login();
        pageContext.getSession().setAttribute("logchk", logchk);
      }
    }
    out.write(_jsp_string2, 0, _jsp_string2.length);
    CP_Classes.Setting setting;
    synchronized (pageContext.getSession()) {
      setting = (CP_Classes.Setting) pageContext.getSession().getAttribute("setting");
      if (setting == null) {
        setting = new CP_Classes.Setting();
        pageContext.getSession().setAttribute("setting", setting);
      }
    }
    out.write(_jsp_string3, 0, _jsp_string3.length);
    CP_Classes.Translate trans;
    synchronized (pageContext.getSession()) {
      trans = (CP_Classes.Translate) pageContext.getSession().getAttribute("trans");
      if (trans == null) {
        trans = new CP_Classes.Translate();
        pageContext.getSession().setAttribute("trans", trans);
      }
    }
    out.write(_jsp_string4, 0, _jsp_string4.length);
    javazoom.upload.UploadBean upBean;
    upBean = (javazoom.upload.UploadBean) pageContext.getAttribute("upBean");
    if (upBean == null) {
      upBean = new javazoom.upload.UploadBean();
      pageContext.setAttribute("upBean", upBean);
      out.write(_jsp_string5, 0, _jsp_string5.length);
      {
        java.lang.String _jspParam;
        _jspParam = request.getParameter("folderstore");
        if (_jspParam != null && ! _jspParam.equals(""))
          upBean.setFolderstore(_jspParam);
      }
      out.write(_jsp_string5, 0, _jsp_string5.length);
      upBean.setWhitelist("*.xls");
      out.write(_jsp_string3, 0, _jsp_string3.length);
    }
    out.write(_jsp_string6, 0, _jsp_string6.length);
    // by lydia Date 05/09/2008 Fix jsp file to support Thai language 
    out.write(_jsp_string7, 0, _jsp_string7.length);
    out.print((trans.tslt("Please select a file to be uploaded")));
    out.write(_jsp_string8, 0, _jsp_string8.length);
    
	int iImportType = (request.getParameter("type") == null)?0:Integer.parseInt(request.getParameter("type"));
	//Added a new variable to save the language type selected by the user when in ImportLanguage.jsp, Chun Yeong 22 Jul 2011
	int iLanguage = (request.getParameter("lang") == null)?0:Integer.parseInt(request.getParameter("lang"));

    out.write(_jsp_string9, 0, _jsp_string9.length);
    out.print((iImportType));
    out.write(_jsp_string10, 0, _jsp_string10.length);
    out.print((iLanguage));
    out.write(_jsp_string11, 0, _jsp_string11.length);
    
			
			//Mutlipart Form Data Request
			MultipartFormDataRequest mrequest = null;
			
			boolean isUpload = false;
			boolean isFileNotFoundError = true;
            
			//Multipart Form Data Request Check    
            if (MultipartFormDataRequest.isMultipartFormData(request)) {
                //Get Multipart Form Data Request
                mrequest = new MultipartFormDataRequest(request);
                //Set Upload Operation
                isUpload = (mrequest.getParameter("todo")!= null)?true:false;				
            } //End of Multipart Form Data Request Check			

            //Multipart Form Data Request Null Check
            if(mrequest != null && isUpload) {
				//Start of Try-Catch
				try{
                //Get Files Hashtable
                Hashtable files = mrequest.getFiles();
                upBean.setFolderstore(setting.getUploadPath());
                upBean.setOverwrite(true);

				//Files Null and Is Empty Check
				if((files != null) && (!files.isEmpty())) {
					//Get File 
					UploadFile file = (UploadFile) files.get("uploadfile");

					//File Null Check
					if(file != null) {
						String sFileName = file.getFileName();
						//File Name Null Check
						if(sFileName != null) {
							upBean.store(mrequest, "uploadfile");
							isFileNotFoundError = false;
							
							//Added a if statement to differentiate the ImportLanguage page from the ImportExport page, Chun Yeong 22 Jul 2011
							if(iImportType == 12 ) {

    out.write(_jsp_string12, 0, _jsp_string12.length);
    out.print((iImportType));
    out.write(_jsp_string10, 0, _jsp_string10.length);
    out.print((iLanguage));
    out.write(_jsp_string13, 0, _jsp_string13.length);
    out.print((sFileName));
    out.write(_jsp_string14, 0, _jsp_string14.length);
    							
							//Else reopen the ImportExport.jsp
							} else {

    out.write(_jsp_string15, 0, _jsp_string15.length);
    out.print((iImportType));
    out.write(_jsp_string13, 0, _jsp_string13.length);
    out.print((sFileName));
    out.write(_jsp_string14, 0, _jsp_string14.length);
    							} //End of If statement
						} //End of File Name Check
					} //End of File Null Check
				} //End of Files Null and Is Empty Check
				}catch(Exception ex) {
					isFileNotFoundError = false;

    out.write(_jsp_string16, 0, _jsp_string16.length);
    out.print((trans.tslt("Format is not correct. Formats suported is .xls")));
    out.write(_jsp_string17, 0, _jsp_string17.length);
    
				}
            } //End of Multopart Form Request Null Check
			
			//File Not Found Check
			if(isFileNotFoundError && isUpload) {

    out.write(_jsp_string18, 0, _jsp_string18.length);
    out.print((trans.tslt("Please select a file to be uploaded")));
    out.write(_jsp_string19, 0, _jsp_string19.length);
    	
			} //End of File Not Found Check

    out.write(_jsp_string20, 0, _jsp_string20.length);
    	
	/*
	* Change(s) : Add Switch statement to determine import type title in the import pop up window
	* Reason(s) : To display the type of import to the user access the function
	* Updated By: Sebastian
	* Updated On: 05 July 2010
	*/	
	//Import Type Code for URL String
    //    1) Import User
    //    2) Import Assignment - Target & Rater
    //    3) Import Assignment - Target Only
    //    4) Import Competency
    //    5) Import Key Behaviour
    //    6) Import Development Activities
    //    7) Import Development Resources
    //    8) Import Division
    //    9) Import Department
    //    10) Import Group/Section
    //    11) Import Questionnaire
	//	  12) Import Language
    //    13) Import Cluster
    //    14) Import Nomination
	String importDesc = "";
	
	switch (iImportType)
	{
		case 1: //Import User
			importDesc = "Import User";	
			break;
			
		case 2: //Import Assignment - Target & Rater
			importDesc = "Import Assignment - Target & Rater";	
			break;
		
		case 3: //Import Assignment - Target Only
			importDesc = "Import Assignment - Target Only";	
			break;
			
		case 4: //Import Competency
			importDesc = "Import Competency";
			break;
 			
 		case 5: //Import Key Behaviour
 			importDesc = "Import Key Behaviour";
  			break;
	 		
	 	case 6: //Import Development Activities
	 		importDesc = "Import Development Activities";
	 		break;
	  		
		case 7: //Import 
			importDesc = "Import Development Resources";
			break;
		
		case 8: //Import Division
			importDesc = "Import Division";
			break;
			
		case 9: //Import Department
			importDesc = "Import Department";
			break;
			
		case 10: //Import Group/Section
			importDesc = "Import Group\\Section";
			break;
		
		case 11: //Import Questionnaire
			importDesc = "Import Questionnaire";
			break;
		
		//Added Import translation for the new functionality, Chun Yeong 22 Jul 2011
		case 12: //Import Language Translation
			importDesc = "Import Language Translation";
			break;
			
		case 13:  //Import Cluster
		    importDesc = "Import Cluster";
		
		case 14:
			importDesc = "Import Nomination";
		//End Import translation***
	}
		
	if (!importDesc.equals(""))
	{
		
    out.write(_jsp_string21, 0, _jsp_string21.length);
    out.print((trans.tslt(importDesc)));
    out.write(_jsp_string22, 0, _jsp_string22.length);
    
	}

    out.write(_jsp_string23, 0, _jsp_string23.length);
    out.print((trans.tslt("Please click the browse button and choose the excel file to be imported")));
    out.write(_jsp_string24, 0, _jsp_string24.length);
    out.print(( trans.tslt("Import") ));
    out.write(_jsp_string25, 0, _jsp_string25.length);
    out.print(( trans.tslt("Cancel") ));
    out.write(_jsp_string26, 0, _jsp_string26.length);
  }

  private com.caucho.make.DependencyContainer _caucho_depends
    = new com.caucho.make.DependencyContainer();

  public java.util.ArrayList<com.caucho.vfs.Dependency> _caucho_getDependList()
  {
    return _caucho_depends.getDependencies();
  }

  public void _caucho_addDepend(com.caucho.vfs.PersistentDependency depend)
  {
    super._caucho_addDepend(depend);
    _caucho_depends.add(depend);
  }

  protected void _caucho_setNeverModified(boolean isNotModified)
  {
    _caucho_isNotModified = true;
  }

  public boolean _caucho_isModified()
  {
    if (_caucho_isDead)
      return true;

    if (_caucho_isNotModified)
      return false;

    if (com.caucho.server.util.CauchoSystem.getVersionId() != -7791540776389363938L)
      return true;

    return _caucho_depends.isModified();
  }

  public long _caucho_lastModified()
  {
    return 0;
  }

  public void destroy()
  {
      _caucho_isDead = true;
      super.destroy();
    TagState tagState;
  }

  public void init(com.caucho.vfs.Path appDir)
    throws javax.servlet.ServletException
  {
    com.caucho.vfs.Path resinHome = com.caucho.server.util.CauchoSystem.getResinHome();
    com.caucho.vfs.MergePath mergePath = new com.caucho.vfs.MergePath();
    mergePath.addMergePath(appDir);
    mergePath.addMergePath(resinHome);
    com.caucho.loader.DynamicClassLoader loader;
    loader = (com.caucho.loader.DynamicClassLoader) getClass().getClassLoader();
    String resourcePath = loader.getResourcePathSpecificFirst();
    mergePath.addClassPath(resourcePath);
    com.caucho.vfs.Depend depend;
    depend = new com.caucho.vfs.Depend(appDir.lookup("ImportUpload.jsp"), -2551151734586932102L, false);
    _caucho_depends.add(depend);
  }

  final static class TagState {

    void release()
    {
    }
  }

  public java.util.HashMap<String,java.lang.reflect.Method> _caucho_getFunctionMap()
  {
    return _jsp_functionMap;
  }

  public void caucho_init(ServletConfig config)
  {
    try {
      com.caucho.server.webapp.WebApp webApp
        = (com.caucho.server.webapp.WebApp) config.getServletContext();
      init(config);
      if (com.caucho.jsp.JspManager.getCheckInterval() >= 0)
        _caucho_depends.setCheckInterval(com.caucho.jsp.JspManager.getCheckInterval());
      _jsp_pageManager = webApp.getJspApplicationContext().getPageManager();
      com.caucho.jsp.TaglibManager manager = webApp.getJspApplicationContext().getTaglibManager();
      com.caucho.jsp.PageContextImpl pageContext = new com.caucho.jsp.InitPageContextImpl(webApp, this);
    } catch (Exception e) {
      throw com.caucho.config.ConfigException.create(e);
    }
  }

  private final static char []_jsp_string5;
  private final static char []_jsp_string6;
  private final static char []_jsp_string4;
  private final static char []_jsp_string3;
  private final static char []_jsp_string1;
  private final static char []_jsp_string9;
  private final static char []_jsp_string8;
  private final static char []_jsp_string18;
  private final static char []_jsp_string2;
  private final static char []_jsp_string0;
  private final static char []_jsp_string12;
  private final static char []_jsp_string19;
  private final static char []_jsp_string26;
  private final static char []_jsp_string16;
  private final static char []_jsp_string14;
  private final static char []_jsp_string24;
  private final static char []_jsp_string21;
  private final static char []_jsp_string13;
  private final static char []_jsp_string20;
  private final static char []_jsp_string15;
  private final static char []_jsp_string11;
  private final static char []_jsp_string10;
  private final static char []_jsp_string25;
  private final static char []_jsp_string7;
  private final static char []_jsp_string22;
  private final static char []_jsp_string17;
  private final static char []_jsp_string23;
  static {
    _jsp_string5 = "\r\n            ".toCharArray();
    _jsp_string6 = "\r\n        <title></title>\r\n        <meta http-equiv=\"Content-Type\" content=\"text/html\">\r\n        \r\n		".toCharArray();
    _jsp_string4 = "    \r\n        ".toCharArray();
    _jsp_string3 = "\r\n        ".toCharArray();
    _jsp_string1 = "        \r\n        ".toCharArray();
    _jsp_string9 = "\r\n        <form method=\"post\" action=\"ImportUpload.jsp?type=".toCharArray();
    _jsp_string8 = "\");\r\n				}\r\n			}\r\n		</script>\r\n    </head>\r\n    <body>\r\n".toCharArray();
    _jsp_string18 = "\r\n				<script>\r\n                    alert('".toCharArray();
    _jsp_string2 = " \r\n        ".toCharArray();
    _jsp_string0 = "		 \r\n<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\r\n<html>\r\n    <head>\r\n        ".toCharArray();
    _jsp_string12 = "\r\n							<script>\r\n                                window.close();\r\n                                opener.location.href = \"ImportLanguage.jsp?type=".toCharArray();
    _jsp_string19 = "');\r\n                </script>\r\n".toCharArray();
    _jsp_string26 = "\" onClick=window.close()>\r\n        </form>\r\n    </body>\r\n</html>".toCharArray();
    _jsp_string16 = "\r\n					<script>\r\n                        alert('".toCharArray();
    _jsp_string14 = "\";\r\n                            </script>\r\n".toCharArray();
    _jsp_string24 = "\r\n                <br><br>\r\n            </p>\r\n            <p></p>\r\n            <input name=\"uploadfile\" type=\"file\" size=\"40\">\r\n            <p></p>\r\n            <input type=\"hidden\" name=\"todo\" value=\"upload\">\r\n            <input type=\"button\" name=\"Submit\" value=\"".toCharArray();
    _jsp_string21 = "\r\n            <p style='font-size:10.0pt;font-family:Arial;font-weight:bold'>\r\n                ".toCharArray();
    _jsp_string13 = "&path=\" + \"".toCharArray();
    _jsp_string20 = "\r\n".toCharArray();
    _jsp_string15 = "\r\n							<script>\r\n                                window.close();\r\n                                opener.location.href = \"ImportExport.jsp?type=".toCharArray();
    _jsp_string11 = "\" name=\"frmImportUpload\" enctype=\"multipart/form-data\">\r\n".toCharArray();
    _jsp_string10 = "&lang=".toCharArray();
    _jsp_string25 = "\" onClick=\"formSubmit();\"> \r\n            <input type=\"reset\" name=\"Reset\" value=\"".toCharArray();
    _jsp_string7 = "\r\n        <script language=\"javascript\">\r\n			function formSubmit() {\r\n				try {\r\n					this.frmImportUpload.submit();\r\n				} catch (error) {\r\n					alert(\"".toCharArray();
    _jsp_string22 = "\r\n                <br><br>\r\n            </p>\r\n		".toCharArray();
    _jsp_string17 = "');\r\n                    </script>\r\n".toCharArray();
    _jsp_string23 = "\r\n            <p style='font-size:10.0pt;font-family:Arial'>\r\n                ".toCharArray();
  }
}