package com.checkers.models;

import java.util.ArrayList;

public class JumpTree{
    NodeOfJumpTree root=null;

    public JumpTree(NodeOfJumpTree root) {
        this.root = root;
    }


    public ArrayList<Place> getMaxRoute(NodeOfJumpTree node){
        ArrayList<Place> route = new ArrayList<>();
        ArrayList<Place> checker,buffor ;
        if(node==null)return route;
        else route.add(node.place);

        checker=getMaxRoute(node.east);
        if((buffor=getMaxRoute(node.south)).size()>(checker==null ? 0 : route.size()))checker=buffor;
        if((buffor=getMaxRoute(node.west)).size()>(checker==null ? 0 : route.size()))checker=buffor;
        if((buffor=getMaxRoute(node.north)).size()>(checker==null ? 0 : route.size()))checker=buffor;
        route.addAll(checker);
        return route;
    }



}
