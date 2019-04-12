package com.checkers.models;

public class NodeOfJumpTree {
    public NodeOfJumpTree south=null,east=null,north=null,west=null;
    public Place place;

    public NodeOfJumpTree(Place here) {
        this.place = here;
    }

    public void setNextChild(NodeOfJumpTree newNode){
        if(east==null)east=newNode;
        else if(south==null)south=newNode;
        else if(west==null)west=newNode;
        else if(north==null)north=newNode;
    }
}
