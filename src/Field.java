import java.util.Random;

public class Field {

    /**
     * -1代表地雷，0-8代表周围地雷数量，-2作为下标越界的返回值，9-18代表插了棋子的格点
     */
    private int[][] field;
    private boolean[][] isShow;
    private final int row, col;
    private final int mines;
    private int minesFound;
    private int blocksLeft;
    private int flags;
    private boolean hasDugOnce;
    private static final int MAX_SIZE = 30;

    Field() {
        row = 0;
        col = 0;
        mines = 0;
    }

    Field(int vrow, int vcol, int vmines) throws Exception {
        if (vrow > MAX_SIZE || vcol > MAX_SIZE || vrow < 0 || vcol < 0)
            throw new Exception("雷阵大小不能为负或者超出" + MAX_SIZE + "\n");
        if (vmines >= vrow * vcol || vmines < 0)
            throw new Exception("地雷数量必须小于雷阵大小，且地雷数量不能为负\n");

        row = vrow;
        col = vcol;
        mines = vmines;
        flags = 0;
        hasDugOnce = false;
        blocksLeft = row * col - mines;
        field = new int[row][col];
        isShow = new boolean[row][col];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                isShow[i][j] = false;
            }
        }
        placeMines();
        placeNum();
    }

    /**
     * 随机放地雷
     */
    private void placeMines() {
        /*col的数组，其中填满从0-row*col的数字，之后使用Fisher-Yates算法打乱，
        取出mines个数字作为地雷坐标，其坐标为(num/row,num%row)                         */
        int size = row * col;
        int[] arr = new int[size];
        for (int i = 0; i < size; i++)
            arr[i] = i;
        Random random = new Random();
        for (int i = size - 1; i >= size - mines; i--) {
            int index = random.nextInt(i + 1);
            int temp = arr[index];
            arr[index] = arr[i];
            arr[i] = temp;
            field[arr[i] / col][arr[i] % col] = -1;
        }
    }

    /**
     * 根据地雷设置数字
     */
    private void placeNum() {
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (!isMine(i, j)) field[i][j] = countMines(i, j);
            }
        }
    }

    /**
     * 数周围八个格子的地雷数量
     */
    private int countMines(int x, int y) {
        int count = 0;
        if (readField(x - 1, y - 1) == -1) count++;
        if (readField(x - 1, y + 1) == -1) count++;
        if (readField(x - 1, y) == -1) count++;
        if (readField(x, y - 1) == -1) count++;
        if (readField(x, y + 1) == -1) count++;
        if (readField(x + 1, y - 1) == -1) count++;
        if (readField(x + 1, y + 1) == -1) count++;
        if (readField(x + 1, y) == -1) count++;
        return count;
    }


    /**
     * 面向玩家打印有遮掩的雷区
     */
    public void printGame() {
        System.out.print("\t");
        for (int i = 0; i < col; i++)
            System.out.print(i + "\t");
        System.out.print("\n");
        for (int i = 0; i < row; i++) {
            System.out.print(i + "\t");
            for (int j = 0; j < col; j++) {
                if (isShow[i][j]) {
                    if (readField(i, j) != -1) System.out.print(readField(i, j) + "\t");
                    else System.out.print("*\t");
                } else if (isFlag(i, j)) System.out.print("▲   ");
                else System.out.print("■\t");
            }
            System.out.print("\n");
        }
        System.out.println("Flags:" + flags + "   MinesFound:" + minesFound);
        //System.out.println(this);
    }

    /**
     * 打印完全展示的雷区
     */
    public void printField() {
        System.out.print("\t");
        for (int i = 0; i < col; i++)
            System.out.print(i + "\t");
        System.out.print("\n");
        for (int i = 0; i < row; i++) {
            System.out.print(i + "\t");
            for (int j = 0; j < col; j++) {
                if (isMine(i, j) || readField(i, j) == 9) System.out.print("*\t");
                else System.out.print(readField(i, j) > 8 ? (readField(i, j) - 10) : readField(i, j) + "\t");
            }
            System.out.print("\n");
        }
    }

    /**
     * 读取某个位置的数值，并进行边界判断
     */
    public int readField(int x, int y) {
        if (x < 0 || y < 0 || x > row - 1 || y > col - 1)
            return -2;
        else
            return field[x][y];
    }

    public boolean isMine(int x, int y) {
        return readField(x, y) == -1;
    }

    /**
     * 若挖到雷，返回false
     */
    public boolean digMine(int x, int y) throws Exception {
        if (x >= row || y >= col || x < 0 || y < 0)
            throw new Exception("不能越界\n");
        if (isShow[x][y] || isFlag(x, y))
            throw new Exception("不能挖掘已被挖掘或者被标记的格点\n");

        if (!hasDugOnce&& !isFlag(x, y)) {
            while (isMine(x, y)) {
                refresh();
            }
            hasDugOnce = true;
        }
        if (isMine(x, y)) {
            isShow[x][y] = true;
            return false;
        } else {
            expand(x, y);
            return true;
        }
    }

    /**
     * 插旗子标记地雷
     */
    public void flagMine(int x, int y) throws Exception {
        if (x >= row || y >= col || x < 0 || y < 0)
            throw new Exception("不能越界\n");
        if (isShow[x][y])
            throw new Exception("不能标记已经被挖掘的格子\n");
        if (flags >= mines && !isFlag(x, y))
            throw new Exception("不能标记比地雷还多的格点\n");

        else if (isFlag(x, y)) {
            field[x][y] -= 10;
            flags--;
            if (isMine(x, y)) {
                minesFound--;
            }
        } else if (!isFlag(x, y)) {
            if (isMine(x, y)) {
                minesFound++;
            }
            field[x][y] += 10;
            flags++;
        }


    }

    /**
     * 将周围空白的格点展开
     */
    private void expand(int x, int y) {
        if (isMine(x, y) || readField(x, y) == -2 || isShow[x][y] || isFlag(x, y)) return;
        else {
            isShow[x][y] = true;
            blocksLeft--;
            if (readField(x, y) == 0) {
                for (int i = x - 1; i <= x + 1; i++) {
                    for (int j = y - 1; j <= y + 1; j++) {
                        expand(i, j);
                    }
                }
            }
        }
    }

    public boolean isFlag(int x, int y) {
        return (readField(x, y) > 8);
    }

    /**
     * 依据现有的雷阵大小和地雷数量刷新地雷位置
     */
    public void refresh() {
        boolean [][] flagPos  = new boolean [row][col];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if(isFlag(i,j)) flagPos[i][j] = true;
                field[i][j] = 0;
            }
        }
        minesFound = 0;
        flags = 0;
        placeMines();
        placeNum();
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                try {
                    if (flagPos[i][j]) flagMine(i, j);
                } catch (Exception ex) {
                    ;
                }
            }
        }
    }


    public boolean isWin() {
        return (blocksLeft == 0 || minesFound == mines);
    }

}
