import java.util.Scanner;

public class Game {
    public static void main(String[] args) {
        Game newgame = new Game();
        Mine mine = new Mine();
        Scanner scanner = new Scanner(System.in);
        while (newgame.isRunning()) {


            while (!newgame.isInit()) {
                try {
                    int arg[] = newgame.commandInit(scanner.nextLine());
                    mine = new Mine(arg[0], arg[1], arg[2]);
                    newgame.init = true;
                    mine.printField();
                } catch (Exception ex) {
                    System.out.println("invalid input");
                }
            }

            try {
                int arg[] = newgame.commandDig(scanner.nextLine());
                mine.digMine(arg[0], arg[1]);
                mine.printField();
            } catch (Exception ex) {
                System.out.println("invalid input");
            }
        }
    }

    private boolean running;
    private boolean init;

    Game() {
        running = true;
        init = false;
    }

    public int[] commandInit(String command) {
        int[] arg = new int[3];
        String[] num = command.split("\\s");
        for (int i = 0; i < 3; i++)
            arg[i] = Integer.parseInt(num[i]);
        return arg;
    }

    public int[] commandDig(String command) {
        int[] arg = new int[2];
        String[] num = command.split("\\s");
        for (int i = 0; i < 2; i++)
            arg[i] = Integer.parseInt(num[i]);
        return arg;
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isInit() {
        return init;
    }

}
