package CP_Classes.vo;

public class votblRelationSpecific {
	
	private int	iSpecificID	 = 0 ;
	private int	iRelationID	 = 0 ;
	private String	sRelationSpecific	= "" ;
	private int	iFKCompanyID	 = 0 ;
	private int	iFKOrganization	 = 0 ;
	
	public int getFKCompanyID() {
		return iFKCompanyID;
	}
	public void setFKCompanyID(int companyID) {
		iFKCompanyID = companyID;
	}
	public int getFKOrganization() {
		return iFKOrganization;
	}
	public void setFKOrganization(int organization) {
		iFKOrganization = organization;
	}
	public int getRelationID() {
		return iRelationID;
	}
	public void setRelationID(int relationID) {
		iRelationID = relationID;
	}
	public String getRelationSpecific() {
		return sRelationSpecific;
	}
	public void setRelationSpecific(String relationSpecific) {
		sRelationSpecific = relationSpecific;
	}
	public int getSpecificID() {
		return iSpecificID;
	}
	public void setSpecificID(int specificID) {
		iSpecificID = specificID;
	}
	
	
}
