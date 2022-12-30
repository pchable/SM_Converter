package Launch;

import Main.Supplier;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Main
{

        public static void main(String[] args)
        {
            String parameter;
            String supplierName = "None";

            DateTimeFormatter  dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
            LocalDateTime start =LocalDateTime.now();


            System.out.println("\nWelcome to StoreMoov supplier file converter" );
            System.out.println("============================================\n" );

            System.out.println("\tStarting at: " + dtf.format( start ));

            //
            // ---- Retrieve parameters
            //
            for( int index = 0; index < args.length; index++ )
            {
                parameter = args[ index ];

                switch( parameter )
                {
                    case "-Supplier":   supplierName = args[index+1];
                                        break;
                }
            }

            //
            // ---- Run Supplier analysis
            //
            if( supplierName.equals("None") )
            {
                System.out.println("Could not find supplier name. Please provide using '-Supplier XXXXX' syntax" );
            }
            else
            {
                Supplier supplier = new Supplier(supplierName);
                supplier.openUpIO();
                supplier.analyse();
                supplier.closeDownIO();
            }

            //
            // ---- And sign off
            //
            System.out.println("\nProcessing Completed  at " + dtf.format(LocalDateTime.now() ) + " !");
        }
}
