package level;

import java.util.*;

public class RandomLevel {
    private final int GRID_SIZE;
    Random random;

    // 0: UP
    // 1: DOWN
    // 2: LEFT
    // 3: RIGHT

    public RandomLevel(int gridSize) {
        GRID_SIZE = gridSize;
        random = new Random();
    }

    public void generateLevel() {
        // Creates the array holding all possible valid moves from any cell
        int[][] validMoves = new int[GRID_SIZE * GRID_SIZE][4];

        // Initialized the array with -1 indicating no restrictions have been applied yet
        for (int[] i: validMoves) {
            Arrays.fill(i, -1);
        }

        // Applies restrictions on boundary cells so that moves taking player out of the boarders are made invalid
        for (int i = 0; i < GRID_SIZE * GRID_SIZE; i++) {
            for (int j = 0; j < 4; j++) {
                if (i < GRID_SIZE)
                    validMoves[i][0] = 0;
                if (i % GRID_SIZE == 0)
                    validMoves[i][2] = 0;
                if (i > GRID_SIZE * GRID_SIZE - GRID_SIZE)
                    validMoves[i][1] = 0;
                if (i % GRID_SIZE == 4)
                    validMoves[i][3] = 0;
            }
        }

        // Fills the rest of the positions with random valid values for each cell
        int nextPos;
        for (int i = 0; i < GRID_SIZE * GRID_SIZE; i++) { // i represents the currPos
            for (int j = 0; j < 2; j++) { //j = 0 ~ 30% | j = 1 ~ 50 % | j = 2 ~ 60% | j = 4 ~ 70%
                if (validMoves[i][j] == -1) {
                    char[] validChars = getValidChars(i);
                    char c = getRandomValidChar(validChars, ' ');
                    switch (c - '0') {
                        case 0:
                            nextPos = nextPosition(i, 0);
                            validMoves[i][0] = 1;
                            validMoves[nextPos][1] = 1;
                            break;

                        case 1:
                            nextPos = nextPosition(i, 1);
                            validMoves[i][1] = 1;
                            validMoves[nextPos][0] = 1;
                            break;

                        case 2:
                            nextPos = nextPosition(i, 2);
                            validMoves[i][2] = 1;
                            validMoves[nextPos][3] = 1;
                            break;

                        case 3:
                            nextPos = nextPosition(i, 3);
                            validMoves[i][3] = 1;
                            validMoves[nextPos][2] = 1;
                            break;

                        default:
                            System.out.println();
                    }
                }
            }
        }

        // Invalidates the remaining undecided moves
        for (int i = 0; i < GRID_SIZE * GRID_SIZE; i++) {
            for (int j = 0; j < 4; j++) {
                if (validMoves[i][j] == -1)
                    validMoves[i][j] = 0;
            }
        }

        // Creates the string which is the valid path
        String path = "";
        while (path.length() < 15)
            path = generateRandomString(15, 50); // Chosen arbitrarily
        System.out.println(path); // Remove later

        // Validates moves so that the string can be treated as the correct path
        int currPos = 0;
        nextPos = 0;
        for (char c: path.toCharArray()) {
            switch (c - '0') {
                case 0:
                    nextPos = nextPosition(currPos, 0);
                    validMoves[currPos][0] = 1;
                    validMoves[nextPos][1] = 1;
                    break;

                case 1:
                    nextPos = nextPosition(currPos, 1);
                    validMoves[currPos][1] = 1;
                    validMoves[nextPos][0] = 1;
                    break;

                case 2:
                    nextPos = nextPosition(currPos, 2);
                    validMoves[currPos][2] = 1;
                    validMoves[nextPos][3] = 1;
                    break;

                case 3:
                    nextPos = nextPosition(currPos, 3);
                    validMoves[currPos][3] = 1;
                    validMoves[nextPos][2] = 1;
                    break;

                default:
                    System.out.println();
            }
            currPos = nextPos;
        }

        // Remove later
        int count = 0;
        System.out.println("\t" + "U D L R");
        for (int i = 0; i < GRID_SIZE * GRID_SIZE; i++) {
            System.out.print(i + "\t");
            for (int j = 0; j < 4; j++) {
                System.out.print(validMoves[i][j] + " ");
                if (validMoves[i][j] == 1)
                    count++;
            }
            System.out.println();
        }
        // Counts % of 1
        float per1 = count * 100.0f / (GRID_SIZE * GRID_SIZE * 4.0f);
        System.out.println("Percentage of 1: " + per1 + "%");
    }

