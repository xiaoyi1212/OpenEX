package ex.openex.compile.parser;

import ex.openex.compile.ast.AstLeaf;

import java.util.ArrayList;

public class CompileFile {
    String filename;
    public ArrayList<String> value_names;
    public ArrayList<String> all_value_names;

    public ArrayList<String> list_names;
    public ArrayList<String> all_list_names;

    ArrayList<AstLeaf> trees;
    public CompileFile(String filename){
        this.filename = filename;
        value_names = new ArrayList<>();
        all_value_names = new ArrayList<>();
        list_names = new ArrayList<>();
        all_list_names = new ArrayList<>();
        trees = new ArrayList<>();
    }

    public ArrayList<AstLeaf> children() {
        return trees;
    }

    public String getFilename() {
        return filename;
    }
}
