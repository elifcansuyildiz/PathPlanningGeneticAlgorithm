import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Genetic {

	public static final String GO_LEFT = "L";
	public static final String GO_UP = "U";
	public static final String GO_RIGHT = "R";
	public static final String GO_DOWN = "D";

	public static final char GO_LEFT_c = 'L';
	public static final char GO_UP_c = 'U';
	public static final char GO_RIGHT_c = 'R';
	public static final char GO_DOWN_c = 'D';

	public static final float MAX_POINT = 100.0f;

	public static final int USE_EUCLIDIAN = 1;
	public static final int USE_MANHATTAN = 2;

	ArrayList<Mice> miceList = new ArrayList<Mice>();
	World world;
	int population;
	int lengthOfDNA;
	double selectionRate, mutuationRate;
	int deterministicMode;
	ArrayList<Mice> selectedMice = new ArrayList<Mice>();

	public Genetic(int population, int lengthOfDNA, World world, double selectionRate, double mutuationRate,
			int deterministicMode) {
		this.population = population;
		this.lengthOfDNA = lengthOfDNA;
		this.world = world;
		this.selectionRate = selectionRate;
		this.mutuationRate = mutuationRate;
		this.deterministicMode = deterministicMode;

		for (int i = 0; i < population; i++) {
			miceList.add(new Mice(lengthOfDNA, world));
			miceList.get(i).createDNA();
		}
	}

	/**
	 * Check if mice's DNA includes LR, RL, UD, DU. If then it means penalty
	 * 
	 * @param mice the mice
	 * @param i    DNA index
	 * @return If penalty 1, otherwise 0
	 */
	public int isPenalty(Mice mice, int i) {
		if ((mice.dna[i - 1] == 'L' && mice.dna[i] == 'R') || (mice.dna[i - 1] == 'R' && mice.dna[i] == 'L')
				|| (mice.dna[i - 1] == 'U' && mice.dna[i] == 'D') || (mice.dna[i - 1] == 'D' && mice.dna[i] == 'U')) {
			return 1;
		} else
			return 0;
	}

	public int dnaConflictPenalty(Mice mice) {
		int penalty = 0;
		char dna[] = mice.dna;
		for (int i = 1; i < dna.length; i++) {
			if ((dna[i - 1] == 'L' && dna[i] == 'R') || (dna[i - 1] == 'R' && dna[i] == 'L')
					|| (dna[i - 1] == 'U' && dna[i] == 'D') || (dna[i - 1] == 'D' && dna[i] == 'U')) {
				penalty++;
			}
		}
		return penalty * 2;
	}

	public float deterministic(int x1, int y1, int x2, int y2) {
		if (deterministicMode == USE_EUCLIDIAN) {
			return euclidian(x1, y1, x2, y2);
		} else if (deterministicMode == USE_MANHATTAN) {
			return manhattan(x1, y1, x2, y2);
		}
		System.out.println("Set Deterministic Mode!");
		return -1.0f;
	}

	public float euclidian(int x1, int y1, int x2, int y2) {
		float x_sqr = (float) ((x1 - x2) * (x1 - x2));
		float y_sqr = (float) ((y1 - y2) * (y1 - y2));
		return (float) Math.sqrt(x_sqr + y_sqr);
	}

	public float manhattan(int x1, int y1, int x2, int y2) {
		return Math.abs(x1 - x2) + Math.abs(y1 - y2);
	}

	public void fitnessCalculation(Mice mice) {
		int i, cont, numberOfSuccessfulMove;
		int current_x, current_y, target_x, target_y, start_x, start_y;
		float maxDistance, currentDistance;

		current_x = world.getStart().x;
		current_y = world.getStart().y;
		target_x = world.getStop().x;
		target_y = world.getStop().y;
		start_x = world.getStart().x;
		start_y = world.getStart().y;

		i = 0;
		cont = 1; // continue
		while (i < mice.lengthOfDNA && cont > 0) {
			switch (mice.dna[i]) {
			case 'L': {
				current_x--;
				break;
			}
			case 'R': {
				current_x++;
				break;
			}
			case 'U': {
				current_y--;
				break;
			}
			case 'D': {
				current_y++;
				break;
			}
			}

			// Found target
			if (current_x == target_x && current_y == target_y) {
				cont = 0;
			}
			// Hit the wall
			else if (world.isObstacle(current_x, current_y) == true) {
				cont = -1;
			}

			i++;
		}
		numberOfSuccessfulMove = i - 1;
		mice.numberOfSuccessfulMove = numberOfSuccessfulMove;

		// Found target
		if (cont == 0) {
			mice.fitness = MAX_POINT;
		} else {
			maxDistance = deterministic(start_x, start_y, target_x, target_y);
			currentDistance = deterministic(current_x, current_y, target_x, target_y);
			mice.fitness = (float) (MAX_POINT * (1.0f - (currentDistance / maxDistance)));
			// mice.fitness *= (float) ( ((float)numberOfSuccessfulMove /
			// (float)lengthOfDNA));
			// mice.fitness *= (float) ( 1.0 - (dnaConflictPenalty(mice) /
			// (float)lengthOfDNA) );
		}

	}

	public int showFitness() {
		for (int i = 0; i < population; i++) {
			fitnessCalculation(miceList.get(i));
			// System.out.println(" fitness= " + miceList.get(i).fitness + " numberofMove= "
			// + miceList.get(i).numberOfSuccessfulMove);

			miceList.get(i).world.drawGen(new String(miceList.get(i).dna), world.randomColor(),
					miceList.get(i).numberOfSuccessfulMove);

			if (miceList.get(i).fitness >= MAX_POINT) {
				return 1;
			}
		}
		return 0;
	}

	public void selection() {
		Collections.sort(miceList);

		selectedMice.clear();

		for (int j = 0; j < (selectionRate * population); j++) {
			selectedMice.add(miceList.get(j));
		}
	}

	public void crossover() {
		miceList.clear();
		Random rand = new Random();
		for (int k = 0; k < population / 2 + population % 2; k++) {

			int mice1 = (int) (rand.nextDouble() * selectionRate * population); // selected mice1 index
			int mice2; // selected mice2 index
			do {
				mice2 = (int) (rand.nextDouble() * selectionRate * population); // update mice2 if mice1==mice2
			} while (mice2 == mice1);

			int crossoverPoint = rand.nextInt(lengthOfDNA) + 1;

			miceList.add(new Mice(lengthOfDNA, world));
			miceList.add(new Mice(lengthOfDNA, world));

			for (int j = 0; j < lengthOfDNA; j++) {
				if (j < crossoverPoint) {
					miceList.get(2 * k).dna[j] = selectedMice.get(mice1).dna[j];
					miceList.get(2 * k + 1).dna[j] = selectedMice.get(mice2).dna[j];
				} else {
					miceList.get(2 * k).dna[j] = selectedMice.get(mice2).dna[j];
					miceList.get(2 * k + 1).dna[j] = selectedMice.get(mice1).dna[j];
				}
			}
		}
	}

	public void mutuation() {
		Random rand = new Random();
		int choice;

		for (int i = 0; i < population; i++) {
			for (int j = 0; j < lengthOfDNA; j++) {
				if (rand.nextFloat() < mutuationRate) {

					choice = rand.nextInt(4);

					switch (choice) {
					case 0: {
						miceList.get(i).dna[j] = Genetic.GO_LEFT_c; // Left

						break;
					}
					case 1: {
						miceList.get(i).dna[j] = Genetic.GO_UP_c; // Up

						break;
					}
					case 2: {
						miceList.get(i).dna[j] = Genetic.GO_RIGHT_c; // Right

						break;
					}
					case 3: {
						miceList.get(i).dna[j] = Genetic.GO_DOWN_c; // Down

						break;
					}
					}
				}
			}
		}
	}

}
