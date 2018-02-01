package com.company;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

class Line {

    int originX;
    int originY;

    Line(int c_x, int c_y){
        originX = c_x;
        originY = c_y;
    }


    public static ArrayList<Line> buildLines(int maxLine, int lenght) {
        Line firstLine = buildRandomLine(lenght);
        ArrayList<Line> lines = new ArrayList<>();

        int linesCount = 0;

        while (linesCount < maxLine) {
            Line newLine = buildRandomLine(lenght);
            
            //if (!newLine.overlap(lines)) {
                lines.add(newLine);
                linesCount++;
            //}
        }

        return lines;
    }

    private static Line buildRandomLine(int lenght) {
        return new Line( 
            ThreadLocalRandom.current().nextInt(0, Main.WIDTH_BOARD - lenght),
            ThreadLocalRandom.current().nextInt(0, Main.WIDTH_BOARD - lenght)
            );
    }
}