/******************************************************************************
*  A Teaching GA					  Developed by Hal Stringer & Annie Wu, UCF
*  Version 2, January 18, 2004
*******************************************************************************/

import java.util.*;

public class Chromo
{
/*******************************************************************************
*                            INSTANCE VARIABLES                                *
*******************************************************************************/

	public String chromo;
	public double rawFitness;
	public double sclFitness;
	public double proFitness;

/*******************************************************************************
*                            INSTANCE VARIABLES                                *
*******************************************************************************/

	private static double randnum;

/*******************************************************************************
*                              CONSTRUCTORS                                    *
*******************************************************************************/

	public Chromo(){

		//  Set gene values to a randum sequence of 1's and 0's
		char geneBit;
		chromo = "";
		for (int i=0; i<Parameters.numGenes; i++){
			for (int j=0; j<Parameters.geneSize; j++){
				randnum = Search.r.nextDouble();
				if (randnum > 0.5) geneBit = '0';
				else geneBit = '1';
				this.chromo = chromo + geneBit;
			}
		}

		this.rawFitness = -1;   //  Fitness not yet evaluated
		this.sclFitness = -1;   //  Fitness not yet scaled
		this.proFitness = -1;   //  Fitness not yet proportionalized
	}


/*******************************************************************************
*                                MEMBER METHODS                                *
*******************************************************************************/

	//  Get Alpha Represenation of a Gene **************************************

	public String getGeneAlpha(int geneID){
		int start = geneID * Parameters.geneSize;
		int end = (geneID+1) * Parameters.geneSize;
		String geneAlpha = this.chromo.substring(start, end);
		return (geneAlpha);
	}

	//  Get Integer Value of a Gene (Positive or Negative, 2's Compliment) ****

	public int getIntGeneValue(int geneID){
		String geneAlpha = "";
		int geneValue;
		char geneSign;
		char geneBit;
		geneValue = 0;
		geneAlpha = getGeneAlpha(geneID);
		for (int i=Parameters.geneSize-1; i>=1; i--){
			geneBit = geneAlpha.charAt(i);
			if (geneBit == '1') geneValue = geneValue + (int) Math.pow(2.0, Parameters.geneSize-i-1);
		}
		geneSign = geneAlpha.charAt(0);
		if (geneSign == '1') geneValue = geneValue - (int)Math.pow(2.0, Parameters.geneSize-1);
		return (geneValue);
	}

	//  Get Integer Value of a Gene (Positive only) ****************************

	public int getPosIntGeneValue(int geneID){
		String geneAlpha = "";
		int geneValue;
		char geneBit;
		geneValue = 0;
		geneAlpha = getGeneAlpha(geneID);
		for (int i=Parameters.geneSize-1; i>=0; i--){
			geneBit = geneAlpha.charAt(i);
			if (geneBit == '1') geneValue = geneValue + (int) Math.pow(2.0, Parameters.geneSize-i-1);
		}
		return (geneValue);
	}

	//  Mutate a Chromosome Based on Mutation Type *****************************

	public void doMutation(){

		String mutChromo = "";
		char x;

		switch (Parameters.mutationType){

		case 1:     //  Replace with new random number
			//System.out.println("Chromosome before Bit Flip mutation: "+ this.chromo);
			for (int j=0; j<(Parameters.geneSize * Parameters.numGenes); j++){
				x = this.chromo.charAt(j);
				randnum = Search.r.nextDouble();
				if (randnum < Parameters.mutationRate){
					if (x == '1') x = '0';
					else x = '1';
				}
				mutChromo = mutChromo + x;
			}
			this.chromo = mutChromo;
			//System.out.println("Chromosome After Bit Flip mutation: "+ this.chromo);

			break;

		case 2:     //  Right shift mutation

			randnum = Search.r.nextDouble();
			if (randnum < Parameters.mutationRate){
				//System.out.println("Chromosome before Right shift mutation: "+ this.chromo);
				//Convert binary string to Integer
				char end = this.chromo.charAt(this.chromo.length()-1);
				this.chromo = end + this.chromo.substring(0,this.chromo.length()-1);
				//System.out.println("Chromosome after Right shift mutation: "+ this.chromo);
			}
			break;
		case 3:     //  Left shift mutation

			randnum = Search.r.nextDouble();
			if (randnum < Parameters.mutationRate){
				//System.out.println("Chromosome before Left shift mutation: "+ this.chromo);
				char end = this.chromo.charAt(0);
				this.chromo = this.chromo.substring(1)+end;
				//System.out.println("Chromosome after Left shift mutation: "+ this.chromo);

			}
			break;
		case 4: 	// Random Bit Swap

			randnum = Search.r.nextDouble();
			if (randnum < Parameters.mutationRate){
				//System.out.println("Chromosome before bit swap mutation: "+ this.chromo);
				char[] charArray = this.chromo.toCharArray();
				Random random = new Random();
				// Generate random positions to swap
				int bit1 = random.nextInt(this.chromo.length());
				int bit2 = random.nextInt(this.chromo.length());

				//System.out.println("Swapping Bits "+bit1+" and "+bit2);
				
				// Swap bits at the selected positions
				char temp = charArray[bit1];
				charArray[bit1] = charArray[bit2];
				charArray[bit2] = temp;

				this.chromo = new String(charArray);
				//System.out.println("Chromosome after bit swap mutation: "+ this.chromo);
			}
			break;
		default:
			System.out.println("ERROR - No mutation method selected");
		}

	}

/*******************************************************************************
*                             STATIC METHODS                                   *
*******************************************************************************/

