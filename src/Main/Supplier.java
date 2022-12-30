package Main;


import java.io.*;
import java.nio.Buffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class Supplier
{
    Properties _Properties; // Information from the property file
    String[] _Headers = null;      // Supplier header columns

    FileWriter _Output = null;
    BufferedReader _Input = null;

    /** ==============================================
     *
     *
     *  Constructor
     *
     * ===============================================
     */
    public Supplier( String pSupplierName )
    {
        String configFileName;

        //
        // ---- Get Properties
        //
        try
        {
            configFileName = "data/"+pSupplierName+".config";
            System.out.println("\tReading configuration file: " + configFileName );

            _Properties = new Properties();
            FileInputStream ip = new FileInputStream( configFileName);
            _Properties.load(ip);
        }
        catch( Exception pException )
        {
            System.out.println("Failed reading supplier config file" );
        }
        System.out.println("\nRead configuration for: " + _Properties.getProperty("Name"));
    }

    /** ==============================================
     *
     *
     *  Open Up IO
     *
     * ===============================================
     */
    public void openUpIO()
    {
        try
        {

            // ---- Open Input
            _Input = new BufferedReader(new FileReader(_Properties.getProperty("Source"), Charset.forName("UTF-8")));

            // ---- Open Ouput & init file
            _Output = new FileWriter(_Properties.getProperty("Target"), Charset.forName("UTF-8"));
            _Output.write("{\n");
            _Output.write("   \"Items\": \n   [\n");
        }
        catch( Exception pException )
        {
            System.out.println("Failed on I/O: " + pException.getMessage() );
        }
    }

    /** ==============================================
     *
     *
     *  Open Up IO
     *
     * ===============================================
     */
    public void closeDownIO()
    {
        try
        {
            _Input.close();

            _Output.write( "   ]\n");
            _Output.write("}\n");
            _Output.close();
        }
        catch( Exception pException )
        {
            System.out.println("Failed closing down I/O: " + pException.getMessage());
        }
    }

    /**** ========================================================================================================
     *
     *
     *  Read file
     *
     *
     * ==========================================================================================================
     */
    public void analyse()
    {
        int count = 0;
        String line;
        boolean doRead = true;
        String headerLine;

        //
        // ---- Get Properties
        //
        int maxRead = Integer.parseInt( _Properties.getProperty("MaxRead"));

        //
        // ---- Get column headers
        //
        try
        {
            if( _Properties.getProperty("HasHeader").equals("true") )
            {
                headerLine = _Input.readLine();
                count++;
            }
            else
            {
                headerLine = _Properties.getProperty( "Headers");
            }
            _Headers = headerLine.split(_Properties.getProperty("ColumnSplitter"));
        }
        catch( Exception pException )
        {
            System.out.println("Could not process header !");
        }


        try
        {
            while( doRead )
            {


                //
                // ---- Read next line
                //
                line = _Input.readLine();
                if( line != null )
                {
                    Item item = processLine( "Line", line );
                    item.write( _Output );
                    count++;
                }

                if( ( line == null)  || ( count > maxRead ) )
                {
                    doRead = false;
                }
                else
                {
                    _Output.write(",\n"); // next line
                }
            }
        }
        catch( Exception pException )
        {
            System.out.println("Error reading file: " + _Properties.getProperty("Source") );
            System.out.println("Error " + pException.getMessage() );
        }

        System.out.println("Lines Processed: " + count );
        return;
    }

    /****************************************************************************************************************
     *
     * Process Line
     *
     *
     * @param pLine
     *  =============================================================================================================
     */
    protected Item processLine(String pPrompt, String pLine )
    {
        Item item = new Item();

        //
        // ---- Get Properties
        //
        String columnSplitter = _Properties.getProperty( "ColumnSplitter");
        int columnToSplit = Integer.parseInt( _Properties.getProperty("ColumnToSplit") );

        //
        // ---- Retrieve all columns
        //
        String columns[] = pLine.split( columnSplitter );
        for( int index = 0; index < columns.length; index++ )
        {
            if( index == columnToSplit )
            {
                item._Attributes.putAll( getAttributes( columns[index ]));
            }
            else
            {
                //System.out.println( pPrompt + ": " + header );
                item._Attributes.put(_Headers[index], columns[index]);
            }
        }

        return( item );

    }

    // =================================================================================
    //
    //  Split attributes
    //
    //
    // =================================================================================
    protected HashMap<String, String> getAttributes( String pAttributes )
    {
        HashMap<String, String> attributes;
        String[] attributeList;

        //
        // ---- Init
        //
        attributes = new HashMap<String, String>();

        attributeList = pAttributes.split( _Properties.getProperty( "AttributeSplitter"));
        for( String attribute: attributeList )
        {
            attributes.put( "desc", attribute );
        }

        return( attributes );
    }


}
