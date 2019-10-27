import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class TravelingSalesman {

    public static void main(String[] args) throws java.io.IOException {

        int[][] map;
        String input = "C:\\Users\\Robert\\IdeaProjects\\TravelingSalesman\\src\\05-map-10x10-36border.txt";

        map = readmap(input);


        for (int i = 0;i < map.length; i++){
            System.out.println(Arrays.toString(map[i]));
        }
    }

    private static int[][] readmap(String input) throws java.io.IOException{
        List<int[]> list = new ArrayList<>();

        Scanner sc = new Scanner(new BufferedReader(new FileReader(input)));
        while(sc.hasNextLine()) {
                int[] temp = Arrays.stream(sc.nextLine().trim().split(" +")).mapToInt(Integer::parseInt).toArray();
                list.add(temp);
        }

        return list.toArray(new int[list.size()][list.size()]);
    }
}
