import java.util.Scanner;

public class Game {
    /**
     * 判断整个游戏是否运行
     */
    private boolean isRunning;
    /**
     * 判断建立雷区的三个参数是否成功读入并且创建成功
     */
    private boolean isInit;
    /**
     * 判断挖雷后是否存活
     */
    private boolean isSurvive;

    private Field minefield;
    private Scanner scanner;

    Game(Field minefield, Scanner scanner) {
        isRunning = true;
        isInit = false;
        isSurvive = true;
        this.minefield = minefield;
        this.scanner = scanner;
    }


    private int[] commandInit(String command) {
        int[] arg = new int[3];
        String[] num = command.split("\\s");
        for (int i = 0; i < 3; i++)
            arg[i] = Integer.parseInt(num[i]);
        return arg;
    }


    private int[] commandDig(String command) {
        int[] arg = new int[2];
        String[] num = command.split("\\s");
        for (int i = 0; i < 2; i++)
            arg[i] = Integer.parseInt(num[i]);
        return arg;
    }


    private int[] commandFlag(String command) {
        command = command.substring(1, command.length());
        int[] arg = new int[2];
        String[] num = command.split("\\s");
        for (int i = 0; i < 2; i++)
            arg[i] = Integer.parseInt(num[i]);
        return arg;
    }


    /**
     * 不打印纯英文的错误信息
     */
    private void myPrintError(String str) {
        if (str.matches("^[a-zA-Z0-9\\s\":]*"))
            System.out.println("未知命令");
        else
            System.out.println(str);
    }


    public void play() {
        while (isRunning) {
            System.out.println("欢迎来到钱玉航设计的扫雷游戏，请输入三个整数");

            //进行雷盘的初始化
            while (!isInit) {
                try {
                    int[] argI = commandInit(scanner.nextLine());
                    minefield = new Field(argI[0], argI[1], argI[2]);
                    minefield.printGame();
                    isInit = true;
                } catch (Exception ex) {
                    myPrintError(ex.getMessage());
                }
            }

            //开始游戏
            String command;
            while (isSurvive) {
                try {
                    if (minefield.isWin()) break;
                    command = scanner.nextLine();

                    //挖雷与标记雷
                    if (Character.isDigit(command.charAt(0))) {
                        int[] argD = commandDig(command);
                        isSurvive = minefield.digMine(argD[0], argD[1]);
                    } else if (command.startsWith("f")) {
                        int[] argF = commandFlag(command);
                        minefield.flagMine(argF[0], argF[1]);
                    } else {
                        throw new Exception("未知命令");
                    }

                    minefield.printGame();

                } catch (Exception ex) {
                    myPrintError(ex.getMessage());
                }


            }

            //游戏结束判断
            if (!isSurvive) {
                System.out.println("踩中地雷，游戏结束");
            } else {
                System.out.println("恭喜你，扫雷完成");
            }
            minefield.printField();
            System.out.println("重新开始请输入'y'");
            if (!scanner.nextLine().equals("y")) isRunning = false;
            else {
                isRunning = true;
                isInit = false;
                isSurvive = true;
            }

        }
    }
}


