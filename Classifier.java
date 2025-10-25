import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class Classifier 
{

    /*************************************************************************/

	//Record class (inner class)
    private class Record 
    {
        private int[][] attributes;         //attributes of record      
        private int className;               //class of record

        //Constructor of Record
        private Record(int[][] attributes, int className)
        {
            this.attributes = attributes;    //set attributes 
            this.className = className;      //set class
        }
    }
    
    /*************************************************************************/
    
    private int numberRecords;				 //number of records 
    private int numberAttributes;            //number of attributes   
    private int numberClasses;               //number of classes
    private int numberNeighbors;             //number of nearest neighbors
    private ArrayList<Record> records;       //list of training records
    
    /*************************************************************************/

    public Classifier(int neighbor)
    {
    	//initial data is empty           
        numberRecords = 0;      
        numberAttributes = 0;
        numberClasses = 0;
        numberNeighbors = neighbor; 
        records = null;    
    }
    
    /*************************************************************************/
    
    //Method loads data from training file
    public void loadTrainingData(String trainingFile) throws IOException
    {
    	Scanner inFile = new Scanner(new File(trainingFile));
    	
    	//read number of records, attributes, classes
        numberRecords = inFile.nextInt();
        numberAttributes = inFile.nextInt();
        numberClasses = inFile.nextInt();
        
        //create empty list of records
        records = new ArrayList<Record>();
        
        //for each record
        for(int x = 0 ; x<numberRecords ; x++)
        {
        	//create attribute array
        	int attributeArray[][] = new int[numberAttributes][numberAttributes];
        	
        	//read a record
        	for(int i=0; i<numberAttributes ; i++)
        	{
        		for(int j=0; j<numberAttributes ; j++)
        		{
        			attributeArray[i][j] = inFile.nextInt();
        		}
        	}
        	
        	//read class name
        	int className = inFile.nextInt();
        	
        	//create a record 
        	Record oneRecord = new Record(attributeArray,className);
        	
        	//add a record to record list 
        	records.add(oneRecord);
        	
        }
        	
    	inFile.close();
    }
    
    /*************************************************************************/
    
    //Method reads records from test file, determines their classes, 
    //and writes classes to classified file
    public void classifyData(String testFile, String classifiedFile) throws IOException
    {
    	Scanner inFile = new Scanner(new File(testFile));
        PrintWriter outFile = new PrintWriter(new FileWriter(classifiedFile));
        
        //read number of records
        int numberRecords = inFile.nextInt();

        //write number of records
        outFile.println(numberRecords);
        
        //for each record
        for(int x=0 ; x<numberRecords ; x++)
        {
        	//create an attribute array
        	int[][] attributeArray = new int[numberAttributes][numberAttributes];
        	
        	//read a record
        	//read a record
        	for(int i=0; i<numberAttributes ; i++)
        	{
        		for(int j=0; j<numberAttributes ; j++)
        		{
        			attributeArray[i][j] = inFile.nextInt();
        		}
        	}
        	
        	//find a class of an attribute
        	int className = classify(attributeArray);
        	
        	//write class name 
        	outFile.println(className);
        }
        
        outFile.println("\nValidation error : %"+validate());
        outFile.println("Number of nearest neighbor : "+numberNeighbors);
        
        inFile.close();
        outFile.close();
    }
    
    /*************************************************************************/
    
    //method determines a class of a given attribute
    private int classify(int[][] attributesArray)
    {
    	double[] distance = new double[numberRecords];
        int[] id = new int[numberRecords];
        
        //find distance between attribute and all records
        for(int x=0; x < numberRecords ; x++)
        {
        	distance[x] = distance(attributesArray , records.get(x).attributes);
        	id[x] = x;
        }
        
        //find nearest neighbors 
        nearestNeighbor(distance,id);
        
        //find majority class of nearest neighbors
        int className = majority(id);
        
        //return class name
    	return className;
    }
    
    /*************************************************************************/
    
    //method calculates the distance between two points using binary distance 
    private double distance(int[][] u , int[][] v)
    {
    	int numberMismatches=0;
    	
    	//find how many mismatches between two arrays
    	for(int x = 0 ; x<u.length; x++)
    	{
    		for(int y = 0 ; y<u.length ; y++)
    		{
    			if(u[x][y] != v[x][y])
    				numberMismatches +=1;
    		}
    	}
    	
    	//total number of digit 16x16
    	int length = 256;
    	
    	//calculate distance 
    	double distance =(double) numberMismatches/length;
    	
    	//return distance 
    	return distance;
    }
    
    /*************************************************************************/
    
    //method finds the nearest neighbor 
    private void nearestNeighbor(double[] distance , int[] id)
    {
        //sort distances and choose nearest neighbors
        for (int i = 0; i < numberNeighbors; i++)
            for (int j = i; j < numberRecords; j++)
                if (distance[i] > distance[j])
                {
                    double tempDistance = distance[i];
                    distance[i] = distance[j];
                    distance[j] = tempDistance;

                    int tempId = id[i];
                    id[i] = id[j];
                    id[j] = tempId;
                }
    }
    
    /*************************************************************************/
    
    //method finds the majority class of nearest neighbors
    private int majority(int[] id)
    {
    	double[] frequency = new double[numberClasses];

        //class frequencies are zero initially
        for (int i = 0; i < numberClasses; i++)
            frequency[i] = 0;

        //each neighbor contributes 1 to its class
        for (int i = 0; i < numberNeighbors; i++)
            frequency[records.get(id[i]).className] += 1;

        //find majority class
        int maxIndex = 0;                         
        for (int i = 0; i < numberClasses; i++)   
            if (frequency[i] > frequency[maxIndex])
               maxIndex = i;

        return maxIndex ;
    }
    
    /*************************************************************************/
    
    //method validates records using leave one out validation
    private double validate()
    {
    	int numberErrors = 0;	//number of errors
    	
    	for(int x = 0 ; x<numberRecords ; x++)
    	{
    		//remove one record for validation
    		Record temp = records.remove(x);
    		numberRecords-=1;
    		
    		//create an attribute out of record
    		int[][] attribute = temp.attributes;
    		
    		//get actual class 
    		int actualClass = temp.className;
    		
    		//predict class based on the remaining records
    		int predictedClass = classify(attribute);
    		
    		//if predicted class does not match actual class
    		if(predictedClass != actualClass)
    			numberErrors +=1;	//increment number of errors
    		
    		//adding the record back to list 
    		records.add(x, temp);
    		numberRecords+=1;
    	}
    	
    	//calculate the error rate 
    	double errorRate = (numberErrors/numberRecords)*100;
    	
    	//return error rate
    	return errorRate;
    }
    
    
}
