package sample;

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
	List<HistoryStep> history;
	
	History() {
		history = new ArrayList<>();
	}
	
	boolean isPreviousStep(Coord pre) {
		return true;
	}
	//TODO: 历史操作和棋盘的保存，防止重复争子，增加回退功能
	//TODO: 可能的情况下，添加保存棋谱，复盘功能
	
	//TODO: Test save and load method
	boolean save(String name) {
		String path = "replay/" + name + new SimpleDateFormat("_yyyy_MM_dd_(HH:mm:ss)").format(new Date()) + ".rep";
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(path)));
			writeObject(oos);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	boolean load(String path) {
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(path)));
			readObject(ois);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
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
