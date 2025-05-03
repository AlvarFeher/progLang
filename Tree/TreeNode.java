package Tree;

import java.util.ArrayList;
import java.util.List;

public class TreeNode {
    public String label;
    public String value;
    public List<TreeNode> children;

    public TreeNode(String label, String value) {
        this.label = label;
        this.value = value;
        this.children = new ArrayList<>();
    }

    public TreeNode(String label){
        this.label = label;
        this.children = new ArrayList<>();
    }

    public void addChild(TreeNode child) {
        children.add(child);
    }

    public String toString(){
        return toString("");
    }

    private String toString(String indent) {
        StringBuilder sb = new StringBuilder();
        for(TreeNode child : children){
            sb.append(child.toString(indent + "  "));
        }
        return sb.toString();
    }

    public TreeNode find(String label) {
        for (TreeNode child : children) {
            if (child.label != null && child.label.equals(label)) {
                return child;
            }
        }
        return null;
    }



}
