package scanner;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import token.*;

public class Scanner {
    final char EOF = (char) -1;
    final char EOL = '\n';
    private int riga;
    private PushbackReader buffer;

    // skpChars: insieme caratteri di skip (include EOF)
    ArrayList<Character> skpChar = new ArrayList<Character>(List.of(' ', EOF, EOL, '\t', '\r'));

    // letters: insieme delle lettere ammesse
    ArrayList<Character> letters = new ArrayList<Character>();

    // digits: insieme delle cifre
    ArrayList<Integer> digits = new ArrayList<Integer>();

    // delOpTkType: mapping fra caratteri '+', '-', '*', '/', '=', ';' e il
    // TokenType
    // corrispondente
    HashMap<Character, TokenType> delOpTkType = new HashMap<Character, TokenType>();

    // keyWordsTkType: mapping fra le stringhe "print", "float", "int" e il
    // TokenType
    // corrispondente
    HashMap<String, TokenType> keyWordsTkType = new HashMap<String, TokenType>();

    public Scanner(String fileName) throws FileNotFoundException {

        this.buffer = new PushbackReader(new FileReader(fileName));
        riga = 1;

        // inizializzare campi che non hanno inizializzazione
        for (char c = 'a'; c <= 'z'; c++) {
            letters.add(c);
        }

        for (int i = 0; i <= 9; i++) {
            digits.add(i);
        }

        delOpTkType.put('+', TokenType.SUM);
        delOpTkType.put('-', TokenType.SUB);
        delOpTkType.put('/', TokenType.DIV);
        delOpTkType.put('*', TokenType.TIMES);
        delOpTkType.put('=', TokenType.ASS);
        delOpTkType.put(';', TokenType.SEMI);

        keyWordsTkType.put("int", TokenType.TYINT);
        keyWordsTkType.put("float", TokenType.TYFLOAT);
        keyWordsTkType.put("print", TokenType.PRINT);
    }

    // nextToken ritorna il prossimo token nel file di input e legge
    // i caratteri del token ritornato (avanzando fino al carattere
    // successivo all'ultimo carattere del token)
    public Token nextToken() throws LexicalException {

        riga = 0;

        // nextChar contiene il prossimo carattere dell'input (non consumato).
        try {
            while (true) {
                char nextChar = peekChar(); // Catturate l'eccezione IOException e

                while (skpChar.contains(nextChar)) {
                    if (nextChar == EOF) {
                        return new Token(TokenType.EOF, riga);
                    } else if (nextChar == EOL) {
                        riga++;
                    }
                    readChar();
                    continue;
                }

                if (nextChar == '#') {
                    skipComment();
                    continue;
                }

                if (delOpTkType.containsKey(nextChar)) {
                    scanDelOp();
                    continue;
                }

                if (letters.contains(nextChar)) {
                    scanId();
                    continue;
                }
            }
        } catch (IOException e) {
            throw new LexicalException(e.getMessage());
        }
        // ritornate una LexicalException che la contiene

        // Avanza nel buffer leggendo i carattere in skipChars (stato 0)
        // incrementando riga se leggi EOL.
        // Se raggiungi la fine del file ritorna il Token EOF => (stato 1)

        // Se nextChar e' # => (stato 2)
        // Avanza nel buffer leggendo tutti i caratteri eccetto '\n' o il fine file
        // (stato 2)
        // Se raggiungi la fine del file ritorna il Token EOF => (stato 1)
        // Se il prossimo carattere e' EOL return nextToken() => (stato 0)

        // Se nextChar e' un delimitatore o un operatore
        // return scanDelOp() (attenzione agli operatori di assegnamento)

        // Se nextChar e' in letters
        // return scanId()
        // che deve generare o un Token ID o parola chiave

        // Se nextChar e' in numbers
        // return scanNumber()
        // che legge sia un intero che un float e ritorna il Token INUM o FNUM
        // i caratteri che leggete devono essere accumulati in una stringa
        // che verra' assegnata al campo valore del Token

        // Altrimenti il carattere NON E' UN CARATTERE LEGALE sollevate una
        // eccezione lessicale dicendo la riga e il carattere che la hanno
        // provocata.
    }

    private Token scanId() {

    }

    private Token scanDelOp() throws IOException {
        char c = readChar();
        if ((c == '+' || c == '-' || c == '/' || c == '*') && peekChar() == '=') {
            return new Token(TokenType.OP_ASS, riga);
        }
        return new Token(TokenType.ASS, riga);
    }

    private void skipComment() throws IOException {
        readChar();
        while (peekChar() != EOF && peekChar() != EOL) {
            readChar();
        }
        if (peekChar() == EOL) {
            readChar();
            riga++;
        }
    }

    // i metodi privati scanXXX

    private char readChar() throws IOException {
        return ((char) this.buffer.read());
    }

    private char peekChar() throws IOException {
        char c = (char) buffer.read();
        buffer.unread(c);
        return c;
    }
}
