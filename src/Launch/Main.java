package Launch;

import Main.SupplierReader;


public class Main
{

        public static void main(String[] args)
        {
            System.out.println("Bienvenue dans le convertisseur" );
            SupplierReader reader = new SupplierReader();
            reader.read();

        }
}
