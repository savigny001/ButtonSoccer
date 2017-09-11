package com.soccer.pojo;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Kit implements Serializable {

    private String typePath;
    private List<Color> colors;
    private Sprite sprite;
    private Pixmap pixmap;
    private float playersPhotoRadius;

    public Kit () {
        typePath = "kittypes/default.png";
        colors = new ArrayList<Color>();
        colors.add(new Color(10,10,10,1));
        colors.add(new Color(50,10,10,1));
        colors.add(new Color(10,50,10,1));
        colors.add(new Color(10,10,50,1));
        colors.add(new Color(100,50,10,1));
        playersPhotoRadius = 30;
    }

    public String getTypePath() {
        return typePath;
    }

    public void setTypePath(String typePath) {
        this.typePath = typePath;
    }

    public List<Color> getColors() {
        return colors;
    }

    public void setColors(List<Color> colors) {
        this.colors = colors;
    }

    public float getPlayersPhotoRadius() {
        return playersPhotoRadius;
    }

    public void setPlayersPhotoRadius(float playersPhotoRadius) {
        this.playersPhotoRadius = playersPhotoRadius;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    public Pixmap getPixmap() {
        return pixmap;
    }

    public void setPixmap(Pixmap pixmap) {
        this.pixmap = pixmap;
    }


}
