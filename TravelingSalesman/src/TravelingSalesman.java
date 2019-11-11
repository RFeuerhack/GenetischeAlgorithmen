import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Array;
import java.util.*;

public class TravelingSalesman {

    static int[][] map;
    static int towncount = 0;
    static int ngenes = 1;
    static int maxgenerations = 1000;
    static double pc = 0.5;
    static double pm = 0.05;
    static boolean protect_best = false;
    static int relication = 1;
    static int crossover_method = 1;
    static double[][] distance;
    static Genome genome;
    static double bestpossible = 36;

    public static void main(String[] args) throws java.io.IOException{
        int rounds = 50;
        String input = "C:\\Users\\Robert\\Desktop\\GenetischeAlgorithmen\\TravelingSalesman\\src\\05-map-10x10-36-dist42.64.txt";

        setup(input);
        genome = new Genome(ngenes,towncount);
        distance = new double[towncount][towncount];
        getDistance();

        //for(int i = 0; i < distance.length; i++)
        //    System.out.println(Arrays.toString(distance[i]));
//
        //initgenome();
        //for(int i = 0; i < ngenes; i++)
        //    System.out.println(i +" " +Arrays.toString(genome.genes[i].towns));
//
        //System.out.println("\n");
//
        //gen();
//

        genome.genes[0].towns = new int[]{32, 20, 14, 28, 19, 26, 4, 24, 34, 21, 22, 31, 2, 17, 7, 15, 30, 13, 35, 11, 33, 8, 16, 6, 5, 25, 3, 18, 10, 12, 23, 9, 1, 36, 27, 29};

        System.out.println(Arrays.deepToString(distance));

        get_fitness();
        for(int i = 0; i < ngenes; i++)
            System.out.println(i +" " +Arrays.toString(genome.genes[i].towns) + " " + genome.genes[i].fitness);
        //for (pc = 0.0; pc <= 1; pc += 0.05) {
        //    for (pm = 0.0; pm <= 0.2; pm += 0.005) {
        //        double x = 0;
        //        for (int i = 0; i < rounds; i++) {
        //            x += gen();
        //        }
        //        System.out.println(pc + "\t" + pm + "\t" + x / rounds + " ");
        //    }
        //    System.out.print("\n");
        //}
    }


    private static void initgenome() {
        for (int i = 0; i < ngenes; i++) {
            for (int pos = 0; pos < towncount; pos++) {
                genome.genes[i].towns[pos] = pos+1;
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

    public static void getDistance(){
        for (int i = 0; i < distance.length; i++){
            for (int j = 0; j < distance.length; j++){
                if (i == j)
                    distance[i][j] = 0;
                else
                    distance[i][j] = distanceBetween(i,j);
            }
        }
    }

    static double distanceBetween(int x , int y){
        double x1 = 0;
        double x2 = 0;
        double y1 = 0;
        double y2 = 0;
        for (int i = 0; i < map.length; i++){
            for (int j = 0; j < map.length; j++){
                if (map[i][j] == (x+1) ){
                    x1 = i;
                    y1 = j;
                }else if (map[i][j] == (y+1) ){
                    x2 = i;
                    y2 = j;
                }
            }
        }
        return Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1));
    }

    private static int get_random_number(int von, int bis) {
        Random r = new Random();
        return r.nextInt(bis) + von;
    }

    private static void mutation() {
        Gene best = checkBest();
        for (int i = 0; i < (ngenes * towncount * pm); i++) {
            int p = get_random_number(0, ngenes);
            int q = get_random_number(0, towncount);
            int o = get_random_number(0, towncount);
            //System.out.println(p+" "+  q+ " " + o);
            if (protect_best && best == genome.genes[p] )
                i--;
            else {
                int temp = genome.genes[p].towns[q];
                genome.genes[p].towns[q] = genome.genes[p].towns[o];
                genome.genes[p].towns[o] = temp;
            }
        }
    }

    private static void get_fitness() {
        double p = 0;
        for (int i = 0; i < ngenes; i++) {
            for (int j = 0; j < towncount-1; j++) {
                p += distance[genome.genes[i].towns[j]-1][genome.genes[i].towns[j+1]-1];
            }
            p+= distance[genome.genes[i].towns[towncount-1]-1][genome.genes[i].towns[0]];
            genome.genes[i].fitness = p;
        }
    }

    private static void sort_fitness() {
        Arrays.sort(genome.genes);
    }

    private static void crossover() {
        Gene best = checkBest();
        Genome nextGen = new Genome(ngenes,towncount);
        int i = 0;
        for (; i < (ngenes * pc); i++) {
            int p = get_random_number(0, ngenes);
            int q = get_random_number(0, ngenes);
            //System.out.println(p + " " + q);
            if ( protect_best && ( best == genome.genes[p] || best == genome.genes[q] ) )
                i--;
            else
                nextGen.genes[i] = cross_two_genes(genome.genes[p], genome.genes[q]);
        }
        for (; i < ngenes; i++){
            if (protect_best){
                nextGen.genes[i] = best;
                continue;
            }
            int x = get_random_number(0,ngenes);
            nextGen.genes[i] = genome.genes[x].copyof();
        }
        genome = nextGen;
    }

    private static Gene cross_two_genes(Gene gen1, Gene gen2) {
        Gene child = new Gene(towncount);
        child.towns[0] = gen1.towns[0];
        int x,y;
        Random rand = new Random();
        for(int i = 1; i < towncount; i++ ){
            x = gen1.getFollowing(child.towns[i-1]);
            y = gen2.getFollowing(child.towns[i-1]);
            if (child.isUsed(x) && child.isUsed(y)){
                child.towns[i] = child.getUnused();
            }else if (child.isUsed(x))
                child.towns[i] = y;
            else if (child.isUsed(y))
                child.towns[i] = x;
            else{
                if( distance[child.towns[i-1]-1][x-1] <= distance[child.towns[i-1]-1][y-1] )
                    child.towns[i] = x;
                else
                    child.towns[i] = y;
            }
        }
        return child;
    }

    private static void replicate(int replicant_sceme) {
        if (replicant_sceme == 1) {
            int counter = 0;
            get_fitness();
            sort_fitness();
            Genome temp = new Genome(ngenes, towncount);
            for (int i = 0; i < (ngenes * 0.5); i++) {
                for (int j = 0; j < 2; j++) {
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
            replicate(relication);
            crossover();
            get_fitness();
            //for (int i = 0; i < ngenes; i++)
            //    System.out.println(Arrays.toString(genome.genes[i].towns) + " " + genome.genes[i].fitness);
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

    private static Gene checkBest (){
        get_fitness();
        Gene best = genome.genes[0];
        for (int i = 1; i < genome.genes.length; i++){
            if (genome.genes[i].fitness < best.fitness)
                best = genome.genes[i];
        }
        return best;
    }
}
