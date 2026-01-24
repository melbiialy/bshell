package commandexecution.dto;

public class Token {
    private String token;
    private boolean isQuoted;
    private boolean isDoubleQuoted;


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isQuoted() {
        return isQuoted;
    }

    public void setQuoted(boolean quoted) {
        isQuoted = quoted;
    }

    public boolean isDoubleQuoted() {
        return isDoubleQuoted;
    }

    public void setDoubleQuoted(boolean doubleQuoted) {
        isDoubleQuoted = doubleQuoted;
    }
}
