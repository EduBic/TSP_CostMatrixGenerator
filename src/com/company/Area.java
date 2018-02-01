package com.company;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

class Area {

    // Area Options
    static int MIN_AREA_WIDTH = Main.WIDTH_BOARD / 20;
    static int MAX_AREA_WIDTH = Main.WIDTH_BOARD / 10;

    int originX;
    int originY;
    int maxX;
    int maxY;

    Area(int width, int c_x, int c_y){
       originX = c_x;
       originY = c_y;

       maxX = originX + width;
       maxY = originY + width;
    }

    public boolean overlap(ArrayList<Area> areas) {
        for (Area area : areas) {
            if (originX < area.maxX &&
                maxX > area.originX &&
                originY < area.maxY &&
                maxY > area.originY) {
                    return true;
                }
        }
        return false;
    } 

    public int getWidth() {
        return maxX - originX;
    }

    public static ArrayList<Area> buildAreas(int maxAreas) {
        Area firstArea = buildRandomArea();
        ArrayList<Area> areas = new ArrayList<>();

        int areasBuilt = 0;

        while (areasBuilt < maxAreas) {
            Area newArea = buildRandomArea();
            
            if (!newArea.overlap(areas)) {
                areas.add(newArea);
                areasBuilt++;
            }
        }

        return areas;
    }

    private static Area buildRandomArea() {
        int width = ThreadLocalRandom.current().nextInt(MIN_AREA_WIDTH, MAX_AREA_WIDTH + 1);
        return new Area(width, 
            ThreadLocalRandom.current().nextInt(0, Main.WIDTH_BOARD - width),
            ThreadLocalRandom.current().nextInt(0, Main.WIDTH_BOARD - width)
            );
    }
}
