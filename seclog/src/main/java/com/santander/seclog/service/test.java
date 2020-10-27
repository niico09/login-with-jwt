package com.santander.seclog.service;

import com.sun.mail.imap.protocol.INTERNALDATE;

import javax.swing.text.Segment;
import java.util.*;

public class test {

    public static void main(String[] args) {

        List<Integer> add = new ArrayList<>();
        add.add(1);
        add.add(1);
        add.add(1);
        //add.add(6);
      //  add.add(8);


        System.out.println(segment(3, add));
    }

    public static int segment(int x, List<Integer> space) {
        Map<Integer, List<Integer>> segments = new HashMap<>();

        for (int i = 0; i < space.size(); i++) {
            if(x > space.size()){
                break;
            }

            List<Integer> listAux = new ArrayList<Integer>();

            if(x+i <= space.size()){
                for(int j = i; j < x + i; j++){
                    listAux.add(space.get(j));
                }

                segments.put(i, listAux);
            }
        }

        List<Integer> minList = new ArrayList<>();

        for(Map.Entry<Integer, List<Integer>> entry : segments.entrySet()){
            List<Integer> listAux = entry.getValue();

            int min = Integer.MAX_VALUE;
            for (Integer aux : listAux) {
                if (aux < min) {
                    min = aux;
                }
            }
            minList.add(min);

        }

        int max = Integer.MIN_VALUE;
        for(int maxValue : minList){
            if(maxValue > max){
                max = maxValue;
            }
        }


        return max;

    }


}
