/*
 * Author: Dai Yong
 * June 2013
 */
package CP_Classes.vo;

public class voCoachSessionDate {

	int PK;
	int SessionFK;
	int DateFK;
	int CoachFK;
	public voCoachSessionDate() {
		super();
	}
	public voCoachSessionDate(int pK, int sessionFK, int dateFK, int coachFK) {
		super();
		PK = pK;
		SessionFK = sessionFK;
		DateFK = dateFK;
		CoachFK = coachFK;
	}
	public int getPK() {
		return PK;
	}
	public void setPK(int pK) {
		PK = pK;
	}
	public int getSessionFK() {
		return SessionFK;
	}
	public void setSessionFK(int sessionFK) {
		SessionFK = sessionFK;
	}
	public int getDateFK() {
		return DateFK;
	}
	public void setDateFK(int dateFK) {
		DateFK = dateFK;
	}
	public int getCoachFK() {
		return CoachFK;
	}
	public void setCoachFK(int coachFK) {
		CoachFK = coachFK;
	}
	
	
	
}
