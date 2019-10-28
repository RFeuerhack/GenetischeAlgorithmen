public class Gene implements Comparable<Gene> {
    int[] bits;
    private int genelen;
    int fitness;
    public double ps,ps_kum;
    Gene(int genelen){
        this.genelen = genelen;
        bits = new int[genelen];
    }

    public int compareTo(Gene g){
        if(this.fitness>g.fitness)
            return -1;
        else{
            if(this.fitness<g.fitness)
            return 1;
        }
        return 0;
    }

    Gene copyof() {
        Gene gene = new Gene(genelen);
        gene.fitness = this.fitness;
        gene.ps = this.ps;
        gene.ps_kum = this.ps_kum;
        System.arraycopy(this.bits, 0, gene.bits, 0, genelen);

        return gene;
    }
}
