package gitlet;

import java.io.*;
import java.util.Map;

import static gitlet.Main.BRANCHES_FILE;
import static gitlet.Utils.error;

public class Branch implements Serializable {

    private Map<String, String> _branchMap;

    public Map<String, String> getMap() {
        return _branchMap;
    }

    public Branch(String branchName, String commitHash) {

        var branch = importBranch();
        _branchMap = branch.getMap();

        if (!(_branchMap == null) && !_branchMap.containsKey(branchName)) { // check that BRANCHES_FILE Branch doesn't already have this branch listed
            _branchMap.put(branchName, commitHash);
        } else {
            throw error("A branch with that name already exists.");
        }
    }

    public Branch importBranch() {
        Branch branch;
        try {
            ObjectInputStream inp =
                    new ObjectInputStream(new FileInputStream(BRANCHES_FILE));
            branch = (Branch) inp.readObject();
            inp.close();

        } catch (IOException | ClassNotFoundException e) {
            branch = null;
            e.printStackTrace();
        }
        return branch;
    }

    public void exportBranch() {
        try {
            ObjectOutputStream out =
                    new ObjectOutputStream(new FileOutputStream(BRANCHES_FILE));
            out.writeObject(this);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
