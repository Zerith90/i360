/*
 * Author: Dai Yong
 * June 2013
 */
package CP_Classes.vo;

public class voCoachDateGroup {
	int PK=0;
	String name="";
	String description="";
	
	
	public voCoachDateGroup() {
		super();
	}


	public voCoachDateGroup(int pK, String name, String description) {
		super();
		PK = pK;
		this.name = name;
		this.description = description;
	}


	public int getPK() {
		return PK;
	}


	public void setPK(int pK) {
		PK = pK;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getdescription() {
		return description;
	}


	public void setdescription(String description) {
		this.description = description;
	}
	

}
