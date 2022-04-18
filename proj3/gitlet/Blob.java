package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.Stage.addBlob;
import static gitlet.Utils.readContentsAsString;
import static gitlet.Utils.sha1;

public class Blob implements Serializable {

    private final File _file;
    private final String _hash;
    private final String _fileName;

    public Blob(File file) {
        _file = file;
        _hash = sha1("blob" + readContentsAsString(file));
        _fileName = _file.getName();
    }

    public String getFileName() {
        return _fileName;
    }

    public String getHash() {
        return _hash;
    }
}
