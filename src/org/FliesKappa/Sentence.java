package org.FliesKappa;



public class Sentence {
	public enum Emotion {INTEREST, ENGAGEMENT, CONFUSION, FRUSTRATION, DISAPPOINTMENT, BOREDOM, HOPEFULNESS, SATISFACTION, NEUTRAL };
	String id;
	String text;
	Emotion emotion;
	public Sentence(String i, String t, Emotion e) {
		id = i;
		text = t;
		emotion = e;
	}
	
	public Sentence(String i, String t) {
		id = i;
		text = t;
	}
	
	public static void main(String args[]) {
		
	}
	
}
