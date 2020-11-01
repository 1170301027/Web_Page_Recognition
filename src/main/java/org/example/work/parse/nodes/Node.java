package org.example.work.parse.nodes;

/**
 * 节点类型，保存
 */
public abstract class Node {
    Element parent;
    int depth;
    int index;

    Node(Element parent, int index){
        this.parent = parent;
        this.depth = parent == null ? 0 : parent.depth + 1;
        this.index = index << 24;
    }

    Node(){ }

    public Element parent(){
        return parent;
    }

    public void parent(Element parent){
        this.depth = parent == null ? 0 : parent.depth + 1;
        this.parent = parent;
    }

    public int index(){
        return index >>> 24;
    }

    public void setHashCode(int hashCode){
        index = (index & 0xFF_00_00_00) | (hashCode & 0x00_FF_FF_FF) | 0x01;
    }

    @Override
    public int hashCode(){
        return index & 0x00_FF_FF_FF;
    }

    public boolean hasHashCode(){
        return (index & 0x01) == 1;
    }

    public void updateIndex(){
        if(parent == null){
            index = index & 0x00_FF_FF_FF;
        }else{
            index = (index & 0x00_FF_FF_FF) | (parent.children().indexOf(this) << 24);
        }
    }

    public int getDepth(){
        return depth;
    }

    public String toFpString(){
        return "";
    }
}
