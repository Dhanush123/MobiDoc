package com.x10host.dhanushpatel.mobidoc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chapatel on 1/22/17.
 */

public class Datas {
    public ArrayList<Double> cataractData = new ArrayList<>();
    public ArrayList<Double> vitiligoData = new ArrayList<>();
    public ArrayList<Double> cornData = new ArrayList<>();
    public ArrayList<Double> redveinData = new ArrayList<>();

    public Datas(){

    }

    public Datas(ArrayList<Double> ca,ArrayList<Double> vi,ArrayList<Double> co,ArrayList<Double> re){
        cataractData = ca;
        vitiligoData = vi;
        cornData = co;
        redveinData = re;
    }

    public Map<String,Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("cataractData",cataractData);
        result.put("vitiligoData",vitiligoData);
        result.put("cornData",cornData);
        result.put("redveinData",redveinData);
        return result;
    }
}
