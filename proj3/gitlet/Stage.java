package gitlet;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Stage {

    private Map<String, List<Blob>> _stage;
    private static final String ADD_STR = "add";
    private static final String REMOVE_STR = "remove";

    public Stage(String filename, String flag) {
        _stage = new TreeMap<>();
        _stage.put(ADD_STR, new ArrayList<>());
        _stage.put(REMOVE_STR, new ArrayList<>());

    }

}
