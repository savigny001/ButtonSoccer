package com.soccer.pojo;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;

public class Player {
    private Integer id;
    private String firstName;
    private String lastName;
    private Integer number;
    private Post post;
    private Sprite sprite;
    private Body body;
    private Team team;
    private Boolean isTouchedBall;
    private Boolean isAimed;
    private float distanceFromBall;
    private Boolean hasToWriteName, hasPhoto;
    private String photoPath;
    public Position position, originalPosition;

    public Player () {
    }

    public Player(String firstName, Position position) {
        this(firstName, position, Post.OUTFIELD_PLAYER);
    }

    public Player(String firstName, Position position, Post post, String photoPath) {
        this.firstName = firstName;
        this.position = position;
        this.originalPosition = position;
        this.post = post;
        this.photoPath = photoPath;
        hasPhoto = true;
        hasToWriteName = true;
        isAimed = false;
    }

    public Player(String firstName, Position position, Post post) {
        this(firstName, position, post, "");
        hasPhoto = false;
    }

    public void resetOriginalPosition() {
        position = originalPosition;
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Boolean getTouchedBall() {
        return isTouchedBall;
    }

    public void setTouchedBall(Boolean touchedBall) {
        isTouchedBall = touchedBall;
    }

    public float getDistanceFromBall() {
        return distanceFromBall;
    }

    public void setDistanceFromBall(float distanceFromBall) {
        this.distanceFromBall = distanceFromBall;
    }

    public Boolean isAimed() {
        return isAimed;
    }

    public void setAimed(Boolean aimed) {
        isAimed = aimed;
    }

    public Boolean getHasToWriteName() {
        return hasToWriteName;
    }

    public void setHasToWriteName(Boolean hasToWriteName) {
        this.hasToWriteName = hasToWriteName;
    }

    public Boolean getHasPhoto() {
        return hasPhoto;
    }

    public void setHasPhoto(Boolean hasPhoto) {
        this.hasPhoto = hasPhoto;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}
