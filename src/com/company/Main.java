package com.company;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Main {

    static int H = 80;  // use a max of 100 holes
    static int WIDTH_BOARD = 500;
    private static String FILENAME = "fb";
    private static String FILENAME_DAT;

    //Options
    static boolean ROUND_ENABLE = false;
    static int ROUND_SCALE = 3;
    static int SIGN_DIGITS = 2;
    static int SEED_RAND = 72;

    static int LINE_LENGHT = WIDTH_BOARD / 20;


    public static double[][] mC;
    public static Hole[] mHoles;

    public static void main(String[] args) throws IOException {

        if (args.length < 1) {
            throw new IOException();
        } else {
            H = Integer.parseInt(args[0]);
            FILENAME += H;
            FILENAME_DAT = FILENAME + ".dat";
        }

        mHoles = generateMicroAreasHoles();
        //printTableOfHoles(mHoles);
        generateFileOfPoints(mHoles);

        mC = generateMatrixCosts(mHoles);
        //printMatrix(mC);

        generateDat(mC);
    }

    private static Hole[] generateMicroAreasHoles() {
        ArrayList<Hole> holes = new ArrayList<>();

        int maxAreas = 3;
        int maxLines = 4;
        int maxPointsPerArea = H / 5;
        int minPointsPerArea = H / 20;

        int pointsCount = H;
        int totAreasPoints = 0;
        int totLinesPoints = 0;
        ArrayList<Area> areas = Area.buildAreas(maxAreas);
        ArrayList<Line> lines = Line.buildLines(maxLines, LINE_LENGHT);

        for (Area area : areas) {
            int pointsPerArea = ThreadLocalRandom.current()
                        .nextInt(minPointsPerArea, maxPointsPerArea);
            while (pointsPerArea % 4 != 0) { // pointsPerArea must be divided by four
                pointsPerArea++;
            }

            if (pointsCount >= pointsPerArea) {

                System.out.println("Area, points: " + pointsPerArea);

                pointsCount -= pointsPerArea;
                totAreasPoints += pointsPerArea;

                // Instert points into micro area   
             
                generateRectHoles(holes, pointsPerArea, area.originX, area.maxX, area.originY, area.maxY);
            }
        }

        for (Line line : lines) {
            int pointsPerLine = ThreadLocalRandom.current().nextInt(4, 8);

            if (pointsPerLine <= pointsCount) {
                System.out.println("Line, points: " + pointsPerLine);

                pointsCount -= pointsPerLine;
                totLinesPoints += pointsPerLine;

                double distanceBetween = LINE_LENGHT / (pointsPerLine - 1);

                // chose if grow vertical or horizontal
                double increaseX = 0;
                double increaseY = 0;
                int which = ThreadLocalRandom.current().nextInt();

                if (which % 2 == 0) {
                    increaseX = distanceBetween;
                } else {
                    increaseY = distanceBetween;
                }

                double x = line.originX;
                double y = line.originY;
                holes.add(new Hole(x, y));

                for (int i = 1; i < pointsPerLine; i++) {
                    x += increaseX;
                    y += increaseY;
                    holes.add(new Hole(x, y));
                }
            }
        }

        System.out.println("\nTotal expected points in Areas: " + totAreasPoints);
        System.out.println("\nTotal expected points in Lines: " + totLinesPoints);
        System.out.println("Total points in holes: " + holes.size());

        System.out.println("\nHoles random: " + pointsCount);

        while (pointsCount > 0) {
            holes.add(generateRndHolesNotOverlap(areas, 0, WIDTH_BOARD, 0, WIDTH_BOARD));
            pointsCount--;
        }

        System.out.println("Final number of holes " + holes.size());

        return holes.toArray(new Hole[holes.size()]);
    }

    private static void generateRectHoles(ArrayList<Hole> holes, int maxHoles, int minX, 
                                        int maxX, int minY, int maxY) {

        double outerPerimeter = ((maxX - minX) + (maxY - minY)) * 2;
        double distBetween = outerPerimeter / (maxHoles - 1);

        // starting point is the origin
        double xLine = minX;
        double yLine = minY;

        //for (int square = 0; square < 2; square++) {

            for (int edge = 0; edge < 4; edge++) {

                for (int i = 0; i < Math.floor(maxHoles / 4); i++) {
                    if (edge == 0) xLine += distBetween;
                    if (edge == 1) yLine += distBetween;
                    if (edge == 2) xLine -= distBetween;
                    if (edge == 3) yLine -= distBetween;

                    //System.out.println("New hole");
                    holes.add(new Hole(xLine, yLine));
                }
            }
            System.out.println("Holes into area: " + holes.size());
    }

    private static Hole generateRndHolesNotOverlap(ArrayList<Area> areas,
                                            int originX, int maxX, int originY, int maxY) {
        // init position
        double rndX = ThreadLocalRandom.current().nextDouble(originX + 1, maxX);
        double rndY = ThreadLocalRandom.current().nextDouble(originY + 1, maxY);

        for (Area area : areas) {
            while (rndX >= area.originX && rndX <= area.maxX && 
                rndY >= area.originY && rndY <= area.maxY) { 
                // overlap -> change position
                rndX = ThreadLocalRandom.current().nextDouble(originX + 1, maxX);
                rndY = ThreadLocalRandom.current().nextDouble(originY + 1, maxY);
            }
        }
        
        return new Hole(rndX, rndY);
    }

    // Origin point is on TOP LEFT
    private static Hole[] generateGoldenRatioHoles() {
        ArrayList<Hole> holes = new ArrayList<>();
        Random generator = new Random(SEED_RAND);

        int padding = WIDTH_BOARD / 20;

        // External Area Top
        int minTopX = 0;
        int maxTopX = WIDTH_BOARD;
        int minTopY = 0;
        int maxTopY = WIDTH_BOARD / 10 - padding; 
        
        // External Area Bottom
        int minBottomX = 0;
        int maxBottomX = WIDTH_BOARD;
        int minBottomY = WIDTH_BOARD * 9 / 10 - padding;
        int maxBottomY = WIDTH_BOARD;

        // External Area Left
        int minLeftX = 0;
        int maxLeftX = WIDTH_BOARD / 10 - padding;
        int minLeftY = WIDTH_BOARD / 10;
        int maxLeftY = WIDTH_BOARD * 9 / 10;

        // External Area Right
        int minRightX = WIDTH_BOARD * 9 / 10 - padding;
        int maxRightX = WIDTH_BOARD;
        int minRightY = WIDTH_BOARD / 10;
        int maxRightY = WIDTH_BOARD * 9 / 10;

        // Inner Area
        int originInnerX = WIDTH_BOARD / 10;
        int originInnerY = WIDTH_BOARD / 10;
        int maxInnerX = WIDTH_BOARD * 8 / 10;
        int maxInnerY = WIDTH_BOARD * 8 / 10;

        // Inner Area 1
        int minX_1 = originInnerX;
        int maxX_1 = maxInnerX / 2;
        int minY_1 = originInnerY;
        int maxY_1 = maxInnerY;

        // Inner Area 2
        int minX_2 = maxInnerX / 2;
        int maxX_2 = maxInnerX;
        int minY_2 = originInnerY;
        int maxY_2 = maxInnerY / 2;

        // Inner Area 3
        int minX_3 = maxInnerX / 2;
        int maxX_3 = maxInnerX;
        int minY_3 = maxInnerY / 2;
        int maxY_3 = maxInnerY;

        // Holes for external area
        //int extHoles = H / 5; 
        int innerHoles = H;

        int numAreas = 3;

        //generateRndHoles(holes, extHoles/4, minTopX, maxTopX, minTopX, maxTopY);
        //generateRndHoles(holes, extHoles/4, minBottomX, maxBottomX, minBottomY, maxBottomY);
        //generateRndHoles(holes, extHoles/4, minLeftX, maxLeftX, minLeftY, maxLeftY);
        //generateRndHoles(holes, extHoles/4, minRightX, maxRightX, minRightY, maxRightY);

        generateRndHoles(holes, innerHoles/numAreas, minX_1, maxX_1, minY_1, maxY_1);
        generateRndHoles(holes, innerHoles/numAreas, minX_2, maxX_2, minY_2, maxY_2);
        generateRndHoles(holes, innerHoles/numAreas, minX_3, maxX_3, minY_3, maxY_3);

        return holes.toArray(new Hole[holes.size()]);
    }

    // side effect on holes array
    private static void generateRndHoles(ArrayList<Hole> holes, int numHoles, 
                                            int originX, int maxX, int originY, int maxY) {
        for (int i = 0; i < numHoles; i++) {
            double rndX = ThreadLocalRandom.current().nextDouble(originX + 1, maxX);
            double rndY = ThreadLocalRandom.current().nextDouble(originY + 1, maxY);

            holes.add(new Hole(rndX, rndY));
        }
    }

    
    // Deprecated
    private static Hole[] generateHoles(){
        Hole[] holes = new Hole[H];
        Random generator = new Random(SEED_RAND);

        for(int i = 0; i < H; i++){
            double start = 0;
            double end = WIDTH_BOARD;
            double generatedRand_X = start + generator.nextDouble() * (end - start);
            double generatedRand_Y = start + generator.nextDouble() * (end - start);

            holes[i] = new Hole(generatedRand_X, generatedRand_Y);
        }

        return holes;
    }

    private static void printHoles(Hole[] holes){
        for(int i = 0; i < holes.length; i++){
            System.out.println("Hole_" + i + " " + holes[i].getX() + " " + holes[i].getY());
        }
    }

    private static void printTableOfHoles(Hole[] holes) {
        for (int i = 0; i < holes.length; i++){
            System.out.println(holes[i].getX() + ", " + holes[i].getY() + ",");
        }
    }

    private static void printMatrix(double[][] matrix){
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                System.out.print(matrix[i][j] + "\t");
            }
            System.out.println("\n ");
        }
    }

    private static double[][] generateMatrixCosts(Hole[] holes) {
        double[][] matrix = new double[H][H];
        double distance;
        double x1;
        double x2;
        double y1;
        double y2;

        for (int row = 0; row < H; row++) {
            for (int col = row + 1; col < H; col++) {
                if (row != col) {
                    x1 = holes[row].getX();
                    x2 = holes[col].getX();

                    y1 = holes[row].getY();
                    y2 = holes[col].getY();

                    distance = Math.hypot(x1-x2, y1-y2);

                    matrix[row][col] = distance;
                    matrix[col][row] = distance;
                }
            }
        }
        return matrix;
    }

    public static void generateDat(double[][] C) throws IOException {
        BufferedWriter outputWriter = null;
        outputWriter = new BufferedWriter(new FileWriter(FILENAME_DAT));

        String digit;
        BigDecimal d;

        // Header num of Holes
        outputWriter.write("" + H);
        outputWriter.newLine();

        for (int i = 0; i < H; i++) {
            for(int k = 0; k < H; k++){

                if(ROUND_ENABLE) {
                    d = BigDecimal.valueOf(C[i][k]).setScale(ROUND_SCALE, BigDecimal.ROUND_HALF_UP);
                }
                else {
                    d = BigDecimal.valueOf(C[i][k]);
                }
                digit = String.format(Locale.US, "%."+ SIGN_DIGITS + "f" , d) ; // %.2f
                outputWriter.write(digit +"\t");
            }
            outputWriter.newLine();
        }
        outputWriter.flush();
        outputWriter.close();
    }

    public static void generateFileOfPoints(Hole[] holes) throws IOException {
        BufferedWriter outputWriter = null;
        outputWriter = new BufferedWriter(new FileWriter(FILENAME + ".txt"));

        String digit;
        BigDecimal x;
        BigDecimal y;

        for (int k = 0; k < holes.length; k++){

            /*if (ROUND_ENABLE) {
                x = BigDecimal.valueOf(holes[k].getX() * 10).setScale(ROUND_SCALE, BigDecimal.ROUND_HALF_UP);
                y = BigDecimal.valueOf(holes[k].getY() * 10).setScale(ROUND_SCALE, BigDecimal.ROUND_HALF_UP);
            }
            else {*/
                x = BigDecimal.valueOf(holes[k].getX());
                y = BigDecimal.valueOf(holes[k].getY());
            //}

            //digit = String.format(Locale.US, "%."+ SIGN_DIGITS + "f" , x) ; // %.2f
            outputWriter.write(x +", " + y + ",");
            outputWriter.newLine();
        }
        outputWriter.flush();
        outputWriter.close();
    }
}
