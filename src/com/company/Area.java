package com.company;

import java.util.ArrayList;

class Area {

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
            /*if (this.originX >= area.originX && this.originY >= area.originY && 
                this.maxX <= area.maxX && this.maxY <= area.maxY) {
                // area overlap
                return true;   
            }*/
            /*if (originX >= area.originX && originX <= area.maxX &&
                originY >= area.originY && originY <= area.maxY) {
                return true;
            }
            if (maxX >= area.originX && maxX <= area.maxX && 
                maxY >= area.originY && maxY <= area.maxY) {
                return true;
            }
            if (originX >= area.originX && originX <= area.maxX &&
                maxY >= area.originY && maxY <= area.maxY) {
                return true;
            }
            if (maxX >= area.originX && maxX <= area.maxX &&
                originY >= area.originY && originY <= area.maxY) {
                return true;
            }*/
            if (originX < area.maxX &&
                maxX > area.originX &&
                originY < area.maxY &&
                maxY > area.originY) {
                    return true;
                }
        }
        return false;
    } 
}
