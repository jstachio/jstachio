package com.snaphop.staticmustache.apt;

import java.util.ArrayDeque;

import org.eclipse.jdt.annotation.Nullable;

import com.github.sviperll.staticmustache.TemplateCompilerFlags;
import com.snaphop.staticmustache.apt.MustacheToken.NewlineChar;
import com.snaphop.staticmustache.apt.MustacheToken.SpecialChar;
import com.snaphop.staticmustache.apt.MustacheToken.TextToken;

public abstract class AbstractTemplateCompiler implements TemplateCompilerLike, TokenProcessor<PositionedToken<MustacheToken>> {
    
    private ArrayDeque<PositionedToken<MustacheToken>> previousTokens = new ArrayDeque<>(5);
    protected boolean atStartOfLine = true;
    protected @Nullable PositionedToken<MustacheToken> lastProcessedToken = null;
    protected Position position = null;
    protected String partialIndent = "";
    

    @Override
    public void processToken(PositionedToken<MustacheToken> positionedToken) throws ProcessingException {
        previousTokens.offer(positionedToken);
        processTokens();
    }
    
    protected void debug(CharSequence message) {
        if (isDebug()) {
            System.out.println("[MUSTACHE] " + getTemplateName() + ": " + message);
        }
    }
    
    protected boolean isDebug() {
        return flags().contains(TemplateCompilerFlags.Flag.DEBUG);
    }
    
    private void processTokens() throws ProcessingException {
        
        
        boolean eof = previousTokens.stream().filter(t -> t.innerToken().isEOF()).findFirst().isPresent();
        if (eof) {
            if (! previousTokens.getLast().innerToken().isEOF()) {
                throw new IllegalStateException(previousTokens.toString());
            }
        }
        
        /*
         * For standalone tag line support we need to see if blank space
         * is around the tag.
         * 
         * That is four tokens max:
         * 
         * [ space* ] {{#some section}} [ space* ] [ newline ]
         * 
         * Beginning of the file case:
         * {{#some section}} [ space* ] [ newline ]
         */
        
        ArrayDeque<PositionedToken<MustacheToken>> buf = new ArrayDeque<>();

        do {
            
            int size = previousTokens.size();
            
            if (size == 1 && eof) {
                _processToken(previousTokens.poll());
                return;
            }
            
            if (size < 2 && ! eof) {
                return; // we need more tokens
            }
            
            buf.clear();
            var firstToken = previousTokens.poll();
            var secondToken = previousTokens.poll();
            buf.offerLast(firstToken);
            buf.offerLast(secondToken);
            
            /*
             * Handle the easiest negative case
             * [ not new line or space ] {{#somesection}}
             */
            if (atStartOfLine && ! (firstToken.innerToken().isNewlineToken() || firstToken.innerToken().isWhitespaceToken()) 
                    && secondToken.innerToken().isStandaloneToken()) {
                _processToken(firstToken);
                _processToken(secondToken);
                atStartOfLine = false;
                continue;
            }
            /*
             * {{#some section}} [ newline ]
             */
            if (atStartOfLine && firstToken.innerToken().isStandaloneToken() && secondToken.innerToken().isNewlineOrEOF()) {
                debug("2 standalone condition: {{#some section}} [ newline ]");
                _processToken(firstToken);
                if (secondToken.innerToken().isEOF()) {
                    _processToken(secondToken);
                }
                atStartOfLine = true;
                continue;
            }

            if (atStartOfLine && size >= 3) {
                var thirdToken = previousTokens.poll();
                buf.add(thirdToken);

                /*
                 * {{#some section}} [white space] [ newline ]
                 */
                if (firstToken.innerToken().isStandaloneToken() //
                        && secondToken.innerToken().isWhitespaceToken() //
                        && thirdToken.innerToken().isNewlineOrEOF()) {
                    debug("3 standalone condition: {{#some section}} [white space] [ newline ]");
                    _processToken(firstToken);
                    if (thirdToken.innerToken().isEOF()) {
                        _processToken(thirdToken);
                    }
                    atStartOfLine = true;
                    continue;
                }

                /*
                 * [white space] {{#some section}} [ newline ]
                 */
                if (firstToken.innerToken().isWhitespaceToken() //
                        && secondToken.innerToken().isStandaloneToken() //
                        && thirdToken.innerToken().isNewlineOrEOF()) {
                    debug("3 standalone condition: [white space] {{#some section}} [ newline ]");
                    _processIndentToken(firstToken, secondToken);
                    if (thirdToken.innerToken().isEOF()) {
                        _processToken(thirdToken);
                    }
                    atStartOfLine = true;
                    continue;
                }

                if (size >= 4) {
                    var fourthToken = previousTokens.poll();
                    buf.add(fourthToken);

                    /*
                     * [white space] {{#some section}} [white space] [ newline ]
                     */
                    if (firstToken.innerToken().isWhitespaceToken() //
                            && secondToken.innerToken().isStandaloneToken() //
                            && thirdToken.innerToken().isWhitespaceToken() //
                            && fourthToken.innerToken().isNewlineOrEOF()) {
                        debug("4 standalone condition: [white space] {{#some section}} [white space] [ newline ]");
                        _processIndentToken(firstToken, secondToken);
                        if (fourthToken.innerToken().isEOF()) {
                            _processToken(fourthToken);
                        }
                        atStartOfLine = true;
                        continue;
                    }
                }
            }
            // We have to put the tokens back into the queue
            buf.descendingIterator().forEachRemaining(previousTokens::offerFirst);

            //exitEarly = false;
            
            if (eof && ! previousTokens.isEmpty()) {
                _processToken(previousTokens.poll());
            }
            else if (previousTokens.size() > 5) {
                if (isDebug()) {
                    debug("More than 5 tokens");
                }
                _processToken(previousTokens.poll());
            }
            
        } while(eof && !previousTokens.isEmpty());
    }
    
