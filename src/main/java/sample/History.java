package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * ***********************************************
 * Created by Lunzi on 1/28/2018.
 * Just presonal practice.
 * Not allowed to copy without permission.
 * ***********************************************
 */
public class History implements Serializable {
	public List<HistoryStep> history;
	ObservableList<String> historyTextList = FXCollections.observableArrayList();
	
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof History)) return false;
		History that = (History) o;
		return this.history.equals(that.history);
	}
	
	public History() {
		history = new ArrayList<>();
	}
	
	boolean isPreviousStep(Coord pre) {
		for (int i = history.size() - 1; i >= 0; i--) {
			HistoryStep temp = history.get(i);
			if (temp.coord.equals(pre)) {
				return true;
			}
			if (temp.behavior.equals("legal")) {
				return false;
			}
		}
		return false;
	}
	//TODO: 增加回退功能，右侧操作区新增选项卡，显示历史操作，点击渲染该步棋盘，复盘功能
	
	public String save(String name) {
		String path = "E:/replay/" + name + new SimpleDateFormat("_yyyy_MM_dd_(HH_mm_ss)").format(new Date()) + ".rep";
		System.out.println(path);
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(path)));
			writeObject(oos);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return path;
	}
	
	public boolean load(String path) {
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(path)));
			readObject(ois);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public void addStep(Coord coord, String behavior) {
		HistoryStep temp = new HistoryStep(coord, behavior, ChessBoard.board);
		history.add(temp);
		historyTextList.add(behavior + (behavior.equals("capture") ? ": " : ":" + coord.toString()));
	}
	private void writeObject(ObjectOutputStream oos) {
		try {
			oos.writeObject(history.toArray(new HistoryStep[history.size()]));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void readObject(ObjectInputStream ois) {
		try {
			history = Arrays.asList((HistoryStep[]) ois.readObject());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
