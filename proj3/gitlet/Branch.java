package gitlet;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import static gitlet.Main.*;
import static gitlet.Utils.*;

public class Branch implements Serializable {

    private Map<String, String> _branchMap;

    public Map<String, String> getMap() {
        return _branchMap;
    }

    public Branch(String branchName, String commitHash) {
        if (branchName.equals("master")) {
            _branchMap = new TreeMap<>();
            exportBranch();
            setNewBranchHead("master");
        }

        _branchMap = importBranch().getMap();

        if (!_branchMap.containsKey(branchName)) { // check that BRANCHES_FILE Branch doesn't already have this branch listed
            _branchMap.put(branchName, commitHash);
        } else {
            throw error("A branch with that name already exists.");
        }


    }

    public Branch importBranch() {
        return readObject(BRANCHES_FILE, Branch.class);
    }

    public void exportBranch() {
        byte[] bytes = serialize(this);
        writeContents(BRANCHES_FILE, bytes);
    }

    public static void setNewBranchHead(String branchName) {
        HEAD_BRANCH_NAME = branchName;
        try (PrintWriter out = new PrintWriter(HEAD_BRANCHES_FILE)) {
            out.println(branchName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String getHeadBranchName() {
        return readContentsAsString(HEAD_BRANCHES_FILE);
    }
}
