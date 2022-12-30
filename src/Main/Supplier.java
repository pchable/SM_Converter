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

    HashMap<String,String> _TagProposal;
    HashMap<String, String> _TagNormalizer;

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

            //_TagNormalizer = readTable( _Properties.getProperty( "TagNormalizer") );
            _TagProposal = readTable( _Properties.getProperty( "TagProposal"));
        }
        catch( Exception pException )
        {
            System.out.println("Failed reading supplier config file: "  + pException.getMessage() );
        }

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

    /**
     *  ====================================================================================
     *
     *  ReadTable
     *
     *
     * @param pTableName to read
     * @return HashMap with couple hint/tag
     *
     * ====================================================================================
     */
    public HashMap<String,String> readTable( String pTableName )
    {
        HashMap<String, String> table = new HashMap<String, String>();
        String line;
        String hint, tag;

        try
        {
            // ---- Open Input
            _Input = new BufferedReader(new FileReader(pTableName, Charset.forName("UTF-8")));

            do
            {
                line = _Input.readLine();

                if( line != null ) {
                    String[] infos = line.split(";");
                    hint = infos[0];
                    tag = infos[1];

                    table.put(hint, tag);
                }
            }
            while( line != null );
        }
        catch( Exception pException )
        {
            System.out.println("Could not read table: " + pTableName );
        }

        return( table );
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
        int count=0;
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

        //
        // ---- Loop over all lines in the file unless limited
        //
        try
        {
            while( doRead )
            {
                // ---- Read next line
                //
                line = _Input.readLine();
                if( line != null )
                {
                    Item item = processLine( "Line", line );
                    item.write( _Output );
                    count++;
                }

                // ---- Check if ended
                //
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
            System.out.println("*** Error reading file: " + _Properties.getProperty("Source") );
            System.out.println("*** Error " + pException.getMessage() );
        }

        System.out.println("\tLines Processed: " + count );
    }

    /** ===================================================================================
     * Get Column to split index
     *
     *
     * @return column to split index
     * ====================================================================================
     */
    public int getColumnToSplitIndex()
    {
        int index = -1;
        String columnToSplitName;

        columnToSplitName = _Properties.getProperty("ColumnToSplit");

        for( index = 0; index < _Headers.length; index++ )
        {
            if( _Headers[index].equals( columnToSplitName ) )
            {
                break;
            }
        }

        return( index );
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
        int columnToSplit = getColumnToSplitIndex();

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
        String builtDesc = "";
        int separatorIndex;
        int count =0;

        //
        // ---- Init
        //
        attributes = new HashMap<String, String>();

        attributeList = pAttributes.split( _Properties.getProperty( "AttributeSplitter"));
        for( String attribute: attributeList )
        {
            separatorIndex = attribute.indexOf(_Properties.getProperty("AttributeHeader"));

            // ---- If contains the attribute header (:)
            //
            if( separatorIndex != -1)
            {
                String attributeKey = attribute.substring(0, separatorIndex);
                String attributeValue = attribute.substring( separatorIndex+1, attribute.length());

                attributes.put( attributeKey.trim(), attributeValue.trim() );
            }
            else
            {
                String tag_value[] = findTagInAttribute( attribute );

                if( tag_value != null )
                {
                    attributes.put( tag_value[0], tag_value[1] );
                }
                else
                {
                    builtDesc += " " + attribute;
                }
            }
        }

        //
        // ---- Add desc
        //
        if( ! builtDesc.equals("") )
        {
            attributes.put("desc", builtDesc.trim() );
        }


        return( attributes );
    }

    protected String[] findTagInAttribute( String pAttribute )
    {
        String tag_value[] = null;

        for( Map.Entry<String, String> set : _TagProposal.entrySet() )
        {
            if( pAttribute.contains( set.getKey() ) )
            {
                tag_value = new String[2];
                tag_value[0]=set.getValue();
                tag_value[1]=pAttribute.trim();

                break;
            }
        }

        return( tag_value );

    }


}
