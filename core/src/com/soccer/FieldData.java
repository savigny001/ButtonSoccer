package com.soccer;

public interface FieldData {

    public static final float PIXELS_TO_METERS = 100f;
    public static final float SECTOR_RADIUS = 200f;
    public static final float BALL_RADIUS = 20f;
    public static final float PLAYER_RADIUS = 50f;
    public static final float GOALPOST_RADIUS = 16f;
    public static final float NET_HEIGHT = 200f*2;

//    public static final int TABLE_WIDTH = 1680;
//    public static final int TABLE_HEIGHT = 1085;

    public static final int TABLE_WIDTH = 1680*2;
    public static final int TABLE_HEIGHT = 1085*2;

    public static final int SCREEN_WIDTH = 1366;
    public static final int SCREEN_HEIGHT = 768;


//    public static final int TABLE_WIDTH = 1366*2;
//    public static final int TABLE_HEIGHT = 768*2;

    public static final float TOUCHLINE_TABLE_PERCENTAGE = 0.8f;
    public static final float GOALLINE_TABLE_PERCENTAGE = 0.8f;

    public static final float GOALBOX_WIDTH_FIELD_PERCENTAGE = 0.05f;
    public static final float GOALBOX_HEIGHT_FIELD_PERCENTAGE = 0.29f;

    public static final float PENALTYBOX_WIDTH_FIELD_PERCENTAGE = 0.142f;
    public static final float PENALTYBOX_HEIGHT_FIELD_PERCENTAGE = 0.568f;
    public static final float PENALTYSPOT_DISTANCE_FROM_GOALLINE_FIELD_PERCENTAGE = 0.1f;

    public static final float TOUCHLINE_LENGTH = TABLE_WIDTH * TOUCHLINE_TABLE_PERCENTAGE;
    public static final float GOALLINE_LENGTH = TABLE_HEIGHT * GOALLINE_TABLE_PERCENTAGE;

    public static final float GOALBOX_WIDTH = TOUCHLINE_LENGTH * GOALBOX_WIDTH_FIELD_PERCENTAGE;
    public static final float GOALBOX_HEIGHT = GOALLINE_LENGTH * GOALBOX_HEIGHT_FIELD_PERCENTAGE;
    public static final float PENALTYBOX_WIDTH = TOUCHLINE_LENGTH * PENALTYBOX_WIDTH_FIELD_PERCENTAGE;
    public static final float PENALTYBOX_HEIGHT = GOALLINE_LENGTH * PENALTYBOX_HEIGHT_FIELD_PERCENTAGE;
    public static final float PENALTYSPOT_DISTANCE_FROM_GOALLINE = TOUCHLINE_LENGTH *
            PENALTYSPOT_DISTANCE_FROM_GOALLINE_FIELD_PERCENTAGE;

    public static final float CORNERSPOT_NW_X = -TOUCHLINE_LENGTH/2;
    public static final float CORNERSPOT_NW_Y = GOALLINE_LENGTH/2;
    public static final float CORNERSPOT_SW_X = -TOUCHLINE_LENGTH/2;
    public static final float CORNERSPOT_SW_Y = -GOALLINE_LENGTH/2;
    public static final float CORNERSPOT_NE_X = TOUCHLINE_LENGTH/2;
    public static final float CORNERSPOT_NE_Y = GOALLINE_LENGTH/2;
    public static final float CORNERSPOT_SE_X = TOUCHLINE_LENGTH/2;
    public static final float CORNERSPOT_SE_Y = -GOALLINE_LENGTH/2;

    public static final float GOALKICKSPOT_W_X = -TOUCHLINE_LENGTH/2 + GOALBOX_WIDTH;
    public static final float GOALKICKSPOT_W_Y = GOALBOX_HEIGHT/2 - GOALBOX_HEIGHT/10;
    public static final float GOALKICKSPOT_E_X = TOUCHLINE_LENGTH/2 - GOALBOX_WIDTH;
    public static final float GOALKICKSPOT_E_Y = -GOALBOX_HEIGHT/2 + GOALBOX_HEIGHT/10;



}