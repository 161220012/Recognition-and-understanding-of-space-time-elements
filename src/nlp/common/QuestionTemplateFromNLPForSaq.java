package nlp.common;

import java.util.ArrayList;

public class QuestionTemplateFromNLPForSaq extends QuestionTemplateFromNLP{
	/**
	 * 槽信息列表
	 */
	public ArrayList<SlotStructureFromNLPForSaq> slots ;
	/**
	 * toString
	 */
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append(templateType + "(");
		boolean first = false;
		for (SlotStructureFromNLPForSaq slot : slots) {
			if (!first) {
				result.append(slot);
				first = true;
			} else 
				result.append("," + slot);
		}
		result.append(")");
		return result.toString();
	}
}
