import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class RafUtils
{
	private final static  char TERM_CHAR = '\0';
	private static final String FILE_LOCATION = "BinFiles/scores.dat";
	/*
	 * Will store the top three players from the ArrayList (if a player
	 * for that position exists) and write to scores.dat 
	 * 
	 */
	public static void generateRAF(ArrayList<Player> obList)
	{
		try
		{
			RandomAccessFile obRaf = new RandomAccessFile(FILE_LOCATION,"rw");
			if(obList.size() > 0)
			{
				obList.get(0).writePlayer(obRaf);
			}
			if(obList.size() > 1)
			{
				obList.get(1).writePlayer(obRaf);
			}
			if(obList.size() > 2)
			{
				obList.get(2).writePlayer(obRaf);
			}
		}
		catch(FileNotFoundException exp)
		{
			exp.printStackTrace();
		}
	}
	
	/**
	 * Loads the highScore arrayList from the RAF file
	 * Verifies the Raf contains players to ensure
	 * empty players aren't placed in the ArrayList
	 * 
	 * @param obRaf
	 * @return
	 */
	public static ArrayList<Player> loadHighScores(RandomAccessFile obRaf)
	{
		ArrayList<Player> obList = new ArrayList<>();
		
		try
		{
			if(obRaf.length() > 1)
			{
				obRaf.seek(0);
				Player obPlayer1 = new Player();
				obPlayer1.readPlayer(obRaf);
				obList.add(obPlayer1);
			}
			if(obRaf.length() > Player.RECORD_LENGTH)
			{
				obRaf.seek(Player.RECORD_LENGTH);
				Player obPlayer2 = new Player();
				obPlayer2.readPlayer(obRaf);
				obList.add(obPlayer2);
			}
			if(obRaf.length() > Player.RECORD_LENGTH*2)
			{
				obRaf.seek(Player.RECORD_LENGTH*2);
				Player obPlayer3 = new Player();
				obPlayer3.readPlayer(obRaf);
				obList.add(obPlayer3);
			}
		}
		catch(IOException exp)
		{
			exp.printStackTrace();
		}
		return obList;
	}

	/**
	 * Helper file for reading a String from a Random Access File.  This method should be used in tandem with writeStringToRaf
	 * to successfully write objects to files
	 * @param nSize    Size of string to read
	 * @param obRaf    Random Access File where Record is stored.
	 * @return
	 * @throws IOException
	 */
	public static String readStringFromRaf(int nSize, RandomAccessFile obRaf) throws IOException
	{
		byte[] aRead = new byte[nSize];
		
		obRaf.read(aRead);
		
		String sReturn = new String(aRead);
		
		if ( sReturn.indexOf(TERM_CHAR) != -1)
		{
			return sReturn.substring(0, sReturn.indexOf(TERM_CHAR));
		}
		else
		{
			return sReturn;
		}
	}
	
	/**
	 * Helper method for writing a string (as part of an object) to a Random Access File.  This method should be used in tandem with 
	 * readStringFromRaf to handle Random object access to and from these files.
	 * @param sVals
	 * @param nSize
	 * @param obRaf
	 * @throws IOException
	 */
	public static void writeStringToRaf(String sVals, int nSize, RandomAccessFile obRaf) throws IOException
	{
		
		byte[] aWrite = sVals.getBytes();

		int i=0;
		
		for (; i<Math.min(aWrite.length, nSize); i++)
		{
			obRaf.writeByte(aWrite[i]);
		}
		
		for (; i<nSize; i++)
		{
			obRaf.writeByte(TERM_CHAR);
		}
	}
}
