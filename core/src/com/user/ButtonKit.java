package com.user;

/**
 * Created by Zoli on 2017.05.08..
 */

public class ButtonKit {
    private KitColor[] kitColors = new KitColor[4];
    private String style;
    private Integer id;

    public KitColor[] getKitColors() {
        return kitColors;
    }

    public void setKitColors(KitColor[] kitColors) {
        this.kitColors = kitColors;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
