package Launch;

public class Main
{
        public static void main(String[] args)
        {
            String sourceFile = "Not Found";

            System.out.println("Bienvenue dans le convertisseur" );

            for( int index = 0; index < args.length; index++  )
            {
                String parameter = args[index];

                switch( parameter ) {
                    case "-Source":
                        sourceFile = args[index + 1];
                        break;
                }
            }

            System.out.println("Source: " + sourceFile );
        }

}