	//  Select a parent for crossover ******************************************

	public static int selectParent(int selection_chance) {

		double rWheel = 0;
		int j = 0;
		int k1 = 0;
		int k2 = 0;

		switch (Parameters.selectType) {

		case 1:     // Proportional Selection
			randnum = Search.r.nextDouble();
			for (j=0; j<Parameters.popSize; j++) {
				rWheel = rWheel + Search.member[j].proFitness;
				if (randnum < rWheel) return(j);
			}
			break;

		case 2:     //  Tournament Selection
			// tournamentSize can be changed
			int tournamentSize = 5;
			double max = 0.0;
			int savedRand = 0;
			for (j=0; j<tournamentSize; j++) {
				Random rand = new Random();
				int randomNumber = rand.nextInt(Parameters.popSize);
				if(Search.member[randomNumber].proFitness > max) {
					max = Search.member[randomNumber].proFitness;
					savedRand = randomNumber;
				}
			}
			return(savedRand);

		case 3:     // Random Selection
			randnum = Search.r.nextDouble();
			j = (int) (randnum * Parameters.popSize);
			return(j);

		case 4:		//Rank Selection
			Chromo[] ranked = Search.member;
			Arrays.sort(ranked, (o1, o2) -> Double.compare(o1.proFitness,o2.proFitness));
			float ranksum = (float) (Parameters.popSize * (1 + Parameters.popSize)) / 2;
			randnum = Search.r.nextDouble();
			int randnum1 = (int) Math.floor((ranksum * randnum));
			int tempr = 0;
			for(int i = Parameters.popSize-1; i > 0; i--) {
				tempr = tempr + i + 1;
				if(tempr>=randnum1) {
					return i;
				}
			}
			break;


		default:
			System.out.println("ERROR - No selection method selected");
		}
	return(-1);
	}

	//  Produce a new child from two parents  **********************************

	public static void mateParents(int pnum1, int pnum2, Chromo parent1, Chromo parent2, Chromo child1, Chromo child2){

		int xoverPoint1;
		int xoverPoint2;

		switch (Parameters.xoverType){

		case 1: //  Single Point Crossover
				//  Select crossover point
				xoverPoint1 = 1 + (int)(Search.r.nextDouble() * (Parameters.numGenes * Parameters.geneSize-1));
				//  Create child chromosome from parental material
				child1.chromo = parent1.chromo.substring(0, xoverPoint1) + parent2.chromo.substring(xoverPoint1);
				child2.chromo = parent2.chromo.substring(0, xoverPoint1) + parent1.chromo.substring(xoverPoint1);
			break;

		case 2:     //  Two Point Crossover
				//  Select crossover point
				xoverPoint1 = 1 + (int)(Search.r.nextDouble() * (Parameters.numGenes * Parameters.geneSize-1));
				xoverPoint2 = 1 + (int)(Search.r.nextDouble() * (Parameters.numGenes * Parameters.geneSize-1));

				if(xoverPoint1 > xoverPoint2) {
					int temp = xoverPoint2;
					xoverPoint2 = xoverPoint1;
					xoverPoint1 = temp;
				}

				//  Create child chromosome from parental material
				child1.chromo = parent1.chromo.substring(0, xoverPoint1) + parent2.chromo.substring(xoverPoint1, xoverPoint2) + parent1.chromo.substring(xoverPoint2);
				child2.chromo = parent2.chromo.substring(0, xoverPoint1) + parent1.chromo.substring(xoverPoint1, xoverPoint2) + parent2.chromo.substring(xoverPoint2);
			break;
		case 3:     //  Uniform Crossover

		default:
			System.out.println("ERROR - Bad crossover method selected");
		}

		//  Set fitness values back to zero
		child1.rawFitness = -1;   //  Fitness not yet evaluated
		child1.sclFitness = -1;   //  Fitness not yet scaled
		child1.proFitness = -1;   //  Fitness not yet proportionalized
		child2.rawFitness = -1;   //  Fitness not yet evaluated
		child2.sclFitness = -1;   //  Fitness not yet scaled
		child2.proFitness = -1;   //  Fitness not yet proportionalized
	}

	//  Produce a new child from a single parent  ******************************

	public static void mateParents(int pnum, Chromo parent, Chromo child){

		//  Create child chromosome from parental material
		child.chromo = parent.chromo;

		//  Set fitness values back to zero
		child.rawFitness = -1;   //  Fitness not yet evaluated
		child.sclFitness = -1;   //  Fitness not yet scaled
		child.proFitness = -1;   //  Fitness not yet proportionalized
	}

	//  Copy one chromosome to another  ***************************************

	public static void copyB2A (Chromo targetA, Chromo sourceB){

		targetA.chromo = sourceB.chromo;

		targetA.rawFitness = sourceB.rawFitness;
		targetA.sclFitness = sourceB.sclFitness;
		targetA.proFitness = sourceB.proFitness;
		return;
	}

}   // End of Chromo.java ******************************************************
