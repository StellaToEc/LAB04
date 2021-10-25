/*
 * Lab 4: Finite Fields
 * Implement of irreducible polynomial
 * Implement multiplicative polinomial (using bits)
 */
import java.util.Scanner;

/**
 *
 * @author Cristina Aline Toledo Espinosa
 * @author David Madrigal Buendía
 * @author David Israel Castillo Rodriguez
 */
public class finitefields{
    // mx is our irreducible polynomial in binary
    static String mx = "1000011";

    /**
     * This method converts a string binary to number
     * @param str Is a string with binary data
     * @return The equivalent in decimal format
     */
    static double findNum(String str){
        double num=0;

        for(int i= str.length(); i>0 ; i--)
            if(str.charAt(i-1)=='1')
                num += Math.pow(2,str.length()-i);
        return num;
    }

    static String multi(String mx, String fx, String gx){
        int n = mx.length()-1; // n, mx's bits - GF
        int m = fx.length()-1; // m, fx's bits
        int k = gx.length()-1; // k, gx's bits

        int r, mult;

        if(gx.charAt(k)== '1')
            r = (int)findNum(fx);
        else
            r = 0x00;

        for(int i=0; i < k ; i++) {
            if (gx.charAt(i) == '1') {
                mult = (int)findNum(fx);
                for (int j = 0; j < k-i; j++) {
                    if (m + j +1< n)
                        mult = mult << 1;
                    else
                        mult = (mult << 1) ^ (int)findNum(mx);
                }
                r = (r ^ mult);
            }
        }
        return Integer.toHexString(r);
    }

    /**
     * This method multiply two functions in GF(2^n) (Galois Field)
     * @param fx Is the first function in string with binary data
     * @param gx Is the second function in string with binary data
     * @return 
     */
    public static int polynomialMultiplication(String fx, String gx)
    {
        int maskFx = (int)Math.pow(2, fx.length()) - 1;
        int maskGx = (int)Math.pow(2, gx.length()) - 1;
        int fxNum = Integer.parseInt(fx, 2);
        int gxNum = Integer.parseInt(gx, 2);
        int mxDegree = mx.length() - 1;
        int result;

        if(fx.length() > mx.length() || gx.length() > mx.length())
        {
            System.out.println("There has been an error, one of the polynomials degree is greater");
            return 0;
        }

        /** f(x) equals zero, it is calculated g(x) mod m(x) */
        if((fxNum & maskFx) == 1)
        {
            System.out.println("The polynomial f(x) is equal one");
            result = singlePolynomialMultiplication(gxNum, mxDegree);
            return result;
        }
        /** g(x) equals zero, it is calculated f(x) mod m(x) */
        else if((gxNum & maskGx) == 1)
        {
            System.out.println("The polynomial g(x) is equal one");
            result = singlePolynomialMultiplication(fxNum, mxDegree);
            return result;
            /*
            System.out.println("The result expressed as a number: " + result);
            System.out.println("The result expressed as a binary string: " + Integer.toBinaryString(result));
            */
        }

        int aux = 0, position;
        result = 0;
        for(int i = mxDegree - 1; i >= 0; i--)
        {
            position =  1 << i;
            if(gxNum >= position)
            {
                gxNum = gxNum - position;
                aux = fxNum;

                for(int j = i; j > 0; j--)
                    aux = singlePolynomialMultiplication(aux, mxDegree);
                result = result ^ aux;
            }
        }

        return result;
        /*
        System.out.println("The result expressed as a number: " + result);
        System.out.println("The result expressed as a binary string: " + Integer.toBinaryString(result));
        */
    }

    /**
     * n extracted from GF(2^n)
     * @param polynomial
     * @param n
     * @return
     */
    public static int singlePolynomialMultiplication(int polynomial, int n)
    {
        int maxDegree = 1 << n - 1;
        if(polynomial < maxDegree)
            return (polynomial << 1);

        else if(polynomial >= maxDegree)
        {
            /**
             * Since the degree of the polynomial is greater than the maximum degree
             * of GF it is substracted the MSB on the polynomial
             */
            polynomial = polynomial - maxDegree;
            /**
             * Since the irreducible polynomial considered is x^6 + x + 1
             * so the remainder will be (x + 1) <=> 3
             */
            int maskMx = (int) Math.pow(2, mx.length() - 1) - 1;
            System.out.println("Remainder of m(x): " + (Integer.parseInt(mx, 2) & maskMx));
            return (polynomial << 1) ^ (Integer.parseInt(mx, 2) & maskMx);
        }

        return 0;
    }

    static int[][] findTable(int n)
    {
        /**
         * MASKS
         * 15 <=> 0000 1111
         * 240 <=> 1111 0000
         */
        int columns = (int)(Math.pow(2, n) - 1) & 15;
        int rows = ((int)(Math.pow(2, n) - 1) & 240) >> 4;
        int [][]table  = new int[rows + 1][columns + 1];
        System.out.println("Cols: " + Integer.toHexString(columns));
        System.out.println("Rows: " + Integer.toHexString(rows));

        for(int row = 0; row <= rows; row++)
        {
            for(int col = 0; col <= columns; col++)
            {
                table[row][col] = col | (row << 4);
                System.out.print(Integer.toHexString(table[row][col]) + "\t");
            }
            System.out.println();
        }

        return table;
    }
    /*public void table(){

    }

    public void searchInv(){

    }*/

    public static void main (String[]args){
        String mx, gx, fx;

        Scanner reader = new Scanner(System.in);

        System.out.println("Please give de m(x) in binary format: ");
        mx = reader.nextLine();
        System.out.println("Please give the first polynomial f(x) in binary format: ");
        fx = reader.nextLine();
        System.out.println("Please give the second polynomial g(x) in binary format: ");
        gx = reader.nextLine();

        System.out.println("Result: " + multi(mx,fx,gx));

        int result = polynomialMultiplication(fx, gx);

        System.out.println("The result expressed as a number: " + result);
        System.out.println("The result expressed as a binary string: " + Integer.toBinaryString(result));
    }
}
