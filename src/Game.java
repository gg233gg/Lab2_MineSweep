import java.util.Scanner;

public class Game {
    public static void main(String[] args) {
        Game newgame = new Game();
        Mine mine = new Mine();
        Scanner scanner = new Scanner(System.in);

        while (newgame.isRunning) {


            while (!newgame.isInit) {
                try {
                    if(!newgame.isCreate) {
                        int[] argI = newgame.commandInit(scanner.nextLine());
                        mine = new Mine(argI[0], argI[1], argI[2]);
                        mine.printGame();
                        newgame.isCreate = true;
                    }
                    int[] argI = mine.getBasic();
                    int[] argD = newgame.commandDig(scanner.nextLine());
                    while(mine.readField(argD[0],argD[1])==-1)
                        mine = new Mine(argI[0], argI[1], argI[2]);
                    newgame.isSurvive = mine.digMine(argD[0], argD[1]);
                    mine.printGame();

                    newgame.isInit = true;
                    //mine.printField();
                } catch (Exception ex) {
                    System.out.println("invalid input");
                }
            }

            String command;
            while(newgame.isSurvive){
                try {
                    if(mine.getBlocksLeft() == 0 || mine.getMinesFound() == mine.getMines()) break;
                    command = scanner.nextLine();
                    if (Character.isDigit(command.charAt(0))) {
                        int[] argD = newgame.commandDig(command);
                        newgame.isSurvive = mine.digMine(argD[0], argD[1]);
                    }
                    else if(command.startsWith("f")) {
                        int[] argF = newgame.commandFlag(command);
                        mine.flagMine(argF[0],argF[1]);
                    }
                    mine.printGame();
                } catch (Exception ex) {
                    System.out.println("invalid input");
                }
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
     * 判断建立雷区的三个参数是否成功读入并且创建成功
     */
    private boolean isCreate;
    /**
     * 判断雷区是否初始化，做到先手不踩雷
     */
    private boolean isInit;
    /**
     * 判断挖雷后是否存活
     */
    private boolean isSurvive;

    Game() {
        isRunning = true;
        isCreate = false;
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

    public int[] commandFlag(String command) {
        command = command.substring(1,command.length());
        int[] arg = new int[2];
        String[] num = command.split("\\s");
        for (int i = 0; i < 2; i++)
            arg[i] = Integer.parseInt(num[i]);
        return arg;
    }
}


