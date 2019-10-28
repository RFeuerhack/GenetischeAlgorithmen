public class Genome {
    int length;
    Gene[] genes;

    public Genome (int genepop, int towns){
        length = genepop;
        genes = new Gene[genepop];
        for (int i = 0; i < length; i++)
            genes[i] = new Gene(towns);
    }
}