import java.io.IOException;

public class Main {

    public static void main(String arg[]) throws IOException, InterruptedException{
    	
    	// Default parameters
    	String worldFile = "world.png";
        int population = 100;
        int lenghtOfDNA = 300;
        double selectionRate = 0.30;
        double mutuationRate = 0.005;
        int threadSleep = 20;
        int distanceMethod = 1; 
    	
    	if (arg.length > 0)
    	{
    		if (arg.length == 7)
    		{
    			worldFile = arg[0];
    			threadSleep = Integer.parseInt(arg[1]);
    			population = Integer.parseInt(arg[2]);
    			lenghtOfDNA = Integer.parseInt(arg[3]);
    			selectionRate = Double.parseDouble(arg[4]);
    			mutuationRate = Double.parseDouble(arg[5]);
    			distanceMethod = Integer.parseInt(arg[6]);
    		}
    		else
    		{
    			System.err.println("Wrong number of parameters");
    			System.exit(-1);
    		}
    	}
    	
        World p = new World(worldFile);
        
        // Set start-stop point
        p.setStart(p.getWidth()/2, p.getHeight()/2); // center
        p.setStop(1, 1); // top-left corner
        
        int numOfGeneration=0;
        int cont = 1; // continue flag
        Genetic g=new Genetic(population,lenghtOfDNA,p,selectionRate, mutuationRate, distanceMethod);
        p.updateDrawings();
        while (cont == 1){
            numOfGeneration++;
            System.out.println("GENERATION: " + numOfGeneration);
            p.clearDrawings();
            g.selection();
            g.crossover();
            g.mutuation();
            if(g.showFitness()==1) 
            	cont = 0;
            p.updateDrawings();
            Thread.sleep(threadSleep);
        }
    }
}