    void _processToken(PositionedToken<MustacheToken> positionedToken) throws ProcessingException {
        this.position = positionedToken.position();
        positionedToken.innerToken().accept(new CompilingTokenProcessor(this));
        if (positionedToken.innerToken().isNewlineOrEOF()) {
            atStartOfLine = true;
        }
        else {
            atStartOfLine = false;
        }
        lastProcessedToken = positionedToken;
    }
    
    void _processIndentToken(
            PositionedToken<MustacheToken> whitespace, 
            PositionedToken<MustacheToken> positionedToken) throws ProcessingException {
        if(positionedToken.innerToken().isIndented()) {
            if (whitespace.innerToken() instanceof TextToken tt) {
                if (isDebug()) {
                    debug("Setting indent. whitespace: " + tt + " standalone: " + positionedToken.innerToken());
                }
                partialIndent = tt.text();
            }
            else {
                throw new IllegalStateException("whitespace token is wrong");
            }
        }
        _processToken(positionedToken);
    }

    protected abstract void _endOfFile() throws ProcessingException;

    protected abstract void _text(String s) throws ProcessingException;

    protected abstract void _newline(NewlineChar c) throws ProcessingException;

    protected abstract void _specialCharacter(SpecialChar specialChar) throws ProcessingException;

    protected abstract void _unescapedVariable(String name) throws ProcessingException;

    protected abstract void _partial(String name) throws ProcessingException;

    protected abstract void _variable(String name) throws ProcessingException;

    protected abstract void _endSection(String name) throws ProcessingException;

    protected abstract void _beginBlockSection(String name) throws ProcessingException;

    protected abstract void _beginParentSection(String name) throws ProcessingException;

    protected abstract void _beginInvertedSection(String name) throws ProcessingException;

    protected abstract void _beginSection(String name) throws ProcessingException;


}
