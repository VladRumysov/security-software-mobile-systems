package com.example.vlad.lab_googlemaps;

/**
 * Created by Vlad on 15.04.2017.
 */

public class SendingData {

    public String device_id;
    public String coords;
    public Boolean is_hash;


    public String getDevice_id() {
        return device_id;
    }

    public String getCoords() {
        return coords;
    }

    public Boolean getIs_hash() {
        return is_hash;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public void setCoords(String coords) {
        this.coords = coords;
    }

    public void setIs_hash(Boolean is_hash) {
        this.is_hash = is_hash;
    }

}
