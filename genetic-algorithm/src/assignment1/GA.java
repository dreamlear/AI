package assignment1;
/**
 * @author: li zhiyuan
 * @date: 20/9/2018
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;

class Individual implements Comparable<Individual>{
	public int fitness = 0;
	public double[] weights;
	public Individual(){
		weights = new double[GA.weight_num];
		for(int i=0;i<GA.weight_num;++i) {
			//weights[i] = 1;
			weights[i] = (GA.weight_max-GA.weight_min) * Math.random() - GA.weight_max;
		}
	}
	@Override
	public int compareTo(Individual ind2) {
		if(this.fitness > ind2.fitness)
			return -1;
		else if (this.fitness < ind2.fitness)
			return 1;
		else 
			return 0;
	}
	
	public Individual co() {
		Individual ind = new Individual();
		for(int i=0;i<GA.weight_num;++i) {
			ind.weights[i] = this.weights[i];
		}
		ind.fitness = this.fitness;
		return ind;
	}
	
}

public class GA {
	public static int number = 1000; //size of initial generation
	public static int weight_num = 10;
	public static double weight_min = -100;
	public static double weight_max = 100;
	public static ArrayList<Individual> population;
	public static int train_num = 49;
	public static double train[][];
	public static int fitness_sum = 0;
	
	public static void main(String[] args) {
		initial();
		getFitness();
//		for(int i=0;i<population.size();++i) {
//			for(int j=0;j<population.get(i).weights.length;++j)
//				System.out.print(population.get(i).weights[j]+" ");
//			System.out.println();
//		}
		System.out.println("The initial generation's condition of fitness");
		for(int i=0;i<number;++i) {
			System.out.print(population.get(i).fitness+" ");
		}
		System.out.println();
		System.out.println("The average of the fitness is : " + (double)fitness_sum/number);
		System.out.println();
		for(int i=0;i<100;++i) {
			getNextGeneration();
		}
//		for(int i=0;i<population.size();++i) {
//			for(int j=0;j<population.get(i).weights.length;++j)
//				System.out.print(population.get(i).weights[j]+" ");
//			System.out.println();
//		}
		System.out.println("The 500th generation's condition of fitness");
		for(int i=0;i<population.size();++i) {
			System.out.print(population.get(i).fitness+" ");
		}
		System.out.println();
		System.out.println("The average of the fitness is : " + (double)fitness_sum/number);
		
		System.out.println();
		Collections.sort(population);
		System.out.print("The best program is : [ ");
		for(int i=1;i<weight_num;++i) {
			System.out.print(population.get(0).weights[i]+" ");
		}
		System.out.println("]");
		System.out.println("Theta is : "+(0-population.get(0).weights[0]));
	}
	
	public static void initial() {
		train = new double[train_num][weight_num+1];
		getData(train);
		population = new ArrayList<Individual>();
		for(int i=0;i<number;++i) {
			Individual ind = new Individual();
			population.add(ind);
		}
	}
	
	public static void getData(double[][] train) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader("src/assignment1/training-set.csv"));
			String line = null;
			int row = 0;
			while((line = reader.readLine()) != null) {
				String[] items = line.split(",");
				train[row][0] = 1;
				for(int i=1;i<weight_num+1;++i) {
					train[row][i] = Double.parseDouble(items[i-1]);
				}
				row++;
			}
			reader.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void getFitness() {
		fitness_sum = 0;
		for(int i=0;i<number;++i) {
			population.get(i).fitness = 0;
			for(int j=0;j<train_num;++j) {
				double actual = 0;
				for(int k=0;k<weight_num;++k) {
					actual += train[j][k]*population.get(i).weights[k];
				}
				if((actual>=0 && train[j][weight_num] == 1.0) || (actual<0 && train[j][weight_num] == 0.0)) {
					population.get(i).fitness++;
				}
			}
			fitness_sum += population.get(i).fitness;
		}
	}
	
	public static void getNextGeneration() {
		ArrayList<Individual> nextGeneration = new ArrayList<Individual>();
		copy(nextGeneration);
		crossover(nextGeneration);
		mutation(nextGeneration);
		population.clear();
		population = nextGeneration;
		getFitness();
	}
	
	public static void copy(ArrayList<Individual> nextGeneration) {
		Collections.sort(population);
		int count = number/10;
		for(int i=0;i<count;++i) {
			Individual ind = (Individual)population.get(i).co();
			nextGeneration.add(ind);
		}
	}
	
	public static void crossover(ArrayList<Individual> nextGeneration) {
		int count = number * 9 / 20;
		for(int i=0;i<count;++i) {
			Individual ind1 = (Individual)getRandom().co();
			Individual ind2 = (Individual)getRandom().co();
			int index = (int)(Math.random()*weight_num);
			for(int j=index;j<weight_num;++j) {
				double temp = ind1.weights[j];
				ind1.weights[j] = ind2.weights[j];
				ind2.weights[j] = temp;
			}
			nextGeneration.add(ind1);
			nextGeneration.add(ind2);
		}
	}
	
	public static void mutation(ArrayList<Individual> nextGeneration) {
		//get random one from the new generation
		int cnt = (int)(Math.random() * number);
		int index = (int)(Math.random() * weight_num);
		nextGeneration.get(cnt).weights[index] = (GA.weight_max-GA.weight_min) * Math.random() - GA.weight_max;;
	}
	
	public static Individual getRandom() {
		double ran = Math.random();
		double m = 0;
		Individual ind = new Individual();
		for(int j=0;j<number;++j) {
			m = m + ((double)population.get(j).fitness/fitness_sum);
			if(ran<=m) {
				ind = population.get(j);
				break;
			}
		}
		return ind;
	}
	
}
