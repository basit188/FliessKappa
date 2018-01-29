package org.FliesKappa;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Set;

import org.FliesKappa.Sentence.Emotion;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class FliessKappa {
	LinkedHashMap<String, Sentence> sentence_list;
	LinkedHashMap<String, Sentence> annotator_list[];
	LinkedHashMap<String, Integer[]> matrix;
	final int numAnnotator = 5;
	int presentAnnotators = 0;
	int numSentences = 0;
	double pBar = 0.0, pE = 0.0, kappa = 0.0;
	double propSentence[], propEmotion[];

	public FliessKappa() {
		sentence_list = new LinkedHashMap<>();
		matrix = new LinkedHashMap<>();
		annotator_list = new LinkedHashMap[numAnnotator];
		for (int i = 0; i < numAnnotator; i++)
			annotator_list[i] = new LinkedHashMap<String, Sentence>();
		loadAnnotatorList();
		propSentence = new double[sentence_list.size()];
		propEmotion = new double[Emotion.values().length];
		generateMatrix();
		calculateKappa();
		printMatrix();
	}

	private void loadAnnotatorList() {
		File f = new File("input");
		JSONParser parser = new JSONParser();
		if (f.isDirectory()) {
			File files[] = f.listFiles();
			presentAnnotators = files.length;
			for (int an = 0; an < files.length && an < numAnnotator; an++) {
				try {
					JSONArray obj = (JSONArray) parser.parse(new FileReader(files[an]));
					sentence_list = new LinkedHashMap<>();
					for (int i = 0; i < obj.size(); i++) {
						JSONObject o = (JSONObject) obj.get(i);
						String id = (String) o.get("id");
						String emot = (String) o.get("emotion");
						String txt = (String) o.get("text");
						sentence_list.put(id, new Sentence(id, txt, getEmotionFromString(emot)));
					}
					annotator_list[an] = sentence_list;
				} catch (IOException | ParseException e) {
					e.printStackTrace();
				}
			}
			sentence_list.size();
		} else {
			System.out.println("Input is not a directory");
		}
	}

	private void printSentenceList() {
		Set<String> keys = sentence_list.keySet();
		Sentence s;
		for (String k : keys) {
			s = sentence_list.get(k);
			System.out.println(s.id + "\t" + s.text + "\t" + s.emotion);
		}
	}

	private void generateMatrix() {
		Set<String> sids = sentence_list.keySet();
		Integer emotion_array[];
		int eLen = Emotion.values().length;
		for (String sid : sids) {
			Integer eArray[] = new Integer[eLen];
			for (int i = 0; i < eArray.length; i++)
				eArray[i] = 0;
			matrix.put(sid, eArray);
		}

		for (String sid : sids) {
			for (int i = 0; i < presentAnnotators; i++) {
				emotion_array = matrix.get(sid);
				emotion_array[annotator_list[i].get(sid).emotion.ordinal()]++;
				matrix.remove(sid);
				matrix.put(sid, emotion_array);
			}
		}
	}

	private void calculateKappa() {
		// ---------------- calculating propSentence[] ----------------------
		Set<String> keys = matrix.keySet();
		Integer emotions[] = null;
		int i = 0;
		double ps = 0.0;

		for (String key : keys) {
			emotions = matrix.get(key);
			ps = 0.0;
			for (int j = 0; j < emotions.length; j++) {
				ps += emotions[j] * emotions[j];
			}

			ps = ps - presentAnnotators;
			ps = ps / (presentAnnotators * (presentAnnotators - 1));
			propSentence[i] = ps;
			i++;
		}

		pBar = 0.0;
		for (int j = 0; j < propSentence.length; j++)
			pBar += propSentence[j];
		pBar = pBar / propSentence.length;
		System.out.println("pBar = " + pBar);

		// ----------------------calculating propEmotion[]----------------------
		double es = 0.0;
		double esSum = 0.0;
		for (i = 0; i < propEmotion.length; i++) {
			es = 0.0;
			for (String key : keys)
				es += matrix.get(key)[i];
			propEmotion[i] = es;
			esSum += es;
		}
		
		pE = 0.0;
		for(i =0; i< propEmotion.length; i++) {
			es = propEmotion[i];
			es = es/esSum;
			es = es * es;
			pE += es;
		}

		
//		for (i = 0; i < propEmotion.length; i++)
//			pE += propEmotion[i];
		System.out.println("PE = " + pE);
		
		// ----------------------calculating propEmotion[]----------------------
		kappa = (pBar - pE) / (1 - pE);
		System.out.println("Kappa = " + kappa);
		
	}

	private void printMatrix() {
		Set<String> keys = matrix.keySet();
		Integer ear[];
		System.out.printf("%8s", " ");
		for (int i = 0; i < Emotion.values().length; i++) {
			System.out.printf("%15s", Emotion.values()[i]);
		}
		System.out.println("");
		for (String key : keys) {
			System.out.print(key + "\t");
			ear = matrix.get(key);
			for (int i = 0; i < ear.length; i++) {
				System.out.printf("%15d", ear[i]);
			}
			System.out.println("");
		}

	}

	public Emotion getEmotionFromString(String e) {

		if (e.equalsIgnoreCase("INTEREST")) {
			return Emotion.INTEREST;
		} else if (e.equalsIgnoreCase("ENGAGEMENT")) {
			return Emotion.ENGAGEMENT;
		} else if (e.equalsIgnoreCase("CONFUSION")) {
			return Emotion.CONFUSION;
		} else if (e.equalsIgnoreCase("FRUSTRATION")) {
			return Emotion.FRUSTRATION;
		} else if (e.equalsIgnoreCase("DISAPPOINTMENT")) {
			return Emotion.DISAPPOINTMENT;
		} else if (e.equalsIgnoreCase("BOREDOM")) {
			return Emotion.BOREDOM;
		} else if (e.equalsIgnoreCase("HOPEFULNESS")) {
			return Emotion.HOPEFULNESS;
		} else if (e.equalsIgnoreCase("SATISFACTION")) {
			return Emotion.SATISFACTION;
		} else if (e.equalsIgnoreCase("NEUTRAL")) {
			return Emotion.NEUTRAL;
		}
		return null;

	}

	public static void main(String args[]) {
		new FliessKappa();
	}
}
