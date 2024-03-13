import java.util.Scanner;

public class Game {
    public static void main(String[] args) {
        Game newgame = new Game();
        Mine mine = new Mine();
        Scanner scanner = new Scanner(System.in);

        while (newgame.isRunning) {


            while (!newgame.isInit) {
                try {
                    int[] arg = newgame.commandInit(scanner.nextLine());
                    mine = new Mine(arg[0], arg[1], arg[2]);
                    newgame.isInit = true; //游戏已经初始化，可以向下继续进行
                    mine.printGame();
                } catch (Exception ex) {
                    System.out.println("invalid input");
                }
            }

            while(newgame.isSurvive){
                try {
                    int[] arg = newgame.commandDig(scanner.nextLine());
                    newgame.isSurvive = mine.digMine(arg[0], arg[1]);
                    mine.printGame();
                } catch (Exception ex) {
                    System.out.println("invalid input");
                }
                if(mine.blocksLeft() == 0) break;
            }

            if(!newgame.isSurvive) {
                System.out.println("Fail");
            }
            else {
                System.out.println("Win");
            }
            mine.printField();
            System.out.println("重新开始请输入'y'");
            if (scanner.nextLine().equals("y")) newgame = new Game();
            else newgame.isRunning = false;

        }
    }

    /**
     * 判断整个游戏是否运行
     */
    private boolean isRunning;
    /**
     * 判断雷区是否初始化
     */
    private boolean isInit;
    /**
     * 判断挖雷后是否存活
     */
    private boolean isSurvive;

    Game() {
        isRunning = true;
        isInit = false;
        isSurvive = true;
        System.out.println("欢迎来到扫雷游戏，请输入三个整数");
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

}


