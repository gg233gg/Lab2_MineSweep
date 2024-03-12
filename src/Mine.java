import java.util.Random;
import java.util.Scanner;
public class Mine {

    //-1代表地雷，0-8代表周围地雷数量，-2作为下标越界的返回值
    private int[][] field;
    private boolean[][] show;
    private final int row, col;
    private final int mines;
    private int mines_found;
    private static final int MAX_SIZE = 30;

    Mine() {
        row = 0;
        col = 0;
        mines = 0;
    }

    Mine(int vrow, int vcol, int vmines) throws Exception {
        if (vmines > vrow * vcol || vrow > MAX_SIZE || vcol > MAX_SIZE) throw new Exception();
        row = vrow;
        col = vcol;
        mines = vmines;
        field = new int[row][col];
        show = new boolean[row][col];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                show[i][j] = false;
            }
        }
        placeMines();
        placeNum();
    }

    //随机放地雷
    private void placeMines() {
        /*建立一个大小为row*col的数组，其中填满从0-row*col的数字，之后使用Fisher-Yates算法打乱，
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
            setField(arr[i] / col, arr[i] % col, -1);
        }
    }

    //根据地雷设置数字
    private void placeNum() {
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (readField(i, j) != -1) setField(i, j, countMines(i, j));
            }
        }
    }

    //数周围八个格子的地雷数量
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


    public void printField() {
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (isShow(i, j)) System.out.print(readField(i, j) + "  ");
                else System.out.print("■  ");
            }
            System.out.print("\n");
        }
    }

    public void setField(int x, int y, int status) {
        field[x][y] = status;
    }

    //读取某个位置的数值，并进行边界判断
    public int readField(int x, int y) {
        if (x < 0 || y < 0 || x > row - 1 || y > col - 1)
            return -2;
        else
            return field[x][y];
    }

    public boolean isMine(int x, int y) {
        if (readField(x, y) == -1) return true;
        else return false;
    }

    public boolean isShow(int x, int y) {
        return show[x][y];
    }

    public void flip(int x, int y) {
        show[x][y] = !show[x][y];
    }

    public boolean digMine(int x, int y) {
        if (isMine(x, y)) return true;
        else {
            expand(x, y);
            return false;
        }
    }

    //揭开当前的格点空白展开，在此之前不要先将隐藏的格点揭开
    public void expand(int x, int y) {
        if (isMine(x, y) || readField(x, y) == -2 || isShow(x,y)) return;
        else{
            flip(x,y);
            int count = countMines(x, y);
            if (count == 0) {
                for (int i = x - 1; i <= x + 1; i++) {
                    for (int j = y - 1; j <= y + 1; j++) {
                        expand(i, j);
                    }
                }
            }
        }
    }

}
