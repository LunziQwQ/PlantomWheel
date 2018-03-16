import org.junit.Assert;
import org.junit.Test;
import sample.ChessBoard;
import sample.Coord;
import sample.History;

/**
 * ***********************************************
 * Created by Lunzi on 2/1/2018.
 * Just presonal practice.
 * Not allowed to copy without permission.
 * ***********************************************
 */
public class TestHistory {
	
	@Test
	public void testSaveAndLoad() {
		new ChessBoard();
		History history = new History();
		
		ChessBoard.getChess(new Coord(5, 5)).setChess('b');
		history.addStep(new Coord(5, 5), "legal");
		
		ChessBoard.getChess(new Coord(3, 5)).setChess('w');
		history.addStep(new Coord(5, 5), "legal");
		
		ChessBoard.getChess(new Coord(5,6)).setChess('?');
		history.addStep(new Coord(5, 5), "illegal");
		
		String path = history.save("/home/Lunzi/Downloads/replay/");
		Assert.assertNotNull(path);
		
		History forLoad = new History();
		Assert.assertTrue(forLoad.load(path));
		
		Assert.assertEquals(forLoad, history);
	}
}
