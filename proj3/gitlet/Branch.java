package gitlet;

import java.io.*;
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
            updateActiveBranch("master");
        }

        _branchMap = importBranches().getMap();

        if (!_branchMap.containsKey(branchName)) { // check that BRANCHES_FILE Branch doesn't already have this branch listed
            _branchMap.put(branchName, commitHash);
        } else {
            throw error("A branch with that name already exists.");
        }
    }

  /*  public static void updateBranchHead(String branchName, String commitHash) {

    }*/

    public static void deleteBranch(String branchName) {
        var currentBranch = importBranches();
        var currentMap = currentBranch.getMap();
        if (!currentMap.containsKey(branchName)) {
            throw error("A branch with that name does not exist.");
        } else if (fetchActiveBranchName().equals(branchName)) {
            throw error("Cannot remove the current branch.");
        } else {
            currentMap.remove(branchName);
            currentBranch.exportBranch();
        }
    }

    public static Branch importBranches() {
        return readObject(BRANCHES_FILE, Branch.class);
    }

    public void exportBranch() {
        byte[] bytes = serialize(this);
        writeContents(BRANCHES_FILE, bytes);
    }

    public static void updateActiveBranch(String branchName) {
        try (PrintWriter out = new PrintWriter(ACTIVE_BRANCH_FILE)) {
            out.println(branchName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String fetchActiveBranchName() {
        String activeBranchName = readContentsAsString(ACTIVE_BRANCH_FILE);
        activeBranchName = activeBranchName.replaceAll("[\\n\\r]", "");
        return activeBranchName;
    }
}
