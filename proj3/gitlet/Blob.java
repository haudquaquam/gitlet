package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.Utils.sha1;

public class Blob implements Serializable {

    private final File _file;
    private final String _hash;
    private final String _fileName;

    public Blob(File file) {
        _file = file;
        _hash = sha1("blob" + _file);
        _fileName = _file.getName();
    }
}
