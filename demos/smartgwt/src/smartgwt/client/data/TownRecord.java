package smartgwt.client.data;

import java.io.Serializable;
import java.util.Date;

public class TownRecord implements Serializable {

    private Integer id;
    private String name;
    private Date date;

    public TownRecord() {
    }

    public TownRecord(Integer id, String name, Date date) {
        this.id = id;
        this.name = name;
        this.date = date;
    }

    public Integer getId () {
        return id;
    }

    public void setId (Integer id) {
        this.id = id;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public Date getDate () {
        return date;
    }

    public void setDate (Date date) {
        this.date = date;
    }

}
