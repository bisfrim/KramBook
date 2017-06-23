package com.app.krambook.models;

/**
 * Created by mobile_perfect on 12/12/14.
 */
public class Homeitem {
    private String ID;
    private String category;
    private String condition;
    private String username;
    private String expirdate;
    private String tagline;
    private String title;
    private String author;
    private String price;
    private String isbn;
    private String like;
    private String comment;
    private String imageurl;
    private String location;
    private String retail;
    private String likeflag;
    private String followflag;
    private String userimageurl;
    private String userrealname;
    private String expireflag;
    private String inappflag;
    private String userID;

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserID() {
        return userID;
    }

    public void setExpireflag(String expireflag) {
        this.expireflag = expireflag;
    }

    public void setInappflag(String inappflag) {
        this.inappflag = inappflag;
    }

    public String getExpireflag() {
        return expireflag;
    }

    public String getInappflag() {
        return inappflag;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public void setUserrealname(String userrealname) {
        this.userrealname = userrealname;
    }

    public String getUserrealname() {
        return userrealname;
    }

    public String getUserimageurl() {
        return userimageurl;
    }

    public void setUserimageurl(String userimageurl) {
        this.userimageurl = userimageurl;
    }

    public void setLikeflag(String likeflag) {
        this.likeflag = likeflag;
    }

    public String getLikeflag() {
        return likeflag;
    }

    public void setFollowflag(String followflag) {
        this.followflag = followflag;
    }

    public String getFollowflag() {
        return followflag;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getID() {
        return ID;
    }



    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public String getRetail() {
        return retail;
    }

    public void setRetail(String retail) {
        this.retail = retail;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public String getusername() {
        return username;
    }

    public void setusername(String username) {
        this.username = username;
    }

    public String getExpirdate() {
        return expirdate;
    }

    public void setExpirdate(String expirdate) {
        this.expirdate =expirdate;
    }

    public String getIamgeURL() {
        return imageurl;
    }

    public void setIamgeURL(String iamgeurl) {
        this.imageurl = iamgeurl;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public String getBookTitle(){
        return title;
    }
    public void setBookTitle(String title){
        this.title = title;
    }
    public String getBookIsbn(){
        return isbn;
    }
    public void setBookIsbn(String isbn){
        this.isbn = isbn;
    }
    public String getBookAuthor(){
        return author;
    }
    public void setBookAuthor(String author){
        this.author = author;
    }
    public String getBookPrice(){
        return price;
    }
    public void setBookPrice(String price){
        this.price = price;
    }
    public String getBookCondition(){
        return condition;
    }
    public void setBookCondition(String condition){
        this.condition = condition;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }


}
