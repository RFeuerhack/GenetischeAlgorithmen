import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Gene implements Comparable<Gene> {
    int[] towns;
    double fitness;


    public Gene(int towns) {
        this.towns = new int[towns];
        for (int i = 0; i < this.towns.length; i++)
            this.towns[i] = 0;
    }

    public int compareTo(Gene g) {
        if (this.fitness > g.fitness)
            return 1;
        else {
            if (this.fitness < g.fitness)
                return -1;
        }
        return 0;
    }

    Gene copyof() {
        Gene gene = new Gene(this.towns.length);
        System.arraycopy(this.towns, 0, gene.towns, 0, this.towns.length);
        return gene;
    }

    void shuffle() {
        Random rnd = ThreadLocalRandom.current();
        for (int i = towns.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            int a = towns[index];
            towns[index] = towns[i];
            towns[i] = a;
        }
    }

    int getFollowing(int x){
        for (int i = 0; i < towns.length; i++){
            if (towns[towns.length -1] == x)
                return towns[0];
            else if (towns[i] == x)
                return towns[i+1];
        }
        return 0;
    }

    boolean isUsed (int value){
        for (int town : towns) {
            if (town == value)
                return true;
        }
        return false;
    }


    int getUnused(){
        Random random = new Random();
        List<Integer> values = new ArrayList<>();
        for (int i = 1; i < towns.length+1; i++){
            if (!isUsed(i))
                values.add(i);
        }
        return values.get(random.nextInt(values.size()));
    }
}
