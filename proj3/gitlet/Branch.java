package gitlet;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static gitlet.Commit.importCommit;
import static gitlet.Main.ACTIVE_BRANCH_FILE;
import static gitlet.Main.BRANCHES_FILE;
import static gitlet.Utils.message;
import static gitlet.Utils.readContentsAsString;
import static gitlet.Utils.readObject;
import static gitlet.Utils.serialize;
import static gitlet.Utils.writeContents;
import static java.lang.Math.min;


/** Class that represents the Branch object, a single object that stores
 * mappings of all Branch names to their respective Commit hashes.
 * @author Rae Xin
 * */
public class Branch implements Serializable {

    /** Map of all branches. */
    private Map<String, String> _branchMap;

    /** Returns a map representing all the branches in this object, mapping
     * branch name
     to the Commit that it is pointing to. */
    public Map<String, String> getMap() {
        return _branchMap;
    }

    /** Given BRANCHNAME and COMMITHASH, creates a new Branch that is
     * pointing to the Commit specified by the hash. */
    public Branch(String branchName, String commitHash) {
        if (branchName.equals("master")) {
            _branchMap = new TreeMap<>();
            exportBranch();
            updateActiveBranch("master");
        }

        _branchMap = importBranches().getMap();

        if (!_branchMap.containsKey(branchName)) {
            _branchMap.put(branchName, commitHash);
        } else {
            message("A branch with that name already exists.");
            System.exit(0);
        }
        this.exportBranch();
    }

    /** Deletes the Branch specified by the name BRANCHNAME. */
    public static void deleteBranch(String branchName) {
        var currentBranch = importBranches();
        var currentMap = currentBranch.getMap();
        if (!currentMap.containsKey(branchName)) {
            message("A branch with that name does not exist.");
            System.exit(0);
        } else if (fetchActiveBranchName().equals(branchName)) {
            message("Cannot remove the current branch.");
            System.exit(0);
        } else {
            currentMap.remove(branchName);
            currentBranch.exportBranch();
        }
    }

    /** Returns the Branch that is currently stored in the Branches file. */
    public static Branch importBranches() {
        return readObject(BRANCHES_FILE, Branch.class);
    }

    /** Updates the Branch object with the current Branch object. */
    public void exportBranch() {
        byte[] bytes = serialize(this);
        writeContents(BRANCHES_FILE, bytes);
    }

    /** Sets the current (active) branch to the Branch specified by
     * BRANCHNAME. */
    public static void updateActiveBranch(String branchName) {
        try (PrintWriter out = new PrintWriter(ACTIVE_BRANCH_FILE)) {
            out.println(branchName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /** Returns the name of the current (active) branch. */
    public static String fetchActiveBranchName() {
        String activeBranchName = readContentsAsString(ACTIVE_BRANCH_FILE);
        activeBranchName = activeBranchName.replaceAll("[\\n\\r]", "");
        return activeBranchName;
    }

    /** Finds latest common ancestor between BRANCHFIRST and BRANCHOTHER, where
     * these are names of branches. Returns the commit hash of the latest common
     * ancestor. */
    public static String findLatestCommonAncestor(String branchFirst,
                                                  String branchOther) {
        Branch branches = importBranches();
        var branchMap = branches.getMap();
        if (!(branchMap.containsKey(branchFirst)
                && branchMap.containsKey(branchOther))) {
            message("A branch with that name does not exist.");
            System.exit(0);
        } else if (branchFirst.equals(branchOther)) {
            message("Cannot merge a branch with itself.");
            System.exit(0);
        }
        Commit commitFirst = importCommit(branchMap.get(branchFirst));
        Commit commitOther = importCommit(branchMap.get(branchOther));
        List<String> firstAncestors = new ArrayList<>();
        List<String> otherAncestors = new ArrayList<>();
        getAncestors(commitFirst, firstAncestors);
        getAncestors(commitOther, otherAncestors);
        List<String> commonAncestors = new ArrayList<>();
        for (String firstAncestorHash : firstAncestors) {
            if (otherAncestors.contains(firstAncestorHash)) {
                commonAncestors.add(firstAncestorHash);
            }
        }

        int shortestCombinedPath = Integer.MAX_VALUE;
        String shortestLCA = null;

        for (String commonAncestorHash : commonAncestors) {
            Commit commonAncestor = importCommit(commonAncestorHash);
            int combinedPath = getShortestPath(commitFirst, commonAncestor, 0)
                    + getShortestPath(commitOther, commonAncestor, 0);
            if (combinedPath < shortestCombinedPath) {
                shortestCombinedPath = combinedPath;
                shortestLCA = commonAncestorHash;
            }
        }
        return shortestLCA;
    }

    /** Mutates a list to contain all of COMMIT's ancestors by their SHA-1
     * hash values. */
    private static void getAncestors(Commit commit,
                                      List<String> allAncestors) {
        allAncestors.add(commit.getHash());
        if (commit.getParentHash() != null) {
            getAncestors(commit.getParentCommit(), allAncestors);
        }
        if (commit.getParent2Hash() != null) {
            getAncestors(commit.getParent2Commit(), allAncestors);
        }
    }

    /** Find and return shortest path between COMMIT and ANCESTOR. */
    private static int getShortestPath(Commit commit, Commit ancestor,
                                       int steps) {
        if (commit.equals(ancestor)) {
            return steps;
        } else if (commit.hasParent1() && commit.hasParent2()) {
            return min(getShortestPath(commit.getParentCommit(),
                    ancestor, steps + 1),
                    getShortestPath(commit.getParent2Commit(),
                    ancestor, steps + 1));
        } else if (commit.hasParent1()) {
            return getShortestPath(commit.getParentCommit(), ancestor,
                    steps + 1);
        } else if (commit.hasParent2()) {
            return getShortestPath(commit.getParent2Commit(), ancestor,
                    steps + 1);
        } else {
            return Integer.MAX_VALUE;
        }
    }

    /** Returns whether Commit POTENTIALANCESTOR is an ancestor of Commit,
     * COMMITHASH. */
    public static boolean isAncestor(String potentialAncestor,
                                     String commitHash) {
        Commit potentialCommit = importCommit(potentialAncestor);
        Commit commit = importCommit(commitHash);
        return false;
    }

}