    private String generateRandomString(int minLength, int maxLength) {
        // Generate a random length for the String
        int length = random.nextInt((maxLength - minLength) + 1) + minLength;

        // sb will contain the random string
        StringBuilder sb = new StringBuilder();
        char lastChar = '\0';

        // Deals with the positioning of the correct path in the maze
        int currPos = 0;
        boolean[] visited = new boolean[GRID_SIZE * GRID_SIZE];
        Arrays.fill(visited, false);
        visited[0] = true;

        // Generate the string
        while (sb.length() < length) {
            char[] validChars = getValidChars(currPos);
            boolean moveMade = false;

            for (int attempts = 0; attempts < validChars.length * 2; attempts++) { // Allow multiple attempts
                lastChar = getRandomValidChar(validChars, lastChar);
                int nextPos = nextPosition(currPos, lastChar - '0');

                if (nextPos >= 0 && nextPos < GRID_SIZE * GRID_SIZE && !visited[nextPos]) {
                    sb.append(lastChar);
                    visited[nextPos] = true;
                    currPos = nextPos;
                    moveMade = true;
                    break;
                }
            }

            if (!moveMade) {
                break; // No valid move found, break out of the loop to avoid infinite loop
            }
        }

        return sb.toString();
    }

    private char[] getValidChars(int currPos) {
        if (currPos == 0) { // Top-Left grid
            return new char[]{'1', '3'};
        } else if (currPos == GRID_SIZE - 1) { // Top-Right grid
            return new char[]{'1', '2'};
        } else if (currPos == GRID_SIZE * GRID_SIZE - GRID_SIZE) { // Bottom-Left grid
            return new char[]{'0', '3'};
        } else if (currPos == GRID_SIZE * GRID_SIZE - 1) { // Bottom-Right grid
            return new char[]{'0', '2'};
        } else if (currPos < GRID_SIZE) { // Top margin grids
            return new char[]{'1', '2', '3'};
        } else if (currPos >= GRID_SIZE * GRID_SIZE - GRID_SIZE) { // Bottom margin grids
            return new char[]{'0', '2', '3'};
        } else if (currPos % GRID_SIZE == 0) { // Left margin grids
            return new char[]{'0', '1', '3'};
        } else if (currPos % GRID_SIZE == GRID_SIZE - 1) { // Right margin grids
            return new char[]{'0', '1', '2'};
        } else {
            return new char[]{'0', '1', '2', '3'};
        }
    }

    private char getRandomValidChar(char[] validChars, char lastChar) {
        char newChar;

        do {
            newChar = validChars[random.nextInt(validChars.length)];
        } while (!isValidFollow(lastChar, newChar));

        return newChar;
    }

    private boolean isValidFollow(char lastChar, char newChar) {
        return !((lastChar == '0' && newChar == '1') ||
                (lastChar == '1' && newChar == '0') ||
                (lastChar == '2' && newChar == '3') ||
                (lastChar == '3' && newChar == '2'));
    }

    private int nextPosition(int currPos, int move) {
        int row = currPos / GRID_SIZE;
        int col = currPos % GRID_SIZE;

        return switch (move) {
            case 0 -> (row > 0) ? currPos - GRID_SIZE : -1; // Up
            case 1 -> (row < GRID_SIZE - 1) ? currPos + GRID_SIZE : -1; // Down
            case 2 -> (col > 0) ? currPos - 1 : -1; // Left
            case 3 -> (col < GRID_SIZE - 1) ? currPos + 1 : -1; // Right
            default -> -1;
        };
    }
}
