package token;

public class Token {

    private TokenType idToken;
    private int riga;
    private String var;

    public Token(TokenType idToken, int riga, String var) {
        super();
        this.idToken = idToken;
        this.riga = riga;
        this.var = var;
    }

    public Token(TokenType tokenType, int riga) {
        this(tokenType, riga, null);
    }

    public String toString() {
        return "< TokenType = " + idToken + ",riga = " + riga;
    }

}
