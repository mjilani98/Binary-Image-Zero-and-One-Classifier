import java.io.IOException;
import java.util.Scanner;

public class Driver {

	public static void main(String[] args) throws IOException
	{
		//Scanner object 
		Scanner input = new Scanner(System.in);
		
		//get the training data file 
		System.out.println("Enter the name of Training data file : ");
		String inputTrainingFile = input.nextLine();
		//get the test data file
		System.out.println("Enter the name of Test data file : ");
		String inputTestFile = input.nextLine();
		//get the classified data file
		System.out.println("Enter the name of classified file :");
		String classifiedFile = input.nextLine();
		
		//create a classifier object 
		Classifier classifier = new Classifier(3);
		
		//load training file to classifier
		classifier.loadTrainingData(inputTrainingFile);
		
		//classify test data
		classifier.classifyData(inputTestFile, classifiedFile);

	}

}
