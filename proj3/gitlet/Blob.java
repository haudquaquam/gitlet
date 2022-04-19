package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import static gitlet.Main.BLOBS_FOLDER;
import static gitlet.Stage.addBlob;
import static gitlet.Utils.*;

public class Blob implements Serializable {

    private final File _file;
    private final String _hash;
    private final String _fileName;
    private final byte[] _fileContents;

    public Blob(File file) {
        _file = file;
        _hash = sha1("blob" + readContentsAsString(file));
        _fileName = _file.getName();
        _fileContents = readContents(file);
    }

    public String getFileName() {
        return _fileName;
    }

    public String getHash() {
        return _hash;
    }

    public byte[] getFileContents() {
        return _fileContents;
    }

    public static void exportBlob(Blob blob) {
        String fileName = blob.getHash();
        File newBlobFile = new File(BLOBS_FOLDER, fileName);
        if (!newBlobFile.exists()) {
            try {
                newBlobFile.createNewFile();
                writeObject(newBlobFile, blob);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
