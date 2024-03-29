import java.util.Random;

public class Field {

    /**
     * -1代表地雷，0-8代表周围地雷数量，-2作为下标越界的返回值
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
        if (vmines >= vrow * vcol || vrow > MAX_SIZE || vcol > MAX_SIZE || vmines < 0) throw new Exception();
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
                if (isShow[i][j]) System.out.print(readField(i, j) + "\t");
                else if (isFlag(i, j)) System.out.print("▲   ");
                else System.out.print("■\t");
            }
            System.out.print("\n");
        }
        System.out.println("Flags:" + flags + "    " + "Minesfound" + minesFound);
        System.out.println(this);
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
                if (isMine(i, j)) System.out.print("*\t");
                else System.out.print(readField(i, j) + "\t");
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
        if (x >= row || y >= col || x < 0 || y < 0 || isShow[x][y] || isFlag(x, y)) throw new Exception();
        if(!hasDugOnce && !isFlag(x, y)) {
            while(isMine(x, y)) {
                refresh();
            }
            hasDugOnce = true;
        }
        if (isMine(x, y)) {
            isShow[x][y] = true;
            return false;
        }
        else {
            expand(x, y);
            return true;
        }
    }

    public void flagMine(int x, int y) throws Exception {
        if (x >= row || y >= col || x < 0 || y < 0 || isShow[x][y] || (flags >= mines && !isFlag(x, y)))
            throw new Exception();
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
     * 揭开当前的格点空白展开，在此之前不要先将隐藏的格点揭开
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

    public void refresh() {
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                field[i][j] = 0;
            }
        }
        placeMines();
        placeNum();
    }


    public boolean isWin() {
        return (blocksLeft == 0 || minesFound == mines);
    }

}
