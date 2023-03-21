package tools4wd;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import javafx.beans.property.SimpleMapProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.control.ListView;

public class MapListView extends ListView<String> implements ChangeListener<ObservableMap<Integer, Integer>> {

	private final Map<Integer, Integer> backedMap = new TreeMap<Integer, Integer>(Comparator.<Integer>naturalOrder());
	private final SimpleMapProperty<Integer, Integer> map = new SimpleMapProperty<>(
			FXCollections.observableMap(backedMap));

	public SimpleMapProperty<Integer, Integer> getMap() {
		return map;
	}

	public MapListView() {
		super();
		map.addListener(this);
	}

	@Override
	public void changed(ObservableValue<? extends ObservableMap<Integer, Integer>> observable,
			ObservableMap<Integer, Integer> oldValue, ObservableMap<Integer, Integer> newValue) {
		final ObservableList<String> items = getItems();
		items.clear();
		for (int k : map.keySet()) {
			items.add("" + k + " : " + map.get(k));
		}
	}
}
