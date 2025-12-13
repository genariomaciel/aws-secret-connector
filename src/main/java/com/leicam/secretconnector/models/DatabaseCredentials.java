package com.leicam.secretconnector.models;

/**
 * Exemplo de classe para representar credenciais em formato JSON.
 */
public class DatabaseCredentials {
    private String appname;
    private String host;
    private String user;
    private String pass;
    private String dialect;

    // Construtores
    public DatabaseCredentials() {}

    public DatabaseCredentials(String appname, String host, int port, String user, String pass, String dialect) {
        this.appname = appname;
        this.host = host;
        this.user = user;
        this.pass = pass;
        this.dialect = dialect;
    }

    // Getters e Setters
    public String getAppname() {
        return host;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }
    
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

        public String getDialect() {
        return dialect;
    }

    public void setDialect(String dialect) {
        this.dialect = dialect;
    }

    @Override
    public String toString() {
        return String.format("DatabaseCredentials{appname='%s', host='%s', user='%s', pass='%s', dialect='%s'}", 
                appname, host, user, pass, dialect);
    }

}
