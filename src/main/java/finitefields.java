import java.util.Scanner;

public class finitefields{

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
    }
}

