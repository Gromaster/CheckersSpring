package com.checkers.models;

import java.util.ArrayList;
import java.util.Collections;

public class Board {
    private final int BOARD_SIZE=32;
    private final int NUMBER_OF_ROWS=8;
    private ArrayList<Place> places= new ArrayList<Place>(BOARD_SIZE);


    public Board() {
        initPlaces();
    }

    public Place getPlace(char column,int row){
        return places.get(places.indexOf(new Place(column,row)));
    }

    private void initPlaces(){
        for(int i=1;i<=NUMBER_OF_ROWS;i++){
            for(int j=0;j<(BOARD_SIZE/NUMBER_OF_ROWS);j++)
                places.add(new Place( (char) ( i%2==1 ?  ('A'+2*j) : ('B'+2*j) ) , i));
        }
        if(places.size()!=32) throw new RuntimeException("Places initialization failed, illegal number of places");
    }

    public ArrayList<Place> getPlaces() {
        return places;
    }

    public void setPieceOnPlaces(Place place,Piece piece) {
        this.places.get(places.indexOf(place)).setPieceOccupying(piece);
    }

    public Place getPlace(Place place) {
        return places.get(places.indexOf(place));
    }

    public Place placeBetween(Place place1,Place place2){
        int middleRow = (place1.getRow() + place2.getRow())/2;
        char middleColumn = (char)((place1.getColumn() + place2.getColumn())/2);
        return getPlace(middleColumn,middleRow);
    }

}


/*
Marek ogląda sobie filmik na youtubku zamiast robić rzeczy na studia, Marek słyszy "half twelve" a widzi na filmiku 00:30
Chwila, czy po niemiecku "halb zwolf" nie oznaczałoby wpół do dwónastej ??
Muszę to sprawdzić [...one translate later...] HA miałem rację, ja to jednak germanista jestem, już widzę efekty nauki języka.
Chwila kurwa co ty robisz miałeś kodzić, ujebiesz te studia ,idioto!
Ale chwila, to w takim razie ten Angol w filmiku się pojebał że powiedział "half twelve" na 00:30 czy w UK mówią w drugą stronę?
[... one google later...] Lel oni mówią w drugą stronę, dziwne Brytole
Kurwa wracaj do kodzenia chory pojebie
Hmm ale co jeśli uwzględnimy strefy czasowe, co się wtedy stanie?
[...google graphics...] Ej oni są w innych strefach (UK i Niemcy)
UK ma -1 w stosunku do Niemiec
To oznacza że gdy u Brytola jest 3:30 to on powie "half three"
Ale u Niemca jest wtedy 4:30 i on powie "halb funf"
Kurwa nie wyszło, myślałem że może się zjadą i powiedzą to samo
Co to oznacza dla świata
Co to oznacza dla Europy
Zresztą, czy te studia trzeba w ogóle zdać?
*/


