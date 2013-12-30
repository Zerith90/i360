package CP_Classes;

public class PrelimQuestion implements Comparable{
	
	private int prelimQnId;
	private int fkSurveyId;
	private String question;
	private int prelimRatingScaleId;
	
	public PrelimQuestion(int prelimQnId, int fkSurveyId, String question, int prelimRatingScaleId)
	{
		this.prelimQnId = prelimQnId;
		this.fkSurveyId = fkSurveyId; 
		this.question = question;
		this.prelimRatingScaleId = prelimRatingScaleId;
	}
	
	public int getPrelimQnId()
	{
		return prelimQnId;
	}
	
	public String getQuestion()
	{
		return question;
	}
	
	public int getPrelimRatingScaleId(){
		return prelimRatingScaleId;
	}
	
	/**
	 * @author xukun
	 * @param o
	 * @return
	 */
	public int compareTo(Object o){
		return this.prelimQnId - ((PrelimQuestion)o).getPrelimQnId();
	}

}