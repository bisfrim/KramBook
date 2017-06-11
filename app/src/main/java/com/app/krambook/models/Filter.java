package com.app.krambook.models;

/**
 * Created by mobile_perfect on 14/12/14.
 */
public class Filter implements Comparable<Filter> {

    private long id;
    private String filtername;

    public Filter(long id, String filtername) {
        super();
        this.id = id;
        this.filtername = filtername;

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFiltername() {
        return filtername;
    }

    public void setFiltername(String filtername) {
        this.filtername = filtername;
    }




    public int compareTo(Filter other) {
        return (int) (id - other.getId());
    }
}
