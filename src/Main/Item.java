package Main;

import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

public class Item
{
    protected HashMap<String, String> _Attributes;
    protected String _Tab = "\t";

    /** ******************************************************************************
     *
     *
     *  Constructor
     *
     * ******************************************************************************
    */
    public Item()
    {
        _Attributes = new HashMap<String, String>();
    }

    /** ******************************************************************************
     *
     *
     *  JSON Writer
     *
     * ******************************************************************************
     */
    public void write( FileWriter pOutput )
    {
        try {
            pOutput.write( _Tab + "{\n");
            writeAttributes(pOutput);
            pOutput.write("\n" + _Tab + "}");
        }
        catch( Exception pException )
        {
            System.out.println("Could not write item");
        }
    }

    public void writeAttributes( FileWriter pOutput )
    {
        boolean notFirst = false;

        for( Map.Entry<String, String> set : _Attributes.entrySet() )
        {
            String key = set.getKey();
            String value = set.getValue();

            try
            {
                if( notFirst )
                {
                    pOutput.write(",\n");
                }
                pOutput.write( _Tab + _Tab + "\""+ key + "\":" + "\""+ value + "\"" );
                notFirst = true;
            }
            catch( Exception pException )
            {
                System.out.println("Could not write attribute: " + key );
            }

        }
    }




}
