import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class TravelingSalesman {

    private static int[][] map;
    private static int towncount = 0;
    private static int ngenes = 100;
    private static int maxgenerations = 2000;
    private static double pc = 0.95;
    private static double pm = 0.015;
    private static boolean protect_best = true;
    private static int relication = 2;
    private static double[][] distance;
    private static Genome genome;
    private static double bestpossible = 41.66;
    private static double da;

    public static void main(String[] args) throws java.io.IOException {
        int rounds = 10;
        String input = "C:\\Users\\Robert\\Desktop\\GenetischeAlgorithmen\\TravelingSalesman\\src\\06-map-100x100-50.txt";

        setup(input);
        genome = new Genome(ngenes, towncount);
        distance = new double[towncount][towncount];
        getDistance();
        da = 0.765 * Math.sqrt((towncount+1)*map.length);

        //int j = 0;
        //for (pc = 0.0; pc <= 1; pc += 0.05) {
        //    for (pm = 0.0; pm <= 0.2; pm += 0.005) {
        //        double x = 0;
        //        for (int i = 0; i < rounds; i++) {
        //            j = gen();
        //            if (j == maxgenerations)
        //                break;
        //            x += j;
        //        }
        //        if (j == maxgenerations)
        //            System.out.println(String.format(Locale.US, "%.2f\t%.3f\t%d", pc, pm, j));
        //        else
        //            System.out.println(String.format(Locale.US, "%.2f\t%.3f\t%d", pc, pm, Math.round(x / (double) rounds)));
//
        //    }
        //    System.out.print("\n");
        //}


        List<Double> list = new ArrayList<>();
        for (int i = 0; i < 100; i++){
            gen();
            list.add(checkBest().fitness);
        }
        list.sort(Comparator.naturalOrder());
        for(int i = 0;i < list.size();i++)
            System.out.println(i+1 + "\t" + list.get(i));
    }

    private static void initgenome() {
        for (int i = 0; i < ngenes; i++) {
            for (int pos = 0; pos < towncount; pos++) {
                genome.genes[i].towns[pos] = pos + 1;
            }
            genome.genes[i].shuffle();
        }
    }

    private static void setup(String input) throws java.io.IOException {
        List<int[]> list = new ArrayList<>();

        Scanner sc = new Scanner(new BufferedReader(new FileReader(input)));
        while (sc.hasNextLine()) {
            int[] temp = Arrays.stream(sc.nextLine().trim().split(" +")).mapToInt(Integer::parseInt).toArray();
            list.add(temp);
        }

        map = list.toArray(new int[list.size()][list.size()]);

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map.length; j++) {
                if (map[i][j] != 0)
                    towncount++;
            }
        }
    }

    public static void getDistance() {
        for (int i = 0; i < distance.length; i++) {
            for (int j = 0; j < distance.length; j++) {
                if (i == j)
                    distance[i][j] = 0;
                else
                    distance[i][j] = distanceBetween(i, j);
            }
        }
    }

    static double distanceBetween(int x, int y) {
        double x1 = 0;
        double x2 = 0;
        double y1 = 0;
        double y2 = 0;
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map.length; j++) {
                if (map[i][j] == (x + 1)) {
                    x1 = i;
                    y1 = j;
                } else if (map[i][j] == (y + 1)) {
                    x2 = i;
                    y2 = j;
                }
            }
        }
        return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }

    private static int get_random_number(int von, int bis) {
        Random r = new Random();
        return r.nextInt(bis) + von;
    }

    private static void mutation() {
        Gene best = null;
        if (protect_best)
            best = checkBest();
        for (int i = 0; i < (ngenes * towncount * pm); i++) {
            int p = get_random_number(0, ngenes);
            int q = get_random_number(0, towncount);
            int o = get_random_number(0, towncount);

            int temp = genome.genes[p].towns[q];
            genome.genes[p].towns[q] = genome.genes[p].towns[o];
            genome.genes[p].towns[o] = temp;
        }
        if (best != null)
            genome.genes[0] = best;
    }

    private static void get_fitness() {
        for (int i = 0; i < ngenes; i++) {

            double p = 0;
            for (int j = 0; j < towncount - 1; j++) {
                p += distance[genome.genes[i].towns[j] - 1][genome.genes[i].towns[j + 1] - 1];
            }
            p += distance[genome.genes[i].towns[towncount - 1] - 1][genome.genes[i].towns[0] - 1];
            genome.genes[i].fitness = p;
        }
    }

    private static void sort_fitness() {
        Arrays.sort(genome.genes);
    }

    private static void crossover() {
        Genome nextGen = new Genome(ngenes, towncount);
        int i = 0;
        if (protect_best) {
            Gene best = checkBest();
            nextGen.genes[i] = best;
            i++;
        }
        for (; i < (ngenes * pc); i++) {
            int p = get_random_number(0, ngenes);
            int q = get_random_number(0, ngenes);
            nextGen.genes[i] = cross_two_genes(genome.genes[p], genome.genes[q]);
        }
        for (; i < ngenes; i++) {
            int x = get_random_number(0, ngenes);
            nextGen.genes[i] = genome.genes[x].copyof();
        }
        genome = nextGen;
    }

    private static Gene cross_two_genes(Gene gen1, Gene gen2) {
        Gene child = new Gene(towncount);
        child.towns[0] = gen1.towns[0];
        int x, y;
        Random rand = new Random();
        for (int i = 1; i < towncount; i++) {
            x = gen1.getFollowing(child.towns[i - 1]);
            y = gen2.getFollowing(child.towns[i - 1]);
            if (child.isUsed(x) && child.isUsed(y)) {
                child.towns[i] = child.getUnused();
            } else if (child.isUsed(x))
                child.towns[i] = y;
            else if (child.isUsed(y))
                child.towns[i] = x;
            else {
                if (distance[child.towns[i - 1] - 1][x - 1] <= distance[child.towns[i - 1] - 1][y - 1])
                    child.towns[i] = x;
                else
                    child.towns[i] = y;
            }
        }
        return child;
    }

    private static void replicate(int replicant_sceme) {

        int counter = 0;

        get_fitness();
        sort_fitness();
        Genome temp = new Genome(ngenes, towncount);
        if (replicant_sceme == 1) {
            for (int i = 0; i < (ngenes * 0.5); i++) {
                for (int j = 0; j < 2; j++) {
                    if (counter >= ngenes)
                        break;
                    temp.genes[counter] = genome.genes[i].copyof();
                    counter++;
                }
            }
            genome.genes = temp.genes;
        } else if (replicant_sceme == 2) {
            for (int i = 0; i < (ngenes * 0.25); i++) {
                for (int j = 0; j < 4; j++) {
                    if (counter >= ngenes)
                        break;
                    temp.genes[counter] = genome.genes[i].copyof();
                    counter++;
                }
            }
            genome.genes = temp.genes;
        }
    }

    private static int gen() {
        int generation = 2;
        initgenome();
        get_fitness();
        while (generation < maxgenerations) {
            crossover();
            if (checkBest().fitness <= bestpossible)
                break;
            replicate(relication);
            if (checkBest().fitness <= bestpossible)
                break;
            mutation();
            if (checkBest().fitness <= bestpossible)
                break;
            generation++;
        }
        return generation;
    }

    private static void debug(int generation) {
        System.out.println("Generation: " + generation);
        System.out.println("Beste Fitness: " + checkBest().fitness);
        System.out.println("Schlechteste Fitness: " + genome.genes[ngenes - 1].fitness);
        double x = 0;
        for (int i = 0; i < ngenes; i++) {
            x += genome.genes[i].fitness;
        }
        System.out.println("Durchschnittliche Fitness: " + (x / ngenes) + "\n");
    }

    private static Gene checkBest() {
        get_fitness();
        Gene best = genome.genes[0];
        for (int i = 1; i < genome.genes.length; i++) {
            if (genome.genes[i].fitness < best.fitness)
                best = genome.genes[i];
        }
        return best;
    }

}
