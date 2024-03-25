import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Field minefield = new Field();
        Scanner scanner = new Scanner(System.in);
        Game newgame = new Game(minefield, scanner);

        newgame.play();
    }
}
