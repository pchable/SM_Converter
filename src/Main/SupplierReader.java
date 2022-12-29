package Main;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.Properties;

public class SupplierReader
{
    Properties _Properties;

    public SupplierReader( )
    {
        try
        {
            _Properties = new Properties();
            FileInputStream ip = new FileInputStream("data/SupplierReader.config");
            _Properties.load(ip);
        }
        catch( Exception pException )
        {
            System.out.println("Failed reading supplier config file" );
        }
    }

    public void read()
    {
        int count = 0;
        String line;
        boolean doRead = true;

        //
        // ---- Get Properties
        //
        int maxRead = Integer.parseInt( _Properties.getProperty("MaxRead"));
        String sourceFile = _Properties.getProperty("Source");

        try
        {

            BufferedReader br = new BufferedReader(new FileReader( sourceFile ) );
            while( doRead )
            {
                line = br.readLine();

                if( line != null )
                {
                    processLine( line );
                    count++;
                }

                if( ( line == null)  || ( count > maxRead ) )
                {
                    doRead = false;
                }
            }
        }
        catch( Exception pException )
        {
            System.out.println("Error reading file: " + sourceFile );
            System.out.println("Error " + pException.getMessage() );
        }
    }

    protected void processLine(@NotNull String pLine )
    {
        //
        // ---- Get Properties
        //
        String columnSplitter = _Properties.getProperty( "ColumnSplitter");

        String columns[] = pLine.split( columnSplitter );
        for( String header : columns )
        {
            System.out.println("header: " + header );
        }

    }

}
