import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * 
 * Stores the information for Player class
 * which acts as the user in CadCam Defense
 * 
 * @author cst143
 *
 */
public class Player {

	private String sPlayerName;
	private int nLevel;
	private int nKills;
	
	public static final int NAME_LENGTH = 20;
	public static final int RECORD_LENGTH = 28;
	
	public Player()
	{
		
	}
	
	public Player(String sPlayer)
	{
		this.sPlayerName = sPlayer;
		this.nLevel = 1;
		this.nKills = 0;
	}
	
	public int getScore()
	{
		return this.nKills;
	}
	public int getLevel()
	{
		return this.nLevel;
	}
	public String getName()
	{
		return this.sPlayerName;
	}
	public void setLevel(int nLevel)
	{
		this.nLevel = nLevel;
	}
	public void setScore(int nScore)
	{
		this.nKills = nScore;
	}
	public void writePlayer(RandomAccessFile obRaf)
	{
		try
		{
			RafUtils.writeStringToRaf(this.sPlayerName, NAME_LENGTH, obRaf);
			obRaf.writeInt(this.nLevel);
			obRaf.writeInt(this.nKills);
		}
		catch(IOException exp)
		{
			exp.printStackTrace();
		}
	}
	public void readPlayer(RandomAccessFile obRaf)
	{
		try
		{
			this.sPlayerName = RafUtils.readStringFromRaf(NAME_LENGTH, obRaf);
			this.nLevel = obRaf.readInt();
			this.nKills = obRaf.readInt();
		}
		catch(IOException exp)
		{
			exp.printStackTrace();
		}
			}
}
