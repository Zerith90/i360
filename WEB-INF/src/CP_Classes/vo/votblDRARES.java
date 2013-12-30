package CP_Classes.vo;

public class votblDRARES {

	private int	iResID	= 0 ;
	private int	iCompetencyID	= 0 ;
	private String [] sResource	= new String[6] ;
	private int	iResType	= 0 ;
	private int	iIsSystemGenerated	= 0 ;
	private int	iFKCompanyID	= 0 ;
	private int	iFKOrganizationID	= 0 ;
	private String sDescription="";
	public int getCompetencyID() {
		return iCompetencyID;
	}
	public void setCompetencyID(int competencyID) {
		iCompetencyID = competencyID;
	}
	public int getFKCompanyID() {
		return iFKCompanyID;
	}
	public void setFKCompanyID(int companyID) {
		iFKCompanyID = companyID;
	}
	public int getFKOrganizationID() {
		return iFKOrganizationID;
	}
	public void setFKOrganizationID(int organizationID) {
		iFKOrganizationID = organizationID;
	}
	public int getIsSystemGenerated() {
		return iIsSystemGenerated;
	}
	public void setIsSystemGenerated(int isSystemGenerated) {
		iIsSystemGenerated = isSystemGenerated;
	}
	public int getResID() {
		return iResID;
	}
	public void setResID(int resID) {
		iResID = resID;
	}
	public String getResource() {
		return sResource[0];
	}
	public void setResource(String resource) {
		sResource[0] = resource;
	}
	
	public void setResource(int lang, String resource){
		sResource[lang] = resource;
	}
	
	public String getResource(int lang){
		return sResource[lang];
	}
	
	public String[] getAllResource(){
		return sResource;
	}
	
	public int getResType() {
		return iResType;
	}
	public void setResType(int resType) {
		iResType = resType;
	}
	public String getDescription() {
		return sDescription;
	}
	public void setDescription(String description) {
		sDescription = description;
	}
	

}
