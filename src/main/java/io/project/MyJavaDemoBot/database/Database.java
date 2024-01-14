package io.project.MyJavaDemoBot.database;

import org.springframework.beans.factory.annotation.Value;

public class Database {


    String DB_NAME;

    String DB_HOST;

    String DB_PORT;

    String DB_USER;

    String DB_PASSWORD;

    public Database(){
    }


    public Database(String DB_NAME, String DB_HOST, String DB_PORT, String DB_USER, String DB_PASSWORD) {
        this.DB_NAME = DB_NAME;
        this.DB_HOST = DB_HOST;
        this.DB_PORT = DB_PORT;
        this.DB_USER = DB_USER;
        this.DB_PASSWORD = DB_PASSWORD;
    }
}
