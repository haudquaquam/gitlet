package gitlet;

import org.antlr.v4.runtime.tree.Tree;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Stage {

    private Map<String, String> _stage; //map between filename and -B-l-o-b- SHA-1 hash

    public Stage() {
        _stage = new TreeMap<>();
    }

    public Map<String, String> getStage() {
        return _stage;
    }

    public void addToStage(String filename, String flag) {
        //logic for adding stuff to stage
    }

}
