import org.junit.Assert;
import org.junit.Test;
import sample.ChessBoard;
import sample.Coord;
import sample.History;
import sample.HistoryStep;

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
		history.history.add(new HistoryStep(new Coord(5, 5), "legal", ChessBoard.board));
		
		ChessBoard.getChess(new Coord(3, 5)).setChess('w');
		history.history.add(new HistoryStep(new Coord(5, 5), "legal", ChessBoard.board));
		
		ChessBoard.getChess(new Coord(5,6)).setChess('?');
		history.history.add(new HistoryStep(new Coord(5, 5), "illegal", ChessBoard.board));
		
		String path = history.save("test");
		Assert.assertNotNull(path);
		
		History forLoad = new History();
		forLoad.load(path);
		
		Assert.assertEquals(forLoad, history);
	}
}
