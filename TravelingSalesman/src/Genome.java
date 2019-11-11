public class Genome {
    Gene[] genes;

    public Genome (int genepop, int towns){
        genes = new Gene[genepop];
        for (int i = 0; i < genes.length; i++)
            genes[i] = new Gene(towns);
    }
}