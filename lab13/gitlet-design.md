# Gitlet Design Document
author: Rae Xin

## Design Document Guidelines

Please use the following format for your Gitlet design document. Your design
document should be written in markdown, a language that allows you to nicely 
format and style a text file. Organize your design document in a way that 
will make it easy for you or a course-staff member to read.  

## 1. Classes and Data Structures

Include here any class definitions. For each class list the instance
variables and static variables (if any). Include a ***brief description***
of each variable and its purpose in the class. Your explanations in
this section should be as concise as possible. Leave the full
explanation to the following sections. You may cut this section short
if you find your document is too wordy.

### Main.java
This class handles commands that are inputted into the program. It also
keeps track of information relating to the current repository for easy
access by the rest of the program. It contains a main method that will call
relevant methods to execute given commands.

#### Fields
1. static final COMMITS_DIR: pointer to `.gitlet/commits` folder that
stores information about all commits.
2. static final BLOBS_DIR: pointer to `.gitlet/commits/blobs` folder
that stores all blobs.
3. static final HEAD_FILE: pointer to current head commit, stored
in `.gitlet/head.txt`
4. static final BRANCHES_FILE: pointer to file that contains data
structure for branch storage. Stored at `.gitlet/branches.txt`

### Commit.java
This class implements Serializable and represents a commit. The data in each
commit is stored in a HashMap between two Strings. The first three key-value
mappings are between `_timestamp` and the commit's timestamp, `_message` and
the commit's message, and `_parent1` or `_parent2` and the commit's parent hash(es). The rest
of the key-value pairs are between the filenames and the SHA-1 hashes of each 
blob in the commit. Each commit is serialized and saved to `COMMITS_DIR` under
a file named with the Commit object's SHA-1 hash.

#### Fields
1. commit: HashMap as described above.
2. final timeStamp: the date and time that the Commit was made at.
3. final message: String value of message passed in.
4. final parent1: SHA-1 hash of parent Commit (always present).
5. final parent2: SHA-1 hash of second parent (only if merge)

### Stage.java
This class represents two kinds of stages: an add-stage, wherein 
Blob objects are staged to be added to a commit, and a remove-stage,
wherein Blob objects are staged to be removed from a commit. We store
Blobs in a HashMap of String to String; filename to SHA-1 hash.

#### Fields
1. removeStage: HashMap from String to String representing Blobs to be
removed.
2. addStage: same as above but for Blobs to be added to the next Commit.

### Blob.java
This class implements Comparable. It represents a blob and will determine
whether two Blobs are equivalent (referring to same state of same file).

#### Fields
1. final BLOB_FILE: 

## 2. Algorithms

This is where you tell us how your code works. For each class, include
a high-level description of the methods in that class. That is, do not
include a line-by-line breakdown of your code, but something you would
write in a javadoc comment above a method, ***including any edge cases
you are accounting for***. We have read the project spec too, so make
sure you do not repeat or rephrase what is stated there.  This should
be a description of how your code accomplishes what is stated in the
spec.


The length of this section depends on the complexity of the task and
the complexity of your design. However, simple explanations are
preferred. Here are some formatting tips:

* For complex tasks, like determining merge conflicts, we recommend
  that you split the task into parts. Describe your algorithm for each
  part in a separate section. Start with the simplest component and
  build up your design, one piece at a time. For example, your
  algorithms section for Merge Conflicts could have sections for:

   * Checking if a merge is necessary.
   * Determining which files (if any) have a conflict.
   * Representing the conflict in the file.
  
* Try to clearly mark titles or names of classes with white space or
  some other symbols.

## 3. Persistence

Describe your strategy for ensuring that you don’t lose the state of your program
across multiple runs. Here are some tips for writing this section:

* This section should be structured as a list of all the times you
  will need to record the state of the program or files. For each
  case, you must prove that your design ensures correct behavior. For
  example, explain how you intend to make sure that after we call
       `java gitlet.Main add wug.txt`,
  on the next execution of
       `java gitlet.Main commit -m “modify wug.txt”`, 
  the correct commit will be made.
  
* A good strategy for reasoning about persistence is to identify which
  pieces of data are needed across multiple calls to Gitlet. Then,
  prove that the data remains consistent for all future calls.
  
* This section should also include a description of your .gitlet
  directory and any files or subdirectories you intend on including
  there.

## 4. Design Diagram

Attach a picture of your design diagram illustrating the structure of your
classes and data structures. The design diagram should make it easy to 
visualize the structure and workflow of your program.

