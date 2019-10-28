class Genom {
    Gene[] genes;

    Genom(int genecnt, int genelen){
        genes = new Gene[genecnt];
        for(int i = 0;i<genelen;i++){
            genes[i] = new Gene(genelen);
        }
    }
}
