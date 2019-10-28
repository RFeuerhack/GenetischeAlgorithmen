import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class TravelingSalesman {

    static int[][] map;
    static int towncount = 0;
    static int ngenes = 200;
    static int maxgenerations = 3000;
    static double pc = 0.05;
    static double pm = 0.02;
    static int protect_best = 0;
    static int relication = 0;
    static int crossover_method = 1;
    static double[][] distance;
    static Genome genome;

    public static void main(String[] args) throws java.io.IOException{
        String input = "C:\\Users\\RFeue\\Desktop\\GenetischeAlgorithmen\\TravelingSalesman\\src\\05-map-10x10-36border.txt";

        setup(input);
        genome = new Genome(ngenes,towncount);
        distance = new double[towncount][towncount];
        getDistance();

        for (int i = 0; i < map.length; i++) {
            System.out.println(Arrays.toString(map[i]));
        }
        System.out.println(towncount);
        for (int i = 0; i < distance.length; i++) {
            System.out.println(Arrays.toString(distance[i]));
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
}
