package CP_Classes.vo;

public class voCompetency {

	private int iFKCompetency = 0;;
	//private String sCompetencyName = "";
	//private String sCompetencyDefinition = "";
	private String[] sCompetencyNameArr = new String[6];
	private String[] sCompetencyDefinitionArr = new String[6];
	private String sDescription="";
	private double dResult =0.0;
	private int iPKJobCategoryItem = 0;

	private int	iIsExpert = 0;	
	private int	iIsTraitOrSimulation	= 0;
	private int	iIsSystemGenerated	= 0;
	private int	iFKCompanyID	= 0;
	private int	iFKOrganizationID	= 0;
	private int iPKCompetency = 0;
	private int iRatingTaskID=0;
	private int iAdminSetup=0;
	private String sRatingCode="";
	private String sOrigin = "";
	
	private String sRatingName = "";
	private int iScaleRange = 0;
	
	private String sOrganisationName = "";
	private int iRank = 0;
	
	public int getFKCompanyID() {
		return iFKCompanyID;
	}

	public void setFKCompanyID(int companyID) {
		iFKCompanyID = companyID;
	}

	public int getPKCompetency() {
		return iPKCompetency;
	}

	public void setPKCompetency(int competency) {
		iPKCompetency = competency;
	}

	public int getFKOrganizationID() {
		return iFKOrganizationID;
	}

	public void setFKOrganizationID(int organizationID) {
		iFKOrganizationID = organizationID;
	}

	public int getIsExpert() {
		return iIsExpert;
	}

	public void setIsExpert(int isExpert) {
		iIsExpert = isExpert;
	}

	public int getIsSystemGenerated() {
		return iIsSystemGenerated;
	}

	public void setIsSystemGenerated(int isSystemGenerated) {
		iIsSystemGenerated = isSystemGenerated;
	}

	public int getIsTraitOrSimulation() {
		return iIsTraitOrSimulation;
	}

	public void setIsTraitOrSimulation(int isTraitOrSimulation) {
		iIsTraitOrSimulation = isTraitOrSimulation;
	}

	public void setCompetencyID(int iFKCompetency) {
		this.iFKCompetency = iFKCompetency;
	}

	public int getCompetencyID() {
		return iFKCompetency;
	}

	public void setCompetencyName(String sCompetencyName) {
		this.sCompetencyNameArr[0] = sCompetencyName;
	}

	public String getCompetencyName() {
		return sCompetencyNameArr[0];
	}

	public void setCompetencyDefinition(String sCompetencyDefinition) {
		this.sCompetencyDefinitionArr[0] = sCompetencyDefinition;
	}

	public String getCompetencyDefinition() {
		return sCompetencyDefinitionArr[0];
	}

	public String getDescription() {
		return sDescription;
	}

	public void setDescription(String description) {
		sDescription = description;
	}

	public void setResult(double dResult) {
		this.dResult = dResult;
	}

	public double getResult() {
		return dResult;
	}

	public void setPKJobCategoryItem(int iPKJobCategoryItem) {
		this.iPKJobCategoryItem = iPKJobCategoryItem;
	}

	public int getPKJobCategoryItem() {
		return iPKJobCategoryItem;
	}

	public int getAdminSetup() {
		return iAdminSetup;
	}

	public void setAdminSetup(int adminSetup) {
		iAdminSetup = adminSetup;
	}

	public String getRatingCode() {
		return sRatingCode;
	}

	public void setRatingCode(String ratingCode) {
		sRatingCode = ratingCode;
	}

	public int getRatingTaskID() {
		return iRatingTaskID;
	}

	public void setRatingTaskID(int ratingTaskID) {
		iRatingTaskID = ratingTaskID;
	}

	public void setOrigin(String sOrigin) {
		this.sOrigin = sOrigin;
	}

	public String getOrigin() {
		return sOrigin;
	}

	public void setRatingName(String sRatingName) {
		this.sRatingName = sRatingName;
	}

	public String getRatingName() {
		return sRatingName;
	}

	public void setScaleRange(int iScaleRange) {
		this.iScaleRange = iScaleRange;
	}

	public int getScaleRange() {
		return iScaleRange;
	}

	public void setOrganisationName(String sOrganisationName) {
		this.sOrganisationName = sOrganisationName;
	}

	public String getOrganisationName() {
		return sOrganisationName;
	}

	public void setRank(int iRank) {
		this.iRank = iRank;
	}
	public int getRank() {
		return iRank;
	}
	public String getCompetencyName(int lang){
		return sCompetencyNameArr[lang];
	}
	public void setCompetencyName(int lang, String name){
		sCompetencyNameArr[lang] = name;
	}
	public String getCompetencyDefinition(int lang){
		return sCompetencyDefinitionArr[lang];
	}
	public void setCompetencyDefinition(int lang, String definition){
		sCompetencyDefinitionArr[lang] = definition;
	}
	
	public void setAllCompetencyName(String[] arr){
		sCompetencyNameArr = arr;
	}
	public void setCompetencyDefinition(String[] arr){
		sCompetencyDefinitionArr = arr;
	}
	public String[] getAllCompetencyName(){
		return sCompetencyNameArr;
	}
	public String[] getAllCompetencyDefinition(){
		return sCompetencyDefinitionArr;
	}
}
