import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class GenAlg {

    private static double pc = 0.0;
    private static double pm = 0.0;
    private static double initrate = 0.05;
    private static int genecnt = 200;
    private static int genelen = 200;
    private static int replicant_sceme = 1;
    private static int max_generations = 1000;
    private static boolean saveBest = true;
    private static Genom genome;


    public static void main(String[] args) {
        int rounds = 10;

        genome = new Genom(genecnt, genelen);



        for (pc = 0.5; pc <= 0.9; pc += 0.02) {
            for (pm = 0.0; pm <= 0.03; pm += 0.002) {
                double x = 0;
                for (int i = 0; i < rounds; i++) {
                    x += gen();
                }
                System.out.println(pc + "\t" + pm + "\t" + x / rounds + " ");
            }
            System.out.print("\n");
        }
    }

    private static int get_random_number(int von, int bis) {
        Random r = new Random();
        return r.nextInt(bis - von) + von;
    }

    private static void set_gene_pos(Gene gen, int pos, int value) {
        if (value != -1)
            gen.bits[pos] = value;
        else if (gen.bits[pos] == 1)
            gen.bits[pos] = 0;
        else
            gen.bits[pos] = 1;
    }

    private static void initgenome() {
        for (int i = 0; i < genecnt; i++) {
            for (int pos = 0; pos < genelen; pos++) {
                set_gene_pos(genome.genes[i], pos, 0);
            }
        }

        for (int i = 0; i < (genelen * genecnt * initrate); i++) {
            int p = get_random_number(0, genecnt);
            int pos = get_random_number(0, genelen);
            set_gene_pos(genome.genes[p], pos, 1);
        }
    }

    private static void crossover() {
        Gene best = checkBest();
        for (int i = 0; i < (genecnt * pc); i++) {
            int p = get_random_number(0, genecnt);
            int q = get_random_number(0, genecnt);
            int pos = get_random_number(0,genelen);
            if (saveBest && (best == genome.genes[p] ||best == genome.genes[q] ) )
                i--;
            else
                cross_two_genes(genome.genes[p], genome.genes[q], pos);
        }
    }

    private static void cross_two_genes(Gene gen1, Gene gen2, int pos) {
        for (int i = 0; i < genelen; i++) {
            if (i >= pos) {
                int temp = gen1.bits[i];
                gen1.bits[i] = gen2.bits[i];
                gen2.bits[i] = temp;
            }
        }
    }

    private static void mutation() {
        Gene best = checkBest();
        for (int i = 0; i < (genelen * genecnt * pm); i++) {
            int p = get_random_number(0, genecnt);
            int q = get_random_number(0, genelen);
            if (saveBest && (best == genome.genes[p]) )
                i--;
            else
                set_gene_pos(genome.genes[p], q, -1);
        }
    }

    private static void get_fitness() {
        for (int i = 0; i < genecnt; i++) {
            int p = 0;
            for (int j = 0; j < genelen; j++) {
                if (genome.genes[i].bits[j] == 1)
                    p++;
            }
            genome.genes[i].fitness = p;
        }
    }

    private static void sort_fitness() {
        Arrays.sort(genome.genes);
    }

    private static void replicate(int replicant_sceme) {
        if (replicant_sceme == 1) {
            int counter = 0;
            sort_fitness();
            Genom temp = new Genom(genecnt, genelen);
            for (int i = 0; i < (genecnt * 0.1); i++) {
                for (int j = 0; j < 10; j++) {
                    if (counter >= genecnt)
                        break;
                    temp.genes[counter] = genome.genes[i].copyof();
                    counter++;
                }
            }
            genome.genes = temp.genes;
        } else if (replicant_sceme == 2)
            rank_based();
    }

    private static void rank_based() {
        double n = genecnt;
        double s = 2;

        Genom temp = new Genom(genecnt,genelen);

        Arrays.sort(genome.genes, Collections.reverseOrder());

        genome.genes[0].ps = 0;
        genome.genes[0].ps_kum = 0;
        for (int i = 1; i < genecnt; i++) {
            genome.genes[i].ps = (2 - s) / n + (2 * i * (s - 1)) / (n * (n - 1));
            genome.genes[i].ps_kum = genome.genes[i].ps + genome.genes[i - 1].ps_kum;
        }

        Random x = new Random();
        for (int i = 0;i<genecnt;i++) {
            double dart = x.nextDouble();
            for (int j=0;j<genecnt;j++){
                if (genome.genes[j].ps_kum < dart && genome.genes[j+1].ps_kum >= dart)
                    temp.genes[i] = genome.genes[j + 1].copyof();
            }
        }
        genome.genes = temp.genes;
    }

    private static void debug(int generation) {
        System.out.println("Generation: " + generation);
        System.out.println("Beste Fitness: " + genome.genes[0].fitness);
        System.out.println("Schlechteste Fitness: " + genome.genes[genecnt - 1].fitness);
        double x = 0;
        for (int i = 0; i < genelen; i++) {
            x += genome.genes[i].fitness;
        }
        System.out.println("Durchschnittliche Fitness: " + (x / genecnt) + "\n");
    }

    private static int gen() {
        int generation = 2;
        initgenome();
        get_fitness();
        while (generation < max_generations) {
            replicate(replicant_sceme);
            crossover();
            if (checkBest().fitness == genelen)
                break;
            mutation();
            if (checkBest().fitness == genelen)
                break;
            generation++;
        }
        return generation;
    }

    private static Gene checkBest (){
        get_fitness();
        Gene best = genome.genes[0];
        for (int i = 1; i < genome.genes.length; i++){
            if (genome.genes[i].fitness > best.fitness)
                best = genome.genes[i];
        }
        return best;
    }
}
