package com.thoughtworks.lariii.model;

import java.util.List;
import java.util.Map;

public class MarkProcessor {

	private Map<Integer, List<Integer>> questionAnswerMap;

	public MarkProcessor(Map<Integer, List<Integer>> questionAnswerMap) {
		this.questionAnswerMap = questionAnswerMap;
	}

	public int getScore(Map<Integer, List<Integer>> answers) {
		int score = 0;
		for (Integer question : answers.keySet()) {
			if (questionAnswerMap.containsKey(question)) {
				for (int i = 0; i < answers.get(question).size()
						&& i < questionAnswerMap.get(question).size(); i++) {
					if (questionAnswerMap.get(question).get(i).equals(answers.get(question).get(i))) {
						score++;
					}
				}
			}
		}
		return score;
	}
}
