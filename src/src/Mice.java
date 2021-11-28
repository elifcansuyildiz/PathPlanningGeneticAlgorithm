import java.util.Random;

public class Mice implements Comparable<Mice> {

	World world;
	String successPath = new String();
	char dna[];
	int lengthOfDNA;
	float fitness;
	int numberOfSuccessfulMove;

	public Mice(int lengthOfDNA, World world) {
		this.lengthOfDNA = lengthOfDNA;
		this.world = world;
		dna = new char[lengthOfDNA + 1];
		successPath = "";
	}

	/**
	 * Create random mice DNA
	 */
	public void createDNA() {
		Random rand = new Random();
		int choice;
		int i = 0;
		for (i = 0; i < lengthOfDNA; i++) {
			choice = rand.nextInt(4);
			switch (choice) {
			case 0: {
				dna[i] = Genetic.GO_LEFT_c; // Left

				break;
			}
			case 1: {
				dna[i] = Genetic.GO_UP_c; // Up

				break;
			}
			case 2: {
				dna[i] = Genetic.GO_RIGHT_c; // Right

				break;
			}
			case 3: {
				dna[i] = Genetic.GO_DOWN_c; // Down

				break;
			}
			}
		}
		dna[i] = '\0';
	}

	/**
	 * Print mice DNA sequence
	 */
	public void showDNA() {
		for (int i = 0; i < lengthOfDNA; i++) {
			System.out.print(dna[i]);
		}
	}

	@Override
	public int compareTo(Mice object) {
		Mice f = (Mice) object;

		if (fitness == object.fitness) {
			return 0;
		} else if (fitness > object.fitness)
			return -1;
		else
			return 1;
	}

}
