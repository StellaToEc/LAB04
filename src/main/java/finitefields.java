import java.util.Scanner;

/**
 * Cryptography. (3CM15)
 * Lab4. Finite Fields
 * @professor Sandra Díaz Santiago
 * @author Castillo Rodríguez David Israel
 * @author Madrial Buendía David
 * @author Toledo Espinosa Cristina Aline
 * @date October 2021
 */

public class finitefields{
    static String mx = "1000011";

    static double findNum(String str){

        double num=0;

        for(int i= str.length(); i>0 ; i--)
            if(str.charAt(i-1)=='1')
                num += Math.pow(2,str.length()-i);

        return num;
    }

    static String multi(String mx, String fx, String gx){
        int n = mx.length()-1;
        int m = fx.length()-1;
        int k = gx.length()-1;

        int r,mult;

        if( gx.charAt(k)== '1')
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
     * This method takes two polynomials expressed as a binary string
     * (i.e. 101010, 000010) and it returns an integer which is equal
     * to the result of the multiplication of the two polynomials
     * expressed as an integer value
     * @param fx
     * @param gx
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
            result = singlePolynomialMultiplication(gxNum, mxDegree);
            return result;
        }
        /** g(x) equals zero, it is calculated f(x) mod m(x) */
        else if((gxNum & maskGx) == 1)
        {
            result = singlePolynomialMultiplication(fxNum, mxDegree);
            return result;
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
    }

    /**
     * Implementation of the reviewed algorithm during the class time
     * If the degree of the polynomial is less than (n - 1) an array shift
     * is made, else an array shift is made and a XOR operation are made
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
            return (polynomial << 1) ^ (Integer.parseInt(mx, 2) & maskMx);
        }

        return 0;
    }

    /**
     * According to the degree 'n' of the irreducible polynomial both of the rows
     * and columns are calculated and the table is made
     * @param n
     * @return
     */
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

    /**
     * By using a previously calculated table and the degree 'n'
     * of the irreducible polynomial, the multiplicative inverse
     * for each element of the table is calculated by using a previous
     * function 'polynomialMultiplication'
     * @param n
     * @param multTable
     * @return
     */
    static int[][] multiplicativeInverse(int n, int[][] multTable)
    {
        int columns = (int)(Math.pow(2, n) - 1) & 15;
        int rows = ((int)(Math.pow(2, n) - 1) & 240) >> 4;
        int [][]table  = new int[rows + 1][columns + 1];
        System.out.println("Cols: " + Integer.toHexString(columns));
        System.out.println("Rows: " + Integer.toHexString(rows));

        int aux, result;
        for(int row = 0; row <= rows; row++)
        {
            for(int col = 0; col <= columns; col++)
            {
                aux = multTable[row][col];
                for(int innerRow = 0; innerRow <= rows; innerRow++)
                {
                    for(int innerCol = 0; innerCol <= columns; innerCol++)
                    {
                        result = polynomialMultiplication(Integer.toBinaryString(aux), Integer.toBinaryString(multTable[innerRow][innerCol]));
                        if(result == 1)
                        {
                            table[row][col] = multTable[innerRow][innerCol];
                            break;
                        }
                    }
                }
            }
        }

        for(int row = 0; row <= rows; row++)
        {
            for(int col = 0; col <= columns; col++)
            {
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
        String mx,gx,fx;

        Scanner reader = new Scanner(System.in);

        System.out.println("Please give de m(x)");
        mx = reader.nextLine();
        System.out.println("Please give the first polynomial f(x)");
        fx = reader.nextLine();
        System.out.println("Please give the second polynomial g(x)");
        gx = reader.nextLine();

        System.out.println("RESULTADO:"+multi(mx,fx,gx));

        int result = polynomialMultiplication(fx, gx);

        System.out.println("The result expressed as a number: " + result);
        System.out.println("The result expressed as a binary string: " + Integer.toBinaryString(result));

        int [][]table = findTable(6);
        int [][]inverse = multiplicativeInverse(6, table);
    }
}

