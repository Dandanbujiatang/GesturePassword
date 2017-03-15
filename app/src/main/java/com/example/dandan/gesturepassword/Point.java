package com.example.dandan.gesturepassword;

/**
 * Created by dandan on 2017/3/7.
 */
public class Point {

    public float x;
    public float y;
    public int state = 0;
    public int index = 0;

    public Point() {

    }

    public Point(float x, float y, int value) {
        this.x = x;
        this.y = y;
        index = value;
    }

    public int getColNum() {
        return (index - 1) % 3;
    }

    public int getRowNum() {
        return (index - 1) / 3;
    }


}