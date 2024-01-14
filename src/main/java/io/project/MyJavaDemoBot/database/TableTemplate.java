package io.project.MyJavaDemoBot.database;

import java.util.ArrayList;

public interface TableTemplate {
    public ArrayList<String[]> select(String[] selectingColumns, String[] targetColumns, String[] targetValues);

    public void insert(String[] columns, String[] values);

    public void delete(String[] columnsToDelete, String[] targetColumns, String[] targetValues);

    public void executeQuery(String query);

    public void update(String[] columnsToUpdate, String[] newValues, String[] targetColumns, String[] targetValues);

}
