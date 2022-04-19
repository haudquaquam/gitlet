package gitlet;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

import static gitlet.Main.BRANCHHEAD_FILE;
import static gitlet.Utils.*;

public class BranchHead implements Serializable {

    private Map<String, String> _branchToHeadCommitMap;

    public BranchHead(String branchName, String commitHash) {
        Branch branch = Branch.importBranches();
        Commit commit = Commit.importCommit(commitHash);
        _branchToHeadCommitMap = new TreeMap<>();
        if (branch.getMap().containsKey(branchName)) {
            _branchToHeadCommitMap.put(branchName, commitHash);

        } else {
            throw error("No such branch exists.");
        }
    }

    private static BranchHead importBranchHead() {
        return readObject(BRANCHHEAD_FILE, BranchHead.class);
    }

    private static void exportBranchHead(BranchHead branchHead) {
        writeObject(BRANCHHEAD_FILE, branchHead);
    }
}
