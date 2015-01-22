package com.makarov_denis_vertigo633;

import java.math.BigDecimal;

/**
 * Calculator for basic operations of addition, subtraction, multiplication, division. Works with parentheses, unary
 * minus and numbers with floating point.
 *
 * Created by Vertigo633 on 19.12.2014.
 */

public class CalculatorImpl implements Calculator {

    // Setting token types as constants;
    final int NONE = 0; // Unrecognised lexem, that will be recognised later;
    final int DELIMETER = 1; // Any delimiter /,*,-,+,(,);
    final int NUMBER = 2; // Symbol is number
    final String EOE = "\0"; //End of expression

    private String exp; //expression that should be parsed;
    private String token; //holds current token
    private int expIndex; //current index in expression
    private int tokenType; //token type listed above;

    public CalculatorImpl(){

    }
    /**
     * Evaluate statement represented as string.
     *
     * @param statement mathematical statement containing digits, '.' (dot) as decimal mark,
     *                  parentheses, operations signs '+', '-', '*', '/'<br>
     *                  Example: <code>(1 + 38) * 4.5 - 1 / 2.</code>
     *
     * @return string value containing result of evaluation or null if statement is invalid
     */
    @Override
    public String evaluate(String statement) {
        String resultOutput="";
        double value;
        try {
            value = calculate(statement);

        } catch (IllegalArgumentException e) {
            return null;
        }

        //Rounding obtained result;
        BigDecimal roundedValue = new BigDecimal(value);
        resultOutput = roundedValue.setScale(4, BigDecimal.ROUND_HALF_EVEN).toString();

        return resultOutput;
    }
    /**
     * Method based on recursive-descent parser
     *
     * @param statement mathematical statement containing digits, '.' (dot) as decimal mark,
     *                  parentheses, operations signs '+', '-', '*', '/'<br>
     *                  Example: <code>(1 + 38) * 4.5 - 1 / 2.</code>
     *
     * @return double value containing result of evaluation
     * @throws java.lang.IllegalArgumentException if statement is not correct;
     */
    //
    private double calculate(String statement) throws IllegalArgumentException {
        double result;
        exp = statement;
        expIndex = 0;
        getToken();
        if(token.equals(EOE)){
            throw new IllegalArgumentException(); //No expression present
        }

        result = addOrSubtract();

        if(!token.equals(EOE)) // Last token should be EOE
            throw new IllegalArgumentException();
        return result;

    }

    /**
     * Calculates addition and subtraction
     * @return double value containing result of evaluation
     */


    private double addOrSubtract() {
        char operand;
        double result;
        double tempResult;
        result = divideOrMultiply();
        while((operand = token.charAt(0)) == '+' || operand == '-'){
            getToken();
            tempResult = divideOrMultiply();
            if(operand == '-'){
                result = result - tempResult;
            } else if (operand=='+'){
                result = result + tempResult;
            }
        }
        return result;
    }
    /**
     * Calculates multiplication division
     * @return double value containing result of evaluation
     */
    private double divideOrMultiply() {
        char operand;
        double result;
        double tempResult;
        result = unarySign();
        while((operand = token.charAt(0)) == '*' || operand == '/'){
            getToken();
            tempResult = unarySign();
            if(operand == '*'){
                result = result*tempResult;
            } else if (operand == '/'){
                if(tempResult == 0.0)
                    throw new IllegalArgumentException();

                result = result / tempResult;
                result = result / tempResult;
            }
        }
        return result;
    }
    /**
     * Parses unary sign
     * @return double value containing result of evaluation
     */
    private double unarySign() {
        String operand = "";
        double result;

        if((tokenType == DELIMETER) && token.equals("+") || token.equals("-")){
            operand = token;
            getToken();
        }
        result = evalBrackets();
        if(operand.equals("-")) result = -result;
        return result;
    }
    /**
     * Parses parentheses
     * @return double value containing result of evaluation
     */
    private double evalBrackets() {
        double result;
        if(token.equals("(")){
            getToken();
            result = addOrSubtract();
            if(!token.equals(")")) //Wrong quantity of opened and closed brackets.
                throw new IllegalArgumentException();

            getToken();
        }
        else result = valueOfNumber();
        return result;
    }
    /**
     * Evaluate the whole number
     * @return double value containing result of evaluation
     */
    private double valueOfNumber() {
        double result;
        if(tokenType == NUMBER){
            result = Double.parseDouble(token);
            getToken();
        } else throw new IllegalArgumentException();
        return result;
    }
    /**
     * Getting correct type of token
     *
     */
    private void getToken() {
        tokenType = NONE;
        token = "";
        if(expIndex == exp.length()){
            token = EOE;
            return;
        }
        while(expIndex < exp.length() &&
                Character.isWhitespace(exp.charAt(expIndex))) ++ expIndex;
        if(expIndex == exp.length()){
            token = EOE;
            return;
        }
        if(isDelim(exp.charAt(expIndex))){
            token += exp.charAt(expIndex);
            expIndex++;
            tokenType = DELIMETER;
        } else if(Character.isDigit(exp.charAt(expIndex))){
            while(!isDelim(exp.charAt(expIndex))){
                token += exp.charAt(expIndex);
                expIndex ++;
                if(expIndex>=exp.length()) break;
            }
            tokenType = NUMBER;
        } else {
            token = EOE;
            return;
        }
    }
    /**
     * Checks whether token is delimiter
     *
     */
    boolean isDelim(char ch){
        if((" +-/*=()".indexOf(ch)!= -1))
            return true;
        else return false;
    }

}
