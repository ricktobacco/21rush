package casino;

import java.util.Scanner;

class InputHelper {
	private static Scanner sc = new Scanner(System.in);
	//Reads in a string given a prompt to ask the user
	String readString(String promptToPrint) {
		String s;
		do {
			System.out.println(promptToPrint);
			s = sc.next();
		} while (s.equals(""));
		return s;
	}
	///Reads in a bool based on a question that is being asked to the user
	boolean readBoolean(String question) {
		boolean isYes = true;
		//make a variable to help figure out if the input from user is good
		boolean isGoodInput = false; // assume NOT good to start
		while(!isGoodInput) {
			System.out.println(question + "\nEnter either '1' or 'stay' to stay. Enter either '2' or 'hit' to hit.");
			String userAnswer = sc.next();

			if(userAnswer.startsWith("hit") || userAnswer.equals("2")) {
				isYes = true;
				isGoodInput = true;
			}
			else if(userAnswer.startsWith("stay") || userAnswer.equals("1")) {
				isYes = false;
				isGoodInput = true;
			}
			else {
				System.out.println("Invalid input.");
				isGoodInput = false;
			}
		}

		return isYes;
	}
	int readIntBetween(String prompt, int minInc, int maxInc) {
		int num = 0;
		boolean isValid = false;

		while(!isValid) {
			System.out.println(prompt);
			if(sc.hasNextInt()) {
				num = sc.nextInt();
				if(num >= minInc && num <= maxInc){
					isValid = true;
				}
				else {
					System.out.println("Your input must be between " + minInc + " and  " + maxInc);
					isValid = false;
				}
			}
			else {
				System.out.println("Invalid input.");
				sc.next();
			}
		}
		return num;
	}
}
