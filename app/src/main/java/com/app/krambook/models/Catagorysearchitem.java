package com.app.krambook.models;

/**
 * Created by mobile_perfect on 14/12/14.
 */
public class Catagorysearchitem implements Comparable<Catagorysearchitem> {

    private long id;
    private String catagory;
    private String contente;


    public Catagorysearchitem(long id, String catagory) {
        super();
        this.id = id;
        this.catagory=catagory;
        this.contente=contente;


    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCatagory() {
        return catagory;
    }

    public void setCatagory(String catagory) {
        this.catagory= catagory;
    }



    public int compareTo(Catagorysearchitem other) {
        return (int) (id - other.getId());
    }
}
