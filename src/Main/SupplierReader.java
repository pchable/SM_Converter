package Main;

import java.io.BufferedReader;
import java.io.FileReader;

public class SupplierReader
{
    String _FileName;
    String _Line = "";
    String _SplitBy = ",";
    int _MaxRead = 10;

    public SupplierReader( String pFileName )
    {
        _FileName = pFileName;
    }

    public void read( String pFileName )
    {
        int count = 0;
        try
        {

            BufferedReader br = new BufferedReader(new FileReader( _FileName ));
            while( count < _MaxRead )
            {
                _Line = br.readLine();
                System.out.println("Line: " + _Line);
                count++;
            }
        }
        catch( Exception pException )
        {
            System.out.println("Error reading file: " + _FileName );
            System.out.println("Error " + pException.getMessage() );
        }
    }



}
