package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import static gitlet.Main.BLOBS_FOLDER;
import static gitlet.Utils.readContents;
import static gitlet.Utils.readContentsAsString;
import static gitlet.Utils.readObject;
import static gitlet.Utils.sha1;
import static gitlet.Utils.writeObject;

/** Blob class that represents a file, including the file's name, the file's
 * contents, and the SHA-1 hash of the file.
 * @author Rae Xin
 * */
public class Blob implements Serializable {

    /** Represents the directions to the file. */
    private final File _file;

    /** The SHA-1 hash of the file. */
    private final String _hash;

    /** The name of the file. */
    private final String _fileName;

    /** The byte array of the file's contents. */
    private final byte[] _fileContents;

    /** Takes in a file, FILE, and sets instance variables. */
    public Blob(File file) {
        _file = file;
        _fileName = _file.getName();
        if (file.exists()) {
            _hash = getBlobHash(file);
            _fileContents = readContents(file);
        } else {
            _hash = null;
            _fileContents = null;
        }
    }

    /** Returns the SHA-1 hash of FILE. */
    public static String getBlobHash(File file) {
        return sha1("blob" + readContentsAsString(file));
    }

    /** Returns the filename of the current Blob. */
    public String getFileName() {
        return _fileName;
    }

    /** Returns the SHA-1 hash of the current Blob. */
    public String getHash() {
        return _hash;
    }

    /** Returns the byte array of the current Blob's contents. */
    public byte[] getFileContents() {
        return _fileContents;
    }

    /** Writes the blob, BLOB, into its corresponding file. Creates a new
     * file for the blob if one does not already exist. */
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

    /** Retrieves a Blob object with specified SHA-1 HASH. */
    public static Blob importBlob(String hash) {
        File blobFile = new File(BLOBS_FOLDER, hash);
        return readObject(blobFile, Blob.class);
    }
}
