package com.checkers.models;

public class NodeOfJumpTree {
    public NodeOfJumpTree south=null,east=null,north=null,west=null;
    public Place place;

    public NodeOfJumpTree(Place here) {
        this.place = here;
    }

    public void setNextChild(Place place){
        if(east==null)east=new NodeOfJumpTree(place);
        else if(south==null)south=new NodeOfJumpTree(place);
        else if(west==null)west=new NodeOfJumpTree(place);
        else if(north==null)north=new NodeOfJumpTree(place);
    }
}
